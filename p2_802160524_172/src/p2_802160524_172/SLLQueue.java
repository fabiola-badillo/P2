package p2_802160524_172;

import p2_802160524_172.Queue;

public class SLLQueue<E> implements Queue<E> {

	// inner class for nodes in singly linked lists
		protected static class Node<T> {
			private T element; 
			private Node<T> next; 
			public Node() { 
				element = null; 
				next = null; 
			}
			public Node(T data, Node<T> next) { 
				this.element = data; 
				this.next = next; 
			}
			public Node(T data)  { 
				this.element = data; 
				next = null; 
			}
			public T getElement() {
				return element;
			}
			public void setElement(T data) {
				this.element = data;
			}
			public Node<T> getNext() {
				return next;
			}
			public void setNext(Node<T> next) {
				this.next = next;
			}
			public void clean() { 
				element = null; 
				next = null; 
			}
		}	
		private Node<E> first, last;   // references to first and last node
		private int size; 
   
		public SLLQueue() {           // initializes instance as empty queue
			first = last = null; 
			size = 0; 
		}
		public int size() {
			return size;
		}
		public boolean isEmpty() {
			return size == 0;
		}
		public E first() {
			if (isEmpty()) return null;
			return first.getElement(); 
		}
		public E dequeue() {
			if (isEmpty()) return null;		
			Node<E> ntr = first;
			first = first.getNext();
			if (size == 1) last = null;
			E etr = ntr.getElement();
			ntr.clean();
			size--;
			return etr;
		}
		public void enqueue(E e) {
			Node<E> nuevo = new Node<E>(e);
			if (size == 0) 
				first = last = nuevo; 
			else { 
				last.setNext(nuevo);
	  			last = nuevo;
			}
			size++; 
		}
		
	//JUST FOR TESTING
	@Override
	public void showReverse() { 
	    if (size == 0)
		   System.out.println("Queue is empty."); 
		else
		   recSR(first);
    } 
    private void recSR(Node<E> f) { 
		if (f != null) { 
		   recSR(f.getNext()); 
		   System.out.println(f.getElement()); 
	     } 
    } 

}
