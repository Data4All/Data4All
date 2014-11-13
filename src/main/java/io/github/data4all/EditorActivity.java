package io.github.data4all;

import android.R.color;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class EditorActivity extends Activity implements OnTouchListener {
	private ImageView imageView;
	private Bitmap bitmap;
	private Canvas canvas;
	private Paint paint;
	private float downx = 0, downy = 0, upx = 0, upy = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editor);
	    imageView = (ImageView) this.findViewById(R.id.imageView1);
	 
	    Display currentDisplay = getWindowManager().getDefaultDisplay();
	    float dw = currentDisplay.getWidth();
	    float dh = currentDisplay.getHeight();
	 
	    bitmap = Bitmap.createBitmap((int) dw, (int) dh, Bitmap.Config.ARGB_8888);
	    canvas = new Canvas(bitmap);
	    paint = new Paint();
	    paint.setColor(Color.BLACK);
	    imageView.setImageBitmap(bitmap);
	 
	    imageView.setOnTouchListener(this);
	}
	  
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
	    switch (action) {
	    case MotionEvent.ACTION_DOWN:
	    	downx = event.getX();
	    	downy = event.getY();
	    	break;
	    case MotionEvent.ACTION_MOVE:
	    	break;
	    case MotionEvent.ACTION_UP:
	    	upx = event.getX();
	    	upy = event.getY();
	    	canvas.drawLine(downx, downy, upx, upy, paint);
	    	canvas.drawCircle(downx, downy, 4, paint);
	    	canvas.drawCircle(upx, upy, 4, paint);
	    	imageView.invalidate();
	    	break;
	    case MotionEvent.ACTION_CANCEL:
	    	break;
	    default:
	    	break;
	    }
	    return true;
	}


}


