package xyz.msws.hardmode.modules.combat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.packets.WrapperPlayServerWorldParticles;

public class SweepAttackPreventor {
	private ProtocolManager manager;

	private PacketListener listener;

	public SweepAttackPreventor(HardMode plugin) {
		manager = ProtocolLibrary.getProtocolManager();

		listener = new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.WORLD_PARTICLES) {
			@Override
			public void onPacketSending(PacketEvent event) {
				WrapperPlayServerWorldParticles wrapper = new WrapperPlayServerWorldParticles(event.getPacket());
				event.setCancelled(wrapper.getParticleData() == 0);
			}
		};
		manager.addPacketListener(listener);
	}

	public void disable() {
		manager.removePacketListener(listener);
	}
}
