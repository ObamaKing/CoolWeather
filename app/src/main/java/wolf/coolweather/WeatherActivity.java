package wolf.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import wolf.coolweather.gson.Forecast;
import wolf.coolweather.gson.Weather;
import wolf.coolweather.util.HttpUtil;
import wolf.coolweather.util.Utility;

public class WeatherActivity extends AppCompatActivity {

    private static final String PROFESSIONAL_KEY = "06c5706a05a444d589b0f061fd958f78";

    private static final String BING_PIC_URL = "http://guolin.tech/api/bing_pic";

    @BindView(R.id.title_city)
    TextView titleCity;
    @BindView(R.id.title_update_time)
    TextView titleUpdateTime;
    @BindView(R.id.degree_text)
    TextView degreeText;
    @BindView(R.id.weather_info_text)
    TextView weatherInfoText;
    @BindView(R.id.forecast_layout)
    LinearLayout forecastLayout;
    @BindView(R.id.aqi_text)
    TextView aqiText;
    @BindView(R.id.pm25_text)
    TextView pm25Text;
    @BindView(R.id.comfort_text)
    TextView comfortText;
    @BindView(R.id.car_wash_text)
    TextView carWashText;
    @BindView(R.id.sport_text)
    TextView sportText;
    @BindView(R.id.weather_layout)
    ScrollView weatherLayout;
    @BindView(R.id.bing_pic_img)
    ImageView bingPicImg;
    @BindView(R.id.swipe_refresh)
    public SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.nav_button)
    Button navButton;
    @BindView(R.id.drawer_layout)
    public DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        String weatherId;
        if (weatherString == null) {
            weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        } else {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        }

        swipeRefresh.setOnRefreshListener(() -> requestWeather(weatherId));
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }
    }

    private void loadBingPic() {
        HttpUtil.sendOkHttpRequest(BING_PIC_URL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(() -> Glide.with(WeatherActivity.this)
                        .load(bingPic).into(bingPicImg));
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;

        titleCity.setText(cityName);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        titleUpdateTime.setText(updateTime);
        forecastLayout.removeAllViews();

        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(WeatherActivity.this)
                    .inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);

            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);

            forecastLayout.addView(view);

            if (weather.aqi != null) {
                aqiText.setText(weather.aqi.aqiCity.aqi);
                pm25Text.setText(weather.aqi.aqiCity.pm25);
            }

            String comfortInfo = "舒适度:" + weather.suggestion.comfort.info;
            String carWashInfo = "洗车指数:" + weather.suggestion.carWash.info;
            String sportInfo = "运动建议:" + weather.suggestion.sport.info;

            comfortText.setText(comfortInfo);
            carWashText.setText(carWashInfo);
            sportText.setText(sportInfo);
            forecastLayout.setVisibility(View.VISIBLE);
        }
    }

    public void requestWeather(String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key="
                + PROFESSIONAL_KEY;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                            Toast.makeText(WeatherActivity.this,
                                    "获取天气信息失败", Toast.LENGTH_SHORT).show();
                            swipeRefresh.setRefreshing(false);
                        }
                );

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseText = response.body().string();
                Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(() -> {
                    if (weather != null && weather.status.equals("ok")) {
                        SharedPreferences.Editor editor = PreferenceManager
                                .getDefaultSharedPreferences(WeatherActivity.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                        showWeatherInfo(weather);
                    } else {
                        Toast.makeText(WeatherActivity.this,
                                "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                    swipeRefresh.setRefreshing(false);
                });
            }
        });
        loadBingPic();
    }

    @OnClick(R.id.nav_button)
    public void onViewClicked() {
        drawerLayout.openDrawer(Gravity.START);
    }
}
