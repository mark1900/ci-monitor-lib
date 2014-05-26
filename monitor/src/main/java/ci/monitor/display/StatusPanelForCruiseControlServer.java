package ci.monitor.display;

import ci.monitor.status.cruisecontrol.CruiseControlStatusViaHttp;

public class StatusPanelForCruiseControlServer extends StatusPanelForContinuousIntegrationServerWithStandardDisplay {
	
	public StatusPanelForCruiseControlServer(
            String connectionBaseUrl, String prefixFilterList,
            StatusPanelBasicSkinOptions referenceDisplayOptions){

        super();

        final CruiseControlStatusViaHttp ciCruiseControlServer = new CruiseControlStatusViaHttp(connectionBaseUrl);
		ciCruiseControlServer.setPrefixFilterList(prefixFilterList);

        setContinuousIntegrationServer(ciCruiseControlServer);

        StatusPanelBasicSkinOptions newDisplayOptions = null;
        if (null == referenceDisplayOptions) {
            newDisplayOptions = new StatusPanelBasicSkinOptions();
        }else{
            newDisplayOptions = (StatusPanelBasicSkinOptions)referenceDisplayOptions.clone();
        }

        if (null == newDisplayOptions.getBrowserUrl()) {
            newDisplayOptions.setBrowserUrl(ciCruiseControlServer.getConnectionBaseUrl());
        }
        
        setSimpleStatusPanelDisplayOptions(newDisplayOptions);
		
	}

}
