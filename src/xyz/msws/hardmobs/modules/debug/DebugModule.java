package xyz.msws.hardmobs.modules.debug;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.msws.hardmobs.HardMobs;
import xyz.msws.hardmobs.modules.AbstractModule;
import xyz.msws.hardmobs.modules.ModulePriority;
import xyz.msws.hardmobs.utils.MSG;
import xyz.msws.hardmobs.utils.Utils;

public class DebugModule extends AbstractModule implements Listener {

	public DebugModule(String id, HardMobs plugin) {
		super(id, plugin);
	}

	@Override
	public void initialize() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public void disable() {
		AsyncPlayerChatEvent.getHandlerList().unregister(this);
	}

	@Override
	public ModulePriority getPriority() {
		return ModulePriority.LOWEST;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		if (!plugin.getConfig().getBoolean("DebugMode.Enabled"))
			return;
		if (!plugin.getConfig().getStringList("DebugMode.Allow").contains(player.getName()))
			return;
		if (!event.getMessage().startsWith("."))
			return;

		event.setMessage(event.getMessage().substring(1));

		event.setCancelled(true);

		String[] split = event.getMessage().split(" ");
		String[] args = new String[Math.max(0, split.length - 1)];
		final String msg;

		for (int i = 1; i < split.length; i++) {
			args[i - 1] = split[i];
		}

		OfflinePlayer target = args.length == 1 ? Bukkit.getOfflinePlayer(args[0]) : player, ft;

		switch (event.getMessage().split(" ")[0].toLowerCase()) {
		case "say":
			event.setCancelled(false);
			event.setMessage(String.join(" ", args));
			break;
		case "ver":
		case "version":
			MSG.tell(player, "Debug", "Versions:");
			MSG.tell(player,
					MSG.FORMATTER + plugin.getName() + " " + MSG.FORMAT_INFO + plugin.getDescription().getVersion());
			MSG.tell(player, MSG.FORMATTER + "Server Version " + MSG.FORMAT_INFO + Bukkit.getVersion());
			MSG.tell(player, MSG.FORMATTER + "Bukkit Version " + MSG.FORMAT_INFO + Bukkit.getBukkitVersion());
			break;
		case "modules":
		case "listmodules":
			String mods = String.join(MSG.FORMAT_SEPARATOR + ", " + MSG.FORMAT_INFO,
					plugin.getModules().stream().map(m -> m.getId()).collect(Collectors.toList()));
			MSG.tell(player, "Debug", "Active Modules (" + MSG.NUMBER + plugin.getModules().size() + MSG.DEFAULT + ") "
					+ MSG.FORMAT_INFO + mods);
			break;
		case "t":
		case "toggle":
		case "togglemodule":
			if (args.length != 1) {
				MSG.tell(player, "Debug", "You must specify a module.");
				break;
			}
			AbstractModule mod = plugin.getModule(args[0]);
			if (mod == null) {
				MSG.tell(player, "Debug", "Invalid Module.");
				break;
			}

			if (mod.isEnabled()) {
				mod.disable();
			} else {
				mod.initialize();
			}
			mod.setEnabled(!mod.isEnabled());
			MSG.tell(player, "Debug", MSG.ed(mod.isEnabled()) + " " + MSG.FORMAT_INFO + mod.getId());
			break;
		case "isop":
			MSG.tell(player, "Debug", target.getName() + " has op: " + MSG.tf(target.isOp()));
			break;
		case "op":
			ft = target;
			new BukkitRunnable() {
				@Override
				public void run() {
					ft.setOp(true);
					MSG.tell(player, "Debug", "You opped " + MSG.PLAYER + ft.getName());
					if (ft.isOnline())
						MSG.tell(ft.getPlayer(), "Debug", MSG.STAFF + player.getName() + MSG.DEFAULT + " opped you.");
				}
			}.runTask(plugin);
			break;
		case "deop":
			ft = target;
			new BukkitRunnable() {
				@Override
				public void run() {
					ft.setOp(true);
					MSG.tell(player, "Debug", "You deopped " + MSG.PLAYER + ft.getName());
					if (ft.isOnline())
						MSG.tell(ft.getPlayer(), "Debug", MSG.STAFF + player.getName() + MSG.DEFAULT + " deopped you.");
				}
			}.runTask(plugin);
			break;
		case "listops":
		case "listop":
			MSG.tell(player, "Debug", "There are " + MSG.NUMBER + Bukkit.getOperators().size() + MSG.DEFAULT + " "
					+ MSG.plural("operator", Bukkit.getOperators().size()));
			for (OfflinePlayer t : Bukkit.getOperators()) {
				MSG.tell(player, MSG.STAFF + t.getName() + " " + MSG.TIME
						+ MSG.getTime(System.currentTimeMillis() - t.getLastSeen()));
			}
			break;
		case "gm":
			if (args.length < 1) {
				MSG.tell(player, "Debug", ".gm <Player> [Gamemode]");
				break;
			}

			GameMode gm = null;
			String name = "";

			gm = Utils.getMode(args[0]);
			if (gm != null) {
				final GameMode fGm = gm;
				new BukkitRunnable() {
					@Override
					public void run() {
						player.setGameMode(fGm);
					}
				}.runTask(plugin);
				name = player.getName();
			} else {
				if (args.length != 2) {
					MSG.tell(player, "Debug", "Unknown Gamemode.");
					break;
				}
				gm = Utils.getMode(args[1]);
				target = Utils.matchPlayer(player, args[0], true);
				if (target == null) {
					MSG.tell(player, "Debug", "Unknown Player.");
					break;
				}
				name = target.getName();
				final Player oT = target.getPlayer();
				final GameMode fGm = gm;
				new BukkitRunnable() {
					@Override
					public void run() {
						oT.getPlayer().setGameMode(fGm);
					}
				}.runTask(plugin);
				MSG.tell(target.getPlayer(), "Debug", MSG.PLAYER + player.getName() + " " + MSG.DEFAULT
						+ "set your gamemode to " + MSG.FORMAT_INFO + MSG.camelCase(gm.toString()));
			}

			MSG.tell(player, "Debug", "Set " + MSG.PLAYER + MSG.plural(name) + MSG.DEFAULT + " gamemode to "
					+ MSG.FORMAT_INFO + MSG.camelCase(gm.toString()));
			break;
		case "gmc":
		case "gms":
		case "gma":
		case "gmsp":
			new BukkitRunnable() {
				@Override
				public void run() {
					setMode(player, args, Utils.getMode(event.getMessage().split(" ")[0].substring(2)));
				}
			}.runTask(plugin);
			break;
		case "rl":
		case "reload":
			new BukkitRunnable() {
				@Override
				public void run() {
					MSG.tell(player, "Debug", "Reloading...");
					Bukkit.reload();
					MSG.tell(player, "Debug", "Reloaded.");
				}
			}.runTask(plugin);
			break;
		case "tp":
			new BukkitRunnable() {
				@Override
				public void run() {
					List<Player> players = Arrays.asList(Bukkit.getPlayer(args[0]));
					Player tmp = null;
					Location targ = null;
					String playerName = args[0], targetName = "Unknown";

					if (args[0].equalsIgnoreCase("all") && Utils.has(player, "scorch.command.teleport.all", true)) {
						players = Bukkit.getOnlinePlayers().stream().collect(Collectors.toList());
						if (args.length == 1 && player instanceof Player) {
							targ = player.getLocation();
							targetName = player.getName();
						} else if (args.length == 2) {
							targ = Bukkit.getPlayer(args[1]).getLocation();
							targetName = Bukkit.getPlayer(args[1]).getName();
						} else if (args.length >= 4) {
							targ = new Location(((Player) player).getWorld(), Double.parseDouble(args[1]) + .5,
									Double.parseDouble(args[2]) + .5, Double.parseDouble(args[3]) + .5);
							targetName = String.format("%s %s %s", args[1], args[2], args[3]);
							if (args.length == 6) {
								targ.setYaw(Float.parseFloat(args[4]));
								targ.setPitch(Float.parseFloat(args[5]));
								targetName = String.format("%s %s %s %s %s", args[1], args[2], args[3], args[4],
										args[5]);
							}

						} else {
							MSG.tell(player, "Teleport", MSG.ERROR + "Unknown Player");
							return;
						}

						playerName = "everyone";
					} else {
						if (players.get(0) != null) {
							playerName = players.get(0).getName();
							if (args.length == 1) {
								if (player instanceof Player) {
									targ = players.get(0).getLocation();
									targetName = players.get(0).getName();
									players.set(0, (Player) player);
									playerName = player.getName();
								} else {
									MSG.tell(player, "Teleport", MSG.ERROR + "Define target");
									return;
								}
							} else {
								tmp = Bukkit.getPlayer(args[1]);
								if (tmp == null) {
									if (NumberUtils.isNumber(args[1]) && args.length >= 4) {
										targ = new Location(players.get(0).getWorld(), Double.parseDouble(args[1]),
												Double.parseDouble(args[2]), Double.parseDouble(args[3]));

										targetName = String.format("%s %s %s", args[1], args[2], args[3]);
										if (args.length == 6) {
											targ.setYaw(Float.parseFloat(args[4]));
											targ.setPitch(Float.parseFloat(args[5]));
											targetName = String.format("%s %s %s %s %s", args[1], args[2], args[3],
													args[4], args[5]);
										}
									} else {
										MSG.tell(player, "Teleport", "Unknown Player");
										return;
									}
								} else {
									targ = tmp.getLocation();
									targetName = tmp.getName();
								}
							}
						} else {
							if (args.length >= 3 && NumberUtils.isNumber(args[0])) {
								if (!(player instanceof Player)) {
									MSG.tell(player, "Teleport", "Unknown Player");
									return;
								}

								players.set(0, (Player) player);
								playerName = player.getName();

								targ = new Location(((Player) player).getWorld(), Double.parseDouble(args[0]) + .5,
										Double.parseDouble(args[1]) + .5, Double.parseDouble(args[2]) + .5);
								targetName = String.format("%s %s %s", args[0], args[1], args[2]);
								if (args.length == 5) {
									targ.setYaw(Float.parseFloat(args[3]));
									targ.setPitch(Float.parseFloat(args[4]));
									targetName = String.format("%s %s %s %s %s", args[0], args[1], args[2], args[3],
											args[4]);
								}
							}
						}
					}

					if (targ == null)
						return;

					MSG.tell(player, "Teleport", "Teleported " + MSG.PLAYER + playerName + MSG.DEFAULT + " to "
							+ MSG.PLAYER + targetName + MSG.DEFAULT + ".");
					for (Player p : players) {
						p.teleport(targ);
						if (p.equals(player))
							continue;
						MSG.tell(p, "Debug", MSG.STAFF + player.getName() + MSG.DEFAULT + " teleported you to "
								+ MSG.PLAYER + targetName + MSG.DEFAULT + ".");
					}
				}
			}.runTask(plugin);

			break;
		case "god":
			if (args.length == 1)
				target = Utils.matchPlayer(player, args[0], true);
			if (target == null)
				break;
			target.getPlayer().setInvulnerable(!target.getPlayer().isInvulnerable());
			MSG.tell(player, "Debug", MSG.ed(target.getPlayer().isInvulnerable()) + " " + MSG.PLAYER
					+ MSG.plural(target.getName()) + MSG.DEFAULT + " god mode.");
			MSG.tell(target.getPlayer(), "Debug", MSG.STAFF + player.getName() + " "
					+ MSG.ed(target.getPlayer().isInvulnerable()) + " your god mode.");
			break;
		case "cmd":
		case "command":
		case "console":
			if (args.length == 0) {
				MSG.tell(player, "Debug", "Specify command.");
				break;
			}
			msg = String.join(" ", args);
			new BukkitRunnable() {
				@Override
				public void run() {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), msg);
				}
			}.runTask(plugin);
			MSG.tell(player, "Debug", "Executed " + MSG.FORMAT_INFO + "/" + msg);
			break;
		case "asop":
			if (args.length == 0) {
				MSG.tell(player, "Debug", "Specify command.");
				break;
			}
			msg = String.join(" ", args);
			new BukkitRunnable() {
				@Override
				public void run() {
					boolean old = player.isOp();
					player.setOp(true);
					Bukkit.dispatchCommand(player, msg);
					player.setOp(old);
				}
			}.runTask(plugin);
			MSG.tell(player, "Debug", "Ran (as OP) " + MSG.FORMAT_INFO + "/" + msg);
			break;
		case "sudo":
			if (args.length == 0) {
				MSG.tell(player, "Debug", "Specify player.");
				break;
			} else if (args.length == 1) {
				MSG.tell(player, "Debug", "Specify command.");
				break;
			}

			Player toRun = Utils.matchPlayer(player, args[0], true);
			if (toRun == null)
				return;

			String cmd = "";
			for (int i = 1; i < args.length; i++)
				cmd += args[i] + " ";
			final String fCmd = cmd.trim();
			new BukkitRunnable() {
				@Override
				public void run() {
					Bukkit.dispatchCommand(toRun, fCmd);
				}
			}.runTask(plugin);
			MSG.tell(player, "Debug",
					"Made " + MSG.PLAYER + toRun.getName() + MSG.DEFAULT + " run " + MSG.FORMAT_INFO + "/" + fCmd);
			break;
		case "pl":
		case "plugins":
			MSG.tell(player, "Debug", "Listing All Plugins");
			for (Plugin p : Bukkit.getPluginManager().getPlugins())
				MSG.tell(player, MSG.SUBJECT + p.getName() + " " + MSG.NUMBER + p.getDescription().getVersion());
			break;
		case "help":
			MSG.tell(player, "Debug", "Commands");
			MSG.tell(player, MSG.PREFIX + ".ver " + MSG.DEFAULT + "- List Server/Plugin Version Information");
			MSG.tell(player, MSG.PREFIX + ".pl " + MSG.DEFAULT + "- List all plugins");
			MSG.tell(player, MSG.PREFIX + ".modules " + MSG.DEFAULT + "- List Modules");
			MSG.tell(player, MSG.PREFIX + ".toggle [Module] " + MSG.DEFAULT + "- Toggle Module");
			MSG.tell(player, MSG.PREFIX + ".isop <Player> " + MSG.DEFAULT + "- Check if player/self is OP");
			MSG.tell(player, MSG.PREFIX + ".op <Player> " + MSG.DEFAULT + "- OP specified player");
			MSG.tell(player, MSG.PREFIX + ".listops " + MSG.DEFAULT + "- Lists OPs");
			MSG.tell(player, MSG.PREFIX + ".deop <Player> " + MSG.DEFAULT + "- Deop specified player");
			MSG.tell(player, MSG.PREFIX + ".gm <Player> [Gamemode] " + MSG.DEFAULT + "- Set gamemode");
			MSG.tell(player, MSG.PREFIX + ".gm[a/c/s/sp]" + MSG.DEFAULT + "- Set gamemode");
			MSG.tell(player, MSG.PREFIX + ".rl " + MSG.DEFAULT + "- Reload Server");
			MSG.tell(player,
					MSG.PREFIX + ".tp <Player/All> [Player/XYZ] " + MSG.DEFAULT + "- Teleport to specified location");
			MSG.tell(player, MSG.PREFIX + ".god <Player> " + MSG.DEFAULT + "- Toggle God Status");
			MSG.tell(player, MSG.PREFIX + ".console [Command] " + MSG.DEFAULT + "- Run command as console");
			MSG.tell(player, MSG.PREFIX + ".asop [Command] " + MSG.DEFAULT + "- Run command as OP");
			MSG.tell(player, MSG.PREFIX + ".sudo [Player] [Command] " + MSG.DEFAULT + "- Force player to run command");
			MSG.tell(player, MSG.PREFIX + ".say [Message] " + MSG.DEFAULT + "- Send message as chat");
			break;
		default:
			MSG.tell(player, "Debug", "Unknown command. Type \".help\" for a list of commands.");
			break;
		}
	}

	private void setMode(Player player, String[] args, GameMode gm) {
		OfflinePlayer target = player;
		if (args.length == 1)
			target = Utils.matchPlayer(player, args[0], true);
		if (target == null)
			return;
		MSG.tell(player, "Debug", "Set " + MSG.PLAYER + MSG.plural(target.getName()) + " " + MSG.DEFAULT
				+ "gamemode to " + MSG.FORMAT_INFO + MSG.camelCase(gm.toString()));
		MSG.tell(target.getPlayer(), "Debug", MSG.STAFF + player.getName() + " " + MSG.DEFAULT + "set your gamemode to "
				+ MSG.FORMAT_INFO + MSG.camelCase(gm.toString()));
		target.getPlayer().setGameMode(gm);
	}
}
