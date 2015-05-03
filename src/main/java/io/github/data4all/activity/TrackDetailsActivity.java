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
import io.github.data4all.model.data.Track;
import io.github.data4all.util.TrackUtil;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
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

    private TextView trackDescription;
    private TextView trackTags;

    private ImageButton deleteButton;
    private ImageButton uploadButton;

    private ImageButton editNameButton;
    private ImageButton editTagsButton;
    private ImageButton editDescriptionButton;

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
        trackDescription = (TextView) findViewById(R.id.textViewTrackDescription);
        trackTags = (TextView) findViewById(R.id.textViewTrackTags);

        Intent intent = getIntent();
        trackDetails.setText(intent.getStringExtra("name"));
        trackId.setText(Long.toString(intent.getLongExtra("id", -1)));
        trackPointCount.setText(Integer.toString(intent.getIntExtra(
                "trackpoints", -1)));
        trackDescription.setText(intent.getStringExtra("description"));
        trackTags.setText(intent.getStringExtra("tags"));

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

        editNameButton = (ImageButton) findViewById(R.id.buttonEditTrackName);
        editTagsButton = (ImageButton) findViewById(R.id.buttonEditTrackTags);
        editDescriptionButton = (ImageButton) findViewById(R.id.buttonEditTrackDescription);
        deleteButton = (ImageButton) findViewById(R.id.buttonDeleteTrack);
        uploadButton = (ImageButton) findViewById(R.id.buttonUploadTrack);

        editNameButton.setOnClickListener(this);
        editTagsButton.setOnClickListener(this);
        editDescriptionButton.setOnClickListener(this);
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
            deleteTrack(Long.valueOf(trackId.getText().toString()));// getIntent().getLongExtra("id",
                                                                    // -1));
            this.finish();
            break;
        case R.id.buttonUploadTrack:
            startActivity(new Intent(this, LoginActivity.class));
            break;
        case R.id.buttonEditTrackName:
            editNameDialog();
            break;
        case R.id.buttonEditTrackTags:
            editTagDialog();
            break;
        case R.id.buttonEditTrackDescription:
            editDescriptionDialog();
            break;
        default:
            break;
        }

    }

    private void editDescriptionDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(
                R.layout.dialog_edit_track_description, null);

        final EditText txtDescription = (EditText) view
                .findViewById(R.id.editTrackDescriptionText);
        txtDescription.setHint(trackDescription.getText());

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setView(view);
        adb.setTitle(R.string.editTrackDescription);
        adb.setPositiveButton(R.string.save,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Track track = trackUtil.loadTrack(getIntent()
                                .getLongExtra("id", -1));
                        track.setDescription(txtDescription.getText()
                                .toString());
                        trackUtil.updateTrack(track);
                        refreshActivity(track);
                        return;
                    }
                });

        adb.setNegativeButton(R.string.CancelButton,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        return;
                    }
                });

        adb.show();

    }

    private void editTagDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory
                .inflate(R.layout.dialog_edit_track_tags, null);

        final EditText txtTags = (EditText) view
                .findViewById(R.id.editTrackTagsText);
        txtTags.setHint(trackTags.getText());

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setView(view);
        adb.setTitle(R.string.editTrackTags);
        adb.setMessage(R.string.hint_edit_track_tags);
        adb.setPositiveButton(R.string.save,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Track track = trackUtil.loadTrack(getIntent()
                                .getLongExtra("id", -1));
                        track.setTags(txtTags.getText().toString());
                        trackUtil.updateTrack(track);
                        refreshActivity(track);
                        return;
                    }
                });

        adb.setNegativeButton(R.string.CancelButton,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        return;
                    }
                });

        adb.show();

    }

    private void editNameDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.edit_track_name, null);

        final EditText txtName = (EditText) view
                .findViewById(R.id.editTracknameText);
        txtName.setHint(trackDetails.getText());

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setView(view);
        adb.setTitle(R.string.editTrackName);
        adb.setPositiveButton(R.string.save,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Track track = trackUtil.loadTrack(getIntent()
                                .getLongExtra("id", -1));
                        track.setTrackName(txtName.getText().toString());
                        trackUtil.updateTrack(track);
                        refreshActivity(track);
                        return;
                    }
                });

        adb.setNegativeButton(R.string.CancelButton,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        return;
                    }
                });

        adb.show();

    }

    private void refreshActivity(Track track) {
        Intent intent = getIntent();
        intent.putExtra("name", track.getTrackName());
        intent.putExtra("trackpoints", track.getTrackPoints().size());
        intent.putExtra("id", track.getID());
        intent.putExtra("description", track.getDescription());
        intent.putExtra("tags", track.getTags());

        finish();

        startActivity(intent);
    }

}
