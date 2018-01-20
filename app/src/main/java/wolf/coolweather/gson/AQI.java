package wolf.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class AQI {

    @SerializedName("city")
    public AQICity aqiCity;

    public class AQICity{

        @SerializedName("aqi")
        public String aqi;

        @SerializedName("pm25")
        public String pm25;
    }
}
