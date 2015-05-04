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

import io.github.data4all.view.AddressSuggestionView;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;


/**
 * this class is the TwoColumnAdapter.
 * 
 * @author: Maurice Boyke
 */
public class TwoColumnAdapter extends BaseAdapter {

    private Context context;
    private List<String> listKey;
    private List<String> listValue;
    private AddressSuggestionView suggestionView;

    /**
     * Default Constructor.
     * 
     * @param context
     *            The Context of the Application
     * @param listKey
     *            The List of Keys
     * @param listValue
     *            The List of Values
     */
    public TwoColumnAdapter(Context context, List<String> listKey,
            List<String> listValue) {
        this.context = context;
        this.listKey = listKey;
        this.listValue = listValue;

    }

    @Override
    public int getCount() {
        return listKey.size();
    }

    @Override
    public Object getItem(int position) {
        return listKey.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TwoLineListItem twoLineListItem;

        if (convertView == null) {
            final LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            twoLineListItem = (TwoLineListItem) inflater.inflate(
                    android.R.layout.simple_list_item_2, null);
        } else {
            twoLineListItem = (TwoLineListItem) convertView;
        }

        final TextView text1 = twoLineListItem.getText1();
        final TextView text2 = twoLineListItem.getText2();

        text1.setText(listKey.get(position));
        if (listValue.size() > position) {
        text2.setText(listValue.get(position));
        } else {
        text2.setText("");	
        }
        if (suggestionView != null) {
           suggestionView.savedTwoLineListItem(listKey.get(position), text2);
        }
        return twoLineListItem;
    }

	public void setSuggestionView(AddressSuggestionView suggestionView) {
		this.suggestionView = suggestionView;
	}

}
