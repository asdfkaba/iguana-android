package iguana.iguana.fragments.issue;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import iguana.iguana.app.MainActivity;
import iguana.iguana.R;
import iguana.iguana.models.Issue;
import iguana.iguana.remote.APIService;


public class IssueDetailFragment extends Fragment {
    TextView title, desc, status, type;
    Issue issue;
    ProgressBar progress;

    public IssueDetailFragment() {}

    @Override
    public void onStart() {
        super.onStart();
        View v = getView();

        if (issue == null)
            issue = getArguments().getParcelable("issue");

        View view = getView();
        title = (TextView) getView().findViewById(R.id.issue_title);
        title.setText(issue.getTitle());
        title.append(" ("+ issue.getProject()+"-"+issue.getNumber()+")");
        desc = (TextView) getView().findViewById(R.id.issue_description);
        desc.setText(issue.getDescription());
        status = (TextView) getView().findViewById(R.id.issue_status);
        status.setText(issue.getKanbancol());
        type = (TextView) getView().findViewById(R.id.issue_type);
        type.setText(issue.getType());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (issue != null) {
            outState.putParcelable("issue", issue);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
            if (issue == null)
                issue = savedInstanceState.getParcelable("issue");

            title = (TextView) getView().findViewById(R.id.issue_title);
            title.setText(issue.getTitle());
            title.append(" ("+ issue.getProject()+"-"+issue.getNumber()+")");
            desc = (TextView) getView().findViewById(R.id.issue_description);
            desc.setText(issue.getDescription());
            status = (TextView) getView().findViewById(R.id.issue_status);
            status.setText(issue.getKanbancol());
            type = (TextView) getView().findViewById(R.id.issue_type);
            type.setText(issue.getType());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_issue_detail, container, false);
    }

}
