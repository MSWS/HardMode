package xyz.msws.hardmode.world;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.google.common.base.Preconditions;

public class Area {
	private UUID worldID;
	private double x1, y1, z1;
	private double x2, y2, z2;

	public Area(Location start, Location end) {
		Preconditions.checkNotNull(start);
		Preconditions.checkNotNull(end);
		Preconditions.checkArgument(start.getWorld().equals(end.getWorld()));

		this.x1 = Math.min(start.getX(), end.getX());
		this.y1 = Math.min(start.getY(), end.getY());
		this.z1 = Math.min(start.getZ(), end.getZ());
		this.x2 = Math.max(start.getX(), end.getX());
		this.y2 = Math.max(start.getY(), end.getY());
		this.z2 = Math.max(start.getZ(), end.getZ());
	}

	public boolean contains(Location loc) {
		return (loc.getX() >= x1 && loc.getY() >= y1 && loc.getZ() >= z1)
				&& (loc.getX() <= x2 && loc.getY() <= y2 && loc.getZ() <= z2);
	}

	public Location getMinPoint() {
		return new Location(Bukkit.getWorld(worldID), x1, y1, z1);
	}

	public Location getMaxPoint() {
		return new Location(Bukkit.getWorld(worldID), x2, y2, z2);
	}
}
