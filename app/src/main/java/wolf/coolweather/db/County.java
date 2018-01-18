package wolf.coolweather.db;

import org.litepal.crud.DataSupport;


public class County extends DataSupport {

    //县城Id
    private int id;
    //县城名称
    private String countyName;
    //所属市ID
    private int cityId;
    //该县城对应天气ID
    private String weatherId;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}
