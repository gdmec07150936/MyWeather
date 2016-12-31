package com.example.shana.myweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by hasee on 2016/12/14.
 */

public class MyAdapter extends BaseAdapter{
    private Context context;
    private List<Map<String,Object>> mList;
    private LayoutInflater mInfalter;
    public  MyAdapter(Context context,List<Map<String,Object>>  list){
        this.context=context;
        this.mList=list;
        mInfalter=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHodler hodler;
        if(view==null){
            hodler=new ViewHodler();
            view=mInfalter.inflate(R.layout.day,viewGroup,false);
            hodler.icon= (ImageView) view.findViewById(R.id.imageView);
            hodler.tep= (TextView) view.findViewById(R.id.tv_temp);
            hodler.status= (TextView) view.findViewById(R.id.tv_status);
            hodler.wind= (TextView) view.findViewById(R.id.tv_wind);
            hodler.date= (TextView) view.findViewById(R.id.tv_date);
            hodler.day= (TextView) view.findViewById(R.id.tv_day);
            view.setTag(hodler);
        }else{
            hodler= (ViewHodler) view.getTag();
        }
        for(int j=0;j<Weather.w.length;j++){
            if (mList.get(i).get("status").toString().equals(Weather.w[j])){
                hodler.icon.setImageResource(WeatherIcon.weather_icons[j]);
            }else{

            }
        }

        hodler.tep.setText(mList.get(i).get("tep").toString());
        hodler.status.setText(mList.get(i).get("status").toString());
        hodler.wind.setText(mList.get(i).get("wind").toString());
        hodler.date.setText(mList.get(i).get("date").toString());
        hodler.day.setText(mList.get(i).get("day").toString());
        return view;
    }
    class ViewHodler{
        ImageView icon;
        TextView tep;
        TextView status;
        TextView wind;
        TextView date;
        TextView day;
    }

}
