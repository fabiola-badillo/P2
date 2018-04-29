package p2_802160524_172;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import p2_802160524_172.SLLQueue;

public class dataReader {
	private String parentDirectory;
	SLLQueue<Customer> inputQueue;

	public dataReader() throws FileNotFoundException {
		parentDirectory = "inputFiles"; 
	}

	public SLLQueue<Customer> readData() throws FileNotFoundException{ // TODO add file "dataFiles.txt"
	
		int id = 0;
		//TODO:  read filenames from file "datafiles.txt" and then repeat logic inside the for loop below for each file
		for (int i=1; i<3; i++) { // first 3 only for testing only, change per comment above
			String fileName = "data_" + i  + ".txt"; 
			Scanner inputFile = new Scanner(new File(parentDirectory, fileName)); 

			while (inputFile.hasNext()) {
				String data = inputFile.nextLine();
				String[] dataArr = data.split(" ");
				inputQueue.enqueue(new Customer(id, Integer.parseInt(dataArr[0]), Integer.parseInt(dataArr[1]), i));	//TODO: make first param globally unique, last param is file index
				id++;
			}
			inputFile.close();
		}
		return inputQueue; 
	}
} 
