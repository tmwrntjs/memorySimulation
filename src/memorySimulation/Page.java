package memorySimulation;

//import java.sql.Timestamp;


public class Page {
	public int address=-1;
	public boolean needsWrite;
	public long time;
	
	public Page(int address, boolean needsWrite){
		this.address = address;
		this.needsWrite = needsWrite;
		this.time = System.nanoTime();
	}

	public Page() {
		this.time = System.nanoTime();
	}
	

}
