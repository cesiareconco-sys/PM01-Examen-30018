package com.ucenm.pm01_examen_18;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ContactosActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ContactosAdapter adaptador;
    private ArrayList<Contacto> listaCompleta;
    private Contacto contactoSeleccionado = null;
    private ListView listaContactos;
    private EditText etBuscar;
    private static final int REQUEST_CALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        dbHelper = new DatabaseHelper(this);
        listaContactos = findViewById(R.id.listaContactos);
        etBuscar = findViewById(R.id.etBuscar);

        cargarContactos();

        findViewById(R.id.btnBuscar).setOnClickListener(v -> buscarContacto());

        listaContactos.setOnItemClickListener((parent, view, position, id) -> {
            contactoSeleccionado = adaptador.getItem(position);
            adaptador.setSeleccionado(position);
        });

        findViewById(R.id.btnCompartir).setOnClickListener(v -> compartir());
        findViewById(R.id.btnVerImagen).setOnClickListener(v -> verImagen());
        findViewById(R.id.btnEliminar).setOnClickListener(v -> eliminar());
        findViewById(R.id.btnActualizar).setOnClickListener(v -> actualizar());
        findViewById(R.id.btnLlamar).setOnClickListener(v -> confirmarLlamada());
    }

    private void cargarContactos() {
        listaCompleta = dbHelper.obtenerContactos();
        adaptador = new ContactosAdapter(this, listaCompleta);
        listaContactos.setAdapter(adaptador);
        contactoSeleccionado = null;
    }

    private void buscarContacto() {
        String texto = etBuscar.getText().toString().trim().toLowerCase();
        ArrayList<Contacto> filtrada;
        if (texto.isEmpty()) {
            filtrada = listaCompleta;
        } else {
            filtrada = listaCompleta.stream()
                    .filter(c -> c.getNombre().toLowerCase().contains(texto)
                            || c.getTelefono().contains(texto))
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        adaptador = new ContactosAdapter(this, filtrada);
        listaContactos.setAdapter(adaptador);
    }

    private void compartir() {
        if (contactoSeleccionado == null) {
            Toast.makeText(this, "Seleccione un contacto", Toast.LENGTH_SHORT).show();
            return;
        }
        Contacto c = contactoSeleccionado;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,
                c.getNombre() + "\n" + c.getPais() + "\n" + c.getTelefono() + "\n" + c.getNota());
        startActivity(Intent.createChooser(intent, "Compartir con"));
    }

    private void verImagen() {
        if (contactoSeleccionado == null) {
            Toast.makeText(this, "Seleccione un contacto", Toast.LENGTH_SHORT).show();
            return;
        }
        if (contactoSeleccionado.getImagen() == null) {
            Toast.makeText(this, "Sin imagen guardada", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Imagen de " + contactoSeleccionado.getNombre());
        ImageView iv = new ImageView(this);
        iv.setPadding(20, 20, 20, 20);
        iv.setImageBitmap(BitmapFactory.decodeByteArray(
                contactoSeleccionado.getImagen(), 0, contactoSeleccionado.getImagen().length));
        builder.setView(iv);
        builder.setPositiveButton("Cerrar", null);
        builder.show();
    }

    private void eliminar() {
        if (contactoSeleccionado == null) {
            Toast.makeText(this, "Seleccione un contacto", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Eliminar")
                .setMessage("¿Eliminar a " + contactoSeleccionado.getNombre() + "?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    dbHelper.eliminarContacto(contactoSeleccionado.getId());
                    cargarContactos();
                    Toast.makeText(ContactosActivity.this, "Eliminado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void actualizar() {
        if (contactoSeleccionado == null) {
            Toast.makeText(this, "Seleccione un contacto", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Carga los datos en el formulario para editar", Toast.LENGTH_SHORT).show();
    }

    private void confirmarLlamada() {
        if (contactoSeleccionado == null) {
            Toast.makeText(this, "Seleccione un contacto", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Confirmar llamada")
                .setMessage("¿Desea llamar a " + contactoSeleccionado.getNombre() + "?\n"
                        + contactoSeleccionado.getPais() + " " + contactoSeleccionado.getTelefono())
                .setPositiveButton("Llamar", (dialog, which) -> hacerLlamada())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void hacerLlamada() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                    + contactoSeleccionado.getTelefono()));
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            hacerLlamada();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarContactos();
    }
}