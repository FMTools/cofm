package collab.data.bean;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Feature extends Votable<Boolean> {
	
	private Long id;
	private Votable<Boolean> mandatory = new Votable<Boolean>(Boolean.valueOf(true));
	private ConcurrentLinkedQueue<Votable<String>> names = new ConcurrentLinkedQueue<Votable<String>>();
	private ConcurrentLinkedQueue<Votable<String>> descriptions = new ConcurrentLinkedQueue<Votable<String>>();
	private ConcurrentLinkedQueue<Votable<Long>> requiring = new ConcurrentLinkedQueue<Votable<Long>>();
	private ConcurrentLinkedQueue<Votable<Long>> excluding = new ConcurrentLinkedQueue<Votable<Long>>();
	private ConcurrentLinkedQueue<Votable<Long>> children = new ConcurrentLinkedQueue<Votable<Long>>();
	
	public Feature() {
		super(Boolean.valueOf(true));
	}

	public Long getId() {
		return id;
	}

	private void setId(Long id) { // for Hibernate
		this.id = id;
	}
}
