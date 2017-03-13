package br.com.denis.boaviagem;

import android.app.ListActivity;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GastoListActivity extends ListActivity implements AdapterView.OnItemClickListener {

    private List<Map<String, Object>> gastos;
    private String dataAnterior = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] de = {
                "data", "descricao", "valor", "categoria"
        };
        int[] para = { R.id.data, R.id.descricao,
                R.id.valor, R.id.categoria };
        SimpleAdapter adapter = new SimpleAdapter(this,
                listarGastos(), R.layout.lista_gasto, de, para);
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
            gastos.remove(info.position);
            getListView().invalidateViews();
            dataAnterior = "";
            Toast.makeText(this, "Remover do banco", Toast.LENGTH_SHORT).show();
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
        String descricao = (String) map.get("descricao");
        String mensagem = "Gasto selecionado: " + descricao;
        Toast.makeText(this, mensagem,Toast.LENGTH_SHORT).show();
    }

    private List<Map<String, Object>> listarGastos() {
        gastos = new ArrayList<Map<String, Object>>();
        Map<String, Object> item =
                new HashMap<String, Object>();
        item.put("data", "04/02/2014");
        item.put("descricao", "Diária Hotel");
        item.put("valor", "R$ 260,00");
        item.put("categoria", R.color.categoria_hospedagem);
        gastos.add(item);

        item = new HashMap<String, Object>();
        item.put("data", "03/04/2012");
        item.put("descricao", "Diária Hotel");
        item.put("valor", "R$ 160,00");
        item.put("categoria", R.color.categoria_hospedagem);
        gastos.add(item);

        item = new HashMap<String, Object>();
        item.put("data", "04/02/2014");
        item.put("descricao", "Côco Bambu");
        item.put("valor", "R$ 160,00");
        item.put("categoria", R.color.categoria_alimentacao);
        gastos.add(item);

        item = new HashMap<String, Object>();
        item.put("data", "04/02/2016");
        item.put("descricao", "Translado");
        item.put("valor", "R$ 320,00");
        item.put("categoria", R.color.categoria_transporte);
        gastos.add(item);

        item = new HashMap<String, Object>();
        item.put("data", "04/02/2016");
        item.put("descricao", "Passeio Oeste");
        item.put("valor", "R$ 350,00");
        item.put("categoria", R.color.categoria_transporte);
        gastos.add(item);

        item = new HashMap<String, Object>();
        item.put("data", "07/02/2016");
        item.put("descricao", "Passeio Leste");
        item.put("valor", "R$ 125,00");
        item.put("categoria", R.color.categoria_transporte);
        gastos.add(item);

        return gastos;
    }
}
