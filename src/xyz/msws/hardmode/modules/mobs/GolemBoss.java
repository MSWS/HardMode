package xyz.msws.hardmode.modules.mobs;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.attacks.AID;
import xyz.msws.hardmode.utils.Callback;
import xyz.msws.hardmode.utils.MSG;
import xyz.msws.hardmode.world.Area;

public class GolemBoss implements Boss, Listener {

	private IronGolem golem;

	private Area area;

	private BossBar bar;

	private PeriodManager period;

	public GolemBoss(IronGolem golem) {
		this.golem = (IronGolem) golem;

		golem.getAttribute(Attribute.GENERIC_MAX_HEALTH)
				.setBaseValue(golem.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() * 3);

		golem.setHealth(golem.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());

		this.area = new Area(golem.getLocation().clone().add(30, 30, 30),
				golem.getLocation().clone().subtract(30, 10, 30));

		bar = Bukkit.createBossBar(MSG.color(this.getBossType().getName()), BarColor.WHITE, BarStyle.SEGMENTED_12);

		period = new PeriodManager(golem);

		ThreadLocalRandom random = ThreadLocalRandom.current();

		period.addPeriodicalAction(new Callback<Object>() {
			@Override
			public void execute(Object arg) {
				for (Location l : area.getEdges(1)) {
					l.getWorld().spawnParticle(Particle.FLAME, l, 0);
				}
			}
		}, 500);

		period.addPeriodicalAction(new Callback<Object>() {
			@Override
			public void execute(Object j) {
				for (Location l : area.getFaces(3)) {
					l.getWorld().spawnParticle(Particle.BARRIER, l, 0);
				}
			}
		}, 1000);

		period.addPeriodicalAction(new Callback<Object>() {
			long start = System.currentTimeMillis();

			@Override
			public void execute(Object arg) {
				long time = System.currentTimeMillis() - start;
				double speed = golem.getHealth() / golem.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
				speed = 1000.0 * (speed);
				for (double y = 0; y < 2; y += .5) {
					Location off = golem.getLocation().clone().add(Math.cos(y * 5 + time / speed), y,
							Math.sin(y * 5.0 + time / speed));
					off.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, off, 0);
				}
			}
		}, 5);

		period.addPeriodicalAction(new Callback<Object>() {
			@Override
			public void execute(Object arg) {
				Sound[] sounds = new Sound[] { Sound.ENTITY_IRON_GOLEM_DEATH, Sound.ENTITY_GHAST_SCREAM,
						Sound.ENTITY_IRON_GOLEM_HURT, Sound.BLOCK_ANVIL_BREAK, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR,
						Sound.BLOCK_IRON_DOOR_OPEN, Sound.BLOCK_STONE_BREAK, Sound.BLOCK_STONE_PLACE,
						Sound.ENTITY_BLAZE_SHOOT };
				if (random.nextDouble() < golem.getHealth()
						/ golem.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())
					return;
				golem.getWorld().playSound(golem.getLocation(), sounds[random.nextInt(sounds.length)], 2,
						random.nextFloat() * 2);
			}
		}, 3500);

		period.addPeriodicalAction(new Callback<Object>() {
			@Override
			public void execute(Object arg) {
				golem.setHealth(golem.getHealth() * 1.1);
				golem.getWorld().playSound(golem.getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, 2, 1);
			}
		}, 10000);

		MobTargetter target = new MobTargetter() {
			@Override
			public Entity getTarget(List<Entity> targets) {
				targets = targets.stream().filter(ent -> getSelector().matches(ent)).collect(Collectors.toList());
				targets.remove(golem);
				targets.sort(distance(golem.getLocation()));
				if (targets.isEmpty())
					return null;
				return targets.get(0);
			}
		};

		period.addPeriodicalAttack(HardMode.getPlugin().getMobManager().getAttack(AID.GROUND_POUND), target, 5000);

		period.addPeriodicalAttack(HardMode.getPlugin().getMobManager().getAttack(AID.BLOCK_PHYSIC_THROW), target, 1000,
				Material.IRON_BLOCK);

		period.addPeriodicalAction(new Callback<Object>() {
			@Override
			public void execute(Object arg) {
				bar.setProgress(golem.getHealth() / golem.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			}
		}, 5);

		period.addPeriodicalAction(new Callback<Object>() {
			@Override
			public void execute(Object arg) {
				if (!area.contains(golem.getLocation())) {
					Vector off = area.getMiddle().toVector().subtract(golem.getLocation().toVector());
					off.multiply(.05);
					off.setY(1);
					golem.setVelocity(golem.getVelocity().clone().add(off));
				}
			}
		}, 2000);

		Bukkit.getPluginManager().registerEvents(this, HardMode.getPlugin());
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (!area.contains(player.getLocation())) {
			new BukkitRunnable() {
				@Override
				public void run() {
					bar.removePlayer(player);
				}
			}.runTaskLater(HardMode.getPlugin(), 20);
			return;
		}
		if (golem != null && golem.isValid())
			bar.addPlayer(player);
	}

	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		if (!event.getEntity().equals(golem))
			return;
		bar.removeAll();
	}

	@Override
	public BossType getBossType() {
		return BossType.GOLEM;
	}

	@Override
	public Entity getEntity() {
		return golem;
	}

	@Override
	public Map<List<ItemStack>, Double> getLoot() {
		return null;
	}

	@Override
	public Area getArea() {
		return area;
	}

	public Comparator<Entity> distance(Location loc) {
		return new Comparator<Entity>() {
			@Override
			public int compare(Entity o1, Entity o2) {
				return loc.distanceSquared(o1.getLocation()) > loc.distanceSquared(o2.getLocation()) ? 1 : -1;
			}
		};
	}

	@Override
	public MobSelector getSelector() {
		return new MobSelector() {
			@Override
			public boolean matches(Entity ent) {
				if (ent instanceof Player) {
					GameMode gm = ((Player) ent).getGameMode();
					return (gm == GameMode.SURVIVAL || gm == GameMode.ADVENTURE);
				}

				return (!ent.equals(golem) && ent.getType() != EntityType.IRON_GOLEM && ent instanceof LivingEntity);
			}
		};
	}

	@Override
	public PeriodManager getAttackManager() {
		return period;
	}

}
