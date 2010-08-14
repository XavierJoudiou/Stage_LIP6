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
		
		
//		BufferedReader  in = new BufferedReader(new FileReader("test.txt" )); 
//		CacheFichierTraitements traitement = new CacheFichierTraitements();
//		traitement.TwoToOneLine(in);
		
		double current = 340;
		double dist = 20;
		boolean  result = isNears(current, 320, dist);
		System.out.println("Result: " + result + ", current: " + current);

	}
	public boolean isNears(double a,double b,double dist){
		double Aa = a + dist;
		if (Aa >= 360){
			Aa = (a + dist) % 360;
			if (Aa < b ){
				System.out.println("1.1: Aa: " + Aa + ", b: " + b);
				return false;
			}
		}else{
			if (Aa < b){
				System.out.println("1.2: Aa: " + Aa + ", b: " + b);
				return false;
			}
		}
		
		double Ab =  a - dist;
		if (Ab < 0){
			Ab = (a + dist) % 360;
			if (Ab > b){
				System.out.println("2.1: Ab: " + Ab + ", b: " + b);
				return false;
			}
		}else{
			if (Ab < b){
				System.out.println("2.2: Ab: " + Ab + ", b: " + b);
				return false;
			}
		}
		System.out.println("final: Aa:" + Aa + ", b:" + b + ", Ab: " + Ab);
		return true;
	}
	
	
}
