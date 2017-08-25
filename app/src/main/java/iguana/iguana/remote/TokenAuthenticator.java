package iguana.iguana.remote;

import android.content.Context;
import android.content.SharedPreferences;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.HashMap;

import iguana.iguana.app.MainActivity;
import iguana.iguana.events.new_token;
import iguana.iguana.events.rtoken_invalid;
import iguana.iguana.models.Token;
import iguana.iguana.service.TokenService;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;

public class TokenAuthenticator implements Authenticator {

    private String credentials;
    private Context context;

    public TokenAuthenticator(String token, Context activity) {
        this.credentials = token;
        this.context = activity;
    }

    public String refresh_token(){
        HashMap body = new HashMap<>();
        SharedPreferences sharedPref = context.getSharedPreferences("api", Context.MODE_PRIVATE);
        String refresh_token =  sharedPref.getString("api_refresh_token", "");
        String url =  sharedPref.getString("api_url", "");
        body.put("refresh_token", refresh_token);
        body.put("client_id", "iguana");
        body.put("api_type", "iguana");
        body.put("grant_type", "grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer");
        SharedPreferences.Editor editor = sharedPref.edit();
        System.out.println("WE are in REFRESH");
        Call<Token> result = ApiUtils.getAPIService(url, null, null).refreshToken(body);
        retrofit2.Response<Token> response = null;
        try {
            response = result.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response == null)
            return null;
        if (response.isSuccessful()) {
            editor.putString("api_token", response.body().getToken());
            editor.putString("api_refresh_token", response.body().getRefreshToken());
            editor.commit();
        } else {
            EventBus.getDefault().post(new rtoken_invalid("TEST,TEST"));
            return null;
        }
        EventBus.getDefault().post(new new_token("New token!"));
        return response.body().getToken();
    }


    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        TokenService service = new TokenService();
        credentials = refresh_token();
        if(credentials == null)
            return null;

        // Add new header to rejected request and retry it
        return response.request().newBuilder()
                .header("Authorization", String.format("JWT %s", credentials)).build();    }
}
