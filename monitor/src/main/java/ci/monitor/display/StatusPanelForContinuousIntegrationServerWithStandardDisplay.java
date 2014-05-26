package ci.monitor.display;

import ci.monitor.hook.BrowserLauncher;
import ci.monitor.status.ContinuousIntegrationServer;
import ci.monitor.status.StatusCode;
import ci.monitor.status.StatusResult;
import ci.monitor.status.StatusSummary;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class StatusPanelForContinuousIntegrationServerWithStandardDisplay extends StatusPanel{

    private JLabel timerLabel;
    private long timeAtLastPositiveStatus = System.currentTimeMillis();

	private JLabel statusSummaryCountLabel;
    private JLabel statusCommentLabel;
    
    private StatusPanelBasicSkinOptions displayOptions = null;

	public StatusPanelForContinuousIntegrationServerWithStandardDisplay(){
		super();
	}


    public void setSimpleStatusPanelDisplayOptions(final StatusPanelBasicSkinOptions referenceDisplayOptions){                
        
        if (null == referenceDisplayOptions) {
            this.displayOptions = new StatusPanelBasicSkinOptions();
        }else{
            this.displayOptions = (StatusPanelBasicSkinOptions)referenceDisplayOptions.clone();
        }

		this.setBorder(displayOptions.getBorder());
		this.setLayout(new BorderLayout());


        timerLabel = new JLabel(displayOptions.getTextDisplayForInitialState(), JLabel.CENTER);
		timerLabel.setForeground(displayOptions.getFontColor());
        Font timerFont = displayOptions.getFont().deriveFont(displayOptions.getFont().getSize() / 10.0f);
        timerLabel.setFont(timerFont);
		timerLabel.setVerticalTextPosition(JLabel.TOP);
		timerLabel.setHorizontalTextPosition(JLabel.CENTER);

		statusSummaryCountLabel = new JLabel(displayOptions.getTextDisplayForInitialState(), JLabel.CENTER);
		statusSummaryCountLabel.setForeground(displayOptions.getFontColor());
		statusSummaryCountLabel.setFont(displayOptions.getFont());
		statusSummaryCountLabel.setVerticalTextPosition(JLabel.CENTER);
		statusSummaryCountLabel.setHorizontalTextPosition(JLabel.CENTER);

        statusCommentLabel = new JLabel(displayOptions.getTextDisplayForInitialState(), JLabel.CENTER);
		statusCommentLabel.setForeground(displayOptions.getFontColor());
        Font statusCommentFont = displayOptions.getFont().deriveFont(displayOptions.getFont().getSize() / 2.0f);
        statusCommentLabel.setFont(statusCommentFont);
		statusCommentLabel.setVerticalTextPosition(JLabel.CENTER);
		statusCommentLabel.setHorizontalTextPosition(JLabel.CENTER);

        this.add(timerLabel, BorderLayout.NORTH);
		this.add(statusSummaryCountLabel, BorderLayout.CENTER);
        this.add(statusCommentLabel, BorderLayout.SOUTH);

		this.setBackground(displayOptions.getBackgroundColorForInitialState());
		this.setDoubleBuffered(true);

        final JPanel thisStatusPanel = this;

        MouseListener mouseListener = new MouseListener(){

            public void mouseClicked(MouseEvent arg0) {
                openBrowser();
            }

            public void mousePressed(MouseEvent arg0) {
                // Do nothing
            }

            public void mouseReleased(MouseEvent arg0) {
                // Do nothing
            }

            public void mouseEntered(MouseEvent arg0) {
                // Do nothing
            }

            public void mouseExited(MouseEvent arg0) {
                // Do nothing
            }

            protected void openBrowser(){

                if (null == displayOptions || null == displayOptions.getBrowserUrl()){
                    return;
                }

                BrowserLauncher.openURL(displayOptions.getBrowserUrl());

                JFrame parentFrame = getParentJFrameContainer(thisStatusPanel);
                if (null != parentFrame){
                    parentFrame.setExtendedState(parentFrame.getExtendedState() | JFrame.ICONIFIED);
                }

            }

        };

        this.addMouseListener(mouseListener);
        addMouseListenerToChildren(this, mouseListener);
    }

	public void update() {


        if (null == displayOptions){
            setSimpleStatusPanelDisplayOptions(new StatusPanelBasicSkinOptions());
        }

        if (null == getContinuousIntegrationServer()){
            return;
        }

        getContinuousIntegrationServer().update();

		StatusSummary statusSummary = getContinuousIntegrationServer().getStatusSummary();


		if (StatusCode.BAD == statusSummary.getStatusSummary()){
			this.setBackground(displayOptions.getBackgroundColorForBadState());
            timerLabel.setText(getTimerText(getContinuousIntegrationServer(), StatusCode.BAD));
			statusSummaryCountLabel.setText(String.valueOf(statusSummary.getGoodStatusCount()));
		}else if (StatusCode.UNKNOWN == statusSummary.getStatusSummary()){
			this.setBackground(displayOptions.getBackgroundColorForUnknownState());
			timerLabel.setText(getTimerText(getContinuousIntegrationServer(), StatusCode.UNKNOWN));
            statusSummaryCountLabel.setText(String.valueOf(statusSummary.getGoodStatusCount()));
		}else{
			this.setBackground(displayOptions.getBackgroundColorForGoodState());
			timerLabel.setText(getTimerText(getContinuousIntegrationServer(), StatusCode.GOOD));
            timeAtLastPositiveStatus = System.currentTimeMillis();
            statusSummaryCountLabel.setText(String.valueOf(statusSummary.getGoodStatusCount()));

		}

        statusCommentLabel.setText("<html><sup>/</sup> " + statusSummary.getTotalStatusCount() + "</html>");
	}


    protected int statusIndicatorState = 3;

    protected String getTimerText(ContinuousIntegrationServer ciServer, StatusCode statusCode){

     if (statusCode == StatusCode.GOOD){
         statusIndicatorState = (++statusIndicatorState) % 4;
         switch (statusIndicatorState){
             case 0:
                 return "-";
             case 1:
                 return "\\";
             case 2:
                 return "|";
             case 3:
                 return "/";
             default:
                 return "-";
         }
     }

     Map<String, StatusResult> statusResults = ciServer.getStatusResults();
     Date lastSuccessfulTimestamp = getOldestBuildTimestampWithStatusType(statusResults, statusCode);

     if (null == lastSuccessfulTimestamp){
         return "?  " + getTimerTextFromTime(System.currentTimeMillis() - timeAtLastPositiveStatus) + "  ?";
     }

     return getTimerTextFromTime(System.currentTimeMillis() - lastSuccessfulTimestamp.getTime());

    }

    protected String getTimerTextFromTime(long millis){

        long totalSeconds = (long)(millis / 1000.0);

       	// long days = (totalSeconds % 31557600) / 86400;
        // long hours = (totalSeconds % 86400) / 3600;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return String.format("%s%s%s",
                hours < 10 ? "0" + hours : "" + hours ,
                minutes < 10 ? ":0" + minutes : ":" + minutes ,
                seconds < 10 ? ":0" + seconds : ":" + seconds
                );

    }

    protected Date getOldestBuildTimestampWithStatusType(Map<String, StatusResult> statusResults, StatusCode statusCode){

        Date oldestBuild = null;

        for (Iterator<Map.Entry<String, StatusResult>> iterator = statusResults.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, StatusResult> entry = (Map.Entry<String, StatusResult>)iterator.next();

            String currentProjectName = entry.getKey();
            StatusResult currentProjectStatusResult = entry.getValue();

            Date currentProjectLastSuccessfulBuild = currentProjectStatusResult.getLastBuildPassed();


            if (statusCode.equals(currentProjectStatusResult.getCode())
                    && null != currentProjectLastSuccessfulBuild){


                if (null == oldestBuild){
                    oldestBuild = currentProjectLastSuccessfulBuild;
                }else if (currentProjectLastSuccessfulBuild.before(oldestBuild)){
                    oldestBuild = currentProjectLastSuccessfulBuild;
                }
            }

        }

        return oldestBuild;
    }


    protected JFrame getParentJFrameContainer(Component child){

        Component currentComponent = child;
        do{
            currentComponent = currentComponent.getParent();
        }while(null != currentComponent && !(currentComponent instanceof JFrame));

        return (JFrame)currentComponent;
    }

    protected void addMouseListenerToChildren(Container c, MouseListener l) {
        Component[] children = (Component[]) c.getComponents();
        for (int i = 0; i < children.length; i++) {
            children[i].addMouseListener(l);
            if (children[i] instanceof Container) {
                addMouseListenerToChildren((Container) children[i], l);
            }
        }
    }
}
