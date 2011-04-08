package collab.fm.mining.constraint;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.bean.persist.entity.Value;
import collab.fm.server.persistence.HibernateUtil;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class SVM {

	public static final String TRAINING_FILE = "mining/cons_svm_train";
	public static final String MODEL_FILE = "mining/cons_svm_model";
	public static final String TEST_FILE = "mining/cons_svm_test";
	
	/**
	 * Generate training file for LIBSVM. 
	 * @param model The feature model to be analyzed.
	 * @throws IOException 
	 */
	public void dumpTrainingFile(Model model) throws IOException {
		BufferedWriter tf = new BufferedWriter(new FileWriter(SVM.TRAINING_FILE));
		
		Entity[] features = model.getEntities().toArray(new Entity[0]);
		int numPair = 0;
		List<Float> simPairs = new ArrayList<Float>(); 
		for (int i = 0; i < features.length; i++) {
			for (int j = i+1; j < features.length; j++) {
				FeaturePair pair = new FeaturePair(features[i], features[j]);
				tf.write(formatPair(pair) + "\n");
				if (pair.getSimilarity() > 0.0f) {
					System.out.println(formatPair(pair));
					simPairs.add(pair.getSimilarity());
				}		
				numPair++;
			}
		}
		tf.close();
		System.out.println(features.length + " features, " + numPair + " pairs, " + simPairs.size() + " pairs of similar feaures.");
		Float[] sims = simPairs.toArray(new Float[0]);
		Arrays.sort(sims);
		System.out.println("Sorted similarity:");
		for (int i = 0; i < sims.length;) {
			int j = i;
			for (; j < sims.length && sims[i].equals(sims[j]); j++) {
				// empty loop body
			}
			System.out.print(sims[i]);
			if (j - i > 1) {
				System.out.print("  (" + (j-i) + ")");
			}
			System.out.println();
			i = j;
		}
	}
	
	private String formatPair(FeaturePair pair) {
		return pair.getLabel() + 
			   " 1:" + pair.getSimilarity() +
			   " 2:" + pair.getParental() +
			   " 3:" + pair.getSibling() + 
			   " 4:" + pair.getNumMandatory() + 
			   " 5:" + pair.getRequireOut() + 
			   " 6:" + pair.getExcludeOut();
	}
	
	public static void main(String[] argv) throws IOException {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();;
		try {
			session.beginTransaction();
			
			Model model = DaoUtil.getModelDao().getByName("Media Player");
			if (model == null) {
				System.err.println("No such model");
			} else {
				SVM svm = new SVM();
				svm.dumpTrainingFile(model);
			}
			
			session.getTransaction().commit();
		} catch (HibernateException he) {
			session.getTransaction().rollback();
			he.printStackTrace();
			session.close();
		} catch (ItemPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			session.close();
		} catch (StaleDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			session.close();
		} 
			
	}
}
