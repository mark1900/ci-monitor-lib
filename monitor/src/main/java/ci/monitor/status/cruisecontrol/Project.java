package ci.monitor.status.cruisecontrol;

import ci.monitor.status.StatusCode;
import ci.monitor.status.StatusResult;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;


public class Project {

    private static final Logger logger = Logger.getLogger(Project.class);

    private static final SimpleDateFormat CC_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");


    private String name;
    private String status;

	private Date lastSuccessfulBuild;
	private Date lastBuild;
    

	public Project() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStatus(String newStatus) {

        if (null == newStatus){
            status = null;
        }else if (newStatus.startsWith("waiting") || newStatus.equalsIgnoreCase("Build passed")){
			status = "waiting";
        }else{
            status = newStatus;
        }
	}

	public String getStatus() {
		return status;
	}

	public Date getLastBuild() {
        return lastBuild;
	}

	public void setLastBuild(String lastBuildDateValue) {

        if (null == lastBuildDateValue){
            lastBuild = null;
            return;
        }

        Date date = null;
        try {
            date = CC_DATE_FORMAT.parse(lastBuildDateValue);
        } catch (ParseException ex) {
            logger.error("Failed to parse date", ex);
        }

        lastBuild = date;
	}

	public Date getLastSuccessfulBuild() {
        return lastSuccessfulBuild;
	}

	public void setLastSuccessfulBuild(String lastSuccessfullBuildDateValue) {

        if (null == lastSuccessfullBuildDateValue){
            lastSuccessfulBuild = null;
            return;
        }

        Date date = null;
        try {
            date = CC_DATE_FORMAT.parse(lastSuccessfullBuildDateValue);
        } catch (ParseException ex) {
            logger.error("Failed to parse date", ex);
        }

        lastSuccessfulBuild = date;
	}
    
	public StatusResult getContinuousIntegrationStatusResult() {

        StatusResult statusResult = new StatusResult();
        
        statusResult.setLastBuildAttempted(getLastBuild());
        statusResult.setLastBuildPassed(getLastSuccessfulBuild());

		statusResult.setCode(StatusCode.BAD);
		if (getLastSuccessfulBuild() == null
				&& getLastBuild() == null) {
			statusResult.setCode(StatusCode.UNKNOWN);
		} else if (lastBuild.equals(lastSuccessfulBuild)) {
			statusResult.setCode(StatusCode.GOOD);
		}

		return statusResult;
	}

    
    public String toString() {
		return "Project[ name='" + name
                + "', lastBuild='" + lastBuild
                + "', lastSuccessfulBuild='" + lastSuccessfulBuild
                + "', statusResult='" + String.valueOf(getContinuousIntegrationStatusResult())
                + "']";
	}

}
