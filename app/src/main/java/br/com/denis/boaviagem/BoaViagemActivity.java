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
        preferencias = getSharedPreferences(Constantes.PREFERENCIAS, MODE_PRIVATE);
        boolean conectado = preferencias.getBoolean(Constantes.MANTER_CONECTADO, false);
        if(conectado){
            solicitarAutorizacao();
        }
    }

    public void entrarOnClick(View view) {
        gravarPreferenciaManterConectado();
        String usuarioInformado = usuario.getText().toString();
        String senhaInformada = senha.getText().toString();
        autenticar(usuarioInformado, senhaInformada);
    }

    private void gravarPreferenciaManterConectado() {
        SharedPreferences preferencias =
                getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putBoolean(Constantes.MANTER_CONECTADO,
                manterConectado.isChecked());
        editor.commit();
    }

    private void iniciarDashboard(){
        startActivity(new Intent(this, DashBoardActivity.class));
    }

    private void solicitarAutorizacao() {
        String tokenAcesso =
                preferencias.getString(Constantes.TOKEN_ACESSO, null);
        String nomeConta =
                preferencias.getString(Constantes.NOME_CONTA, null);
        if(tokenAcesso != null){
            accountManager.invalidateAuthToken(tokenAcesso);
            conta = accountManager.getAccountByName(nomeConta);
        }
        accountManager.getAccountManager()
                .getAuthToken(conta,
                        Constantes.AUTH_TOKEN_TYPE,
                        null,
                        this,
                        new AutorizacaoCallback(),
                        null);
    }

    private class AutorizacaoCallback
            implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> future) {
            try {
                Bundle bundle = future.getResult();
                String nomeConta =
                        bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
                String tokenAcesso =
                        bundle.getString(AccountManager.KEY_AUTHTOKEN);
                gravarTokenAcesso(nomeConta, tokenAcesso);
                iniciarDashboard();
            } catch (OperationCanceledException e) {
// usuário cancelou a operação
            } catch (AuthenticatorException e) {
                Log.d("ERRO: ", e.getMessage());
            } catch (IOException e) {
// possível falha de comunicação
            }
        }
    }
    private void gravarTokenAcesso(String nomeConta,
                                   String tokenAcesso) {
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString(Constantes.NOME_CONTA, nomeConta);
        editor.putString(Constantes.TOKEN_ACESSO, tokenAcesso);
        editor.commit();
    }

    private void autenticar(final String nomeConta, String senha) {
        if (isAutorizadoRecuperarContatos()) {
            solicitarPermissaoAcessoContatosAoUsuario();
        }else {
            conta = accountManager.getAccountByName(nomeConta);
            if (conta == null) {
                Toast.makeText(this, R.string.falha_ao_autenticar, Toast.LENGTH_LONG).show();
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString(AccountManager.KEY_ACCOUNT_NAME, nomeConta);
            bundle.putString(AccountManager.KEY_PASSWORD, senha);
            accountManager.getAccountManager().confirmCredentials(conta, bundle, this, new AutenticacaoCallback(), null);
        }
    }

    private void solicitarPermissaoAcessoContatosAoUsuario(){
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.GET_ACCOUNTS}, 0);
    }

    private boolean isAutorizadoRecuperarContatos() {
        return ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED;
    }


    private class AutenticacaoCallback
     implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> future) {
            try {
                Bundle bundle = future.getResult();
                if(bundle.getBoolean(
                        AccountManager.KEY_BOOLEAN_RESULT)) {
                    solicitarAutorizacao();
                } else {
                    Toast.makeText(getBaseContext(),
                            getString(R.string.erro_autenticacao),
                            Toast.LENGTH_LONG).show();
                }
            } catch (OperationCanceledException e) {
// usuário cancelou a operação
            } catch (AuthenticatorException e) {
// possível problema no autenticador
            } catch (IOException e) {
// possível problema de comunicação
            }
        }
    }

}
