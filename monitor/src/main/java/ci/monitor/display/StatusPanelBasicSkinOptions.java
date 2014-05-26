package ci.monitor.display;

import java.awt.Color;
import java.awt.Font;

public class StatusPanelBasicSkinOptions extends PanelBasicSkinOptions implements Cloneable {

    Color backgroundColorForInitialState = Color.GRAY;
    Color backgroundColorForUnknownState = Color.ORANGE;
    Color backgroundColorForBadState = Color.RED;
    Color backgroundColorForGoodState = Color.GREEN;
    
    String textDisplayForInitialState = "?";

    String browserUrl = null;

    public StatusPanelBasicSkinOptions() {
        textDisplayForInitialState = "?";

        font = new Font("SansSerif", Font.BOLD, 400);
        fontColor = Color.BLACK;
    }
    

    public Color getBackgroundColorForBadState() {
        return backgroundColorForBadState;
    }

    public void setBackgroundColorForBadState(Color backgroundColorForBadState) {
        this.backgroundColorForBadState = backgroundColorForBadState;
    }

    public Color getBackgroundColorForGoodState() {
        return backgroundColorForGoodState;
    }

    public void setBackgroundColorForGoodState(Color backgroundColorForGoodState) {
        this.backgroundColorForGoodState = backgroundColorForGoodState;
    }

    public Color getBackgroundColorForInitialState() {
        return backgroundColorForInitialState;
    }

    public void setBackgroundColorForInitialState(Color backgroundColorForInitialState) {
        this.backgroundColorForInitialState = backgroundColorForInitialState;
    }

    public Color getBackgroundColorForUnknownState() {
        return backgroundColorForUnknownState;
    }

    public void setBackgroundColorForUnknownState(Color backgroundColorForUnknownState) {
        this.backgroundColorForUnknownState = backgroundColorForUnknownState;
    }

    public String getTextDisplayForInitialState() {
        return textDisplayForInitialState;
    }

    public void setTextDisplayForInitialState(String textDisplayForInitialState) {
        this.textDisplayForInitialState = textDisplayForInitialState;
    }

    public String getBrowserUrl() {
        return browserUrl;
    }

    public void setBrowserUrl(String browserUrl) {
        this.browserUrl = browserUrl;
    }

    
    public Object clone() throws RuntimeException {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }



    public String toString() {
		return "StatusPanelBasicSkinOptions[ "
                + "textDisplayForInitialState='" + String.valueOf(textDisplayForInitialState)
                + "', backgroundColorForInitialState='" + String.valueOf(backgroundColorForInitialState)
                + "', backgroundColorForUnknownState='" + String.valueOf(backgroundColorForUnknownState)
                + "', backgroundColorForBadState='" + String.valueOf(backgroundColorForBadState)
                + "', backgroundColorForGoodState='" + String.valueOf(backgroundColorForGoodState)
                + "', browserUrl='" + String.valueOf(browserUrl)
                + "']";
	}
}
