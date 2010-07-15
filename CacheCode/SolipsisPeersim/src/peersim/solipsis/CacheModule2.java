/**
 * 
 */
package peersim.solipsis;

import peersim.config.Configuration;

/**
 * @author xavier
 *
 */
public class CacheModule2 {
	
	
	private final static String CACHE_SIZE		= "cacheSize";
	private final static String CACHE_STRATEGIE = "cachestrategie";
	
	private final static int FIFO	=  0;
	private final static int LRU	=  1;
	private final static int OFF	=  2;
	
	
	private int cacheSize;
	private int strategieCache;
	
	private SolipsisProtocol protocol;
	
	private CacheRequest cacheRequest;
	
	
	CacheModule2(SolipsisProtocol protocol){
		this.protocol = protocol;
		this.cacheSize = Configuration.getInt(protocol.getPrefix()+"."+CACHE_SIZE);
		this.strategieCache = Configuration.getInt(protocol.getPrefix()+"."+CACHE_STRATEGIE);
		cacheRequest = null;

		
	}
	
	public void createCacheRequest(int Destination, int ttl, CacheRequest cacheRequest){
		
		
	}

	
	public boolean needToCacheRequest(){	
		return false;
	}
	
	
	
	public void propagateCacheRequest(){
		
		
		
		
	}
	
	
	public void processCacheUpdMsg(Message msg){
		
		
		
		
	}
	
	/* ********************************************************************* */
							/* Getters & Setters */
	/* ********************************************************************* */
	

	public int getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}

	public int getStrategieCache() {
		return strategieCache;
	}

	public void setStrategieCache(int strategieCache) {
		this.strategieCache = strategieCache;
	}

	public SolipsisProtocol getProtocol() {
		return protocol;
	}

	public void setProtocol(SolipsisProtocol protocol) {
		this.protocol = protocol;
	}

	public CacheRequest getCacheRequest() {
		return cacheRequest;
	}

	public void setCacheRequest(CacheRequest cacheRequest) {
		this.cacheRequest = cacheRequest;
	}
	
	
	
}
