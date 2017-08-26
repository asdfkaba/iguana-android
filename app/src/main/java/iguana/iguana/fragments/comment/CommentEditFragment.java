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

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import iguana.iguana.R;
import iguana.iguana.events.comment_changed;
import iguana.iguana.events.project_changed;
import iguana.iguana.fragments.base.ApiFragment;
import iguana.iguana.models.Comment;
import iguana.iguana.models.Issue;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentEditFragment extends ApiFragment {
    private EditText text;
    private Comment comment;
    private Issue issue;


    public CommentEditFragment() {
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

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        Button button = (Button) view.findViewById(R.id.send);
        text = (EditText) view.findViewById(R.id.text);
        if (getArguments() != null) {
            comment = getArguments().getParcelable("comment");
            issue = getArguments().getParcelable("issue");

        }
        text.setText(comment.getText());

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String body_text = text.getText().toString();

                HashMap body = new HashMap<>();
                body.put("text", text.getText().toString());

                get_api_service().editComment(comment.getNameShort(), comment.getIssueNumber(), comment.getSeqnum(), body).enqueue(new Callback<Comment>() {
                                                                   @Override
                                                                   public void onResponse(Call<Comment> call, Response<Comment> response) {
                                                                       if (response.isSuccessful()) {
                                                                           EventBus.getDefault().postSticky(new comment_changed(response.body()));
                                                                           getFragmentManager().popBackStack();

                                                                       } else {
                                                                           try {
                                                                               JSONObject obj = new JSONObject(response.errorBody().string());
                                                                               Iterator<?> keys = obj.keys();
                                                                               while (keys.hasNext()) {
                                                                                   String key = (String) keys.next();
                                                                                   if (key.equals("text")) {
                                                                                       text.setError(obj.get(key).toString());
                                                                                   }
                                                                               }

                                                                           } catch (JSONException | IOException e) {
                                                                               e.printStackTrace();
                                                                           }
                                                                       }
                                                                   }

                                                                   @Override
                                                                   public void onFailure(Call<Comment> call, Throwable t) {
                                                                       t.printStackTrace();
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
        return inflater.inflate(R.layout.fragment_comment_edit, container, false);
    }


}