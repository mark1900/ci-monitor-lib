package ci.monitor.status;

import java.util.Map;

public interface ContinuousIntegrationServer {


    public String getConnectionBaseUrl();

    public void setPrefixFilterList(String prefixFilterList);
	public String getPrefixFilterList();


	public Map<String, StatusResult> getStatusResults();

    public StatusSummary getStatusSummary();

    public void update();
    
}