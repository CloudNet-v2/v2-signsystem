package eu.cloudnetservice.sign.plugin.hook;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import eu.cloudnetservice.sign.core.manager.SignManager;
import eu.cloudnetservice.sign.core.models.SearchingAnimation;
import eu.cloudnetservice.sign.core.models.Sign;
import eu.cloudnetservice.sign.core.models.SignLayout;
import eu.cloudnetservice.sign.plugin.adapter.SignNetworkHandlerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ThreadImpl extends Thread {

	private final SignNetworkHandlerAdapter signNetworkHandlerAdapter;

	int animationTick = 1;
	private boolean valueTick = false;

	public ThreadImpl(SignNetworkHandlerAdapter signNetworkHandlerAdapter) {
		this.signNetworkHandlerAdapter = signNetworkHandlerAdapter;
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			if (SignManager.getInstance().getSignLayoutConfig() != null && SignManager.getInstance().getSignLayoutConfig().isKnockbackOnSmallDistance()) {
				try {
					for (Sign sign : SignManager.getInstance().getSigns().values()) {
						if (Bukkit.getWorld(sign.getPosition().getWorld()) != null) {
							Location location = signNetworkHandlerAdapter.toLocation(sign.getPosition());
							for (Entity entity : location.getWorld().getNearbyEntities(location,
									SignManager.getInstance().getSignLayoutConfig().getDistance(),
									SignManager.getInstance().getSignLayoutConfig().getDistance(),
									SignManager.getInstance().getSignLayoutConfig().getDistance())) {
								if (entity instanceof Player && !entity.hasPermission("cloudnet.signs.knockback.bypass")) {
									Bukkit.getScheduler().runTask(signNetworkHandlerAdapter.getPlugin(), new Runnable() {
										@Override
										public void run() {
											if (location.getBlock().getState() instanceof org.bukkit.block.Sign) {
												try {
													Location entityLocation = entity.getLocation();
													entity.setVelocity(new Vector(entityLocation.getX() - location.getX(),
															entityLocation.getY() - location.getY(),
															entityLocation.getZ() - location.getZ()).normalize()
															.multiply(
																	SignManager.getInstance().getSignLayoutConfig()
																			.getStrength())
															.setY(0.2D));
												} catch (Exception ex) {
													ex.printStackTrace();
												}
											}
										}
									});
								}
							}
						}
					}
				} catch (Exception ex) {

				}
			}

			if (SignManager.getInstance().getSignLayoutConfig() != null) {

				SearchingAnimation searchingAnimation = SignManager.getInstance().getSignLayoutConfig().getSearchingAnimation();

				SignLayout searchLayer = signNetworkHandlerAdapter.getSearchingLayout(animationTick);
				Bukkit.getScheduler().runTask(signNetworkHandlerAdapter.getPlugin(), () -> {
					for (Sign sign : SignManager.getInstance().getSigns().values()) {
						boolean exists = signNetworkHandlerAdapter.exists(sign);

						if (!exists) {
							sign.setServerInfo(null);
							continue;
						}

						if (signNetworkHandlerAdapter.isMaintenance(sign.getTargetGroup())) {
							SignLayout _signLayout = signNetworkHandlerAdapter.getLayout(sign.getTargetGroup(), "maintenance");
							String[] layout = signNetworkHandlerAdapter.updateOfflineAndMaintenance(_signLayout.getSignLayout().clone(), sign);
							sign.setServerInfo(null);
							signNetworkHandlerAdapter.sendUpdateSynchronized(signNetworkHandlerAdapter.toLocation(sign.getPosition()), layout);
							signNetworkHandlerAdapter.changeBlock(signNetworkHandlerAdapter.toLocation(sign.getPosition()),
									_signLayout.getBlockName(),
									_signLayout.getBlockId(),
									_signLayout.getSubId());
							continue;
						}

						Location location = signNetworkHandlerAdapter.toLocation(sign.getPosition());
						if (sign.getServerInfo() == null) {
							List<String> servers = new ArrayList<>(signNetworkHandlerAdapter.freeServers(sign.getTargetGroup()));
							if (servers.size() != 0) {
								String server = servers.get(NetworkUtils.RANDOM.nextInt(servers.size()));
								ServerInfo serverInfo = signNetworkHandlerAdapter.getServers().get(server);
								if (serverInfo != null && serverInfo.isOnline() && !serverInfo.isIngame()) {
									if (SignManager.getInstance().getSignLayoutConfig().isFullServerHide() && serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
										String[] layout = signNetworkHandlerAdapter.updateOfflineAndMaintenance(searchLayer.getSignLayout().clone(), sign);
										layout = signNetworkHandlerAdapter.updateOfflineAndMaintenance(layout, sign);
										signNetworkHandlerAdapter.sendUpdateSynchronized(location, layout);
										signNetworkHandlerAdapter.changeBlock(location,
												searchLayer.getBlockName(),
												searchLayer.getBlockId(),
												searchLayer.getSubId());
										continue;
									}

									sign.setServerInfo(serverInfo);
									String[] layout;
									SignLayout signLayout;
									if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
										signLayout = signNetworkHandlerAdapter.getLayout(sign.getTargetGroup(), "full");
										layout = signLayout.getSignLayout().clone();
									} else if (serverInfo.getOnlineCount() == 0) {
										signLayout = signNetworkHandlerAdapter.getLayout(sign.getTargetGroup(), "empty");
										layout = signLayout.getSignLayout().clone();
									} else {
										signLayout = signNetworkHandlerAdapter.getLayout(sign.getTargetGroup(), "online");
										layout = signLayout.getSignLayout().clone();
									}
									signNetworkHandlerAdapter.updateArray(layout, serverInfo);
									signNetworkHandlerAdapter.sendUpdateSynchronized(location, layout);
									signNetworkHandlerAdapter.changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
								} else {
									sign.setServerInfo(null);
									String[] layout = signNetworkHandlerAdapter.updateOfflineAndMaintenance(searchLayer.getSignLayout().clone(), sign);
									signNetworkHandlerAdapter.sendUpdateSynchronized(location, layout);
								}
							} else {
								sign.setServerInfo(null);
								String[] layout = signNetworkHandlerAdapter.updateOfflineAndMaintenance(searchLayer.getSignLayout().clone(), sign);
								signNetworkHandlerAdapter.sendUpdateSynchronized(location, layout);
								signNetworkHandlerAdapter.changeBlock(location, searchLayer.getBlockName(), searchLayer.getBlockId(), searchLayer.getSubId());
							}

							continue;
						}

						if (valueTick) {
							if (sign.getServerInfo() != null) {
								ServerInfo serverInfo = sign.getServerInfo();
								if (!signNetworkHandlerAdapter.isMaintenance(sign.getTargetGroup())) {
									if (serverInfo != null && serverInfo.isOnline() && !serverInfo.isIngame()) {
										if ((SignManager.getInstance().getSignLayoutConfig().isFullServerHide() && serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) || serverInfo
												.getServerConfig()
												.isHideServer()) {
											sign.setServerInfo(null);
											String[] layout = signNetworkHandlerAdapter.updateOfflineAndMaintenance(signNetworkHandlerAdapter.getSearchingLayout(((ThreadImpl) SignManager.getInstance().getWorker()).animationTick)
													.getSignLayout()
													.clone(), sign);
											layout = signNetworkHandlerAdapter.updateOfflineAndMaintenance(layout, sign);
											signNetworkHandlerAdapter.sendUpdateSynchronized(signNetworkHandlerAdapter.toLocation(sign.getPosition()), layout);
											return;
										}
										String[] layout;
										SignLayout signLayout;
										if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
											signLayout = signNetworkHandlerAdapter.getLayout(sign.getTargetGroup(), "full");
											layout = signLayout.getSignLayout().clone();
										} else if (serverInfo.getOnlineCount() == 0) {
											signLayout = signNetworkHandlerAdapter.getLayout(sign.getTargetGroup(), "empty");
											layout = signLayout.getSignLayout().clone();
										} else {
											signLayout = signNetworkHandlerAdapter.getLayout(sign.getTargetGroup(), "online");
											layout = signLayout.getSignLayout().clone();
										}
										sign.setServerInfo(serverInfo);
										signNetworkHandlerAdapter.updateArray(layout, serverInfo);
										signNetworkHandlerAdapter.sendUpdateSynchronized(location, layout);
										signNetworkHandlerAdapter.changeBlock(location,
												signLayout.getBlockName(),
												signLayout.getBlockId(),
												signLayout.getSubId());
									} else {
										sign.setServerInfo(null);
										String[] layout = signNetworkHandlerAdapter.updateOfflineAndMaintenance(signNetworkHandlerAdapter.getSearchingLayout(((ThreadImpl) SignManager.getInstance().getWorker()).animationTick)
												.getSignLayout()
												.clone(), sign);
										signNetworkHandlerAdapter.sendUpdateSynchronized(location, layout);
									}
								} else {
									sign.setServerInfo(null);
									SignLayout _signLayout = signNetworkHandlerAdapter.getLayout(sign.getTargetGroup(), "maintenance");
									String[] layout = signNetworkHandlerAdapter.updateOfflineAndMaintenance(_signLayout.getSignLayout().clone(), sign);
									signNetworkHandlerAdapter.sendUpdateSynchronized(location, layout);
									signNetworkHandlerAdapter.changeBlock(location, _signLayout.getBlockName(), _signLayout.getBlockId(), _signLayout.getSubId());
								}
							}
						}
					}
				});

				if (searchingAnimation.getAnimations() <= animationTick) {
					animationTick = 1;
				}

				animationTick++;
				valueTick = !valueTick;

				try {
					Thread.sleep(1000 / searchingAnimation.getAnimationsPerSecond());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public SignNetworkHandlerAdapter getSignNetworkHandlerAdapter() {
		return signNetworkHandlerAdapter;
	}

	public int getAnimationTick() {
		return animationTick;
	}
}
