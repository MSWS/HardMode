package xyz.msws.hardmode.modules.movement;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import xyz.msws.hardmode.HardMode;
import xyz.msws.hardmode.modules.AbstractModule;
import xyz.msws.hardmode.modules.ModulePriority;
import xyz.msws.hardmode.utils.MSG;

public class PlayerMovementModule extends AbstractModule {

	private HashMap<Material, Double> speedFactor;

	private BukkitTask task;

	public PlayerMovementModule(HardMode plugin) {
		super("PlayerMovement", plugin);
	}

	public Double getModifier(Material mat) {
		return speedFactor.getOrDefault(mat, 1.0);
	}

	@Override
	public void initialize() {
		speedFactor = new HashMap<>();

		Map<String, Double> mats = new HashMap<>();
		mats.put("DIAMOND", 0.001);
		mats.put("IRON", .002);
		mats.put("CHAINMAIL", 0.003);
		mats.put("LEATHER", 0.004);
		mats.put("GOLDEN", 0.0045);

		Map<String, Double> weights = new HashMap<>();
		weights.put("HELMET", 1.0);
		weights.put("CHESTPLATE", 2.0);
		weights.put("LEGGINGS", 1.8);
		weights.put("BOOTS", 1.5);

		for (String matType : mats.keySet()) {
			for (String weight : weights.keySet()) {
				String name = matType + "_" + weight;
				Material mat = Material.valueOf(name);
				speedFactor.put(mat, 1 + (mats.get(matType) * weights.get(weight)));
			}
		}

		speedFactor.put(Material.DIAMOND_SWORD, 1 + .0008);
		speedFactor.put(Material.DIAMOND_AXE, 1 + .0004);
		speedFactor.put(Material.IRON_SWORD, 1 + .001);
		speedFactor.put(Material.IRON_AXE, 1 + .0005);
		speedFactor.put(Material.GOLDEN_SWORD, 1 + .0045);
		speedFactor.put(Material.GOLDEN_AXE, 1 + .00225);
		speedFactor.put(Material.BLAZE_ROD, 1 + .009);
		speedFactor.put(Material.BOW, 1 + .0047);
		speedFactor.put(Material.AIR, 1 + .005);
		speedFactor.put(Material.SHIELD, 1 + .0008);
		speedFactor.put(Material.OBSIDIAN, 1 + -.0005);
		speedFactor.put(Material.DIAMOND_BLOCK, 1 + -.005);
		speedFactor.put(Material.IRON_BLOCK, 1 + -.004);
		speedFactor.put(Material.GOLD_BLOCK, 1 + -.003);

		resetSpeeds();

		task = new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					EntityEquipment equip = player.getEquipment();
					if (equip == null || equip.getArmorContents() == null)
						continue;

					ItemStack[] armor = equip.getArmorContents();
					AttributeInstance att = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);

					double speed = 1;

					for (ItemStack item : armor) {
						if (item == null || item.getType() == Material.AIR) {
							speed *= getModifier(Material.AIR);
							continue;
						}
						speed *= getModifier(item.getType());
					}

					speed *= getModifier(equip.getItemInMainHand().getType());
					speed *= getModifier(equip.getItemInOffHand().getType());

					Material main = equip.getItemInMainHand() == null ? Material.AIR
							: equip.getItemInMainHand().getType();
					Material off = equip.getItemInOffHand() == null ? Material.AIR : equip.getItemInOffHand().getType();

					speed *= getModifier(main);
					speed *= getModifier(off);

					double target = att.getDefaultValue() * speed;
					double diff = target - att.getDefaultValue();

					Iterator<AttributeModifier> it = att.getModifiers().iterator();
					while (it.hasNext()) {
						AttributeModifier mod = it.next();
						if (mod.getName().equals("HardMode")) {
							if (mod.getAmount() == diff)
								return;
							att.removeModifier(mod);
						}
					}

					MSG.tell(player, "Speed",
							MSG.NUMBER + "" + target + MSG.FORMAT_SEPARATOR + " (" + MSG.FORMAT_INFO
									+ MSG.parseDecimal(target / att.getDefaultValue() * 100, 2) + "%"
									+ MSG.FORMAT_SEPARATOR + ")");

					att.addModifier(new AttributeModifier("HardMode", diff, Operation.ADD_NUMBER));
				}

			}
		}.runTaskTimer(plugin, 0, 3);
	}

	public void resetSpeeds() {
		for (World w : Bukkit.getWorlds()) {
			for (Entity ent : w.getEntities()) {
				if (!(ent instanceof LivingEntity))
					continue;
				LivingEntity e = (LivingEntity) ent;
				AttributeInstance inst = e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
				Iterator<AttributeModifier> it = e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getModifiers()
						.iterator();
				while (it.hasNext())
					inst.removeModifier(it.next());
				inst.setBaseValue(inst.getDefaultValue());
			}
		}
	}

	@Override
	public void disable() {
		task.cancel();
		resetSpeeds();
	}

	@Override
	public ModulePriority getPriority() {
		return ModulePriority.LOW;
	}

}
