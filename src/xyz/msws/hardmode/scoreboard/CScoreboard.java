package xyz.msws.hardmode.scoreboard;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public abstract class CScoreboard {
	protected List<String> lines;
	protected Player player;
	protected String title;

	public CScoreboard(final Player player) {
		this.lines = new ArrayList<String>();
		this.player = player;
		for (int i = 0; i <= 15; ++i) {
			this.lines.add(i, "");
		}
	}

	public abstract void onTick();

	public List<String> getLines() {
		return this.lines;
	}

	public String getLine(final int line) {
		return (line < 0 || line > 15 || line >= this.lines.size()) ? null : this.lines.get(line);
	}

	public void setLine(final int line, final String value) {
		this.lines.set(line, value);
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}
}
