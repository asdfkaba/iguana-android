package iguana.iguana.fragments.base;

import android.app.Fragment;

import iguana.iguana.app.MainActivity;
import iguana.iguana.fragments.calls.FragmentCalls;
import iguana.iguana.remote.APIService;
import iguana.iguana.remote.apicalls.ApiCalls;

/**
 * Created by moritz on 25.08.17.
 */

public class ApiFragment extends Fragment {
    protected FragmentCalls calls;


    protected APIService get_api_service(){
        return ((MainActivity) getActivity()).get_api_service();
    }

    public void onStart() {
        super.onStart();
        calls = new FragmentCalls(getActivity());
    }

}
