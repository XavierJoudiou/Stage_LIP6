package Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import Cache.CacheFichierTraitements;

import junit.framework.TestCase;

public class testCacheFichierTraitements extends TestCase {

	
	public void testTwoToOneLine() throws IOException{
		
		
		BufferedReader  in = new BufferedReader(new FileReader("test.txt" )); 
		CacheFichierTraitements traitement = new CacheFichierTraitements();
		traitement.TwoToOneLine(in);

	}
	
	
	
}
