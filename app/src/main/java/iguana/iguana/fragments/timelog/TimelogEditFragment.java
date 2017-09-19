package iguana.iguana.fragments.timelog;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;

import iguana.iguana.R;
import iguana.iguana.fragments.base.BaseFragment;
import iguana.iguana.models.Timelog;
import iguana.iguana.remote.apicalls.TimelogCalls;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimelogEditFragment extends BaseFragment {
    private EditText time;
    private Timelog timelog;
    private TimelogCalls api;

    public TimelogEditFragment() {
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
        Button edit = (Button) view.findViewById(R.id.send);
        time = (EditText) view.findViewById(R.id.time);

        if (getArguments() != null)
            timelog = getArguments().getParcelable("timelog");

        time.setText(timelog.getTime());

        edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String body_text = time.getText().toString();
                HashMap body = new HashMap<>();
                body.put("time", time.getText().toString());
                api.editTimelog(timelog, body);
            }
        });

        Button delete = (Button) view.findViewById(R.id.delete);

        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        getView().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                        api.deleteTimelog(timelog);
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
        return inflater.inflate(R.layout.fragment_timelog_edit, container, false);
    }


}