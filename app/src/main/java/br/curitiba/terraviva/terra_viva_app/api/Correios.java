package br.curitiba.terraviva.terra_viva_app.api;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import br.curitiba.terraviva.terra_viva_app.Session;
import br.curitiba.terraviva.terra_viva_app.Util;
import br.curitiba.terraviva.terra_viva_app.volley.Volley;
import br.curitiba.terraviva.terra_viva_app.volley.VolleyCallback;

public class Correios{
    public static final int SEDEX = 1;
    public static final int PAC = 2;
    private String prazoFormatado;
    private float valor;
    private String erro;
    private Context context;

    public Correios(final Context context, Activity activity, int servico, final String destino) {
        this.context = context;
        final Volley volley = new Volley(context, "https://terraviva.curitiba.br/api/frete/" +
                Session.usuario.getEmail() + "/" + destino + "/" + servico, activity);
        String[] items = {"prazo", "valor", "erro"};
        volley.getRequest(items, new VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> response) {
                if (response.size() > 0) {
                    int prazo = Integer.parseInt(response.get(0).get("prazo"));
                    valor = Float.parseFloat(response.get(0).get("valor"));
                    prazoFormatado = formataDataEntrega(prazo);
                    setErro(Integer.parseInt(response.get(0).get("erro")));
                }
            }

        });
    }

    public Correios(final Context context, Activity activity, int servico, final String destino, final float carrinho,
                    final TextView textViewPrazo, final TextView textViewValor, final TextView textViewTotal, final Button avancar) {
        this.context = context;
        final Volley volley = new Volley(context, "https://terraviva.curitiba.br/api/frete/" +
                Session.usuario.getEmail() + "/" + destino + "/" + servico, activity);
        String[] items = {"prazo", "valor", "erro"};
        volley.getRequest(items, new VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> response) {
                if (response.size() > 0) {
                    int prazo = Integer.parseInt(response.get(0).get("prazo"));
                    valor = Float.parseFloat(response.get(0).get("valor"));
                    prazoFormatado = formataDataEntrega(prazo);
                    setErro(Integer.parseInt(response.get(0).get("erro")));

                    if(erro == null) {
                        textViewPrazo.setText(prazoFormatado);
                        textViewValor.setText(Util.formatCurrency(valor));
                        textViewTotal.setText(Util.formatCurrency(carrinho + valor));
                        avancar.setVisibility(View.VISIBLE);
                    }else
                        Toast.makeText(context,erro,Toast.LENGTH_LONG).show();
                }
            }

        });
    }


    public float getValor(){
        return valor;
    }

    public String getPrazo(){
        return prazoFormatado;
    }
    public String getErro(){
        return this.erro;
    }

    private void setErro(int codErro){
        if(codErro < 0){
            switch (codErro){
                case -2:
                    erro = "CEP de origem inválido";
                    break;
                case -3:
                    erro = "CEP de destino inválido";
                    break;
                case -4:
                    erro = "Peso excedido";
                    break;
                case -6:
                    erro = "Serviço indisponível para o trecho informado";
                    break;
                case -10:
                    erro = "Precificação indisponível para o trecho informado";
                    break;
                case -15:
                    erro = "O comprimento não pode ser maior que 105 cm";
                    break;
                case -16:
                    erro = "A largura não pode ser maior que 105 cm";
                    break;
                case -17:
                    erro = "A altura não pode ser maior que 105 cm.";
                    break;
                case -23:
                    erro = "A soma resultante do comprimento + largura + altura não deve superar a 200 cm";
                    break;
                case -28:
                    erro = "O comprimento não pode ser maior que 105 cm";
                    break;
                default:
                    erro = "Erro ao calcular dados";
                    break;
            }

        }else if(codErro > 0){
            switch (codErro){
                case 7:
                    Toast.makeText(context,"Área com entrega temporariamente sujeita a prazo diferenciado.",Toast.LENGTH_LONG).show();
                    erro = "Localidade de destino não abrange o serviço informado";
                    break;
                case 8:
                    Toast.makeText(context,"Serviço indisponível para o trecho informado",Toast.LENGTH_LONG).show();
                    erro = "Serviço indisponível para o trecho informado";
                    break;
                case 9:
                    Toast.makeText(context,"CEP inicial pertencente a Área de Risco.",Toast.LENGTH_LONG).show();
                    erro = null;
                    break;
                case 10:
                    Toast.makeText(context,"Área com entrega temporariamente sujeita a prazo diferenciado.",Toast.LENGTH_LONG).show();
                    erro = null;
                    break;
                case 11:
                    Toast.makeText(context,"CEP inicial e final pertencentes a Área de Risco",Toast.LENGTH_LONG).show();
                    erro = null;
                    break;
                default:
                    Toast.makeText(context,"Erro desconhecido ao calcular frete",Toast.LENGTH_LONG).show();
                    erro = null;
                    break;
            }
        }else
            erro = null;
    }

    //formata data de entrega pra extenso e compensa os domingos
    private String formataDataEntrega(int prazo){
        Calendar c = Calendar.getInstance();

        int domingos = 0;
        for(int i=0;i<prazo;i++){
            c.add(Calendar.DAY_OF_MONTH,i);
            if(c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                domingos++;
        }

        c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH,prazo + domingos);
        Date data = c.getTime();
        DateFormat formataData = DateFormat.getDateInstance(DateFormat.FULL);
        return "Entrega prevista para "+formataData.format(data);
    }
}
