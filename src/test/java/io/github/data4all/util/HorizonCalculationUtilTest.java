package io.github.data4all.util;

import io.github.data4all.model.DeviceOrientation;
import io.github.data4all.model.data.TransformationParamBean;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.location.Location;


@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class HorizonCalculationUtilTest {

    
    HorizonCalculationUtil util = new HorizonCalculationUtil();
    Location location = new Location("Test");
    
    @Test
    public void transformTest(){
        TransformationParamBean tps = new TransformationParamBean(2.0, Math.toRadians(90) ,
        Math.toRadians(90) , 1000, 1000, location);

        DeviceOrientation deviceOrientation = new DeviceOrientation(0.0f, 
                (float) Math.toRadians(84), (float) Math.toRadians(84), 10L);
        float[][] test = new float[100][100];

          //util.calculatePixelFromAngle((float) Math.toRadians(15), 1000, (float) Math.toRadians(40));
          test[0] = util.calcHorizontalPoints((float) Math.toRadians(40), (float) Math.toRadians(40), 
                  1000, 1000,(float) Math.toRadians(85), deviceOrientation);
          deviceOrientation = new DeviceOrientation(0.0f, 
                  (float) Math.toRadians(0), (float) Math.toRadians(80), 10L);
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
          i++;
    }
}
