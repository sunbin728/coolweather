package app.coolweather.com.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sunbin on 15-8-5.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
    /**
     * Province表建表语句
     */
    public static final String CREATE_PROVINCE = "create table Province ("
            + "id integer primary key autoincrement, "
            + "province_name text, "
            + "province_code text)";
    /**
     * City表建表语句
     */
    public static final String CREATE_CITY = "create table City ("
            + "id integer primary key autoincrement, "
            + "city_name text, "
            + "city_code text, "
            + "province_id integer)";
    /**
     * County表建表语句
     */
    public static final String CREATE_COUNTY = "create table County ("
            + "id integer primary key autoincrement, "
            + "county_name text, "
            + "county_code text, "
            + "city_id integer)";
    /**
     * WeatherInfo表建表语句
     */
    public static final String CREATE_WEATHERINFO = "create table WeatherInfo ("
            + "id integer primary key autoincrement, "
            + "countycode text, "
            + "cityname text, "
            + "temp1 text,"
            + "temp2 text,"
            + "weatherdesp text,"
            + "publishtime text)";

    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory
            factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE); // 创建Province表
        db.execSQL(CREATE_CITY); // 创建City表
        db.execSQL(CREATE_COUNTY); // 创建County表
        db.execSQL(CREATE_WEATHERINFO); // 创建WeatherInfo表
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }
}