package collab.storage;

import java.util.List;

import collab.data.bean.*;

public interface DataProvider {
	
	/**
	 * 
	 * @param id The id of feature
	 * @return null if the feature doesn't exist
	 */
	Feature getFeatureById(Long id);
	
	/**
	 * If the feature name doesn't exist, a new feature will be created and returned.
	 * @param name The name, or one of the name candidates of feature
	 * @return Feature
	 */
	Feature getFeatureByName(String name);
	
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
	boolean removeFeature(Feature f);
	
	/**
	 * 
	 * @param op The operation
	 * @return the operation whose id field has been set by an auto-generated id.
	 */
	Operation addOperation(Operation op);
	
	/**
	 * Get features where their IDs are greater than beginId.
	 * @param beginId
	 * @return null if no such features can be found.
	 */
	List<Feature> getRecentFeatures(Long beginId);
	
	/**
	 * Get operations where their IDs are greater than beginId
	 * @param beginId
	 * @return null if no such operations can be found.
	 */
	List<Operation> getRecentOperations(Long beginId);
	
	/**
	 * 
	 * @param username
	 * @return the user's auto-generated id, null if no such user.
	 */
	Integer getUserIdByName(String username);
}
