package xyz.msws.hardmode.modules.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.utils.CE;
import xyz.msws.hardmode.utils.MSG;

public class HardCommand extends BukkitCommand {

	private HardMode plugin;

	protected HardCommand(HardMode plugin) {
		super("hard");

		this.plugin = plugin;
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (args.length == 0) {
			MSG.tell(sender, "Hard", MSG.ERROR + "Invalid Arguments.");
			return true;
		}

		switch (args[0].toLowerCase()) {
		case "reload":
			MSG.tell(sender, "Hard", "Old Speed: " + CE.BLAZE_PARTICLES_SPEED.doubleValue());
			plugin.reload();
			MSG.tell(sender, "Hard", "Successfully reloaded config.");
			MSG.tell(sender, "Hard", "New Speed: " + CE.BLAZE_PARTICLES_SPEED.doubleValue());
			break;
		}
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		List<String> result = new ArrayList<>();
		if (args.length > 1)
			return result;
		for (String s : new String[] { "reload" }) {
			if (s.toLowerCase().startsWith(args[0]))
				result.add(s);
		}

		return result;
	}

}
