package com.example.comercial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Inicio extends AppCompatActivity {


    Button registrar;
    Button editar;
    Button consultar;
    Button liberar;
    Button eliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio);


        registrar = findViewById(R.id.BtnRegistrar);
        editar = findViewById(R.id.BtnEditar);
        consultar = findViewById(R.id.BtnConsultar);
        liberar = findViewById(R.id.BtnLiberar);
        eliminar = findViewById(R.id.BtnEliminar);


        configurarPermisos();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void configurarPermisos() {

        // Obtenemos la sesión del usuario actual
        SesionManager session = new SesionManager(this);

        // Obtenemos el perfil guardado durante el Login
        String perfil = session.getPerfil();

        // Convertimos el perfil a mayúsculas para evitar problemas
        // si viene como "admin", "Admin", etc.
        perfil = perfil.trim().toUpperCase();


        // ============================================================
        // PRIMERO OCULTAMOS TODOS LOS BOTONES
        // ============================================================
        // De esta forma ningún botón queda habilitado por error.
        // Después mostramos solamente los que corresponden al perfil.

        registrar.setVisibility(View.GONE);
        editar.setVisibility(View.GONE);
        consultar.setVisibility(View.GONE);
        liberar.setVisibility(View.GONE);
        eliminar.setVisibility(View.GONE);


        // ============================================================
        // USUARIO ADMIN
        // ============================================================
        // ADMIN tiene acceso a todas las opciones.

        if (perfil.equals("ADMIN")) {

            registrar.setVisibility(View.VISIBLE);
            editar.setVisibility(View.VISIBLE);
            consultar.setVisibility(View.VISIBLE);
            liberar.setVisibility(View.VISIBLE);
            eliminar.setVisibility(View.VISIBLE);
        }


        // ============================================================
        // USUARIO PACKING
        // ============================================================
        // PACKING puede:
        // - Registrar
        // - Editar
        // - Consultar
        //
        // No puede:
        // - Despachar
        // - Eliminar

        else if (perfil.equals("PACKING")) {

            registrar.setVisibility(View.VISIBLE);
            editar.setVisibility(View.VISIBLE);
            consultar.setVisibility(View.VISIBLE);
        }


        // ============================================================
        // USUARIO DESPACHO
        // ============================================================
        // DESPACHO puede:
        // - Consultar
        // - Despachar
        //
        // No puede:
        // - Registrar
        // - Editar
        // - Eliminar

        else if (perfil.equals("DESPACHO")) {

            consultar.setVisibility(View.VISIBLE);
            liberar.setVisibility(View.VISIBLE);
        }


        // ============================================================
        // PERFIL NO RECONOCIDO
        // ============================================================
        // Si el usuario no tiene perfil o tiene uno incorrecto,
        // no se mostrará ninguna opción.

        else {

            Toast.makeText(
                    this,
                    "Usuario sin permisos asignados",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    public void Registrar(View view){

        Intent registro = new Intent(this, IngresoFruta.class);
        startActivity(registro);
    }
    public void Editar(View view){

        Intent editar = new Intent(this, EditarFolio.class);
        startActivity(editar);
    }
    public void Consultar(View view){

        Intent consulta = new Intent(this, ConsultarFolio.class);
        startActivity(consulta);
    }
    public void Liberar(View view){

        Intent libera = new Intent(this, LiberarFolio.class);
        startActivity(libera);
    }
    public void Eliminar(View view){
        Intent elimina = new Intent(this, Eliminar.class);
        startActivity(elimina);
    }
}