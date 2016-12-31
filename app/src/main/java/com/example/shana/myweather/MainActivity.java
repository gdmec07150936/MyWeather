package com.example.shana.myweather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity  {

    private List<Map<String,Object>> list;
    private ListView lv;
    private String mBaseUrl="http://v.juhe.cn/weather/index?format=2&cityname=";
    private String myKey="&key=22c4c3d87aceaf1d1e8288be71084c0c";
    private RequestQueue rq;
    private Handler handler;
    private MyAdapter myadapter;
    private Map<String,List<String>> city=new HashMap<String,List<String>>();
    private Spinner p_sp, c_sp;
    private TextView tv_city;
    private String cityName;

//    http://v.juhe.cn/weather/index?format=2&cityname=%E8%8B%8F%E5%B7%9E&key=您申请的KEY
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        sendDataAndGet();
        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                String rel=message.getData().getString("result");
                parseData(rel);
                return false;
            }
        });
    }

    private void initView() {
        tv_city= (TextView) findViewById(R.id.tv_city);
        lv= (ListView) findViewById(R.id.lv_myweather);
        myadapter=new MyAdapter(this,list);
        lv.setAdapter(myadapter);
    }

    private void initData() {
        rq= Volley.newRequestQueue(this);
        list=new ArrayList<Map<String, Object>>();
        initProvinces();
        cityName="广州";//默认初始城市为广州
    }

    private void parseData(String rel) {
        try {

            JSONObject json=new JSONObject(rel);
            Map<String,Object> map=new HashMap<String,Object>();
                JSONArray future=new JSONArray(json.get("future").toString());
            for(int i=0;i<6;i++){
                String future_tep=new JSONObject(future.get(i).toString()).getString("temperature");
                String future_status=new JSONObject(future.get(i).toString()).getString("weather");
                String future_wind=new JSONObject(future.get(i).toString()).getString("wind");
                String future_date=new JSONObject(future.get(i).toString()).getString("date");
                String future_week=new JSONObject(future.get(i).toString()).getString("week");
                map=new HashMap<String,Object>();
                map.put("tep",future_tep);
                map.put("status",future_status);
                map.put("wind",future_wind);
                map.put("date",future_date);
                map.put("day",future_week);
                list.add(map);
            }
            myadapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void sendDataAndGet() {

        JsonObjectRequest json=new JsonObjectRequest(mBaseUrl+cityName+myKey, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                    try {
                    Log.i("info",jsonObject.getString("result"));
                    Message message=Message.obtain(handler);
                    Bundle data=new Bundle();
                    data.putString("result",jsonObject.getString("result"));
                    message.setData(data);
                    message.sendToTarget();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MainActivity.this,volleyError.toString(),Toast.LENGTH_LONG).show();
            }
        });
        rq.add(json);
    }
    public void refresh(View v){
        list.clear();
        sendDataAndGet();
        Toast.makeText(MainActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();
    }
    public void setting(View v){
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("选择城市");
        LayoutInflater mInflater=LayoutInflater.from(this);
        View sp=mInflater.inflate(R.layout.city_choice,null);
        p_sp= (Spinner) sp.findViewById(R.id.province_sp);
        c_sp= (Spinner) sp.findViewById(R.id.city_sp);
        p_sp.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,new ArrayList<String>(
                city.keySet()
        )));
        p_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String pname= p_sp.getSelectedItem().toString();
                c_sp.setAdapter(new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_spinner_item,city.get(pname)));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        dialog.setView(sp);

        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String pname= c_sp.getSelectedItem().toString();
                tv_city.setText(pname);
                cityName=pname;
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }

    private void initProvinces(){
        AssetManager asserManager=getAssets();
        SaxHandler sax=new SaxHandler();
        InputStream is=null;
        try {
            is=asserManager.open("City.xml");
            SAXParserFactory factory=SAXParserFactory.newInstance();
            SAXParser parser=factory.newSAXParser();
            parser.parse(is,sax);
            city=sax.getCityMap();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
