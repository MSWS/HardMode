package xyz.msws.hardmode.mobs.behaviors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.GameMode;
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
import xyz.msws.hardmode.inventory.CItem;
import xyz.msws.hardmode.modules.mobs.BehaviorListener;
import xyz.msws.hardmode.modules.mobs.MobSelector;
import xyz.msws.hardmode.utils.CE;

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
//		loot.put(new ItemStack(Material.GOLD_BLOCK, 2), 1.0 / 400); // 18
//		loot.put(new ItemStack(Material.GOLD_BLOCK, 1), 1.0 / 300); // 18
//		loot.put(new ItemStack(Material.GOLDEN_APPLE, 1), 1.0 / 250); // 7
//		loot.put(new ItemStack(Material.GOLD_INGOT, 2), 1.0 / 200); // 2
//		loot.put(new ItemStack(Material.GOLD_INGOT, 1), 1.0 / 100); // 1
//		loot.put(new ItemStack(Material.GOLD_ORE, 1), 1.0 / 88); // 1
//		loot.put(new ItemStack(Material.GOLD_NUGGET, 3), 1.0 / 80); // 3/9
//		loot.put(new ItemStack(Material.GOLD_NUGGET, 2), 1.0 / 35); // 2/9
//		loot.put(new ItemStack(Material.GOLD_NUGGET, 1), 1.0 / 20); // 1/9
		for (String item : plugin.getConfig().getConfigurationSection("Mobs.Global.Loot").getKeys(false)) {
			String path = "Mobs.Global.Loot." + item;
			ItemStack i = new CItem(plugin.getConfig().getConfigurationSection(path).getValues(false)).build();
			loot.put(i, plugin.getConfig().getDouble(path + ".Probability"));
		}
	}

	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		if (!selector.matches(event.getEntity()))
			return;

		Monster entity = (Monster) event.getEntity();
		if (entity.fromMobSpawner()) {
			List<ItemStack> loot = new ArrayList<ItemStack>();
			for (ItemStack stack : event.getDrops())
				loot.add(new CItem(stack)
						.amount((int) Math.ceil(stack.getAmount() * CE.GLOBAL_MOBSPAWNERMODIFIER.doubleValue()))
						.build());
			event.getDrops().clear();
			event.getDrops().addAll(loot);
			return;
		}
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

		double hRange = CE.GLOBAL_HIVE_HORIZONTALRANGE.doubleValue();
		double vRange = CE.GLOBAL_HIVE_VERTICALRANGE.doubleValue();

		List<Entity> entities = entity.getNearbyEntities(hRange, vRange, hRange);

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
				mEnt.getWorld().playSound(mEnt.getLocation(), CE.GLOBAL_HIVE_SOUND_NAME.getValue(Sound.class),

						CE.GLOBAL_HIVE_SOUND_VOLUME.getValue(Number.class).floatValue(),
						CE.GLOBAL_HIVE_SOUND_PITCH.getValue(Number.class).floatValue());

			}
		}.runTaskTimer(plugin, CE.GLOBAL_HIVE_SOUND_RATE.longValue(), CE.GLOBAL_HIVE_SOUND_RATE.longValue());
	}

	@Override
	public void disable() {
		EntityDeathEvent.getHandlerList().unregister(this);
		EntityDamageByEntityEvent.getHandlerList().unregister(this);
	}
}
