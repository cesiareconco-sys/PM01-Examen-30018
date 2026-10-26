package com.ucenm.pm01_examen_18;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private EditText etNombre, etTelefono, etNota;
    private Spinner spinnerPais;
    private ImageView ivImagen;
    private DatabaseHelper dbHelper;
    private byte[] imagenBytes = null;
    private static final int PICK_IMAGE = 100;
    private static final int REQUEST_CAMERA = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNombre = findViewById(R.id.etNombre);
        etTelefono = findViewById(R.id.etTelefono);
        etNota = findViewById(R.id.etNota);
        spinnerPais = findViewById(R.id.spinnerPais);
        ivImagen = findViewById(R.id.ivImagen);
        dbHelper = new DatabaseHelper(this);

        List<String> paises = new ArrayList<>();
        paises.add("Honduras (504)");
        paises.add("Costa Rica (506)");
        paises.add("Guatemala (502)");
        paises.add("El Salvador (503)");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, paises);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPais.setAdapter(adapter);
        spinnerPais.setSelection(0);

        findViewById(R.id.btnSeleccionarImagen).setOnClickListener(v -> {
            String[] opciones = {"Tomar Foto", "Elegir de Galería", "Cancelar"};
            new AlertDialog.Builder(this)
                    .setTitle("Imagen")
                    .setItems(opciones, (dialog, which) -> {
                        if (which == 0) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, REQUEST_CAMERA);
                        } else if (which == 1) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, PICK_IMAGE);
                        }
                    }).show();
        });

        findViewById(R.id.btnGuardar).setOnClickListener(v -> {
            if (validarCampos()) {
                Contacto c = new Contacto();
                c.setPais(spinnerPais.getSelectedItem().toString());
                c.setNombre(etNombre.getText().toString().trim());
                c.setTelefono(etTelefono.getText().toString().trim());
                c.setNota(etNota.getText().toString().trim());
                c.setImagen(imagenBytes);

                long id = dbHelper.agregarContacto(c);
                if (id != -1) {
                    Toast.makeText(this, "Contacto guardado ✅", Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                } else {
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.btnVerContactos).setOnClickListener(v ->
                startActivity(new Intent(this, ContactosActivity.class)));
    }

    private boolean validarCampos() {
        String nombre = etNombre.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String nota = etNota.getText().toString().trim();

        if (nombre.isEmpty()) { mostrarAlerta("Debe escribir un nombre"); return false; }
        if (!Pattern.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", nombre)) {
            mostrarAlerta("El nombre solo debe contener letras"); return false;
        }
        if (telefono.isEmpty()) { mostrarAlerta("Debe escribir un teléfono"); return false; }
        if (!Pattern.matches("^[0-9]+$", telefono)) {
            mostrarAlerta("El teléfono solo debe contener números"); return false;
        }
        if (nota.isEmpty()) { mostrarAlerta("Debe escribir una nota"); return false; }
        return true;
    }

    private void mostrarAlerta(String mensaje) {
        new AlertDialog.Builder(this).setTitle("Alerta").setMessage(mensaje)
                .setPositiveButton("OK", null).show();
    }

    private void limpiarCampos() {
        etNombre.setText(""); etTelefono.setText(""); etNota.setText("");
        ivImagen.setImageResource(android.R.drawable.ic_menu_gallery);
        imagenBytes = null; spinnerPais.setSelection(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            try {
                if (requestCode == PICK_IMAGE) {
                    Uri uri = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    ivImagen.setImageBitmap(bitmap); imagenABytes(bitmap);
                } else if (requestCode == REQUEST_CAMERA) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    ivImagen.setImageBitmap(bitmap); imagenABytes(bitmap);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void imagenABytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        imagenBytes = stream.toByteArray();
    }
}