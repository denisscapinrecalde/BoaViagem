package br.com.denis.boaviagem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class NovaViagemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nova_viagem);
    }

    public void selecionarData(View view) {
        Toast.makeText(this, "Em breve...", Toast.LENGTH_SHORT).show();
    }

    public void salvarViagem(View view) {
        Toast.makeText(this, "Em breve...", Toast.LENGTH_SHORT).show();
    }
}
