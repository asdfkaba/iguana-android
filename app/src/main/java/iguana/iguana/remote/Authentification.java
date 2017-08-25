package iguana.iguana.remote;

/**
 * Created by moritz on 15.05.17.
 */

import java.io.IOException;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class Authentification implements Interceptor {

    private String credentials;

    public Authentification(String user, String password) {
         this.credentials = Credentials.basic(user, password);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request authenticatedRequest = request.newBuilder()
               .header("Authorization", credentials).build();
        return chain.proceed(authenticatedRequest);
    }
}

