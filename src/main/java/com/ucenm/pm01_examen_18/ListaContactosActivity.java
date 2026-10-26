package com.ucenm.pm01_examen_18;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class ListaContactosActivity extends AppCompatActivity implements ContactoAdapter.OnContactoActionListener {
    private ListView listaView;
    private DatabaseHelper db;
    private List<Contacto> listaContactos;
    private ContactoAdapter adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contactos);

        db = new DatabaseHelper(this);
        listaView = findViewById(R.id.listaContactos);

        cargarContactos();

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
    }

    private void cargarContactos() {
        listaContactos = db.obtenerContactos();
        adaptador = new ContactoAdapter(this, listaContactos, this);
        listaView.setAdapter(adaptador);
    }

    @Override
    public void onEditarClick(int posicion) {
        Toast.makeText(this, "Función de editar - " + listaContactos.get(posicion).getNombre(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEliminarClick(int posicion) {
        Contacto c = listaContactos.get(posicion);
        db.eliminarContacto(c.getId());
        Toast.makeText(this, "Eliminado: " + c.getNombre(), Toast.LENGTH_SHORT).show();
        cargarContactos();
    }
}
