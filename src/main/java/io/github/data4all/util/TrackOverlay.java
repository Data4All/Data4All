package io.github.data4all.util;

import io.github.data4all.model.data.Track;
import io.github.data4all.model.data.TrackPoint;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.util.GeoPoint;

import android.content.Context;

public class TrackOverlay extends Polyline{
    
    List<TrackPoint> tracklist;
    List<IGeoPoint> pointlist;
    Polyline trackOverlay;

    public TrackOverlay(Context ctx, Track track) {
        super(ctx);
        tracklist = track.getTrackPoints();
        pointlist = new ArrayList<IGeoPoint>();
        
        for(TrackPoint tp : tracklist) {
            IGeoPoint gp = new GeoPoint(tp.getLat(), tp.getLon());
            pointlist.add(gp);
        }
        
        trackOverlay = new Polyline(ctx);
        
        //trackOverlay.setPoints(pointlist);
        
    }
    
    public List<IGeoPoint> returnList() {
        return pointlist;
    }
    
    public Polyline getOverlay() {
        return trackOverlay;
    }

}
