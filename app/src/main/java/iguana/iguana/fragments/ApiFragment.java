package iguana.iguana.fragments;

import android.app.Fragment;

import iguana.iguana.app.MainActivity;
import iguana.iguana.remote.APIService;

/**
 * Created by moritz on 25.08.17.
 */

public class ApiFragment extends Fragment {

    protected APIService get_api_service(){
        return ((MainActivity) getActivity()).get_api_service();
    }


}
