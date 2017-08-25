package iguana.iguana.fragments.project;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import iguana.iguana.app.MainActivity;
import iguana.iguana.R;
import iguana.iguana.models.Project;
import iguana.iguana.remote.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectCreateFragment extends Fragment {
    private EditText name;
    private EditText name_short;
    private EditText description;
    private APIService mAPIService;

    public ProjectCreateFragment() {}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name", name.getText().toString());
        outState.putString("name_short", name_short.getText().toString());
        outState.putString("description", description.getText().toString());

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (name != null)
                name.setText(savedInstanceState.getString("title"));
            if (description != null)
                description.setText(savedInstanceState.getString("description"));
            if (name_short != null)
                name_short.setText(savedInstanceState.getString("name_short"));
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mAPIService = ((MainActivity) getActivity()).get_api_service();
        View view = getView();
        Button button = (Button) view.findViewById(R.id.send);
        name = (EditText) view.findViewById(R.id.comment_info);
        description = (EditText) view.findViewById(R.id.description);
        name_short = (EditText) view.findViewById(R.id.name_short);


        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String body_name = name.getText().toString();
                String body_name_short = name_short.getText().toString();
                String body_description = description.getText().toString();
                HashMap body = new HashMap<>();
           /*     if (body_name.length() > 3 ) {
                    body.put("name", body_name);
                } else {
                    name.setError("Name is required and needs to be at least 4 chars long");
                    return;
                }
                if (body_description.length() > 0)
                    body.put("description", body_description);
                if (body_name_short.length() > 0 && body_name_short.length()<5) {
                    body.put("name_short", body_name_short);
                } else {
                    name_short.setError("Short name is required and cant be longer than 4 chars");
                    return;
                }*/
                body.put("name", body_name);
                body.put("name_short", body_name_short);
                body.put("description", body_description);



                mAPIService.createProject(body).enqueue(new Callback<Project>() {
                                                                   @Override
                                                                   public void onResponse(Call<Project> call, Response<Project> response) {
                                                                       if (response.isSuccessful()) {
                                                                           ProjectBaseFragment fragment= new ProjectBaseFragment();
                                                                           Bundle d = new Bundle();
                                                                           d.putParcelable("project", response.body());
                                                                           fragment.setArguments(d);
                                                                           FragmentTransaction ft = getFragmentManager().beginTransaction();
                                                                           ft.replace(R.id.content_frame, fragment, "visible_fragment");
                                                                           ft.addToBackStack(null);
                                                                           ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                                                           ft.commit();
                                                                       } else {
                                                                           try {
                                                                               JSONObject obj = new JSONObject(response.errorBody().string());
                                                                               Iterator<?> keys = obj.keys();
                                                                               while (keys.hasNext()) {
                                                                                   String key = (String) keys.next();
                                                                                   if (key.equals("name")) {
                                                                                       name.setError(obj.get(key).toString());
                                                                                   } else if (key.equals("name_short")) {
                                                                                       name_short.setError(obj.get(key).toString());
                                                                                   } else if (key.equals("description")) {
                                                                                       description.setError(obj.get(key).toString());
                                                                                   }
                                                                               }

                                                                           } catch (JSONException | IOException e) {
                                                                               e.printStackTrace();
                                                                           }

                                                                       }

                                                                       }
                                                                   @Override
                                                                   public void onFailure(Call<Project> call, Throwable t) {
                                                                       Toast.makeText(getActivity(), "A problem occured, you can try again.\n Maybe there is a problem with your internet connection", Toast.LENGTH_SHORT).show();
                                                                   }
                                                               }
                    );
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_create, container, false);
    }



}