package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.Track;
import io.github.data4all.util.TrackUtility;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TrackControlActivity extends AbstractActivity implements
        OnClickListener {

    public static final String TAG = TrackControlActivity.class.getSimpleName();

    private TrackUtility trackUtil;

    private Track track;

    private TextView textViewOpenedTrack;

    private TextView textViewTrackId;

    private Button recButton;

    private Button stopButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_track_control);

        this.trackUtil = new TrackUtility(this);

        textViewOpenedTrack = (TextView) findViewById(R.id.textViewTrackOpened);
        textViewOpenedTrack.setText(getOpenTrackName());

        textViewTrackId = (TextView) findViewById(R.id.textViewTrackId);
        textViewTrackId.setText(getOpenTrackId());

        recButton = (Button) findViewById(R.id.recordTrackButton);
        recButton.setOnClickListener(this);

        stopButton = (Button) findViewById(R.id.stopTrackButton);
        stopButton.setOnClickListener(this);

    }

    private String getOpenTrackId() {
        Track track = trackUtil.getLastTrack();
        if (track != null) {
            return String.valueOf(track.getID());
        }

        String str = getString(R.string.no_track_opened);
        return str;
    }

    private String getOpenTrackName() {
        Track track = trackUtil.getLastTrack();
        if (track != null) {
            return track.getTrackName();
        }

        String str = getString(R.string.no_track_opened);
        return str;

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.track = trackUtil.getLastTrack();
        if (track != null) {
            Log.d(TAG,
                    "onResume(), getting last opened track: "
                            + track.toString());
        }
    }

    @Override
    protected void onWorkflowFinished(Intent data) {
        finishWorkflow(data);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.recordTrackButton:
            Log.d(TAG, "Click in record");
            if (trackUtil.getLastTrack() == null) {
                trackUtil.startNewTrack();
                finish();
                startActivity(getIntent());
            }
            break;
        case R.id.stopTrackButton:
            Log.d(TAG, "Click on stop");
            Track tmp = trackUtil.getLastTrack();
            if (tmp != null) {
                trackUtil.saveTrack(tmp);
                finish();
                startActivity(getIntent());
            }
            break;
        default:
            break;
        }

    }

}
