package br.com.denis.boaviagem;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.denis.boaviagem.dao.BoaViagemDAO;
import br.com.denis.boaviagem.domain.Gasto;
import br.com.denis.boaviagem.helper.Constantes;
import br.com.denis.boaviagem.helper.DatabaseHelper;

public class GastoListActivity extends ListActivity implements AdapterView.OnItemClickListener {

    private List<Map<String, Object>> gastos;
    private String dataAnterior = "";
    private String idViagem;
    private BoaViagemDAO dao;
    private int gastoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dao = new BoaViagemDAO(this);

        String[] de = {
                "data", "descricao", "valor", "categoria"
        };
        int[] para = { R.id.data, R.id.descricao,
                R.id.valor, R.id.categoria };

        idViagem = getIntent().getStringExtra(Constantes.VIAGEM_ID);

        SimpleAdapter adapter = new SimpleAdapter(this,
                listarGastos(new Gasto(Integer.parseInt(idViagem))), R.layout.lista_gasto, de, para);
        adapter.setViewBinder(new GastoViewBinder());
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
        registerForContextMenu(getListView());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gasto_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.remover) {
            AdapterView.AdapterContextMenuInfo info =
                    (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            String id =
                    (String) gastos.get(gastoSelecionado).get(DatabaseHelper.Gasto._ID).toString();
            dao.removerGasto(new Gasto(Long.parseLong(id)));
            gastos.remove(info.position);
            getListView().invalidateViews();
            dataAnterior = "";
            Toast.makeText(this, R.string.gasto_excluida_sucesso, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ViagemListActivity.class));
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private class GastoViewBinder implements SimpleAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Object data,
                                    String textRepresentation) {
            if(view.getId() == R.id.data){
                if(!dataAnterior.equals(data)){
                    TextView textView = (TextView) view;
                    textView.setText(textRepresentation);
                    dataAnterior = textRepresentation;
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.GONE);
                }
                return true;
            }
            if(view.getId() == R.id.categoria){
                Integer id = (Integer) data;
                view.setBackgroundColor(getResources().getColor(id));
                return true;
            }
            return false;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Map<String, Object> map = gastos.get(position);
        gastoSelecionado = position;
        String descricao = (String) map.get("descricao");
        String mensagem = "Gasto selecionado: " + descricao;
        Toast.makeText(this, mensagem,Toast.LENGTH_SHORT).show();
    }

    private List<Map<String, Object>> criarListViewGastos(List<Gasto> listaGastos){
        List<Map<String, Object>> gastos = new ArrayList<Map<String, Object>>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        for (Gasto gasto : listaGastos) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put(DatabaseHelper.Gasto.DATA, dateFormat.format(gasto.getData()));
            item.put(DatabaseHelper.Gasto.DESCRICAO, gasto.getDescricao());
            item.put(DatabaseHelper.Gasto.VALOR, gasto.getValor());
            item.put(DatabaseHelper.Gasto.CATEGORIA, retornarCorCategoria(gasto));
            item.put(DatabaseHelper.Gasto.LOCAL, gasto.getLocal());
            item.put(DatabaseHelper.Gasto._ID, gasto.getId());
            item.put(DatabaseHelper.Gasto.VIAGEM_ID, gasto.getViagemId());
            gastos.add(item);
        }
        return gastos;
    }

    public Integer retornarCorCategoria(Gasto gasto){
        Integer retorno = R.color.categoria_alimentacao;
        if(gasto.getCategoria().equals(getText(R.string.alimentacao))){
            retorno = R.color.categoria_alimentacao;
        }else if(gasto.getCategoria().equals(getText(R.string.combustivel))){
            retorno = R.color.categoria_combustivel;
        }else if(gasto.getCategoria().equals(getText(R.string.transporte))){
            retorno = R.color.categoria_transporte;
        }else if(gasto.getCategoria().equals(getText(R.string.hospedagem))){
            retorno = R.color.categoria_hospedagem;
        }else if(gasto.getCategoria().equals(getText(R.string.outros))){
            retorno = R.color.categoria_outros;
        }
        return retorno;
    }

    private List<Map<String, Object>> listarGastos(Gasto gasto) {
        gastos = criarListViewGastos(dao.listarGastosPorViagem(gasto));
        return gastos;
    }

    @Override
    protected void onDestroy() {
        dao.close();
        super.onDestroy();
    }
}
