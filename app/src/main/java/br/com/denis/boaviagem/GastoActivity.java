package br.com.denis.boaviagem;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import br.com.denis.boaviagem.dao.BoaViagemDAO;
import br.com.denis.boaviagem.domain.Gasto;
import br.com.denis.boaviagem.helper.Constantes;

public class GastoActivity extends Activity {

    private int ano, mes, dia;
    private Button dataGastoButton;
    private Date dataGasto;
    private Spinner categoria;
    private EditText local, descricao, valor;
    private String viagemId;
    private BoaViagemDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gasto);

        Calendar dataAtual = Calendar.getInstance();
        ano = dataAtual.get(Calendar.YEAR);
        mes = dataAtual.get(Calendar.MONTH);
        dia = dataAtual.get(Calendar.DAY_OF_MONTH);

        dataGasto = dataAtual.getTime();
        dataGastoButton = (Button)findViewById(R.id.data);
        dataGastoButton.setText(dia+"/"+(mes+1)+"/"+ano);

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this, R.array.categoria_gasto, android.R.layout.simple_spinner_dropdown_item);
        categoria = (Spinner) findViewById(R.id.categoria);
        categoria.setAdapter(adapter);

        local = (EditText) findViewById(R.id.local);
        descricao = (EditText) findViewById(R.id.descricao);
        valor = (EditText) findViewById(R.id.valor);

        viagemId = getIntent().getStringExtra(Constantes.VIAGEM_ID);

        dao = new BoaViagemDAO(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Ao receber o id do gasto incluir na condicao
        if(1==2) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.gasto_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId,
                                      MenuItem item) {
        Toast.makeText(this, "Remover Gasto", Toast.LENGTH_SHORT).show();
        return true;
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
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            dataGasto = calendar.getTime();
            dataGastoButton.setText(dia + "/" + (mes + 1) + "/" + ano);
        }
    };

    public void registrarGasto(View view) {
        Gasto gasto = popularGasto();
        long resultado = dao.persistirGasto(gasto);
        if(resultado != -1 ){
            Toast.makeText(this, getString(R.string.registro_salvo),
                    Toast.LENGTH_SHORT).show();
            finish();
        }else{
            Toast.makeText(this, getString(R.string.erro_salvar),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private Gasto popularGasto(){
        Gasto gasto = new Gasto();
        //gasto.setId(null);//refatorar quando incluir edicao em gasto
        gasto.setCategoria(categoria.getSelectedItem().toString());
        gasto.setData(dataGasto);
        gasto.setDescricao(descricao.getText().toString());
        gasto.setLocal(local.getText().toString());
        gasto.setValor(Double.parseDouble(valor.getText().toString()));
        gasto.setViagemId(Integer.parseInt(viagemId));
        return gasto;
    }
}
