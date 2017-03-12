package br.com.denis.boaviagem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DashBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
    }

    public void selecionarOpcao(View view) {
        TextView textView = (TextView) view;
        String opcao = textView.getText().toString();
        switch (view.getId()){
            case R.id.nova_viagem:
                startActivity(new Intent(this, NovaViagemActivity.class));
                break;
            default:
                Toast.makeText(this, "Opção: " + opcao, Toast.LENGTH_SHORT).show();
        }
    }
}
