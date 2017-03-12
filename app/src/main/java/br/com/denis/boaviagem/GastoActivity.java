package br.com.denis.boaviagem;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import java.util.Calendar;

public class GastoActivity extends AppCompatActivity {

    private int ano, mes, dia;
    private Button dataGasto;
    private Spinner categoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gasto);

        Calendar dataAtual = Calendar.getInstance();
        ano = dataAtual.get(Calendar.YEAR);
        mes = dataAtual.get(Calendar.MONTH);
        dia = dataAtual.get(Calendar.DAY_OF_MONTH);

        dataGasto = (Button)findViewById(R.id.data);
        dataGasto.setText(dia+"/"+(mes+1)+"/"+ano);

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this, R.array.categoria_gasto, android.R.layout.simple_spinner_item);
        categoria = (Spinner) findViewById(R.id.categoria);
        categoria.setAdapter(adapter);
    }


    public void selecionarData(View view) {
        showDialog(view.getId());
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if(R.id.data == id){
            return new DatePickerDialog(this,
                    listener, ano, mes, dia);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view,
                              int year, int monthOfYear, int dayOfMonth) {
            ano = year;
            mes = monthOfYear;
            dia = dayOfMonth;
            dataGasto.setText(dia + "/" + (mes + 1) + "/" + ano);
        }
    };

    public void registrarGasto(View view) {
    }
}
