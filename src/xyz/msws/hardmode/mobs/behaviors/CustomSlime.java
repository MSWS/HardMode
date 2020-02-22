package xyz.msws.hardmode.mobs.behaviors;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.attacks.AID;
import xyz.msws.hardmode.modules.mobs.BehaviorListener;
import xyz.msws.hardmode.modules.mobs.MobSelector;
import xyz.msws.hardmode.utils.CE;

public class CustomSlime extends BehaviorListener {

	private ThreadLocalRandom random;

	private Map<Slime, BukkitTask> tasks;

	public CustomSlime(HardMode plugin) {
		super(plugin);

		random = ThreadLocalRandom.current();
		tasks = new HashMap<Slime, BukkitTask>();

		selector = new MobSelector() {
			@Override
			public boolean matches(Entity ent) {
				return (ent instanceof Slime);
			}
		};
	}

	@EventHandler
	public void onTarget(EntityTargetLivingEntityEvent event) {
		if (!selector.matches(event.getEntity()))
			return;
		Slime slime = (Slime) event.getEntity();

		if (tasks.containsKey(slime))
			return;

		if (event.getTarget() == null)
			return;
		if (plugin.getConfig().getBoolean("DebugMode.Enabled"))
			plugin.log("Slime runnable started targetting " + event.getTarget() + ".");
		tasks.put(slime, charge(slime, event.getTarget()).runTaskTimer((Plugin) plugin,
				CE.SLIME_CHARGE_STARTDELAY.intValue(), CE.SLIME_CHARGE_PERIODDELAY.intValue()));
	}

	@EventHandler
	public void increaseSize(EntityDamageByEntityEvent event) {
		if (!selector.matches(event.getEntity()))
			return;
		Slime slime = (Slime) event.getEntity();
		if (random.nextDouble() > CE.SLIME_SIZEINCREASE_PROBABILITY.doubleValue())
			return;
		if (slime.isDead())
			return;
		if (slime.getSize() > CE.SLIME_SIZEINCREASE_MAXSIZE.intValue())
			return;
		slime.setSize(slime.getSize() + 1);
		slime.getWorld().playSound(slime.getLocation(), CE.SLIME_SIZEINCREASE_SOUND_NAME.getValue(Sound.class),
				CE.SLIME_SIZEINCREASE_SOUND_VOLUME.getValue(Number.class).floatValue(),
				CE.SLIME_SIZEINCREASE_SOUND_PITCH.getValue(Number.class).floatValue());
	}

	@EventHandler
	public void inflictSlow(EntityDamageByEntityEvent event) {
		if (!selector.matches(event.getDamager()))
			return;
		Slime slime = (Slime) event.getDamager();
		if (slime.getSize() < CE.SLIME_SLOWATTACK_MINIMUMSIZE.intValue())
			return;
		if (random.nextDouble() > CE.SLIME_SLOWATTACK_PROBABILITY.doubleValue())
			return;
		if (!(event.getEntity() instanceof LivingEntity))
			return;
		LivingEntity entity = (LivingEntity) event.getEntity();
		entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, CE.SLIME_SLOWATTACK_DURATION.intValue(),
				random.nextInt(CE.SLIME_SLOWATTACK_POWER_MINIMUM.intValue(),
						CE.SLIME_SLOWATTACK_POWER_MAXIMUM.intValue())));
	}

	public BukkitRunnable charge(Slime slime, Entity target) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				if (slime == null || !slime.isValid() || slime.getTarget() == null || !slime.getTarget().isValid()
						|| slime.getTarget() != target)
					return;
				plugin.getMobManager().getAttack(AID.CHARGE_AT).attack(slime, target);
			}
		};
	}

	@Override
	public void disable() {
		EntityDamageByEntityEvent.getHandlerList().unregister(this);
	}

}
