package ci.monitor.display;


import ci.monitor.status.ContinuousIntegrationServer;


public abstract class StatusPanel extends DisplayPanel implements Runnable {
	
	ContinuousIntegrationServer  continuousIntegrationServer = null;
	long updateInterval = 20000;


	public StatusPanel(){
	}

    public ContinuousIntegrationServer getContinuousIntegrationServer() {
        return continuousIntegrationServer;
    }

    public void setContinuousIntegrationServer(ContinuousIntegrationServer continuousIntegrationServer) {
        this.continuousIntegrationServer = continuousIntegrationServer;
    }

    public long getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(long updateInterval) {
        this.updateInterval = updateInterval;
    }


    public void run(){

        while (true) {
			try {
                Thread.sleep((long) (getUpdateInterval() * 0.10));
                this.update();
                this.revalidate();
                Thread.sleep((long) (getUpdateInterval() * 0.90));
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
    }

	public abstract void update();
}
