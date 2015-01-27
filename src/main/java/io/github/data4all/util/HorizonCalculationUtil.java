package io.github.data4all.util;

import io.github.data4all.model.DeviceOrientation;
import io.github.data4all.model.drawing.Point;

import java.util.List;

public class HorizonCalculationUtil {
    
    
    
    public Point calcHorizontalPoints(float maxPitch, float maxRoll, float maxWidth, 
            float maxHeight, float horizon, DeviceOrientation deviceOrientation){
        float missPitch = horizon - Math.abs(deviceOrientation.getPitch());
        float missRoll = horizon - Math.abs(deviceOrientation.getRoll());
        double pitchPixel = (Math.tan(missPitch) * maxWidth) / (2 * Math.tan(maxPitch)) ;
        double rollPixel = (Math.tan(missRoll) * maxHeight) / (2*Math.tan(maxRoll));
        float x , y;
        if(deviceOrientation.getPitch() < 0){
            x = (float) pitchPixel + (maxWidth /2);
        }
        else {
            x = (float) pitchPixel - (maxWidth /2);
        }
        if(deviceOrientation.getRoll() < 0){
            y = (float) rollPixel - (maxHeight /2);
        }
        else {
            y = (float) rollPixel + (maxHeight /2);
        }
        Point point = new Point(x,y);
        return point;
    }
    
}
