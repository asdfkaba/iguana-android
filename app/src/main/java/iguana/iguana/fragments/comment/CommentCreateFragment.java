package iguana.iguana.fragments.comment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import iguana.iguana.adapters.BaseAdapter;
import iguana.iguana.fragments.base.BaseFragment;
import iguana.iguana.models.Comment;
import iguana.iguana.models.Issue;
import iguana.iguana.remote.apicalls.CommentCalls;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class CommentCreateFragment extends BaseFragment {
    private EditText text;
    private Issue issue;
    private CommentCalls api;

    public CommentCreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (text != null)
            outState.putString("text", text.getText().toString());


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (text != null)
                text.setText(savedInstanceState.getString("text"));
        }
    }
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.add).setVisible(false);
        menu.findItem(R.id.menuSort).setVisible(false);
    }


    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        api = new CommentCalls(view);
        Button button = (Button) view.findViewById(R.id.send);
        text = (EditText) view.findViewById(R.id.text);
        if (getArguments() != null)
            issue = getArguments().getParcelable("issue");

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String body_text = text.getText().toString();
                HashMap body = new HashMap<>();
                body.put("text", text.getText().toString());
                api.createComment(issue, body);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment_create, container, false);
    }


}