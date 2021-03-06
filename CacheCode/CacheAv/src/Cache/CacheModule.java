/**
 * 
 */
package Cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;




/**
 * @author Xavier Joudiou
 *
 */
public class CacheModule {
	
	
	private final static int FIFO	=  0;

	
	/* Variables pour la mise en place du cache */ 
	private HashMap<Integer,NeighborProxy> cache;
	private HashMap<Integer,CacheData> cacheInfo;
	private int cacheSize;
	private int strategieCache;
	
	
	public CacheModule(HashMap<Integer, NeighborProxy> cache,HashMap<Integer, Integer> cacheInfo, int cacheSize) {
		super();
		this.cacheSize = 5;
		this.cache = new HashMap<Integer,NeighborProxy>();
		this.cacheInfo = new HashMap<Integer, CacheData>();
		this.strategieCache = 0;
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
			System.out.println("Ajout du nœud " + boor.getId() + " dans le cache réussi");
			System.out.println(boor.toString());
			
		}else{
			System.out.println("Problème d'ajout d'un nœud dans le cache: " + boor.getId() + " déjà présent.");
			System.out.println(boor.toString());
		}
	}
	
	/*
	 * Incrémente les positions des nœuds 
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
			System.out.println("Le nœud " + boor.getId() + " a été supprimé");
		}else{
			System.out.println("Le nœud " + boor.getId() + " n'est pas dans la liste, alors il va être difficile de le supprimer");

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
				System.out.println("FIFO: Le noeud choisi est " + suppr.getId());
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
		nearestDist = Outils.simpleDistance(me.getCoord(),nearestCoord);
		current = (NeighborProxy)((Map.Entry)it.next()).getValue();
		
		while(it.hasNext()){
			
			currentDist = Outils.simpleDistance(me.getCoord(),current.getCoord());
			if (current.getId() != me.getId()){
				if (nearestDist > currentDist){
					nearest = current;
					nearestCoord = nearest.getCoord();
					nearestDist = currentDist;
				}
			}
			current = (NeighborProxy)((Map.Entry)it.next()).getValue();
		}
		
		System.out.println("Le nœud le plus proche est: " + nearest.getId() + ", avec une distance de " + nearestDist);
		return nearest;
	}
	
	public NeighborProxy searchCacheNeighborLimit(long[] destination){
		NeighborProxy current;
		NeighborProxy best = null;
		
		double currentDist,bestDist = -1;
		int limit = 3;
		
//		HashMap<Integer, NeighborProxy> cach = this.cache;
		Iterator it;
		it = this.cache.entrySet().iterator();
				
		while(it.hasNext()){
			current = (NeighborProxy)((Map.Entry)it.next()).getValue();
			currentDist = Outils.simpleDistance(destination, current.getCoord());
			if ( bestDist == -1){
				bestDist = currentDist;
			}
			System.out.println("Current Dist: " + currentDist);
			if (currentDist < limit && currentDist < bestDist){
				best = current;
				bestDist = currentDist;
			}
		}
		System.out.println("le Minimum est: " +  bestDist );
		
		return best;
	
	}
	
	
	
	//long[] origin,
	public NeighborProxy searchCacheNeighbor(long[] destination,HashMap<Integer, NeighborProxy> voisin){
		NeighborProxy current;
		NeighborProxy best = null;
		NeighborProxy farNeigh;
		
		double farNeighDist,currentDist,bestDist;
		
		
//		HashMap<Integer, NeighborProxy> cach = this.cache;
		Iterator it,it_voisin;
		it = this.cache.entrySet().iterator();
		it_voisin = voisin.entrySet().iterator();
		
		
		farNeigh = (NeighborProxy)((Map.Entry)it_voisin.next()).getValue();
		farNeighDist = Outils.simpleDistance(destination, farNeigh.getCoord());
		
		while(it_voisin.hasNext()){
			current = (NeighborProxy)((Map.Entry)it_voisin.next()).getValue();
			currentDist = Outils.simpleDistance(destination, current.getCoord());
			if( farNeighDist < currentDist){
				farNeigh = current;
				farNeighDist = currentDist;
				System.out.println("Changement de farNeigh, distance = " + farNeighDist);
			}			
		}
		System.out.println("Le voisin le plus loin est :" + farNeigh.getId() + ", distance = " + farNeighDist);
		
		
		
		while(it.hasNext()){
			current = (NeighborProxy)((Map.Entry)it.next()).getValue();
			currentDist = Outils.simpleDistance(destination, current.getCoord());
			if( farNeighDist > currentDist){
				best = current;
				bestDist = currentDist;		
				System.out.println("On a un plus proche, le nœud: " + best.getId() + ", distance = " + bestDist);
			}
			
			//((SolipsisProtocol)n.getProtocol(me.getPeersimNodeId())).getVirtualEntity();
		}
		if(best != null){
			System.out.println("On a trouvé un meilleur dans le cache ");
		}else{
			System.out.println("Pas de meilleur dans le cache");
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
	
}
