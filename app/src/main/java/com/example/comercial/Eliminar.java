package com.example.comercial;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@SuppressLint("NotConstructor")
public class Eliminar extends AppCompatActivity {


    // Campos de la pantalla
    EditText TxtFolio;
    EditText TxtProductor;
    EditText TxtVariedad;
    EditText TxtKilos;
    EditText TxtCategoria;

    // Indica si el folio encontrado existe
    boolean folioEncontrado = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_eliminar);

        TxtFolio = findViewById(R.id.TxtEliminarFolio);
        TxtProductor = findViewById(R.id.TxtEliminarProductor);
        TxtVariedad = findViewById(R.id.TxtEliminarVariedad);
        TxtKilos = findViewById(R.id.TxtEliminarKilos);
        TxtCategoria = findViewById(R.id.TxtEliminarCategoria);

        ImageButton BtnCasa;

        BtnCasa = findViewById(R.id.BtnCasa);

        BtnCasa.setOnClickListener(v -> {

            Intent volver = new Intent(
                    Eliminar.this,
                    Inicio.class
            );

            startActivity(volver);
            finish();

        });

        // Al comenzar no existe ningún folio seleccionado
        folioEncontrado = false;

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public Connection conexionBD() {
        Connection conexion = null;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            conexion = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.201.76;databaseName=Fruta;user=admin;password=1234;");

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return conexion;
    }

    // =========================================================
    // BUSCAR FOLIO
    // =========================================================

    public void Buscar(View view) {

        String folioTexto = TxtFolio.getText().toString().trim();

        // Comprobamos que se haya ingresado un folio
        if (folioTexto.isEmpty()) {

            Toast.makeText(
                    this,
                    "Ingrese un número de folio",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        try {

            Connection conexion = conexionBD();

            if (conexion == null) {
                return;
            }

            Statement ejecutor = conexion.createStatement();

            String consulta =
                    "SELECT Folio, Productor, Variedad, Kilos, categoria " +
                            "FROM FrutaComercial " +
                            "WHERE Folio = " + folioTexto;

            ResultSet rs = ejecutor.executeQuery(consulta);

            if (rs.next()) {

                // -------------------------------------------------
                // Encontramos el folio
                // -------------------------------------------------

                TxtProductor.setText(
                        rs.getString("Productor")
                );

                TxtVariedad.setText(
                        rs.getString("Variedad")
                );

                TxtKilos.setText(
                        String.valueOf(
                                rs.getFloat("Kilos")
                        )
                );

                TxtCategoria.setText(
                        rs.getString("categoria")
                );

                folioEncontrado = true;

                Toast.makeText(
                        this,
                        "Folio encontrado",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                // -------------------------------------------------
                // No encontramos el folio
                // -------------------------------------------------

                folioEncontrado = false;

                limpiarDatos();

                Toast.makeText(
                        this,
                        "El folio no existe",
                        Toast.LENGTH_LONG
                ).show();
            }

            rs.close();
            ejecutor.close();
            conexion.close();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Error al consultar el folio: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }
    public void Eliminar(View view) {

        // Verificamos que primero se haya buscado un folio
        if (!folioEncontrado) {

            Toast.makeText(
                    this,
                    "Primero debe buscar un folio válido",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        // Creamos el cuadro de confirmación
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Eliminar Registro");

        builder.setMessage(
                "¿Está seguro de eliminar el folio "
                        + TxtFolio.getText().toString()
                        + "?"
        );

        // Botón SI
        builder.setPositiveButton("Sí, eliminar",
                (dialog, which) -> eliminarRegistro());

        // Botón CANCELAR
        builder.setNegativeButton("Cancelar",
                (dialog, which) -> dialog.dismiss());

        // Mostrar ventana
        builder.show();
    }
    private void eliminarRegistro() {

        String folioTexto =
                TxtFolio.getText().toString().trim();

        try {

            Connection conexion = conexionBD();

            if (conexion == null) {
                return;
            }

            Statement ejecutor =
                    conexion.createStatement();

            String sql =
                    "DELETE FROM FrutaComercial " +
                            "WHERE Folio = " + folioTexto;

            int filasAfectadas =
                    ejecutor.executeUpdate(sql);

            if (filasAfectadas > 0) {

                Toast.makeText(
                        this,
                        "Folio eliminado correctamente",
                        Toast.LENGTH_LONG
                ).show();

                // Limpiamos la pantalla
                TxtFolio.setText("");
                limpiarDatos();

                folioEncontrado = false;

            } else {

                Toast.makeText(
                        this,
                        "No se pudo eliminar el folio",
                        Toast.LENGTH_LONG
                ).show();
            }

            ejecutor.close();
            conexion.close();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Error al eliminar: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }
    private void limpiarDatos() {

        TxtProductor.setText("");
        TxtVariedad.setText("");
        TxtKilos.setText("");
        TxtCategoria.setText("");
    }

    public void Casa (View view) {

        Intent siguiente =
                new Intent(this, Inicio.class);

        startActivity(siguiente);

        finish();
    }
}