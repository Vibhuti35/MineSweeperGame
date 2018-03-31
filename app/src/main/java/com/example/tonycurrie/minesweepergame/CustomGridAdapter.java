package com.example.tonycurrie.minesweepergame;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomGridAdapter extends BaseAdapter{
    private Context context;
    LayoutInflater inflater;
    public ArrayList<String> items = new ArrayList<String>();

    public CustomGridAdapter(Context context,ArrayList<String> items)
    {
        this.context=context;
        this.items= items;
        inflater=(LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if(view==null)
        {
            view=inflater.inflate(R.layout.cell,null);
        }
        TextView tv=(TextView) view.findViewById(R.id.textview);
        tv.setText(items.get(i));
        tv.setBackgroundResource(R.drawable.border);
        //tv.setBackgroundResource(R.color.grey);
       // tv.setBackgroundColor(Color.parseColor("#7F7F7F"));
        //tv.setLayoutParams(new GridView.LayoutParams(10, 10));
        //tv.setBackgroundResource(R.drawable.item);
       // final Button ib=(Button)view.findViewById(R.id.IB);
        //ib.setText(items.get(i));


        return view;
    }
}
