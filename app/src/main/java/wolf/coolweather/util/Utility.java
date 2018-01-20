package wolf.coolweather.util;


import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePalApplication;

import wolf.coolweather.db.City;
import wolf.coolweather.db.County;
import wolf.coolweather.db.Province;
import wolf.coolweather.gson.Weather;


/**
 * 解析遍历后的Json数据
 * */
public class Utility {


    /**
     * 解析服务器返回的省份数据
     * */
    public static boolean handleProvinceResponse(@NonNull String response){

        if (TextUtils.isEmpty(response)){
            try {
                JSONArray allProvince = new JSONArray(response);
                for (int i =0;;i++){
                    if (i==allProvince.length()){
                        break;
                    }
                    JSONObject provinceObject = allProvince.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.setProvinceName(provinceObject.getString("name"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    /**
     *解析服务器返回的市区数据
     * */
    public static boolean handleCityResponse(@NonNull String response,int provinceId){

        if (TextUtils.isEmpty(response)){
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i =0;;i++){
                    if (i==allCities.length()){
                        break;
                    }
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setId(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

/**
 *解析服务器返回的县城数据
 * */
    public static boolean handleCountyResponse(@NonNull String response,int cityId){

        if (TextUtils.isEmpty(response)){
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i =0;;i++){
                    if (i==allCounties.length()){
                        break;
                    }
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 处理天气回复
     * */
    public static Weather handleWeatherResponse(String response){

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
