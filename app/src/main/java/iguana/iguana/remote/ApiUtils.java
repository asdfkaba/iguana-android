package iguana.iguana.remote;

import iguana.iguana.app.MainActivity;
import retrofit2.Retrofit;

public class ApiUtils {

    private ApiUtils() {}

    public static String BASE_URL;

    public static APIService getAPIService(String url, String token, MainActivity activity) {
        BASE_URL = url;
        Retrofit api =  RetrofitClient.getClient(BASE_URL, token, activity);
        if (api == null)
            return null;
        return api.create(APIService.class);
    }
}