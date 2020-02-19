package xyz.msws.hardmode.data;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.scheduler.BukkitRunnable;

import xyz.msws.hardmode.HardMobs;
import xyz.msws.hardmode.modules.data.ConnectionManager;
import xyz.msws.hardmode.modules.data.DataManager;
import xyz.msws.hardmode.modules.data.SQLSelector;
import xyz.msws.hardmode.modules.data.annotations.DataPrimaryKey;
import xyz.msws.hardmode.modules.data.exceptions.DataObtainException;
import xyz.msws.hardmode.modules.data.exceptions.DataPrimaryKeyException;
import xyz.msws.hardmode.modules.data.exceptions.NoDefaultConstructorException;
import xyz.msws.hardmode.utils.MSG;

public class CSQL implements Saveable {

	private Map<String, Object> data;

	private Class<?> obj;

	private String table, idField;
	private HardMobs plugin;

	public CSQL(String table, Class<?> obj, HardMobs plugin) {
		this.table = table;
		this.plugin = plugin;
		this.obj = obj;
		this.data = new HashMap<String, Object>();

		for (Field f : obj.getDeclaredFields()) {
			if (f.isAnnotationPresent(DataPrimaryKey.class)) {
				idField = f.getName();
				break;
			}
		}
		if (idField == null) {
			MSG.error("Unable to get primary key for " + obj);
			return;
		}

		loadData();
	}

	@Override
	public boolean loadData() {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					plugin.getModule(DataManager.class).createTable(table, obj);
				} catch (NoDefaultConstructorException | DataPrimaryKeyException e) {
					e.printStackTrace();
				}

				try {
					for (Object obj : plugin.getModule(DataManager.class).getAllObjects(table)) {
						String id = null;

						for (Field f : obj.getClass().getDeclaredFields()) {
							if (f.isAnnotationPresent(DataPrimaryKey.class)) {
								f.setAccessible(true);
								id = f.get(obj).toString();
								break;
							}
						}

						if (id == null) {
							MSG.error("Unable to get primary key for " + obj);
							continue;
						}

						data.put(id, obj.getClass().cast(obj));
					}
				} catch (DataObtainException | IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(plugin);
		return true;
	}

	public boolean saveDataAsync() {
		new BukkitRunnable() {
			@Override
			public void run() {
				saveData();
			}
		}.runTaskAsynchronously(plugin);
		return true;
	}

	@Override
	@Deprecated
	/**
	 * Use saveDataAsync
	 * 
	 */
	public boolean saveData() {
		for (Object o : data.values()) {
			plugin.getModule(DataManager.class).saveObjectAsync(table, o);
		}
		return true;
	}

	@Override
	public boolean hasData(String key) {
		return data.containsKey(key);
	}

	@Override
	public Object getData(String key) {
		return data.get(key);
	}

	@Override
	public boolean setData(String key, Object value) {
		if (value == null) {
			plugin.getModule(DataManager.class).deleteObjectAsync(table, new SQLSelector(idField, key));
			return true;
		}

		if (value.getClass() != obj)
			return false;

		try {
			data.put(key, obj.cast(value));
		} catch (ClassCastException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean clearData() {
		PreparedStatement del = plugin.getModule(ConnectionManager.class).prepareStatement("DELETE FROM ?");
		try {
			del.setString(1, table);
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						del.execute();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}.runTaskAsynchronously(plugin);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		data.clear();
		return true;
	}

	@Override
	public Map<String, Object> getData() {
		return data;
	}

}
