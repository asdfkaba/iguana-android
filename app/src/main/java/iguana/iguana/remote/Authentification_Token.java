package iguana.iguana.remote;

import java.io.IOException;
import java.lang.reflect.Proxy;

import iguana.iguana.app.MainActivity;
import iguana.iguana.service.TokenService;
import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class Authentification_Token implements Interceptor {

    private String credentials;

    public Authentification_Token(String token) {
        this.credentials = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request authenticatedRequest = request.newBuilder()
                .header("Authorization", String.format("JWT %s", credentials)).build();
        return chain.proceed(authenticatedRequest);
    }

}

