package br.com.denis.boaviagem;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class BoaViagemActivity extends Activity {

    private EditText usuario;
    private EditText senha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        usuario = (EditText) findViewById(R.id.usuario);
        senha = (EditText) findViewById(R.id.senha);
    }

    public void entrarOnClick(View view) {

        if("denis".equals(usuario.getText().toString()) && "123".equals(senha.getText().toString())){
            startActivity(new Intent(this, DashBoardActivity.class));
        }else{
            Toast.makeText(this, getString(R.string.erro_autenticacao), Toast.LENGTH_SHORT).show();
        }

    }
}
