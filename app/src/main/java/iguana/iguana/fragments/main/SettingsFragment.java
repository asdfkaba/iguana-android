package iguana.iguana.fragments.main;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

import iguana.iguana.R;
import iguana.iguana.app.MainActivity;
import iguana.iguana.fragments.base.BaseFragment;
import iguana.iguana.models.Token;
import iguana.iguana.remote.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SettingsFragment extends BaseFragment {
    private APIService s;
    private EditText url, user, password;

    public SettingsFragment() {
        // Required empty public constructor
    }

    private APIService get_api_service() {
        return ((MainActivity) getActivity()).get_api_service();
    }

    public void onStart() {
        super.onStart();
        Button save = (Button) getView().findViewById(R.id.send);
        EditText url = (EditText) getView().findViewById(R.id.url);
        EditText user = (EditText) getView().findViewById(R.id.user);
        EditText password = (EditText) getView().findViewById(R.id.password);
        final String api_url = getActivity().getSharedPreferences("api", Context.MODE_PRIVATE).getString("api_url", null);
        url.setText(getActivity().getSharedPreferences("api", Context.MODE_PRIVATE).getString("api_url", "https://"));
        user.setText(getActivity().getSharedPreferences("api", Context.MODE_PRIVATE).getString("api_user", ""));
        final TextView status = (TextView) getView().findViewById(R.id.api_status);


        get_api_service().checkStatus().enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    status.setText("Status: successfully connected to " + api_url);
                    status.setTextColor(Color.GREEN);
                } else {
                    status.setText("Status: no connection");
                    status.setTextColor(Color.RED);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                t.printStackTrace();
                status.setText("Status: no connection");
                status.setTextColor(Color.RED);

            }

        });

        save.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        EditText url = (EditText) getView().findViewById(R.id.url);
                                        System.out.println(url);
                                        EditText user = (EditText) getView().findViewById(R.id.user);
                                        System.out.println(user);
                                        EditText password = (EditText) getView().findViewById(R.id.password);
                                        System.out.println(password);

                                        String possible_url = url.getText().toString();

                                        if (!Pattern.matches("^https?://[^\\s/$.?#].[^\\s]*$", possible_url)) {
                                            url.setError("Not a valid url");
                                            return;
                                        }
                                        save_preferences("api", "api_user", user.getText().toString());

                                        ((MainActivity) getActivity()).reinit_api_service();


                                        if (!possible_url.substring(possible_url.length() - 1).equals('/'))
                                            possible_url += "/";

                                        final String api_url = possible_url;
                                        save_preferences("api", "api_url", possible_url);


                                        HashMap body = new HashMap<>();
                                        body.put("username", user.getText().toString());
                                        body.put("password", password.getText().toString());

                                        get_api_service().getToken(body).enqueue(new Callback<Token>() {
                                            @Override
                                            public void onResponse(Call<Token> call, Response<Token> response) {
                                                if (response.isSuccessful()) {
                                                    save_preferences("api", "api_token", response.body().getToken());
                                                    save_preferences("api", "api_refresh_token", response.body().getRefreshToken());

                                                    TextView status = (TextView) getView().findViewById(R.id.api_status);
                                                    status.setText("Status: successfully connected to " + api_url);
                                                    status.setTextColor(Color.GREEN);
                                                    ((MainActivity) getActivity()).reinit_api_service();

                                                    Toast.makeText(getActivity(), "Successfully connected", Toast.LENGTH_SHORT).show();
                                                    FragmentManager fm = getActivity().getFragmentManager();
                                                    for (int i = 0; i < fm.getBackStackEntryCount() - 1; ++i) {
                                                        fm.popBackStack();
                                                    }

                                                } else {
                                                    EditText user = (EditText) getView().findViewById(R.id.user);
                                                    user.setError("Wrong username or password for given url.");
                                                    EditText password = (EditText) getView().findViewById(R.id.password);
                                                    password.setError("Wrong username or password for given url.");
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Token> call, Throwable t) {
                                                t.printStackTrace();
                                                Toast.makeText(getActivity(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection. ", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

}
