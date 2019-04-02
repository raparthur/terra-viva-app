package br.curitiba.terraviva.terra_viva_app.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.connexion.JSONManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private View view;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);//Inflate Layout
        JSONManager manager = new JSONManager(getContext(), getActivity(), view);

        Spinner dropdown = view.findViewById(R.id.dropdown);
        TextView tv_subcateg = view.findViewById(R.id.tv_subcateg);

        dropdown.setVisibility(View.GONE);
        tv_subcateg.setVisibility(View.GONE);

        //create a list of items for the spinner.
        String[] items = new String[]{"TODAS"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_checked, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);

        //show
        dropdown.setVisibility(View.VISIBLE);
        tv_subcateg.setVisibility(View.VISIBLE);

        manager.getCategorias();

        return view;
    }

    public View getView(){
        return view;
    }

}
