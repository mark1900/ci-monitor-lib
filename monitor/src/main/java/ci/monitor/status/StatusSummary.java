package ci.monitor.status;


public class StatusSummary {
	
	int badStatusCount = 0;
    int disabledStatusCount = 0;
	int unknownStatusCount = 0;
	int goodStatusCount = 0;
		
	public StatusSummary(){
		
	}
	
	public int getBadStatusCount() {
		return badStatusCount;
	}
	public void setBadStatusCount(int badBuildStatusProjectCount) {
		this.badStatusCount = badBuildStatusProjectCount;
	}
	
	public int incrementBadStatusCount(){
		return this.badStatusCount++;
	}

    public int getDisabledStatusCount() {
        return disabledStatusCount;
    }

    public void setDisabledStatusCount(int disabledStatusCount) {
        this.disabledStatusCount = disabledStatusCount;
    }

    public int incrementDisabledStatusCount(){
		return this.disabledStatusCount++;
	}
	
	public int getUnknownStatusCount() {
		return unknownStatusCount;
	}
	public void setUnknownStatusCount(int unknownBuildStatusProjectCount) {
		this.unknownStatusCount = unknownBuildStatusProjectCount;
	}
	
	public int incrementUnknownStatusCount(){
		return this.unknownStatusCount++;
	}
	
	public int getGoodStatusCount() {
		return goodStatusCount;
	}
	public void setGoodStatusCount(int goodBuildStatusProjectCount) {
		this.goodStatusCount = goodBuildStatusProjectCount;
	}
	
	public int incrementGoodStatusCount(){
		return this.goodStatusCount++;
	}

    public int getTotalStatusCount(){
        return goodStatusCount + badStatusCount + unknownStatusCount;
    }
	
	
	public void reset(){
		goodStatusCount = 0;
		badStatusCount = 0;
        disabledStatusCount = 0;
		unknownStatusCount = 0;
	}
	
	public StatusCode getStatusSummary(){
		if (badStatusCount > 0){
			return StatusCode.BAD;
		}else if (unknownStatusCount > 0 || goodStatusCount == 0){
			return StatusCode.UNKNOWN;
		}else{
			return StatusCode.GOOD;
		}
	}

    public String toString() {
		return "StatusSummary[ code='" + String.valueOf(getStatusSummary())
                + "', badStatusCount='" + badStatusCount
                + "', disabledStatusCount='" + disabledStatusCount
                + "', unknownStatusCount='" + unknownStatusCount
                + "', goodStatusCount='" + goodStatusCount
                + "']";
	}
}
