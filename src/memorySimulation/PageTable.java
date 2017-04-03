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
	public static int secondNumberOfReads =0;

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
		for (i = 0; i < currentIndex; i++) {// checks if it already in the page
											// table
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
				
				if (!pages[i].needsWrite) { //if needs write is false (read)
											// change to read, but if its write, keep it write
					pages[i].needsWrite = page.needsWrite; // otherwise make it
															// the new r/w
				}
				
				
				//if old is write, new is write--> write
				//if old is read, new is write -->write
				//if old is write, new is read --> write
				//if old is read, new is read --> read

				return i;
			}//end if found

		}// end of for
		
		// if not found in the page table yet, add it
		pages[currentIndex] = page;
		if (!Main.quiet) {
			System.out.println(page.address + " Did NOT exist in Page Table");
		}
		currentIndex++;

		return i;

	}// end of add method
}// end of class
