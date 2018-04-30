package p2_802160524_172;

import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * This class implements the algorithms for each
 * serving sheme and policy.
 * 
 * @author Fabiola Badillo 802-16-0524 Section 090
 *
 */

public class ServiceStation {

	// necessary instance variables
	
	private policy currentPolicy; // the serving scheme being executed
	private int serversQty; // the number of service stations
	private PriorityQueueArrivalSort arrivalPriorityQueueSorter = new PriorityQueueArrivalSort(); // comparator used to determine the priority on the arrivalPriorityQueue
	private PriorityQueue<Customer> arrivalPriorityQueue = new PriorityQueue<Customer>(11,arrivalPriorityQueueSorter); // stores the arrival events
	private PriorityQueueCompletionSort servicePriorityQueueSorter = new PriorityQueueCompletionSort(); // comparator used to determine the priority on the servicePriorityQueue
	private PriorityQueue<Customer> servicePriorityQueue = new PriorityQueue<Customer>(11,servicePriorityQueueSorter); // stores the service events
	private LinkedList<Customer>[] waitLines; // list of waiting lines
	private boolean ServiceCompleted[]; // array used as flag for finding service stations availability
	private double avgM; // average number of customers overpassing per customer
	private int currentTime; // keeps track of the actual time
	private double t2; // average waiting time per customer
	private int n; // number of customers being serviced

	public ServiceStation(policy currentPolicy, int serversQty, PriorityQueue<Customer> arrivalPriorityQueue) {
		super();
		this.currentPolicy = currentPolicy;
		this.serversQty = serversQty;
		this.arrivalPriorityQueue = arrivalPriorityQueue;
		this.waitLines = new LinkedList[serversQty];
		this.ServiceCompleted = new boolean[serversQty];
		this.avgM = 0;
		currentTime = 0;
		t2 = 0;
		

		//create waiting lines
		for(int i=0;i<serversQty; i++) { 
			waitLines[i] = new LinkedList<Customer>();
			ServiceCompleted[i] = false;
		}
	}

	public void Serve() {

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
		double m = 0;
		double totalWaitingTime = 0;


		while (!finished) {

			//check for completion events (highest priority events, done here first)
			while (!servicePriorityQueue.isEmpty() && servicePriorityQueue.peek().getCompletionTime() == currentTime) {  //more completion events to process for current time
				currentCompletedCustomer = servicePriorityQueue.poll();	//poll should remove for priority queue, verify
				//flag serviceCompleted on this line so we can start a new one
				ServiceCompleted[currentCompletedCustomer.getServerid()] = true;
				//remove from waitLine[currentCompletedCustomer.getServerid()]
				waitLines[currentCompletedCustomer.getServerid()].pollFirst();	
				
				// add up every customer's waiting time
				totalWaitingTime += (currentCompletedCustomer.getStartTime() - currentCompletedCustomer.getArrivalTime());
				
				// logic for keeping track of m
				// compare that arrival and start time with the arrival and starttime of every line's head customer
				for (int i = 0; i < waitLines.length; i++) {
					if (waitLines[i].size() > 0) {	//skip empty lines
						//we need to check the arrival and start times of the customers currently being serviced to see if they arrived before but this one started before those
						if (currentCompletedCustomer.getArrivalTime() > waitLines[i].get(0).getArrivalTime()) { //it arrived after this one that hasn't finished, need to check start time
							if (waitLines[i].get(0).getStartTime() < 0 || (waitLines[i].get(0).getStartTime() > currentCompletedCustomer.getStartTime())){
								m++; //number of customers who started being serviced before other customer who arrived first
							}
						}
						for (int j = 1; j < waitLines[i].size(); j++) {
							if (waitLines[i].get(j).getArrivalTime() < currentCompletedCustomer.getArrivalTime()) {
								m++; //number of customers who arrived before other customer who just finished
							}
						}
					}
				}
				
				// keep track of number of events
				completionEventId++;
				globalEventId++;

				// used for testing
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
					for (int i = 1; i < serversQty; i++) { 
						if (waitLines[i].size() < waitLines[shortest].size()) {
							shortest = i; // identifies shortest line
						}
						if (waitLines[i].size() > waitLines[longest].size()) {
							longest = i; // identifies longest line
						}
					}
					sizeDiff = waitLines[longest].size() - waitLines[shortest].size(); 
					// checks if the difference in line length is equal or greater than two, which is the case when a transfer makes sense
					if (sizeDiff >= 2) {
						waitLines[shortest].addLast(waitLines[longest].pollLast());
					}
				}
			}

			//check for service starts (caused by completions above) (third priority events, done here third)
			//get new waitline heads and add to servicePriorityQueue
			if (this.currentPolicy == policy.SLMS) {			
				if (ServiceCompleted[0]) {	//check line with server
					if (waitLines[0].size() > 0) {	//the first server became available so don't need to send next customer to any other server
						tmpStartingCustomer = waitLines[0].peekFirst();
						tmpStartingCustomer.setServerid(0);
						tmpStartingCustomer.setStartTime(currentTime);
						tmpStartingCustomer.setCompletionTime(currentTime + tmpStartingCustomer.getServiceTime());
						servicePriorityQueue.add(tmpStartingCustomer);	//priority queue automatically places the new customer being serviced in its correct position based on scheduled completion time	
					}
					ServiceCompleted[0] = false;
				}
				for(int i = 1; i < serversQty; i++) {
					if (ServiceCompleted[i]) {
						if (waitLines[0].size() > 1) {	//customers waiting in our only line, bring next customer over to this server that just became available
							tmpStartingCustomer = waitLines[0].get(1);
							waitLines[0].remove(1);
							tmpStartingCustomer.setServerid(i);
							tmpStartingCustomer.setStartTime(currentTime);
							tmpStartingCustomer.setCompletionTime(currentTime + tmpStartingCustomer.getServiceTime());
							servicePriorityQueue.add(tmpStartingCustomer);	//priority queue automatically places the new customer being serviced in its correct position based on scheduled completion time
						}
						ServiceCompleted[i] = false;
					}
				}				
			}
			else {	
				//get new waitline heads and add to servicePriorityQueue
				for(int i = 0; i < serversQty; i++) { 
					if (ServiceCompleted[i]) {
						if (waitLines[i].size() > 0) {
							tmpStartingCustomer = waitLines[i].peekFirst();
							tmpStartingCustomer.setServerid(i);
							tmpStartingCustomer.setStartTime(currentTime);
							tmpStartingCustomer.setCompletionTime(currentTime + tmpStartingCustomer.getServiceTime());
							servicePriorityQueue.add(tmpStartingCustomer);	//priority queue automatically places the new customer being serviced in its correct position based on scheduled completion time	
						}
						ServiceCompleted[i] = false;
					}
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
					for(int i = 1; i < serversQty; i++) {
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
					for (int i = 1; i < waitLines[0].size(); i++) { // identify which line has less waiting time
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
					// add the next customer to the line with less waiting time
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
					System.out.println(globalEventId + ", " + arrivalEventId + ". Time = " + currentTime + " Arrived cid = " + waitLines[fastestLine].peekLast().getCid() + " at = " + 
							waitLines[fastestLine].peekLast().getArrivalTime());  
				}
				
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
					t2 = totalWaitingTime / completionEventId;
					n = completionEventId;
//					System.out.println("Avg m = " + avgM);
//					System.out.println("T2 = " + t2);
//					System.out.println(n);
				}
			}		
		}
		

	}

	public int getCurrentTime() {
		return currentTime;
	}
	
	public double getM() {
		return avgM;
	}
	
	public double getT2() {
		return t2;
	}
	
	public int getN(){
		return n;
	}
	
	
}
