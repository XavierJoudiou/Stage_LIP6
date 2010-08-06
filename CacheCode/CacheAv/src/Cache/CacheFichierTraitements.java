/**
 * 
 */
package Cache;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author xavier
 *
 */
public class CacheFichierTraitements {

	
	
	public CacheFichierTraitements() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CacheStatistiquesStruct TwoToOneLine(BufferedReader in) throws IOException{
		
		
		String line; 
		String[] temp = null; 
		int index=0; 
		Double[] res = new Double[40];
		String[] inter = new String[40];
		int compteurLine = 0 ;
		int nbElem = 0;
		CacheStatistiquesStruct resultatStruct = new CacheStatistiquesStruct();
		
		while ((line=in.readLine()) != null){  
		       	temp=line.split(" "); 
		       	if (compteurLine == 0){
		       		nbElem = temp.length;
		       	}
		       	for (int i=0; i<temp.length; i++){ 
//		            System.out.println(temp[i] + " +++"); 
		            inter[index++] = temp[i];
		       	} 
		       	compteurLine ++;
//		       	System.out.println("nouvelle ligne");
		}  
		System.out.println(compteurLine);
		if (compteurLine == 2 && temp.length == nbElem && nbElem == 5) {
		
	//		for(i=0;i<index;i++){
	//			System.out.println(inter[i] + "---");
	//		}

			resultatStruct.setViewCoherence( (Double.parseDouble(inter[0]) + Double.parseDouble(inter[0 + nbElem])) /2 );
			resultatStruct.setTopoCoherence( (Double.parseDouble(inter[1]) + Double.parseDouble(inter[1 + nbElem])) /2 );
			resultatStruct.setMsgCount( (Integer.parseInt(inter[2]) + Integer.parseInt(inter[2 + nbElem])) /2 );
			resultatStruct.setAheadCounter( (Double.parseDouble(inter[3]) + Double.parseDouble(inter[3 + nbElem])) /2 );
			resultatStruct.setConnecDuration( (Double.parseDouble(inter[4]) + Double.parseDouble(inter[4 + nbElem])) /2 );
						
			System.out.println(resultatStruct.toString());

		}else{
			System.out.println("Formatage du fichier en Erreur!!!");
		}
		
		in.close(); 
		return resultatStruct;
	}
	
	
	
}
