package br.curitiba.terraviva.terra_viva_app.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.connexion.JSONManager;
import br.curitiba.terraviva.terra_viva_app.connexion.Volley;
import br.curitiba.terraviva.terra_viva_app.connexion.VolleyCallback;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_lock).setVisible(false);

        // adicionar o fragmento
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new HomeFragment(),"HomeFragment").commit();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search,menu);

        final SearchView searchView = (SearchView)menu.findItem(R.id.menuSearch).getActionView();

        searchView.setQueryHint("pesquisar produto...");

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                Fragment f = getVisibleFragment();
                if (f instanceof HomeFragment) {
                    TextView tv_titulo = f.getView().findViewById(R.id.tv_titulo);
                    tv_titulo.setText("Destaques");
                    JSONManager manager = new JSONManager(f.getContext(), f.getActivity(), f.getView());
                    manager.getDestaques();
                }
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                Fragment f = getVisibleFragment();
                if(f instanceof HomeFragment){
                    TextView tv_titulo = f.getView().findViewById(R.id.tv_titulo);
                    tv_titulo.setText("Pesquisa para \""+query+"\":");
                    JSONManager manager = new JSONManager(f.getContext(),f.getActivity(),f.getView());
                    searchView.clearFocus();
                    manager.getProdutosPesquisa(query);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_in) {


        } else if (id == R.id.nav_home) {

           getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, new HomeFragment()).commit();

        } else if (id == R.id.nav_subscribe) {


            Map<String,String> params = new HashMap<>();
            params.put("email", "user@user.com");
            params.put("pwd", "12");
            Volley volley = new Volley(this,"https://terraviva.curitiba.br/user/login",params);
            String[] login = {"login"};
            volley.postRequest(login, new VolleyCallback() {
                @Override
                public void onSuccess(ArrayList<HashMap<String, String>> response) {
                    String nome = response.get(0).get("login");
                    Toast.makeText(getApplicationContext(), nome, Toast.LENGTH_SHORT).show();
                }
            });


        } else if (id == R.id.nav_lock) {
            Toast.makeText(this, "sair!", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Fragment getVisibleFragment(){
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null){
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }
}
