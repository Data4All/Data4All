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
package io.github.data4all.service;

import io.github.data4all.R;
import io.github.data4all.handler.CapturePictureHandler;
import io.github.data4all.handler.DataBaseHandler;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.User;
import io.github.data4all.util.oauth.exception.OsmException;
import io.github.data4all.util.upload.Callback;
import io.github.data4all.util.upload.ChangesetUtil;
import io.github.data4all.util.upload.CloseableCloseRequest;
import io.github.data4all.util.upload.CloseableRequest;
import io.github.data4all.util.upload.CloseableUpload;
import io.github.data4all.util.upload.HttpCloseable;

import java.io.File;
import java.util.List;

import android.app.IntentService;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

/**
 * Service to upload osm objects to the OSM API.
 * 
 * @author tbrose
 */
public class UploadElementsService extends IntentService {

    /**
     * The comment for the changeset to open.
     */
    public static final String CHANGESET_COMMENT =
            "User-triggered upload via App";

    /**
     * The id of the foreground notification.
     */
    private static final int NOTIFICATION_ID = 201;

    /**
     * Logger.
     */
    private static final String TAG = UploadElementsService.class
            .getSimpleName();

    /**
     * Key/Message for handling intent extras.
     */
    public static final String ACTION =
            "io.github.data4all.service.UploadElementsService:ACTION";
    public static final String HANDLER =
            "io.github.data4all.service.UploadElementsService:HANDLER";
    public static final String MESSAGE =
            "io.github.data4all.service.UploadElementsService:MESSAGE";

    /**
     * Codes to identify different events.
     */
    public static final int UPLOAD = 1;
    public static final int CANCLE = 2;

    public static final int MAX_PROGRESS = 11;
    public static final int CURRENT_PROGRESS = 12;
    public static final int SUCCESS = 13;
    public static final int ERROR = 14;

    private volatile boolean stopNext;
    private volatile HttpCloseable currentConnection;
    private volatile Builder currentNotification;

    private int currentMaxProgress;

    /**
     * Constructs a new UploadService with an new Handler.
     * 
     * @param name
     *            Used to name the worker thread, important only for debugging
     */
    public UploadElementsService() {
        super(UploadElementsService.class.getSimpleName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.IntentService#onStartCommand(android.content.Intent,
     * int, int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getIntExtra(ACTION, 0) == CANCLE) {
            Log.d(TAG, "stopping upload service for osm elements...");
            stopNext = true;
            if (currentConnection != null) {
                currentConnection.stop();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && intent.getIntExtra(ACTION, 0) == UPLOAD) {
            final ResultReceiver receiver = intent.getParcelableExtra(HANDLER);
            final String comment = intent.getStringExtra(CHANGESET_COMMENT);
            final DataBaseHandler db = new DataBaseHandler(this);
            final List<User> users = db.getAllUser();
            db.close();
            if (users != null && !users.isEmpty()) {
                final User user = users.get(0);
                this.uploadElems(receiver, user, comment);
                stopNext = false;
            }
        }
    }

    /**
     * Requests a new Changeset ID from the OSM API, parses the OSM elements
     * from the Database and starts the upload.
     * 
     * @param receiver
     *            The ResultReceiver instance
     * @param user
     *            The User to the data from
     */
    private void uploadElems(final ResultReceiver receiver, final User user,
            final String comment) {
        try {
            this.startForeground(user);
            int requestId = 0;
            if (!stopNext) {
                // Request the changesetId
                final CloseableRequest request =
                        ChangesetUtil.requestId(user, comment);
                this.currentConnection = request;
                requestId = request.request();
            }

            String changesetXml = null;
            if (!stopNext) {
                changesetXml = ChangesetUtil.getChangesetXml(this, requestId);
                Log.d(TAG, changesetXml.replaceAll("\n", ""));
            }
            if (!stopNext) {
                // Upload the changeset
                currentMaxProgress = changesetXml.length();
                send(receiver, MAX_PROGRESS, currentMaxProgress);
                final CloseableUpload upload =
                        ChangesetUtil.upload(user, requestId, changesetXml,
                                new MyCallback(receiver));
                this.currentConnection = upload;
                upload.upload();

            }
            if (!stopNext) {
                // Close the changeset
                final CloseableCloseRequest closeId =
                        ChangesetUtil.closeId(user, requestId);
                this.currentConnection = closeId;
                closeId.request();
            }
            if (!stopNext) {
                this.stopForeground(SUCCESS);
                send(receiver, SUCCESS, (Bundle) null);
                deleteImages();
            }
        } catch (OsmException e) {
            Log.e(TAG, "", e);
            this.stopForeground(ERROR);
            send(receiver, ERROR, e.getLocalizedMessage());
        }
        if (stopNext) {
            this.stopForeground(CANCLE);
        }
    }

    /**
     * Deletes all images in the "Data4All" folder, where the 'single-mode'
     * images where saved.
     */
    private static void deleteImages() {
        final File folder = CapturePictureHandler.getImageFolder();
        final File[] images = folder.listFiles();

        if (images != null) {
            for (File image : images) {
                image.delete();
            }
        }

        folder.delete();
    }

    /**
     * Moves the service to foreground and posts a notification for the user.
     * 
     * @param user
     *            The user to upload for
     */
    private void startForeground(User user) {
        currentNotification = new Notification.Builder(this);
        currentNotification
                .setContentTitle(getString(R.string.upload_to_openstreetmap))
                .setContentText(
                        getString(R.string.upload_to_openstreetmap_progress,
                                user.getUsername()))
                .setSmallIcon(android.R.drawable.ic_menu_upload);
        currentNotification.setProgress(0, 0, true);

        this.startForeground(NOTIFICATION_ID, currentNotification.build());
    }

    /**
     * Updates the current foreground notification with the current progress.
     * 
     * @param current
     *            The current progress
     */
    private void updateForeground(int current) {
        if (currentNotification != null) {
            currentNotification.setProgress(currentMaxProgress, current, false);

            this.startForeground(NOTIFICATION_ID, currentNotification.build());
        }
    }

    /**
     * Stops the foreground service and leaves a notification on success and
     * failure.
     * 
     * @param reason
     *            The reason to stop
     */
    private void stopForeground(int reason) {
        this.stopForeground(true);
        if (reason == SUCCESS) {
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                    .notify(NOTIFICATION_ID,
                            new Notification.Builder(this)
                                    .setContentTitle(
                                            getString(R.string.upload_to_openstreetmap))
                                    .setContentText(
                                            getString(R.string.upload_to_openstreetmap_success))
                                    .setSmallIcon(
                                            android.R.drawable.ic_menu_upload)
                                    .build());
        } else if (reason == ERROR) {
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                    .notify(NOTIFICATION_ID,
                            new Notification.Builder(this)
                                    .setContentTitle(
                                            getString(R.string.upload_to_openstreetmap))
                                    .setContentText(
                                            getString(R.string.upload_to_openstreetmap_error))
                                    .setSmallIcon(
                                            android.R.drawable.ic_menu_upload)
                                    .build());
        }
    }

    /**
     * Deliver a result to this receiver. Will call {@link #onReceiveResult},
     * always asynchronously if the receiver has supplied a Handler in which to
     * dispatch the result.
     * 
     * @param receiver
     *            The ResultReceiver
     * @param code
     *            Arbitrary result code to deliver, as defined by you.
     * @param data
     *            Additional data provided by you.
     */
    private static void send(ResultReceiver receiver, int code, int data) {
        final Bundle bundle = new Bundle();
        bundle.putInt(MESSAGE, data);
        send(receiver, code, bundle);
    }

    /**
     * Deliver a result to this receiver. Will call {@link #onReceiveResult},
     * always asynchronously if the receiver has supplied a Handler in which to
     * dispatch the result.
     * 
     * @param receiver
     *            The ResultReceiver
     * @param code
     *            Arbitrary result code to deliver, as defined by you.
     * @param data
     *            Additional data provided by you.
     */
    private static void send(ResultReceiver receiver, int code, String data) {
        final Bundle bundle = new Bundle();
        bundle.putString(MESSAGE, data);
        send(receiver, code, bundle);
    }

    /**
     * Deliver a result to this receiver. Will call {@link #onReceiveResult},
     * always asynchronously if the receiver has supplied a Handler in which to
     * dispatch the result.
     * 
     * @param receiver
     *            The ResultReceiver
     * @param code
     *            Arbitrary result code to deliver, as defined by you.
     * @param data
     *            Additional data provided by you.
     */
    private static void send(ResultReceiver receiver, int code, Bundle data) {
        if (receiver != null) {
            receiver.send(code, data);
        }
    }

    /**
     * Call back (send) the current progress at some convenient time to the
     * {@link ResultReceiver}.
     * 
     * @author tbrose
     */
    private class MyCallback implements Callback<Integer> {
        /**
         * The interval for the callback method.
         */
        private static final int CALLBACK_INTERVAL = 100;

        /**
         * The receiver for the activity callback.
         */
        private ResultReceiver receiver;

        /**
         * Constructs a new {@link MyCallback}.
         * 
         * @param receiver
         */
        public MyCallback(ResultReceiver receiver) {
            this.receiver = receiver;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * io.github.data4all.util.upload.Callback#callback(java.lang.Object)
         */
        @Override
        public void callback(Integer t) {
            send(receiver, CURRENT_PROGRESS, t);
            UploadElementsService.this.updateForeground(t);
        }

        /*
         * (non-Javadoc)
         * 
         * @see io.github.data4all.util.upload.Callback#interval()
         */
        @Override
        public int interval() {
            return CALLBACK_INTERVAL;
        }
    }
}
