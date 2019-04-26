package br.curitiba.terraviva.terra_viva_app.activities;

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
import br.curitiba.terraviva.terra_viva_app.api.HomeManager;
import br.curitiba.terraviva.terra_viva_app.model.Usuario;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Menu nav_Menu;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HomeManager manager = new HomeManager(getApplicationContext(),HomeActivity.this);

        //instancia e alimenta dropboxes
        Spinner subcategs = findViewById(R.id.dropdown);
        Spinner ordenar = findViewById(R.id.ordenar);

        String[] orders = {"Nome (todos)","Maior valor","Menor valor","Indisponíveis"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_checked, orders);
        ordenar.setAdapter(adapter);

        //onde irá aparecer o nome da subcategoria selecionada
        TextView tv_subcateg = findViewById(R.id.tv_subcateg);

        //a principio, esconde-as, pois só aparecerão quando clicado em uma categoria
        subcategs.setVisibility(View.GONE);
        tv_subcateg.setVisibility(View.GONE);

        /**
         * ponto de partida
         */
        manager.getCategorias();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //menu deslizante
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        nav_Menu = navigationView.getMenu();
        headerView = navigationView.getHeaderView(0);

        //"decide" quais opções aparecerão no menu conforme o usuario esteja logado (usuario != null) ou não (usuario == null)
        menuFilter(Session.usuario);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        MenuActions menuAction = new MenuActions(getApplicationContext(),HomeActivity.this);
        int id = item.getItemId();

        //opções do menu
        if (id == R.id.nav_in) {

            Intent it = new Intent(this,LoginActivity.class);
            startActivity(it);
        }
        else if (id == R.id.nav_out) {
            menuAction.logout();
        }
        else if (id == R.id.nav_home) {
            menuAction.goHome();
        }
        else if (id == R.id.nav_register) {

            Toast.makeText(this, "cadastrar-se!", Toast.LENGTH_SHORT).show();

        }
        else if (id == R.id.nav_cart) {
            menuAction.goCarrinho();
        }
        else if (id == R.id.nav_profile) {
            Toast.makeText(this, "ver meu perfil!", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //ao apertar voltar do telefone
    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // mostra a caixa de dialogo "sair ou não"
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


    //insere itens no toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        //barra pesquisa do toolbar
        final SearchView searchView = (SearchView) menu.findItem(R.id.menuSearch).getActionView();

        searchView.setQueryHint("pesquisar produto...");

        //ao clicar no "x" de cancelar da barra de busca, vai chamar os destaques (como no inicio)
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

            //ao clicar na "lupa" da barra de pesquisa, chama a perquisa por termo
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
