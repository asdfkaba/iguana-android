package iguana.iguana.remote;

import iguana.iguana.app.MainActivity;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;
    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    private static OkHttpClient client;


    public static Retrofit getClient(String baseUrl, String token, MainActivity activity) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (token != null) {
            builder.addInterceptor(new Authentification_Token(token));
            builder.authenticator(new TokenAuthenticator(token, activity));
        }

        builder.addInterceptor(logging);
        client = builder.build();

        Retrofit.Builder asd = new Retrofit.Builder();
        asd.baseUrl(baseUrl);

        retrofit = asd.addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }
}
