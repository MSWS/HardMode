package xyz.msws.hardmobs.modules.data.exceptions;

import xyz.msws.hardmobs.modules.data.annotations.DataPrimaryKey;

/**
 * This exception is thrown when the class you're trying to use as database
 * structure doesn't have a field with the {@link DataPrimaryKey} annotation!
 */
public class DataPrimaryKeyException extends Exception {
	public DataPrimaryKeyException(String message) {
		super(message);
	}

}
