package ci.monitor.display;

public class HeaderPanel extends DisplayPanel {

    
    PanelBasicSkinOptions simplePanelDisplayOptions = new PanelBasicSkinOptions();

    public HeaderPanel(String header, PanelBasicSkinOptions simplePanelDisplayOptions){

        this();
        if (null != simplePanelDisplayOptions) { this.simplePanelDisplayOptions = simplePanelDisplayOptions; }
        
        displayText.setFont(simplePanelDisplayOptions.getFont());
        displayText.setForeground(simplePanelDisplayOptions.getFontColor());
        displayText.setText(header);
        displayText.setSize(simplePanelDisplayOptions.getSize());
    }

    public HeaderPanel() {
        initComponents();
    }

    public PanelBasicSkinOptions getSimplePanelDisplayOptions() {
        return simplePanelDisplayOptions;
    }

    public void setSimplePanelDisplayOptions(PanelBasicSkinOptions simplePanelDisplayOptions) {
        this.simplePanelDisplayOptions = simplePanelDisplayOptions;
    }



    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        displayText = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        displayText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        displayText.setText("?");
        displayText.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add(displayText, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel displayText;
    // End of variables declaration//GEN-END:variables

}
