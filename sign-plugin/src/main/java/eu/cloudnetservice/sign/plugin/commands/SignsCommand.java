package eu.cloudnetservice.sign.plugin.commands;

import com.google.common.collect.ImmutableList;
import de.dytanic.cloudnet.api.CloudAPI;
import eu.cloudnetservice.sign.core.manager.SignManager;
import eu.cloudnetservice.sign.core.models.Position;
import eu.cloudnetservice.sign.core.models.Sign;
import eu.cloudnetservice.sign.core.packets.PacketOutAddSign;
import eu.cloudnetservice.sign.core.packets.PacketOutRemoveSign;
import eu.cloudnetservice.sign.plugin.hook.ThreadImpl;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public final class SignsCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        CloudAPI.getInstance().getLogger().finest(String.format("%s executed %s (label = %s) with arguments %s",
            commandSender,
            command,
            label,
            Arrays.toString(args)));
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("removeSign")) {
                return playerGuard(commandSender, player -> removeSign(commandSender, player));

            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("createSign")) {
                return playerGuard(commandSender, player -> createSign(commandSender, args, player));
            } else if (args[0].equalsIgnoreCase("removeSigns")) {
                return removeSigns(commandSender, args[1]);
            }
        }
        help(commandSender);
        return false;
    }

    private static boolean playerGuard(CommandSender sender, Function<Player, Boolean> method) {
        if (sender instanceof Player) {
            return method.apply((Player) sender);
        } else {
            sender.sendMessage(CloudAPI.getInstance().getPrefix() + "This command can only be called by a player!");
            return false;
        }
    }

    private boolean removeSign(CommandSender commandSender, Player player) {
        if (checkSignSelectorActive(commandSender)) {
            return true;
        }

        Block block = player.getTargetBlock((Set<Material>) null, 15);
        if (block.getState() instanceof org.bukkit.block.Sign) {
            if (((ThreadImpl) SignManager.getInstance().getWorker()).getSignNetworkHandlerAdapter().containsPosition(block.getLocation())) {
                Sign sign = ((ThreadImpl) SignManager.getInstance().getWorker()).getSignNetworkHandlerAdapter().getSignByPosition(block.getLocation());

                if (sign != null) {
                    CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutRemoveSign(sign));
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The sign has been removed");
                }
            }
        }
        return false;
    }

    private boolean createSign(CommandSender commandSender, String[] args, Player player) {
        if (checkSignSelectorActive(commandSender)) {
            return false;
        }

        Block block = player.getTargetBlock((Set<Material>) null, 15);
        if (block.getState() instanceof org.bukkit.block.Sign) {
            if (!((ThreadImpl) SignManager.getInstance().getWorker()).getSignNetworkHandlerAdapter().containsPosition(block.getLocation())) {
                if (CloudAPI.getInstance().getServerGroupMap().containsKey(args[1])) {
                    Sign sign = new Sign(args[1],
                        ((ThreadImpl) SignManager.getInstance().getWorker()).getSignNetworkHandlerAdapter().toPosition(block.getLocation()));
                    CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutAddSign(sign));
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The sign was successfully created!");
                } else {
                    commandSender.sendMessage("The group doesn't exist");
                }
            } else {
                commandSender.sendMessage("The sign already exists!");
            }
        }
        return false;
    }

    private boolean removeSigns(CommandSender commandSender, String arg) {
        if (checkSignSelectorActive(commandSender)) {
            return true;
        }

        for (Sign sign : SignManager.getInstance().getSigns().values()) {
            if (sign.getTargetGroup() != null && sign.getTargetGroup().equalsIgnoreCase(arg)) {
                CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutRemoveSign(sign));
            }
        }

        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "§7You deleted all signs from the group \"§6" + arg + "§7\"");
        return false;
    }

    private void help(CommandSender commandSender) {
        if (SignManager.getInstance() != null) {
            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/signs createSign <targetGroup>");
            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/signs removeSign");
            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/signs removeSigns <targetGroup>");
            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cs copyTo <targetGroup>");
        }

    }

    private boolean checkSignSelectorActive(CommandSender commandSender) {
        if (SignManager.getInstance() == null) {
            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The Module \"SignSelector\" isn't enabled!");
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        switch (args.length) {
            case 1: {
                return ImmutableList.of("createSign",
                    "removeSign",
                    "removeSigns");
            }
            case 2: {
                if (args[0].equalsIgnoreCase("createsign") || args[0].equalsIgnoreCase("removesigns") || args[0].equalsIgnoreCase("copyto")) {
                    return ImmutableList.copyOf(CloudAPI.getInstance().getServerGroupMap().keySet());
                }
            }
        }
        return ImmutableList.of();
    }

    private boolean copyTo(CommandSender commandSender, String arg) {
        if (checkSignSelectorActive(commandSender)) {
            return false;
        }

        if (CloudAPI.getInstance().getServerGroupMap().containsKey(arg)) {
            for (Sign sign : SignManager.getInstance().getSigns().values()) {
                CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutAddSign(new Sign(sign.getTargetGroup(),
                    new Position(
                        sign.getPosition().getX(),
                        sign.getPosition().getY(),
                        sign.getPosition().getZ(),
                        sign.getPosition().getWorld(),
                        arg))));
            }

            commandSender.sendMessage(CloudAPI.getInstance()
                                              .getPrefix() + "The signs by this group was successfully copied to the target group.");
        }
        return true;
    }
}
