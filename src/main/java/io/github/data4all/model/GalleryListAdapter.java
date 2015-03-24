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

import io.github.data4all.util.Gallery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * @author tbrose
 *
 */
public class GalleryListAdapter implements ListAdapter {

    private Gallery gallery;

    private List<DataSetObserver> observer = new ArrayList<DataSetObserver>();

    private Context context;

    /**
     * @param context
     */
    public GalleryListAdapter(Context context) {
        this.context = context;
        gallery = new Gallery(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return gallery.getImageFiles().length;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public File getItem(int position) {
        return gallery.getImageFiles()[position];
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return gallery.getImages()[position];
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItemViewType(int)
     */
    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView result;
        if (convertView == null) {
            result = new TextView(context);
        } else {
            result = (TextView) convertView;
        }

        result.setText("Item: " + getItemId(position));

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getViewTypeCount()
     */
    @Override
    public int getViewTypeCount() {
        return 1;
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

}
