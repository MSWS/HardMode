package xyz.msws.hardmode.utils;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import xyz.msws.hardmode.HardMode;

public enum CE {
	GLOBAL_MOBSPAWNERMODIFIER("Mobs.Global.MobSpawnerModifier", 0.5),
	GLOBAL_HIVE_HORIZONTALRANGE("Mobs.Global.Hive.HorizontalRange", 30),
	GLOBAL_HIVE_VERTICALRANGE("Mobs.Global.Hive.VerticalRange", 10),
	GLOBAL_HIVE_SOUND_NAME("Mobs.Global.Hive.Sound.Name", "UI_BUTTON_CLICK"),
	GLOBAL_HIVE_SOUND_VOLUME("Mobs.Global.Hive.Sound.Volume", 0.5),
	GLOBAL_HIVE_SOUND_PITCH("Mobs.Global.Hive.Sound.Pitch", 0.05),
	GLOBAL_HIVE_SOUND_RATE("Mobs.Global.Hive.Sound.Rate", 5),
	GLOBAL_LOOT_BLOCK_ICON("Mobs.Global.Loot.block.Icon", "GOLD_BLOCK"),
	GLOBAL_LOOT_BLOCK_AMOUNT("Mobs.Global.Loot.block.Amount", 2),
	GLOBAL_LOOT_BLOCK_PROBABILITY("Mobs.Global.Loot.block.Probability", 0.0025),
	GLOBAL_LOOT_BLOCK2_ICON("Mobs.Global.Loot.block2.Icon", "GOLD_BLOCK"),
	GLOBAL_LOOT_BLOCK2_PROBABILITY("Mobs.Global.Loot.block2.Probability", 0.00333),
	GLOBAL_LOOT_GAPPLE_ICON("Mobs.Global.Loot.gapple.Icon", "GOLDEN_APPLE"),
	GLOBAL_LOOT_GAPPLE_PROBABILITY("Mobs.Global.Loot.gapple.Probability", 0.004),
	GLOBAL_LOOT_GINGOT_ICON("Mobs.Global.Loot.gingot.Icon", "GOLD_INGOT"),
	GLOBAL_LOOT_GINGOT_AMOUNT("Mobs.Global.Loot.gingot.Amount", 2),
	GLOBAL_LOOT_GINGOT_PROBABILITY("Mobs.Global.Loot.gingot.Probability", 0.005),
	GLOBAL_LOOT_GINGOT2_ICON("Mobs.Global.Loot.gingot2.Icon", "GOLD_INGOT"),
	GLOBAL_LOOT_GINGOT2_PROBABILITY("Mobs.Global.Loot.gingot2.Probability", 0.01),
	GLOBAL_LOOT_GORE_ICON("Mobs.Global.Loot.gore.Icon", "GOLD_ORE"),
	GLOBAL_LOOT_GORE_PROBABILITY("Mobs.Global.Loot.gore.Probability", 0.0113),
	GLOBAL_LOOT_GNUG_ICON("Mobs.Global.Loot.gnug.Icon", "GOLD_NUGGET"),
	GLOBAL_LOOT_GNUG_AMOUNT("Mobs.Global.Loot.gnug.Amount", 3),
	GLOBAL_LOOT_GNUG_PROBABILITY("Mobs.Global.Loot.gnug.Probability", 0.0125),
	GLOBAL_LOOT_GNUG1_ICON("Mobs.Global.Loot.gnug1.Icon", "GOLD_NUGGET"),
	GLOBAL_LOOT_GNUG1_AMOUNT("Mobs.Global.Loot.gnug1.Amount", 2),
	GLOBAL_LOOT_GNUG1_PROBABILITY("Mobs.Global.Loot.gnug1.Probability", 0.0285),
	GLOBAL_LOOT_GNUG3_ICON("Mobs.Global.Loot.gnug3.Icon", "GOLD_NUGGET"),
	GLOBAL_LOOT_GNUG3_AMOUNT("Mobs.Global.Loot.gnug3.Amount", 1),
	GLOBAL_LOOT_GNUG3_PROBABILITY("Mobs.Global.Loot.gnug3.Probability", 0.05),
	BLAZE_PARTICLES_SPEED("Mobs.Blaze.Particles.Speed", 0.2), BLAZE_PARTICLES_LINES("Mobs.Blaze.Particles.Lines", 10),
	BLAZE_PARTICLES_YRATE("Mobs.Blaze.Particles.YRate", 400),
	BLAZE_SPAWNING_VALIDBLOCKS("Mobs.Blaze.Spawning.ValidBlocks", "LAVA"),
	BLAZE_SPAWNING_PROBABILITY("Mobs.Blaze.Spawning.Probability", 0.40),
	BLAZE_FIREBALLS_POWER("Mobs.Blaze.Fireballs.Power", 1.5), CREEPER_FUSETICKS("Mobs.Creeper.FuseTicks", 15),
	CREEPER_TNTRATE("Mobs.Creeper.TNTRate", 0.05), CREEPER_TNTPOWERED("Mobs.Creeper.TNTPowered", true),
	ENDERMAN_UNTARGETDISTANCESQUARED("Mobs.Enderman.UntargetDistanceSquared", 2500),
	ENDERMAN_DAMAGE("Mobs.Enderman.Damage", 5.5), ENDERMAN_CHUCK_PROBABILITY("Mobs.Enderman.Chuck.Probability", 0.7),
	ENDERMAN_CHUCK_STARTDELAY("Mobs.Enderman.Chuck.StartDelay", 60),
	ENDERMAN_CHUCK_PERIODDELAY("Mobs.Enderman.Chuck.PeriodDelay", 40),
	ENDERMAN_CHUCK_SOUND_NAME("Mobs.Enderman.Chuck.Sound.Name", "ENTITY_ENDERMAN_SCREAM"),
	ENDERMAN_CHUCK_SOUND_VOLUME("Mobs.Enderman.Chuck.Sound.Volume", 2),
	ENDERMAN_CHUCK_SOUND_PITCH("Mobs.Enderman.Chuck.Sound.Pitch", 1),
	ENDERMAN_GRABTELEPORT_PROBABILITY_REGULAR("Mobs.Enderman.GrabTeleport.Probability.Regular", 0.6),
	ENDERMAN_GRABTELEPORT_PROBABILITY_BLOCKING("Mobs.Enderman.GrabTeleport.Probability.Blocking", 0.15),
	ENDERMAN_TELEPORTBEHIND_PROBABILITY("Mobs.Enderman.TeleportBehind.Probability", 0.4),
	ENDERMAN_TELEPORTBEHIND_MINIMUMDISTANCE("Mobs.Enderman.TeleportBehind.MinimumDistance", 25),
	ENDERMAN_TELEPORTBEHIND_DISTANCE_MINIMUM("Mobs.Enderman.TeleportBehind.Distance.Minimum", -3),
	ENDERMAN_TELEPORTBEHIND_DISTANCE_MAXIMUM("Mobs.Enderman.TeleportBehind.Distance.Maximum", -1),
	GHAST_UNTARGETDISTANCESQUARED("Mobs.Ghast.UntargetDistanceSquared", 2500),
	GHAST_SPAWNING_BLOCKHEIGHT("Mobs.Ghast.Spawning.BlockHeight", 235),
	GHAST_SPAWNING_MESSAGECOOLDOWN("Mobs.Ghast.Spawning.MessageCooldown", 600),
	GHAST_SPAWNING_MESSAGEONPLACE("Mobs.Ghast.Spawning.MessageOnPlace",
			"An odd presence says you shouldn't place blocks this high..."),
	GHAST_SPAWNING_MESSAGEONSPAWN("Mobs.Ghast.Spawning.MessageOnSpawn", "You should have listened..."),
	GHAST_SPAWNING_HORIZONTALRANGE("Mobs.Ghast.Spawning.HorizontalRange", 30),
	GHAST_SPAWNING_VERTICALRANGE("Mobs.Ghast.Spawning.VerticalRange", 5),
	GHAST_SHOOT_PROBABILITY("Mobs.Ghast.Shoot.Probability", .05),
	GHAST_SHOOT_STARTDELAY("Mobs.Ghast.Shoot.StartDelay", 10),
	GHAST_SHOOT_PERIODDELAY("Mobs.Ghast.Shoot.PeriodDelay", 120),
	SKELETON_UNTARGETDISTANCESQUARED("Mobs.Skeleton.UntargetDistanceSquared", 2500),
	SKELETON_SPEED("Mobs.Skeleton.Speed", 0.52),
	SKELETON_FASTARROW_STARTDELAY("Mobs.Skeleton.FastArrow.StartDelay", 10),
	SKELETON_FASTARROW_PERIODDELAY("Mobs.Skeleton.FastArrow.PeriodDelay", 40),
	SKELETON_RESPAWN_PROBABILITY("Mobs.Skeleton.Respawn.Probability", 0.25),
	SKELETON_RESPAWN_TIME("Mobs.Skeleton.Respawn.Time", 3000),
	SKELETON_RESPAWN_RESPAWNINGPARTICLES("Mobs.Skeleton.Respawn.RespawningParticles", "FLAME"),
	SKELETON_RESPAWN_SOUNDS_RESPAWNING_NAME("Mobs.Skeleton.Respawn.Sounds.Respawning.Name", "ENTITY_SKELETON_AMBIENT"),
	SKELETON_RESPAWN_SOUNDS_RESPAWNING_VOLUME("Mobs.Skeleton.Respawn.Sounds.Respawning.Volume", 2),
	SKELETON_RESPAWN_SOUNDS_RESPAWNING_PITCH("Mobs.Skeleton.Respawn.Sounds.Respawning.Pitch", 0.1),
	SKELETON_RESPAWN_SOUNDS_RESPAWNING_RATE("Mobs.Skeleton.Respawn.Sounds.Respawning.Rate", 200),
	SKELETON_RESPAWN_SOUNDS_RESPAWNED_NAME("Mobs.Skeleton.Respawn.Sounds.Respawned.Name", "ENTITY_BLAZE_AMBIENT"),
	SKELETON_RESPAWN_SOUNDS_RESPAWNED_VOLUME("Mobs.Skeleton.Respawn.Sounds.Respawned.Volume", 2),
	SKELETON_RESPAWN_SOUNDS_RESPAWNED_PITCH("Mobs.Skeleton.Respawn.Sounds.Respawned.Pitch", 0.25),
	SLIME_CHARGE_STARTDELAY("Mobs.Slime.Charge.StartDelay", 25),
	SLIME_CHARGE_PERIODDELAY("Mobs.Slime.Charge.PeriodDelay", 80),
	SLIME_SIZEINCREASE_PROBABILITY("Mobs.Slime.SizeIncrease.Probability", 0.2),
	SLIME_SIZEINCREASE_MAXSIZE("Mobs.Slime.SizeIncrease.MaxSize", 5),
	SLIME_SIZEINCREASE_SOUND_NAME("Mobs.Slime.SizeIncrease.Sound.Name", "BLOCK_SLIME_BLOCK_FALL"),
	SLIME_SIZEINCREASE_SOUND_VOLUME("Mobs.Slime.SizeIncrease.Sound.Volume", 2),
	SLIME_SIZEINCREASE_SOUND_PITCH("Mobs.Slime.SizeIncrease.Sound.Pitch", 1),
	SLIME_SLOWATTACK_MINIMUMSIZE("Mobs.Slime.SlowAttack.MinimumSize", 2),
	SLIME_SLOWATTACK_PROBABILITY("Mobs.Slime.SlowAttack.Probability", 0.3),
	SLIME_SLOWATTACK_DURATION("Mobs.Slime.SlowAttack.Duration", 100),
	SLIME_SLOWATTACK_POWER_MINIMUM("Mobs.Slime.SlowAttack.Power.Minimum", 1),
	SLIME_SLOWATTACK_POWER_MAXIMUM("Mobs.Slime.SlowAttack.Power.Maximum", 3), SPIDER_HEALTH("Mobs.Spider.Health", 11),
	SPIDER_SPEED("Mobs.Spider.Speed", 0.62), SPIDER_COBTHROW_STARTDELAY("Mobs.Spider.CobThrow.StartDelay", 20),
	SPIDER_COBTHROW_NEXTSHOT_MINIMUM("Mobs.Spider.CobThrow.NextShot.Minimum", 500),
	SPIDER_COBTHROW_NEXTSHOT_MAXIMUM("Mobs.Spider.CobThrow.NextShot.Maximum", 6000),
	SPIDER_COBTHROW_MATERIAL("Mobs.Spider.CobThrow.Material", "COBWEB"),
	WITCH_BATSPAWN_PROBABILITY("Mobs.Witch.BatSpawn.Probability", 0.2),
	WITCH_POTIONS_LEVITATION_PROBABILITY("Mobs.Witch.Potions.LEVITATION.Probability", 0.02),
	WITCH_POTIONS_LEVITATION_DURATION("Mobs.Witch.Potions.LEVITATION.Duration", 100),
	WITCH_POTIONS_LEVITATION_LEVEL("Mobs.Witch.Potions.LEVITATION.Level", 2),
	WITCH_POTIONS_REGENERATION_PROBABILITY("Mobs.Witch.Potions.REGENERATION.Probability", 0.03),
	WITCH_POTIONS_REGENERATION_DURATION("Mobs.Witch.Potions.REGENERATION.Duration", 140),
	WITCH_POTIONS_REGENERATION_LEVEL("Mobs.Witch.Potions.REGENERATION.Level", 0),
	WITCH_POTIONS_DAMAGE_RESISTANCE_PROBABILITY("Mobs.Witch.Potions.DAMAGE_RESISTANCE.Probability", 0.025),
	WITCH_POTIONS_DAMAGE_RESISTANCE_DURATION("Mobs.Witch.Potions.DAMAGE_RESISTANCE.Duration", 400),
	WITCH_POTIONS_DAMAGE_RESISTANCE_LEVEL("Mobs.Witch.Potions.DAMAGE_RESISTANCE.Level", 1),
	WITCH_POTIONS_SPEED_PROBABILITY("Mobs.Witch.Potions.SPEED.Probability", 0.028),
	WITCH_POTIONS_SPEED_DURATION("Mobs.Witch.Potions.SPEED.Duration", 200),
	WITCH_POTIONS_SPEED_LEVEL("Mobs.Witch.Potions.SPEED.Level", 2),
	ZOMBIE_SPAWNNEW_PROBABILITY("Mobs.Zombie.SpawnNew.Probability", 0.3),
	ZOMBIE_SPAWNNEW_RADIUS("Mobs.Zombie.SpawnNew.Radius", 5),
	ZOMBIE_SPAWNNEW_BABYPROBABILITY("Mobs.Zombie.SpawnNew.BabyProbability", 0.8),
	ZOMBIE_RESPAWN_PROBABILITY("Mobs.Zombie.Respawn.Probability", 0.2),
	ZOMBIE_RESPAWN_TIME("Mobs.Zombie.Respawn.Time", 2000),
	ZOMBIE_RESPAWN_RESPAWNINGPARTICLES("Mobs.Zombie.Respawn.RespawningParticles", "SMOKE_NORMAL"),
	ZOMBIE_RESPAWN_SOUNDS_RESPAWNING_NAME("Mobs.Zombie.Respawn.Sounds.Respawning.Name", "ENTITY_ZOMBIE_HURT"),
	ZOMBIE_RESPAWN_SOUNDS_RESPAWNING_VOLUME("Mobs.Zombie.Respawn.Sounds.Respawning.Volume", 2),
	ZOMBIE_RESPAWN_SOUNDS_RESPAWNING_PITCH("Mobs.Zombie.Respawn.Sounds.Respawning.Pitch", 0.1),
	ZOMBIE_RESPAWN_SOUNDS_RESPAWNING_RATE("Mobs.Zombie.Respawn.Sounds.Respawning.Rate", 800),
	ZOMBIE_RESPAWN_SOUNDS_RESPAWNED_NAME("Mobs.Zombie.Respawn.Sounds.Respawned.Name", "ENTITY_SKELETON_HURT"),
	ZOMBIE_RESPAWN_SOUNDS_RESPAWNED_VOLUME("Mobs.Zombie.Respawn.Sounds.Respawned.Volume", 2),
	ZOMBIE_RESPAWN_SOUNDS_RESPAWNED_PITCH("Mobs.Zombie.Respawn.Sounds.Respawned.Pitch", 0.1),
	ZOMBIE_INFLICTEFFECTS_WEAKNESS_DURATION("Mobs.Zombie.InflictEffects.WEAKNESS.Duration", 600),
	ZOMBIE_INFLICTEFFECTS_WEAKNESS_LEVEL("Mobs.Zombie.InflictEffects.WEAKNESS.Level", 0),
	ZOMBIE_INFLICTEFFECTS_WEAKNESS_PROBABILITY("Mobs.Zombie.InflictEffects.WEAKNESS.Probability", 0.0192307692307692),
	ZOMBIE_INFLICTEFFECTS_SLOW_DURATION("Mobs.Zombie.InflictEffects.SLOW.Duration", 200),
	ZOMBIE_INFLICTEFFECTS_SLOW_LEVEL("Mobs.Zombie.InflictEffects.SLOW.Level", 1),
	ZOMBIE_INFLICTEFFECTS_SLOW_PROBABILITY("Mobs.Zombie.InflictEffects.SLOW.Probability", "0.0357142857142857?"),
	ZOMBIE_INFLICTEFFECTS_POISON_DURATION("Mobs.Zombie.InflictEffects.POISON.Duration", 300),
	ZOMBIE_INFLICTEFFECTS_POISON_LEVEL("Mobs.Zombie.InflictEffects.POISON.Level", 0),
	ZOMBIE_INFLICTEFFECTS_POISON_PROBABILITY("Mobs.Zombie.InflictEffects.POISON.Probability", 0.0212765957446809),
	ZOMBIE_INFLICTEFFECTS_WITHER_DURATION("Mobs.Zombie.InflictEffects.WITHER.Duration", 100),
	ZOMBIE_INFLICTEFFECTS_WITHER_LEVEL("Mobs.Zombie.InflictEffects.WITHER.Level", 1),
	ZOMBIE_INFLICTEFFECTS_WITHER_PROBABILITY("Mobs.Zombie.InflictEffects.WITHER.Probability", 0.03125),
	ZOMBIE_INFLICTEFFECTS_BLINDNESS_DURATION("Mobs.Zombie.InflictEffects.BLINDNESS.Duration", 400),
	ZOMBIE_INFLICTEFFECTS_BLINDNESS_LEVEL("Mobs.Zombie.InflictEffects.BLINDNESS.Level", 0),
	ZOMBIE_INFLICTEFFECTS_BLINDNESS_PROBABILITY("Mobs.Zombie.InflictEffects.BLINDNESS.Probability", 0.04),
	ZOMBIE_SPAWNITEMS_DSWORD_ICON("Mobs.Zombie.SpawnItems.dsword.Icon", "DIAMOND_SWORD"),
	ZOMBIE_SPAWNITEMS_DSWORD_PROBABILITY("Mobs.Zombie.SpawnItems.dsword.Probability", 0.01),
	ZOMBIE_SPAWNITEMS_WSWORD_ICON("Mobs.Zombie.SpawnItems.wsword.Icon", "WOODEN_SWORD"),
	ZOMBIE_SPAWNITEMS_WSWORD_ENCHANTMENTS("Mobs.Zombie.SpawnItems.wsword.Enchantments", "DAMAGE_ALL:1"),
	ZOMBIE_SPAWNITEMS_WAXE_ICON("Mobs.Zombie.SpawnItems.waxe.Icon", "WOODEN_AXE"),
	ZOMBIE_SPAWNITEMS_WAXE_PROBABILITY("Mobs.Zombie.SpawnItems.waxe.Probability", 0.018),
	ZOMBIE_SPAWNITEMS_ROD_ICON("Mobs.Zombie.SpawnItems.rod.Icon", "FISHING_ROD"),
	ZOMBIE_SPAWNITEMS_ROD_ENCHANTMENTS("Mobs.Zombie.SpawnItems.rod.Enchantments", "VANISHING_CURSE:1"),
	ZOMBIE_SPAWNITEMS_ROD_PROBABILITY("Mobs.Zombie.SpawnItems.rod.Probability", 0.02),
	ZOMBIE_SPAWNITEMS_GOLDNUGGET_ICON("Mobs.Zombie.SpawnItems.goldnugget.Icon", "GOLD_NUGGET"),
	ZOMBIE_SPAWNITEMS_GOLDNUGGET_PROBABILITY("Mobs.Zombie.SpawnItems.goldnugget.Probability", 0.0285),
	ZOMBIE_SPAWNITEMS_STONE_ICON("Mobs.Zombie.SpawnItems.stone.Icon", "STONE"),
	ZOMBIE_SPAWNITEMS_STONE_PROBABILITY("Mobs.Zombie.SpawnItems.stone.Probability", 0.0217),
	ZOMBIE_SPAWNITEMS_IRONSWORD_ICON("Mobs.Zombie.SpawnItems.ironsword.Icon", "IRON_SWORD"),
	ZOMBIE_SPAWNITEMS_IRONSWORD_PROBABILITY("Mobs.Zombie.SpawnItems.ironsword.Probability", 0.0133),
	ZOMBIE_SPAWNITEMS_STONESWORD_ICON("Mobs.Zombie.SpawnItems.stonesword.Icon", "STONE_SWORD"),
	ZOMBIE_SPAWNITEMS_STONESWORD_ENCHANTMENTS("Mobs.Zombie.SpawnItems.stonesword.Enchantments", "DURABILITY:2"),
	ZOMBIE_SPAWNITEMS_STONESWORD_PROBABILITY("Mobs.Zombie.SpawnItems.stonesword.Probability", 0.01923),
	ZOMBIE_SPAWNITEMS_GOLDSHOVEL_ICON("Mobs.Zombie.SpawnItems.goldshovel.Icon", "GOLDEN_SHOVEL"),
	ZOMBIE_SPAWNITEMS_GOLDSHOVEL_ENCHANTMENTS("Mobs.Zombie.SpawnItems.goldshovel.Enchantments", "DIG_SPEED:1"),
	ZOMBIE_SPAWNITEMS_GOLDSHOVEL_PROBABILITY("Mobs.Zombie.SpawnItems.goldshovel.Probability", 0.0138),
	ZOMBIE_SPAWNITEMS_STICK_ICON("Mobs.Zombie.SpawnItems.stick.Icon", "STICK"),
	ZOMBIE_SPAWNITEMS_STICK_PROBABILITY("Mobs.Zombie.SpawnItems.stick.Probability", 0.0277),
	ZOMBIE_SPAWNITEMS_REDSTONE_ICON("Mobs.Zombie.SpawnItems.redstone.Icon", "REDSTONE"),
	ZOMBIE_SPAWNITEMS_REDSTONE_PROBABILITY("Mobs.Zombie.SpawnItems.redstone.Probability", 0.0121),
	ZOMBIE_SPAWNITEMS_DAXE_ICON("Mobs.Zombie.SpawnItems.daxe.Icon", "DIAMOND_AXE"),
	ZOMBIE_SPAWNITEMS_DAXE_ENCHANTMENTS("Mobs.Zombie.SpawnItems.daxe.Enchantments", "DAMAGE_UNDEAD:4"),
	ZOMBIE_SPAWNITEMS_DAXE_PROBABILITY("Mobs.Zombie.SpawnItems.daxe.Probability", 0.0117);

	private String path;
	private Object value;

	CE(String path, Object... values) {
		this.path = path;
		this.value = values.length == 1 ? values[0] : values;
	}

	public String getPath() {
		return path;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public static void updateValues(FileConfiguration file) {
		for (CE e : CE.values()) {
			if (file.contains(e.getPath()))
				e.setValue(file.get(e.getPath()));
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(Class<T> cast) {
		if (cast.equals(Particle.class))
			return (T) Particle.valueOf(getValue(String.class));
		if (cast.equals(Sound.class))
			return (T) Sound.valueOf(getValue(String.class));
		if (cast.equals(Material.class))
			return (T) Material.valueOf(getValue(String.class));
		return cast.cast(this.value);
	}

	public Object getConfigValue(HardMode plugin) {
		return plugin.getConfig().get(this.path);
	}

	public long longValue() {
		return getValue(Number.class).longValue();
	}

	public double doubleValue() {
		return getValue(Number.class).doubleValue();
	}

	public int intValue() {
		return getValue(Number.class).intValue();
	}

}
