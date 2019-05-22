package br.curitiba.terraviva.terra_viva_app.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import br.curitiba.terraviva.terra_viva_app.MenuActions;
import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.api.HistoricoManager;

import static br.curitiba.terraviva.terra_viva_app.Session.usuario;

public class HistoricoActivity extends AppCompatActivity {

    ListView historico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        historico = findViewById(R.id.lv_hist);
        HistoricoManager manager = new HistoricoManager(getApplicationContext(),this);
        manager.getVendas(usuario);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();

        //conforme logado ou não, apenas alguns ícones dotoolbar irão aparecer (irá "inflar" arquivos de menu diferentes)
        if(usuario == null)
            inflater.inflate(R.menu.menu_logout_icons, menu);
        else
            inflater.inflate(R.menu.menu_login_icons, menu);

        return true;
    }

    //icones da toolbar
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
                action.partiuCadastro();
                break;
            case R.id.nav_out:
                action.logout();
                break;
            case R.id.nav_profile:
                action.partiuCadastro();
                break;
            case R.id.nav_cart:
                action.goCarrinho();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
