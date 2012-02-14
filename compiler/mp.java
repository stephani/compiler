package compiler;

import java.io.IOException;

public class mp {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException
	   {	
	    	if (args.length < 1) 	{
	    		System.out.println("No input file provided!!!");
	    		return;	    		
	    	}
	    	//call scanner constructor and open inputFile
	    	for(int i=0;i<args.length;i++) {
	    		run(args[i], "output.txt");	    		
	    	}		
	    	//for parser part
		//gets TokenQueue and writes to TokenFile
	    	
	    } 
	private static void run(String input, String output) throws IOException {
		System.out.println("Input:  " + input);
		Scanner scanner = new Scanner();
		
		if(scanner.openFile(input) !=0)  {
			System.out.println("failed to open "+input);
			return;
		}
		scanner.dispatcher();
		scanner.writeFile(output, scanner.getQueue());	
		System.out.println("Finished scanning.........");
	}
	
}
