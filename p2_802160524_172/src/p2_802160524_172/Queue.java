package p2_802160524_172;

public interface Queue<E> {
	int size();
	boolean isEmpty(); 
	E first(); 
	E dequeue(); 
	void enqueue(E e);
	//just for testing
	void showReverse(); 
}
