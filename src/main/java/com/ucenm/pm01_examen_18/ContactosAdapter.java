package com.ucenm.pm01_examen_18;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class ContactosAdapter extends BaseAdapter {
    private Context contexto;
    private ArrayList<Contacto> lista;
    private int posicionSeleccionada = -1;

    public ContactosAdapter(Context contexto, ArrayList<Contacto> lista) {
        this.contexto = contexto;
        this.lista = lista;
    }

    public void setSeleccionado(int pos) {
        this.posicionSeleccionada = pos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() { return lista.size(); }

    @Override
    public Contacto getItem(int position) { return lista.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(contexto)
                    .inflate(R.layout.item_contacto, parent, false);
        }
        Contacto c = getItem(position);
        ((TextView) convertView.findViewById(R.id.tvNombre)).setText(c.getNombre());
        ((TextView) convertView.findViewById(R.id.tvTelefono)).setText(c.getPais() + "  " + c.getTelefono());
        ((TextView) convertView.findViewById(R.id.tvNota)).setText(c.getNota());

        if (position == posicionSeleccionada) {
            convertView.setBackgroundColor(0xFFBBDEFB);
        } else {
            convertView.setBackgroundColor(0xFFFFFFFF);
        }
        return convertView;
    }
}
