package p2_802160524_172;

import java.util.Comparator;

public class PriorityQueueArrivalSort implements Comparator<Customer> {

	@Override
	public int compare(Customer c1, Customer c2) {
		if (c1.getArrivalTime() < c2.getArrivalTime()) {
			return -1;
		}
		else if (c1.getArrivalTime() > c2.getArrivalTime()) {
			return 1;		
		}
		else { 	//same arrival time, go by file index
			if (c1.getFileIndex() < c2.getFileIndex()) {
				return -1;
			}
			else if (c1.getFileIndex() > c2.getFileIndex()) {
				return 1;		
			}
			else { 	//same file index, go by cid (read order)		
				if (c1.getCid() < c2.getCid()) {
					return -1;
				}
				else if (c1.getCid() > c2.getCid()) {
					return 1;		
				}
				// no else cause no ties by definition (no equal cids)
			}
		}
		return 0;	//shouldn't be needed but eclipse wants it
	}
}
