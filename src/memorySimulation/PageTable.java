package memorySimulation;

/**
 * Page Table Object
 * 
 * @author Tim Warntjes, Andy Dulmes
 * 
 */
public class PageTable {
	public Page[] pages = new Page[1048576]; // 2^20
	private int currentIndex = 0;
	public static int numReads = 0;
	public static int numWrites = 0;

	public PageTable() {

		for (int i = 0; i < pages.length; i++) {
			pages[i] = new Page();
		}

	}

	/**
	 * updates the time and adds page to page table if not already in it
	 * 
	 * @param page
	 */
	public int add(Page page) {
		int i;
		for (i = 0; i < currentIndex; i++) {
			if (pages[i].address == page.address) {
				if (!Main.quiet) {
					System.out.println(page.address
							+ " already exists in Page Table");
					System.out.println("Previous time was: " + page.time);
				}
				pages[i].time = System.nanoTime();
				if (!Main.quiet) {
					System.out.println("Time updated to: " + page.time);
				}

				if (!pages[i].needsWrite) { // if it is currently write, don't
											// change to read
					pages[i].needsWrite = page.needsWrite; // otherwise make it
															// the new r/w
				}

				return i;// out of loop
			}

		}// end of for
			// if not found in the page table yet, add it
		if (currentIndex == i) {
			pages[currentIndex].address = page.address;
			pages[currentIndex].needsWrite = page.needsWrite;
			pages[currentIndex].time = page.time;
			if (!Main.quiet) {
				System.out.println(page.address
						+ " Did NOT exist in Page Table");
			}

			currentIndex++;

		}// end of if statement
		return i;

	}// end of add method

}// end of class
