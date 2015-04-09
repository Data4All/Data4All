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
package io.github.data4all.model;

import io.github.data4all.R;
import io.github.data4all.activity.AbstractActivity;
import io.github.data4all.activity.ShowPictureActivity;
import io.github.data4all.logger.Log;
import io.github.data4all.model.data.Node;
import io.github.data4all.util.Gallery;
import io.github.data4all.util.Gallery.Informations;
import io.github.data4all.util.MapUtil;
import io.github.data4all.view.D4AMapView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.location.Location;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * This ListAdapter provides the content of the gallery to be shown in a
 * ListView. It provides also methods to delete and tag single images and delete
 * all images.
 * 
 * @author tbrose
 *
 */
public class GalleryListAdapter implements ListAdapter {

    /**
     * The sample size for the thumbnails.
     */
    private static final int SAMPLE_SIZE = 8;

    /**
     * The id for the "delete all"-item.
     */
    private static final int DELETE_ALL_ID = 2508;

    private Gallery gallery;

    private List<DataSetObserver> observer = new ArrayList<DataSetObserver>();

    private AbstractActivity context;

    /**
     * @param context
     */
    public GalleryListAdapter(AbstractActivity context) {
        this.context = context;
        gallery = new Gallery(context);
    }

    /**
     * Removes the image of the given view id from the gallery. If the id is
     * {@code DELETE_ALL_ID} all images will be deleted.
     * 
     * @param id
     *            The id of the ListView
     */
    public void removeImage(long id) {
        if (id == DELETE_ALL_ID) {
            this.removeAllImages();
        } else {
            gallery.deleteImage(id);
            this.invalidate();
        }
    }

    /**
     * Delete all images from the gallery.
     */
    public void removeAllImages() {
        for (long id : gallery.getImages()) {
            gallery.deleteImage(id);
        }
        this.invalidate();
    }

    /**
     * Starts the tag-process for the image of the view with the given id. If
     * the id is {@code DELETE_ALL_ID} all images will be deleted.
     * 
     * @param id
     *            The id of the ListView
     */
    public void tagImage(long id) {
        if (id == DELETE_ALL_ID) {
            this.removeAllImages();
        } else {
            try {
                final Informations infos = gallery.getImageInformations(id);
                final File imageFile = gallery.getImageFile(id);

                final Bundle extras = new Bundle();
                extras.putLong(Gallery.GALLERY_ID_EXTRA, id);

                ShowPictureActivity.startActivity(context, imageFile,
                        infos.getParameters(), infos.getOrientation(),
                        infos.getDimension(), extras);
            } catch (IOException e) {
                Log.e(this.getClass().getSimpleName(), "Error on tagImage("
                        + id + ")", e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        final int length = gallery.getImageFiles().length;
        if (length == 0) {
            return 0;
        } else {
            return length + 1;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public File getItem(int position) {
        if (position < 0) {
            return null;
        } else {
            final File[] files = gallery.getImageFiles();
            if (files.length > position) {
                return files[position];
            } else {
                return null;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        if (position >= 0) {
            final long[] files = gallery.getImages();
            if (files.length > 0 && files.length == position) {
                return DELETE_ALL_ID;
            } else if (files.length > position) {
                return files[position];
            }
        }
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItemViewType(int)
     */
    @Override
    public int getItemViewType(int position) {
        if (this.getItemId(position) == DELETE_ALL_ID) {
            return 0;
        } else {
            return 1;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View result = convertView;
        Layout layout;

        // inflate the "delete all"-item
        if (this.getItemId(position) == DELETE_ALL_ID) {
            if (result == null) {
                final LayoutInflater inflater =
                        LayoutInflater.from(parent.getContext());
                result =
                        inflater.inflate(R.layout.view_listitem_deleteall, null);
            }
            return result;
        }

        // inflate the "normal" item, if there is a convertView get the
        // attachment
        if (result == null) {
            final LayoutInflater inflater =
                    LayoutInflater.from(parent.getContext());
            result = inflater.inflate(R.layout.view_listitem, null);
            layout =
                    new Layout(result.findViewById(R.id.gallery_date_text),
                            result.findViewById(R.id.gallery_thumbnail),
                            result.findViewById(R.id.gallery_map));
            result.setTag(layout);
        } else {
            layout = (Layout) convertView.getTag();
        }

        layout.text.setText(this.getDateText(this.getItemId(position)));

        // load and show the image
        try {
            final Options options = new Options();
            options.inSampleSize = SAMPLE_SIZE;
            final Bitmap bitmap =
                    BitmapFactory.decodeFile(
                            gallery.getImageFile(this.getItemId(position))
                                    .getAbsolutePath(), options);
            layout.image.setImageBitmap(bitmap);
            if (bitmap.getWidth() > bitmap.getHeight()) {
                layout.image.setRotation(90);
            } else {
                layout.image.setRotation(0);
            }
        } catch (FileNotFoundException e) {
            Log.e(this.getClass().getSimpleName(), "Error on getView("
                    + position + ")", e);
        }

        // load and show the location
        try {
            final Informations info =
                    gallery.getImageInformations(this.getItemId(position));
            final Location location = info.getParameters().getLocation();
            final Node node =
                    new Node(-1, location.getLatitude(),
                            location.getLongitude());

            layout.map.getController().setCenter(
                    MapUtil.getCenterFromOsmElement(node));
            layout.map
                    .setBoundingBox(MapUtil.getBoundingBoxForOsmElement(node));
            layout.map.setScrollable(false);
            layout.map.getOverlays().clear();
            layout.map.addOsmElementToMap(context, node);
            layout.map.postInvalidate();
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), "Error on getView("
                    + position + ")", e);
        }

        return result;
    }

    /**
     * Beautifies the given timestamp, so it is readable by humans.
     * 
     * @param timestamp
     *            A timestamp
     * @return A beautified, human readable time-string
     */
    private String getDateText(long timestamp) {
        final long now = System.currentTimeMillis();
        final long diff = (now / 1000) - (timestamp / 1000);

        String result = null;

        if (diff < 60) {
            result = context.getString(R.string.gallery_sec, "🕒");
        } else if (diff < 60 * 60) {
            final int min = (int) (diff / 60);
            if (min == 1) {
                result = context.getString(R.string.gallery_min, "🕒");
            } else {
                result = context.getString(R.string.gallery_mins, "🕒 " + min);
            }
        } else if (diff < 60 * 60 * 24) {
            final int hour = (int) (diff / 3600);
            if (hour == 1) {
                result = context.getString(R.string.gallery_hour, "🕒");
            } else {
                result =
                        context.getString(R.string.gallery_hours, "🕒 " + hour);
            }
        } else if (isYesterday(timestamp)) {
            result =
                    context.getString(R.string.gallery_yesterday, "🕒 ",
                            DateFormat.format("HH:mm", timestamp));
        } else {
            result = "🕒 " + DateFormat.format("dd.MM HH:mm", timestamp);
        }
        return result;
    }

    /**
     * Determines if a timestamp was yesterday.
     * 
     * @param timestamp
     *            A timestamp
     * @return Whether or not this timestamp was yesterday
     */
    private static boolean isYesterday(long timestamp) {
        final Calendar cal = Calendar.getInstance();
        final int currentDayNumber = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTimeInMillis(timestamp);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        final int givenDayNumber = cal.get(Calendar.DAY_OF_MONTH);
        return givenDayNumber == currentDayNumber;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getViewTypeCount()
     */
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#hasStableIds()
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return gallery.getImageFiles().length == 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#registerDataSetObserver(android.database.
     * DataSetObserver)
     */
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        if (!this.observer.contains(observer)) {
            this.observer.add(observer);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#unregisterDataSetObserver(android.database.
     * DataSetObserver)
     */
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        this.observer.remove(observer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.ListAdapter#areAllItemsEnabled()
     */
    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.ListAdapter#isEnabled(int)
     */
    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    /**
     * Notify all obervers, that the content has changed.
     */
    public void invalidate() {
        for (DataSetObserver o : this.observer) {
            o.onChanged();
        }
    }

    /**
     * This class holds all Views of a ListItem for faster accessibility.
     * 
     * @author tbrose
     */
    private static final class Layout {
        private final TextView text;
        private final ImageView image;
        private final D4AMapView map;

        private Layout(View text, View image, View map) {
            super();
            this.text = (TextView) text;
            this.image = (ImageView) image;
            this.map = (D4AMapView) map;
        }
    }
}
