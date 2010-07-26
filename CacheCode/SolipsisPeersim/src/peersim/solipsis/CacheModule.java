/**
 * 
 */
package peersim.solipsis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import peersim.core.CommonState;


/**
 * @author xavier
 *
 */
public class CacheModule {
	
	/* pour le cache */
	private final static String CACHE_SIZE		= "cacheSize";
	private final static String CACHE_STRATEGIE = "cachestrategie";
	private final static int OFF 	=  0;
	private final static int FIFO	=  1;
	private final static int LRU	=  2;
	private final static int FIFOMULT = 3;
	

	/* Variables pour la mise en place du cache */ 
	private HashMap<Integer,NeighborProxy> cache;
	private HashMap<Integer,CacheData> cacheInfo;
	private int cacheSize;
	private int strategieCache;
	/* mettre dans fichier de conf */
	private int limit;
	private SolipsisProtocol protocol;

	

	public CacheModule(HashMap<Integer, NeighborProxy> cache, HashMap<Integer, CacheData> cacheInfo, int cacheSize,
			int strategieCache,SolipsisProtocol protocol) {
		super();
		this.protocol = protocol;

		/* Initialisation des variables pour le cache*/
		this.cacheSize = cacheSize;
		this.strategieCache = strategieCache;
		this.cache = new HashMap<Integer,NeighborProxy>();
		this.cacheInfo = new HashMap<Integer, CacheData>();
	}
	

	/* ********************************************************************* */
	/* Fonctions pour le cache */
	/* ********************************************************************* */
	
	/*
	 * Ajout d'un nœud dans le cache
	 */
	public void AddCache(NeighborProxy boor){
		Iterator it,info;
		NeighborProxy current;
		it = this.cache.entrySet().iterator();
		info = this.cacheInfo.entrySet().iterator();
		CacheData cacheData;
			
		/* On vérifie que le nœud n'est pas déjà dans le cache */
		if (!IsInCache(boor)){
			
			/* Le cache est plein, suppression d'un élément*/
			if (this.cache.size() >= this.cacheSize){
				RmCache(SelectNode());
			}
						
			IncPosAll();
			/* Ajout du nœud au cache à la première place */
			this.cache.put(boor.getId(), boor);
			cacheData = new CacheData(1, 0);
			this.cacheInfo.put(boor.getId(), cacheData);
			boor.setTime(CommonState.getIntTime());;
			
		}		
	}
	
	/*
	 * Supprime le voisin "boor" 
	 */
	public void IncPosAll(){
		Iterator info;
		CacheData current;
		info = this.cacheInfo.entrySet().iterator();
		while(info.hasNext()){
			current = (CacheData)((Map.Entry)info.next()).getValue();
			current.setPosition(current.getPosition() + 1);
		}
		
	}

	/*
	 * Supprime le voisin "boor" 
	 */
	public void RmCache(NeighborProxy boor){
		
		Iterator info;
		CacheData currentInfo;
		info = this.cacheInfo.entrySet().iterator();
		if (IsInCache(boor)){
			while(info.hasNext()){
				currentInfo = (CacheData)((Map.Entry)info.next()).getValue();
				if (currentInfo.getPosition() > this.cacheInfo.get(boor.getId()).getPosition()){
					currentInfo.setPosition(currentInfo.getPosition() -1);
				}
			}
			this.cache.remove(boor.getId());
			this.cacheInfo.remove(boor.getId());
		}
	}
	
	/*
	 * Sélectionne le nœud à supprimer en fonction de la statégie 
	 */
	public NeighborProxy SelectNode(){
		NeighborProxy suppr = null;
		CacheData supprInfo;
		Iterator it,info;
		NeighborProxy current;
		CacheData currentInfo;
		it = this.cache.entrySet().iterator();
		info = this.cacheInfo.entrySet().iterator();
		
		switch(strategieCache){
		case FIFO:
			suppr = (NeighborProxy)((Map.Entry)it.next()).getValue();
			supprInfo = (CacheData)((Map.Entry)info.next()).getValue();
				while(it.hasNext()){
					current = (NeighborProxy)((Map.Entry)it.next()).getValue();
					currentInfo = (CacheData)((Map.Entry)info.next()).getValue();
					if ( supprInfo.getPosition() < currentInfo.getPosition() ){
						suppr = current;
						supprInfo = currentInfo;
					}
				}
			break;
		case FIFOMULT:
			suppr = (NeighborProxy)((Map.Entry)it.next()).getValue();
			supprInfo = (CacheData)((Map.Entry)info.next()).getValue();
				while(it.hasNext()){
					current = (NeighborProxy)((Map.Entry)it.next()).getValue();
					currentInfo = (CacheData)((Map.Entry)info.next()).getValue();
					if ( supprInfo.getPosition() < currentInfo.getPosition() ){
						suppr = current;
						supprInfo = currentInfo;
					}
				}
			break;
		}			
				
		return suppr;
	}
	
	/*
	 * Fonction IsInCache:
	 *  renvoie un booleen en fonction de la présence
	 *  ou non du nœud passé en argument dans le cache
	 * 
	 */
	public boolean IsInCache(NeighborProxy boor){
		boolean res = false;
		Iterator it;
		NeighborProxy current;
		it = this.cache.entrySet().iterator();
		while(it.hasNext()){
			current = (NeighborProxy)((Map.Entry)it.next()).getValue();
			if (current.getId() == boor.getId()){
				res = true;
				return true;
			}
		}
		return res;
	}
	
	/*
	 * Affiche le contenu du cache et les informations correspondantes
	 * 
	 */
	public void ShowCache(){
		Iterator it,info;
		it = this.cache.entrySet().iterator();
		info = this.cacheInfo.entrySet().iterator();
		
		System.out.println("ShowCache: ");
		System.out.println("  -------");
		
		while(it.hasNext()){
			System.out.println("  voisin: " + it.next().toString());
			System.out.println("  info  : " + info.next().toString());
			System.out.println("  -------");
		}
	}
	
	/*
	 * Fonction NeighborProxyFindNearest: 
	 *  recherche le nœud le plus proche du cache du 
	 *  nœud passé en argument
	 *  
	 */
	public  NeighborProxy NeighborProxyFindNearest(HashMap<Integer, NeighborProxy> kach, NeighborProxy me){
		NeighborProxy current, nearest;
		long[] currentCoord,nearestCoord;;
		double currentDist, nearestDist;
		Iterator it;
		it = this.cache.entrySet().iterator();
		nearest = (NeighborProxy)((Map.Entry)it.next()).getValue();
		nearestCoord = nearest.getCoord();
		nearestDist = VirtualWorld.simpleDistance(me.getCoord(),nearestCoord);
		current = (NeighborProxy)((Map.Entry)it.next()).getValue();
		
		while(it.hasNext()){
			
			currentDist = VirtualWorld.simpleDistance(me.getCoord(),current.getCoord());
			if (current.getId() != me.getId()){
				if (nearestDist > currentDist){
					nearest = current;
					nearestCoord = nearest.getCoord();
					nearestDist = currentDist;
				}
			}
			current = (NeighborProxy)((Map.Entry)it.next()).getValue();
		}
		
		return nearest;
	}
	
	/*
	 * Fonction searchCacheNeighborLimit:
	 *  Recherche dans le cache le nœud le plus proche de
	 *  la destination passé en argument et qui est inférieur 
	 *  à la limite passé en argument
	 * 
	 */
	public NeighborProxy searchCacheNeighborLimit(long[] destination,int limite){
		NeighborProxy current;
		NeighborProxy best = null;
		
		double currentDist,bestDist = -1;
			
		Iterator it;
		it = this.cache.entrySet().iterator();
		this.setLimite((int)limite);
				
		while(it.hasNext()){
			current = (NeighborProxy)((Map.Entry)it.next()).getValue();
			currentDist = VirtualWorld.simpleDistance(destination, current.getCoord());
			if (current.getQuality() !=  NeighborProxy.CACHED){
				if (currentDist < limite){
					if ( bestDist == -1){
						bestDist = currentDist;
						best = current;
					}else{
						if(currentDist < bestDist){
							best = current;
							bestDist = currentDist;
						}
					}
				}
			}
		}
		return best;
	}
	
	/*
	 * Fonction searchCacheHelpNeighbor:
	 *  Recherche dans le cache si un nœud peut aider à 
	 *  reconstruire l'enveloppe ou si il est dans sa zone 
	 *  de connaissance. Ce fait après la reception d'un message
	 *  SEARCH_HELP, regarde en fonction des arguments du émetteur
	 *  du message
	 * 
	 */
	public NeighborProxy searchCacheHelpNeighbor(CacheHelpRequest request,int id){
		NeighborProxy current;
		NeighborProxy best = null;
		long bestTime = -1;
		double currentDist = -1;
		
		Iterator it;
		it = this.cache.entrySet().iterator();
				
		while(it.hasNext()){
			current = (NeighborProxy)((Map.Entry)it.next()).getValue();
			currentDist = VirtualWorld.simpleDistance(request.getDestination(), current.getCoord());
			if ( currentDist < request.getKnowledgeRay() ){
				return current;
			}
			if (this.protocol.helpfulToEnvelopeCacheHelpNeighbor(current,request) && current.getTime() > bestTime){
				if (current.getId() != id){
				best = current;
				bestTime = current.getTime();

				}else{
					System.out.println("C le meme ==========");
				}
			}
		}
		return best;
	
	}
	
	/*
	 * Fonction searchCacheNeighborEnvelopEv:
	 *  recherche dans le cache si un nœud peut aider à 
	 *  reconstruire l'enveloppe du nœud local.
	 */
	public NeighborProxy searchCacheNeighborEnvelopEv(long[] coord){
		NeighborProxy current;
		NeighborProxy best = null;
		NeighborProxy other = null;
		long bestTime = -1;
		double currentDist,bestDist = -1;
		
		Iterator it;
		it = this.cache.entrySet().iterator();
				
		while(it.hasNext()){
			current = (NeighborProxy)((Map.Entry)it.next()).getValue();
			currentDist = VirtualWorld.simpleDistance(coord, current.getCoord());
			if (current.getQuality() !=  NeighborProxy.CACHED){
				if (this.protocol.helpfulToEnvelopeCache(current) && current.getTime() > bestTime){
					best = current;
					bestTime = current.getTime();
					//System.out.println("$$$ help: " + this.protocol.helpfulToEnvelopeCache(current) + ", myId: " + this.protocol.getVirtualEntity().getId() + ", idcur: " + current.getId()+ ", time: " + CommonState.getTime());
					//System.out.println("$$$ help_ coords: me= " + destination[0] + ", " + destination[1] + " et cur= " + current.getCoord()[0] + ", " + current.getCoord()[1]);
				}
			}
		}
		return best;
	}
	
	/*
	 * Fonction searchCacheNeighborEnvelopEv:
	 *  recherche dans le cache si un nœud peut aider à 
	 *  reconstruire l'enveloppe du nœud local ou si il 
	 *  est dans sa zone de connaissance.
	 */
	public NeighborProxy searchCacheNeighborEnvelopEv(long[] coord, double knowledgeRay){
		NeighborProxy current;
		NeighborProxy best = null;
		NeighborProxy other = null;
		long bestTime = -1;
		double currentDist,bestDist = -1;
		
		Iterator it;
		it = this.cache.entrySet().iterator();
				
		while(it.hasNext()){
			current = (NeighborProxy)((Map.Entry)it.next()).getValue();
			currentDist = VirtualWorld.simpleDistance(coord, current.getCoord());
//			if (current.getQuality() !=  NeighborProxy.CACHED){
				if ( currentDist < (knowledgeRay - 1000) ){
//				if ( this.protocol.isInsideKnowledgeZone(current)){
					return current;
				}
//				if (this.protocol.helpfulToEnvelopeCache(current) && current.getTime() > bestTime){
//					best = current;
//					bestTime = current.getTime();
//					//System.out.println("$$$ help: " + this.protocol.helpfulToEnvelopeCache(current) + ", myId: " + this.protocol.getVirtualEntity().getId() + ", idcur: " + current.getId()+ ", time: " + CommonState.getTime());
//					//System.out.println("$$$ help_ coords: me= " + destination[0] + ", " + destination[1] + " et cur= " + current.getCoord()[0] + ", " + current.getCoord()[1]);
//				}
//			}
		}
		return best;
	}
	
	/*
	 * Fonction searchCacheNeighborEnvelopEvMult:
	 *  recherche dans le cache si un ou plusieurs nœud(s) 
	 *  peu(t/vent) aider à reconstruire l'enveloppe du nœud 
	 *  local ou si il est dans sa zone de connaissance.
	 */
	public HashMap<Integer,NeighborProxy>  searchCacheNeighborEnvelopEvMult(long[] destination, double knowledgeRay){
		NeighborProxy current;
		HashMap<Integer,NeighborProxy> best = new HashMap<Integer, NeighborProxy>();
		NeighborProxy other = null;
		long bestTime = -1;
		double currentDist,bestDist = -1;
		
		Iterator it,it2;
		it = this.cache.entrySet().iterator();
		it2 = this.cache.entrySet().iterator();

				
		while(it.hasNext()){
			current = (NeighborProxy)((Map.Entry)it.next()).getValue();
			currentDist = VirtualWorld.simpleDistance(destination, current.getCoord());
			if (current.getQuality() !=  NeighborProxy.CACHED){
				if ( currentDist < knowledgeRay ){
					best.put(current.getId(), current);
				}
				if (best.size() > 4){
					return best;
				}
			}
		}
		
		while(it2.hasNext()){
			current = (NeighborProxy)((Map.Entry)it2.next()).getValue();
			currentDist = VirtualWorld.simpleDistance(destination, current.getCoord());
			if (current.getQuality() !=  NeighborProxy.CACHED){
				if (best.size() > 4){
					return best;
				}
				if (this.protocol.helpfulToEnvelopeCache(current)){
					best.put(current.getId(), current);
				}
			}
		}
		return best;
	}
	
	/*
	 * Fonction searchCacheHelpNeighborMult:
	 *  Recherche dans le cache si un ou plusieurs nœud(s) peu(t/vent)
	 *  aider à reconstruire l'enveloppe ou si il est dans sa zone 
	 *  de connaissance. Ce fait après la reception d'un message
	 *  SEARCH_HELP, regarde en fonction des arguments du émetteur
	 *  du message
	 * 
	 */
	public HashMap<Integer,NeighborProxy>  searchCacheHelpNeighborMult(CacheHelpRequest request,int id){
		NeighborProxy current;
		HashMap<Integer,NeighborProxy> best = new HashMap<Integer, NeighborProxy>();
		long bestTime = -1;
		double currentDist = -1;
		
		Iterator it,it2;
		it = this.cache.entrySet().iterator();
		it2 = this.cache.entrySet().iterator();
				
		while(it.hasNext()){
			current = (NeighborProxy)((Map.Entry)it.next()).getValue();
			currentDist = VirtualWorld.simpleDistance(request.getDestination(), current.getCoord());
			if ( currentDist < request.getKnowledgeRay() ){
				best.put(current.getId(), current);
			}
			if (best.size() > 4){
				return best;
			}
		}
		
		while(it2.hasNext()){
			current = (NeighborProxy)((Map.Entry)it2.next()).getValue();
			currentDist = VirtualWorld.simpleDistance(request.getDestination(), current.getCoord());			
			if (this.protocol.helpfulToEnvelopeCacheHelpNeighbor(current,request) && current.getTime() > bestTime){
				if (best.size() > 4){
					return best;
				}
				if (current.getId() != id){
					best.put(current.getId(), current);

				}else{
					System.out.println("C le meme ==========");
				}

			}
		}
		return best;
	}
	
	
	
	/*
	 * Fonction  searchCacheNeighborLimitNeighbor:
	 * recherche dans le cache le plus proche des nœud de la 
	 * destination passée en argument et qui est plus proche 
	 * que le plus loin des voisins
	 */
	public NeighborProxy searchCacheNeighborLimitNeighbor(long[] destination,HashMap<Integer, NeighborProxy> voisin){
		NeighborProxy current;
		NeighborProxy best = null;
		
		double currentDist,bestDist = -1;
		double limite = -1;
		
		Iterator it,it_voisin;
		it = this.cache.entrySet().iterator();
		it_voisin = voisin.entrySet().iterator();	
				
		while(it_voisin.hasNext()){
			current = (NeighborProxy)((Map.Entry)it_voisin.next()).getValue();
			currentDist = VirtualWorld.simpleDistance(destination, current.getCoord());

			if ( limite == -1){
				limite = currentDist;
			}else{
				if(currentDist < limite){
					bestDist = limite;
				}
			}	
		}		
		this.setLimite((int)limite);
		
		while(it.hasNext()){
			current = (NeighborProxy)((Map.Entry)it.next()).getValue();
			currentDist = VirtualWorld.simpleDistance(destination, current.getCoord());
			if (current.getQuality() !=  NeighborProxy.CACHED){
				if (currentDist < limite){
					if ( bestDist == -1){
						bestDist = currentDist;
						best = current;
					}else{
						if(currentDist < bestDist){
							best = current;
							bestDist = currentDist;
						}
					}
				}
			}
		}
		return best;
	}
	
	/*
	 * Fonction  searchCacheNeighbor:
	 * recherche dans le cache le plus proche des nœud de la 
	 * destination passée en argument et qui est plus proche 
	 * que le plus loin des voisins
	 */
	public NeighborProxy searchCacheNeighbor(long[] destination,HashMap<Integer, NeighborProxy> voisin,int limite){
		NeighborProxy current;
		NeighborProxy best = null;
		NeighborProxy farNeigh;
		
		double farNeighDist,currentDist,bestDist = 0;
		
		Iterator it,it_voisin;
		it = this.cache.entrySet().iterator();
		it_voisin = voisin.entrySet().iterator();
				
		farNeigh = (NeighborProxy)((Map.Entry)it_voisin.next()).getValue();
		farNeighDist = VirtualWorld.simpleDistance(destination, farNeigh.getCoord());
		
		while(it_voisin.hasNext()){
			current = (NeighborProxy)((Map.Entry)it_voisin.next()).getValue();
			currentDist = VirtualWorld.simpleDistance(destination, current.getCoord());
			
			if( farNeighDist < currentDist){
				farNeigh = current;
				farNeighDist = currentDist;
			}			
		}
		
		while(it.hasNext()){
			current = (NeighborProxy)((Map.Entry)it.next()).getValue();
			currentDist = VirtualWorld.simpleDistance(destination, current.getCoord());
			if (current.getQuality() !=  NeighborProxy.CACHED){

				if( farNeighDist > currentDist){
					best = current;
					bestDist = currentDist;		
				}
			}
		}
		if (bestDist > limite){
			best = null;
		}
		return best;
	}
	
	
	/* ********************************************************************* */
							/* Getters & Setters */
	/* ********************************************************************* */
	
	public HashMap<Integer, NeighborProxy> getCache() {
		return cache;
	}


	public void setCache(HashMap<Integer, NeighborProxy> cache) {
		this.cache = cache;
	}


	public HashMap<Integer, CacheData> getCacheInfo() {
		return cacheInfo;
	}


	public void setCacheInfo(HashMap<Integer, CacheData> cacheInfo) {
		this.cacheInfo = cacheInfo;
	}


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


	public int getLimite() {
		return limit;
	}


	public void setLimite(int limite) {
		this.limit = limite;
	}
	
}
