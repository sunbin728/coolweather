package app.coolweather.com.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import app.coolweather.com.coolweather.model.City;
import app.coolweather.com.coolweather.model.CoolWeatherDB;
import app.coolweather.com.coolweather.model.County;
import app.coolweather.com.coolweather.model.Province;
import app.coolweather.com.coolweather.model.WeatherInfo;

/**
 * Created by sunbin on 15-8-5.
 */
public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB
                                                                       coolWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    // 将解析出来的数据存储到Province表
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,
                                               String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    // 将解析出来的数据存储到City表
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,
                                                 String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    // 将解析出来的数据存储到County表
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的JSON数据,并将解析出的数据存储到本地。
     */
    public static void handleWeatherResponse(CoolWeatherDB coolWeatherDB, String response, String countyCode) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(coolWeatherDB, countyCode, cityName, weatherCode, temp1, temp2,
                    weatherDesp, publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 将服务器返回的所有天气信息存储到SharedPreferences文件中。
     */
    public static void saveWeatherInfo(CoolWeatherDB coolWeatherDB, String countyCode, String cityName,
                                       String weatherCode, String temp1, String temp2, String weatherDesp, String
                                               publishTime) {
        WeatherInfo weatherInfo = new WeatherInfo();
        weatherInfo.setCountyCode(countyCode);
        weatherInfo.setCityName(cityName);
        weatherInfo.setTemp1(temp1);
        weatherInfo.setTemp2(temp2);
        weatherInfo.setWeatherDesp(weatherDesp);
        weatherInfo.setPublishTime(publishTime);
        coolWeatherDB.saveWeatherInfo(weatherInfo);
    }

    public static void addCounty(Context context, String countyCode) {

        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(context);
        String countys = prefs.getString("countys", "");

        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putString("countycode_selected", countyCode);
        if (countys == ""){
            countys = countyCode;
        }else{
            String[] countyList = countys.split("\\|");
            Boolean iscontain = false;
            String tempcodes = "";
            for(int i=0; i< countyList.length; i++){
                String code = countyList[i];
                if (i!=0){
                    tempcodes = tempcodes + code + "|";
                }
                if (code.compareTo(countyCode) == 0){
                    iscontain = true;
                }
            }
            if (!iscontain){
                if(countyList.length < 3){
                    countys = countys + "|" + countyCode;
                }else {
                    countys = tempcodes + countyCode;
                }
            }

        }
        editor.putString("countys", countys);
        editor.commit();
    }
}
