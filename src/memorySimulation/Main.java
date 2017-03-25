package memorySimulation;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	public static boolean quiet;
	public static void main(String[] args) throws FileNotFoundException {
		
		quiet=false;
		ArrayList<Page> pagesArr = new ArrayList<Page>();
		
				
		
		FileReader file	= new FileReader("gcc.trace");
		Scanner sc = new Scanner(file);
		
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
		sc.close();
		//all data from file is into page table,
		//now iterate over them like a simulation
		
		PageTable pageTable = new PageTable();
		PageFrame pageFrame = new PageFrame(5,0);
		int pageTableIndex;
		for(int i = 0;i< 100;i++){ //pagesArr.size()
			//pageTable
			pageTableIndex = pageTable.add(pagesArr.get(i)); //adds to page table, updates time
			
			
			if(pageFrame.pageFault(pageTable.pages[pageTableIndex])){
				
				pageFrame.addPageToFrame(pageTable.pages[pageTableIndex]);	
			}
			
			if (!Main.quiet) {
				System.out.println();
			}
			
			
			
		}
		
		
		
		
		
		
		
		
	}//end main

}//end class
