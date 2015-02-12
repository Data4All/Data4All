package io.github.data4all.util;

import io.github.data4all.model.DeviceOrientation;
import io.github.data4all.model.data.TransformationParamBean;
import io.github.data4all.model.drawing.Point;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.location.Location;


@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class HorizonCalculationUtilTest {

    
    HorizonCalculationUtil util ;
    PointToCoordsTransformUtil util2;
    TransformationParamBean tps;
    DeviceOrientation deviceOrientation;
    Location location;

    @Before
    public void setUp() {
        location = new Location("Provider");
        // height is 1.7, photoWidth is 500 and photoHeight is 1000
        tps = new TransformationParamBean(1.7, Math.toRadians(90),
                Math.toRadians(90), 500, 1000, location);
        util2 = new PointToCoordsTransformUtil(tps, deviceOrientation);
        util = new HorizonCalculationUtil();
    }

    
    @Test
    public void transformTest(){

        DeviceOrientation deviceOrientation ;
        float[][] test = new float[200][100];
       
        int i = -120;
        int b = -120;
        int k = 0;
        int zähler = 0;
        int zähler2 = 0;
        while (i < 120){
            while(b < 120){
                float[] horizon;
                deviceOrientation = new DeviceOrientation(0.0f, 
                        (float) Math.toRadians(i), (float) Math.toRadians(b), 10L);
                horizon = util.calcHorizontalPoints((float) Math.toRadians(40), (float) Math.toRadians(40), 
                        500, 1000,(float) Math.toRadians(30), deviceOrientation);
                if ( horizon[2] == 1){
                   //Point point = new Point(horizon[0], horizon[1]);
                    Point point = new Point(250,500);
                    if(util2.calculateCoordFromPoint(tps, deviceOrientation, point) == null){
                        zähler++;
                    }
                    else{
                        zähler2++;
                    }
                }
                
                b++;
                k++;
            }
            b = -120;
            i++;
        }
        i = i+1;
        k = k;
        /*

          //util.calculatePixelFromAngle((float) Math.toRadians(15), 1000, (float) Math.toRadians(40));

          deviceOrientation = new DeviceOrientation(0.0f, 
                  (float) Math.toRadians(120), (float) Math.toRadians(0), 10L);
          test[1] = util.calcHorizontalPoints((float) Math.toRadians(40), (float) Math.toRadians(40), 
                  1000, 1000,(float) Math.toRadians(85), deviceOrientation);
          deviceOrientation = new DeviceOrientation(0.0f, 
                  (float) Math.toRadians(0), (float) Math.toRadians(70), 10L);
          test[2] = util.calcHorizontalPoints((float) Math.toRadians(40), (float) Math.toRadians(40), 
                  1000, 1000,(float) Math.toRadians(85), deviceOrientation);
          deviceOrientation = new DeviceOrientation(0.0f, 
                  (float) Math.toRadians(50), (float) Math.toRadians(50), 10L);
          test[3] = util.calcHorizontalPoints((float) Math.toRadians(40), (float) Math.toRadians(40), 
                  1000, 1000,(float) Math.toRadians(85), deviceOrientation);

          int i =1;
          i++;*/
    }
}
