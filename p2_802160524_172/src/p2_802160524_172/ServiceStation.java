package p2_802160524_172;

import java.util.LinkedList;
import java.util.PriorityQueue;

public class ServiceStation {

	private policy currentPolicy;
	private int serversQty;

	private PriorityQueueArrivalSort arrivalPriorityQueueSorter = new PriorityQueueArrivalSort();
	private PriorityQueue<Customer> arrivalPriorityQueue = new PriorityQueue<Customer>(11,arrivalPriorityQueueSorter);

	private PriorityQueueCompletionSort servicePriorityQueueSorter = new PriorityQueueCompletionSort();
	private PriorityQueue<Customer> servicePriorityQueue = new PriorityQueue<Customer>(11,servicePriorityQueueSorter);

	private LinkedList<Customer>[] waitLines;
	private boolean ServiceCompleted[];

	public ServiceStation(policy currentPolicy, int serversQty, PriorityQueue<Customer> arrivalPriorityQueue) {
		super();
		this.currentPolicy = currentPolicy;
		this.serversQty = serversQty;
		this.arrivalPriorityQueue = arrivalPriorityQueue;
		this.waitLines = new LinkedList[serversQty];
		this.ServiceCompleted = new boolean[serversQty];

		//create waiting lines
		for(int i=0;i<serversQty; i++) { // TODO check index, i changed to zero, it was 1
			waitLines[i] = new LinkedList<Customer>();
			ServiceCompleted[i] = false;
		}
	}

	public void Serve() {
		int currentTime = 0;
		boolean finished = false;
		Customer currentCompletedCustomer;
		int shortestLine;
		int shortestLineLength;
		int nextServiceCompletionTime = -1;
		int nextArrivalTime = -1;
		boolean arrivalPriorityQueueEmpty = false;
		boolean servicePriorityQueueEmpty = false;
		Customer tmpStartingCustomer;
		int globalEventId = 0;
		int arrivalEventId = 0;
		int completionEventId = 0;
		int m = 0;
		double avgM = 0;


		while (!finished) {

			//check for completion events (highest priority events, done here first)
			while (!servicePriorityQueue.isEmpty() && servicePriorityQueue.peek().getCompletionTime() == currentTime) {  //more completion events to process for current time
				currentCompletedCustomer = servicePriorityQueue.poll();	//poll should remove for priority queue, verify
				//flag serviceCompleted on this line so we can start a new one
				ServiceCompleted[currentCompletedCustomer.getServerid()] = true;
				//remove from waitLine[currentCompletedCustomer.getServerid()]
				waitLines[currentCompletedCustomer.getServerid()].pollFirst();	

				// logic for keeping track of m
				// check arrival time of customer who just finished
				int currentCompletedCustomerArrivalTime = currentCompletedCustomer.getArrivalTime();
				// compare that arrival time with the arrival time of every line's head customer
				for (int i = 0; i < waitLines.length; i++) {
					for (int j = 0; j < waitLines[i].size(); j++) {
						if (waitLines[i].get(j).getArrivalTime() < currentCompletedCustomerArrivalTime) {
							m++; //number of customers who finished being serviced before other customer who arrived first
						}
						else {
							break;
						}
					}
				}
				// keep track of number of events
				completionEventId++;
				globalEventId++;

				//TODO collect running statistics and write to raw results files
				System.out.println(globalEventId + ", " + completionEventId + ". Time = " + currentTime + " Completed cid = " + currentCompletedCustomer.getCid() + " at = " + 
						currentCompletedCustomer.getArrivalTime() + " st = " + currentCompletedCustomer.getStartTime() + " ct = " +
						currentCompletedCustomer.getCompletionTime());  
			}

			//check for possible transfers (if applicable per current policy) (second priority events, done here second)
			if (currentPolicy == policy.MLMSBLL) {
				//check waitLines[i] lengths and balance, need to do it in a loop cause we have variable number of waitlines
				int sizeDiff = 2; // default to two to enter the while initially
				while (sizeDiff >= 2) {
					int shortest = 0;
					int longest = 0;
					for (int i = 1; i < serversQty; i++) { // identifies shortest line
						if (waitLines[i].size() < waitLines[shortest].size()) {
							shortest = i;
						}
						if (waitLines[i].size() > waitLines[longest].size()) {
							longest = i;
						}
					}
					sizeDiff = waitLines[longest].size() - waitLines[shortest].size();
					if (sizeDiff >= 2) {
						waitLines[shortest].addLast(waitLines[longest].pollLast());
					}
				}
			}

			//check for service starts (caused by completions above) (third priority events, done here third)
			//get new waitline heads and add to servicePriorityQueue
			for(int i = 0; i < serversQty; i++) { // TODO check index, i changed to zero, it was 1
				if (ServiceCompleted[i]) {
					if (this.currentPolicy == policy.SLMS) {
						if (waitLines[0].size() > 1) {
							tmpStartingCustomer = waitLines[0].get(1);
							waitLines[0].remove(1);
							tmpStartingCustomer.setServerid(0);
							tmpStartingCustomer.setStartTime(currentTime);
							tmpStartingCustomer.setCompletionTime(currentTime + tmpStartingCustomer.getServiceTime());
							servicePriorityQueue.add(tmpStartingCustomer);	//priority queue automatically places the new customer being serviced in its correct position based on scheduled completion time
						}
					}
					else {
						if (waitLines[i].size() > 0) {
							tmpStartingCustomer = waitLines[i].peekFirst();
							tmpStartingCustomer.setServerid(i);
							tmpStartingCustomer.setStartTime(currentTime);
							tmpStartingCustomer.setCompletionTime(currentTime + tmpStartingCustomer.getServiceTime());
							servicePriorityQueue.add(tmpStartingCustomer);	//priority queue automatically places the new customer being serviced in its correct position based on scheduled completion time	
						}
					}
					ServiceCompleted[i] = false;
				}
			}

			//now check for arrival events and assign to waitline per policy (last priority, done last (forth) here
			//do peek in arrivalPriorityQueue to see if the next arrival time is now (repeat as needed until next one is in the future) 
			//				System.out.println(arrivalPriorityQueue.size());
			while (!arrivalPriorityQueue.isEmpty() && arrivalPriorityQueue.peek().getArrivalTime() == currentTime) {
				if (currentPolicy == policy.SLMS) {
					boolean emptyServerFlag = false;
					int assignedServer = -1; 
					if (waitLines[0].size() == 0) {
						tmpStartingCustomer = arrivalPriorityQueue.poll();
						tmpStartingCustomer.setServerid(0);
						tmpStartingCustomer.setStartTime(currentTime);
						tmpStartingCustomer.setCompletionTime(currentTime + tmpStartingCustomer.getServiceTime());
						waitLines[0].add(tmpStartingCustomer);
						servicePriorityQueue.add(tmpStartingCustomer);
						assignedServer = 0;
					}
					else if (waitLines[0].size() == 1) {
						for (int i = 1; i < waitLines.length; i++) {
							if (waitLines[i].size() == 0) {
								tmpStartingCustomer = arrivalPriorityQueue.poll();
								tmpStartingCustomer.setServerid(i);
								tmpStartingCustomer.setStartTime(currentTime);
								tmpStartingCustomer.setCompletionTime(currentTime + tmpStartingCustomer.getServiceTime());
								waitLines[i].add(tmpStartingCustomer);
								servicePriorityQueue.add(tmpStartingCustomer);
								emptyServerFlag = true;
								assignedServer = i;
							}
						}
						if (!emptyServerFlag) {
							waitLines[0].add(arrivalPriorityQueue.poll());
							assignedServer = 0;
						}
					}
					else {
						waitLines[0].add(arrivalPriorityQueue.poll());
						assignedServer = 0;
					}
					arrivalEventId++;
					globalEventId++;
					System.out.println(globalEventId + ", " + arrivalEventId + ". Time = " + currentTime + " Arrived cid = " + waitLines[assignedServer].peekLast().getCid() + " at = " + 
							waitLines[assignedServer].peekLast().getArrivalTime()); 

				}
				else if (currentPolicy == policy.MLMS || currentPolicy == policy.MLMSBLL) {
					shortestLine = 0;	//initially pick first line as shortest (if not the checks below will substitute it)
					shortestLineLength = waitLines[0].size(); // changed from waitLines[1].size(); to waitLines[0].size();
					for(int i=2;i<serversQty; i++) {
						if (waitLines[i].size() < shortestLineLength) {
							shortestLine = i;
							shortestLineLength = waitLines[i].size();
						}
					}
					//remove form arrival priority queue and add to shortest line
					waitLines[shortestLine].add(arrivalPriorityQueue.poll()); 
					//check if waitline was empty because if so, we'll need to start the service for it right away
					if(waitLines[shortestLine].size() == 1) {
						tmpStartingCustomer = waitLines[shortestLine].peekFirst();
						tmpStartingCustomer.setServerid(shortestLine);
						tmpStartingCustomer.setStartTime(currentTime);
						tmpStartingCustomer.setCompletionTime(currentTime + tmpStartingCustomer.getServiceTime());
						servicePriorityQueue.add(tmpStartingCustomer);
					}
					arrivalEventId++;
					globalEventId++;
					System.out.println(globalEventId + ", " + arrivalEventId + ". Time = " + currentTime + " Arrived cid = " + Integer.toString(waitLines[shortestLine].peekLast().getCid()) + " at = " + 
							Integer.toString(waitLines[shortestLine].peekLast().getArrivalTime()));  
				}
				else { // currentPolicy == policy.MLMSBWT
					int fastestLine = 0;
					int fastestLineWaitingTime = 0;
					int currentLineWaitingTime = 0;
					for (int i = 1; i < waitLines[0].size(); i++) {
						fastestLineWaitingTime += waitLines[0].get(i).getServiceTime();
					}
					if (waitLines[0].size() > 0) {
						fastestLineWaitingTime += (waitLines[fastestLine].getFirst().getCompletionTime() - currentTime);
					}

					for (int i = 1; i < waitLines.length; i++) {
						for (int j = 1; j < waitLines[i].size(); j++) {
							currentLineWaitingTime += waitLines[i].get(j).getServiceTime();
						}
						if (waitLines[i].size() > 0) {
							currentLineWaitingTime += (waitLines[i].getFirst().getCompletionTime() - currentTime);
						}

						if (fastestLineWaitingTime > currentLineWaitingTime) {
							fastestLineWaitingTime = currentLineWaitingTime;
							fastestLine = i;
						}
					}
					waitLines[fastestLine].add(arrivalPriorityQueue.poll());

					//check if waitline was empty because if so, we'll need to start the service for it right away
					if(waitLines[fastestLine].size() == 1) {
						tmpStartingCustomer = waitLines[fastestLine].peekFirst();
						tmpStartingCustomer.setServerid(fastestLine);
						tmpStartingCustomer.setStartTime(currentTime);
						tmpStartingCustomer.setCompletionTime(currentTime + tmpStartingCustomer.getServiceTime());
						servicePriorityQueue.add(tmpStartingCustomer);
					}
					arrivalEventId++;
					globalEventId++;
					System.out.println(globalEventId + ", " + arrivalEventId + ". Time = " + currentTime + " Arrived cid = " + Integer.toString(waitLines[fastestLine].peekLast().getCid()) + " at = " + 
							Integer.toString(waitLines[fastestLine].peekLast().getArrivalTime()));  
				}
				//TODO: add ifs other arrival policies logic
			}	


			//when we get here all current time events have been serviced, determine if we are done or if there are more pending events and update time to time of next event before cycling back to top of while
			if (arrivalPriorityQueue.size() > 0){
				nextArrivalTime = arrivalPriorityQueue.peek().getArrivalTime();				
			}
			else {	//arrival queue is empty so we are done with all arrivals
				arrivalPriorityQueueEmpty = true;
			}
			if (servicePriorityQueue.size() > 0){
				nextServiceCompletionTime = servicePriorityQueue.peek().getCompletionTime();	
				servicePriorityQueueEmpty = false;  //service queue could have gone empty but then busy again if there was a sizable gap in arrival events
			}
			else {	//we are done with all arrivals
				servicePriorityQueueEmpty = true;
			}			

			if (!arrivalPriorityQueueEmpty) { //we still have pending arrivals
				if (!servicePriorityQueueEmpty) {	//and pending services to complete
					if (nextArrivalTime < nextServiceCompletionTime) { //update time for next event
						currentTime = nextArrivalTime; 	
					}
					else {
						currentTime = nextServiceCompletionTime;
					}
				}
				else { //service queue is empty so next event time is next arrival time
					currentTime = nextArrivalTime; 
				}
			}
			else { //no more arrival events pending
				if (!servicePriorityQueueEmpty){	//but we still have service events pending so next time is time of next service complete
					currentTime = nextServiceCompletionTime;
				}
				else {	//neither any service events pending.  we are done!
					finished = true;
					avgM = m / completionEventId;
					System.out.println(avgM);
				}
			}		
		}
		//TODO: we are done processing events so do final statistics

	}

}
