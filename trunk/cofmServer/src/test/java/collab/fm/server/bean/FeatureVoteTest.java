package collab.fm.server.bean;

import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.*;

import net.sf.ezmorph.*;
import net.sf.ezmorph.bean.BeanMorpher;
import net.sf.json.*;
import net.sf.json.util.JSONUtils;

import org.apache.log4j.Logger;
import org.junit.Test;

import collab.fm.server.bean.entity.Feature;
import collab.fm.server.util.BeanUtil;

public class FeatureVoteTest {
	
	static Logger logger = Logger.getLogger(FeatureVoteTest.class);
	
	@Test
	public void testFeatureToJson() {
		Feature feat = new Feature();
		feat.setId(1L);
		
		feat.voteExistence(true, 1L);
		feat.voteExistence(false, 2L);
		
		feat.voteName("rootFeat", true, 1L);
		feat.voteName("root", true, 2L);
		
		feat.voteOptionality(false, 1L);
		feat.voteOptionality(true, 3L);
		
		feat.voteDescription("DDDDD", true, 3L);
		try {
		logger.info(BeanUtil.beanToJson(feat));
		} catch (Exception e) {
			assertTrue(false);
		}
	}
	
	public static class Bean1<T> {
		protected T value;
		protected Set<Integer> support = new TreeSet<Integer>(); // List of User ID
		protected Set<Integer> against = new TreeSet<Integer>(); // List of User ID
		public T getValue() {
			return value;
		}
		public void setValue(T value) {
			this.value = value;
		}
		public Set<Integer> getSupport() {
			return support;
		}
		public void setSupport(Set<Integer> support) {
			this.support.clear();
			this.support.addAll(support);
		}
		public Set<Integer> getAgainst() {
			return against;
		}
		public void setAgainst(Set<Integer> against) {
			this.against.clear();
			this.support.addAll(against);
		}
		
		@Override
		public String toString() {
			//int s1 = support.size(), s2 = against.size();
			return value.toString() + "(" + support.toString() + "/" + against.toString() + ")"; 
		}
		
	}
	
	public static class Bean2 {
		private List<Bean1<Integer>> children = new ArrayList<Bean1<Integer>>();

		public List<Bean1<Integer>> getChildren() {
			return children;
		}

		public void setChildren(List<Bean1<Integer>> children) {
			this.children = children;
		}
		
	}
	
	@Test
	public void testJsonToFeature() {
		// json == result of testFeatureToJson()
		//String json = "{'children':[{'against':[],'support':[1],'value':2},{'against':[],'support':[2],'value':3}],'descriptions':[{'against':[],'support':[3],'value':'DDDDD'}],'exclude':[{'against':[],'support':[4],'value':5}],'existence':{'against':[2],'support':[1],'value':true},'id':1,'mandatory':{'against':[1],'support':[3],'value':true},'names':[{'against':[2],'support':[1],'value':'rootFeat'},{'against':[1],'support':[2],'value':'root'}],'require':[{'against':[],'support':[1],'value':3}]}";
		String json = "[{'children': [{'against':[],'support':[1],'value':2},{'against':[],'support':[2],'value':3}]}]";
		JSON jsonObject = JSONArray.fromObject(json);
		Map<String, Class> clsMap = new HashMap<String, Class>();
		clsMap.put("children", Bean1.class);
		JsonConfig cfg = new JsonConfig();
		cfg.setClassMap(clsMap);
		cfg.setRootClass(Bean2.class);
		//Feature feat = (Feature) JSONSerializer.toJava(jsonObject, cfg);
		//List out = (List)JSONSerializer.toJava(jsonObject, cfg);
		List beanList = (List)JSONSerializer.toJava(jsonObject, cfg);
		
		MorpherRegistry reg = JSONUtils.getMorpherRegistry();
		reg.registerMorpher(new BeanMorpher(Bean2.class, reg));
		reg.registerMorpher(new BeanMorpher(Bean1.class, reg));
		
		Bean2 bean = (Bean2)reg.morph(Bean2.class, beanList.get(0));
		
		List list = new ArrayList();
		for (Object o: bean.getChildren()) {
			list.add(reg.morph(Bean1.class, o));
		}
		bean.setChildren(list);
		List<Bean1<Integer>> chd = bean.getChildren();
		logger.info(Arrays.toString(bean.getChildren().toArray(new Bean1[0])));
		logger.info(chd.get(0).getAgainst().getClass()); // == TreeSet, not HashSet
	}
	
}
