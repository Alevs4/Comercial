package com.example.comercial;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class LiberarFolio extends AppCompatActivity {

    EditText LibFolio, LibProductor, LibVariedad, LibKilos, LibCategoria;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_liberar_folio);

        LibFolio = findViewById(R.id.TxtRespFolio);
        LibProductor = findViewById(R.id.TxtRespProductor);
        LibVariedad = findViewById(R.id.TxtRespVariedad);
        LibKilos = findViewById(R.id.TxtRespKilos);
        LibCategoria = findViewById(R.id.TxtLiberarCategoria);

        ImageButton BtnCasa;

        BtnCasa = findViewById(R.id.BtnCasa);

        BtnCasa.setOnClickListener(v -> {

            Intent volver = new Intent(LiberarFolio.this,
                    Inicio.class
            );

            startActivity(volver);
            finish();

        });

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
    public void consultarFolio() {


        try {
            Statement ejecutor = conexionBD().createStatement();
            ResultSet rst = ejecutor.executeQuery("SELECT * FROM FrutaComercial WHERE Folio = '" + LibFolio.getText().toString() + "' and Estado ='Despachado' ");

            if (rst.next() == true) {

                Toast.makeText(getApplicationContext(), "Folio Despachado o no Existe", Toast.LENGTH_LONG).show();
                LibFolio.setText("");
                LibProductor.setText("");
                LibVariedad.setText("");
                LibKilos.setText("");
                LibCategoria.setText("");

            } else {

                Statement con = conexionBD().createStatement();
                ResultSet rs = con.executeQuery("SELECT * FROM FrutaComercial WHERE Estado = 'Existencia' and Folio = '" + LibFolio.getText().toString() + "'");

                if (rs.next()) {
                    LibProductor.setText(rs.getString(3));
                    LibVariedad.setText(rs.getString(6));
                    LibKilos.setText(rs.getString(5));
                    LibCategoria.setText(rs.getString(10));
                }


            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public void Consultar(View view){
        consultarFolio();
    }
    public void Liberar(View view){
        CambiarEstado();
    }
    public void CambiarEstado(){
        if (LibFolio.getText().toString().isEmpty() ||  LibProductor.getText().toString().isEmpty()
                || LibVariedad.getText().toString().isEmpty() || LibCategoria.getText().toString().isEmpty()
                || LibKilos.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "No debe haber campos vacios", Toast.LENGTH_LONG).show();
        } else {
            try {

                PreparedStatement ps = conexionBD().prepareStatement("UPDATE FrutaComercial SET Estado = 'Despachado' WHERE  Folio = '" + LibFolio.getText().toString() + "' ");
                ps.executeUpdate();

                Toast.makeText(getApplicationContext(), "Guardado", Toast.LENGTH_LONG).show();
                LibFolio.setText("");
                LibProductor.setText("");
                LibVariedad.setText("");
                LibKilos.setText("");
                LibCategoria.setText("");

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}