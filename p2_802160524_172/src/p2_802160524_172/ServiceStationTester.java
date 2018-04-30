package p2_802160524_172;

import java.io.FileNotFoundException;
import java.util.PriorityQueue;

public class ServiceStationTester {

	public static void main(String[] args) throws FileNotFoundException {
		//instantiate service station per policies and number of servers
		//then call their serve method
		
		policy currentPolicy = policy.MLMSBWT;
		dataReader dr = new dataReader();
		Queue<Customer> inputQueue = dr.readData();
		PriorityQueueArrivalSort arrivalPriorityQueueSorter = new PriorityQueueArrivalSort();
		PriorityQueue<Customer> arrivalPriorityQueue = new PriorityQueue<Customer> (11, arrivalPriorityQueueSorter);
		while (!inputQueue.isEmpty()) {
			arrivalPriorityQueue.add(inputQueue.dequeue());
		}
		
		ServiceStation ss = new ServiceStation(currentPolicy, 3, arrivalPriorityQueue);
		ss.Serve();

	}

}
