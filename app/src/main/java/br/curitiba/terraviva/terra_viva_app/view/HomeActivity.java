package br.curitiba.terraviva.terra_viva_app.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import br.curitiba.terraviva.terra_viva_app.MenuActions;
import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.Session;
import br.curitiba.terraviva.terra_viva_app.connexion.EstoqueController;
import br.curitiba.terraviva.terra_viva_app.connexion.HomeManager;
import br.curitiba.terraviva.terra_viva_app.model.Usuario;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Menu nav_Menu;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        HomeManager manager = new HomeManager(getApplicationContext(),HomeActivity.this);
        EstoqueController controller = new EstoqueController(getApplicationContext(),HomeActivity.this);

        Spinner subcategs = findViewById(R.id.dropdown);
        Spinner ordenar = findViewById(R.id.ordenar);

        String[] orders = {"Nome (todos)","Maior valor","Menor valor","Indisponíveis"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_checked, orders);
        ordenar.setAdapter(adapter);

        TextView tv_subcateg = findViewById(R.id.tv_subcateg);

        subcategs.setVisibility(View.GONE);
        tv_subcateg.setVisibility(View.GONE);

        /**
         * inicio de tudo
         */
        manager.getCategorias();

        if(Session.usuario != null) {
            controller.atualizaListaCompra();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        nav_Menu = navigationView.getMenu();
        headerView = navigationView.getHeaderView(0);

        menuFilter(Session.usuario);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        MenuActions menuAction = new MenuActions(getApplicationContext(),HomeActivity.this);
        int id = item.getItemId();

        if (id == R.id.nav_in) {

            Intent it = new Intent(this,LoginActivity.class);
            startActivity(it);
        }
        else if (id == R.id.nav_out) {
            Toast.makeText(this, "saindo!", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.nav_home) {
            menuAction.goHome();
        }
        else if (id == R.id.nav_register) {

            Toast.makeText(this, "cadastrar-se!", Toast.LENGTH_SHORT).show();

        }
        else if (id == R.id.nav_cart) {
            Toast.makeText(this, "var carrinho!", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.nav_profile) {
            Toast.makeText(this, "ver meu perfil!", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // super.onBackPressed();
            new AlertDialog.Builder(this)
                    .setTitle("Sair do aplicativo?")
                    //.setMessage("Sair do aplicativo?")
                    .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //if min API < 16
                            //Intent intent = new Intent(Intent.ACTION_MAIN);
                            //intent.addCategory(Intent.CATEGORY_HOME);
                            //startActivity(intent);
                            finishAffinity();
                        }
                    })
                    .setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(R.drawable.ic_dialog_alert)
                    .show();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.menuSearch).getActionView();

        searchView.setQueryHint("pesquisar produto...");

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {


                    TextView tv_titulo =  findViewById(R.id.tv_titulo);
                    tv_titulo.setText("Destaques");
                    HomeManager manager = new HomeManager(getApplicationContext(),HomeActivity.this);
                    manager.getDestaques();

                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                    TextView tv_titulo = findViewById(R.id.tv_titulo);
                    tv_titulo.setText("Pesquisa para \"" + query + "\":");
                    HomeManager manager = new HomeManager(getApplicationContext(),HomeActivity.this);
                    searchView.clearFocus();
                    manager.getProdutosPesquisa(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        return true;
    }

    private void menuFilter(Usuario usuario){

        TextView tv_welcome = headerView.findViewById(R.id.tv_welcome);
        TextView tv_instructions = headerView.findViewById(R.id.tv_instructions);

        if(usuario == null){
            nav_Menu.findItem(R.id.nav_out).setVisible(false);
            nav_Menu.findItem(R.id.nav_cart).setVisible(false);
            nav_Menu.findItem(R.id.nav_profile).setVisible(false);
            tv_welcome.setText("Bem vindo, Visitante!");
            tv_instructions.setText("Entre para acessar seu carrinho e efetuar compras ou pagamentos");
        }else{
            nav_Menu.findItem(R.id.nav_register).setVisible(false);
            nav_Menu.findItem(R.id.nav_in).setVisible(false);
            tv_instructions.setVisibility(View.GONE);
            tv_welcome.setText("Bem vindo, "+usuario.getNome()+"!");
        }
    }
}
