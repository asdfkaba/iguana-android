package iguana.iguana.fragments.calls;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;

import iguana.iguana.R;
import iguana.iguana.fragments.comment.CommentCreateFragment;
import iguana.iguana.fragments.comment.CommentsFragment;
import iguana.iguana.fragments.issue.IssueBaseFragment;
import iguana.iguana.fragments.issue.IssueCreateFragment;
import iguana.iguana.fragments.issue.IssueDetailFragment;
import iguana.iguana.fragments.issue.IssueEditFragment;
import iguana.iguana.fragments.project.ProjectBaseFragment;
import iguana.iguana.fragments.project.ProjectCreateFragment;
import iguana.iguana.fragments.project.ProjectEditFragment;
import iguana.iguana.fragments.project.SprintBaseFragment;
import iguana.iguana.fragments.timelog.TimelogCreateFragment;
import iguana.iguana.fragments.timelog.TimelogsFragment;
import iguana.iguana.models.Issue;
import iguana.iguana.models.Project;

/**
 * Created by moritz on 02.09.17.
 */

public class FragmentCalls {
    FragmentManager manager;

    public FragmentCalls(Activity context) {
        this.manager = context.getFragmentManager();
    }

    public void HandleFragmentCall(Fragment frag , Bundle d, String tag) {
        frag.setArguments(d);
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.content_frame, frag, tag);
        ft.addToBackStack(tag);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    public void ProjectEdit(Project project) {
        ProjectEditFragment fragment = new ProjectEditFragment();
        Bundle d = new Bundle();
        d.putParcelable("project", project);
        HandleFragmentCall(fragment, d, "project_edit");
    }

    public void ProjectCreate() {
        ProjectCreateFragment fragment = new ProjectCreateFragment();
        HandleFragmentCall(fragment, null, "project_create");
    }

    public void ProjectBase(Project project) {
        ProjectBaseFragment fragment = new ProjectBaseFragment();
        Bundle d = new Bundle();
        d.putParcelable("project", project);
        HandleFragmentCall(fragment, d, "project_base");
    }

    public void ProjectSprints(Project project) {
        SprintBaseFragment fragment = new SprintBaseFragment();
        Bundle d = new Bundle();
        d.putParcelable("project", project);
        HandleFragmentCall(fragment, d, "project_sprints");
    }

    public void IssueBase(Issue issue) {
        IssueBaseFragment fragment = new IssueBaseFragment();
        Bundle d = new Bundle();
        d.putParcelable("issue", issue);
        HandleFragmentCall(fragment, d, "issue_base");
    }

    public void IssueEdit(Issue issue) {
        IssueEditFragment fragment = new IssueEditFragment();
        Bundle d = new Bundle();
        d.putParcelable("issue", issue);
        HandleFragmentCall(fragment, d, "issue_edit");
    }

    public void CommentCreate(Issue issue) {
        CommentCreateFragment fragment = new CommentCreateFragment();
        Bundle d = new Bundle();
        d.putParcelable("issue", issue);
        fragment.setArguments(d);
        HandleFragmentCall(fragment,d, "comment_create");
    }

    public void TimelogCreate(Issue issue) {
        TimelogCreateFragment fragment = new TimelogCreateFragment();
        Bundle d = new Bundle();
        d.putParcelable("issue", issue);
        HandleFragmentCall(fragment, d, "timelog_create");
    }

    public void CommentsFragment(Issue issue) {
        CommentsFragment fragment = new CommentsFragment();
        Bundle d = new Bundle();
        d.putParcelable("issue", issue);
        fragment.setArguments(d);
        HandleFragmentCall(fragment,d, "comment_list");
    }

    public void TimelogList(Issue issue) {
        TimelogsFragment fragment = new TimelogsFragment();
        Bundle d = new Bundle();
        d.putParcelable("issue", issue);
        fragment.setArguments(d);
        HandleFragmentCall(fragment,d, "timelog_list");
    }

    public void IssueCreate(Project project, String sprint, String status, String viewpoint) {
        IssueCreateFragment fragment = new IssueCreateFragment();
        Bundle d = new Bundle();
        d.putParcelable("project", project);
        d.putString("sprint", sprint);
        d.putString("status", status);
        d.putString("sprintview", viewpoint);
        HandleFragmentCall(fragment, d, "issue_create");
    }
}
