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

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * AssistView is a helper class to display the optimal picture shooting angle.
 * 
 * This class is drawing red lines into the {@link CameraPreview} and
 * shows unsuitable angles for taking a photo.
 * 
 * @author Andre Koch
 * @CreationDate 12.02.2015
 * @LastUpdate 12.02.2015
 * @version 1.0
 * 
 */

public class CaptureAssistView extends View {

	private static final String TAG = CaptureAssistView.class.getSimpleName();

	public CaptureAssistView(Context context) {
		super(context);
		initView();
	}

	public CaptureAssistView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public CaptureAssistView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	private void initView() {

	}

}