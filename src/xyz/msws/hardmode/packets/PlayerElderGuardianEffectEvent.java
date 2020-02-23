package xyz.msws.hardmode.packets;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerElderGuardianEffectEvent extends Event implements Cancellable {

	private boolean cancel = false;

	private static final HandlerList handlers = new HandlerList();

	private Player player;

	public PlayerElderGuardianEffectEvent(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public static HandlerList getHandlerList() {
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

	@Override
	public  HandlerList getHandlers() {
		return handlers;
	}

}
