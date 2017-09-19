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




    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        credentials = ((MainActivity) context).refresh_token();
        if(credentials == null)
            return null;

        return response.request().newBuilder()
                .header("Authorization", String.format("JWT %s", credentials)).build();    }
}
