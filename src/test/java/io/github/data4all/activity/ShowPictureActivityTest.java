/*******************************************************************************
 * Copyright (c) 2014, 2015 Data4All
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package io.github.data4all.activity;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Intent;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class ShowPictureActivityTest {
    ShowPictureActivity activity;

    @Before
    public void setup() {
        this.activity = Robolectric.buildActivity(ShowPictureActivity.class).get();
        Intent intent = new Intent();
        File test = new File("@drawable/android3");
        intent.putExtra("file_path", test);
        activity.setIntent(intent);
    }

    @Test
    public void shouldNotNull() throws Exception {
        assertNotNull(activity);
    }

    @Test
    public void getIntentFile() throws Exception {

    }

}
