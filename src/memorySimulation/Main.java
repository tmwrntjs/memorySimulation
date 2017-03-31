package memorySimulation;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * 
 * @author Tim Warntjes, Andrew Dulmes
 * 
 * Main class that reads and does a memory simulation 
 *
 */
public class Main {

	public static boolean quiet;
	public static void main(String[] args) throws FileNotFoundException {
		
		quiet=true;
		ArrayList<Page> pagesArr = new ArrayList<Page>();
		int numFrames= 1;
		int algorithm =0;
		
		
		FileReader file	= new FileReader("gccBig.trace");
		Scanner sc = new Scanner(file);
		try {
		Page thisPage;
		while(sc.hasNext()){
			thisPage = new Page();
			
			thisPage.address= (int)sc.nextLong(16)  >> 12;
			
			if(sc.next().charAt(0) =='R'){
				thisPage.needsWrite = false;
			}else{
				thisPage.needsWrite = true;
			}
			pagesArr.add(thisPage);
		}
		}catch (Exception e){
			System.out.println(pagesArr.size());
		}
		sc.close();
		
		//all data from file is into page table,
		//now iterate over them like a simulation
		
		PageTable pageTable = new PageTable();
		PageFrame pageFrame = new PageFrame(numFrames,algorithm);
		int pageTableIndex;
		for(int i = 0;i< pagesArr.size();i++){ 
			//pageTable
			pageTableIndex = pageTable.add(pagesArr.get(i)); //adds to page table, updates time
			
			
			if(pageFrame.pageFault(pageTable.pages[pageTableIndex])){
				
				pageFrame.addPageToFrame(pageTable.pages[pageTableIndex]);	
			}
			
			if (!Main.quiet) {
				System.out.println();
			}
			
			
			
		}
		
		System.out.println("Number of Frames: " +numFrames);
		System.out.println("Number of Writes: " +PageTable.numWrites);
		System.out.println("Number of Reads: " +PageTable.numReads);
		System.out.println("Number of traces:" +pagesArr.size());
		System.out.println("Not Page Fault successes: "+PageFrame.notPageFault );
		
		
		
		
		
	}//end main

}//end class
