package xyz.msws.hardmobs.scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import xyz.msws.hardmobs.HardMobs;
import xyz.msws.hardmobs.modules.AbstractModule;
import xyz.msws.hardmobs.modules.ModulePriority;
import xyz.msws.hardmobs.utils.MSG;

/**
 * This is the main scoreboard utility that handles scoreboards across the
 * servers
 */
public class ScoreboardModule extends AbstractModule {

	private final String scoreboardName = "";

	private HardMobs plugin;

	public ScoreboardModule(String id, HardMobs plugin) {
		super(id, plugin);
		this.plugin = plugin;
	}

	private ScoreboardManager scoreboardManager;

	private Map<UUID, CScoreboard> assigned = new HashMap<>();

	private BukkitRunnable runner;

	@Override
	public void initialize() {
		this.scoreboardManager = Bukkit.getScoreboardManager();

		assigned = new HashMap<UUID, CScoreboard>();

		runner = new BukkitRunnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (!assigned.containsKey(p.getUniqueId()))
						continue;

					assigned.get(p.getUniqueId()).onTick();
					for (int i = 1; i <= 15; i++)
						setLine(p, i, assigned.get(p.getUniqueId()).getLine(i));
					setTitle(p, assigned.get(p.getUniqueId()).getTitle());
				}
			}
		};
		runner.runTaskTimer(plugin, 0, 1);
	}

	@Override
	public void disable() {
		assigned = new HashMap<>();

		runner.cancel();

		for (Player p : Bukkit.getOnlinePlayers())
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
	}

	private void setTitle(Player player, String value) {
		Scoreboard board = player.getScoreboard();
		Objective obj;
		if (board == null || board.getObjective(scoreboardName) == null)
			return;
		obj = board.getObjective(scoreboardName);
		if (obj == null)
			return;
		obj.setDisplayName(MSG.color(value));
	}

	private void setLine(Player player, int line, String value) {
		Scoreboard board = player.getScoreboard();
		Objective obj;
		if (board == null || board.getObjective(scoreboardName) == null) {
			board = scoreboardManager.getNewScoreboard();
			player.setScoreboard(board);

			obj = board.registerNewObjective("default", "dummy", "a");
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		ChatColor[] vals = ChatColor.values();

		if (value == null) {
			if (!board.getScores(vals[line] + "" + ChatColor.RESET).isEmpty())
				board.resetScores(vals[line] + "" + ChatColor.RESET);
			return;
		}

		obj = board.getObjective("watchlist");

		Validate.isTrue(value.length() <= 124, "Value cannot exceed length of 124", value);

		String prefix = ChatColor.translateAlternateColorCodes('&', value.substring(0, Math.min(62, value.length())));
		String suffix = ChatColor.translateAlternateColorCodes('&',
				value.substring(Math.min(value.length(), 62), Math.max(value.length(), Math.min(value.length(), 62))));

		Team team = board.getTeam(vals[line] + "" + ChatColor.RESET);
		if (team == null)
			team = board.registerNewTeam(vals[line] + "" + ChatColor.RESET);

		team.setPrefix(prefix);
		team.setSuffix(suffix);
		team.addEntry(vals[line] + "" + ChatColor.RESET);
		obj.getScore(vals[line] + "" + ChatColor.RESET).setScore(line);
	}

	public void setScoreboard(Player p, CScoreboard board) {
		assigned.put(p.getUniqueId(), board);
	}

	@Override
	public ModulePriority getPriority() {
		return ModulePriority.LOW;
	}

}