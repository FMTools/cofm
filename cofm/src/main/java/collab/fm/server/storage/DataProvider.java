package collab.fm.server.storage;

import java.util.List;

import collab.fm.server.bean.*;

public interface DataProvider {
	
	/**
	 * 
	 * @param id The id of feature
	 * @return null if the feature doesn't exist
	 */
	Feature getFeatureById(Integer id);
	

	/**
	 * If the feature name doesn't exist, a new feature will be created and returned.
	 * @param name The name, or one of the name candidates of feature
	 * @return feature's auto-generated ID.
	 */
	Integer getFeatureIdByName(String name);
	/**
	 * 
	 * @param f An existed feature
	 * @return false if the feature doesn't exist or an error has occurred.
	 */
	boolean updateFeature(Feature f);
	
	/**
	 * 
	 * @param f An existed feature
	 * @return false if the feature doesn't exist or an error has occurred.
	 */
	//boolean removeFeature(Feature f);
	
	/**
	 * 
	 * @param op The operation
	 * @return the operation whose id field has been set by an auto-generated id.
	 */
	Operation commitOperation(Operation op);
	
	/**
	 * Get features where their IDs are equal to or greater than beginId.
	 * @param beginId
	 * @return null if no such features can be found.
	 */
	List<Feature> getRecentFeatures(Integer beginId);
	
	/**
	 * Get operations where their IDs are equal to or greater than beginId
	 * @param beginId
	 * @return null if no such operations can be found.
	 */
	List<Operation> getRecentOperations(Integer beginId);
	
	/**
	 * 
	 * @param username
	 * @return the user's auto-generated id, null if no such user.
	 */
	Integer getUserIdByName(String username);
	
	/**
	 * 
	 * @param user
	 * @return the user with auto-generated id.
	 */
	User addUser(User user);
}
