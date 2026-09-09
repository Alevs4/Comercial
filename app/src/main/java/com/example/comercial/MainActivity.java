package com.example.comercial;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity {

    EditText Usu, Pass;
    Button Ingreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Usu = findViewById(R.id.TxtUsuario);
        Pass = findViewById(R.id.TxtPass);
        Ingreso = findViewById(R.id.BtnIngreso);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void Ingreso(View view){
        LoginFrio();
    }
    public Connection conexionBD(){
        Connection conexion = null;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            conexion = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.201.76;databaseName=Fruta;user=admin;password=1234;");

        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return conexion;
    }
    public Integer LoginFrio(){
        Integer resultado = 0;

        try {

            // Realizamos la consulta a SQL Server
            Statement ejecutor = conexionBD().createStatement();

            ResultSet rs = ejecutor.executeQuery(
                    "SELECT * FROM Usuarios " +
                            "WHERE usuario = '" + Usu.getText().toString() + "' " +
                            "AND clave = '" + Pass.getText().toString() + "'"
            );


            // Si encontramos el usuario
            if (rs.next()) {

                // =====================================================
                // OBTENEMOS LOS DATOS DESDE SQL SERVER
                // =====================================================

                int idUsuario = rs.getInt("id_usu");

                String usuario = rs.getString("usuario");

                String perfil = rs.getString("perfil");


                // =====================================================
                // GUARDAMOS LOS DATOS EN LA SESIÓN
                // =====================================================

                SesionManager session = new SesionManager(this);

                session.guardarUsuario(
                        idUsuario,
                        usuario,
                        perfil
                );


                // =====================================================
                // MENSAJE DE BIENVENIDA
                // =====================================================

                Toast.makeText(
                        getApplicationContext(),
                        "Bienvenido " + usuario,
                        Toast.LENGTH_LONG
                ).show();


                resultado = 1;


                // =====================================================
                // ABRIMOS LA PANTALLA DE INICIO
                // =====================================================

                Intent siguiente = new Intent(
                        this,
                        Inicio.class
                );

                startActivity(siguiente);


            } else {

                // Usuario o contraseña incorrectos
                Toast.makeText(
                        getApplicationContext(),
                        "Problemas Con el Usuario o Contraseña",
                        Toast.LENGTH_LONG
                ).show();

                resultado = 0;
            }

        } catch (Exception e) {

            // Mostramos el error de base de datos
            Toast.makeText(
                    getApplicationContext(),
                    "Error de base de Datos: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }

        return resultado;
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==event.KEYCODE_BACK){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setMessage("¿Desea Salir de la App?").setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent=new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }
        return super.onKeyDown(keyCode, event);
    }
}