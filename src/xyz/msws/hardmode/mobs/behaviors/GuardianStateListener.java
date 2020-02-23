package xyz.msws.hardmode.mobs.behaviors;

import org.bukkit.Bukkit;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.modules.AbstractModule;
import xyz.msws.hardmode.modules.ModulePriority;
import xyz.msws.hardmode.packets.PlayerElderGuardianEffectEvent;
import xyz.msws.hardmode.packets.WrapperPlayServerGameStateChange;

public class GuardianStateListener extends AbstractModule {

	public GuardianStateListener(HardMode plugin) {
		super("GuardianListener", plugin);
	}

	private ProtocolManager manager;

	@Override
	public void initialize() {
		manager = ProtocolLibrary.getProtocolManager();

		manager.addPacketListener(listen());
	}

	@Override
	public void disable() {
		manager.removePacketListener(listen());
	}

	public PacketAdapter listen() {
		return new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.GAME_STATE_CHANGE) {
			@Override
			public void onPacketSending(PacketEvent event) {
				WrapperPlayServerGameStateChange packet = new WrapperPlayServerGameStateChange(event.getPacket());
				if (packet.getReason() != 10)
					return;
				PlayerElderGuardianEffectEvent e = new PlayerElderGuardianEffectEvent(event.getPlayer());
				Bukkit.getPluginManager().callEvent(e);
				if (e.isCancelled())
					event.setCancelled(true);
			}
		};
	}

	@Override
	public ModulePriority getPriority() {
		return ModulePriority.LOWEST;
	}

}
