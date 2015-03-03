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
package io.github.data4all.logger;

/**
 * This class provides the whole logging for this application. Its override the
 * android.util.log and add the ability to disable logging.
 * 
 * @author AndreKoch
 * @version 1.0
 *
 */
public final class Log {

    private static String DEFAULT_TAG = "DATA4ALL";
    private static final boolean LOG = true;
    private static final boolean ERROR = true;
    private static final boolean WARNING = true;
    private static final boolean VERBOSE = true;
    private static final boolean INFO = true;
    private static final boolean DEBUG = true;
    private static final boolean WTF = true;

    /**
     * Private Constructor, prevents instantization.
     */
    private Log() {

    }

    /**
     * This Log Level reports info messages. any message that is helpful in
     * tracking the flow through the system and isolating issues, especially
     * during the development and QA phases.
     * 
     * @param tag
     *            the actual class
     * @param msg
     *            the message for the logger
     */
    public static void i(String tag, String msg) {
        if (LOG && INFO) {
            android.util.Log.i(createTag(tag), msg);
        }
    }

    /**
     * This Log Level reports error. This means the system is in distress,
     * customers are probably being affected (or will soon be) and the fix
     * probably requires human intervention.
     * 
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg) {
        if (LOG && ERROR) {
            android.util.Log.e(createTag(tag), msg);
        }
    }

    /**
     * This Log Level reports errors.This means the system is in distress,
     * customers are probably being affected (or will soon be) and the fix
     * probably requires human intervention.
     * 
     * @param tag
     *            the actual class
     * @param msg
     *            the message for the logger
     * @param tr
     *            class which can be thrown by the VM e.g. Exception
     */
    public static void e(String tag, String msg, Throwable tr) {
        if (LOG && ERROR) {
            android.util.Log.e(createTag(tag), msg, tr);
        }
    }

    /**
     * This Log Level reports debug.things we want to see at high volume in case
     * we need to forensically analyze an issue. System lifecycle events (system
     * start, stop) go here.
     * 
     * @param tag
     *            the actual class
     * @param msg
     *            the message for the logger
     */
    public static void d(String tag, String msg) {
        if (LOG && DEBUG) {
            android.util.Log.d(createTag(tag), msg);
        }
    }

    /**
     * This Log Level reports verbose.
     * 
     * @param tag
     *            the actual class
     * @param msg
     *            the message for the logger
     */
    public static void v(String tag, String msg) {
        if (LOG && VERBOSE) {
            android.util.Log.v(createTag(tag), msg);
        }
    }

    /**
     * This Log Level reports warnings.This means an unexpected technical or
     * business event happened, customers may be affected, but probably no
     * immediate human intervention is required.
     * 
     * @param tag
     *            the actual class
     * @param msg
     *            the message for the logger
     */
    public static void w(String tag, String msg) {
        if (LOG && WARNING) {
            android.util.Log.w(createTag(tag), msg);
        }
    }

    /**
     * This Log Level reports warnings.This means an unexpected technical or
     * business event happened, customers may be affected, but probably no
     * immediate human intervention is required.
     * 
     * @param tag
     *            the actual class
     * @param msg
     *            the message for the logger
     * @param tr
     *            class which can be thrown by the VM e.g. Exception
     */
    public static void w(String tag, String msg, Throwable tr) {
        if (LOG && WARNING) {
            android.util.Log.w(createTag(tag), msg, tr);
        }
    }

    /**
     * This Log Level reports what a terrible failure (wtf).This means it report
     * failures that should never happen. This Level will be reported to the
     * Development Team.
     * 
     * @param tag
     *            the actual class
     * @param msg
     *            the message for the logger
     * @param tr
     *            class which can be thrown by the VM e.g. Exception
     */
    public static void wtf(String tag, String msg, Throwable tr) {
        if (LOG && WTF) {
            android.util.Log.wtf(createTag(tag), msg, tr);
        }
    }

    private static String createTag(String tag) {
        final StringBuilder result = new StringBuilder(DEFAULT_TAG);
        result.append("-");
        result.append(tag);
        return result.toString();
    }

}
