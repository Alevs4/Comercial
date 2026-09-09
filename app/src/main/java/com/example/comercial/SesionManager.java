package com.example.comercial;

import android.content.Context;
import android.content.SharedPreferences;

public class SesionManager {
    // Nombre de la sesión que se guardará en el teléfono
    private static final String NOMBRE_SESION = "SesionComercial";

    // Datos que vamos a guardar
    private static final String ID_USUARIO = "id_usu";
    private static final String USUARIO = "usuario";
    private static final String PERFIL = "perfil";

    private SharedPreferences preferences;

    // Constructor
    public SesionManager(Context context) {

        preferences = context.getSharedPreferences(
                NOMBRE_SESION,
                Context.MODE_PRIVATE
        );
    }

    // Guarda los datos del usuario que inició sesión
    public void guardarUsuario(
            int idUsuario,
            String usuario,
            String perfil
    ) {

        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(ID_USUARIO, idUsuario);
        editor.putString(USUARIO, usuario);
        editor.putString(PERFIL, perfil);

        editor.apply();
    }

    // Devuelve el ID del usuario
    public int getIdUsuario() {

        return preferences.getInt(
                ID_USUARIO,
                0
        );
    }

    // Devuelve el nombre de usuario
    public String getUsuario() {

        return preferences.getString(
                USUARIO,
                ""
        );
    }

    // Devuelve el perfil del usuario
    public String getPerfil() {

        return preferences.getString(
                PERFIL,
                ""
        );
    }

    // Comprueba si existe una sesión
    public boolean existeSesion() {

        return !getUsuario().isEmpty();
    }

    // Elimina la sesión
    public void cerrarSesion() {

        preferences.edit().clear().apply();
    }
}
