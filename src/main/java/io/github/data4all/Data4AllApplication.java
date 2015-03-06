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
package io.github.data4all;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import android.app.Application;

/**
 * This class represents the application itself. It initializes the acra report
 * system.
 * 
 * @author tbrose
 *
 */
@ReportsCrashes(
        formUri = "http://134.102.219.161:5984/acra-data4all_master/_design/acra-storage/_update/report",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.PUT,
        formUriBasicAuthLogin = "d4a-reporter",
        formUriBasicAuthPassword = "ja!doppelkeks",
        // This is required for backward compatibility but not used
        formKey = "",
        customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PACKAGE_NAME,
                ReportField.REPORT_ID,
                ReportField.BUILD,
                ReportField.STACK_TRACE,
                ReportField.CUSTOM_DATA
        }
)
public class Data4AllApplication extends Application {
    @Override
    public final void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }
}
