package br.com.denis.boaviagem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.denis.boaviagem.calendar.CalendarService;
import br.com.denis.boaviagem.dao.BoaViagemDAO;
import br.com.denis.boaviagem.domain.Viagem;
import br.com.denis.boaviagem.helper.Constantes;
import br.com.denis.boaviagem.helper.DatabaseHelper;

public class ViagemActivity extends Activity implements DialogInterface.OnClickListener {

    private int ano, mes, dia;
    private Button dataChegadaButton, dataSaidaButton;
    private Date dataChegada, dataSaida;
    private EditText destino, quantidadePessoas, orcamento;
    private RadioGroup radioGroup;
    private String id;
    private BoaViagemDAO dao;
    private AlertDialog dialogConfirmacao;
    private CalendarService calendarService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viagem);

        dao = new BoaViagemDAO(this);

        Calendar dataAtual = Calendar.getInstance();
        ano = dataAtual.get(Calendar.YEAR);
        mes = dataAtual.get(Calendar.MONTH);
        dia = dataAtual.get(Calendar.DAY_OF_MONTH);

        dataChegada = dataAtual.getTime();
        dataSaida = dataAtual.getTime();

        dataChegadaButton = (Button)findViewById(R.id.dataChegada);
        dataSaidaButton = (Button)findViewById(R.id.dataSaida);

        String data = dia+"/"+(mes+1)+"/"+ano;
        dataChegadaButton.setText(data);
        dataSaidaButton.setText(data);

        destino = (EditText) findViewById(R.id.destino);
        quantidadePessoas =
                (EditText) findViewById(R.id.quantidadePessoas);
        orcamento = (EditText) findViewById(R.id.orcamento);
        radioGroup = (RadioGroup) findViewById(R.id.tipoViagem);

        this.dialogConfirmacao = criaDialogConfirmacao();

        id = getIntent().getStringExtra(Constantes.VIAGEM_ID);
        if(id != null){
            prepararEdicao();
        }
        calendarService = criarCalendarService();
    }

    private CalendarService criarCalendarService() {
        SharedPreferences preferencias =
                getSharedPreferences(Constantes.PREFERENCIAS, MODE_PRIVATE);
        String nomeConta = preferencias.getString(Constantes.NOME_CONTA, null);
        String tokenAcesso =
                preferencias.getString(Constantes.TOKEN_ACESSO, null);
        return new CalendarService(nomeConta, tokenAcesso);
    }

    @Override
    public void onClick(DialogInterface dialog, int item) {
        switch (item) {
            case DialogInterface.BUTTON_POSITIVE:
                dao.removerViagem(new Viagem(Long.parseLong(id)));
                Toast.makeText(this, R.string.viagem_excluida_sucesso, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, DashBoardActivity.class));
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                dialogConfirmacao.dismiss();
                break;
        }
    }

    private AlertDialog criaDialogConfirmacao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirmacao_exclusao_viagem);
        builder.setPositiveButton(getString(R.string.sim), this);
        builder.setNegativeButton(getString(R.string.nao), this);
        return builder.create();
    }

    private void prepararEdicao() {
        Viagem viagem = new Viagem(Long.parseLong(id.toString()));
        viagem = dao.buscarViagemPorId(viagem);
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd/MM/yyyy");
        if(viagem.getTipoViagem() == Constantes.VIAGEM_LAZER){
            radioGroup.check(R.id.lazer);
        } else {
            radioGroup.check(R.id.negocios);
        }
        destino.setText(viagem.getDestino());
        dataChegada = viagem.getDataChegada();
        dataSaida = viagem.getDataSaida();
        dataChegadaButton.setText(dateFormat.format(dataChegada));
        dataSaidaButton.setText(dateFormat.format(dataSaida));
        quantidadePessoas.setText(viagem.getQuantidadePessoas().toString());
        orcamento.setText(viagem.getOrcamento().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(id != null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.viagem_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId,
                                      MenuItem item) {
        switch (item.getItemId()) {
            case R.id.novo_gasto:
                Intent intent = new Intent(this, GastoActivity.class);
                intent.putExtra(Constantes.VIAGEM_ID, id);
                startActivity(intent);
                return true;
            case R.id.remover:
                dialogConfirmacao.show();
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
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            dataChegada = calendar.getTime();
            dataChegadaButton.setText(dia + "/" + (mes + 1) + "/" + ano);
        }
    };

    private DatePickerDialog.OnDateSetListener dataSaidaListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view,
                              int year, int monthOfYear, int dayOfMonth) {
            ano = year;
            mes = monthOfYear;
            dia = dayOfMonth;
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            dataSaida = calendar.getTime();
            dataSaidaButton.setText(dia + "/" + (mes + 1) + "/" + ano);
        }
    };

    public void salvarViagem(View view) {
        if(validarCamposObrigatorios().isEmpty()) {
            Viagem viagem = popularViagem();
            long resultado = dao.persistirViagem(viagem);
            if (resultado != -1) {
                Toast.makeText(this, getString(R.string.registro_salvo),
                        Toast.LENGTH_SHORT).show();
                if(viagem.getId()==null){
                    new Task().execute(viagem);
                }
                finish();
            } else {
                Toast.makeText(this, getString(R.string.erro_salvar),
                        Toast.LENGTH_SHORT).show();
            }
        }else{
            for(int id : validarCamposObrigatorios()){
                ((EditText)findViewById(id)).setError("Campo obrigatório");
            }
        }
    }

    private class Task extends AsyncTask<Viagem, Void, Void> {
        @Override
        protected Void doInBackground(Viagem... viagens) {
            Viagem viagem = viagens[0];
            calendarService.criarEvento(viagem);
            return null;
        }
    }

    private List<Integer> validarCamposObrigatorios(){
        List<Integer> ids = new ArrayList<Integer>();
        if(destino.getText().toString().isEmpty())ids.add(R.id.destino);
        if(quantidadePessoas.getText().toString().isEmpty())ids.add(R.id.quantidadePessoas);
        if(orcamento.getText().toString().isEmpty())ids.add(R.id.orcamento);
        return ids;
    }

    private Viagem popularViagem(){
        Viagem viagem = new Viagem();
        if(id != null)viagem.setId(Long.parseLong(id.toString()));
        viagem.setDestino(destino.getText().toString());
        viagem.setDataChegada(dataChegada);
        viagem.setDataSaida(dataSaida);
        viagem.setOrcamento(Double.parseDouble(orcamento.getText().toString()));
        viagem.setQuantidadePessoas(Integer.parseInt(quantidadePessoas.getText().toString()));
        int tipo = radioGroup.getCheckedRadioButtonId();
        if(tipo == R.id.lazer) {
            viagem.setTipoViagem(Constantes.VIAGEM_LAZER);
        } else {
            viagem.setTipoViagem(Constantes.VIAGEM_NEGOCIOS);
        }
        return viagem;
    }

    @Override
    protected void onDestroy() {
        dao.close();
        super.onDestroy();
    }
}
