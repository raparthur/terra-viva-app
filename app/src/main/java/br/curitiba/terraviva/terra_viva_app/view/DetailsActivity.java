package br.curitiba.terraviva.terra_viva_app.view;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import br.curitiba.terraviva.terra_viva_app.MenuActions;
import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.Session;
import br.curitiba.terraviva.terra_viva_app.model.Produto;
import br.curitiba.terraviva.terra_viva_app.model.Usuario;

import static br.curitiba.terraviva.terra_viva_app.Session.compras;
import static br.curitiba.terraviva.terra_viva_app.Session.usuario;

public class DetailsActivity extends AppCompatActivity {

    //Produto produto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent it = getIntent();
        Bundle bundle = it.getExtras();
        if(bundle != null){
            Session.produto = (Produto) bundle.getSerializable("produto");

            FragmentManager fragmentManager = getSupportFragmentManager();

            Fragment argumentFragment = new DetailsFragment();
            //caso queira passar dados especificos para o fragmento
            //Bundle data = new Bundle();//Use bundle to pass data
            //data.putSerializable("produto", produto);
            //argumentFragment.setArguments(data);//Finally set argument bundle to fragment

            fragmentManager.beginTransaction().replace(R.id.frame_container, argumentFragment).commit();//now replace the argument fragment

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        if(usuario == null)
            inflater.inflate(R.menu.menu_logout_icons, menu);
        else
            inflater.inflate(R.menu.menu_login_icons, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MenuActions action = new MenuActions(getApplicationContext(),this);
        switch (item.getItemId()) {
            case R.id.nav_home:
                action.goHome();
                break;
            case R.id.nav_in:
                Intent it = new Intent(this,LoginActivity.class);
                startActivity(it);
                break;
            case R.id.nav_register:
                Toast.makeText(getApplicationContext(),"chamar cadastro",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_out:
                Toast.makeText(getApplicationContext(),"fazer logout",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_profile:
                Toast.makeText(getApplicationContext(),"editar perfil",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_cart:
                Toast.makeText(getApplicationContext(),"chamar carrinho",Toast.LENGTH_SHORT).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
