/**
 * 
 */
package peersim.solipsis;

import java.io.FileWriter;
import java.io.IOException;

import peersim.core.CommonState;

/**
 * @author xavier
 *
 */
public class CacheStatistics {
	
	
	private int nbCaheMissGlob;
	private int nbCaheHitGlob;
	private int nbCacheMitClob;
	private int nbCachePassClob;
	private int nbNombreMessages;
	private int nbCacheRequest;
	private int nbCacheResponse;
	private int nbEnvelopNotOK;
	private int nbMessSearch;
	private int nbMessDelta;
	private int nbMessFound;
	private int nbMessConnect;
	private int nbMessClose;

	CacheStatistics() {
		this.nbCaheMissGlob = 0;
		this.nbCaheHitGlob = 0;
		this.nbCacheMitClob = 0;
		this.nbCachePassClob = 0;
		this.nbNombreMessages = 0;
		this.nbCacheResponse = 0;
		this.nbCacheRequest = 0;
		this.nbMessSearch = 0;
		this.nbMessDelta = 0;
	}
	
	
	/* 
	 * Fonctions Incrémentation 
	 */
	
	public void incCacheMissGlob(){
		this.nbCaheMissGlob ++;
	}
	
	public void incCacheHitGLob(){
		this.nbCaheHitGlob ++;
	}
	
	public void incCacheMitGLob(){
		this.nbCacheMitClob ++;
	}
	
	public void incnbCachePassClob(){
		this.nbCachePassClob ++;
	}
	

	public void incnbNombreMessages(){
		this.nbNombreMessages ++;
	}
	
	public void incNbCacheRequest(){
		this.nbCacheRequest ++;
	}
	
	public void incnbCacherResponse(){
		this.nbCacheResponse ++;
	}
	
	public void incnbEnvelopNotOK(){
		this.nbEnvelopNotOK ++;
	}
	
	public void incnbMessSearch(){
		this.nbMessSearch ++;
	}
	
	public void incnbMessDelta(){
		this.nbMessDelta ++;
	}
	
	public void incnbMessConnect(){
		this.nbMessConnect ++;
	}
	
	public void incnbMessClose(){
		this.nbMessClose ++;
	}
	
	public void incnbMessFound(){
		this.nbMessFound ++;
	}
	
	
	/*
	 *  Fonctions Affichage Statistiques 
	 */
	
	public void printNbMessages(){
		
		System.out.println("NbMessages: " + this.nbNombreMessages );
	}

	public void printnbEnvelopNotOK(){
		
		System.out.println("nbEnvelopNotOK: " + this.nbEnvelopNotOK );
	}
	
	public void printStatisticsCacheMess() {
		System.out.println("Hit:Mit:Miss:CacReq:CacResp:Time");
		System.out.println(this.nbCaheHitGlob + ":" + this.nbCacheMitClob + ":" + this.nbCaheMissGlob + ":" + this.nbCacheRequest + ":" + this.nbCacheResponse +  ":"+ CommonState.getTime()+ ", ");
		System.out.println("");

	}
	
	public void printStatisticsActiviteMess() {
		System.out.println("CacheReq|Search|Delta|Found|Total|Time");
		System.out.println(this.nbCacheRequest + "|" + this.nbMessSearch + "|" + this.nbMessDelta + "|" + this.nbMessFound + "|" + this.nbNombreMessages +  "|"+ CommonState.getTime()+ ", ");
	}
	
	public String printStatisticsCacheMessString() {
		String res;
		res = "" + this.nbCaheHitGlob + " " + this.nbCacheMitClob + " " + this.nbCaheMissGlob + " " + this.nbCacheRequest + " " + this.nbCacheResponse + " " + CommonState.getTime() + "\n";
		return res;
	}
	
	public String printStatisticsActiviteMessString() {
		String res;
		res = "" + this.nbCacheRequest + " " + this.nbMessSearch + " " + this.nbMessDelta + " " + this.nbMessFound + " " + this.nbNombreMessages + " " + CommonState.getTime() + "\n";
		return res;
	}


	public int getNbCaheMissGlob() {
		return nbCaheMissGlob;
	}


	public void setNbCaheMissGlob(int nbCaheMissGlob) {
		this.nbCaheMissGlob = nbCaheMissGlob;
	}


	public int getNbCaheHitGlob() {
		return nbCaheHitGlob;
	}


	public void setNbCaheHitGlob(int nbCaheHitGlob) {
		this.nbCaheHitGlob = nbCaheHitGlob;
	}


	public void setNbCacheMitClob(int nbCacheMitClob) {
		this.nbCacheMitClob = nbCacheMitClob;
	}


	public int getNbCacheMitClob() {
		return nbCacheMitClob;
	}

	public int getNbCachePassClob() {
		return nbCachePassClob;
	}


	public void setNbCachePassClob(int nbCachePassClob) {
		this.nbCachePassClob = nbCachePassClob;
	}

	
	

}
