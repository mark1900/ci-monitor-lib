package ci.monitor.status.cruisecontrol;

import ci.monitor.remote.HttpContentGrabber;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import ci.monitor.status.ContinuousIntegrationServer;
import ci.monitor.status.StatusCode;
import ci.monitor.status.StatusResult;
import ci.monitor.status.StatusSummary;
import java.util.Iterator;

public class CruiseControlStatusViaHttp implements ContinuousIntegrationServer {

	private static final Logger logger = Logger.getLogger(CruiseControlStatusViaHttp.class);

    String connectionBaseUrl = null;

    String prefixFilterList = null;

    List<Project> fetchedProjects = null;


	public CruiseControlStatusViaHttp() {
        connectionBaseUrl = "http://localhost:8000";
	}

	public CruiseControlStatusViaHttp(String connectionBaseUrl) {
		this.connectionBaseUrl = connectionBaseUrl;
	}

    public String getConnectionBaseUrl() {
        return connectionBaseUrl;
    }

    public String getPrefixFilterList() {
		return prefixFilterList;
	}

	public void setPrefixFilterList(String prefixFilterList) {
		this.prefixFilterList = prefixFilterList;
	}

	protected synchronized List<Project> getProjects(boolean cached) {

        if (cached == true && null != fetchedProjects){
            return fetchedProjects;
        }else{
            fetchedProjects = new ArrayList<Project>();
        }

        
		String [] projectsNames = getProjectNames();

		for (String projectName : projectsNames) {
			Project p = getProjectStatus(projectName);
			fetchedProjects.add(p);
		}

		return fetchedProjects;
	}

	protected String[] getProjectNames() {
		
        String url = new String(getConnectionBaseUrl() + "/serverbydomain");

		StringBuffer htmlContent = HttpContentGrabber.getUrlContents(url);

        List<String> results = new ArrayList<String>();

		Pattern pattern = Pattern.compile("CruiseControl Project:name=(.*?)<",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

        Matcher matcher = pattern.matcher(htmlContent);

		while (matcher.find()) {
			results.add(matcher.group(1));
		}

		return results.toArray(new String[0]);
	}

	private Project getProjectStatus(String projectName) {
		
		Project project = new Project();
        project.setName(projectName);
		String url = new String(getConnectionBaseUrl()
					+ "/mbean?objectname=CruiseControl+Project:name=" + projectName);
		
		StringBuffer htmlContent = HttpContentGrabber.getUrlContents(url);
        
        String lastBuild = getCruiseControlProjectValueFromHtml("LastBuild", htmlContent);
        project.setLastBuild(lastBuild);

        boolean lastBuildSuccessful = false;

        //  returns true or false ("" if parameter does not exist)
        lastBuildSuccessful = Boolean.parseBoolean(getCruiseControlProjectValueFromHtml("LastBuildSuccessful", htmlContent));

        // Make this compatible with older versions of CruiseControl
        if (lastBuildSuccessful){
            project.setLastSuccessfulBuild(lastBuild);
        }else{
            project.setLastSuccessfulBuild(getCruiseControlProjectValueFromHtml("LastSuccessfulBuild", htmlContent));
        }

		project.setStatus(getCruiseControlProjectValueFromHtml("Status", htmlContent));

		return project;

	}
	
	private String getCruiseControlProjectValueFromHtml(String attribute, StringBuffer htmlContent){

        final Pattern patternForTableRow = Pattern.compile(
                "<tr .*?>(.*?)</tr>",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

        final Pattern patternForTableData = Pattern.compile(
                ""
                + ".*?<td .*?>(" + attribute + ")</td>"
                + ".*?<td .*?>(.*?)</td>"
                + ".*?<td .*?>(.*?)</td>"
                + ".*?<td .*?>(.*?)</td>"
                + ".*?<td .*?>(.*?)</td>",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);


        Matcher matcherForTableRow = patternForTableRow.matcher(htmlContent.toString());

        String valueForTableRow = "";
        while (matcherForTableRow.find()) {
			valueForTableRow = matcherForTableRow.group(1);
            
            Matcher matcherForTableData = patternForTableData.matcher(valueForTableRow);

            if (matcherForTableData.find()) {
                return matcherForTableData.group(4);
            }
		}
		
		return null;
		
	}


    public Map<String, StatusResult> getStatusResults(){
		Map<String, StatusResult> results = new HashMap <String, StatusResult>();

		List <Project> projects = getProjects(true);

		for (Project project : projects) {
			results.put(project.getName(), project.getContinuousIntegrationStatusResult());
		}

		return results;
	}

    
	public StatusSummary getStatusSummary(){

		StatusSummary ccStatusSummary = new StatusSummary();


		try{
			String [] buildTargetPrefixList = null;

			if (null == prefixFilterList || "*".equals(prefixFilterList)){
				buildTargetPrefixList = new String[0];
			}else{
				String [] buildTargetPrefixListItems = prefixFilterList.split(",");
				buildTargetPrefixList = new String[buildTargetPrefixListItems.length];
				for (int i = 0; i < buildTargetPrefixListItems.length; i++) {
					buildTargetPrefixList[i] = String.valueOf(buildTargetPrefixListItems[i]).trim();
				}
			}

			Map<String, StatusResult> ciResults = getStatusResults();

			projectLoop:   for (Iterator<Map.Entry<String, StatusResult>> iterator = ciResults.entrySet().iterator(); iterator.hasNext();) {
				Map.Entry<String, StatusResult> entry = (Map.Entry<String, StatusResult>)iterator.next();

				String currentProjectName = entry.getKey();

				if (buildTargetPrefixList.length > 0){

					boolean foundPrefixMatch = false;

					for (int j = 0; j < buildTargetPrefixList.length; j++) {
						if (currentProjectName.startsWith(buildTargetPrefixList[j])){
							foundPrefixMatch = true;
						}
					}

					if (!foundPrefixMatch){
						continue projectLoop;  // Skip this project
					}
				}


				StatusResult currentBuildStatusForProject = entry.getValue();

				if (StatusCode.GOOD == currentBuildStatusForProject.getCode()){
					ccStatusSummary.incrementGoodStatusCount();
				}else if (StatusCode.BAD == currentBuildStatusForProject.getCode()){
					ccStatusSummary.incrementBadStatusCount();
				}else{
					ccStatusSummary.incrementUnknownStatusCount();
				}

			}


		}catch(Exception e){
			e.printStackTrace();
			ccStatusSummary.incrementUnknownStatusCount();
		}

		return ccStatusSummary;
	}


    public synchronized void update() {
        getProjects(false);
    }

    
    public static void main(String[] args) {
        
        CruiseControlStatusViaHttp ciStatusTool = new CruiseControlStatusViaHttp();

        ciStatusTool.getProjects(true);

        List<Project> projects = ciStatusTool.getProjects(false);

        for (Project project : projects) {
            System.out.println(project);
        }

    }
}
