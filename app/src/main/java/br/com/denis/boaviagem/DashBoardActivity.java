package br.com.denis.boaviagem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import br.com.denis.boaviagem.dao.BoaViagemDAO;

public class DashBoardActivity extends Activity {

    BoaViagemDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        dao = new BoaViagemDAO(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashbord_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        finish();
        return true;
    }

    public void selecionarOpcao(View view) {
        TextView textView = (TextView) view;
        String opcao = textView.getText().toString();
        switch (view.getId()){
            case R.id.nova_viagem:
                startActivity(new Intent(this, ViagemActivity.class));
                break;
            case R.id.novo_gasto:
                if(dao.listarViagens().size()>0) {
                    startActivity(new Intent(this, GastoActivity.class));
                }else{
                    Toast.makeText(this, "Nenhuma viagem cadastrada", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.minhas_viagens:
                if(dao.listarViagens().size()>0) {
                    startActivity(new Intent(this, ViagemListActivity.class));
                }else{
                    Toast.makeText(this, "Nenhuma viagem cadastrada", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.configuracoes:
                startActivity(
                        new Intent(this, ConfiguracoesActivity.class));
                break;
            default:
                Toast.makeText(this, "Opção: " + opcao, Toast.LENGTH_SHORT).show();
        }
    }
}
