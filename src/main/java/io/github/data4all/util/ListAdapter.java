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

            final LayoutInflater inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(res, parent, false);

            holder = new TrackHolder();
            holder.name = (TextView) v.findViewById(R.id.gps_item);

            v.setTag(holder);
        } else {
            holder = (TrackHolder) v.getTag();
        }

        final Track track = trackList.get(position);
        holder.name.setText(track.getTrackName());

        return v;

    }

    /**
     * Holder for several views
     * @author sbrede
     *
     */
    static class TrackHolder {
        TextView name;
    }
}