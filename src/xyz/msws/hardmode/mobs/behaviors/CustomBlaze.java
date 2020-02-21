package xyz.msws.hardmode.mobs.behaviors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.modules.mobs.BehaviorListener;
import xyz.msws.hardmode.modules.mobs.MobSelector;

public class CustomBlaze extends BehaviorListener {

	private BukkitTask particle;

	public CustomBlaze(HardMode plugin) {
		super(plugin);

		selector = new MobSelector() {
			@Override
			public boolean matches(Entity ent) {
				return (ent.getType() == EntityType.BLAZE);
			}
		};

		long start = System.currentTimeMillis();

		double speed = .2;

		particle = new BukkitRunnable() {
			@Override
			public void run() {
				long time = System.currentTimeMillis() - start;
				for (World w : Bukkit.getWorlds()) {
					for (Blaze ent : w.getEntitiesByClass(Blaze.class)) {
						for (double i = 0; i < 10; i++) {
							Location off = ent.getLocation().clone();
							off.add(Math.sin(i + time), .5 + Math.cos(time / 400.0) * 1, Math.cos(i + time));
							double vx = Math.sin(i + time) * speed;
							double vy = 0;
							double vz = Math.cos(i + time) * speed;
							off.getWorld().spawnParticle(Particle.FLAME, off, 0, vx, vy, vz);
						}
					}
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}

	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		Chunk chunk = event.getChunk();
		List<Block> lava = new ArrayList<Block>();

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = chunk.getWorld().getSeaLevel(); y < 256; y++) {
					Block block = chunk.getBlock(x, y, z);
					if (!block.isLiquid())
						continue;
					if (block.getType() != Material.LAVA)
						continue;
					lava.add(block);
					break;
				}
			}
		}

		ThreadLocalRandom random = ThreadLocalRandom.current();

		for (Block b : lava) {
			if (random.nextDouble() > .02)
				continue;
			b.getLocation().getWorld().spawnEntity(b.getLocation(), EntityType.BLAZE);
			plugin.log("Spawned blaze at " + b.getLocation().getBlockX() + " " + b.getLocation().getBlockY() + " "
					+ b.getLocation().getBlockZ());
		}
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if (event.getEntity().getShooter() == null || !(event.getEntity().getShooter() instanceof Entity)
				|| !selector.matches((Entity) event.getEntity().getShooter()))
			return;
		if (!(event.getEntity() instanceof Fireball))
			return;
		event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 1.5f);
	}

	@Override
	public void disable() {
		particle.cancel();
		ChunkLoadEvent.getHandlerList().unregister(this);
		ProjectileHitEvent.getHandlerList().unregister(this);
	}

}
