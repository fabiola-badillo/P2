package p2_802160524_172;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;


public class dataReader {
	private String parentDirectory;
	ArrayList<PriorityQueue<Customer>> inputQueue;
	ArrayList<Integer> IncorrectFormatedFiles, correctlyformatedfiles;
	public int i;

	public dataReader() throws FileNotFoundException {
		parentDirectory = "inputFiles"; 
		IncorrectFormatedFiles = new ArrayList<>();
		correctlyformatedfiles = new ArrayList<>();
		i=0;
	}

	public ArrayList<PriorityQueue<Customer>> readData() throws FileNotFoundException{ // TODO add file "dataFiles.txt"
		try {  inputQueue= new ArrayList<PriorityQueue<Customer>>();
		int id = 0;

		String parenFiles = "dataFiles.txt";
		Scanner input = new Scanner(new File(parentDirectory, parenFiles));
		while(input.hasNextLine()) {
			String datap=input.nextLine();
			i++;
			String fileName = datap;
			Scanner inputFile = new Scanner(new File(parentDirectory, fileName));
			PriorityQueueCompletionSort servicePriorityQueueSorter = new PriorityQueueCompletionSort();
			PriorityQueue<Customer> tool = new PriorityQueue<Customer>(11, servicePriorityQueueSorter);



			while (inputFile.hasNext()) {
				String data = inputFile.nextLine();
				String[] dataArr = data.split(" ");
				for(int j=0; j<dataArr.length;j++) {
					if(!Character.isDigit(data.charAt(j)) || data.length()==0 || data.length()>2){

						generateOutput(i, "Input file does not meet the expected format or it is empty.");
					}
					else {

						tool.add(new Customer(id,(int) Integer.parseInt(dataArr[0]), (int)Integer.parseInt(dataArr[1]), i));	//TODO: make first param globally unique, last param is file index
						id++;
					}
				}
				inputQueue.add(tool);


			}
			inputFile.close();
		}
		input.close();
		return inputQueue; 

		}
		catch(FileNotFoundException nelli) {

			generateOutput(i, "Input file not found.");


		}
		return inputQueue;
	}





	public void generateOutput(int i, String Error) throws FileNotFoundException {  
		String directory = "outputFiles"; // folder that contains the files

		String name ="data_"+i+"_OUT.txt";

		PrintWriter tool = new PrintWriter(new File(directory,name));

		tool.println(Error);

		tool.close();

	}



}
