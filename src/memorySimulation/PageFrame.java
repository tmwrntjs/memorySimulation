package memorySimulation;

import java.util.Random;

public class PageFrame {

	public static int notPageFault=0;
	public int numFrames;
	public Page[] pagesInFrame;
	public int[] accessBit;
	public int algorithmType;

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

		for (int i = 0; i < pagesInFrame.length; i++) { // check all frames for
														// this page
			if (pagesInFrame[i].address == page.address) {
				if (!Main.quiet) {
					System.out.println("(not a page fault)");
				}
				notPageFault++;
				return false; // page was found, not a page fault
			}
		}

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
			// }else if(algorithmType == 1){
			// useLRU(page);
			// }else if(algorithmType == 2){
			// useClockpage(page);
			// }else if(algorithmType == 3){
			// useIdeal(page);
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

		int randomNumber = rand.nextInt(numFrames);

		if (pagesInFrame[randomNumber].needsWrite) {
			PageTable.numWrites++;
		}

		if (!Main.quiet) {
			if (pagesInFrame[randomNumber].address != -1) {
				System.out.println("Using Random, removing page number "
						+ randomNumber);
			}
			System.out.println("Adding address " + page.address
					+ " to the Page Frames at location "+ randomNumber);
		}
		pagesInFrame[randomNumber].address = page.address;
		pagesInFrame[randomNumber].time= page.time;
		pagesInFrame[randomNumber].needsWrite= page.needsWrite;

	}

}
