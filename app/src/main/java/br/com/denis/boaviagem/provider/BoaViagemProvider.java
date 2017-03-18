package br.com.denis.boaviagem.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import br.com.denis.boaviagem.helper.DatabaseHelper;

import static br.com.denis.boaviagem.contract.BoaViagemContract.*;

public class BoaViagemProvider extends ContentProvider {

    private static final int VIAGENS = 1;
    private static final int VIAGEM_ID = 2;
    private static final int GASTOS = 3;
    private static final int GASTO_ID = 4;
    private static final int GASTOS_VIAGEM_ID = 5;
    private static final UriMatcher uriMatcher =  new UriMatcher(UriMatcher.NO_MATCH);

    private static String uriDesconhecida = "Uri desconhecida";

    private DatabaseHelper helper;

    static{
        uriMatcher.addURI(AUTHORITY, VIAGEM_PATH, VIAGENS);
        uriMatcher.addURI(AUTHORITY, VIAGEM_PATH + "/#", VIAGEM_ID);
        uriMatcher.addURI(AUTHORITY, GASTO_PATH, GASTOS);
        uriMatcher.addURI(AUTHORITY, GASTO_PATH + "/#", GASTO_ID);
        uriMatcher.addURI(AUTHORITY, GASTO_PATH + "/"+ VIAGEM_PATH + "/#", GASTOS_VIAGEM_ID);
    }


    @Override
    public boolean onCreate() {
        helper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = helper.getReadableDatabase();

        switch (uriMatcher.match(uri)) {
            case VIAGENS:
                return database.query(VIAGEM_PATH, projection,
                        selection, selectionArgs, null, null, sortOrder);
            case VIAGEM_ID:
                selection = Viagem._ID + " = ?";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                return database.query(VIAGEM_PATH, projection,
                        selection, selectionArgs, null, null, sortOrder);
            case GASTOS:
                return database.query(GASTO_PATH, projection,
                        selection, selectionArgs, null, null, sortOrder);
            case GASTO_ID:
                selection = Gasto._ID + " = ?";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                return database.query(GASTO_PATH, projection,
                        selection, selectionArgs, null, null, sortOrder);
            case GASTOS_VIAGEM_ID:
                selection = Gasto.VIAGEM_ID + " = ?";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                return database.query(GASTO_PATH, projection,
                        selection, selectionArgs, null, null, sortOrder);
            default:
                throw new IllegalArgumentException(uriDesconhecida);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        switch (uriMatcher.match(uri)) {
            case VIAGENS:
                return Viagem.CONTENT_TYPE;
            case VIAGEM_ID:
                return Viagem.CONTENT_ITEM_TYPE;
            case GASTOS:
            case GASTOS_VIAGEM_ID:
                return Gasto.CONTENT_TYPE;
            case GASTO_ID:
                return Gasto.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException(uriDesconhecida);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase database = helper.getWritableDatabase();
        long id;
        switch (uriMatcher.match(uri)) {
            case VIAGENS:
                id = database.insert(VIAGEM_PATH, null, values);
                return Uri.withAppendedPath(Viagem.CONTENT_URI,
                        String.valueOf(id));
            case GASTOS:
                id = database.insert(GASTO_PATH, null, values);
                return Uri.withAppendedPath(Gasto.CONTENT_URI,
                        String.valueOf(id));
            default:
                throw new IllegalArgumentException(uriDesconhecida);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = helper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case VIAGEM_ID:
                selection = Viagem._ID + " = ?";
                selectionArgs =
                        new String[]{uri.getLastPathSegment()};
                return database.delete(VIAGEM_PATH,
                        selection, selectionArgs);
            case GASTO_ID:
                selection = Gasto._ID + " = ?";
                selectionArgs =
                        new String[]{uri.getLastPathSegment()};
                return database.delete(GASTO_PATH,
                        selection, selectionArgs);
            default:
                throw new IllegalArgumentException(uriDesconhecida);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = helper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case VIAGEM_ID:
                selection = Viagem._ID + " = ?";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                return database.update(VIAGEM_PATH, values,
                        selection, selectionArgs);
            case GASTO_ID:
                selection = Gasto._ID + " = ?";
                selectionArgs = new String[] {uri.getLastPathSegment()};
                return database.update(GASTO_PATH, values,
                        selection, selectionArgs);
            default:
                throw new IllegalArgumentException(uriDesconhecida);
        }
    }
}
