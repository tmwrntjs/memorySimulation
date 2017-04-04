package memorySimulation;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

/**
 * 
 * @author Tim Warntjes, Andrew Dulmes
 * 
 *         Main class that reads and does a memory simulation
 * 
 */
public class Main {

	public static boolean quiet;
	public static Page[] pagesArr;

	public static void main(String[] args) throws FileNotFoundException {

		long startTime = System.nanoTime();    
		
		//process command line arguments
		int numFrames = Integer.parseInt(args[0]);
		int algorithm =0;
		if(args[1].equals("random"))
			algorithm=0;
		else if(args[1].equals("lru"))
			algorithm = 1;
		else if(args[1].equals("clockpage"))
			algorithm = 2;
		else if(args[1].equals("ideal"))
			algorithm = 3;
		
		quiet = true;
		if(args[2].equals("debug"))
			quiet=false;
			
		
		
		String fileLocation = args[3];
		System.out.println(fileLocation);

		FileReader file = new FileReader(fileLocation);
		Scanner lineReader = new Scanner(file);
		int numLines = 0;
		// using ArrayList gives a size, but when reading gccBig.trace, even
		// increase heap size doesn't help
		// to initialize array, get size (could also resize array as you go)
		while (lineReader.hasNextLine()) {
			lineReader.nextLine();
			numLines++;
		}
		lineReader.close();

		pagesArr = new Page[numLines]; // made public for the ideal algorithm to
										// access
		file = new FileReader(fileLocation);
		Scanner sc = new Scanner(file);

		Page thisPage = new Page();
		int countLine = 0;
		while (sc.hasNext()) {
			thisPage = new Page();

			thisPage.address = (int) sc.nextLong(16) >> 12;

			char RW = sc.next().charAt(0);
			if (RW == 'R') {
				thisPage.needsWrite = false;
			} else if (RW == 'W') {
				thisPage.needsWrite = true;
			} else
			{
				System.out.println("ERROR: R/W not read in proplery");
			}

			pagesArr[countLine] = thisPage;
			countLine++;
		}

		sc.close();
		if (!quiet) {
			System.out.println("Done reading in file");
		}
		// all data from file is into page table,
		// now iterate over them like a simulation

		PageTable pageTable = new PageTable();
		PageFrame pageFrame = new PageFrame(numFrames, algorithm);
		int pageTableIndex;
		for (int i = 0; i < pagesArr.length; i++) {
			// pageTable
			
			pageTableIndex = pageTable.add(pagesArr[i]); // adds to page table,
															// updates time, returns index in page table

			if (pageFrame.pageFault(pageTable.pages[pageTableIndex])) {
				pageFrame.addPageToFrame(pageTable.pages[pageTableIndex]);
			}

			if (!Main.quiet) {
				System.out.println();
			}

		}

		if (algorithm == 0) {
			System.out.println("Random algorithm");
		} else if (algorithm == 1) {
			System.out.println("LRU algorithm");
		} else if (algorithm == 2) {
			System.out.println("Clockpage algorithm");
		} else if (algorithm == 3) {
			System.out.println("Ideal algorithm");
		}

		System.out.println("Number of Frames: " + numFrames);
		System.out.println("Number of Writes: " + PageTable.numWrites);
		System.out.println("Number of Reads: " + PageTable.numReads);
		System.out.println("Number of traces:" + pagesArr.length);


		long estimatedTime = System.nanoTime() - startTime;
		System.out.println(estimatedTime/1000000 + " milliseconds");
		
		
		
	}// end main

}// end class
