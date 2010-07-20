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
	private final static int FIFO	=  0;
	

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
			boor.setTime(CommonState.getIntTime());
//			System.out.println("Ajout du nœud " + boor.getId() + " dans le cache réussi");
//			System.out.println(boor.toString());
			
		}else{
//			System.out.println("Problème d'ajout d'un nœud dans le cache: " + boor.getId() + " déjà présent.");
//			System.out.println(boor.toString());
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
//			System.out.println("Le nœud " + boor.getId() + " a été supprimé");
		}else{
//			System.out.println("Le nœud " + boor.getId() + " n'est pas dans la liste, alors il va être difficile de le supprimer");

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
//				System.out.println("FIFO: Le noeud choisi est " + suppr.getId());
			break;
		}			
				
		return suppr;
	}
	
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
		
		return best;
	
	}
	
	public NeighborProxy searchCacheNeighborEnvelop(long[] destination){
		NeighborProxy current;
		NeighborProxy best = null;
		NeighborProxy other = null;
		
		double currentDist,bestDist = -1;
		
		
//		HashMap<Integer, NeighborProxy> cach = this.cache;
		Iterator it;
		it = this.cache.entrySet().iterator();

				
		while(it.hasNext()){
			current = (NeighborProxy)((Map.Entry)it.next()).getValue();
			currentDist = VirtualWorld.simpleDistance(destination, current.getCoord());
//			System.out.println(this.protocol.helpfulToEnvelopeCache(current));
			if (this.protocol.helpfulToEnvelopeCache(current)){
				best = current;
				return best;
			}
			if ( this.protocol.constructingEnvelope(current)){
				other = current;
				
			}
		}
		if (other != null){
			System.out.println("OTHER PAS NULL");
		}
		
		return other;
	
	}
	
	
	public NeighborProxy searchCacheNeighborEnvelopEv(long[] destination){
		NeighborProxy current;
		NeighborProxy best = null;
		NeighborProxy other = null;
		long bestTime = -1;
		double currentDist,bestDist = -1;
		
		
//		HashMap<Integer, NeighborProxy> cach = this.cache;
		Iterator it;
		it = this.cache.entrySet().iterator();

				
		while(it.hasNext()){
			current = (NeighborProxy)((Map.Entry)it.next()).getValue();
			currentDist = VirtualWorld.simpleDistance(destination, current.getCoord());
//			System.out.println(this.protocol.helpfulToEnvelopeCache(current));
			if (this.protocol.helpfulToEnvelopeCache(current) && current.getTime() > bestTime){
				best = current;
				bestTime = current.getTime();
//				return best;
			}
			if ( this.protocol.constructingEnvelope(current)){
				other = current;
				
			}
		}
		if (other != null){
			System.out.println("OTHER PAS NULL");
			return other;
		}
		
		return best;
	
	}
	
	
	
	public NeighborProxy searchCacheNeighborLimitNeighbor(long[] destination,HashMap<Integer, NeighborProxy> voisin){
		NeighborProxy current;
		NeighborProxy best = null;
		
		double currentDist,bestDist = -1;
		double limite = -1;
		
//		HashMap<Integer, NeighborProxy> cach = this.cache;
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
		
//		System.out.println("Limite = " + limite);
		this.setLimite((int)limite);
		
		while(it.hasNext()){
			current = (NeighborProxy)((Map.Entry)it.next()).getValue();
			currentDist = VirtualWorld.simpleDistance(destination, current.getCoord());

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
		
		return best;
	
	}
	
	public NeighborProxy searchCacheNeighbor(long[] destination,HashMap<Integer, NeighborProxy> voisin,int limite){
		NeighborProxy current;
		NeighborProxy best = null;
		NeighborProxy farNeigh;
		
		double farNeighDist,currentDist,bestDist = 0;
		
		
//		HashMap<Integer, NeighborProxy> cach = this.cache;
		Iterator it,it_voisin;
		it = this.cache.entrySet().iterator();
		it_voisin = voisin.entrySet().iterator();
		
		
		farNeigh = (NeighborProxy)((Map.Entry)it_voisin.next()).getValue();
//		System.out.println("destination: " + destination + ", farneigh: " + farNeigh.getCoord());
		farNeighDist = VirtualWorld.simpleDistance(destination, farNeigh.getCoord());
		
		while(it_voisin.hasNext()){
			current = (NeighborProxy)((Map.Entry)it_voisin.next()).getValue();
			currentDist = VirtualWorld.simpleDistance(destination, current.getCoord());
			if( farNeighDist < currentDist){
				farNeigh = current;
				farNeighDist = currentDist;
//				System.out.println("Changement de farNeigh, distance = " + farNeighDist);
			}			
		}
//		System.out.println("Le voisin le plus loin est :" + farNeigh.getId() + ", distance = " + farNeighDist);
		
		
		
		while(it.hasNext()){
			current = (NeighborProxy)((Map.Entry)it.next()).getValue();
			currentDist = VirtualWorld.simpleDistance(destination, current.getCoord());
			if( farNeighDist > currentDist){
				best = current;
				bestDist = currentDist;		
//				System.out.println("On a un plus proche, le nœud: " + best.getId() + ", distance = " + bestDist);
			}
			
			//((SolipsisProtocol)n.getProtocol(me.getPeersimNodeId())).getVirtualEntity();
		}
		if(best != null){
//			System.out.println("On a trouvé un meilleur dans le cache ,le nœud: " + best.getId() + ", distance = " + bestDist);
		}else{
//			System.out.println("Pas de meilleur dans le cache");
		}
		
		if (bestDist > limite){
			best = null;
//			System.out.println("trop éloigné");
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
