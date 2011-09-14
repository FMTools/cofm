package cofm.sim.util;

import java.util.HashMap;
import java.util.Map;

public abstract class CacheManager {

	protected abstract class CacheKey {
		abstract public int hashCode();
		abstract public boolean equals(Object o);
	}
	
	protected Map<CacheKey, Object> cache = new HashMap<CacheKey, Object>();
	
	protected void store(CacheKey key, Object value) {
		cache.put(key, value);
	}
	
	protected Object lookup(CacheKey key) {
		return cache.get(key);
	}
}
