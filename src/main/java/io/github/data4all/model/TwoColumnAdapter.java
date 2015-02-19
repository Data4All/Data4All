package io.github.data4all.model;


import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

public class TwoColumnAdapter extends BaseAdapter {

	private Context context;
	private List<String> listKey;
	private List<String> listValue;
	
	public  TwoColumnAdapter(Context context, List<String> listKey, List<String> listValue){
		this.context= context;
		this.listKey= listKey;
		this.listValue= listValue;
		
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
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            twoLineListItem = (TwoLineListItem) inflater.inflate(
            		android.R.layout.simple_list_item_2, null);
        } else {
            twoLineListItem = (TwoLineListItem) convertView;
        }

        TextView text1 = twoLineListItem.getText1();
        TextView text2 = twoLineListItem.getText2();

        text1.setText(listKey.get(position));
        text2.setText(listValue.get(position));

        return twoLineListItem;	}

}
