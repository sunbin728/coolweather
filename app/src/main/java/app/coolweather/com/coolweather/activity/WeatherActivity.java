package app.coolweather.com.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.coolweather.com.coolweather.R;
import app.coolweather.com.coolweather.model.CoolWeatherDB;
import app.coolweather.com.coolweather.model.WeatherInfo;
import app.coolweather.com.coolweather.service.AutoUpdateService;
import app.coolweather.com.coolweather.util.HttpCallbackListener;
import app.coolweather.com.coolweather.util.HttpUtil;
import app.coolweather.com.coolweather.util.Utility;

/**
 * Created by sunbin on 15-8-5.
 */
public class WeatherActivity extends Activity implements View.OnClickListener {
    private RelativeLayout backgroudLayout;
    private LinearLayout weatherInfoLayout;
    /**
     * 用于显示城市名
     */
    private TextView cityNameText;
    /**
     * 用于显示发布时间
     */
    private TextView publishText;
    /**
     * 用于显示天气描述信息
     */
    private TextView weatherDespText;
    /**
     * 用于显示气温1
     */
    private TextView temp1Text;
    /**
     * 用于显示气温2
     */
    private TextView temp2Text;
    /**
     * 用于显示当前日期
     */
    private TextView currentDateText;
    /**
     * 切换城市按钮
     */
    private Button switchCity;
    /**
     * 更新天气按钮
     */
    private Button editcity;
    private SwipeRefreshLayout mSwipeLayout;
    private CoolWeatherDB coolWeatherDB;

    //手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
    float x1 = 0;
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;

    String selectedCountyCode;
    int selectItem = 0;
    List<TextView> itemList;
    List<String> countyList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        initctrl();
        publishText.setText("同步中...");
        weatherInfoLayout.setVisibility(View.INVISIBLE);
        cityNameText.setVisibility(View.INVISIBLE);
        selectedCountyCode = getIntent().getStringExtra("county_code");
        countyList =  new ArrayList<String>();
        queryWeatherCode(selectedCountyCode);
    }

    private void initctrl(){
        // 初始化各控件
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        backgroudLayout = (RelativeLayout) findViewById(R.id.weather_bg_layout);
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);
        switchCity = (Button) findViewById(R.id.switch_city);
        editcity = (Button) findViewById(R.id.edit_city);
        switchCity.setOnClickListener(this);

        itemList = new ArrayList<TextView>();
        itemList.add((TextView) findViewById(R.id.select0));
        itemList.add((TextView) findViewById(R.id.select1));
        itemList.add((TextView) findViewById(R.id.select2));

        editcity.setOnClickListener(this);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                              public void onRefresh() {
                                                  mSwipeLayout.setRefreshing(true);
                                                  refresh_weather();
                                                  mSwipeLayout.setRefreshing(false);
                                              }
                                          }
        );
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_city:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.edit_city:
                editCounty();
                break;
            default:
                break;
        }
    }
    /**
     * 查询县级代号所对应的天气代号。
     */
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" +
                countyCode + ".xml";
        queryFromServer(address, "countyCode", countyCode);
    }
    /**
     * 查询天气代号所对应的天气。
     */
    private void queryWeatherInfo(String weatherCode, String countyCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" +
                weatherCode + ".html";
        queryFromServer(address, "weatherCode", countyCode);
    }
    /**
     * 根据传入的地址和类型去向服务器查询天气代号或者天气信息。
     */
    private void queryFromServer(final String address, final String type, final String countyCode) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        // 从服务器返回的数据中解析出天气代号
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode, countyCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    // 处理服务器返回的天气信息
                    Utility.handleWeatherResponse(coolWeatherDB,
                            response, countyCode);
                    Utility.addCounty(WeatherActivity.this, countyCode);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }
    /**
     * 从SharedPreferences文件中读取存储的天气信息,并显示到界面上。
     */
    private void showWeather() {
        getCountys();
        refreshItemList(selectedCountyCode);
        WeatherInfo weatherInfo = coolWeatherDB.loadWeatherInfo(selectedCountyCode);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",
                Locale.CHINA);

        cityNameText.setText(weatherInfo.getCityName());
        temp1Text.setText(weatherInfo.getTemp1());
        temp2Text.setText(weatherInfo.getTemp2());
        weatherDespText.setText(weatherInfo.getWeatherDesp());
        publishText.setText("今天" + weatherInfo.getPublishTime() + "发布");
        currentDateText.setText(sdf.format(new Date()));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);

        String desp = weatherInfo.getWeatherDesp();
        if (desp.indexOf("晴") != -1){
            backgroudLayout.setBackgroundResource(R.drawable.sunny);
        }else if(desp.indexOf("雨") != -1) {
            backgroudLayout.setBackgroundResource(R.drawable.rain);
        }else if(desp.indexOf("阴") != -1) {
            backgroudLayout.setBackgroundResource(R.drawable.overcast);
        }else if(desp.indexOf("多云") != -1) {
            backgroudLayout.setBackgroundResource(R.drawable.cloudy);
        }else {
            backgroudLayout.setBackgroundResource(R.drawable.default_bg);
        }

        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    private void refresh_weather(){
        publishText.setText("同步中...");
        if (!TextUtils.isEmpty(selectedCountyCode)) {
            queryWeatherCode(selectedCountyCode);
        }
    }

    private void refreshItemList(String countyCode){
        int size = countyList.size();
        if (size == 1){
            itemList.get(0).setVisibility(View.INVISIBLE);
            itemList.get(1).setVisibility(View.INVISIBLE);
            itemList.get(2).setVisibility(View.INVISIBLE);
        }else if(size == 2){
            itemList.get(0).setVisibility(View.VISIBLE);
            itemList.get(1).setVisibility(View.VISIBLE);
            itemList.get(2).setVisibility(View.INVISIBLE);
        }else{
            itemList.get(0).setVisibility(View.VISIBLE);
            itemList.get(1).setVisibility(View.VISIBLE);
            itemList.get(2).setVisibility(View.VISIBLE);
        }

        for(int i=0; i<countyList.size(); i++){
            if(countyList.get(i).compareTo(countyCode) == 0){
                selectItem = i;
                refreshItemColor(i);
            }
        }
    }

    private void getCountys(){
        countyList.clear();
        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(this);
        String strCountys = prefs.getString("countys", "");
        if (strCountys != ""){
            String[] countys = strCountys.split("\\|");
            for(String county: countys){
                countyList.add(county);
            }
        }
    }

    private void refreshItemColor(int i){
        if (i >= 0 && i < 3) {
            for(int j=0; j< 3; j++) {
                if (i==j){
                    itemList.get(j).setTextColor(0xff1bd0ff);
                }else {
                    itemList.get(j).setTextColor(0xffffffff);
                }
            }
        }
    }

    private void editCounty(){
        Toast.makeText(WeatherActivity.this, "编辑", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this, ChooseAreaActivity.class);
//        intent.putExtra("from_weather_activity", true);
//        intent.putExtra("is_add_county", true);
//        startActivity(intent);
//        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //继承了Activity的onTouchEvent方法，直接监听点击事件
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            x1 = event.getX();
            y1 = event.getY();
        }
        if(event.getAction() == MotionEvent.ACTION_UP) {
            //当手指离开的时候
            x2 = event.getX();
            y2 = event.getY();
//            if(y1 - y2 > 50) {
//                Toast.makeText(WeatherActivity.this, "向上滑", Toast.LENGTH_SHORT).show();
//            } else if(y2 - y1 > 50) {
//                Toast.makeText(WeatherActivity.this, "向下滑", Toast.LENGTH_SHORT).show();
//            } else if(x1 - x2 > 50) {
//                Toast.makeText(WeatherActivity.this, "向左滑", Toast.LENGTH_SHORT).show();
//            } else if(x2 - x1 > 50) {
//                Toast.makeText(WeatherActivity.this, "向右滑", Toast.LENGTH_SHORT).show();
//            }
            if(x1 - x2 > 50 && y1 > 300) {
                if (selectItem < countyList.size() - 1) {
                    selectItem = selectItem + 1;
                    publishText.setText("同步中...");
                    selectedCountyCode = countyList.get(selectItem);
                    showWeather();
                }
            } else if(x2 - x1 > 50 && y1 > 300) {
                if (selectItem > 0) {
                    selectItem = selectItem - 1;
                    publishText.setText("同步中...");
                    selectedCountyCode = countyList.get(selectItem);
                    showWeather();
                }
            }
        }
        return super.onTouchEvent(event);
    }
}