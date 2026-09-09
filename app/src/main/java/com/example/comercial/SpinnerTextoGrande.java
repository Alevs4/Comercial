package com.example.comercial;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SpinnerTextoGrande extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> datos;

    public SpinnerTextoGrande(Context context, List<String> datos) {
        super(context, android.R.layout.simple_spinner_item, datos);
        this.context = context;
        this.datos = datos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return crearTexto(position);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return crearTexto(position);
    }

    private TextView crearTexto(int position) {
        TextView texto = new TextView(context);

        texto.setText(datos.get(position));
        texto.setTextSize(20); // Tamaño del texto
        texto.setTextColor(Color.BLACK);
        texto.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        texto.setGravity(Gravity.CENTER_VERTICAL);
        texto.setPadding(20, 0, 20, 0);
        texto.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                60
        ));

        return texto;
    }
}
