package io.github.data4all.util;

import io.github.data4all.R;
import io.github.data4all.model.data.Track;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Custom adapter for the GpsTrackListActivity.
 * @author sbrede
 *
 */
public class ListAdapter extends ArrayAdapter<Track> {

    private int res;
    private List<Track> trackList;

    /**
     * Constructor.
     * @param context The corresponding {@link Context}
     * @param resource Id of the layout
     * @param tracks List of tracks
     */
    public ListAdapter(Context context, int resource, List<Track> tracks) {
        super(context, resource, tracks);
        this.res = resource;
        this.trackList = tracks;
    }

    /* (non-Javadoc)
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        TrackHolder holder = null;

        if (v == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(res, parent, false);

            holder = new TrackHolder();
            holder.name = (TextView) v.findViewById(R.id.gps_item);
            //holder.id = (TextView) v.findViewById(R.id.gps_item_id);

            v.setTag(holder);
        } else {
            holder = (TrackHolder) v.getTag();
        }

        Track track = trackList.get(position);
        holder.name.setText(track.getTrackName());
        //holder.id.setText(Long.toString(track.getID()));

        return v;

    }

    /**
     * Holder for several views
     * @author sbrede
     *
     */
    static class TrackHolder {
        TextView name;
        //TextView id;
    }
}