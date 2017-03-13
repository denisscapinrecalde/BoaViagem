package br.com.denis.boaviagem;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViagemListActivity extends ListActivity implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener,SimpleAdapter.ViewBinder  {

    private List<Map<String, Object>> viagens;
    private AlertDialog alertDialog;
    private int viagemSelecionada;
    private AlertDialog dialogConfirmacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] de = {"imagem", "destino", "data", "total", "barraProgresso"};
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
        switch (item) {
            case 0:
                startActivity(new Intent(this,
                        ViagemActivity.class));
                break;
            case 1:
                startActivity(new Intent(this,
                        GastoActivity.class));
                break;
            case 2:
                startActivity(new Intent(this,GastoListActivity.class));
                break;
            case 3:
                dialogConfirmacao.show();
                break;
            case DialogInterface.BUTTON_POSITIVE:
                viagens.remove(viagemSelecionada);
                getListView().invalidateViews();
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
            viagens = new ArrayList<Map<String, Object>>();
            Map<String, Object> item =
                    new HashMap<String, Object>();
            item.put("imagem", R.drawable.briefcase);
            item.put("destino", "São Paulo");
            item.put("data", "02/02/2012 a 04/02/2012");
            item.put("total", "Gasto total R$ 314,98");
            item.put("barraProgresso",
                    new Double[]{ 500.0, 450.0, 314.98});
            viagens.add(item);
            item = new HashMap<String, Object>();
            item.put("imagem", R.drawable.briefcase);
            item.put("destino", "Maceió");
            item.put("data", "14/05/2012 a 22/05/2012");
            item.put("total", "Gasto total R$ 25834,67");
            item.put("barraProgresso",
                new Double[]{ 50000.0, 45000.0, 25834.67});
            viagens.add(item);
            item = new HashMap<String, Object>();
            item.put("imagem", R.drawable.briefcase);
            item.put("destino", "Rio de Janeiro");
            item.put("data", "14/08/2014 a 22/09/2014");
            item.put("total", "Gasto total R$ 1834,67");
        item.put("barraProgresso",
                new Double[]{ 5000.0, 4500.0, 1834.67});
            viagens.add(item);
            item = new HashMap<String, Object>();
            item.put("imagem", R.drawable.briefcase);
            item.put("destino", "Bahia");
            item.put("data", "14/05/2016 a 22/05/2016");
            item.put("total", "Gasto total R$ 4834,67");
        item.put("barraProgresso",
                new Double[]{ 5000.0, 4500.0, 4834.67});
            viagens.add(item);
            item = new HashMap<String, Object>();
            item.put("imagem", R.drawable.briefcase);
            item.put("destino", "Bahia");
            item.put("data", "14/05/2016 a 22/05/2016");
            item.put("total", "Gasto total R$ 4834,67");
        item.put("barraProgresso",
                new Double[]{ 5000.0, 4500.0, 4834.67});
            viagens.add(item);
        item = new HashMap<String, Object>();
            item.put("imagem", R.drawable.briefcase);
            item.put("destino", "Bahia");
            item.put("data", "14/05/2016 a 22/05/2016");
            item.put("total", "Gasto total R$ 4834,67");
        item.put("barraProgresso",
                new Double[]{ 5000.0, 4500.0, 4834.67});
            viagens.add(item);
        item = new HashMap<String, Object>();
            item.put("imagem", R.drawable.briefcase);
            item.put("destino", "Bahia");
            item.put("data", "14/05/2016 a 22/05/2016");
            item.put("total", "Gasto total R$ 4834,67");
        item.put("barraProgresso",
                new Double[]{ 5000.0, 4500.0, 4834.67});
            viagens.add(item);
        item = new HashMap<String, Object>();
            item.put("imagem", R.drawable.briefcase);
            item.put("destino", "Bahia");
            item.put("data", "14/05/2016 a 22/05/2016");
            item.put("total", "Gasto total R$ 4834,67");
        item.put("barraProgresso",
                new Double[]{ 5000.0, 4500.0, 4834.67});
            viagens.add(item);
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
}
