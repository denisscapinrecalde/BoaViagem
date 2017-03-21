package br.com.denis.boaviagem;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;

import java.io.IOException;

import br.com.denis.boaviagem.helper.Constantes;

import static br.com.denis.boaviagem.helper.Constantes.MANTER_CONECTADO;
import static br.com.denis.boaviagem.helper.Constantes.PREFERENCIAS;

public class BoaViagemActivity extends Activity {


    private EditText usuario;
    private EditText senha;
    private CheckBox manterConectado;

    private SharedPreferences preferencias;
    private GoogleAccountManager accountManager;
    private Account conta;

    public BoaViagemActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        accountManager = new GoogleAccountManager(this);
        usuario = (EditText) findViewById(R.id.usuario);
        senha = (EditText) findViewById(R.id.senha);
        manterConectado =(CheckBox) findViewById(R.id.manterConectado);
        preferencias = getSharedPreferences(PREFERENCIAS, MODE_PRIVATE);
        boolean conectado = preferencias.getBoolean(MANTER_CONECTADO, false);
        if(conectado){
            iniciarDashboard();
        }
    }

    public void entrarOnClick(View view) {

        String usuarioInformado = usuario.getText().toString();
        String senhaInformada = senha.getText().toString();
        autenticar(usuarioInformado, senhaInformada);
    }

    private void iniciarDashboard(){
        startActivity(new Intent(this, DashBoardActivity.class));
    }

    private void autenticar(final String nomeConta, String senha) {
        int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
        if (ContextCompat.checkSelfPermission(this,
         android.Manifest.permission.GET_ACCOUNTS)
         != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                 new String[]{android.Manifest.permission.GET_ACCOUNTS},
                 MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        for(Account ac :accountManager.getAccounts()){
            Log.d(ac.name.toString(), ac.name.toString());
        }
        conta = accountManager.getAccountByName(nomeConta);
        if(conta == null){
            Toast.makeText(this, R.string.falha_ao_autenticar,Toast.LENGTH_LONG).show();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, nomeConta);
        bundle.putString(AccountManager.KEY_PASSWORD, senha);
        accountManager.getAccountManager().confirmCredentials(conta , bundle, this, new AutenticacaoCallback(), null);
    }

    private class AutenticacaoCallback
     implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> future) {
            try {
                Bundle bundle = future.getResult();
                if(bundle.getBoolean(
                 AccountManager.KEY_BOOLEAN_RESULT)) {
                    iniciarDashboard();
                } else {
                    Toast.makeText(getBaseContext(),
                     getString(R.string.erro_autenticacao),
                     Toast.LENGTH_LONG).show();
                }
            } catch (OperationCanceledException e) {
// usuário cancelou a operação
            } catch (AuthenticatorException e) {
// possível falha no autenticador
            } catch (IOException e) {
// possível falha de comunicação
            }
        }
    }

}
