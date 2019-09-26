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
import java.util.stream.Collectors;

public class SignNetworkHandlerAdapter extends NetworkHandlerAdapter {

	private final JavaPlugin plugin;
	private Map<String, ServerInfo> servers = new ConcurrentHashMap<>(0);

	public SignNetworkHandlerAdapter(JavaPlugin plugin) {
		this.plugin = plugin;

		Bukkit.getScheduler().runTask(plugin, () -> servers.putAll(CloudAPI.getInstance().getServers().stream().collect(Collectors.toMap(serverInfo -> serverInfo.getServiceId().getServerId(), serverInfo -> serverInfo))));
	}

	private Sign filter(ServerInfo serverInfo) {
		return SignManager.getInstance().getSigns().values().stream().filter(value -> value.getServerInfo() != null && value.getServerInfo().getServiceId().getServerId().equals(serverInfo.getServiceId()
				.getServerId())).findFirst().orElse(null);
	}

	public boolean exists(Sign sign) {
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

	public Location toLocation(Position position) {
		return new Location(Bukkit.getWorld(position.getWorld()), position.getX(), position.getY(), position.getZ());
	}

	public SignLayout getLayout(String group, String name) {
		SignGroupLayouts signGroupLayouts = getGroupLayout(group);
		if (signGroupLayouts == null) {
			signGroupLayouts = getGroupLayout("default");
		}
		return signGroupLayouts.getLayouts().stream().filter(value -> value.getName().equals(name)).findFirst().orElse(null);
	}

	private SignGroupLayouts getGroupLayout(String group) {
		return SignManager.getInstance().getSignLayoutConfig().getGroupLayouts().stream().filter(value -> value.getName().equals(group)).findFirst().orElse(null);
	}

	public SignLayout getSearchingLayout(int id) {
		for (SignLayout signLayout : SignManager.getInstance().getSignLayoutConfig().getSearchingAnimation().getSearchingLayouts()) {
			if (signLayout.getName().equals("loading" + id)) {
				return signLayout;
			}
		}
		return null;
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
						String[] layout = updateOfflineAndMaintenance(signLayout.getSignLayout().clone(), sign);
						layout = updateOfflineAndMaintenance(layout, sign);
						for (Player all : Bukkit.getOnlinePlayers()) {
							sendUpdate(all, location, layout);
						}
						sendUpdateSynchronizedTask(toLocation(sign.getPosition()), layout);
						changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
						return;
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
					String[] layout = updateOfflineAndMaintenance(getSearchingLayout(((ThreadImpl) SignManager.getInstance().getWorker()).getAnimationTick()).getSignLayout()
									.clone(),
							sign);
					for (Player all : Bukkit.getOnlinePlayers()) {
						sendUpdate(all, location, layout);
					}
					sendUpdateSynchronizedTask(location, layout);
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
						String[] layout = updateOfflineAndMaintenance(signLayout.getSignLayout().clone(), sign);
						for (Player all : Bukkit.getOnlinePlayers()) {
							sendUpdate(all, location, layout);
						}
						sendUpdateSynchronizedTask(toLocation(next.getPosition()), layout);
						changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
						return;
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
					String[] layout = updateOfflineAndMaintenance(signLayout.getSignLayout().clone(), sign);
					for (Player all : Bukkit.getOnlinePlayers()) {
						sendUpdate(all, location, layout);
					}
					sendUpdateSynchronizedTask(location, layout);
					changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
				}
			}
		} else {
			Sign newSign = findFreeSign(serverInfo.getServiceId().getGroup());
			if (newSign != null) {
				if (exists(newSign)) {
					Location location = toLocation(newSign.getPosition());
					if (serverInfo.isOnline() && !serverInfo.isIngame()) {
						if ((SignManager.getInstance().getSignLayoutConfig().isFullServerHide() && serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) || serverInfo
								.getServerConfig()
								.isHideServer()) {
							sign.setServerInfo(null);
							SignLayout signLayout = getSearchingLayout(((ThreadImpl) SignManager.getInstance().getWorker()).getAnimationTick());
							String[] layout = updateOfflineAndMaintenance(signLayout.getSignLayout().clone(), sign);
							for (Player all : Bukkit.getOnlinePlayers()) {
								sendUpdate(all, location, layout);
							}
							sendUpdateSynchronizedTask(toLocation(sign.getPosition()), layout);
							changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
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
						for (Player all : Bukkit.getOnlinePlayers()) {
							sendUpdate(all, location, layout);
						}
						sendUpdateSynchronizedTask(location, layout);
						changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
					} else {
						newSign.setServerInfo(null);
						SignLayout signLayout = getSearchingLayout(((ThreadImpl) SignManager.getInstance().getWorker()).getAnimationTick());
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

	@Override
	public void onServerInfoUpdate(ServerInfo serverInfo) {
		servers.put(serverInfo.getServiceId().getServerId(), serverInfo);
		Sign sign = filter(serverInfo);

		if (sign != null) {
			if (getPlugin() != null && getPlugin().isEnabled()) {
				Bukkit.getScheduler().runTask(getPlugin(), new Runnable() {

					@Override
					public void run() {
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
					}
				});
			} else {
				Bukkit.getScheduler().runTask(getPlugin(), new Runnable() {
					@Override
					public void run() {
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
					.clone(), sign);
			sendUpdateSynchronizedTask(toLocation(sign.getPosition()), layout);
		}
	}

	private Sign findFreeSign(String group) {
		return SignManager.getInstance().getSigns().values().stream().filter(value -> value.getTargetGroup().equals(group) && value.getServerInfo() == null).findFirst().orElse(null);
	}

	public void sendUpdateSynchronizedTask(Location location, String[] layout) {
		Bukkit.getScheduler().runTask(getPlugin(), () -> {
			org.bukkit.block.Sign sign = (org.bukkit.block.Sign) location.getBlock().getState();
			sign.setLine(0, layout[0]);
			sign.setLine(1, layout[1]);
			sign.setLine(2, layout[2]);
			sign.setLine(3, layout[3]);
			sign.update();
		});
	}


	public Collection<String> freeServers(String group) {
		List<String> servers = new ArrayList<>();

		for (ServerInfo serverInfo : this.getServers(group)) {
			servers.add(serverInfo.getServiceId().getServerId());
		}

		for (Sign sign : SignManager.getInstance().getSigns().values()) {
			if (sign.getServerInfo() != null && servers.contains(sign.getServerInfo().getServiceId().getServerId())) {
				servers.remove(sign.getServerInfo().getServiceId().getServerId());
			}
		}

		List<String> x = new ArrayList<>();

		ServerInfo serverInfo;
		for (short i = 0; i < servers.size(); i++) {
			serverInfo = getServers().get(servers.get(i));
			if (serverInfo != null) {
				if (!serverInfo.isOnline() || !serverInfo.getServerState().equals(ServerState.LOBBY) || serverInfo.getServerConfig()
						.isHideServer() || serverInfo
						.getMotd()
						.contains("INGAME") || serverInfo.getMotd().contains("RUNNING") || serverInfo.getServerConfig().isHideServer()) {
					x.add(serverInfo.getServiceId().getServerId());
				}
			} else {
				x.add(servers.get(i));
			}
		}

		for (String b : x) {
			servers.remove(b);
		}

		Collections.sort(servers);
		return servers;
	}

	private Collection<ServerInfo> getServers(String group) {
		return getServers().values().stream().filter(value -> value.getServiceId().getGroup().equals(group)).collect(Collectors.toList());
	}

	public boolean isMaintenance(String group) {
		if (CloudAPI.getInstance().getServerGroupMap().containsKey(group)) {
			return CloudAPI.getInstance().getServerGroupMap().get(group).isMaintenance();
		} else {
			return true;
		}
	}

	public Map<String, ServerInfo> getServers() {
		return servers;
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

	public Sign getSignByPosition(Location location) {
		return SignManager.getInstance().getSigns().values().stream().filter(value -> value.getPosition().equals(toPosition(location))).findFirst().orElse(null);
	}

	public boolean containsPosition(Location location) {
		Position position = toPosition(location);
		for (Sign sign : SignManager.getInstance().getSigns().values()) {
			if (sign.getPosition().equals(position)) {
				return true;
			}
		}
		return false;
	}

	public Position toPosition(Location location) {
		return new Position(
				location.getX(),
				location.getY(),
				location.getZ(),
				location.getWorld().getName(),
				CloudAPI.getInstance().getGroup());
	}

	public void sendUpdateSynchronized(Location location, String[] layout) {
		org.bukkit.block.Sign sign = (org.bukkit.block.Sign) location.getBlock().getState();
		sign.setLine(0, layout[0]);
		sign.setLine(1, layout[1]);
		sign.setLine(2, layout[2]);
		sign.setLine(3, layout[3]);
		sign.update();
	}

	public String[] updateOfflineAndMaintenance(String[] value, Sign sign) {
		for (short i = 0; i < value.length; i++) {
			value[i] = ChatColor.translateAlternateColorCodes('&',
					value[i].replace("%group%", sign.getTargetGroup())
							.replace("%from%", sign.getPosition().getGroup()));
		}
		return value;
	}

	public void updateArray(String[] value, ServerInfo serverInfo) {
		short i = 0;
		for (String x : value) {
			value[i] = ChatColor.translateAlternateColorCodes('&', x.replace("%server%",
					serverInfo.getServiceId()
							.getServerId() + NetworkUtils.EMPTY_STRING)
					.replace("%id%",
							serverInfo.getServiceId().getId() + NetworkUtils.EMPTY_STRING)
					.replace("%host%", serverInfo.getHost())
					.replace("%port%",
							serverInfo.getPort() + NetworkUtils.EMPTY_STRING)
					.replace("%memory%", serverInfo.getMemory() + "MB")
					.replace("%online_players%",
							serverInfo.getOnlineCount() + NetworkUtils.EMPTY_STRING)
					.replace("%max_players%",
							serverInfo.getMaxPlayers() + NetworkUtils.EMPTY_STRING)
					.replace("%motd%",
							ChatColor.translateAlternateColorCodes('&',
									serverInfo.getMotd()))
					.replace("%state%",
							serverInfo.getServerState().name() + NetworkUtils.EMPTY_STRING)
					.replace("%wrapper%",
							serverInfo.getServiceId()
									.getWrapperId() + NetworkUtils.EMPTY_STRING)
					.replace("%extra%", serverInfo.getServerConfig().getExtra())
					.replace("%template%", serverInfo.getTemplate().getName())
					.replace("%group%", serverInfo.getServiceId().getGroup()));
			i++;
		}
	}

	public void sendUpdate(Player player, Location location, String[] layout) {
		if (player.getLocation().distance(location) < 32) {
			player.sendSignChange(location, layout);
		}
	}

	public JavaPlugin getPlugin() {
		return plugin;
	}
}
