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
package io.github.data4all.util;

import io.github.data4all.handler.DataBaseHandler;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.Track;
import io.github.data4all.model.data.TrackPoint;

import java.util.List;

import android.content.Context;
import android.location.Location;

public class TrackUtility {

    private DataBaseHandler db;

    private Context context;

    private static final String TAG = "TrackUtility";

    public TrackUtility(Context ctx) {
        this.context = ctx;
    }

    /**
     * Starts a new Track and saves it in the database
     * 
     * @return the track
     */
    public Track startNewTrack() {
        db = new DataBaseHandler(context.getApplicationContext());
        Track track = new Track();
        long trackID = db.createGPSTrack(track);
        track.setID(trackID);
        Log.d(TAG, "Starting a new track.");
        db.close();
        return track;
    }

    /**
     * Adds a new point to the track
     * 
     * @param track
     *            The track
     * @param loc
     *            The location to be added
     */
    public void addPointToTrack(Track track, Location loc) {
        track.addTrackPoint(loc);
    }

    /**
     * Updates a track in the database
     * 
     * @param track
     *            The track
     */
    public void updateTrack(Track track) {
        db = new DataBaseHandler(context.getApplicationContext());
        db.updateGPSTrack(track);
        Log.d(TAG, "Update track.");
        db.close();
    }

    public void saveTrack(Track track) {
        db = new DataBaseHandler(context.getApplicationContext());
        db.updateGPSTrack(track);
        Log.d(TAG, "Finish and update track in database");
        db.close();
    }

    /**
     * Load a track from database
     * 
     * @param id
     *            The unique id of the track
     * @return the track
     */
    public Track loadTrack(long id) {
        db = new DataBaseHandler(context.getApplicationContext());
        Track track = db.getGPSTrack(id);
        Log.d(TAG, "Loading track with ID: " + id);
        db.close();
        return track;
    }

    /**
     * Deletes all Tracks in the database which does not contain any trackpoints
     */
    public void deleteEmptyTracks() {
        db = new DataBaseHandler(context.getApplicationContext());
        for (Track track : db.getAllGPSTracks()) {
            if (track.getTrackPoints().isEmpty()) {
                Log.d(TAG, "Deleting empty tracks.");
                db.deleteGPSTrack(track);
            }
        }
        db.close();
    }

    /**
     * Returns the last opened and not finished track or a new Track if there
     * are no opened tracks
     * 
     * @return last opened track or a new track
     */
    public Track getLastTrack() {
        db = new DataBaseHandler(context.getApplicationContext());
        List<Track> allGPSTracks = db.getAllGPSTracks();
        db.close();
        if (!allGPSTracks.isEmpty()) {
            Log.d("TrackUtility", "Continue on last track.");
            return allGPSTracks.get(allGPSTracks.size() - 1);
        }
        Log.d("TrackUtility",
                "There is no last opened track, so start a new one");
        return startNewTrack();
    }

    /**
     * Easy comparison of two TrackPoints. Only compares longitude and latitude.
     * 
     * @param point1
     *            The first trackpoint
     * @param point2
     *            The second trackpoint
     * @return true if lon and lat of the trackpoints are the same, else false
     */
    public boolean sameTrackPoints(TrackPoint point1, Location loc) {
        final TrackPoint point2 = new TrackPoint(loc);
        int i = Double.compare(point1.getLat(), point2.getLat());
        int j = Double.compare(point1.getLon(), point2.getLon());

        if (i == 0 && j == 0) {
            return true;
        }

        return false;
    }
}
