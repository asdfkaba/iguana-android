package iguana.iguana.remote;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

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

