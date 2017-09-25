package iguana.iguana.app;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.danlew.android.joda.JodaTimeAndroid;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import iguana.iguana.events.issue_changed;
import iguana.iguana.events.new_token;
import iguana.iguana.events.project_changed;
import iguana.iguana.fragments.main.DashboardFragment;
import iguana.iguana.R;
import iguana.iguana.fragments.main.NotificationFragment;
import iguana.iguana.fragments.main.SettingsFragment;
import iguana.iguana.fragments.issue.IssueBaseFragment;
import iguana.iguana.fragments.issue.IssueCreateFragment;
import iguana.iguana.fragments.issue.IssueEditFragment;
import iguana.iguana.fragments.issue.IssuesFragment;
import iguana.iguana.fragments.project.ProjectBaseFragment;
import iguana.iguana.fragments.project.ProjectCreateFragment;
import iguana.iguana.fragments.project.ProjectsFragment;
import iguana.iguana.fragments.project.SprintBaseFragment;
import iguana.iguana.fragments.timelog.TimelogCreateFragment;
import iguana.iguana.fragments.timelog.TimelogsFragment;
import iguana.iguana.models.Issue;
import iguana.iguana.models.Project;
import iguana.iguana.models.Token;
import iguana.iguana.remote.APIService;
import iguana.iguana.remote.ApiUtils;
import iguana.iguana.events.rtoken_invalid;
import iguana.iguana.remote.ProjectWebsocketListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity {
    private String[] titles;
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ArrayAdapter menuAdapter;
    private int currentPosition = 0;
    private APIService mAPIService;
    private OkHttpClient client;
    private WebSocketListener sock_listener;
    private WebSocket ws;


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public String get_user() {
        return getSharedPreferences("api", Context.MODE_PRIVATE).getString("api_user", null);
    }

    @Override
    public void onBackPressed() {
        Fragment curr = null;
        if (getFragmentManager().getBackStackEntryCount() > 0)
            curr = getFragmentManager().findFragmentByTag(getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 1).getName());
        View view;
        if (curr != null && curr instanceof ProjectBaseFragment)
            view = ((ProjectBaseFragment) curr).getAdapter().getVisibleView();
        else
            view = getCurrentFocus();
        if (view.getId() != R.id.bottomsheet)
            view = view.findViewById(R.id.bottomsheet);
        System.out.println(view);
        if (view != null) {
            if (((BottomSheetLayout) view).isSheetShowing()) {
                ((BottomSheetLayout) view).dismissSheet();
                return;
            }
        }
        super.onBackPressed();
    }

    public String refresh_token(){
        HashMap body = new HashMap<>();
        SharedPreferences sharedPref = getSharedPreferences("api", Context.MODE_PRIVATE);
        String refresh_token =  sharedPref.getString("api_refresh_token", "");
        String url =  sharedPref.getString("api_url", "");
        if (url == null || url.length() == 0)
            return null;
        body.put("refresh_token", refresh_token);
        body.put("client_id", "iguana");
        body.put("api_type", "iguana");
        body.put("grant_type", "grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer");
        SharedPreferences.Editor editor = sharedPref.edit();
        Call<Token> result = ApiUtils.getAPIService(url, null, null).refreshToken(body);
        retrofit2.Response<Token> response = null;
        try {
            response = result.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response == null)
            return null;
        if (response.isSuccessful()) {
            editor.putString("api_token", response.body().getToken());
            editor.putString("api_refresh_token", response.body().getRefreshToken());
            editor.commit();
            init_socket();
        } else {
            EventBus.getDefault().post(new rtoken_invalid("TEST,TEST"));
            return null;
        }
        reinit_api_service();
        return response.body().getToken();
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
        String user = getSharedPreferences("api", Context.MODE_PRIVATE).getString("api_user", null);

        if (token == null && url == null) {
            this.mAPIService = null;
            return 1;
        } else if (token == null) {
            this.mAPIService = ApiUtils.getAPIService(url, token, this);
            return 2;
        } else {
            this.mAPIService = ApiUtils.getAPIService(url, token, this);
            return 0;

        }
    }

    public void init_socket() {
        String token = getSharedPreferences("api", Context.MODE_PRIVATE).getString("api_token", null);
        String url = getSharedPreferences("api", Context.MODE_PRIVATE).getString("api_url", null);
        String user = getSharedPreferences("api", Context.MODE_PRIVATE).getString("api_user", null);
        String host = null;
        if (url != null) {
             host = url.split("//")[1];
        }
        if (host != null && token != null && user != null) {
            String websocket_url = "ws://"+host+user+"/?token=" + token;
            System.out.println(websocket_url);
            Request request = new Request.Builder().url(websocket_url).build();
            if (ws != null)
                ws.close(1000, null);
            ws = client.newWebSocket(request, sock_listener);
        }
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(rtoken_invalid event) {
        Toast show = Toast.makeText(this, "Please relogin on your iguana server (settings)", Toast.LENGTH_LONG);
        View progress = findViewById(R.id.progressBar);
        if (progress != null)
            progress.setVisibility(View.GONE);
        show.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(new_token event) {
        reinit_api_service();
        init_socket();
    }

    protected void onDestroy() {
        if (ws != null) {
            ws.close(1000, null);
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        String api_url = getSharedPreferences("api", Context.MODE_PRIVATE).getString("api_url", null);
        String token = getSharedPreferences("api", Context.MODE_PRIVATE).getString("api_token", null);
        if (client == null)
            client = new OkHttpClient();
        if (sock_listener == null)
            sock_listener =  new ProjectWebsocketListener(this);

        init_socket();
        if (mAPIService == null) {
            int ret = reinit_api_service();
            if (ret == 1)
                Toast.makeText(this, "Please configure your iguana server (settings)", Toast.LENGTH_SHORT).show();
            if (ret == 2)
                Toast.makeText(this, "Please login on your iguana server (settings)", Toast.LENGTH_SHORT).show();
        }

        getSupportActionBar().setElevation(0);

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
            String title = savedInstanceState.getString("title");
            if (title != null)
                getSupportActionBar().setTitle(title);
            else
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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

                        if (fragment instanceof NotificationFragment) {
                            currentPosition = 4;
                            setActionBarTitle(currentPosition);
                        }
                        if (fragment instanceof IssueCreateFragment) {
                            Project project = ((IssueCreateFragment) fragment).getArguments().getParcelable("project");
                            getSupportActionBar().setTitle(project.getNameShort() + " - new issue");
                        }
                        if (fragment instanceof SettingsFragment) {
                            getSupportActionBar().setTitle("Settings");
                        }

                        if (fragment instanceof ProjectCreateFragment) {
                            getSupportActionBar().setTitle("New project");
                        }
                        if (fragment instanceof TimelogCreateFragment) {
                            getSupportActionBar().setTitle("Log time");
                        }

                        if (fragment instanceof ProjectBaseFragment) {
                            Project project = ((ProjectBaseFragment) fragment).getProject();
                            if (project != null)
                                getSupportActionBar().setTitle(project.getNameShort());
                        }
                        if (fragment instanceof SprintBaseFragment) {
                            System.out.println("ASDSADSD");
                            Project project = ((SprintBaseFragment) fragment).getProject();
                            if (project != null)
                                getSupportActionBar().setTitle(project.getNameShort());
                        }

                        if (fragment instanceof IssueBaseFragment) {
                            Issue issue = ((IssueBaseFragment) fragment).getIssue();
                            if (issue != null)
                                getSupportActionBar().setTitle(issue.getProjectShortName() + "-" + issue.getNumber());
                        }

                        if (fragment instanceof IssueEditFragment) {
                            Issue issue = ((IssueEditFragment) fragment).getIssue();
                            getSupportActionBar().setTitle("Edit " + issue.getProjectShortName() + "-" + issue.getNumber());
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
            case 4:
                fragment = new NotificationFragment();
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
        outState.putString("title", getSupportActionBar().getTitle().toString());

    }

    private void setActionBarTitle(int position) {
        String title;
        if (position == 0) {
            title = getResources().getString(R.string.app_name);
        } else {
            title = titles[position];
        }
        getSupportActionBar().setTitle(title);
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
