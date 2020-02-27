package xyz.msws.hardmode.utils;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MSG {
	private final static int CENTER_PX = 154;

	public static ChatColor ALL = ChatColor.WHITE;
	public static ChatColor PLAYER = ChatColor.YELLOW;
	public static ChatColor STAFF = ChatColor.GOLD;

	public static ChatColor ADMIN = ChatColor.RED;

	public static ChatColor DEFAULT = ChatColor.GRAY;

	public static ChatColor FORMATTER = ChatColor.GRAY;
	public static ChatColor FORMAT_INFO = ChatColor.GREEN;
	public static ChatColor FORMAT_SEPARATOR = ChatColor.YELLOW;

	public static ChatColor NUMBER = ChatColor.YELLOW;
	public static ChatColor TIME = ChatColor.GOLD;
	public static ChatColor DATE = ChatColor.DARK_GREEN;
	public static ChatColor MONEY = ChatColor.GREEN;

	public static ChatColor SUBJECT = ChatColor.AQUA;

	public static ChatColor PREFIX = ChatColor.BLUE;

	public static ChatColor ERROR = ChatColor.RED;
	public static ChatColor FAIL = ChatColor.RED;
	public static ChatColor SUCCESS = ChatColor.GREEN;

	public static ChatColor BOLD = ChatColor.BOLD;
	public static ChatColor ITALIC = ChatColor.ITALIC;
	public static ChatColor MAGIC = ChatColor.MAGIC;
	public static ChatColor UNDER = ChatColor.UNDERLINE;
	public static ChatColor STRIKE = ChatColor.STRIKETHROUGH;
	public static ChatColor RESET = ChatColor.RESET;

	public static void log(String message) {
		tell(Bukkit.getConsoleSender(), MSG.DEFAULT + "[INFO] " + message);
	}

	public static void warn(String message) {
		tell(Bukkit.getConsoleSender(), MSG.ERROR + "[WARN] " + message);
	}

	public static void error(String message) {
		tell(Bukkit.getConsoleSender(), MSG.FAIL + "[ERROR] " + message);
	}

	public static void printStackTrace() {
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		for (int i = 2; i < elements.length; i++)
			log(elements[i].toString());
	}

	public static void tell(CommandSender sender, Object msg) {
		if (msg == null)
			return;
		if (msg instanceof Collection<?>) {
			((Collection<?>) msg).forEach((obj) -> tell(sender, obj));
		} else if (msg instanceof Object[]) {
			for (Object obj : (Object[]) msg)
				tell(sender, obj);
		} else {
			sender.sendMessage(color(msg.toString()));
		}
	}

	public static void log(Object message) {
		tell(Bukkit.getConsoleSender(), message);
	}

	public static String color(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public static void tell(CommandSender sender, String module, String message) {
		if (StringUtils.isAlphanumeric(message.charAt(message.length() - 1) + ""))
			message += MSG.DEFAULT + ".";

		tell(sender, PREFIX + module + "> " + DEFAULT + message);
	}

	public static void tell(World world, String module, String message) {
		for (Player p : world.getPlayers())
			tell(p, module, message);
	}

	public static void tell(World world, String message) {
		for (Player p : world.getPlayers())
			tell(p, message);
	}

	public static String format(Location loc, boolean world, boolean decimals) {
		String result = world ? FORMATTER + loc.getWorld().getName() + " " : "";
		String x, y, z;
		if (decimals) {
			x = loc.getX() + "";
			y = loc.getY() + "";
			z = loc.getZ() + "";
		} else {
			x = loc.getBlockX() + "";
			y = loc.getBlockY() + "";
			z = loc.getBlockZ() + "";
		}

		result += FORMATTER + "X: " + NUMBER + x;
		result += FORMATTER + " Y: " + NUMBER + y;
		result += FORMATTER + " Z: " + NUMBER + z;
		return result;
	}

	public static void announce(String message) {
		Bukkit.getOnlinePlayers().forEach(p -> tell(p, message));
	}

	public static void announce(String perm, String message) {
		Bukkit.getOnlinePlayers().parallelStream().filter(p -> p.hasPermission(perm)).forEach(p -> tell(p, message));
	}

	public static String oo(boolean status) {
		return (status ? "&aOn" : "&cOff") + MSG.DEFAULT;
	}

	public static String tf(boolean bool) {
		return (bool ? "&aTrue" : "&cFalse") + MSG.DEFAULT;
	}

	public static String ed(boolean is) {
		return (is ? "&aEnabled" : "&cDisabled") + MSG.DEFAULT;
	}

	/**
	 * Returns string with camel case, and with _'s replaced with spaces
	 * 
	 * @param string hello_how is everyone
	 * @return Hello How Is Everyone
	 */
	public static String camelCase(String string) {
		String prevChar = " ";
		String res = "";
		for (int i = 0; i < string.length(); i++) {
			if (i > 0)
				prevChar = string.charAt(i - 1) + "";
			if (prevChar.matches("[a-zA-Z]")) {
				res = res + ((string.charAt(i) + "").toLowerCase());
			} else {
				res = res + ((string.charAt(i) + "").toUpperCase());
			}
		}
		return res.replace("_", " ");
	}

	public static String plural(String name) {
		return name + (name.toLowerCase().endsWith("s") ? "'" : "'s");
	}

	public static String plural(String name, int amo) {
		if (amo != 1)
			return plural(name);
		return name;
	}

	public static String circle(String message, int length, long tick) {
		if (message.length() <= length)
			return message;
		message = message.substring((int) (tick % message.length()))
				+ message.substring(0, (int) (tick % message.length()));
		message = message.substring(0, Math.min(length, message.length()));
		return message;
	}

	public enum DefaultFontInfo {
		A('A', 5), a('a', 5), B('B', 5), b('b', 5), C('C', 5), c('c', 5), D('D', 5), d('d', 5), E('E', 5), e('e', 5),
		F('F', 5), f('f', 4), G('G', 5), g('g', 5), H('H', 5), h('h', 5), I('I', 3), i('i', 1), J('J', 5), j('j', 5),
		K('K', 5), k('k', 4), L('L', 5), l('l', 1), M('M', 5), m('m', 5), N('N', 5), n('n', 5), O('O', 5), o('o', 5),
		P('P', 5), p('p', 5), Q('Q', 5), q('q', 5), R('R', 5), r('r', 5), S('S', 5), s('s', 5), T('T', 5), t('t', 4),
		U('U', 5), u('u', 5), V('V', 5), v('v', 5), W('W', 5), w('w', 5), X('X', 5), x('x', 5), Y('Y', 5), y('y', 5),
		Z('Z', 5), z('z', 5), NUM_1('1', 5), NUM_2('2', 5), NUM_3('3', 5), NUM_4('4', 5), NUM_5('5', 5), NUM_6('6', 5),
		NUM_7('7', 5), NUM_8('8', 5), NUM_9('9', 5), NUM_0('0', 5), EXCLAMATION_POINT('!', 1), AT_SYMBOL('@', 6),
		NUM_SIGN('#', 5), DOLLAR_SIGN('$', 5), PERCENT('%', 5), UP_ARROW('^', 5), AMPERSAND('&', 5), ASTERISK('*', 5),
		LEFT_PARENTHESIS('(', 4), RIGHT_PERENTHESIS(')', 4), MINUS('-', 5), UNDERSCORE('_', 5), PLUS_SIGN('+', 5),
		EQUALS_SIGN('=', 5), LEFT_CURL_BRACE('{', 4), RIGHT_CURL_BRACE('}', 4), LEFT_BRACKET('[', 3),
		RIGHT_BRACKET(']', 3), COLON(':', 1), SEMI_COLON(';', 1), DOUBLE_QUOTE('"', 3), SINGLE_QUOTE('\'', 1),
		LEFT_ARROW('<', 4), RIGHT_ARROW('>', 4), QUESTION_MARK('?', 5), SLASH('/', 5), BACK_SLASH('\\', 5),
		LINE('|', 1), TILDE('~', 5), TICK('`', 2), PERIOD('.', 1), COMMA(',', 1), SPACE(' ', 3), DEFAULT('a', 4);
		private char character;
		private int length;

		DefaultFontInfo(char character, int length) {
			this.character = character;
			this.length = length;
		}

		public char getCharacter() {
			return this.character;
		}

		public int getLength() {
			return this.length;
		}

		public int getBoldLength() {
			if (this == DefaultFontInfo.SPACE)
				return this.getLength();
			return this.length + 1;
		}

		public static DefaultFontInfo getDefaultFontInfo(char c) {
			for (DefaultFontInfo dFI : DefaultFontInfo.values()) {
				if (dFI.getCharacter() == c)
					return dFI;
			}
			return DefaultFontInfo.DEFAULT;
		}
	}

	public static String centerMessage(String message) {
		message = ChatColor.translateAlternateColorCodes('&', message);

		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;

		for (char c : message.toCharArray()) {
			if (c == '\u00a7') {
				previousCode = true;
				continue;
			} else if (previousCode == true) {
				previousCode = false;
				if (c == 'l' || c == 'L') {
					isBold = true;
					continue;
				} else
					isBold = false;
			} else {
				DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
				messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
				messagePxSize++;
			}
		}

		int halvedMessageSize = messagePxSize / 2;
		int toCompensate = CENTER_PX - halvedMessageSize;
		int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
		int compensated = 0;
		StringBuilder sb = new StringBuilder();
		while (compensated < toCompensate) {
			sb.append(" ");
			compensated += spaceLength;
		}
		return sb.toString() + message;
	}

	/**
	 * Good luck figuring this out
	 * 
	 * @param msg        Message to encrypt
	 * @param length     Length of the output
	 * @param iterations Amount of iterations to hash
	 * @return Hashed result, with length of @param length
	 */
	public static String hash(String msg, int length, int iterations) {
		msg = msg.replace(" ", "");
		if (msg.isEmpty())
			return "";
		String result = "", tmp = "";
		double total = 0, avg = 0, odds = 0, evens = 0;
		for (int i = 0; i < msg.length(); i++) {
			total += msg.charAt(i);
			if (i % 2 == 0) {
				odds += msg.charAt(i);
			} else {
				evens += msg.charAt(i);
			}
		}
		avg = total / msg.length();
		result += "" + (msg.length() / 5.58462 * (evens + 6481.5135)) * ((double) msg.charAt(0)) * (double) total / avg
				* odds;

		result += result.length() * Math.pow(evens, odds) / result.charAt((result.length() - 1) / msg.length() / 5) + ""
				+ Math.copySign(odds, evens * total);

		for (int i = 1; i < msg.length() - 1; i += Math.ceil(msg.length() / 5.0)) {
			result = result.substring(0, Math.min(i, result.length())) + Math.pow(avg * total, i)
					+ Math.sqrt(odds) * Math.log(evens) + result.substring(Math.min(i, result.length()));
		}

		for (int i = (int) Math.ceil(result.length() / avg); i < result.length(); i += Math
				.max(Math.ceil(result.length() / (length * Math.PI)), 1)) {
			if ((result.charAt(i) + "").matches("[0-9]")) {
				result = result.substring(0, i - 1)
						+ (char) (Math.floor((Integer.parseInt(result.charAt(i) + "") * 2.8)) + 65)
						+ result.substring(i, result.length() - 1);
			}
		}

		result = result.replaceAll("[^A-Z0-9]", "");

		while (result.length() < length)
			result = hash(result, length - iterations, --iterations);

		while (iterations > 0)
			result = hash(result, length + iterations, --iterations);

		for (int i = 0; i < result.length() && i < length; i++)
			tmp += result.charAt((int) ((i + avg) % result.length()));

		return tmp;
	}

	public static String hashWithSalt(String salt, String password, int length, int iterations) {
		String result = salt + password;
		for (int i = 0; i < iterations; i++)
			result = hash(salt + result, length, 1);
		return result;
	}

	public static String getTime(long mils) {
		if (mils == 0) {
			return "Just Now";
		}

		boolean isNegative = mils < 0;
		double mil = Math.abs(mils);
		String names[] = { "milliseconds", "seconds", "minutes", "hours", "days", "weeks", "months", "years", "decades",
				"centuries" };
		String sNames[] = { "millisecond", "second", "minute", "hour", "day", "week", "month", "year", "decade",
				"century" };
		Double length[] = { 1.0, 1000.0, 60000.0, 3.6e+6, 8.64e+7, 6.048e+8, 2.628e+9, 3.154e+10, 3.154e+11,
				3.154e+12 };
		String suff = "";
		for (int i = length.length - 1; i >= 0; i--) {
			if (mil >= length[i]) {
				if (suff.equals(""))
					suff = names[i];
				mil = mil / length[i];
				if (mil == 1) {
					suff = sNames[i];
					// suff = suff.substring(0, suff.length() - 1);
				}
				break;
			}
		}
		String name = mil + "";
		if (Math.round(mil) == mil) {
			name = (int) Math.round(mil) + "";
		}
		if (name.contains(".")) {
			if (name.split("\\.")[1].length() > 2)
				name = parseDecimal(name, 2);
		}
		if (isNegative)
			name = "-" + name;
		return name + " " + suff;
	}

	public static String parseDecimal(double decimal, int length) {
		return String.format("%." + length + "f", decimal);
	}

	public static String parseDecimal(String decimal, int length) {
		return parseDecimal(Double.parseDouble(decimal), length);
	}
}
