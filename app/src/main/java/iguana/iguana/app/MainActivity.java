package iguana.iguana.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import net.danlew.android.joda.JodaTimeAndroid;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import iguana.iguana.events.new_token;
import iguana.iguana.fragments.main.DashboardFragment;
import iguana.iguana.R;
import iguana.iguana.fragments.main.SettingsFragment;
import iguana.iguana.fragments.issue.IssueBaseFragment;
import iguana.iguana.fragments.issue.IssueCreateFragment;
import iguana.iguana.fragments.issue.IssueEditFragment;
import iguana.iguana.fragments.issue.IssuesFragment;
import iguana.iguana.fragments.project.ProjectBaseFragment;
import iguana.iguana.fragments.project.ProjectCreateFragment;
import iguana.iguana.fragments.project.ProjectsFragment;
import iguana.iguana.fragments.timelog.TimelogsFragment;
import iguana.iguana.models.Issue;
import iguana.iguana.models.Project;
import iguana.iguana.remote.APIService;
import iguana.iguana.remote.ApiUtils;
import iguana.iguana.events.rtoken_invalid;

public class MainActivity extends Activity {
    private String[] titles;
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ArrayAdapter menuAdapter;
    private int currentPosition = 0;
    private APIService mAPIService;

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }



    public String get_user() {
        return getSharedPreferences("api", Context.MODE_PRIVATE).getString("api_user", null);
    }


    public APIService get_api_service() {
        if (this.mAPIService != null) {
            return this.mAPIService;
        } else {
            return ApiUtils.getAPIService("https://localhost", "invalidtoken", this);
        }
    }

    public int reinit_api_service() {
        String token = getSharedPreferences("api", Context.MODE_PRIVATE).getString("api_token", null);
        String url = getSharedPreferences("api", Context.MODE_PRIVATE).getString("api_url", null);
        if (token == null && url == null) {
            this.mAPIService = null;
            return 1;
        } else if (token == null) {
            this.mAPIService = ApiUtils.getAPIService(url, token, this);
            return 2;
        }
        this.mAPIService = ApiUtils.getAPIService(url, token, this);
        return 0;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(rtoken_invalid event) {
        Toast show = Toast.makeText(this, "Please relogin on your iguana server (settings)", Toast.LENGTH_LONG);
        show.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(new_token event) {
        reinit_api_service();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        if (mAPIService == null) {
            int ret = reinit_api_service();
            if (ret == 1)
                Toast.makeText(this, "Please configure your iguana server (settings)", Toast.LENGTH_SHORT).show();
            if (ret == 2)
                Toast.makeText(this, "Please login on your iguana server (settings)", Toast.LENGTH_SHORT).show();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActionBar().setElevation(0);
        }
        setContentView(R.layout.activity_main);
        titles = getResources().getStringArray(R.array.titles);
        drawerList = (ListView) findViewById(R.id.drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Populate the ListView
        menuAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_activated_1, titles);
        drawerList.setAdapter(menuAdapter);
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        //Display the correct fragment.
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt("position");
            setActionBarTitle(currentPosition);
        } else {
            selectItem(0);
        }
        //Create the ActionBarDrawerToggle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.open_drawer, R.string.close_drawer) {
            //Called when a drawer has settled in a completely closed state
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            //Called when a drawer has settled in a completely open state.
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        FragmentManager fragMan = getFragmentManager();
                        if (getFragmentManager().getBackStackEntryCount() == 0) {
                            currentPosition = 0;
                            setActionBarTitle(currentPosition);
                            return;
                        }
                        String tag = getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 1).getName();
                        Fragment fragment = fragMan.findFragmentByTag(tag);
                        menuAdapter.notifyDataSetChanged();

                        if (fragment instanceof DashboardFragment) {
                            currentPosition = 0;
                            setActionBarTitle(currentPosition);

                        }
                        if (fragment instanceof ProjectsFragment) {
                            currentPosition = 1;
                            setActionBarTitle(currentPosition);

                        }
                        if (fragment instanceof IssuesFragment) {
                            currentPosition = 2;
                            setActionBarTitle(currentPosition);

                        }
                        if (fragment instanceof TimelogsFragment) {
                            currentPosition = 3;
                            setActionBarTitle(currentPosition);

                        }
                        if (fragment instanceof IssueCreateFragment) {
                            String project = ((IssueCreateFragment) fragment).getArguments().getString("project");
                            getActionBar().setTitle(project + " - new issue");
                        }
                        if (fragment instanceof SettingsFragment) {
                            getActionBar().setTitle("Settings");
                        }

                        if (fragment instanceof ProjectCreateFragment) {
                            getActionBar().setTitle("New project");
                        }

                        if (fragment instanceof ProjectBaseFragment) {
                            Project project = ((ProjectBaseFragment) fragment).getProject();
                            getActionBar().setTitle(project.getNameShort());
                        }

                        if (fragment instanceof IssueBaseFragment) {
                            Issue issue = ((IssueBaseFragment) fragment).getIssue();
                            getActionBar().setTitle(issue.getProjectShortName() + "-" + issue.getNumber());
                        }

                        if (fragment instanceof IssueEditFragment) {
                            Issue issue = ((IssueEditFragment) fragment).getIssue();
                            getActionBar().setTitle("Edit " + issue.getProjectShortName() + "-" + issue.getNumber());
                        }
                        drawerList.setItemChecked(currentPosition, true);
                    }
                }
        );
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        currentPosition = position;
        String tag = "visible_fragment";
        Fragment fragment;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        switch (position) {
            case 1:
                fragment = new ProjectsFragment();
                break;
            case 2:
                Bundle d = new Bundle();
                d.putParcelable("project", null);
                fragment = new IssuesFragment();
                fragment.setArguments(d);
                break;
            case 3:
                fragment = new TimelogsFragment();
                break;
            default:
                fragment = new DashboardFragment();
        }

        ft.replace(R.id.content_frame, fragment, tag);
        ft.addToBackStack(tag);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        //Set the action bar title
        setActionBarTitle(position);
        //Close drawer
        drawerLayout.closeDrawer(drawerList);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", currentPosition);
    }

    private void setActionBarTitle(int position) {
        String title;
        if (position == 0) {
            title = getResources().getString(R.string.app_name);
        } else {
            title = titles[position];
        }
        getActionBar().setTitle(title);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setIntent("This is example text");
        return super.onCreateOptionsMenu(menu);
    }

    private void setIntent(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {

            case R.id.action_settings:
                String tag = "visible_fragment";
                SettingsFragment fragment = new SettingsFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment, tag);
                ft.addToBackStack(tag);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
