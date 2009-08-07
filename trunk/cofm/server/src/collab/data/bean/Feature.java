package collab.data.bean;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Feature {
	
	private Integer id;
	private Votable<Boolean> existence = new Votable<Boolean>(Boolean.valueOf(true));
	private Votable<Boolean> mandatory = new Votable<Boolean>(Boolean.valueOf(true));
	private ConcurrentLinkedQueue<Votable<String>> names = new ConcurrentLinkedQueue<Votable<String>>();
	private ConcurrentLinkedQueue<Votable<String>> descriptions = new ConcurrentLinkedQueue<Votable<String>>();
	private ConcurrentLinkedQueue<Votable<Integer>> require = new ConcurrentLinkedQueue<Votable<Integer>>();
	private ConcurrentLinkedQueue<Votable<Integer>> exclude = new ConcurrentLinkedQueue<Votable<Integer>>();
	private ConcurrentLinkedQueue<Votable<Integer>> children = new ConcurrentLinkedQueue<Votable<Integer>>();
	
	public Feature() {
	
	}

	public Integer getId() {
		return id;
	}

	private void setId(Integer id) { // for Hibernate
		this.id = id;
	}
}
