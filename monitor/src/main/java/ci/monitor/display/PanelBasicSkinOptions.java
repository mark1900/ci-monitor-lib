package ci.monitor.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class PanelBasicSkinOptions implements Cloneable {

    Font font = new Font("SansSerif", Font.BOLD, 400);
    Color fontColor = Color.BLACK;


    Border border = BorderFactory.createEmptyBorder();
    int cellSpan = 1;
    Dimension size = null;

    public PanelBasicSkinOptions(){

        font = new Font("SansSerif", Font.BOLD, 40);
        fontColor = Color.BLACK;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Color getFontColor() {
        return fontColor;
    }

    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
    }

    public Border getBorder() {
        return border;
    }

    public void setBorder(Border border) {
        this.border = border;
    }

    public int getCellSpan() {
        return cellSpan;
    }

    public void setCellSpan(int cellSpan) {
        this.cellSpan = cellSpan;
    }

    public Dimension getSize() {
        return size;
    }

    public void setSize(Dimension size) {
        this.size = size;
    }
}
