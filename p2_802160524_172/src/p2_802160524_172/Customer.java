package p2_802160524_172;

/**
 * This class implements the algorithms for each
 * serving scheme and policy.
 * 
 * Based on Job<E> class from lab activity.
 * 
 * @author Fabiola Badillo 802-16-0524 Section 090
 *
 */

public class Customer {
	private int cid;			// customer id (assigned in read order incrementing)
	private int arrivalTime;   	// time it gets to service station
	private int serviceTime; 	// time it takes to be serviced
	private int startTime;		// time it starts being serviced
	private int completionTime; // time when the service for this job is completed
	private int fileIndex; 		// the i from the file name that it was read from
	private int serverid;		// the id of the server that provided the service

	public Customer(int id, int at, int st, int fi) { 	
		cid = id; 
		arrivalTime = at; 
		serviceTime = st; 
		startTime = -1;
		completionTime = -1;
		fileIndex = fi;
		serverid = -1;
	}
	
	public int getCid() {
		return cid;
	}

	public int getServiceTime() {
		return serviceTime;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getCompletionTime() {
		return completionTime;
	}

	public void setCompletionTime(int completionTime) {
		this.completionTime = completionTime;
	}
	
	public int getArrivalTime() {
		return arrivalTime;
	}
		
	public int getFileIndex() {
		return fileIndex;
	}
	
	public int getServerid() {
		return serverid;
	}

	public void setServerid(int serverid) {
		this.serverid = serverid;
	}
}
