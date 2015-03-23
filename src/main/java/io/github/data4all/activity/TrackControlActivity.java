package io.github.data4all.activity;

import io.github.data4all.R;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.Track;
import io.github.data4all.util.TrackUtility;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class TrackControlActivity extends AbstractActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    private TrackUtility trackUtil;

    private Track track;

    private TextView textViewOpenedTrack;

    private TextView textViewTrackId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_track_control);

        this.trackUtil = new TrackUtility(this);

        textViewOpenedTrack = (TextView) findViewById(R.id.textViewTrackOpened);
        textViewOpenedTrack.setText(getOpenTrackName());

        textViewTrackId = (TextView) findViewById(R.id.textViewTrackId);
        textViewTrackId.setText(getOpenTrackId());

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
    protected void onPause() {
    };

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
    protected void onRestart() {
    };

    @Override
    protected void onStop() {
    };

    @Override
    protected void onWorkflowFinished(Intent data) {
        // TODO Auto-generated method stub

    }

}
