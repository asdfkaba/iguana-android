package iguana.iguana.fragments.project;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import iguana.iguana.R;
import iguana.iguana.adapters.UserSuggestionAdapter;
import iguana.iguana.app.MainActivity;
import iguana.iguana.common.view.DelayAutoCompleteTextView;
import iguana.iguana.events.issue_changed;
import iguana.iguana.events.project_changed;
import iguana.iguana.fragments.base.BaseFragment;
import iguana.iguana.models.Project;
import iguana.iguana.models.User;
import iguana.iguana.remote.APIService;
import iguana.iguana.remote.apicalls.ProjectCalls;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectEditFragment extends BaseFragment {
    private EditText name;
    private EditText name_short;
    private EditText description;
    private Project project;
    private ProjectCalls api;

    public ProjectEditFragment() {}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("project", project);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (project == null)
                project = savedInstanceState.getParcelable("project");
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        api = new ProjectCalls(view);
        if (project == null)
            project = getArguments().getParcelable("project");

        Button button = (Button) view.findViewById(R.id.send);
        name = (EditText) view.findViewById(R.id.name);
        description = (EditText) view.findViewById(R.id.description);

        name.setText(project.getName());
        description.setText(project.getDescription());


        ArrayAdapter<String> member = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, project.getMembers());

        final ListView user_list = (ListView) getView().findViewById(R.id.user_list);
        user_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        user_list.setAdapter(member);
        for (int i = 0; i < user_list.getAdapter().getCount(); i++) {
            user_list.setItemChecked(i, project.getManager().contains(member.getItem(i)));
        }
        user_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                view.setSelected(!view.isSelected());
            }
        });

        final DelayAutoCompleteTextView user_search = (DelayAutoCompleteTextView) getView().findViewById(R.id.user_search);
        final List<String> current = project.getMembers();
        user_search.setThreshold(2);
        user_search.setAdapter(new UserSuggestionAdapter(getActivity(), project));
        user_search.setLoadingIndicator(
                (android.widget.ProgressBar) getView().findViewById(R.id.pb_loading_indicator));
        user_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                User selected = (User) user_search.getAdapter().getItem(position);
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) user_list.getAdapter();
                if (!current.contains(selected.getUsername())) {
                    current.add(selected.getUsername());
                    adapter.add(selected.getUsername());
                    adapter.notifyDataSetChanged();
                }
                View v = getActivity().getCurrentFocus();
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String body_name = name.getText().toString();
                String body_description = description.getText().toString();
                List<String> dev = new ArrayList<String>();
                List<String> man = new ArrayList<String>();

                for (int i = 0; i < user_list.getAdapter().getCount(); i++) {
                    CheckedTextView view = (CheckedTextView) user_list.getChildAt(i);
                    if (view.isChecked())
                        man.add(view.getText().toString());
                    else
                        dev.add(view.getText().toString());
                }


                HashMap body = new HashMap<>();
                body.put("name", body_name);
                body.put("manager", man.toArray());
                body.put("developer", dev.toArray());
                if (body_description.length() > 0)
                    body.put("description", body_description);
                System.out.println(body);

                api.editProject(project, body);
            }
        });

        Button delete = (Button) view.findViewById(R.id.delete);

        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        getView().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                        api.deleteProject(project);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                ab.setMessage("Are you sure you want  to delete this object?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_edit, container, false);
    }



}