package br.com.denis.boaviagem;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class BoaViagemActivity extends Activity {

    private static final String MANTER_CONECTADO = "manter_conectado";
    private EditText usuario;
    private EditText senha;
    private CheckBox manterConectado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        usuario = (EditText) findViewById(R.id.usuario);
        senha = (EditText) findViewById(R.id.senha);

        manterConectado =
                (CheckBox) findViewById(R.id.manterConectado);
        SharedPreferences preferencias =
                getPreferences(MODE_PRIVATE);
        boolean conectado = preferencias.getBoolean(MANTER_CONECTADO, false);
        if(conectado){
            startActivity(
                    new Intent(this, DashBoardActivity.class));
        }
    }

    public void entrarOnClick(View view) {

        if("denis".equals(usuario.getText().toString()) && "123".equals(senha.getText().toString())){
            SharedPreferences preferencias =
                    getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putBoolean(MANTER_CONECTADO,
                    manterConectado.isChecked());
            editor.commit();
            startActivity(new Intent(this, DashBoardActivity.class));
        }else{
            Toast.makeText(this, getString(R.string.erro_autenticacao), Toast.LENGTH_SHORT).show();
        }

    }
}
