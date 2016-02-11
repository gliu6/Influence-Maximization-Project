package util;

public class Cover {

	//the covered nodeID
	private int nodeID;
	
	//how many times the node got covered.
	private int times;

	public Cover() {
		super();
		// TODO Auto-generated constructor stub
		nodeID = 0;
		times = 0;
	}
	
	public Cover(int id, int times) {
		// TODO Auto-generated constructor stub
		this.nodeID = id;
		this.times = times;
	}
	
	@Override
    public boolean equals(Object obj) {   
            if (obj instanceof Cover) {   
            	Cover u = (Cover) obj;   
                if(this.nodeID==u.getNodeID())
                	return true;
            }   
            return super.equals(obj);  
    }
	
	
	

	public int getNodeID() {
		return nodeID;
	}

	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}
	
}
