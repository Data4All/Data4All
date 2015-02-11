package io.github.data4all.util;

import io.github.data4all.logger.Log;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

public class CameraUtil {
	
	private static final String TAG = CameraUtil.class.getSimpleName();
	
	public static Size getRotationAndSetPreviewSize(Context context, Size optimalPreviewSize, Size orientationPreviewSize){
		
		Display display = getDisplayRotation(context);
		
		Log.d(TAG,"The Device has the Orientation :"+ display.getRotation());
		
		switch (display.getRotation()) {
		case Surface.ROTATION_0:
			orientationPreviewSize.width = optimalPreviewSize.height;
			orientationPreviewSize.height = optimalPreviewSize.width;
			break;
		case Surface.ROTATION_90:
			orientationPreviewSize.width = optimalPreviewSize.width;
			orientationPreviewSize.height = optimalPreviewSize.height;
			break;
		case Surface.ROTATION_180:
			orientationPreviewSize.width= optimalPreviewSize.height;
			orientationPreviewSize.height = optimalPreviewSize.width;
			break;
		case Surface.ROTATION_270:
			orientationPreviewSize.width = optimalPreviewSize.width;
			orientationPreviewSize.height = optimalPreviewSize.height;
			break;
		}
		
		return orientationPreviewSize;
	
	}
	
	
	private static Display getDisplayRotation(Context context) {
		Display display = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		return display;
		
	}


	public static int getRotationAsInt(Context context){			
			int rotation = getDisplayRotation(context).getRotation();
		
			int result = 0;
			switch (rotation) {
			case Surface.ROTATION_0:
				result = 0;
				break;
			case Surface.ROTATION_90:
				result = 90;
				break;
			case Surface.ROTATION_180:
				result = 180;
				break;
			case Surface.ROTATION_270:
				result = 270;
				break;
			}
			return result;
	}
	
	
	
	public static void setCameraDisplayOrientation(Activity activity, int cameraID, android.hardware.Camera mCamera) {
	        Camera.CameraInfo info = new Camera.CameraInfo();
	        Camera.getCameraInfo(cameraID, info);
	        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
	        int degrees = 0;
	        switch(rotation){
	        case Surface.ROTATION_0: degrees =0;
	        break;
	        case Surface.ROTATION_90: degrees = 90;
	        break;
	        case Surface.ROTATION_180: degrees = 180;
	        break; 
	        case Surface.ROTATION_270: degrees = 270;
	        break;
	        }

	        int result;
	        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	            result = (info.orientation + degrees) % 360;
	            result = (360 - result) % 360;
	        }
	        else {
	            result = (info.orientation - degrees + 360) % 360;
	        }
	        mCamera.setDisplayOrientation(result);
	}  
}
