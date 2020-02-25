package xyz.msws.hardmode.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

import xyz.msws.hardmode.boss.HBoss;

public class BossSpawnEvent extends EntityEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancel = false;

	private HBoss boss;

	public BossSpawnEvent(Entity what, HBoss boss) {
		super(what);
		this.boss = boss;
	}

	public HBoss getBoss() {
		return boss;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

}
