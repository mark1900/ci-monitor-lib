package ci.monitor.status.hudson;

import ci.monitor.remote.HttpContentGrabber;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import ci.monitor.status.ContinuousIntegrationServer;
import ci.monitor.status.StatusCode;
import ci.monitor.status.StatusResult;
import ci.monitor.status.StatusSummary;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class HudsonJobStatusViaHttp implements ContinuousIntegrationServer {

    private static final Logger logger = Logger.getLogger(HudsonJobStatusViaHttp.class);

    String connectionBaseUrl = null;

	private String view;
    String prefixFilterList = null;

    List <Job> jobs = null;

	
	public HudsonJobStatusViaHttp() {
		connectionBaseUrl = "http://localhost:8080";
		view = null;
	}

	public HudsonJobStatusViaHttp(String connectionBaseUrl) {
		this.connectionBaseUrl = connectionBaseUrl;
	}

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }


    
    public String getConnectionBaseUrl() {
        return connectionBaseUrl;
    }


    public String getHudsonBrowserUrl() {

        if (null == view || view.length() == 0) {
            return getConnectionBaseUrl();
        } else {
            return getConnectionBaseUrl() + "/view/" + view;
        }
    }
    
    public String getPrefixFilterList() {
		return prefixFilterList;
	}

	public void setPrefixFilterList(String prefixFilterList) {
		this.prefixFilterList = prefixFilterList;
	}
    

	protected synchronized List<Job> getJobs(boolean cached) {
		
		if (cached == true && null != jobs){
            return jobs;
        }else{
            jobs = new ArrayList<Job>();
        }

		String url = new String(getHudsonBrowserUrl() + "/api/xml");
        
		final Pattern patternForJobs = Pattern.compile("<job><name>(.*?)</name>.*?<color>(.*?)</color>",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL );
		Matcher matcherForJobs = patternForJobs.matcher(HttpContentGrabber.getUrlContents(url));

		while (matcherForJobs.find()) {
			Job matchedJob = new Job();
			matchedJob.setName(matcherForJobs.group(1));
			matchedJob.setStatus(matcherForJobs.group(2));
			jobs.add(matchedJob);
		}

        for (Job job : jobs) {

            url = new String(getConnectionBaseUrl()
                    + "/job/" + job.getName() + "/lastCompletedBuild"
                    + "/api/xml");

            final Pattern patternForLastBuild = Pattern.compile("<timestamp>(.*?)</timestamp>",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL );
            Matcher matcherForLastBuild = patternForLastBuild.matcher(HttpContentGrabber.getUrlContents(url));

            while (matcherForLastBuild.find()) {
                String matchedValue = matcherForLastBuild.group(1);
                job.setLastCompletedBuild(new Date(Long.parseLong(matchedValue)));
            }


            url = new String(getConnectionBaseUrl()
                    + "/job/" + job.getName() + "/lastStableBuild"
                    + "/api/xml");

            final Pattern patternForLastSuccessfulBuild = Pattern.compile("<timestamp>(.*?)</timestamp>",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL );
            Matcher matcherForLastSuccessfulBuild = patternForLastSuccessfulBuild.matcher(HttpContentGrabber.getUrlContents(url));


            while (matcherForLastSuccessfulBuild.find()) {
                String matchedValue = matcherForLastSuccessfulBuild.group(1);
                job.setLastStableBuild(new Date(Long.parseLong(matchedValue)));
            }

        }
		return jobs;
	}


    public Map<String, StatusResult> getStatusResults(){
		Map<String, StatusResult> results = new HashMap <String, StatusResult>();

		List <Job> jobs = getJobs(true);

		for (Job job : jobs) {
			results.put(job.getName(), job.getContinuousIntegrationStatusResult());
		}

		return results;
	}

    public StatusSummary getStatusSummary(){

		StatusSummary ccStatusSummary = new StatusSummary();


		try{
			String [] buildTargetPrefixList = null;

			if (null == getPrefixFilterList() || "*".equals(getPrefixFilterList())){
				buildTargetPrefixList = new String[0];
			}else{
				String [] buildTargetPrefixListItems = getPrefixFilterList().split(",");
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
                }else if (StatusCode.DISABLED == currentBuildStatusForProject.getCode()){
					ccStatusSummary.incrementDisabledStatusCount();
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
        getJobs(false);
    }


	public static void main(String[] args) {

		HudsonJobStatusViaHttp ciStatusTool = new HudsonJobStatusViaHttp();

        List<Job> jobs = ciStatusTool.getJobs(false);

        for (Job job : jobs) {
            System.out.println(job);
        }
	}
}
