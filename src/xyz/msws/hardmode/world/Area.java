package xyz.msws.hardmode.world;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.google.common.base.Preconditions;

public class Area {
	private UUID worldID;
	private double x1, y1, z1;
	private double x2, y2, z2;

	public Area(Location start, Location end) {
		Preconditions.checkNotNull(start);
		Preconditions.checkNotNull(end);
		Preconditions.checkArgument(start.getWorld().equals(end.getWorld()));

		this.worldID = start.getWorld().getUID();
		this.x1 = Math.min(start.getX(), end.getX());
		this.y1 = Math.min(start.getY(), end.getY());
		this.z1 = Math.min(start.getZ(), end.getZ());
		this.x2 = Math.max(start.getX(), end.getX());
		this.y2 = Math.max(start.getY(), end.getY());
		this.z2 = Math.max(start.getZ(), end.getZ());
	}

	public double getMinX() {
		return x1;
	}

	public double getMinY() {
		return y1;
	}

	public double getMinZ() {
		return z1;
	}

	public double getMaxX() {
		return x2;
	}

	public double getMaxY() {
		return y2;
	}

	public double getMaxZ() {
		return z2;
	}

	public boolean contains(Location loc) {
		return (loc.getX() >= x1 && loc.getY() >= y1 && loc.getZ() >= z1)
				&& (loc.getX() <= x2 && loc.getY() <= y2 && loc.getZ() <= z2);
	}

	public List<Entity> getEntities() {
		World world = Bukkit.getWorld(worldID);
		return world.getEntities().stream().filter(e -> contains(e.getLocation())).collect(Collectors.toList());
	}

	public <T extends Entity> List<T> getEntities(Class<T> c) {
		return getEntities().stream().filter(e -> c.isAssignableFrom(e.getClass())).map(e -> c.cast(e))
				.collect(Collectors.toList());
	}

	public Location getMinPoint() {
		return new Location(Bukkit.getWorld(worldID), x1, y1, z1);
	}

	public Location getMaxPoint() {
		return new Location(Bukkit.getWorld(worldID), x2, y2, z2);
	}

	public Location getMiddle() {
		return new Location(Bukkit.getWorld(worldID), (x1 + x2) / 2, (y1 + y2) / 2, (z1 + z2) / 2);
	}

	public List<Location> getFaces(double step) {
		List<Location> result = new ArrayList<Location>();
		World world = Bukkit.getWorld(worldID);
		for (double x = x1; x <= x2; x += step) {
			for (double z = z1; z <= z2; z += step) {
				Location l = new Location(world, x, y1, z);
				result.add(l);
				l = new Location(world, x, y2, z);
				result.add(l);
			}

			for (double y = y1; y <= y2; y += step) {
				Location l = new Location(world, x, y, z1);
				result.add(l);
				l = new Location(world, x, y, z2);
				result.add(l);
			}
		}

		for (double y = y1; y <= y2; y += step) {
			for (double z = z1; z <= z2; z += step) {
				Location l = new Location(world, x1, y, z);
				result.add(l);
				l = new Location(world, x2, y, z);
				result.add(l);
			}
		}

		return result;
	}

	public List<Location> getEdges(double step) {
		List<Location> result = new ArrayList<Location>();
		World world = Bukkit.getWorld(worldID);
		for (double x = x1; x <= x2; x += step) {
			Location l = new Location(world, x, y2, z2);
			result.add(l);
			l = new Location(world, x, y1, z2);
			result.add(l);
			l = new Location(world, x, y2, z1);
			result.add(l);
			l = new Location(world, x, y1, z1);
			result.add(l);
		}

		for (double y = y1; y <= y2; y += .2) {
			Location l = new Location(world, x1, y, z2);
			result.add(l);
			l = new Location(world, x2, y, z2);
			result.add(l);
			l = new Location(world, x2, y, z1);
			result.add(l);
			l = new Location(world, x1, y, z1);
			result.add(l);
		}

		for (double z = z1; z <= z2; z += .2) {
			Location l = new Location(world, x2, y2, z);
			result.add(l);
			l = new Location(world, x2, y1, z);
			result.add(l);
			l = new Location(world, x1, y1, z);
			result.add(l);
			l = new Location(world, x1, y2, z);
			result.add(l);
		}
		return result;
	}
}
