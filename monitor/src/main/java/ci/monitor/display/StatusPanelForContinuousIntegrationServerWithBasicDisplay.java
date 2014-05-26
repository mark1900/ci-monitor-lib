

package ci.monitor.display;

import ci.monitor.status.StatusCode;
import ci.monitor.status.StatusSummary;
import java.awt.BorderLayout;
import javax.swing.JLabel;


public class StatusPanelForContinuousIntegrationServerWithBasicDisplay extends StatusPanel {
    
    private JLabel label;
    private StatusPanelBasicSkinOptions displayOptions = null;


	public StatusPanelForContinuousIntegrationServerWithBasicDisplay(){
		super();
	}

    public void setSimpleStatusPanelDisplayOptions(StatusPanelBasicSkinOptions referenceDisplayOptions){

        if (null == referenceDisplayOptions) {
            this.displayOptions = new StatusPanelBasicSkinOptions();
        }else{
            this.displayOptions = (StatusPanelBasicSkinOptions)referenceDisplayOptions.clone();
        }

		this.setLayout(new BorderLayout());

		label = new JLabel(displayOptions.getTextDisplayForInitialState(), JLabel.CENTER);
		label.setForeground(displayOptions.getFontColor());
		label.setFont(displayOptions.getFont());
		label.setVerticalTextPosition(JLabel.CENTER);
		label.setHorizontalTextPosition(JLabel.CENTER);
        label.setBorder(displayOptions.getBorder());
		this.add(label, BorderLayout.CENTER);

		this.setBackground(displayOptions.getBackgroundColorForInitialState());
		this.setDoubleBuffered(true);
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
			label.setText(String.valueOf(statusSummary.getBadStatusCount()));
		}else if (StatusCode.UNKNOWN == statusSummary.getStatusSummary()){
			this.setBackground(displayOptions.getBackgroundColorForUnknownState());
			label.setText(String.valueOf(statusSummary.getUnknownStatusCount()));
		}else{
			this.setBackground(displayOptions.getBackgroundColorForGoodState());
			label.setText(String.valueOf(statusSummary.getGoodStatusCount()));
		}

	}
}
