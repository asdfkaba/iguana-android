package iguana.iguana.fragments.timelog;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import iguana.iguana.R;
import iguana.iguana.fragments.base.ApiFragment;
import iguana.iguana.models.Issue;
import iguana.iguana.models.Timelog;
import iguana.iguana.remote.apicalls.TimelogCalls;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimelogCreateFragment extends ApiFragment {
    private EditText time;
    private Issue issue;
    private TimelogCalls api;

    public TimelogCreateFragment() {
        // Required empty public constructor
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (time != null)
            outState.putString("time", time.getText().toString());


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (time != null)
                time.setText(savedInstanceState.getString("text"));
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        View view = getView();
        api = new TimelogCalls(view);
        Button button = (Button) view.findViewById(R.id.send);
        time = (EditText) view.findViewById(R.id.time);

        if (getArguments() != null)
            issue = getArguments().getParcelable("issue");

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String body_text = time.getText().toString();
                HashMap body = new HashMap<>();
                body.put("time", time.getText().toString());
                api.createTimelog(issue, body);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timelog_create, container, false);
    }


}