package com.example.spacelite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NoteAdapter extends SimpleAdapter {

    private Context mContext;
    public LayoutInflater inflater=null;
    public NoteAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        mContext = context;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        try{
            if(convertView==null)
                vi = inflater.inflate(R.layout.cus_window, null);
            HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
            TextView showTitle = vi.findViewById(R.id.textView);
            String title = (String) data.get("title");
            showTitle.setText(title);

        }catch (IndexOutOfBoundsException e){

        }

        return vi;
    }
}