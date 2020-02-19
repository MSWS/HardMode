package xyz.msws.hardmobs.data;

public interface Saveable extends CData {
	boolean saveData();

	boolean loadData();
}
