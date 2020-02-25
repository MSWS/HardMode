package xyz.msws.hardmode.boss;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.events.BossSpawnEvent;
import xyz.msws.hardmode.modules.mobs.BossManager;
import xyz.msws.hardmode.modules.mobs.BossSpawner;

public class GolemSpawner extends BossSpawner {

	public GolemSpawner(HardMode plugin) {
		super(plugin);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getType() != Material.IRON_BLOCK)
			return;

		IronGolem golem = (IronGolem) event.getBlock().getWorld().spawnEntity(event.getBlock().getLocation(),
				EntityType.IRON_GOLEM);

		GolemBoss boss = new GolemBoss(plugin, golem);

		BossSpawnEvent e = new BossSpawnEvent(golem, boss);
		Bukkit.getPluginManager().callEvent(e);
		if (e.isCancelled()) {
			boss.getEntity().remove();
			return;
		}

		this.plugin.getModule(BossManager.class).addBoss(boss);
	}

	@Override
	public void disable() {
		BlockBreakEvent.getHandlerList().unregister(this);
	}

}
