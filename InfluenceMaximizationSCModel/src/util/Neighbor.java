package util;


public class Neighbor {
	int nodeId;
	double weight = 0;
	double delay = 0;
	int parent = 0;
	
	
	public Neighbor() {
		super();
	}
	

	@Override
    public boolean equals(Object obj) {   
            if (obj instanceof Neighbor) {   
                Neighbor u = (Neighbor) obj;   
                if(this.nodeId==u.nodeId)
                	return true;
            }   
            return super.equals(obj);  
    }


	/**
	 * @return the nodeId
	 */
	public int getNodeId() {
		return nodeId;
	}


	/**
	 * @param nodeId the nodeId to set
	 */
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}


	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}


	/**
	 * @param weight the weight to set
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}


	/**
	 * @return the delay
	 */
	public double getDelay() {
		return delay;
	}


	/**
	 * @param delay the delay to set
	 */
	public void setDelay(double delay) {
		this.delay = delay;
	}


	/**
	 * @return the parent
	 */
	public int getParent() {
		return parent;
	}


	/**
	 * @param parent the parent to set
	 */
	public void setParent(int parent) {
		this.parent = parent;
	}
	
	
	
	
	
	
	
}
