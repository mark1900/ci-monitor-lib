package ci.monitor.display;



import ci.monitor.status.hudson.HudsonJobStatusViaHttp;

public class StatusPanelForHudsonServer extends StatusPanelForContinuousIntegrationServerWithStandardDisplay {

    public StatusPanelForHudsonServer(String connectionBaseUrl, String view, String prefixFilterList,
                StatusPanelBasicSkinOptions referenceDisplayOptions){

        super();
        
        final HudsonJobStatusViaHttp ciHudsonServer = new HudsonJobStatusViaHttp(connectionBaseUrl);
        ciHudsonServer.setView(view);
		ciHudsonServer.setPrefixFilterList(prefixFilterList);
        
		setContinuousIntegrationServer(ciHudsonServer);
        
        StatusPanelBasicSkinOptions newDisplayOptions = null;
        if (null == referenceDisplayOptions) {
            newDisplayOptions = new StatusPanelBasicSkinOptions();
        }else{
            newDisplayOptions = (StatusPanelBasicSkinOptions)referenceDisplayOptions.clone();
        }
        
        if (null == newDisplayOptions.getBrowserUrl()) {
            newDisplayOptions.setBrowserUrl(ciHudsonServer.getConnectionBaseUrl());
        }

        setSimpleStatusPanelDisplayOptions(newDisplayOptions);
    }

}
