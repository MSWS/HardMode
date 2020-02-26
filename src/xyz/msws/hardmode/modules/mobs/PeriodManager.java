package xyz.msws.hardmode.modules.mobs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.attacks.Attack;
import xyz.msws.hardmode.utils.Callback;

public class PeriodManager {

	private Entity entity;

	private List<BukkitTask> tasks;

	public PeriodManager(Entity entity) {
		this.entity = entity;
		this.tasks = new ArrayList<BukkitTask>();
	}

	public void addPeriodicalAction(Callback<Object> call, long time) {
		tasks.add(new BukkitRunnable() {
			long nextAction = System.currentTimeMillis();

			@Override
			public void run() {
				if (entity == null || !entity.isValid()) {
					this.cancel();
					return;
				}
				if (System.currentTimeMillis() <= nextAction)
					return;
				nextAction = System.currentTimeMillis() + time;
				call.execute(null);
			}
		}.runTaskTimer(HardMode.getPlugin(), 0, 1));
	}

	public void addPeriodicalAttack(Attack attack, Entity entity, long time, Object... data) {
		tasks.add(new BukkitRunnable() {
			long nextAttack = System.currentTimeMillis();

			@Override
			public void run() {
				if (entity == null || !entity.isValid()) {
					this.cancel();
					return;
				}
				if (System.currentTimeMillis() <= time)
					return;

				nextAttack = System.currentTimeMillis() + nextAttack;

				attack.attack(PeriodManager.this.entity, entity, data);
			}
		}.runTaskTimer(HardMode.getPlugin(), 0, 1));
	}

	public void addPeriodicalAttack(Attack attack, MobTargetter target, long time, Object... data) {
		tasks.add(new BukkitRunnable() {
			long nextAttack = System.currentTimeMillis();

			@Override
			public void run() {
				if (entity == null || !entity.isValid()) {
					this.cancel();
					return;
				}
				if (System.currentTimeMillis() <= nextAttack)
					return;

				nextAttack = System.currentTimeMillis() + time;

				Entity t = target.getTarget(PeriodManager.this.entity.getWorld().getEntities());
				if (t == null || !t.isValid())
					return;
				attack.attack(PeriodManager.this.entity, t, data);
			}
		}.runTaskTimer(HardMode.getPlugin(), 0, 1));
	}

	public List<BukkitTask> getTasks() {
		return tasks;
	}

	public void cancelAll() {
		tasks.forEach(t -> t.cancel());
	}
}
