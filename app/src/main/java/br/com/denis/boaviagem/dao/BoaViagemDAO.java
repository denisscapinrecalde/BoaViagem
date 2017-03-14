package br.com.denis.boaviagem.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.denis.boaviagem.domain.Gasto;
import br.com.denis.boaviagem.domain.Viagem;
import br.com.denis.boaviagem.helper.DatabaseHelper;

public class BoaViagemDAO {

    private DatabaseHelper helper;
    private SQLiteDatabase db;

    public BoaViagemDAO(Context context){
        helper = new DatabaseHelper(context);
    }

    public List<Viagem> listarViagens(){
        Cursor cursor = getDb().query(DatabaseHelper.Viagem.TABELA,
                DatabaseHelper.Viagem.COLUNAS,
                null, null, null, null, null);
        List<Viagem> viagens = new ArrayList<Viagem>();
        while(cursor.moveToNext()){
            Viagem viagem = criarViagem(cursor);
            viagens.add(viagem);
        }
        cursor.close();
        return viagens;
    }

    public Viagem buscarViagemPorId(Viagem viagem){
        Cursor cursor = getDb().query(DatabaseHelper.Viagem.TABELA,
                DatabaseHelper.Viagem.COLUNAS,
                DatabaseHelper.Viagem._ID + " = ?",
                new String[]{ viagem.getId().toString() },
                null, null, null);
        if(cursor.moveToNext()){
            Viagem viagemRetorno = criarViagem(cursor);
            cursor.close();
            return viagemRetorno;
        }

        return null;
    }

    public long persistirViagem(Viagem viagem){
        ContentValues values = popularViagemContentValues(viagem);
        long resultado;
        if(viagem.getId() == null){
            resultado = getDb().insert(DatabaseHelper.Viagem.TABELA, null, values);
        } else {
            resultado = getDb().update(DatabaseHelper.Viagem.TABELA, values, DatabaseHelper.Viagem._ID + " = ?",
                    new String[]{viagem.getId().toString()});
        }
        return resultado;
    }

    private ContentValues popularViagemContentValues(Viagem viagem){
        ContentValues values = new ContentValues();
        if(viagem.getId()!=null)values.put(DatabaseHelper.Viagem._ID, viagem.getId());
        values.put(DatabaseHelper.Viagem.DESTINO, viagem.getDestino());
        values.put(DatabaseHelper.Viagem.DATA_CHEGADA, viagem.getDataChegada().getTime());
        values.put(DatabaseHelper.Viagem.DATA_SAIDA, viagem.getDataSaida().getTime());
        values.put(DatabaseHelper.Viagem.ORCAMENTO, viagem.getOrcamento());
        values.put(DatabaseHelper.Viagem.QUANTIDADE_PESSOAS,viagem.getQuantidadePessoas());
        values.put(DatabaseHelper.Viagem.TIPO_VIAGEM, viagem.getTipoViagem());
        return values;
    }

    private Viagem criarViagem(Cursor cursor){
        Viagem viagem = new Viagem();
        viagem.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.Viagem._ID)));
        viagem.setTipoViagem(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.Viagem.TIPO_VIAGEM)));
        viagem.setDestino(cursor.getString(cursor.getColumnIndex(DatabaseHelper.Viagem.DESTINO)));
        viagem.setDataChegada(new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.Viagem.DATA_CHEGADA))));
        viagem.setDataSaida(new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.Viagem.DATA_SAIDA))));
        viagem.setOrcamento(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.Viagem.ORCAMENTO)));
        viagem.setQuantidadePessoas(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.Viagem.QUANTIDADE_PESSOAS)));
        return viagem;
    }

    public void removerViagem(Viagem viagem) {
        String where [] = new String[]{ viagem.getId().toString() };
        getDb().delete(DatabaseHelper.Gasto.TABELA, DatabaseHelper.Gasto.VIAGEM_ID + " = ?", where);
        getDb().delete(DatabaseHelper.Viagem.TABELA, DatabaseHelper.Viagem._ID + " = ?", where);
    }

    public double sumGastoViagem(Viagem viagem) {
        Cursor cursor = getDb().rawQuery(
                "SELECT SUM(" + DatabaseHelper.Gasto.VALOR + ") FROM " + DatabaseHelper.Gasto.TABELA + " " +
                        "WHERE " + DatabaseHelper.Gasto.VIAGEM_ID + " = ?",
                new String[]{ viagem.getId().toString() }
        );
        cursor.moveToFirst();
        double total = cursor.getDouble(0);
        cursor.close();
        return total;
    }

    public long persistirGasto(Gasto gasto){
        ContentValues values = popularGastoContentValues(gasto);
        long resultado;
        if(gasto.getId() == null){
            resultado = getDb().insert(DatabaseHelper.Gasto.TABELA, null, values);
        } else {
            resultado = getDb().update(DatabaseHelper.Gasto.TABELA, values, DatabaseHelper.Gasto._ID + " = ?",
                    new String[]{gasto.getId().toString()});
        }
        return resultado;
    }

    public List<Gasto> listarGastosPorViagem(Gasto gasto){
        Cursor cursor = getDb().query(DatabaseHelper.Gasto.TABELA,
                DatabaseHelper.Gasto.COLUNAS,
                DatabaseHelper.Gasto.VIAGEM_ID + " = ?",
                new String[]{ gasto.getViagemId().toString() },
                null, null, null);
        List<Gasto> gastos = new ArrayList<Gasto>();
        while(cursor.moveToNext()){
            Gasto gastoRetorno = criarGasto(cursor);
            gastos.add(gastoRetorno);
        }
        cursor.close();
        return gastos;
    }

    private Gasto criarGasto(Cursor cursor){
        Gasto gasto = new Gasto();
        gasto.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.Gasto._ID)));
        gasto.setCategoria(cursor.getString(cursor.getColumnIndex(DatabaseHelper.Gasto.CATEGORIA)));
        gasto.setLocal(cursor.getString(cursor.getColumnIndex(DatabaseHelper.Gasto.LOCAL)));
        gasto.setViagemId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.Gasto.VIAGEM_ID)));
        gasto.setValor(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.Gasto.VALOR)));
        gasto.setDescricao(cursor.getString(cursor.getColumnIndex(DatabaseHelper.Gasto.DESCRICAO)));
        gasto.setData(new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.Gasto.DATA))));
        return gasto;
    }

    private ContentValues popularGastoContentValues(Gasto gasto) {
        ContentValues values = new ContentValues();
        if(gasto.getId()!=null)values.put(DatabaseHelper.Gasto._ID, gasto.getId());
        values.put(DatabaseHelper.Gasto.CATEGORIA, gasto.getCategoria());
        values.put(DatabaseHelper.Gasto.DATA, gasto.getData().getTime());
        values.put(DatabaseHelper.Gasto.DESCRICAO, gasto.getDescricao());
        values.put(DatabaseHelper.Gasto.LOCAL, gasto.getLocal());
        values.put(DatabaseHelper.Gasto.VALOR, gasto.getValor());
        values.put(DatabaseHelper.Gasto.VIAGEM_ID, gasto.getViagemId());
        return values;
    }

    public void removerGasto(Gasto gasto) {
        String where [] = new String[]{ gasto.getId().toString() };
        getDb().delete(DatabaseHelper.Gasto.TABELA, DatabaseHelper.Gasto._ID + " = ?", where);
    }

    private SQLiteDatabase getDb() {
        if (db == null) {
            db = helper.getWritableDatabase();
        }
        return db;
    }
    public void close(){
        helper.close();
    }
}
