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
import io.github.data4all.logger.Log;
import io.github.data4all.util.TrackUtil;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Activity to show details of a track.
 * 
 * @author sbrede
 *
 */
public class TrackDetailsActivity extends AbstractActivity implements
        OnClickListener {
    
    private TrackUtil trackUtil;

    private TextView trackDetails;
    private TextView trackId;
    private TextView trackPointCount;
    private ImageButton deleteButton;
    private ImageButton uploadButton;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_details);
        
        trackUtil = new TrackUtil(this.getApplicationContext());

        setFields();

        addButtonListener();

    }

    /**
     * Sets the textViews for this activity and get the data for the
     * corresponding fields.
     */
    private void setFields() {
        trackDetails = (TextView) findViewById(R.id.textViewTrackDetails);
        trackId = (TextView) findViewById(R.id.textViewTrackId);
        trackPointCount = (TextView) findViewById(R.id.textViewTrackPointCount);

        Intent intent = getIntent();
        trackDetails.setText(intent.getStringExtra("name"));
        trackId.setText(Long.toString(intent.getLongExtra("id", -1)));
        trackPointCount.setText(Integer.toString(intent.getIntExtra(
                "trackpoints", -1)));

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.github.data4all.activity.AbstractActivity#onWorkflowFinished(android
     * .content.Intent)
     */
    @Override
    protected void onWorkflowFinished(Intent data) {
        finishWorkflow(data);
    }

    /**
     * Adds a button listener for this activity. A click on delete button
     * deletes a track and starts a new {@link GpsTrackListActivity}. The upload
     * button starts the {@link LoginActivity}.
     */
    private void addButtonListener() {

        deleteButton = (ImageButton) findViewById(R.id.buttonDeleteTrack);
        uploadButton = (ImageButton) findViewById(R.id.buttonUploadTrack);
        deleteButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
    }

    /**
     * Gets the track for a corresponding id and deletes this track from
     * database.
     * 
     * @param id
     */
    private void deleteTrack(long id) {
        Log.d("TrackDetailsActivity", "ID: " + id);
        trackUtil.deleteTrack(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.buttonDeleteTrack:
            deleteTrack(Long.valueOf(trackId.getText().toString()));//getIntent().getLongExtra("id", -1));
            Intent listActivity = new Intent(getApplicationContext(),
                    GpsTrackListActivity.class);
            startActivity(listActivity);
            break;
        case R.id.buttonUploadTrack:
            startActivity(new Intent(this, LoginActivity.class));
            break;
        default:
            break;
        }

    }

}
