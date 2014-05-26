package ci.monitor.status.hudson;

import ci.monitor.status.StatusCode;
import ci.monitor.status.StatusResult;
import java.util.Date;


public class Job {
	
	String name;
	String status;

    Date lastCompletedBuild;
    Date lastStableBuild;


	public Job(){
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

    public Date getLastCompletedBuild() {
        return lastCompletedBuild;
    }

    public void setLastCompletedBuild(Date lastCompletedBuild) {
        this.lastCompletedBuild = lastCompletedBuild;
    }

    public Date getLastStableBuild() {
        return lastStableBuild;
    }

    public void setLastStableBuild(Date lastStableBuild) {
        this.lastStableBuild = lastStableBuild;
    }
	
	
	public StatusResult getContinuousIntegrationStatusResult() {

        StatusResult statusResult = new StatusResult();
        
        statusResult.setLastBuildAttempted(lastCompletedBuild);
        statusResult.setLastBuildPassed(lastStableBuild);

		if ("blue".equals(status) || "blue_anime".equals(status)){
			statusResult.setCode(StatusCode.GOOD);
		}else if ("red".equals(status)  || "red_anime".equals(status)){
			statusResult.setCode(StatusCode.BAD);
        }else if ("disabled".equals(status)){
			statusResult.setCode(StatusCode.DISABLED);
		}else{
			statusResult.setCode(StatusCode.UNKNOWN);
		}

        return statusResult;
	}
    
    public String toString() {
		return "Job[ name='" + name
                + "', lastCompletedBuild='" + lastCompletedBuild
                + "', lastStableBuild='" + lastStableBuild 
                + "', statusResult='" + String.valueOf(getContinuousIntegrationStatusResult())
                + "']";
	}
	
}
