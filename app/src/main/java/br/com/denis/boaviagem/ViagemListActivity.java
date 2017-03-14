package br.com.denis.boaviagem;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.denis.boaviagem.dao.BoaViagemDAO;
import br.com.denis.boaviagem.domain.Viagem;
import br.com.denis.boaviagem.helper.Constantes;
import br.com.denis.boaviagem.helper.DatabaseHelper;

public class ViagemListActivity extends ListActivity implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener,SimpleAdapter.ViewBinder  {

    private List<Map<String, Object>> viagens;
    private AlertDialog alertDialog;
    private int viagemSelecionada;
    private AlertDialog dialogConfirmacao;
    private SimpleDateFormat dateFormat;
    private Double valorLimite;
    BoaViagemDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dao = new BoaViagemDAO(this);

        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SharedPreferences preferencias =
                PreferenceManager.getDefaultSharedPreferences(this);
        String valor = preferencias.getString("valor_limite", "-1");
        valorLimite = Double.valueOf(valor);

        String[] de = {getText(R.string.imagem_vo).toString(), getText(R.string.destino_vo).toString(),
                getText(R.string.data_vo).toString(), getText(R.string.total_vo).toString(), getText(R.string.barra_progresso_vo).toString()};
        int[] para = {
                R.id.tipoViagem, R.id.destino, R.id.data, R.id.valor, R.id.barraProgresso
        };
        SimpleAdapter adapter = new SimpleAdapter(this, listarViagens(), R.layout.lista_viagem, de, para);
        adapter.setViewBinder(this);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
        this.alertDialog = criaAlertDialog();
        this.dialogConfirmacao = criaDialogConfirmacao();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.viagemSelecionada = position;
        alertDialog.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int item) {
        Intent intent;
        String id =
                (String) viagens.get(viagemSelecionada).get(getText(R.string.id_vo).toString());
        switch (item) {
            case 0:
                intent = new Intent(this, ViagemActivity.class);
                intent.putExtra(Constantes.VIAGEM_ID, id);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(this, GastoActivity.class);
                intent.putExtra(Constantes.VIAGEM_ID, id);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(this, GastoListActivity.class);
                intent.putExtra(Constantes.VIAGEM_ID, id);
                startActivity(intent);
                break;
            case 3:
                dialogConfirmacao.show();
                break;
            case DialogInterface.BUTTON_POSITIVE:
                viagens.remove(viagemSelecionada);
                dao.removerViagem(new Viagem(Long.parseLong(id)));
                getListView().invalidateViews();
                Toast.makeText(this, R.string.viagem_excluida_sucesso, Toast.LENGTH_SHORT).show();
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

    private AlertDialog criaAlertDialog() {
        final CharSequence[] items = {
                getString(R.string.editar),
                getString(R.string.novo_gasto),
                getString(R.string.gastos_realizados),
                getString(R.string.remover) };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.opcoes);
        builder.setItems(items, this);
        return builder.create();
    }

    private List<Map<String, Object>> listarViagens() {
        viagens = criarListViewViagens(dao.listarViagens());
        return viagens;
    }

    private List<Map<String, Object>> criarListViewViagens(List<Viagem> listaViagens){
        List<Map<String, Object>> viagens = new ArrayList<Map<String, Object>>();
        for (Viagem viagem : listaViagens) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put(getText(R.string.id_vo).toString(), viagem.getId().toString());
            if (viagem.getTipoViagem() == Constantes.VIAGEM_LAZER) {
                item.put(getText(R.string.imagem_vo).toString(), R.drawable.lazer);
            } else {
                item.put(getText(R.string.imagem_vo).toString(), R.drawable.negocios);
            }
            item.put(DatabaseHelper.Viagem.DESTINO, viagem.getDestino());
            item.put(getText(R.string.data_vo).toString(),
                    dateFormat.format(viagem.getDataChegada()) + " a " + dateFormat.format(viagem.getDataSaida()));
            double totalGasto = dao.sumGastoViagem(viagem);
            item.put(getText(R.string.total_vo).toString(), getText(R.string.gasto_total_vo).toString() + totalGasto);
            double alerta = viagem.getOrcamento() * valorLimite / 100;
            Double [] valores = new Double[] { viagem.getOrcamento(), alerta, totalGasto };
            item.put(getText(R.string.barra_progresso_vo).toString(), valores);
            viagens.add(item);
        }
        return viagens;
    }

    @Override
    public boolean setViewValue(View view, Object data, String textRepresentation) {
        if (view.getId() == R.id.barraProgresso) {
            Double valores[] = (Double[]) data;
            ProgressBar progressBar = (ProgressBar) view;
            progressBar.getProgressDrawable().setColorFilter(
                    Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
            progressBar.setMax(valores[0].intValue());
            progressBar.setSecondaryProgress(
                    valores[1].intValue());
            progressBar.setProgress(
                    valores[2].intValue());
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        dao.close();
        super.onDestroy();
    }
}
