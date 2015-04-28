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

public class TrackUtil {

    private DataBaseHandler db;

    private Context context;

    private static final String TAG = "TrackUtility";

    public TrackUtil(Context ctx) {
        this.context = ctx;
    }

    /**
     * Starts a new Track and saves it in the database
     * 
     * @return the track
     */
    public Track startNewTrack() {

        // TODO check for active tracks and close them
        db = new DataBaseHandler(context.getApplicationContext());
        Track track = new Track();
        db.createGPSTrack(track);
        Log.d(TAG, "Starting a new track. ID: " + track.getID());
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
        Log.d(TAG, "Add point to track with id: " + track.getID());
        track.addTrackPoint(loc);
    }

    /**
     * Updates a track in the database
     * 
     * @param track
     *            The track
     */
    public void updateTrack(Track track) {
        if (track != null) {
            db = new DataBaseHandler(context.getApplicationContext());
            db.updateGPSTrack(track);
            Log.d(TAG, "Update track with id: " + track.getID());
            db.close();
        }
    }

    /**
     * Saves and closes a track in the database By closing a track it is not
     * possible to add new trackpoints.
     * 
     * @param track
     *            The track
     */
    public void saveTrack(Track track) {
        if (track != null) {
            db = new DataBaseHandler(context.getApplicationContext());
            track.finishTrack();
            db.updateGPSTrack(track);
            Log.d(TAG,
                    "Finish and update track in database with id: "
                            + track.getID());
            db.close();
        }
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
     * Gets the track for a corresponding id and deletes this track from
     * database.
     * 
     * @param id
     */
    public void deleteTrack(long id) {
        DataBaseHandler db = new DataBaseHandler(context.getApplicationContext());
        Track track = db.getGPSTrack(id);
        db.deleteGPSTrack(track);
        db.close();
    }

    
    /**
     * Returns a List of all Tracks from Database.
     * @return trackList
     */
    public List<Track> getTracks() {
        DataBaseHandler db = new DataBaseHandler(
                context.getApplicationContext());
        List<Track> trackList = db.getAllGPSTracks();
        db.close();
        return trackList;
    }

    /**
     * Returns the last opened and not finished track or null if there are no
     * opened tracks
     * 
     * @return last opened track or a new track
     */
    public Track getLastTrack() {
        db = new DataBaseHandler(context.getApplicationContext());
        List<Track> allGPSTracks = db.getAllGPSTracks();
        Log.d(TAG, "TrackList" + allGPSTracks.toString());
        if (!allGPSTracks.isEmpty()) {
            for (Track tr : allGPSTracks) {
                if (!tr.isFinished()) {
                    Log.d("TrackUtility", "Continue on last track with id: "
                            + tr.getID());
                    db.close();
                    return tr;
                }
            }
        }
        Log.d("TrackUtility", "There is no last opened track.");
        db.close();
        return null;
    }

    
    /**
     * Return the number of {@link TrackPoint} of a {@link Track}.
     * @param track The {@link Track}
     * @return size The number of {@link TrackPoint}
     */
    public int getTrackSize(Track track) {
        int size = track.getTrackPoints().size();
        return size;
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
        if (point1.getLat() == point2.getLat()
                && point1.getLon() == point2.getLon()) {
            return true;
        }
        return false;
    }
}
