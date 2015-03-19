package io.github.data4all.activity;

import io.github.data4all.R;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Activity to show details of a track
 * @author sbrede
 *
 */
public class TrackDetailsActivity extends AbstractActivity {

    private TextView trackDetails;
    private TextView trackId;
    private TextView trackPointCount;

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_details);

        trackDetails = (TextView) findViewById(R.id.textViewTrackDetails);
        trackId = (TextView) findViewById(R.id.textViewTrackId);
        trackPointCount = (TextView) findViewById(R.id.textViewTrackPointCount);

        Intent intent = getIntent();
        trackDetails.setText(intent.getStringExtra("name"));
        trackId.setText(Long.toString(intent.getLongExtra("id", -1)));
        trackPointCount.setText(Integer.toString(intent.getIntExtra("trackpoints", -1)));

    }

    /* (non-Javadoc)
     * @see io.github.data4all.activity.AbstractActivity#onWorkflowFinished(android.content.Intent)
     */
    @Override
    protected void onWorkflowFinished(Intent data) {
        // TODO Auto-generated method stub

    }

}
