package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.handler.DataBaseHandler;
import io.github.data4all.model.data.Track;
import io.github.data4all.model.data.TrackPoint;
import io.github.data4all.util.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Activity to list all records of gpstracks
 * 
 * @author sbrede
 *
 */
public class GpsTrackListActivity extends AbstractActivity {

    private ArrayAdapter<Track> trackItemArrayAdapter;
    private List<Track> trackList;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracklist_view);

        // Find the ListView resource.
        final ListView trackListView = (ListView) findViewById(R.id.trackListView);

        // Get saved tracks
        getTracks();

        for (int i = 0; i < 10; i++) {
            trackList.add(testMethod(i, i * 5));
        }

        // Get the ListAdapter
        trackItemArrayAdapter = new ListAdapter(getApplicationContext(),
                R.layout.gps_row_item, trackList);

        // Set the new adapter to this view
        trackListView.setAdapter(trackItemArrayAdapter);

        // Set listener for clicking
        trackListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                Intent intent = prepareIntent(trackListView, arg2);
                startActivity(intent);
            }
        });
    }

    /**
     * Prepares an Intent to view the track details. Puts Name, Id and number of
     * TrackPoints as extras into it.
     * 
     * @param trackListView
     *            The correspondent ListView.
     * @param position
     *            The position of the view in the adapter.
     * @return Intent The resulting intent with extras
     */
    private Intent prepareIntent(ListView trackListView, int position) {
        Intent intent = new Intent(getApplicationContext(),
                TrackDetailsActivity.class);
        Track track = (Track) trackListView.getAdapter().getItem(position);

        // Add name, id and number of trackpoints to intent
        intent.putExtra("name", track.getTrackName());
        intent.putExtra("trackpoints", track.getTrackPoints().size());
        intent.putExtra("id", track.getID());

        return intent;
    }

    /**
     * Get all Tracks from Database.
     */
    private void getTracks() {
        DataBaseHandler db = new DataBaseHandler(this);
        trackList = db.getAllGPSTracks();
        db.close();
    }

    private Track testMethod(long id, int trackpoints) {
        Track track1 = new Track();
        track1.setID(id);
        List<TrackPoint> trackPoints1 = new ArrayList<TrackPoint>();
        for (int i = 0; i < trackpoints; i++) {
            trackPoints1.add(new TrackPoint(new Location("test")));
        }
        track1.setTrackPoints(trackPoints1);
        return track1;
    }

    @Override
    protected void onWorkflowFinished(Intent data) {
        // TODO Auto-generated method stub

    };

}