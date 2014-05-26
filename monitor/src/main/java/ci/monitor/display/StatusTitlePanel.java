package ci.monitor.display;

import java.awt.BorderLayout;
import javax.swing.JLabel;

public class StatusTitlePanel extends StatusPanel {

    JLabel label;
    StatusPanelBasicSkinOptions displayOptions = new StatusPanelBasicSkinOptions();


    public StatusTitlePanel(String title, StatusPanelBasicSkinOptions displayOptions){

        super();
        if (null != displayOptions) {
            this.displayOptions = displayOptions;
        }else{
            displayOptions = this.displayOptions;
        }

        
        if (null != displayOptions.getSize() ) {
            this.setSize(displayOptions.getSize());
        }

        this.setLayout(new BorderLayout());

		label = new JLabel(title, JLabel.CENTER);
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
        // Doesn't change over time.
    }

}
