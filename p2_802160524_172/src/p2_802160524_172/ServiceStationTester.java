package p2_802160524_172;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class ServiceStationTester {

	public static void main(String[] args) throws FileNotFoundException {
		//instantiate service station per policies and number of servers
		//then call their serve method
		
		policy[] currentPolicies = {policy.SLMS,policy.MLMSBLL,policy.MLMSBLL,policy.MLMSBWT};
		dataReader dr = new dataReader();

		ArrayList<PriorityQueue<Customer>> allinput= dr.readData();
		PriorityQueueArrivalSort arrivalPriorityQueueSorter = new PriorityQueueArrivalSort();
		PriorityQueue<Customer> arrivalPriorityQueue = new PriorityQueue<Customer> (11, arrivalPriorityQueueSorter);
		for(int i = 0; i<allinput.size(); i++) {
			arrivalPriorityQueue=allinput.get(i);
			
		
		for(int p=0; p<currentPolicies.length; p++) { // number of policies
			for(int s = 1; s < 6; s = s + 2) { // number of servers
		ServiceStation ss = new ServiceStation(currentPolicies[p], s, arrivalPriorityQueue);
		ss.Serve();
		boolean writing = true;
		
			if(writing ) {
				
			String directory = "outputFiles"; // folder that contains the files
			String name ="data_"+p+"_OUT.txt";
			PrintWriter tool = new PrintWriter(new File(directory,name));
			tool.println("Number of customers is:"+ss.getN() );
				if(writing) {
					tool.println("");
					tool.println(""+currentPolicies[p].toString()+ " " + s + ":" + ss.getCurrentTime()+ " "+ss.getT2()+" "+ss.getM());
					tool.println("");
				}
				
				if(p==currentPolicies.length-1) {
					tool.close();
				}
				
			}		
			
		
				}
			}
		}
	}
}