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

import io.github.data4all.handler.DataBaseHandler;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.User;
import io.github.data4all.task.RequestChangesetIDFromOsmTask;
import io.github.data4all.util.oauth.exception.OsmException;
import io.github.data4all.util.upload.Callback;
import io.github.data4all.util.upload.ChangesetUtil;
import io.github.data4all.util.upload.CloseableRequest;
import io.github.data4all.util.upload.CloseableUpload;
import io.github.data4all.util.upload.HttpCloseable;

import java.util.concurrent.ExecutionException;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

/**
 * 
 * @author tbrose
 */
public class UploadService extends IntentService {
    private static final String TAG = UploadService.class.getSimpleName();

    public static final String ACTION =
            "io.github.data4all.service.UploadService:ACTION";
    public static final String HANDLER =
            "io.github.data4all.service.UploadService:HANDLER";
    public static final String MESSAGE =
            "io.github.data4all.service.UploadService:MESSAGE";

    public static final int UPLOAD = 1;
    public static final int CANCLE = 2;

    public static final int MAX_PROGRESS = 11;
    public static final int CURRENT_PROGRESS = 12;
    public static final int ERROR = 13;

    private boolean stopNext;
    private HttpCloseable currentConnection;

    /**
     * @param name
     */
    public UploadService() {
        super(UploadService.class.getSimpleName());
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
            stopNext = true;
            if (currentConnection != null) {
                currentConnection.stop();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && intent.getIntExtra(ACTION, 0) == UPLOAD) {
            final ResultReceiver receiver = intent.getParcelableExtra(HANDLER);

            final DataBaseHandler db = new DataBaseHandler(this);
            final User user = db.getAllUser().get(0);
            db.close();

            this.uploadElems(receiver, user);
            stopNext = false;
        }
    }

    private void uploadElems(final ResultReceiver receiver, final User user) {
        try {
            final Integer changeSetId =
                    new RequestChangesetIDFromOsmTask(user, "").execute().get();

            if (changeSetId == null) {
                send(receiver, ERROR, "Changeset Id cannot be received");
            }

            if (stopNext) {
                stopNext = false;
                return;
            }

            final CloseableRequest request =
                    ChangesetUtil.requestId(user,
                            "User-triggered upload via App");
            this.currentConnection = request;
            final int requestId = request.request();

            if (stopNext) {
                stopNext = false;
                return;
            }

            final String changesetXml =
                    ChangesetUtil.getChangesetXml(this, requestId);

            if (stopNext) {
                stopNext = false;
                return;
            }

            send(receiver, MAX_PROGRESS, changesetXml.length());
            final CloseableUpload upload =
                    ChangesetUtil.upload(user, changeSetId, changesetXml,
                            new MyCallback(receiver));

            upload.upload();
        } catch (InterruptedException e) {
            Log.e(TAG, "", e);
            send(receiver, ERROR, e.getLocalizedMessage());
        } catch (ExecutionException e) {
            Log.e(TAG, "", e);
            send(receiver, ERROR, e.getLocalizedMessage());
        } catch (OsmException e) {
            Log.e(TAG, "", e);
            send(receiver, ERROR, e.getLocalizedMessage());
        }
    }

    private static void send(ResultReceiver receiver, int code, int data) {
        final Bundle bundle = new Bundle();
        bundle.putInt(MESSAGE, data);
        send(receiver, code, bundle);
    }

    private static void send(ResultReceiver receiver, int code, String data) {
        final Bundle bundle = new Bundle();
        bundle.putString(MESSAGE, data);
        send(receiver, code, bundle);
    }

    private static void send(ResultReceiver receiver, int code, Bundle data) {
        if (receiver != null) {
            receiver.send(code, data);
        }
    }

    /**
     * 
     * @author tbrose
     */
    private class MyCallback implements Callback<Integer> {
        private ResultReceiver receiver;

        /**
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
        }

    }
}
