package xyz.msws.hardmode.data;

import xyz.msws.hardmode.modules.data.annotations.DataPrimaryKey;

public class YMLSQL {
	@DataPrimaryKey
	private String key;

	private Object data;

	public YMLSQL() {

	}

	public YMLSQL(String key, Object data) {
		this.key = key;
		this.data = data;
	}

	public String getKey() {
		return key;
	}

	public Object getData() {
		return data;
	}
}
