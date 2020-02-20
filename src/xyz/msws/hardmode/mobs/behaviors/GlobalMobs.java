package xyz.msws.hardmode.mobs.behaviors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.modules.mobs.BehaviorListener;
import xyz.msws.hardmode.modules.mobs.MobSelector;

public class GlobalMobs extends BehaviorListener {
	private final Map<ItemStack, Double> loot;

	public GlobalMobs(HardMode plugin) {
		super(plugin);

		selector = new MobSelector() {
			@Override
			public boolean matches(Entity ent) {
				return (ent instanceof Monster);
			}
		};

		loot = new HashMap<ItemStack, Double>();
		loot.put(new ItemStack(Material.GOLD_BLOCK, 2), 1.0 / 400); // 18
		loot.put(new ItemStack(Material.GOLD_INGOT, 2), 1.0 / 200); // 2
		loot.put(new ItemStack(Material.GOLD_INGOT, 1), 1.0 / 100); // 1
		loot.put(new ItemStack(Material.GOLD_ORE, 1), 1.0 / 88); // 1
		loot.put(new ItemStack(Material.GOLD_NUGGET, 3), 1.0 / 40); // 3/9
		loot.put(new ItemStack(Material.GOLD_NUGGET, 2), 1.0 / 25); // 2/9
		loot.put(new ItemStack(Material.GOLD_NUGGET, 1), 1.0 / 20); // 1/9
	}

	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		if (!selector.matches(event.getEntity()))
			return;
//		Monster entity = (Monster) event.getEntity();
		ThreadLocalRandom random = ThreadLocalRandom.current();
		if (random.nextBoolean())
			return;
		for (Entry<ItemStack, Double> entry : loot.entrySet()) {
			if (random.nextDouble() > entry.getValue())
				continue;
			event.getDrops().add(entry.getKey());
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;

		Player player = (Player) event.getDamager();

		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		if (!selector.matches(event.getEntity()))
			return;

		Monster entity = (Monster) event.getEntity();

		List<Entity> entities = entity.getNearbyEntities(30, 10, 30);

		new BukkitRunnable() {
			int pos = -1;

			@Override
			public void run() {
				pos++;
				if (pos >= entities.size()) {
					this.cancel();
					return;
				}
				Entity ent = entities.get(pos);

				if (ent.equals(entity))
					return;
				if (!selector.matches(ent))
					return;
				Monster mEnt = (Monster) ent;
				if (mEnt.getTarget() != null && mEnt.getTarget().isValid())
					return;
				mEnt.setTarget(player);
				mEnt.getWorld().playSound(mEnt.getLocation(), Sound.UI_BUTTON_CLICK, .5f, .05f);

			}
		}.runTaskTimer(plugin, 20, 10);
	}

	@Override
	public void disable() {
		EntityDeathEvent.getHandlerList().unregister(this);
		EntityDamageByEntityEvent.getHandlerList().unregister(this);
	}
}
