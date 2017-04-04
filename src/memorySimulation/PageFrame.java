package memorySimulation;

import java.util.Arrays;
import java.util.Random;

/**
 * 
 * Class that simulates the Page Frames
 * 
 * @author Tim Warntjes, Andy Dulmes
 * 
 */
public class PageFrame {

	private int numFrames;
	private Page[] pagesInFrame;

	private int algorithmType;
	private int CPIteratorIndex = 0;
	private int[] accessBit;
	private int[] callDistance;
	private int progressThroughProblem = 0;
	private int pagesFilled = 0;

	/**
	 * Constructor for Page Frames
	 * 
	 * @param numFrames
	 * @param algorithmType
	 */
	public PageFrame(int numFrames, int algorithmType) {
		this.numFrames = numFrames;
		pagesInFrame = new Page[numFrames];
		for (int i = 0; i < numFrames; i++) {
			pagesInFrame[i] = new Page();
		}

		for (int i = 0; i < numFrames; i++) {
			pagesInFrame[i] = new Page();
		}

		// create access Bits at 0 to start if clock page is needed
		if (algorithmType == 2) {
			accessBit = new int[numFrames];
			for (int i = 0; i < numFrames; i++) {
				accessBit[i] = 0;
			}
		}

		// create access Bits at 0 to start if clock page is needed
		if (algorithmType == 3) {
			callDistance = new int[numFrames];
			for (int i = 0; i < numFrames; i++) {
				callDistance[i] = -1;
			}
		}

		this.algorithmType = algorithmType;
	}

	/**
	 * returns true if it is a page fault, increments Numreads return false if
	 * it not a page fault
	 * 
	 * @param page
	 * @return
	 */
	public boolean pageFault(Page page) {

		if (algorithmType == 3) {
			progressThroughProblem++;
		}

		for (int i = 0; i < pagesInFrame.length; i++) { // check all frames for
														// this page
			if (pagesInFrame[i].address == page.address) {
				if (!Main.quiet) {
					System.out.println("(Not a page fault)");
				}
				// if using clock page algorithm, you update the access bit to
				// show it was recently used
				if (algorithmType == 2) {
					accessBit[i] = 1;
				}
				if (algorithmType == 3) {
					callDistance[i] = findNextOfThisPage(pagesInFrame[i]);
					if (!Main.quiet) {
						System.out
								.println("Page updated: new next call distance is "
										+ callDistance[i]);
					}

				}

				return false; // page was found, not a page fault
			}
		}

		// if not a page fault, needs to be read
		PageTable.numReads++;
		if (!Main.quiet) {
			System.out.println("PAGE FAULT");
		}
		return true;
	}

	/**
	 * if needs to load frame, loads it according to the proper algorithm writes
	 * if dirty and disposing of frame
	 * 
	 * @param pages
	 */
	public void addPageToFrame(Page page) {
		if (algorithmType == 0) {
			useRandom(page);
		} else if (algorithmType == 1) {
			useLRU(page);
		} else if (algorithmType == 2) {
			useClockpage(page);
		} else if (algorithmType == 3) {
			useIdeal(page);
		} else {
			System.out
					.println("No Valid replacement algorithms found: We will instead use \"Remove all frames and crash violently\" ");

		}

	}

	/**
	 * Algorithm that replaces by picking a random page from the page frames to
	 * replace
	 * 
	 * @param pages
	 */
	private void useRandom(Page page) {
		Random rand = new Random(page.time * page.address);

		// optimization: if the random one is dirty, don't do it and pick
		// a different one

		int randomNumber = rand.nextInt(numFrames);

		//if the page needs write, find a different one to write to.
		//do this just once can save number of writes with little cost for picking a different frame
		if (pagesInFrame[randomNumber].needsWrite) {
			randomNumber = rand.nextInt(numFrames);
		}
		
		
		if (!Main.quiet) {
			if (pagesInFrame[randomNumber].address != -1) {// default address is
															// -1, so if its -1
															// its empty
				System.out.println("Using Random, removing page number "
						+ randomNumber);
			}
			System.out.println("Adding address " + page.address
					+ " to the Page Frames at location " + randomNumber);
		}
		
		if (pagesInFrame[randomNumber].needsWrite) {
			PageTable.numWrites++;
			pagesInFrame[randomNumber].needsWrite =false;
		}
		pagesInFrame[randomNumber] = page;
	}

	/**
	 * Function that employs the Clock page algorithm on the page frames by
	 * keeping track of a reference bit Sees the frames a circular pattern with
	 * an iterator like a clock hand In case of page fault: If the access bit is
	 * 0, replaces that page, otherwise clears access bit and moves on
	 * 
	 * @param page
	 *            The page to be added to the page frame
	 */
	private void useClockpage(Page page) {

		for (int i = 0; i < numFrames + 1; i++) {// +1 to numFrames because it
													// will worst case scenario
													// go over every frame and
													// need to replace the first
													// one.

			if (accessBit[CPIteratorIndex] == 0) {
				// print what is being done
				if (!Main.quiet) {
					if (pagesInFrame[CPIteratorIndex].address != -1) {// default
																		// address
																		// is
																		// -1,so
																		// if
																		// its
																		// -1
																		// its
																		// empty
						System.out
								.println("Using Clock Page, removing page number "
										+ CPIteratorIndex
										+ " after "
										+ i
										+ " pages passed over");
					}
					if (i == numFrames) {
						System.out
								.println("Worst case scenario was reached, every page was at 1, was at 0");
					}
					System.out.println("Adding address " + page.address
							+ " to the Page Frames at location "
							+ CPIteratorIndex);
				}

				// add page to page frames

				if (pagesInFrame[CPIteratorIndex].needsWrite) {
					PageTable.numWrites++;
					pagesInFrame[CPIteratorIndex].needsWrite = false;
				}

				if (!Main.quiet) {
					System.out.println("Writing the replaced frame to disk");
				}

				pagesInFrame[CPIteratorIndex] = page;

				// increment the numbers for the next access
				accessBit[CPIteratorIndex] = 1;
				CPIteratorIndex = (CPIteratorIndex + 1) % numFrames;

				break;
			} else {
				// clear the bit for the next iteration
				accessBit[CPIteratorIndex] = 0;
				// move the clock hand
				CPIteratorIndex = (CPIteratorIndex + 1) % numFrames;

			}// end else statement
		}// end for loop

	}// end clock page algorithm

	/**
	 * LRU finds the least recently used page and uses that one to replace
	 * 
	 * @param page
	 */
	private void useLRU(Page page) {
		long LRU = pagesInFrame[0].time;
		int oldestPage = 0;
		for (int i = 0; i < numFrames; i++) {
			if (pagesInFrame[i].time < LRU) {
				LRU = pagesInFrame[i].time;
				oldestPage = i;
			}
		}

		if (pagesInFrame[oldestPage].needsWrite) {
			PageTable.numWrites++;
			pagesInFrame[oldestPage].needsWrite = false;
		}

		if (!Main.quiet) {
			if (pagesInFrame[oldestPage].address != -1) {
				System.out.println("Using LRU, removing page number "
						+ oldestPage);
			}
			System.out.println("Adding address " + page.address
					+ " to the Page Frames at location " + oldestPage);
		}
		pagesInFrame[oldestPage] = page;
	}// end useLRU method

	/**
	 * Ideal algorithm that finds which page will not be needed for a long time
	 * in the future so it uses that one to be thrown out.
	 * 
	 * @param page
	 */
	private void useIdeal(Page page) {
		int indexOfFurthest = 0;

		if(pagesFilled == numFrames){
			if (!Main.quiet) {
				System.out.println("REPLACING PAGE: index of page chosen is "
						+ Arrays.toString(callDistance));
			}
		}
		
		if (pagesFilled < numFrames) {
			indexOfFurthest = pagesFilled;
			if (!Main.quiet) {
				System.out.println("writing #" + pagesFilled);
			}
			
			
			pagesFilled++;

		} else {
			int currentFarthestCallDistance = 0;
			for (int i = 0; i < numFrames; i++) {
				if (callDistance[i] > currentFarthestCallDistance) {
					currentFarthestCallDistance = callDistance[i];
					indexOfFurthest = i;
				}
			}

			if (!Main.quiet) {
				System.out.println("REPLACING PAGE: index of page chosen is "
						+ indexOfFurthest);
				System.out.println("REPLACING PAGE:  Old distance is "
						+ callDistance[indexOfFurthest]);
				System.out.println("Call distance array:"
						+ Arrays.toString(callDistance));

			}

		}

		// get distance to next one;
		for (int i = progressThroughProblem; i < Main.pagesArr.length; i++) {
			if (Main.pagesArr[i].address == page.address) {
				callDistance[indexOfFurthest] = i;
				if (!Main.quiet) {
					System.out.println("Distance of the added page next use:"
							+ callDistance[indexOfFurthest]);
				}
				break;
			}//end if
			//if never called again, then set to max so its replaced
			callDistance[indexOfFurthest]= Integer.MAX_VALUE;
		}

		if (!Main.quiet) {
			System.out.println("Progress: " + progressThroughProblem);
		}

		if (pagesInFrame[indexOfFurthest].needsWrite) {
			PageTable.numWrites++;
			pagesInFrame[indexOfFurthest].needsWrite = false;
		}

		pagesInFrame[indexOfFurthest] = page;

	}// useIdeal function end

	/**
	 * helper method to update the next time a page is called
	 * 
	 * @param page
	 * @return index of n
	 */
	private int findNextOfThisPage(Page page) {

		for (int i = progressThroughProblem; i < Main.pagesArr.length; i++) {
			if (Main.pagesArr[i].address == page.address) {
				return i;
			}
		}
		return Integer.MAX_VALUE; // page is never called again
	}

}// end Page Frame class
