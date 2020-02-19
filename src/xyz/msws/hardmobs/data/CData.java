package xyz.msws.hardmobs.data;

import java.util.Map;

public interface CData {

//	boolean loadData();

//	boolean saveData();

	boolean hasData(String key);

	Map<String, Object> getData();

	default <T> T getData(Class<T> cast, String key, T def) {
		if (hasData(key))
			return getData(cast, key);
		return def;
	}

	default <T> T getData(Class<T> cast, String key) {
		return cast.cast(getData(key));
	}

	Object getData(String key);

	boolean setData(String key, Object value);

	boolean clearData();

	default void removeData(String key) {
		setData(key, null);
	}
}
