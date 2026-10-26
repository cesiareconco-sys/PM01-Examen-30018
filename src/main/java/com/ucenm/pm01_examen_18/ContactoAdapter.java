package com.ucenm.pm01_examen_18;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class ContactoAdapter extends BaseAdapter {
    private Context contexto;
    private List<Contacto> lista;
    private OnContactoActionListener oyente;

    public interface OnContactoActionListener {
        void onEditarClick(int posicion);
        void onEliminarClick(int posicion);
    }

    public ContactoAdapter(Context contexto, List<Contacto> lista, OnContactoActionListener oyente) {
        this.contexto = contexto;
        this.lista = lista;
        this.oyente = oyente;
    }

    @Override
    public int getCount() { return lista.size(); }

    @Override
    public Object getItem(int posicion) { return lista.get(posicion); }

    @Override
    public long getItemId(int posicion) { return posicion; }

    @Override
    public View getView(int posicion, View vistaReciclada, ViewGroup padre) {
        View vista = vistaReciclada;
        if (vista == null) {
            vista = LayoutInflater.from(contexto).inflate(R.layout.item_contacto, padre, false);
        }

        Contacto c = lista.get(posicion);

        TextView tvNombre = vista.findViewById(R.id.tvNombre);
        TextView tvTelefono = vista.findViewById(R.id.tvTelefono);
        Button btnEditar = vista.findViewById(R.id.btnEditar);
        Button btnEliminar = vista.findViewById(R.id.btnEliminar);

        tvNombre.setText(c.getNombre() + " (" + c.getPais() + ")");
        tvTelefono.setText(c.getTelefono());

        btnEditar.setOnClickListener(v -> oyente.onEditarClick(posicion));
        btnEliminar.setOnClickListener(v -> oyente.onEliminarClick(posicion));

        return vista;
    }
}
