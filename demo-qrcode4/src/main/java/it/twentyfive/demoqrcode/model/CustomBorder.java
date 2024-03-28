package it.twentyfive.demoqrcode.model;
import java.awt.Color;
import lombok.Data;

@Data
public class CustomBorder {

    private String borderColor;
    private String borderText;
    private int bordSizeTop;
    private int bordSizeRight;
    private int bordSizeBottom;
    private int bordSizeLeft;
    
    public Color getBorderColor(){
        return Color.decode(borderColor);
    }
}
