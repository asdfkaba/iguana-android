package iguana.iguana.fragments.base;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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

    @Override
    public void onResume() {
        super.onResume();
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void onStart() {
        super.onStart();

        calls = new FragmentCalls(getActivity());
    }

}
