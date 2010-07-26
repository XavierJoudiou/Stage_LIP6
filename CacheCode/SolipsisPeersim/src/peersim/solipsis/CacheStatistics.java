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
	private int nbMessCacheHelpResp;
	private int nbCacheHitHelpGLob;
	private int nbCacheMissHelpGlob;
	private int nbMessSearchHelp;
	private int nbMessUpdateTot;

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
		this.nbMessFound = 0;
		this.nbMessConnect = 0;
		this.nbMessClose = 0;
		this.nbMessCacheHelpResp = 0;
		this.nbCacheHitHelpGLob = 0;
		this.nbCacheMissHelpGlob = 0;
		this.nbMessSearchHelp = 0;
		this.nbMessUpdateTot = 0;
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
	
	public void incnbCacheResponse(){
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
	
	public void incnbMessCacheHelpResp(){
		this.nbMessCacheHelpResp ++;
	}
	public void incCacheHitHelpGLob() {
		this.nbCacheHitHelpGLob ++;		
	}

	public void incCacheMissHelpGlob() {
		this.nbCacheMissHelpGlob ++;		
	}

	public void incnbMessSearchHelp() {
		this.nbMessSearchHelp ++;			
	}
	public void incnbMessUpdateTot() {
		this.nbMessUpdateTot ++;
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
		System.out.println("Hit:Mit:Miss:HitHelp:MissHelp:Time");
		System.out.println(this.nbCaheHitGlob + ":" + this.nbCacheMitClob + ":" + this.nbCaheMissGlob + ":" + this.nbCacheHitHelpGLob + ":" + this.nbCacheMissHelpGlob + ":"+ CommonState.getTime()+ ", ");
		System.out.println("");

	}
	
	public void printStatisticsActiviteMess() {
		System.out.println("CacheReq|Search|CacheHelp|Update|Delta|Found|Total|Time");
		System.out.println(this.nbCacheRequest + "|" + this.nbMessSearch + "|" + this.nbMessSearchHelp + "|" + this.nbMessUpdateTot + "|" + this.nbMessDelta + "|" + this.nbMessFound + "|" + this.nbNombreMessages +  "|"+ CommonState.getTime()+ ", ");
	}
	
	public String printStatisticsCacheMessString() {
		String res;
		res = "" + this.nbCaheHitGlob + " " + this.nbCacheMitClob + " " + this.nbCaheMissGlob + " " + this.nbCacheHitHelpGLob + " " + this.nbCacheMissHelpGlob + " " + CommonState.getTime() + "\n";
		return res;
	}
	
	public String printStatisticsActiviteMessString() {
		String res;
		res = "" + this.nbCacheRequest + " " + this.nbMessSearch + " " + this.nbMessSearchHelp + " " + this.nbMessUpdateTot + " " + this.nbMessDelta + " " + this.nbMessFound + " " + this.nbNombreMessages + " " + CommonState.getTime() + "\n";
		return res;
	}

	public String printStatisticsCacheMessFinalString() {
		String res;
		res = "Hit:Mit:Miss:HitHelp:MissHelp:Time\n";
		res += this.nbCaheHitGlob + ":" + this.nbCacheMitClob + ":" + this.nbCaheMissGlob + ":" + this.nbCacheHitHelpGLob + ":" + this.nbCacheMissHelpGlob + ":" + CommonState.getTime() + "\n"; 
		res += "\n";
		return res;
	}
	
	public String printStatisticsActiviteMessFinalString() {
		String res;

		res = "CacheReq|Search|CacheHelp|Update|Delta|Found|Total|Time\n";
		res += this.nbCacheRequest + "|" + this.nbMessSearch + "|" + this.nbMessSearchHelp + "|" + this.nbMessUpdateTot + "|" + this.nbMessDelta + "|" + this.nbMessFound + "|" + this.nbNombreMessages +  "|" + CommonState.getTime() + "\n";
		res += "\n";
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
