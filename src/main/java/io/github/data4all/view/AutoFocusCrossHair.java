/*
 * Copyright (c) 2014, 2015 Data4All
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.data4all.view;

import io.github.data4all.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Auto is a helper class for displaying the autofocus status.
 * 
 * This class draws a rectangle into this activity and animate this based on the
 * status of the autofocus.
 * 
 * @author Andre Koch
 * @CreationDate 12.02.2015
 * @LastUpdate 12.02.2015
 * @version 1.0
 * 
 */

public class AutoFocusCrossHair extends View {


	/**
	 * Public Constructor for creating the AutoFocus Cross Hair
	 * @param context 
	 * @param attrs
	 */
	public AutoFocusCrossHair(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private void setDrawable(int resid) {
		this.setBackgroundResource(resid);
	}

	/**
	 * method to set the image on the view
	 */
	public void showStart() {
		this.setDrawable(R.drawable.crosshair);
	}
	
	
	/**
	 * method to remove Image from view
	 */
	public void clear() {
		this.setBackgroundResource(0);
	}

}
