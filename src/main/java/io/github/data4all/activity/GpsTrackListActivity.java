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
package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.model.data.Track;
import io.github.data4all.util.ListAdapter;
import io.github.data4all.util.TrackUtil;

import java.util.List;

import android.content.Intent;
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
    private TrackUtil trackUtil;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracklist_view);
        
        trackUtil = new TrackUtil(this.getApplicationContext());

        // Find the ListView resource.
        final ListView trackListView = (ListView) findViewById(R.id.trackListView);

        // Get saved tracks
        getTracks();

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
        
        // Set the view from an empty List
        trackListView.setEmptyView(findViewById(R.id.emptyList));
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
        trackList = trackUtil.getTracks();
    }

    /* (non-Javadoc)
     * @see io.github.data4all.activity.AbstractActivity#onWorkflowFinished(android.content.Intent)
     */
    @Override
    protected void onWorkflowFinished(Intent data) {
        finishWorkflow(data);
    };

}