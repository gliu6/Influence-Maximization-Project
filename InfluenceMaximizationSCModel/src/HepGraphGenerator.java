import java.io.*;
import java.util.ArrayList;
import util.*;

public class HepGraphGenerator {

    int numOfNodes = 15233;
 
    //Adjacent lists
    ArrayList<Node> nodeList = new ArrayList<Node>();

	 
	public void run(){ 
	    try  
	    {  
	        FileInputStream fstream = new FileInputStream("data/hep.txt");  
	       
	        DataInputStream in = new DataInputStream(fstream);  
	        BufferedReader br = new BufferedReader(new InputStreamReader(in));  
	        String strLine;

	        PrintWriter writer = new PrintWriter("data/hepGraph.txt", "UTF-8");
	       
	        while ((strLine = br.readLine()) != null)  
		    {
	        
	        	String[] str = strLine.split(" ");
	        	int[] values = new int[str.length];
	        	for(int i=0;i<str.length;i++){
	        		values[i] = Integer.valueOf(str[i]);
	        	}
	        	
	        	
	        	//first line show how many nodes there are
	        	if(values[0]==15233&&values[1]==58891){
	        		writer.write(strLine + "\n");
	        	}
	        	else{
	        		double probability = trivalencyModel();
	        		double delay = randomDelayModel();
	        		writer.write(strLine + " " + probability + " " + delay + "\n");
	        	}
	        	
		    }
	        
	        
	        in.close();  
	        writer.close();
	    }  
	    catch(Exception e)  
	    {  
	        System.err.println("Error: " + e.getMessage());  
	    }  
	  }

	
	//return a random value from 0.1, 0.01, 0.001.
	public double trivalencyModel(){
		//double p = 1 + (int)(Math.random()*3);
		//System.out.println(1/(Math.pow(10, p)));
		
		//double p = Math.random();
		
		//TRIVALENCY model
		//return 1/(Math.pow(10, p));
		
		//TRIVALENCY model 2
//		int p = (int)(Math.random()*3);
//		double[] choice = {0.2,0.1,0.05};
		int p = (int)(Math.random()*6);
		double[] choice = {0.3,0.25, 0.2, 0.15,0.1,0.05};
		return choice[p];
		
		//uniform IC model
		//return (double)1/100;
	}
	
	
	//delay model
	public double randomDelayModel(){
		double delay = 0+ (int)(Math.random()*10);
		return delay;
	}
	
	
	


	public static void main(String args[]){
		HepGraphGenerator hepG = new HepGraphGenerator();
		hepG.run();
		
	}

}
