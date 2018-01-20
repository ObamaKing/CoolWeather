package wolf.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Now {

    @SerializedName("tem")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {

        @SerializedName("text")
        public String info;
    }
}
