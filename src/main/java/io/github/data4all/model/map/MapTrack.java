package io.github.data4all.model.map;

import io.github.data4all.activity.AbstractActivity;
import io.github.data4all.model.data.Track;
import io.github.data4all.view.D4AMapView;

import org.osmdroid.bonuspack.overlays.Polyline;

public class MapTrack extends Polyline{
    
    private static final String TAG = "MapTrack";
    private Track track;
    private D4AMapView mapView;
    private AbstractActivity activity;

    public MapTrack(AbstractActivity ctx, D4AMapView mv, Track track) {
        super(ctx);
        this.track = track;
        this.activity = ctx;
        this.mapView = mv;
    }

}
