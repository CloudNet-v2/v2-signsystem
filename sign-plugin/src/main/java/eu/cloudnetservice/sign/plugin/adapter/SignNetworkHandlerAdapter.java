package eu.cloudnetservice.sign.plugin.adapter;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.handlers.adapter.NetworkHandlerAdapter;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.ServerState;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import eu.cloudnetservice.sign.core.manager.SignManager;
import eu.cloudnetservice.sign.core.models.Position;
import eu.cloudnetservice.sign.core.models.Sign;
import eu.cloudnetservice.sign.core.models.SignGroupLayouts;
import eu.cloudnetservice.sign.core.models.SignLayout;
import eu.cloudnetservice.sign.plugin.hook.ThreadImpl;
import eu.cloudnetservice.sign.plugin.util.ItemStackBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SignNetworkHandlerAdapter extends NetworkHandlerAdapter {

    private static final Pattern groupPattern = Pattern.compile("(%group%)");
    private static final Pattern fromPattern = Pattern.compile("(%from%)");
    private static final Pattern idPattern = Pattern.compile("(%id%)");
    private static final Pattern hostPattern = Pattern.compile("(%host%)");
    private static final Pattern serverPattern = Pattern.compile("(%server%)");
    private static final Pattern portPattern = Pattern.compile("(%port%)");
    private static final Pattern memoryPattern = Pattern.compile("(%memory%)");
    private static final Pattern onlinePlayersPattern = Pattern.compile("(%online_players%)");
    private static final Pattern maxPlayersPattern = Pattern.compile("(%max_players%)");
    private static final Pattern motdPattern = Pattern.compile("(%motd%)");
    private static final Pattern statePattern = Pattern.compile("(%state%)");
    private static final Pattern wrapperPattern = Pattern.compile("(%wrapper%)");
    private static final Pattern extraPattern = Pattern.compile("(%extra%)");
    private static final Pattern templatePattern = Pattern.compile("(%template%)");
    private final JavaPlugin plugin;
    private Map<String, ServerInfo> servers = new ConcurrentHashMap<>(0);

    public SignNetworkHandlerAdapter(JavaPlugin plugin) {
        this.plugin = plugin;

        Bukkit.getScheduler().runTask(plugin,
            () -> servers.putAll(CloudAPI.getInstance().getServers().stream().collect(Collectors.toMap(serverInfo -> serverInfo.getServiceId().getServerId(),
                serverInfo -> serverInfo))));
    }

    public static boolean isMaintenance(String group) {
        if (CloudAPI.getInstance().getServerGroupMap().containsKey(group)) {
            return CloudAPI.getInstance().getServerGroupMap().get(group).isMaintenance();
        } else {
            return true;
        }
    }

    public static Sign getSignByPosition(Location location) {
        return SignManager.getInstance().getSigns().values().stream().filter(value -> value.getPosition().equals(toPosition(location))).findFirst().orElse(
            null);
    }

    public static Position toPosition(Location location) {
        return new Position(
            location.getX(),
            location.getY(),
            location.getZ(),
            location.getWorld().getName(),
            CloudAPI.getInstance().getGroup());
    }

    public static boolean containsPosition(Location location) {
        Position position = toPosition(location);
        for (Sign sign : SignManager.getInstance().getSigns().values()) {
            if (sign.getPosition().equals(position)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onServerAdd(ServerInfo serverInfo) {
        servers.put(serverInfo.getServiceId().getServerId(), serverInfo);
        Sign sign = filter(serverInfo);
        if (sign != null) {
            if (exists(sign)) {
                sign.setServerInfo(serverInfo);
                Location location = toLocation(sign.getPosition());
                if (serverInfo.isOnline() && !serverInfo.isIngame()) {
                    if ((SignManager.getInstance().getSignLayoutConfig().isFullServerHide() && serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) || serverInfo
                        .getServerConfig()
                        .isHideServer()) {
                        sign.setServerInfo(null);
                        SignLayout signLayout = getSearchingLayout(((ThreadImpl) SignManager.getInstance().getWorker()).getAnimationTick());
                        if (signLayout != null) {
                            String[] layout = updateOfflineAndMaintenance(signLayout.getSignLayout().clone(), sign);
                            updateOfflineAndMaintenance(layout, sign);
                            for (Player all : Bukkit.getOnlinePlayers()) {
                                sendUpdate(all, location, layout);
                            }
                            sendUpdateSynchronizedTask(toLocation(sign.getPosition()), layout);
                            changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                            return;
                        }
                    }
                    String[] layout;
                    SignLayout signLayout;
                    if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                        signLayout = getLayout(sign.getTargetGroup(), "full");
                        layout = signLayout.getSignLayout().clone();
                    } else if (serverInfo.getOnlineCount() == 0) {
                        signLayout = getLayout(sign.getTargetGroup(), "empty");
                        layout = signLayout.getSignLayout().clone();
                    } else {
                        signLayout = getLayout(sign.getTargetGroup(), "online");
                        layout = signLayout.getSignLayout().clone();
                    }
                    sign.setServerInfo(serverInfo);
                    updateArray(layout, serverInfo);
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        sendUpdate(all, location, layout);
                    }
                    sendUpdateSynchronizedTask(location, layout);
                    changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                } else {
                    sign.setServerInfo(null);
                    SignLayout searchingLayout = getSearchingLayout(((ThreadImpl) SignManager.getInstance().getWorker()).getAnimationTick());
                    if (searchingLayout != null) {
                        String[] layout = updateOfflineAndMaintenance(searchingLayout.getSignLayout()
                                                                                     .clone(),
                            sign);
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            sendUpdate(all, location, layout);
                        }
                        sendUpdateSynchronizedTask(location, layout);
                    }
                }

            } else {
                sign.setServerInfo(null);

                Sign next = findFreeSign(serverInfo.getServiceId().getGroup());
                Location location = toLocation(next.getPosition());
                if (serverInfo.isOnline() && !serverInfo.isIngame()) {
                    if ((SignManager.getInstance().getSignLayoutConfig().isFullServerHide() && serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) || serverInfo
                        .getServerConfig()
                        .isHideServer()) {
                        sign.setServerInfo(null);
                        SignLayout signLayout = getSearchingLayout(((ThreadImpl) SignManager.getInstance().getWorker()).getAnimationTick());
                        if (signLayout != null) {
                            String[] layout = updateOfflineAndMaintenance(signLayout.getSignLayout().clone(), sign);
                            for (Player all : Bukkit.getOnlinePlayers()) {
                                sendUpdate(all, location, layout);
                            }
                            sendUpdateSynchronizedTask(toLocation(next.getPosition()), layout);
                            changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                            return;
                        }
                    }
                    String[] layout;
                    SignLayout signLayout;
                    if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                        signLayout = getLayout(sign.getTargetGroup(), "full");
                        layout = signLayout.getSignLayout().clone();
                    } else if (serverInfo.getOnlineCount() == 0) {
                        signLayout = getLayout(sign.getTargetGroup(), "empty");
                        layout = signLayout.getSignLayout().clone();
                    } else {
                        signLayout = getLayout(sign.getTargetGroup(), "online");
                        layout = signLayout.getSignLayout().clone();
                    }
                    sign.setServerInfo(serverInfo);
                    updateArray(layout, serverInfo);
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        sendUpdate(all, location, layout);
                    }
                    sendUpdateSynchronizedTask(location, layout);
                    changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                } else {
                    sign.setServerInfo(null);
                    SignLayout signLayout = getSearchingLayout(((ThreadImpl) SignManager.getInstance().getWorker()).getAnimationTick());
                    if (signLayout != null) {
                        String[] layout = updateOfflineAndMaintenance(signLayout.getSignLayout().clone(), sign);
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            sendUpdate(all, location, layout);
                        }
                        sendUpdateSynchronizedTask(location, layout);
                        changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                    }
                }
            }
        }
    }

    private static Sign filter(ServerInfo serverInfo) {
        return SignManager.getInstance().getSigns().values().stream().filter(value -> value.getServerInfo() != null && value.getServerInfo().getServiceId().getServerId().equals(
            serverInfo.getServiceId()
                      .getServerId())).findFirst().orElse(null);
    }

    public static boolean exists(Sign sign) {
        try {
            if (Bukkit.getWorld(sign.getPosition().getWorld()) != null) {
                Location location = toLocation(sign.getPosition());
                return location.getBlock().getState() instanceof org.bukkit.block.Sign;
            } else {
                return false;
            }
        } catch (Throwable ex) {
            return false;
        }
    }

    public static Location toLocation(Position position) {
        return new Location(Bukkit.getWorld(position.getWorld()), position.getX(), position.getY(), position.getZ());
    }

    public static SignLayout getSearchingLayout(int id) {
        SignLayout layout = null;
        for (SignLayout signLayout : SignManager.getInstance().getSignLayoutConfig().getSearchingAnimation().getSearchingLayouts()) {
            if (signLayout.getName().equals(String.format("loading%d", id))) {
                layout = signLayout;
                break;
            }
        }
        return layout;
    }

    public static String[] updateOfflineAndMaintenance(String[] value, Sign sign) {
        for (short i = 0; i < value.length; i++) {
            value[i] = ChatColor.translateAlternateColorCodes('&',
                fromPattern.matcher(groupPattern.matcher(value[i]).replaceAll(sign.getTargetGroup())).replaceAll(sign.getPosition().getGroup()));
        }
        return value;
    }

    private static void sendUpdate(Player player, Location location, String[] layout) {
        if (player.getLocation().distance(location) < 32) {
            player.sendSignChange(location, layout);
        }
    }

    private void sendUpdateSynchronizedTask(Location location, String[] layout) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            org.bukkit.block.Sign sign = (org.bukkit.block.Sign) location.getBlock().getState();
            sign.setLine(0, layout[0]);
            sign.setLine(1, layout[1]);
            sign.setLine(2, layout[2]);
            sign.setLine(3, layout[3]);
            sign.update();
        });
    }

    public void changeBlock(Location location, String blockName, int blockId, int subId) {
        Bukkit.getScheduler().runTask(this.plugin, () -> {
            Material material = ItemStackBuilder.getMaterialIgnoreVersion(blockName, blockId);
            BlockState signBlockState = location.getBlock().getState();

            if (material != null && subId != -1 && signBlockState instanceof org.bukkit.block.Sign) {
                MaterialData materialData = signBlockState.getData();

                if (materialData instanceof org.bukkit.material.Sign) { // this will return false in newer 1.14 spigot versions, even if it's a sign
                    org.bukkit.material.Sign materialSign = (org.bukkit.material.Sign) materialData;
                    if (materialSign.isWallSign()) {
                        Block backBlock = location.getBlock().getRelative(materialSign.getAttachedFace());
                        BlockState blockState = backBlock.getState();
                        blockState.setType(material);
                        blockState.setData(new MaterialData(material, (byte) subId));
                        blockState.update(true);
                    }
                }
            }
        });
    }

    public static SignLayout getLayout(String group, String name) {
        SignGroupLayouts signGroupLayouts = getGroupLayout(group);
        if (signGroupLayouts == null) {
            signGroupLayouts = getGroupLayout("default");
        }
        return signGroupLayouts.getLayouts().stream().filter(value -> value.getName().equals(name)).findFirst().orElse(null);
    }

    public static void updateArray(String[] value, ServerInfo serverInfo) {
        short i = 0;
        for (String x : value) {
            value[i] = ChatColor.translateAlternateColorCodes('&',
                groupPattern.matcher(templatePattern.matcher(extraPattern.matcher(wrapperPattern.matcher(statePattern.matcher(
                    motdPattern.matcher(
                        maxPlayersPattern.matcher(
                            onlinePlayersPattern.matcher(
                                memoryPattern.matcher(
                                    portPattern.matcher(hostPattern.matcher(
                                        idPattern.matcher(
                                            serverPattern.matcher(
                                                x).replaceAll(
                                                Matcher.quoteReplacement(serverInfo.getServiceId()
                                                                                   .getServerId() + NetworkUtils.EMPTY_STRING))).replaceAll(
                                            Matcher.quoteReplacement(
                                                serverInfo.getServiceId().getId() + NetworkUtils.EMPTY_STRING))).replaceAll(
                                        Matcher.quoteReplacement(serverInfo.getHost()))).replaceAll(Matcher.quoteReplacement(serverInfo.getPort() + NetworkUtils.EMPTY_STRING))).replaceAll(
                                    Matcher.quoteReplacement(String.format("%dMB",
                                        serverInfo.getMemory())))).replaceAll(Matcher.quoteReplacement(serverInfo.getOnlineCount() + NetworkUtils.EMPTY_STRING))).replaceAll(
                            serverInfo.getMaxPlayers() + NetworkUtils.EMPTY_STRING)).replaceAll(ChatColor.translateAlternateColorCodes('&',
                        serverInfo.getMotd()))).replaceAll(serverInfo.getServerState().name() + NetworkUtils.EMPTY_STRING)).replaceAll(
                    serverInfo.getServiceId()
                              .getWrapperId() + NetworkUtils.EMPTY_STRING)).replaceAll(
                    serverInfo.getServerConfig().getExtra())).replaceAll(serverInfo.getTemplate().getName())).replaceAll(serverInfo.getServiceId().getGroup()));
            i++;
        }
    }

    private static Sign findFreeSign(String group) {
        return SignManager.getInstance().getSigns().values().stream().filter(value -> value.getTargetGroup().equals(group) && value.getServerInfo() == null).findFirst().orElse(
            null);
    }

    private static SignGroupLayouts getGroupLayout(String group) {
        return SignManager.getInstance().getSignLayoutConfig().getGroupLayouts().stream().filter(value -> value.getName().equals(group)).findFirst().orElse(
            null);
    }

    @Override
    public void onServerInfoUpdate(ServerInfo serverInfo) {
        servers.put(serverInfo.getServiceId().getServerId(), serverInfo);
        Sign sign = filter(serverInfo);

        if (sign != null) {
            if (plugin != null && plugin.isEnabled()) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (exists(sign)) {
                        sign.setServerInfo(serverInfo);
                        Location location = toLocation(sign.getPosition());
                        if (serverInfo.isOnline() && !serverInfo.isIngame()) {
                            if ((SignManager.getInstance().getSignLayoutConfig().isFullServerHide() && serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) || serverInfo
                                .getServerConfig()
                                .isHideServer()) {
                                sign.setServerInfo(null);
                                SignLayout signLayout = getSearchingLayout(((ThreadImpl) SignManager.getInstance().getWorker()).getAnimationTick());
                                String[] layout = updateOfflineAndMaintenance(signLayout.getSignLayout().clone(), sign);
                                sendUpdateSynchronized(toLocation(sign.getPosition()), layout);
                                return;
                            }
                            SignLayout signLayout;
                            String[] layout;
                            if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                                signLayout = getLayout(sign.getTargetGroup(), "full");
                                layout = signLayout.getSignLayout().clone();
                            } else if (serverInfo.getOnlineCount() == 0) {
                                signLayout = getLayout(sign.getTargetGroup(), "empty");
                                layout = signLayout.getSignLayout().clone();
                            } else {
                                signLayout = getLayout(sign.getTargetGroup(), "online");
                                layout = signLayout.getSignLayout().clone();
                            }
                            sign.setServerInfo(serverInfo);
                            updateArray(layout, serverInfo);
                            sendUpdateSynchronized(location, layout);
                            changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                        } else {
                            sign.setServerInfo(null);
                            SignLayout signLayout = getSearchingLayout(((ThreadImpl) SignManager.getInstance().getWorker()).getAnimationTick());
                            String[] layout = updateOfflineAndMaintenance(signLayout.getSignLayout().clone(), sign);
                            sendUpdateSynchronized(location, layout);
                            changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                        }

                    } else {
                        sign.setServerInfo(null);

                        Sign next = findFreeSign(serverInfo.getServiceId().getGroup());
                        Location location = toLocation(next.getPosition());
                        if (serverInfo.isOnline() && !serverInfo.isIngame()) {
                            if ((SignManager.getInstance().getSignLayoutConfig().isFullServerHide() && serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) || serverInfo
                                .getServerConfig()
                                .isHideServer()) {
                                sign.setServerInfo(null);
                                String[] layout = updateOfflineAndMaintenance(getSearchingLayout(((ThreadImpl) SignManager.getInstance().getWorker()).getAnimationTick())
                                    .getSignLayout()
                                    .clone(), sign);
                                sendUpdateSynchronized(toLocation(next.getPosition()), layout);
                                return;
                            }
                            String[] layout;
                            if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                                layout = getLayout(sign.getTargetGroup(), "full").getSignLayout().clone();
                            } else if (serverInfo.getOnlineCount() == 0) {
                                layout = getLayout(sign.getTargetGroup(), "empty").getSignLayout().clone();
                            } else {
                                layout = getLayout(sign.getTargetGroup(), "online").getSignLayout().clone();
                            }
                            sign.setServerInfo(serverInfo);
                            updateArray(layout, serverInfo);
                            sendUpdateSynchronized(location, layout);
                        } else {
                            sign.setServerInfo(null);
                            String[] layout = updateOfflineAndMaintenance(getSearchingLayout(((ThreadImpl) SignManager.getInstance().getWorker()).getAnimationTick()).getSignLayout()
                                                                                                                                                                     .clone(),
                                sign);
                            sendUpdateSynchronized(location, layout);
                        }
                    }
                });
            } else {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    Sign newSign = findFreeSign(serverInfo.getServiceId().getGroup());
                    if (newSign != null) {
                        if (exists(newSign)) {
                            Location location = toLocation(newSign.getPosition());
                            if (serverInfo.isOnline() && !serverInfo.isIngame()) {
                                if ((SignManager.getInstance().getSignLayoutConfig().isFullServerHide() && serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) || serverInfo
                                    .getServerConfig()
                                    .isHideServer()) {
                                    sign.setServerInfo(null);
                                    String[] layout = updateOfflineAndMaintenance(getSearchingLayout(((ThreadImpl) SignManager.getInstance().getWorker()).getAnimationTick())
                                        .getSignLayout()
                                        .clone(), sign);
                                    sendUpdateSynchronized(toLocation(sign.getPosition()), layout);
                                    return;
                                }
                                String[] layout;
                                if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                                    layout = getLayout(sign.getTargetGroup(), "full").getSignLayout().clone();
                                } else if (serverInfo.getOnlineCount() == 0) {
                                    layout = getLayout(sign.getTargetGroup(), "empty").getSignLayout().clone();
                                } else {
                                    layout = getLayout(sign.getTargetGroup(), "online").getSignLayout().clone();
                                }
                                sign.setServerInfo(serverInfo);
                                updateArray(layout, serverInfo);
                                sendUpdateSynchronized(location, layout);
                            } else {
                                sign.setServerInfo(null);
                                String[] layout = updateOfflineAndMaintenance(getSearchingLayout(((ThreadImpl) SignManager.getInstance().getWorker()).getAnimationTick())
                                    .getSignLayout()
                                    .clone(), sign);
                                sendUpdateSynchronized(location, layout);
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onServerRemove(ServerInfo serverInfo) {
        servers.remove(serverInfo.getServiceId().getServerId(), serverInfo);

        Sign sign = filter(serverInfo);
        if (sign != null) {
            sign.setServerInfo(null);
            if (!exists(sign)) {
                return;
            }
            String[] layout = updateOfflineAndMaintenance(getSearchingLayout(((ThreadImpl) SignManager.getInstance().getWorker()).getAnimationTick()).getSignLayout()
                                                                                                                                                     .clone(),
                sign);
            sendUpdateSynchronizedTask(toLocation(sign.getPosition()), layout);
        }
    }

    public static void sendUpdateSynchronized(Location location, String[] layout) {
        org.bukkit.block.Sign sign = (org.bukkit.block.Sign) location.getBlock().getState();
        sign.setLine(0, layout[0]);
        sign.setLine(1, layout[1]);
        sign.setLine(2, layout[2]);
        sign.setLine(3, layout[3]);
        sign.update();
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public Collection<String> freeServers(String group) {
        List<String> servers = new ArrayList<>();

        for (ServerInfo serverInfo : this.getServers(group)) {
            servers.add(serverInfo.getServiceId().getServerId());
        }

        for (Sign sign : SignManager.getInstance().getSigns().values()) {
            if (sign.getServerInfo() != null) {
                servers.remove(sign.getServerInfo().getServiceId().getServerId());
            }
        }

        List<String> x = new ArrayList<>();

        ServerInfo serverInfo;
        for (String server : servers) {
            serverInfo = this.servers.get(server);
            if (serverInfo != null) {
                if (!serverInfo.isOnline() || !serverInfo.getServerState().equals(ServerState.LOBBY) || serverInfo
                    .getMotd()
                    .contains("INGAME") || serverInfo.getMotd().contains("RUNNING") || serverInfo.getServerConfig().isHideServer()) {
                    x.add(serverInfo.getServiceId().getServerId());
                }
            } else {
                x.add(server);
            }
        }

        for (String b : x) {
            servers.remove(b);
        }

        Collections.sort(servers);
        return servers;
    }

    private Collection<ServerInfo> getServers(String group) {
        return servers.values().stream().filter(value -> value.getServiceId().getGroup().equals(group)).collect(Collectors.toList());
    }

    public Map<String, ServerInfo> getServers() {
        return servers;
    }
}
