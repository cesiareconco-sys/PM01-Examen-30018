package com.ucenm.pm01_examen_18;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ContactosDB.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CONTACTOS = "contactos";

    private static final String KEY_ID = "id";
    private static final String KEY_PAIS = "pais";
    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_TELEFONO = "telefono";
    private static final String KEY_NOTA = "nota";
    private static final String KEY_IMAGEN = "imagen";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_CONTACTOS + " (" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_PAIS + " TEXT NOT NULL," +
                KEY_NOMBRE + " TEXT NOT NULL," +
                KEY_TELEFONO + " TEXT NOT NULL," +
                KEY_NOTA + " TEXT," +
                KEY_IMAGEN + " BLOB)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTOS);
        onCreate(db);
    }

    public long agregarContacto(Contacto contacto) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PAIS, contacto.getPais());
        values.put(KEY_NOMBRE, contacto.getNombre());
        values.put(KEY_TELEFONO, contacto.getTelefono());
        values.put(KEY_NOTA, contacto.getNota());
        values.put(KEY_IMAGEN, contacto.getImagen());
        long id = db.insert(TABLE_CONTACTOS, null, values);
        db.close();
        return id;
    }

    public ArrayList<Contacto> obtenerContactos() {
        ArrayList<Contacto> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTOS, null, null, null, null, null, KEY_NOMBRE + " ASC");

        if (cursor.moveToFirst()) {
            do {
                Contacto c = new Contacto();
                c.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                c.setPais(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PAIS)));
                c.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NOMBRE)));
                c.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TELEFONO)));
                c.setNota(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NOTA)));
                c.setImagen(cursor.getBlob(cursor.getColumnIndexOrThrow(KEY_IMAGEN)));
                lista.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public int actualizarContacto(Contacto contacto) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PAIS, contacto.getPais());
        values.put(KEY_NOMBRE, contacto.getNombre());
        values.put(KEY_TELEFONO, contacto.getTelefono());
        values.put(KEY_NOTA, contacto.getNota());
        values.put(KEY_IMAGEN, contacto.getImagen());
        int filas = db.update(TABLE_CONTACTOS, values, KEY_ID + " = ?", new String[]{String.valueOf(contacto.getId())});
        db.close();
        return filas;
    }

    public int eliminarContacto(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int filas = db.delete(TABLE_CONTACTOS, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return filas;
    }
}