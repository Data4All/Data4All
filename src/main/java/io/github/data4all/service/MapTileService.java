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
import java.util.Date;
import java.util.List;

import android.app.IntentService;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Service to update the MapTiles.
 * 
 * @author Richard
 */
public class MapTileService extends IntentService {

    public final static String BROADCAST_MAP = "broadcastToMap";
    public final static String INTENT_UPDATE_TILES = "update";
    
    public static String TIME = "Time";
    public static String WEST = "West";
    public static String SOUTH = "South";
    public static String EAST = "East";
    public static String NORTH = "North";

    
    private static final String TAG = MapTileService.class.getSimpleName();

    /**
     * Constructs a new UploadService with an new Handler.
     */
    public MapTileService() {
        super(MapTileService.class.getSimpleName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.IntentService#onStartCommand(android.content.Intent,
     * int, int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Start Command");
        return super.onStartCommand(intent, flags, startId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG, "Handle Intent");
        doIntent(intent);
    }
    
    private void doIntent(Intent intent){

        if (intent != null) {
            long time = intent.getLongExtra(TIME, new Date().getTime() - 600000);
            double west = intent.getDoubleExtra(WEST, 0);
            double south = intent.getDoubleExtra(SOUTH, 0);
            double east = intent.getDoubleExtra(EAST, 0);
            double north = intent.getDoubleExtra(NORTH, 0);
            try {
                boolean empty = ChangesetUtil.getChangeSet(time, west, south, east, north).request();
                        
                Log.d(TAG, "Check ChangeSets empty:" + empty);
                if (!empty) {
                    /*
                     * Creates a new Intent containing a Uri object BROADCAST_ACTION
                     * is a custom Intent action
                     */

                    Log.d(TAG, "New ChangeSets available");
                    Intent localIntent = new Intent(BROADCAST_MAP)
                    // Puts the status into the Intent
                            .putExtra(INTENT_UPDATE_TILES, true);
                    // Broadcasts the Intent to receivers in this app.
                    LocalBroadcastManager.getInstance(this).sendBroadcast(
                            localIntent);
                }
            } catch (OsmException e) {
                Log.e(TAG, "" + e.toString());
            }
        } 
    }
}
