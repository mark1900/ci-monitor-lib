package ci.monitor.examples;

import ci.monitor.display.BuildDisplay;
import ci.monitor.display.DisplayPanel;
import ci.monitor.display.StatusPanelBasicSkinOptions;
import ci.monitor.display.StatusPanelForCruiseControlServer;
import ci.monitor.display.StatusPanelForHudsonServer;
import ci.monitor.display.StatusTitlePanel;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.border.EtchedBorder;




public class BasicExampleDisplay extends BuildDisplay {


    public static void main(String[] args) throws Exception {
        BuildDisplay display = new BasicExampleDisplay();
        display.getInstanceProperties().setProperty("build.host.update.interval", "20000");
        display.start();
    }

    
    protected void createAndShowGui() {

		frame = new JFrame("Build Display");

        int xCount = 2;
        int yCount = 3;

		displayPanels = new DisplayPanel[xCount][yCount];

        StatusPanelBasicSkinOptions titleDisplayOptions = new StatusPanelBasicSkinOptions();
        titleDisplayOptions.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleDisplayOptions.setSize(new Dimension(100, 100));
        titleDisplayOptions.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        StatusPanelBasicSkinOptions buildDisplayOptions = new StatusPanelBasicSkinOptions();
        buildDisplayOptions.setFont(new Font("SansSerif", Font.BOLD, 256));
        buildDisplayOptions.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        displayPanels[0][0] = new StatusTitlePanel("", titleDisplayOptions);
        displayPanels[0][1] = new StatusTitlePanel("CruiseControl", titleDisplayOptions);
        displayPanels[0][2] = new StatusTitlePanel("Hudson", titleDisplayOptions);

        displayPanels[1][0] = new StatusTitlePanel("Builds", titleDisplayOptions);

	    displayPanels[1][1] = new StatusPanelForCruiseControlServer(
	    		"http://localhost:8000",
	    		null,
                buildDisplayOptions);

	    displayPanels[1][2] = new StatusPanelForHudsonServer(
	    		"http://localhost:8080",
	    		null,
	    		null,
                buildDisplayOptions);

	    setDefaultFrameLayoutForDisplayPanels(frame, displayPanels);

		setDefaultFrameDisplayBehaviour(frame);

		frame.setVisible(true);

	}

}
