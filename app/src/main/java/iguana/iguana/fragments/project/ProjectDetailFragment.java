package iguana.iguana.fragments.project;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import iguana.iguana.app.MainActivity;
import iguana.iguana.R;
import iguana.iguana.events.project_changed;
import iguana.iguana.models.Project;
import iguana.iguana.remote.APIService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.feras.mdv.MarkdownView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectDetailFragment extends Fragment {
    private TextView title, description, members;
    MarkdownView desc;
    private Project project;

    public ProjectDetailFragment() {    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(project_changed event) {
        Project new_project = event.getProject();
        if (new_project.getNameShort().equals(project.getNameShort())) {
            this.project = new_project;
            init();
        }
    }

    public void init() {
        title.setText(project.getName());
        if (project.getDescription().length() > 0) {
            desc.loadMarkdown(project.getDescription());
        } else {
            desc.setVisibility(View.GONE);
        }
        members.setText("Members: ");
        for (String member: project.getMembers()) {
            members.append(System.getProperty("line.separator") + member);
            if (project.getManager().contains(member))
                members.append(" (manager)");
        }
    }
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        View view = getView();
        if (project == null && getArguments() != null)
            project = getArguments().getParcelable("project");
        title = (TextView) view.findViewById(R.id.project_name);
        desc = (MarkdownView) view.findViewById(R.id.project_description);
        members = (TextView) view.findViewById(R.id.members);
        init();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_detail, container, false);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (project != null) {
            outState.putParcelable("project", project);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
            if(project == null)
                project = savedInstanceState.getParcelable("project");
        }
    }
}