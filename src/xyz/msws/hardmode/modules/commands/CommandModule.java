package xyz.msws.hardmode.modules.commands;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.modules.AbstractModule;
import xyz.msws.hardmode.modules.ModulePriority;
import xyz.msws.hardmode.utils.MSG;

/**
 * Module that manages all commands, use to enable/disable commands specifically
 * 
 * @author imodm
 *
 */
public class CommandModule extends AbstractModule implements Listener {

	private Map<Command, Boolean> commands;

	private CommandMap map;

	public CommandModule(HardMode plugin) {
		super("CommandModule", plugin);
	}

	@Override
	public void initialize() {
		Bukkit.getPluginManager().registerEvents(this, plugin);

		commands = new HashMap<>();

		try {
			map = Bukkit.getServer().getCommandMap();
		} catch (NoSuchMethodError e) {
			try {
				final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
				bukkitCommandMap.setAccessible(true);
				map = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
			} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
				MSG.error("Unable to get CommandMap");
				e.printStackTrace();
				return;
			}
		}

		MSG.log("&9Enabling commands...");

//		commands.put(new ACommand(), false);

		List<Command> cmds = new ArrayList<>();
		for (Command cmd : commands.keySet()) {
			cmds.add(cmd);
			commands.put(cmd, true);
		}

		enableCommands(cmds);
		MSG.log(MSG.SUCCESS + "Successfully enabled " + MSG.SUBJECT + commands.size() + MSG.SUCCESS + " command"
				+ (commands.size() == 1 ? "" : "s"));
	}

	public void enableCommands(List<Command> commands) {
		commands.forEach(c -> enableCommand(c));

	}

	public void enableCommand(Command command) {
		map.register(plugin.getName(), command);
		commands.put(command, true);
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.updateCommands();
		}
	}

	public void disableCommands(List<Command> commands) {
		commands.forEach(cmd -> disableCommand(cmd));
	}

	public void disableCommand(Command cmd) {
		Iterator<Entry<String, Command>> it = getKnownCommands().entrySet().iterator();

		while (it.hasNext()) {
			Entry<String, Command> c = it.next();
			if (c.getValue().equals(cmd)) {
				it.remove();
			}
		}

		commands.put(cmd, false);
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.updateCommands();
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Command> getKnownCommands() {
		try {
			return map.getKnownCommands();
		} catch (NoSuchMethodError e) {
			try {
				final Map<String, Command> knownCommands = (Map<String, Command>) getPrivateField(map, "knownCommands");
				return knownCommands;
			} catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
				e1.printStackTrace();
			}
		}

		return null;
	}

	public Command getCommand(String command) {
		return commands.keySet().stream().filter(cmd -> cmd.getName().equals(command)).findFirst().orElse(null);
	}

	public boolean isEnabled(Command cmd) {
		return commands.getOrDefault(cmd, true);
	}

	public Map<Command, Boolean> getCommands() {
		return commands;
	}

	@Override
	public void disable() {
		disableCommands(new ArrayList<Command>(commands.keySet()));
	}

	/*
	 * Code from "zeeveener" at
	 * https://bukkit.org/threads/how-to-unregister-commands-from-your-plugin.
	 * 131808/ , edited by RandomHashTags
	 */
	private Object getPrivateField(Object object, String field)
			throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Class<?> clazz = object.getClass();
		Field objectField = field
				.equals("commandMap")
						? clazz.getDeclaredField(field)
						: field.equals("knownCommands")
								? Bukkit.getVersion().contains("1.13") ? clazz.getSuperclass().getDeclaredField(field)
										: clazz.getDeclaredField(field)
								: null;
		objectField.setAccessible(true);
		Object result = objectField.get(object);
		objectField.setAccessible(false);
		return result;
	}

	@Override
	public ModulePriority getPriority() {
		return ModulePriority.MEDIUM;
	}

}
