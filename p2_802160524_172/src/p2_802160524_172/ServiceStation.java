package p2_802160524_172;

import java.util.PriorityQueue;

public class ServiceStation {
	
	private policy currentPolicy;
	private int serversQty;

	private PriorityQueueArrivalSort arrivalPriorityQueueSorter = new PriorityQueueArrivalSort();
	private PriorityQueue<Customer> arrivalPriorityQueue = new PriorityQueue<Customer> (11, arrivalPriorityQueueSorter);
	
	private PriorityQueueCompletionSort servicePriorityQueueSorter = new PriorityQueueCompletionSort();
	private PriorityQueue<Customer> servicePriorityQueue = new PriorityQueue<Customer>(11, servicePriorityQueueSorter);
	
	private SLLQueue<Customer>[] waitLines;
	
	public ServiceStation(policy currentPolicy, int serversQty, PriorityQueue<Customer> arrivalPriorityQueue) {
		super();
		this.currentPolicy = currentPolicy;
		this.serversQty = serversQty;
		this.arrivalPriorityQueue = arrivalPriorityQueue;
		
		//create waiting lines
		for(int i=1;i<serversQty; i++) {
			waitLines[i] = new SLLQueue<Customer>();
		}
	}

	public void Serve() {
		int currentTime = 0;
		boolean finished = false;
		Customer currentCompletedCustomer;
		
		while (!finished) {
			
			//check completion events
			while (servicePriorityQueue.peek().getCompletionTime() == currentTime) {  //more completion events to process for current time
				currentCompletedCustomer = servicePriorityQueue.poll();	//poll should remove from priority queue, verify
				System.out.println(currentCompletedCustomer.getCid());  //TODO collect running statistics and write to raw results files
				//TODO:  need to remove head of line waitLines[currentCompletedCustomer.getServerid()].
			}
			
			//check for possible transfers (if applicable per currrent policy)
			if (currentPolicy == policy.MLMSBLL) {
				//check waitLines[i] lengths and balance, need to do it in a loop cause we have variable number of waitlines
				
				int shortestLine = 0;
				for (int i = 1; i < serversQty; i++) { // identifies shortest line
					if (waitLines[i].size() < waitLines[shortestLine].size()) {
						shortestLine = i;
					}
				}
				
			}
			if (currentPolicy == policy.MLMSBWT) {
				//check waitLines[i] wait times and balance, need to do it in a loop cause we have variable number of waitlines
			}
			
			//check for service starts (caused by completions above)
			//get new waitline heads and add to servicePriorityQueue
			
			
			
			//now check for arrival events and assign to waitline per policy
			//do peek in arrivalPriorityQueue to see if the next arrival time is now
			//if so poll (remove customer from arrival pq) and assign to wait line
			//loop (keep peeking into arrival queue and processing as needed until you see a future time)
			while (arrivalPriorityQueue.peek().getArrivalTime() == currentTime) {
				if (currentPolicy == policy.SLMS) {
					
				}
				else if (currentPolicy == policy.MLMS) {
					
				}
				else if (currentPolicy == policy.MLMSBLL) {
					
				}
				else { // currentPolicy = policy.MLMSBWT
					
				}
			}
			
			
			//now update time to time of next event by peeking into service queue and arrival queue a choosing nearest future time
			//currentTime = new next time
			if (arrivalPriorityQueue.peek().getArrivalTime() < servicePriorityQueue.peek().getServiceTime()) {
				currentTime = arrivalPriorityQueue.peek().getArrivalTime();
			}else {
				currentTime = servicePriorityQueue.peek().getServiceTime();
			}
			
			//if arrival queue and service queue are empty we are done
			//finished = true;
			if (arrivalPriorityQueue.isEmpty() && servicePriorityQueue.isEmpty()) {
				finished = true;
			}

			
		}
	}
	
}
