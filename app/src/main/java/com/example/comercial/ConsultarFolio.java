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
import java.sql.ResultSet;
import java.sql.Statement;

public class ConsultarFolio extends AppCompatActivity {

    EditText ConsultarFolio, ConsultarProductor, ConsultarVariedad, ConsultarFecha, ConsultarKilos,
            ConsultarCajas, ConsultarBins, ConsultarEstado, ConsultarCategoria, ConsultarCsg;

    ImageButton btnCasa;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_consultar_folio);

        ConsultarFolio = findViewById(R.id.TxtConsultarFolio);
        ConsultarProductor = findViewById(R.id.TxtConsultarProductor);
        ConsultarVariedad = findViewById(R.id.Txtconsultarvariedad);
        ConsultarFecha = findViewById(R.id.TxtConsultarFecha);
        ConsultarKilos = findViewById(R.id.TxtConsultarKilos);
        ConsultarCajas = findViewById(R.id.TxtConsultarCajas);
        ConsultarBins = findViewById(R.id.TxtConsultarBins);
        ConsultarEstado = findViewById(R.id.TxtConsultarEstado);
        ConsultarCategoria = findViewById(R.id.TxtCategoria);
        ConsultarCsg = findViewById(R.id.TxtConsultarCsg);



        btnCasa = findViewById(R.id.BtnCasa);

        btnCasa.setOnClickListener(v -> {

            Intent volver = new Intent(
                    ConsultarFolio.this,
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
    public void EjecutarConsulta(View view){
        InformacionFolio();

    }

    public void InformacionFolio() {

        try {
            Statement ejecutor = conexionBD().createStatement();
            ResultSet rst = ejecutor.executeQuery("SELECT * FROM FrutaComercial WHERE Folio = '" + ConsultarFolio.getText().toString() + "' ");

            if (rst.next() == false) {

                Toast.makeText(getApplicationContext(), "Folio no Existe", Toast.LENGTH_LONG).show();
                ConsultarFolio.setText("");
                Limpiar();

            } else {

                Statement con = conexionBD().createStatement();
                ResultSet rs = con.executeQuery("SELECT Productor, Fecha, Variedad, Kilos,Cajas, Bins, Estado, Categoria, Csg FROM FrutaComercial WHERE Folio = '" + ConsultarFolio.getText().toString() + "' ");

                if (rs.next()) {
                    ConsultarProductor.setText(rs.getString(1));
                    ConsultarFecha.setText(rs.getString(2));
                    ConsultarVariedad.setText(rs.getString(3));
                    ConsultarKilos.setText(rs.getString(4));
                    ConsultarCajas.setText(rs.getString(5));
                    ConsultarBins.setText(rs.getString(6));
                    ConsultarEstado.setText(rs.getString(7));
                    ConsultarCategoria.setText(rs.getString(8));
                    ConsultarCsg.setText(rs.getString(9));



                }

                ConsultarFolio.setText("");

            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }
    public void Limpiar(){
        ConsultarProductor.setText("");
        ConsultarFolio.setText("");
        ConsultarFecha.setText("");
        ConsultarVariedad.setText("");
        ConsultarKilos.setText("");
        ConsultarCajas.setText("");
        ConsultarBins.setText("");
        ConsultarEstado.setText("");
        ConsultarCategoria.setText("");
        ConsultarCsg.setText("");

    }
}