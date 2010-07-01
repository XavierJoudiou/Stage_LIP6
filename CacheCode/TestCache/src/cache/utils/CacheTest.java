/**
 * 
 */
package cache.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;



/**
 * @author xavier
 *
 */
public class CacheTest {
	
	/*Différentes Stratégies pour la gestion du cache*/
	private final static int FIFO 	= 0;
	private final static int LRU	= 1;
	private final static int MFU	= 2;
	
	private  HashMap<Integer,Node> cache;
	private int cacheSize;
	private int strategie;
	
	
	public CacheTest(HashMap<Integer, Node> cache,int strategie) {
		super();
		this.cache = new HashMap<Integer,Node>();
		this.strategie = strategie;
		this.cacheSize = 5;
	}
	
	
	public synchronized void AddNode(Node node){
		if (cache.size() >= this.cacheSize){
			RmNode(SelectDeletNode());
		}
		OrganizeCache();
		if (node.getCacheInfo() < 0){
			this.cache.put(node.getId(), node);
			node.setCachePos(0);
			node.setCacheInfo(0);
			System.out.println("Ajout du nœud " + node.getId() + " dans le cache");
		}else{
			System.out.println("Probleme lors de l'ajout du nœud dans le cache");
			System.out.println(node.toString());
		}
	}
	
	public void RmNode(Node node){
		this.cache.remove(node.getId());
		System.out.println("Suppression du nœud " + node.getId());
		Iterator it;
		Node current;
		it = this.cache.entrySet().iterator();
		while(it.hasNext()){
			current = (Node)((Map.Entry)it.next()).getValue();
			if (current.getCachePos() > node.getCachePos()){
				current.DecPos();
			}
		}
	}
	
	public void ShowCache(){
		Iterator it;
		it = this.cache.entrySet().iterator();
		while(it.hasNext()){
			System.out.println("id: " + it.next().toString());
		}
	}
	
	public void OrganizeCache(){
		Iterator it;
		Node current;
		it = this.cache.entrySet().iterator();
		switch(strategie){
		case FIFO:
				while(it.hasNext()){
					current = (Node)((Map.Entry)it.next()).getValue();
					current.IncPos();
				}
			
			break;
		case LRU:
				while(it.hasNext()){
					current = (Node)((Map.Entry)it.next()).getValue();
					current.IncPos();
				}
		
			break;
		case MFU:
				while(it.hasNext()){
					current = (Node)((Map.Entry)it.next()).getValue();
					current.IncPos();
				}
			break;
		}
		
		
	}
	
	public Node SelectDeletNode(){
		Node nodeDel = null;
		Iterator it;
		Node current;
		it = this.cache.entrySet().iterator();
		switch(strategie){
		case FIFO:
				nodeDel = (Node)((Map.Entry)it.next()).getValue();	
				while(it.hasNext()){
					current = (Node)((Map.Entry)it.next()).getValue();
					
					if ( nodeDel.getCachePos() < current.getCachePos()){
						nodeDel = current;
					}
				}
				System.out.println("FIFO: Le noeud choisi est " + nodeDel.getId());
			break;
		case LRU:
				nodeDel = (Node)((Map.Entry)it.next()).getValue();	
				while(it.hasNext()){
					current = (Node)((Map.Entry)it.next()).getValue();
					
					if ( nodeDel.getCachePos() < current.getCachePos()){
						nodeDel = current;
					}
				}
				System.out.println("LRU: Le noeud choisi est " + nodeDel.getId());
			break;
		case MFU:
				nodeDel = (Node)((Map.Entry)it.next()).getValue();	
				while(it.hasNext()){
					current = (Node)((Map.Entry)it.next()).getValue();
					if (nodeDel.getCacheInfo() > current.getCacheInfo()){
						nodeDel = current;
						System.out.println("New NodeDel");
					}else{
						if (nodeDel.getCacheInfo() == current.getCacheInfo()){
							if (nodeDel.getCachePos() < current.getCachePos()){ 
								nodeDel = current;
							}
						}
					}
				}
			System.out.println("MFU: Le noeud choisi est " + nodeDel.getId());
			break;
		}
		
		return nodeDel;
	}
	
	public void consultNode(Node node,int nb){
		node.IncInfo(nb);
		Iterator it;
		Node current;
		it = this.cache.entrySet().iterator();
		switch(strategie){
		case FIFO:
				System.out.println("Consultation du nœud " + node.getId());
			break;
		case LRU:
			while(it.hasNext()){
				current = (Node)((Map.Entry)it.next()).getValue();
				if (current.getCachePos() < node.getCachePos()){
					current.IncPos();
				}
			}
			node.setCachePos(0);
			System.out.println("Consultation du nœud " + node.getId());
			break;
		case MFU:
				while(it.hasNext()){
					current = (Node)((Map.Entry)it.next()).getValue();
					if (current != node){
						current.DecInfo();
					}
				}
				System.out.println("Consultation du nœud " + node.getId());
			break;
		}
	}
	
	/* Setters & Getters */
	
	public HashMap<Integer, Node> getCache() {
		return cache;
	}

	public void setCache(HashMap<Integer, Node> cache) {
		this.cache = cache;
	}
	
	public int getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}



}
