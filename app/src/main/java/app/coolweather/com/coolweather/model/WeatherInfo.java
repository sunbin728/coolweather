package app.coolweather.com.coolweather.model;

/**
 * Created by sunbin on 15-8-12.
 */
public class WeatherInfo {
    private int id;
    private String countycode;
    private String cityname;
    private String temp1;
    private String temp2;
    private String weatherdesp;
    private String publishtime;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getCountyCode() {

        return countycode;
    }
    public void setCountyCode(String _countycode) {
        this.countycode = _countycode;
    }

    public String getCityName() {
        return cityname;
    }
    public void setCityName(String _cityname) {
        this.cityname = _cityname;
    }

    public String getTemp1() {
        return temp1;
    }
    public void setTemp1(String _temp1) {
        this.temp1 = _temp1;
    }

    public String getTemp2() {
        return temp2;
    }
    public void setTemp2(String _temp2) {
        this.temp2 = _temp2;
    }

    public String getWeatherDesp() {
        return weatherdesp;
    }
    public void setWeatherDesp(String _weatherdesp) {
        this.weatherdesp = _weatherdesp;
    }

    public String getPublishTime() {
        return publishtime;
    }
    public void setPublishTime(String _publishtime) {
        this.publishtime = _publishtime;
    }
}
