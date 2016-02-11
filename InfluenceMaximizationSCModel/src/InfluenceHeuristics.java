import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import util.*;

public class InfluenceHeuristics implements Runnable{

	//private static StringBuilder dataToWrite;
	 ArrayList<Node> nodeList;
	 int sizeOfSeedSet;
	 int[] seeds;
	 //create a checkList to store the potentail active nodes in order(delay).
	 LinkedList<Neighbor> checkList = new LinkedList<Neighbor>();
	 private static String method;
	 int R = 50;
	 
	 //initialize
	 public InfluenceHeuristics(int seedSize) {
		this.sizeOfSeedSet = seedSize;

//OLD WAY -random probability
//         //read hep
//         ReadHep readHepData = new ReadHep();
//         readHepData.run();
//         this.nodeList = readHepData.getNodeList();

//NEW WAY -Static probability
         //read hep
         ReadHepGraph readHepData = new ReadHepGraph();
         readHepData.run();
         this.nodeList = readHepData.getNodeList();
		
//		//read phy
//		ReadPhy readPhyData = new ReadPhy();
//		readPhyData.run();
//		this.nodeList = readPhyData.getNodeList();
		
		//read dblp
//		ReadDblp readDblpData = new ReadDblp();
//		readDblpData.run();
//		this.nodeList = readDblpData.getNodeList();
		
		//read Epinions
//		ReadEpinions readEpinionsData = new ReadEpinions();
//		readEpinionsData.run();
//		this.nodeList = readEpinionsData.getNodeList();
		
		//read Amazon
//		ReadAmazon readAmazonData = new ReadAmazon();
//		readAmazonData.run();
//		this.nodeList = readAmazonData.getNodeList();
		

		seeds = new int[sizeOfSeedSet];
		for(int i=0; i<sizeOfSeedSet; i++){
			seeds[i] = 0;
		}
		
	}
	 
	 public void startProcess(){
		 switch(method)
		 {
		 case("MaxDegreeSelectSeed"):
			 MaxDegreeSelectSeed();
			 break;
		 case("RandomSelectSeed"):
			 RandomSelectSeed();
		 	 break;
		 case("DegreeDiscountIC"):
			 DegreeDiscountIC();
		 	 break;
		 case("OriginalGreedy"):
			 OriginalGreedy();
		 	break;
		 case("improvedGreedy"):
			improvedGreedy();
		 	break;
		 case("InfluenceSpread"):
			 InfluenceSpread();
		 	break;
		 }
	 }
	 
	 
	 //a set S with k seeds with maximum degree
	 public void MaxDegreeSelectSeed(){
		 for(int i=0; i<sizeOfSeedSet; i++){
			 int maxDegree = 0;
			 int tempID = 0;
			 for(Node n: nodeList){
				 if(!n.isSeed()&&n.getNeighborList().size()>maxDegree){
					 maxDegree = n.getNeighborList().size();
					 tempID = n.getNodeID(); 
				 }
			 }
			 seeds[i] = tempID;
			 nodeList.get(tempID).setSeed(true);
			 nodeList.get(tempID).setActive(true);
			 //System.out.println("Seed: " + i + " : " + tempID + " degree: " + nodeList.get(tempID).getNeighborList().size());
			 //output test
			 //System.out.println("Seed : " + i + " " + seeds[i]);
		 }
	 }
	 
	 //random select
	 public void RandomSelectSeed(){
		 for(int i=0; i<sizeOfSeedSet; i++){
			 int tempID = (int)(Math.random()*(nodeList.size()));
			 while(nodeList.get(tempID).isSeed()){
				 tempID = (int)(Math.random()*(nodeList.size()));
			 }
			 seeds[i] = tempID;
			 nodeList.get(tempID).setSeed(true);
			 nodeList.get(tempID).setActive(true);
			 //System.out.println("Seed: " + i + " : " + tempID + " degree: " + nodeList.get(tempID).getNeighborList().size());
			 //System.out.println("seed: " +  i + " "+ tempID );
		 }
	 }

	 //Degree Discount IC
	 public void DegreeDiscountIC(){
		 for(Node n:nodeList){
			 n.setDegree(n.getNeighborList().size());
			 n.setActiveNeighbors(0);
		 }
		 
		 for(int i=0; i<sizeOfSeedSet; i++){
			 int tempID = MaxCurrentDegree();
			 seeds[i] = tempID;
			 nodeList.get(tempID).setSeed(true);
			 nodeList.get(tempID).setActive(true);
			 //System.out.println("Seed: " + i + " : " + tempID + " degree: " + nodeList.get(tempID).getNeighborList().size() + " dd: " +  nodeList.get(tempID).getDegree());
			 DegreeDiscountProcess(tempID);
		 }
	 }
	 
	 public int MaxCurrentDegree(){
		 int tempID = 0;
		 int tempDegree = 0;
		 for(Node n:nodeList){
			 if(!n.isSeed()&&n.getDegree() > tempDegree){
				 tempDegree = n.getDegree();
				 tempID = n.getNodeID();
			 }
		 }
		 return tempID;
	 }
	 
	 public void DegreeDiscountProcess(int nodeId){
		 for(Neighbor e: nodeList.get(nodeId).getNeighborList()){
			 Node n = nodeList.get(e.getNodeId());
			 if(!n.isSeed()){
				 n.setActiveNeighbors(n.getActiveNeighbors()+1);
				 n.setDegree((int)(n.getNeighborList().size() - 2 * n.getActiveNeighbors() -(n.getNeighborList().size() -  n.getActiveNeighbors())* n.getActiveNeighbors()*n.getNeighborList().get(0).getWeight()));
			 }
		 }
	 }
	 
	 public void improvedGreedy(){
		 //first round, calculate the avg coverage and and largest coverage
//		 dataToWrite.append("Program output: \n");
		 
		 
		 //add a pre-filter to do only top 10% of the seeds
		 ArrayList<Node> filterList = new ArrayList<Node>();
//		 filterList.addAll(nodeList);
		 
		
		 
		 for(Node n: nodeList){
			n.setDegree(n.getNeighborList().size());
		 }
		 
		 //find top 100 nodes to further calculate coverage. Pre-filter
		 int threshold = 300;
		 for(int i=0; i<threshold; i++){
			 int maxDegree = 0;
			 Node tempNode = null;
			 for(Node n: nodeList){
				 if(n.getDegree()>maxDegree){
					 maxDegree = n.getNeighborList().size();
					 tempNode = n;
				 }
			 }
			 filterList.add(tempNode);
			 tempNode.setDegree(0);
		 }
		 
		 
		 
		 
		 //output test
		 System.out.println("filterList.size: " + filterList.size() + " nodeList.size: " + nodeList.size());
		 
		 for(Node n: filterList){
			 coverageCalculation(n);
//			 dataToWrite.append(n.getNodeID() + " covers all: " + n.getCoverage().size()+ "\n");
//			 dataToWrite.append(n.getNodeID() + " covers avg: " + n.getAvgCoverage().size()+ "\n");
//			 dataToWrite.append(n.getNodeID() + " covers max: " + n.getMaxCoverage().size()+ "\n");
//			 System.out.println(n.getNodeID() + " covers all: " + n.getCoverage().size());
//			 System.out.println(n.getNodeID() + " covers avg: " + n.getAvgCoverage().size());
//			 System.out.println(n.getNodeID() + " covers max: " + n.getMaxCoverage().size());
		 }
		 
		 for(int i=0; i<sizeOfSeedSet; i++){
			 int tempID = 0;
			 tempID = maxCover(i, filterList);
			 seeds[i] = tempID;
			 nodeList.get(tempID).setSeed(true);
			 //dataToWrite.append("Seed: " + i + " : " + tempID + " degree: " + nodeList.get(tempID).getNeighborList().size() + "\n");
			 //System.out.println("Seed: " + i + " : " + tempID + " degree: " + nodeList.get(tempID).getNeighborList().size());
			 //Spread(tempID);
		 }
		 clearActive();
		 clearTempActive();
		 clearCurrentInfluence();
		 for(int i=0; i<sizeOfSeedSet; i++){
			 nodeList.get(seeds[i]).setSeed(true);
			 nodeList.get(seeds[i]).setActive(true);
		 }
	 }
	 
	 public int maxCover(int num, ArrayList<Node> filterList){
		 
		 ArrayList<Cover> CoveredList = new ArrayList<Cover>();
		 for(int i = 0; i <num; i++){
			 Node n = nodeList.get(seeds[i]);
			 for(Cover c: n.getCoverage()){
				 if(!CoveredList.contains(c)){
					 CoveredList.add(c);
				 }else{
					 int temp = CoveredList.indexOf(c);
					 Cover co = CoveredList.get(temp); 
					 int times = (int) ((1-(1-c.getTimes()*1.0/R)*(1-co.getTimes()*1.0/R))*R);
					 co.setTimes(times);
				 }
			 }
		 }
		 
		 //do some statistics
		 double avgThreshold = 0.8; //better set up a little higher, cause the are overlapping
		 double maxThreshold = 0.1;
		 
		 ArrayList<Cover> avgCoveredList = new ArrayList<Cover>();
		 ArrayList<Cover> maxCoveredList = new ArrayList<Cover>();
		 for(Cover c: CoveredList){
			 if(c.getTimes()*1.0/R > avgThreshold){
				 avgCoveredList.add(c);
			 }
			 if(c.getTimes()*1.0/R > maxThreshold && c.getTimes()*1.0/R < avgThreshold){
				 maxCoveredList.add(c);
			 }
		 }
		 
		 double maxGain = 0;
		 int nodeId = 0;
		 for(Node n: filterList){
			 if(!n.isSeed()){
				 double gains = gain(avgCoveredList, maxCoveredList, n);
				 if(gains > maxGain){
					 maxGain = gains;
					 nodeId = n.getNodeID();
				 }
			 }
		 }
		 //output test
//		 System.out.println("avgCoveredList: " + avgCoveredList.size() + " maxCoveredList: " + maxCoveredList.size());
//		 System.out.println("nodeId: " + nodeId + " maxGain: " +maxGain );
		 
		 
		 return nodeId;
	 }
	 
	 public double gain(ArrayList<Cover> avgCoveredList, ArrayList<Cover> maxCoveredList,  Node n){
		 double counter = 0;
		 for(Cover c: n.getAvgCoverage()){
			 if(!avgCoveredList.contains(c)){
				 counter += c.getTimes()*1.0/R;
			 }
//			 else{
//				 Cover co = avgCoveredList.get(avgCoveredList.indexOf(c));
//				 counter += (1-(1-c.getTimes()*1.0/R)*(1-co.getTimes()*1.0/R)-co.getTimes()*1.0/R);
//			 }
		 }
		 for(Cover c: n.getMaxCoverage()){
			 if(maxCoveredList.contains(c)){
				 int temp = maxCoveredList.indexOf(c);
				 Cover co = maxCoveredList.get(temp);
				 co.setTimes(co.getTimes()+c.getTimes());
//				 if(co.getTimes()>=R){
//					 co.setTimes(R);
//				 }
				 counter = counter + (1-(1-c.getTimes()*1.0/R)*(1-co.getTimes()*1.0/R)-co.getTimes()*1.0/R);
			 }
		 }
		 
		 return counter;
	 }
	 
	 //calculate the node's coverage
	 public void coverageCalculation(Node n){
		 //new spread which considers the time delay
		 //add the checkList, when checkList is empty, program ends.
		 
		 
		 for(int i = 0; i< R; i++){
			 clearTempActive();
			 checkList.clear();
			 for(Neighbor e: n.getNeighborList()){
				 if(!nodeList.get(e.getNodeId()).isTempActive()&&!nodeList.get(e.getNodeId()).isActive()){
					 insertIntoWaitingList(e);
				 }
			 }
			 
			 while(checkList.size()!=0){
				 //get first one in the waiting list and remove it and adjust others waiting time
				 Neighbor e = checkList.getFirst();
				 checkList.removeFirst();
				 adjustWaitingListDelay(e.getDelay());
				 Node child = nodeList.get(e.getNodeId());
				 if(e.getWeight() >= child.getCurrentInfluence()){
					 //try to active the node
					 double currentInfluence = 1-(1-e.getWeight())*(1-child.getCurrentInfluence());
					 child.setCurrentInfluence(currentInfluence);
					 double r = Math.random();
					 if(r<=currentInfluence){
						 //node is activated
						 nodeList.get(e.getNodeId()).setTempActive(true);
						 //add cover times
						 //if exist, times+1, if not, create new cover
						 Cover c = new Cover(e.getNodeId(), 1);
						 if(!n.getCoverage().contains(c)){
							 n.getCoverage().add(c);
						 }
						 else{
							 int temp = n.getCoverage().indexOf(c);
							 Cover co = n.getCoverage().get(temp);
							 co.setTimes(co.getTimes()+1);
						 }
						 //now try to insert its children into the waiting list
						 for(Neighbor ne: nodeList.get(e.getNodeId()).getNeighborList()){
							 if(!nodeList.get(ne.getNodeId()).isTempActive()){
								 insertIntoWaitingList(ne);
							 }
						 }
	//					 System.out.println("checkList.size: " + checkList.size());
					 }
				 }
				 //else do nothing, even not give a try.
			 }
		 }
		 
		 //do some statistics
		 double avgThreshold = 0.6;
		 double maxThreshold = 0.1;
		 for(Cover c: n.getCoverage()){
			 if(c.getTimes()*1.0/R > avgThreshold){
				 n.getAvgCoverage().add(c);
			 }
			 if(c.getTimes()*1.0/R > maxThreshold && c.getTimes()*1.0/R < avgThreshold){
				 n.getMaxCoverage().add(c);
			 }
		 }
	 }
	 
	 //original greedy
	 public void OriginalGreedy(){
		 for(int i=0; i<sizeOfSeedSet; i++){
			 int tempID = 0;
			 tempID = MaxIncreaseSpread(i);
			 seeds[i] = tempID;
			 nodeList.get(tempID).setSeed(true);
			 //System.out.println("Seed: " + i + " : " + tempID + " degree: " + nodeList.get(tempID).getNeighborList().size());
			 //Spread(tempID);
		 }
		 clearActive();
		 for(int i=0; i<sizeOfSeedSet; i++){
			 nodeList.get(seeds[i]).setSeed(true);
			 nodeList.get(seeds[i]).setActive(true);
		 }
	 }
	 
	 
	 public int MaxIncreaseSpread(int number){
		 int tempID = 0;
		 int repetition = 10;
//		 int repetition = R;
		 long spreadNum = 0;
		 for(Node n: nodeList){
			 if(!n.isSeed()){
				 long temp = 0;
				 for(int i=0; i<repetition; i++){
					 clearTempActive();
					 temp += InfluenceSpreadTrial(n.getNodeID(), number);
				 }
				 temp = temp / repetition;
				 if(temp > spreadNum){
					 tempID = n.getNodeID();
					 spreadNum = temp;
				 }
			 }
		 }
		 System.out.println("spreadNum: " + spreadNum);
		 return tempID;
	 }
	 
	 public int InfluenceSpreadTrial(int nodeID, int num){
//		 for(int i=0; i<num; i++){
//			 SpreadTrial(seeds[i]);
//		 }
//		 SpreadTrial(nodeID);

		 
		 //new sread which considers the time delay
		 //add the checkList, when checkList is empty, program ends.
		 checkList.clear();
		 for(int i=0; i<num; i++){
			 for(Neighbor e: nodeList.get(seeds[i]).getNeighborList()){
				 if(!nodeList.get(e.getNodeId()).isTempActive()&&!nodeList.get(e.getNodeId()).isActive()){
					 insertIntoWaitingList(e);
				 }
			 }
		 }
		 for(Neighbor e: nodeList.get(nodeID).getNeighborList()){
			 if(!nodeList.get(e.getNodeId()).isTempActive()&&!nodeList.get(e.getNodeId()).isActive()){
				 insertIntoWaitingList(e);
			 }
		 }
		 
		 
		 //output test
//		 System.out.println("checkList.size: " + checkList.size());
		 
		 while(checkList.size()!=0){
			 //get first one in the waiting list and remove it and adjust others waiting time
			 Neighbor e = checkList.getFirst();
			 checkList.removeFirst();
			 adjustWaitingListDelay(e.getDelay());
			 Node child = nodeList.get(e.getNodeId());
			 if(e.getWeight() >= child.getCurrentInfluence()){
				 //try to active the node
				 double currentInfluence = 1-(1-e.getWeight())*(1-child.getCurrentInfluence());
				 child.setCurrentInfluence(currentInfluence);
				 double r = Math.random();
				 if(r<=currentInfluence){
					 //node is activated
					 nodeList.get(e.getNodeId()).setTempActive(true);
					 //now try to insert its children into the waiting list
					 for(Neighbor ne: nodeList.get(e.getNodeId()).getNeighborList()){
						 if(!nodeList.get(ne.getNodeId()).isTempActive()){
							 insertIntoWaitingList(ne);
						 }
					 }
//					 System.out.println("checkList.size: " + checkList.size());
				 }
			 }
			 //else do nothing, even not give a try.
		 }
		 
		 //Statistics
		 int activeNum = 0;
		 int inactiveNum = 0;
		 for(Node n: nodeList){
			 if(n.isTempActive()&&!n.isActive()){
				 activeNum++;
			 }else{
				 inactiveNum++;
			 }
		 }
			
//		 System.out.println("activeNum: " + activeNum + " nodeId: " +nodeID);
		 
		 return activeNum;
	 }
	 
	 public void SpreadTrial(int nodeId){
		 for(Neighbor e: nodeList.get(nodeId).getNeighborList()){
			 double r = Math.random();
			 //if the neighbor is not active and r<e then activate it.
			 if(!nodeList.get(e.getNodeId()).isTempActive()&&!nodeList.get(e.getNodeId()).isActive()&&r<=e.getWeight()){
				 nodeList.get(e.getNodeId()).setTempActive(true);
				 SpreadTrial(e.getNodeId());
			 }
		 }
	 }
	 
	 public void clearActive(){
		 for(Node n: nodeList){
			 n.setActive(false);
		 }
	 }
	 
	 public void clearCurrentInfluence(){
		 for(Node n: nodeList){
			 n.setCurrentInfluence(0);
		 }
	 }
	 
	 public void clearTempActive(){
		 for(Node n: nodeList){
			 n.setTempActive(false);
			 n.setCurrentInfluence(0);
		 }
	 }
	 
	 public int InfluenceSpread(){
//		 for(int i=0; i<k; i++){
//			 Spread(seeds[i]);
//		 }
		 
		 //initialization
		 clearActive();
		 clearCurrentInfluence();
		 checkList.clear();
		 
		 //new sread which considers the time delay
		 //add the checkList, when checkList is empty, program ends.
		
		 for(int i=0; i<sizeOfSeedSet; i++){
			 for(Neighbor e: nodeList.get(seeds[i]).getNeighborList()){
				 if(!nodeList.get(e.getNodeId()).isActive()){
					 insertIntoWaitingList(e);
				 }
			 }
		 }
		 
		 //output test
//		 System.out.println("checkList.size: " + checkList.size());
		 
		 while(checkList.size()!=0){
			 //get first one in the waiting list and remove it and adjust others waiting time
			 Neighbor e = checkList.getFirst();
			 checkList.removeFirst();
			 adjustWaitingListDelay(e.getDelay());
			 Node child = nodeList.get(e.getNodeId());
			 if(e.getWeight() >= child.getCurrentInfluence()){
				 //try to active the node
				 double currentInfluence = 1-(1-e.getWeight())*(1-child.getCurrentInfluence());
				 child.setCurrentInfluence(currentInfluence);
				 double r = Math.random();
				 if(r<=currentInfluence){
					 //node is activated
					 nodeList.get(e.getNodeId()).setActive(true);
					 //now try to insert its children into the waiting list
					 for(Neighbor ne: nodeList.get(e.getNodeId()).getNeighborList()){
						 if(!nodeList.get(ne.getNodeId()).isActive()){
							 insertIntoWaitingList(ne);
						 }
					 }
					 //System.out.println("checkList.size: " + checkList.size());
				 }
			 }
			 //else do nothing, even not give a try.
		 }
		 
		 
		 //Statistics
		 int activeNum = 0;
		 int inactiveNum = 0;
		 for(Node n: nodeList){
			 if(n.isActive()){
				 activeNum++;
			 }else{
				 inactiveNum++;
			 }
		 }
		 
		 double ratio = (double)activeNum/(activeNum + inactiveNum);
		// System.out.println("Spread: " + activeNum);
		 return activeNum;
	 }
	 
	 //insert new node into the waiting list
	 public void insertIntoWaitingList(Neighbor e){
		 if(checkList.size()==0){
			 checkList.add(e);
		 }else {
			 //insert if the new edge e has a smaller delay
			 boolean inserted = false;
			 for(Neighbor ee: checkList){
				 //insert e
				 if(e.getDelay()<ee.getDelay()){
					 checkList.add(checkList.indexOf(ee), e);
					 inserted = true;
					 break;
				 }
			 }
			 //else, add to the last of the waiting list
			 if(!inserted){
				 checkList.addLast(e);
			 }
		 }
	 }
	 
	 //adjust waiting list's delay
	 public void adjustWaitingListDelay(double elapsed){
		 for(Neighbor n: checkList){
			n.setDelay(n.getDelay()-elapsed); 
		 }
	 }
	 
	 
	 
	 public void Spread(int nodeId){
		 for(Neighbor e: nodeList.get(nodeId).getNeighborList()){
			 double r = Math.random();
			 //if the neighbor is not active and r<e then activate it.
			 if(!nodeList.get(e.getNodeId()).isActive()&&r<=e.getWeight()){
				 nodeList.get(e.getNodeId()).setActive(true);
				 Spread(e.getNodeId());
			 }
		 }
	 }
	 
	 public static void main(String args[]){
		 int startSeedSize = 1;
		 int maxSeedSize  = 36;
		 int incrementSize = 5;
		 
		 String[] methods = {
              "MaxDegreeSelectSeed"
			 , "RandomSelectSeed"
             , "DegreeDiscountIC"
			 , "improvedGreedy"
			 , "OriginalGreedy"
            };

		 //Thread pool
		 ArrayList<Thread> threads = new ArrayList<>(); 
		 
		 //Run all methods
		 for(String m : methods)
		 {
			 method = m;
//		 	 method = "improvedGreedy";
			 System.out.println("Starting method: " + method);
			 try
			 {
				 //test
				 System.out.println("available processor number: " + Runtime.getRuntime().availableProcessors() );
				 
				 //Setup the threads 
				 for(int seedSetSize = startSeedSize; seedSetSize <= maxSeedSize; seedSetSize += incrementSize)
					 threads.add(new Thread(new InfluenceHeuristics(seedSetSize)));
			 
				//Sanity Check
				 if(threads.size() > Runtime.getRuntime().availableProcessors())
					 throw new Exception("Number of threads is greater than number of cores!");	 

				 //Start all threads
				 for(Thread t : threads)
					 t.start();
				 
				 //Wait for all threads to complete
				 for(Thread t : threads)
					 t.join();
				 
				 //Clear the thread pool
				 for(Thread t : threads)
					 t.stop();
				 threads.clear();

			 }
			 catch (Exception e)
			 {
				 System.out.println("Thread error: " + e);
			 }
		 }
	 }
	 
	 public void run()
	 {
		 long startTime = System.currentTimeMillis();

		 //clearFile("logFile_" + method + ".FINAL.csv");
		 //int sizeOfSeedSet = 30;
		 int runCount = 1;
		 int reps = 100;
		 long totalSpread = 0;
		 long totalTime = 0;
		 
		 for(int counter = 0; counter < runCount; counter++)
		 { 
			 if(counter % 5 == 0)
				 System.out.println("Run count for Thread " + (Thread.currentThread().getId()-8) + " running " + method + " = " + counter);
			 //String logFile = "logFile_" + method + "seedCount-" + sizeOfSeedSet + "_runNumber-" + counter + ".txt";
			 int spread = 0;
			 
			 //dataToWrite = new StringBuilder("Run Count = " + runCount + "\n Start Time in milliseconds is: " + startTime +"\n");
			 //InfluenceHeuristics g = new InfluenceHeuristics(sizeOfSeedSet);
			 this.startProcess();
			 for(int i = 0; i<reps; i++){
				int temp = this.InfluenceSpread();
				spread += temp;
				System.out.println("rep round: " + i + " spread: " + temp);
			 } 
			 spread = spread/reps;
			 totalSpread += spread;
			 totalTime += System.currentTimeMillis() - startTime;
			 //dataToWrite.append("Influence Spread: " + spread + "\n");
			 //dataToWrite.append("Time for " + sizeOfSeedSet + " seeds is " + (System.currentTimeMillis() - startTime )+ " milliseconds\n");
			 //writeToFile(logFile, dataToWrite.toString());
		 }
		 
		 System.out.println("Thread " + (Thread.currentThread().getId()-8) + " is DONE with " + method + ".  It took: " + (totalTime/runCount)/1000 + " seconds");
		 
		 String fileName = "OneRun_logFile_" + method + ".FINAL.csv";
		 String content = ("\n\n\nMethod: " + method + ", Size of seed set, " + sizeOfSeedSet + ", run count: " + runCount + "\nTotal (avg) Spread for all runs, " + totalSpread/runCount) + "\r\nTotal (avg) time for all runs, " + totalTime/runCount;
		 writeToFile(fileName, content, true);
	 }
	 
	 
	 private static boolean writeToFile(String fileName, String dataToWrite, boolean append)
	 {
		 try (FileWriter fr = new FileWriter(new File(fileName), append))
			{
				BufferedWriter writer = new BufferedWriter(fr);
				
				writer.write(dataToWrite);
				
				writer.close();
				return true;
			}
			catch(IOException e)
			{
				System.out.println("File Exception: " + e);
				return false;
			}
	 }
	 
	 private static boolean writeToFile(String fileName, String dataToWrite)
	 {
			return writeToFile(fileName, dataToWrite, false);
	 }
	 
	 private boolean clearFile(String fileName)
	 {
		 return writeToFile(fileName, "", false);
	 }
	 
	 
}
