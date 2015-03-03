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
        db.createGPSTrack(track);
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
        if (!allGPSTracks.isEmpty()) {
            for (int i = allGPSTracks.size()-1; i > 0; i--) {
                Log.d("TrackUtility", "Continue on last track.");
                db.close();
                
                return allGPSTracks.get(i);
            }
        }
        Log.d("TrackUtility",
                "There is no last opened track, so start a new one");
        db.close();
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
        if (point1.getLat() == point2.getLat()
                && point1.getLon() == point2.getLon()) {
            return true;
        }
        return false;
    }
}
