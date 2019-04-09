package br.curitiba.terraviva.terra_viva_app.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.adapter.ProdAdapter;
import br.curitiba.terraviva.terra_viva_app.connexion.JSONManager;
import br.curitiba.terraviva.terra_viva_app.model.Produto;

import static java.util.Collections.sort;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static final int UNBOUNDED = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    private View view;
    private JSONManager manager;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);//Inflate Layout
        manager = new JSONManager(getContext(), getActivity(), view);

        Spinner subcategs = view.findViewById(R.id.dropdown);
        Spinner ordenar = view.findViewById(R.id.ordenar);

        String[] orders = {"Nome (todos)","Maior valor","Menor valor","Indisponíveis"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_checked, orders);
        ordenar.setAdapter(adapter);

        TextView tv_subcateg = view.findViewById(R.id.tv_subcateg);

        subcategs.setVisibility(View.GONE);
        tv_subcateg.setVisibility(View.GONE);

        //inicio de tudo
        manager.getCategorias();

        return view;
    }

    public View getView(){
        return view;
    }
}
