package ci.monitor.display;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.util.Properties;

import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.border.EtchedBorder;

public class BuildDisplay extends Thread {

	protected DisplayPanel [][] displayPanels = null;
	
	protected Properties instanceProperties = new Properties();

	public static void main(String[] args) throws Exception {
		new BuildDisplay().start();
	}
	
	public BuildDisplay() {
		 
	}
	
	
	public void run() {

		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGui();
			}
		});

		updateInstanceProperties();

        while(null == frame || !frame.isVisible()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        for (int i = 0; i < displayPanels.length; i++) {
            for (int j = 0; j < displayPanels[i].length; j++) {
                if (null != displayPanels[i][j] && displayPanels[i][j] instanceof StatusPanel){

                    long pollingInterval = Long.parseLong(instanceProperties.getProperty("build.host.update.interval"));
                    StatusPanel statusPanel = (StatusPanel)displayPanels[i][j];
                    statusPanel.setUpdateInterval(pollingInterval);
                    new Thread(statusPanel).start();
                }
            }
        }
		
	}
	
	
	protected JFrame frame;


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

    public void setDefaultFrameLayoutForDisplayPanels(JFrame frame, DisplayPanel [][] displayPanels){

        int xCount = displayPanels.length;
        int yCount = displayPanels[0].length;


        GridBagLayout layout = new GridBagLayout();


	    frame.getContentPane().setLayout(layout);

	    GridBagConstraints c = new GridBagConstraints();


        for (int i = 0; i < xCount; i++) {
            for (int j = 0; j < yCount; j++) {

                if (null == displayPanels[i][j]){
                    continue;
                }

                if (displayPanels[i][j] instanceof StatusTitlePanel){
                    c.gridwidth = 1;
                    c.gridheight = 1;
                    c.weightx = 1;
                    c.weighty = 1;
                }else if (displayPanels[i][j] instanceof StatusPanel){
                    c.gridwidth = 1;
                    c.gridheight = 2;
                    c.weightx = 1;
                    c.weighty = 2;
                }else if (displayPanels[i][j] instanceof HeaderPanel){
                    HeaderPanel headerPanel = (HeaderPanel)displayPanels[i][j];

                    c.gridwidth = 1;
                    c.gridheight = 1;
                    c.weightx = 1;
                    c.weighty = 1;
                    c.gridwidth =  headerPanel.getSimplePanelDisplayOptions().getCellSpan() * 2;
                }

                //c.insets = new Insets(1, 1, 1, 1);
                c.gridx = i * 2;
                c.gridy = j * 2;

                c.fill = GridBagConstraints.BOTH;

                frame.getContentPane().add(displayPanels[i][j], c);

            }

        }

    }

    public void setDefaultFrameDisplayBehaviour(final JFrame frame) {

        // Set frame behavior

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //CLOSE WHEN ESCAPE KEY PRESSED
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            // close the frame when the user presses escape

            public void actionPerformed(ActionEvent e) {
                frame.setExtendedState(frame.getExtendedState() | JFrame.ICONIFIED);
            }
        };
        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        frame.getRootPane().getActionMap().put("ESCAPE", escapeAction);

        // Set how the frame is to be displayed

        frame.setUndecorated(true);
        frame.setResizable(false);

        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        device.setFullScreenWindow(frame);
        
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }
    

    protected void updateInstanceProperties() {

        Properties pastProperties = instanceProperties;
        Properties currentProperties = new Properties();

        try{
            currentProperties.load(new FileInputStream("instance.properties"));
        }catch(Exception e){
            System.out.print("Using default properties as instance.properties file not found");
        }

        Set currentPropertyKeySet = currentProperties.keySet();

        boolean changedProperties = false;
        String newline = System.getProperty("line.separator");

        String changedPropertiesString = "";

        for (Object currentPropertyKey : currentPropertyKeySet) {

            String currentProperty = currentProperties.getProperty((String) currentPropertyKey);
            String previousProperty = pastProperties.getProperty((String) currentPropertyKey);

            if (null != currentProperty && null != previousProperty && currentProperty.equals(previousProperty)) {
                // do nothing
            } else {
                changedProperties = true;
                changedPropertiesString += currentPropertyKey + "=" + currentProperty + newline;
            }

        }

        if (changedProperties) {
            System.out.println("-- listing changed properties --");
            System.out.println(changedPropertiesString);

            instanceProperties = currentProperties;
        }
        
    }


    public Properties getInstanceProperties(){
        return instanceProperties;
    }
}
