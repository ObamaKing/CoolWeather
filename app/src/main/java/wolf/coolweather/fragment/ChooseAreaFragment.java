package wolf.coolweather.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import wolf.coolweather.R;
import wolf.coolweather.db.City;
import wolf.coolweather.db.County;
import wolf.coolweather.db.Province;
import wolf.coolweather.util.HttpUtil;
import wolf.coolweather.util.Utility;

/**
 * 区域选择
 * */

public class ChooseAreaFragment extends Fragment {

    private static final String CHINA_URL = "http://guolin.tech/api/china";

    //省份层级
    public static final int LEVEL_PROVINCE = 0;
    //市区层级
    public static final int LEVEL_CITY = 1;
    //县城层级
    public static final int LEVEL_COUNTY = 2;
    //搜索服务器数据对话框
    private ProgressDialog progressDialog;

    private Button btnBack;

    private TextView tvTitle;

    private ListView ltPlaces;

    private ArrayAdapter<String> adapter;
    //列表数据
    private List<String> dataList = new ArrayList<>();
    //省份列表
    private List<Province> provinceList;
    //市区列表
    private List<City> cityList;
    //县城列表
    private List<County> countyList;
    //选中省份
    private Province selectedProvince;
    //选中市区
    private City selectedCity;
    //选中县城
    private County selectedCounty;
    //当前展示层级｛省份，市区，县城｝
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        initView(view);
        return view;
    }


    private void initView(View view) {

        btnBack = view.findViewById(R.id.btn_back);
        tvTitle = view.findViewById(R.id.tv_title);
        ltPlaces = view.findViewById(R.id.lt_places);

        btnBack.setOnClickListener((v)->{
            if (currentLevel==LEVEL_COUNTY){
                queryCity();
            }else if (currentLevel == LEVEL_CITY){
                queryProvince();
            }
        });

        queryProvince();
        adapter = new ArrayAdapter<>(Objects
                .requireNonNull(getContext(),"无法获取Context"),
                android.R.layout.simple_list_item_1,dataList);
        ltPlaces.setAdapter(adapter);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ltPlaces.setOnItemClickListener((adapterView,view,position,l)->{
            if (currentLevel==LEVEL_PROVINCE){
                selectedProvince=provinceList.get(position);
                queryCity();
            }else if (currentLevel==LEVEL_CITY){
                selectedCity=cityList.get(position);
                queryCounties();
            }else {
                selectedCounty = countyList.get(position);
            }
        });
    }

    private void queryFromServer(String chinaUrl, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(chinaUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(()->{
                    closeProgressDialog();
                    Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result =false;
                if ("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                }else if ("city".equals(type)){
                    result = Utility.handleCityResponse(responseText,
                            selectedProvince.getId());
                }else if ("county".equals(type)){
                    result = Utility.handleCountyResponse(responseText,selectedCity.getId());
                }
                if (result){
                    getActivity().runOnUiThread(()->{
                        closeProgressDialog();
                        if ("province".equals(type)){
                            queryProvince();
                        }else if ("city".equals(type)){
                            queryCity();
                        }else if ("county".equals(type)){
                            queryCounties();
                        }
                    });
                }
            }
        });
    }

    private void closeProgressDialog() {
        if (progressDialog!=null)
            progressDialog.dismiss();
    }

    private void showProgressDialog() {
        if (progressDialog==null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }


    private void queryProvince() {
        btnBack.setVisibility(View.GONE);
        tvTitle.setText("中国");
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size()>0){
            dataList.clear();
            for (Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            ltPlaces.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else {
            queryFromServer(CHINA_URL,"province");
        }
    }



    private void queryCity() {
        btnBack.setVisibility(View.VISIBLE);
        tvTitle.setText(selectedProvince.getProvinceName());
        cityList = DataSupport.findAll(City.class);
        if (cityList.size()>0){
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            ltPlaces.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            String cityUrl = CHINA_URL+"/"+selectedProvince.getProvinceCode();
            queryFromServer(cityUrl,"city");
        }
    }

    private void queryCounties() {
        btnBack.setVisibility(View.VISIBLE);
        tvTitle.setText(selectedCity.getCityName());
        countyList = DataSupport.findAll(County.class);
        if (countyList.size()>0){
            dataList.clear();
            for (County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            ltPlaces.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            String countyUrl = CHINA_URL+"/"+selectedProvince.getProvinceCode()
                    +"/"+selectedCity.getCityCode();
            queryFromServer(countyUrl,"county");
        }
    }

}
