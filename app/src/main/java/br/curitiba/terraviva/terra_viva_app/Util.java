package br.curitiba.terraviva.terra_viva_app;


import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Util {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);

    public static boolean isCPF(EditText editText) {
        String CPF = editText.getText().toString().replace(".","").replace("-","");
        // considera-se erro CPF's formados por uma sequencia de numeros iguais
        if (CPF.equals("00000000000") ||
                CPF.equals("11111111111") ||
                CPF.equals("22222222222") || CPF.equals("33333333333") ||
                CPF.equals("44444444444") || CPF.equals("55555555555") ||
                CPF.equals("66666666666") || CPF.equals("77777777777") ||
                CPF.equals("88888888888") || CPF.equals("99999999999") ||
                (CPF.length() != 11)) {
            editText.setError("CPF inválido");
            return (false);
        }

        char dig10, dig11;
        int sm, i, r, num, peso;

        // "try" - protege o codigo para eventuais erros de conversao de tipo (int)
        try {
            // Calculo do 1o. Digito Verificador
            sm = 0;
            peso = 10;
            for (i = 0; i < 9; i++) {
                // converte o i-esimo caractere do CPF em um numero:
                // por exemplo, transforma o caractere '0' no inteiro 0
                // (48 eh a posicao de '0' na tabela ASCII)
                num = (int) (CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig10 = '0';
            else dig10 = (char) (r + 48); // converte no respectivo caractere numerico

            // Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 11;
            for (i = 0; i < 10; i++) {
                num = (int) (CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig11 = '0';
            else dig11 = (char) (r + 48);

            // Verifica se os digitos calculados conferem com os digitos informados.
            if ((dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10))) {
                editText.setError(null);
                return (true);
            }else {
                editText.setError("CPF inválido");
                return (false);
            }
        } catch (InputMismatchException erro) {
            editText.setError("CPF inválido");
            return (false);
        }
    }

    public static String imprimeCPF(String CPF) {
        return (CPF.substring(0, 3) + "." + CPF.substring(3, 6) + "." +
                CPF.substring(6, 9) + "-" + CPF.substring(9, 11));
    }
    public static String clearCpf(String cpf){
        return cpf.replace(".","").replace("-","").replace(" ","");
    }

    public static boolean isEmail(EditText editText) {
        Matcher matcher = pattern.matcher(editText.getText().toString());
        if(!matcher.matches()){
            editText.setError("Email inváldo");
            return false;
        }else{
            editText.setError(null);
            return true;
        }
    }

    public static boolean isValidPwd(EditText senha,EditText confirm) {
        if(senha.length() < 6){
            senha.setError("Senha muito curta");
            return false;
        }else
        if(!senha.getText().toString().equals(confirm.getText().toString())) {
            senha.setError("Senhas não coincidem");
            confirm.setError("Senhas não coincidem");
            return false;
        }else{
            senha.setError(null);
            confirm.setError(null);
            return true;
        }
    }

    public static boolean isEmpty(EditText... inputs){
        for (EditText editText:inputs) {
            if(editText.length() == 0){
                editText.setError("Campo obrigatório!");
                return true;
            }else{
                editText.setError(null);
            }
        }
        return false;
    }

    public static boolean isValid(int minLengh,String errorMessage,EditText editText){
            if(editText.length() < minLengh){
                editText.setError(errorMessage);
                return false;
            }else{
                editText.setError(null);
                return true;
            }
    }

    public static String formatCurrency(float valor){
        Locale ptBr = new Locale("pt", "BR");
        NumberFormat formato = NumberFormat.getCurrencyInstance(ptBr);
        return formato.format(valor);
    }


    public static Date strToDate(String date,String pattern){
        Locale ptBr = new Locale("pt", "BR");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern,ptBr);

        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String dateToStr(Date data,String pattern){
        Locale ptBr = new Locale("pt", "BR");
        DateFormat df = new SimpleDateFormat(pattern,ptBr);
        return df.format(data);
    }

}
