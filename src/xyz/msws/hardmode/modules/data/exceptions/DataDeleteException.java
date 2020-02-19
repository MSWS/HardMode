package xyz.msws.hardmode.modules.data.exceptions;

/**
 * Thrown when something goes wrong while trying to dete data from the database
 * in {@link xyz.msws.hardmode.modules.data.DataManager}
 * 
 * @apiNote I feel like this might be a bit redundant since we can just check
 *          for nulls, but it is the proper way to do it
 */
public class DataDeleteException extends Exception {
	/**
	 * Creates a new {@link DataDeleteException}
	 * 
	 * @param message the reason why the exception is thrown
	 */
	public DataDeleteException(String message) {
		super(message);
	}
}
