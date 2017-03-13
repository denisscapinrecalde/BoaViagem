package br.com.denis.boaviagem;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;

public class ViagemActivity extends Activity {

    private int ano, mes, dia;
    private Button dataChegada, dataSaida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viagem);

        Calendar dataAtual = Calendar.getInstance();
        ano = dataAtual.get(Calendar.YEAR);
        mes = dataAtual.get(Calendar.MONTH);
        dia = dataAtual.get(Calendar.DAY_OF_MONTH);

        dataChegada = (Button)findViewById(R.id.dataChegada);
        dataSaida = (Button)findViewById(R.id.dataSaida);

        String data = dia+"/"+(mes+1)+"/"+ano;
        dataChegada.setText(data);
        dataSaida.setText(data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.viagem_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId,
                                      MenuItem item) {
        switch (item.getItemId()) {
            case R.id.novo_gasto:
                startActivity(new Intent(this,
                        GastoActivity.class));
                return true;
            case R.id.remover:
                Toast.makeText(this, "Remover viagem", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }

    public void selecionarData(View view) {
        showDialog(view.getId());
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if(R.id.dataChegada == id){
            return new DatePickerDialog(this,
                    dataChegadaListener, ano, mes, dia);
        }else if(R.id.dataSaida == id){
            return new DatePickerDialog(this,
                    dataSaidaListener, ano, mes, dia);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener dataChegadaListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view,
                              int year, int monthOfYear, int dayOfMonth) {
            ano = year;
            mes = monthOfYear;
            dia = dayOfMonth;
            dataChegada.setText(dia + "/" + (mes + 1) + "/" + ano);
        }
    };

    private DatePickerDialog.OnDateSetListener dataSaidaListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view,
                              int year, int monthOfYear, int dayOfMonth) {
            ano = year;
            mes = monthOfYear;
            dia = dayOfMonth;
            dataSaida.setText(dia + "/" + (mes + 1) + "/" + ano);
        }
    };

    public void salvarViagem(View view) {
        Toast.makeText(this, "Em breve...", Toast.LENGTH_SHORT).show();
    }
}
