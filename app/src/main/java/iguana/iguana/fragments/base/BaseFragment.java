package iguana.iguana.fragments.base;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;

import iguana.iguana.app.MainActivity;
import iguana.iguana.fragments.calls.FragmentCalls;
import iguana.iguana.remote.APIService;
import iguana.iguana.remote.apicalls.ApiCalls;

/**
 * Created by moritz on 25.08.17.
 */

public class BaseFragment extends Fragment {
    protected FragmentCalls calls;
    private SharedPreferences sharedPref;

    protected void save_preferences(String PREF_KEY, String key, String value) {
        sharedPref = getActivity().getSharedPreferences(PREF_KEY ,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void onStart() {
        super.onStart();

        calls = new FragmentCalls(getActivity());
    }

}
