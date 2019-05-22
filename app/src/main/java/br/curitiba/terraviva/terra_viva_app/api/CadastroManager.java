package br.curitiba.terraviva.terra_viva_app.api;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.curitiba.terraviva.terra_viva_app.Session;
import br.curitiba.terraviva.terra_viva_app.Util;
import br.curitiba.terraviva.terra_viva_app.model.Usuario;
import br.curitiba.terraviva.terra_viva_app.volley.Volley;
import br.curitiba.terraviva.terra_viva_app.volley.VolleyCallback;

public class CadastroManager {
    private Context context;
    private Activity activity;
    private ProgressDialog pDialog;

    public CadastroManager(Context context,Activity activity){
        this.context = context;
        this.activity = activity;
        pDialog = new ProgressDialog(activity);
        pDialog.setMessage("Contatando o servidor...");
    }

    public void novoUsuario(final Usuario usuario, final TextView enviado){
        Map<String,String> params = new HashMap<>();
        params.put("email", usuario.getEmail());
        params.put("nome", usuario.getNome());
        params.put("nasc", Util.dateToStr(usuario.getNasc(),"yyyy-MM-dd"));
        params.put("cpf", usuario.getCpf());
        params.put("senha", usuario.getSenha());
        params.put("rua", usuario.getRua());
        params.put("num", usuario.getNum());
        params.put("compl", usuario.getCompl());
        params.put("bairro", usuario.getBairro());
        params.put("cidade", usuario.getCidade());
        params.put("uf", usuario.getUf());
        params.put("cep", usuario.getCep().replace("-",""));
        params.put("tel", usuario.getTel().replace("-",""));
        params.put("ddd", usuario.getDdd());

        Volley volley = new Volley(context,"https://terraviva.curitiba.br/user/cadastrar",params);
        String[] dados = {"inserted"};
        volley.postRequest(dados, new VolleyCallback() {

            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> response) {
                enviado.setVisibility(View.VISIBLE);
                if(response.get(0).get("inserted").equals("1")){
                    enviado.setText("Email de confirmação enviado para '"+usuario.getEmail()+"'.\n Verifique também, se necessário, sua caixa de span");
                }else if(response.get(0).get("inserted").equals("0")){
                    enviado.setText("Já existe um usuário cadastrado com esses dados!");
                }else{
                    enviado.setText("Sua requisição não pôde ser porcessada. Por favor, tente novamente!");
                }
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
            }

            @Override
            public void onError(String error) {
                enviado.setText("Problemas ao acessar o servidor. Por favor, tente novamente!");
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
            }
        });
    }

    public void atualizaUsuario(final Usuario usuario, final TextView enviado){
        Map<String,String> params = new HashMap<>();
        params.put("email", usuario.getEmail());
        params.put("nome", usuario.getNome());
        params.put("nasc", Util.dateToStr(usuario.getNasc(),"yyyy-MM-dd"));
        params.put("cpf", usuario.getCpf());
        params.put("senha", usuario.getSenha());
        params.put("rua", usuario.getRua());
        params.put("num", usuario.getNum());
        params.put("compl", usuario.getCompl());
        params.put("bairro", usuario.getBairro());
        params.put("cidade", usuario.getCidade());
        params.put("uf", usuario.getUf());
        params.put("cep", usuario.getCep().replace("-",""));
        params.put("tel", usuario.getTel().replace("-",""));
        params.put("ddd", usuario.getDdd());


        Volley volley = new Volley(context,"https://terraviva.curitiba.br/user/atualizar",params);
        String[] dados = {"affected"};
        volley.postRequest(dados, new VolleyCallback() {

            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> response) {
                enviado.setVisibility(View.VISIBLE);
                if(response.get(0).get("affected").equals("1")){
                    enviado.setText("Dados cadastrais alterados com sucesso!");
                }else{
                    enviado.setText("Sem alteração de dados!");
                }
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
            }

            @Override
            public void onError(String error) {
                enviado.setText("Problemas ao acessar o servidor. Por favor, tente novamente!");
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
            }
        });
    }

}
