package iguana.iguana.fragments.comment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
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
import iguana.iguana.fragments.base.BaseFragment;
import iguana.iguana.models.Comment;
import iguana.iguana.models.Issue;
import iguana.iguana.remote.apicalls.CommentCalls;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentEditFragment extends BaseFragment {
    private EditText text;
    private Comment comment;
    private CommentCalls api;

    public CommentEditFragment() {}

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
        api = new CommentCalls(view);
        Button button = (Button) view.findViewById(R.id.send);
        text = (EditText) view.findViewById(R.id.text);
        if (getArguments() != null) {
            comment = getArguments().getParcelable("comment");
        }
        text.setText(comment.getText());

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String body_text = text.getText().toString();
                HashMap body = new HashMap<>();
                body.put("text", text.getText().toString());
                api.editComment(comment, body);
            }
        });

        Button delete = (Button) view.findViewById(R.id.delete);

        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        getView().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                        api.deleteComment(comment);
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
        return inflater.inflate(R.layout.fragment_comment_edit, container, false);
    }
}