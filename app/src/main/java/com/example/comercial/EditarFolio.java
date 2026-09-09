package com.example.comercial;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditarFolio extends AppCompatActivity {

    EditText Folio, Fecha, Kilos, Cajas, Csg,Estado;
    Spinner Spn, SpnCategoria, Productor, Turno , Variedad, Especie;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_folio);

        Folio = findViewById(R.id.TxtFolio);
        Productor = findViewById(R.id.SpnExport);
        Fecha = findViewById(R.id.TxtFecha);
        Kilos = findViewById(R.id.TxtKilos);
        Cajas = findViewById(R.id.TxtCajas);
        Csg = findViewById(R.id.TxtCsg);
        Spn = findViewById(R.id.SpnBins);
        SpnCategoria = findViewById(R.id.spinCategoria);
        Turno= findViewById(R.id.spTurno);
        Variedad = findViewById(R.id.SpnVariedad);
        Especie = findViewById(R.id.SpnEspecie);
        Estado = findViewById(R.id.TxtEstado);

        Fecha.setOnClickListener(v -> seleccionarFecha());

        String[] turno = {"Selecciona","Turno 1", "Turno 2"};

        SpinnerTextoGrande adapter3 = new SpinnerTextoGrande(this, Arrays.asList(turno));
        Turno.setAdapter(adapter3);

        String[] SiNo = {"Selecciona","Cajas", "Totem","Bins"};

        SpinnerTextoGrande adapter = new SpinnerTextoGrande(this, Arrays.asList(SiNo));
        Spn.setAdapter(adapter);

        String[] Categoria = {"Selecciona","Comercial", "Pre Calibre","Desecho","Desecho Basura"};

        SpinnerTextoGrande adapter1 = new SpinnerTextoGrande(this, Arrays.asList(Categoria));
        SpnCategoria.setAdapter(adapter1);

        new Thread(() -> {
            ArrayList<String> exportadoras = obtenerExportadoras();

            runOnUiThread(() -> {SpinnerTextoGrande Exportadora = new SpinnerTextoGrande(this, exportadoras);

                Productor.setAdapter(Exportadora);
                Productor.setSelection(0);
            });
        }).start();

        String[] especies = {"Selecciona", "Cerezas", "Ciruelas"};

        SpinnerTextoGrande adapter4 = new SpinnerTextoGrande(this, Arrays.asList(especies));
        Especie.setAdapter(adapter4);

        String[] variedadesCereza = {"Lapins", "Santina", "Regina", "Bing","Royal Dawn","Kordia","Sweet Heart","Garnet","Sam","Stella","Rainier","Royal Tioga","Royal Lynn","Royal Hazel"
                ,"Somer Set","Coral","Skeena","Friar","Superior","Van","Sweet Aryana","Sylvia","Summit","Sunburst","Reina Luisa","Newstar","Nimba","Pacific Red"};
        String[] variedadesCiruela = {"D’agen", "Larry Ann", "Friar", "Black Diamond"};


        ImageButton BtnCasa;

        BtnCasa = findViewById(R.id.BtnCasa);

        BtnCasa.setOnClickListener(v -> {

            Intent volver = new Intent(
                    EditarFolio.this,
                    Inicio.class
            );

            startActivity(volver);
            finish();

        });
        // Listener para cambiar variedades según especie
        // Listener para cambiar variedades según especie
        Especie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                String especieSeleccionada = especies[position];
                List<String> listaVariedades;

                switch (especieSeleccionada) {
                    case "Cerezas":
                        listaVariedades = Arrays.asList(variedadesCereza);
                        break;

                    case "Ciruelas":
                        listaVariedades = Arrays.asList(variedadesCiruela);
                        break;

                    default:
                        listaVariedades = Arrays.asList(
                                "Selecciona especie primero"
                        );
                        break;
                }

                SpinnerTextoGrande adapterVariedad =
                        new SpinnerTextoGrande(
                                EditarFolio.this,
                                listaVariedades
                        );

                Variedad.setAdapter(adapterVariedad);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private ArrayList<String> obtenerExportadoras() {
        ArrayList<String> lista = new ArrayList<>();
        Connection conexion = conexionBD();

        if (conexion != null) {
            try {
                String sql = "SELECT Nombre FROM Exportadoras ORDER BY Nombre";
                Statement statement = conexion.createStatement();
                ResultSet resultado = statement.executeQuery(sql);

                while (resultado.next()) {
                    lista.add(resultado.getString("Nombre"));
                }

                resultado.close();
                statement.close();
                conexion.close();

            } catch (Exception e) {
                Log.e("SQL_SERVER", "Error al cargar exportadoras", e);
            }
        }

        return lista;
    }
    // ============================================================
// SELECCIONAR FECHA
// ============================================================
// Al presionar TxtFecha se abre un calendario.
// El usuario selecciona la fecha y presiona Aceptar.
// ============================================================

    private void seleccionarFecha() {

        // Obtenemos la fecha actual
        Calendar calendario = Calendar.getInstance();

        // Creamos el calendario de Android
        DatePickerDialog datePicker = new DatePickerDialog(
                this,

                // Cuando el usuario selecciona una fecha
                (view, year, month, dayOfMonth) -> {

                    // Creamos un Calendar con la fecha seleccionada
                    Calendar fechaSeleccionada = Calendar.getInstance();

                    fechaSeleccionada.set(
                            year,
                            month,
                            dayOfMonth
                    );

                    // Formato que utilizaremos en la aplicación
                    SimpleDateFormat formato =
                            new SimpleDateFormat(
                                    "dd/MM/yyyy",
                                    Locale.getDefault()
                            );

                    // Mostramos la fecha seleccionada
                    Fecha.setText(
                            formato.format(
                                    fechaSeleccionada.getTime()
                            )
                    );
                },

                // Año actual
                calendario.get(Calendar.YEAR),

                // Mes actual
                calendario.get(Calendar.MONTH),

                // Día actual
                calendario.get(Calendar.DAY_OF_MONTH)
        );

        // Mostramos el calendario
        datePicker.show();
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
    public void consultar(View view){
        ConsultarFolio();
    }
    public void ConsultarFolio() {

        try {
            Statement ejecutor = conexionBD().createStatement();
            ResultSet rst = ejecutor.executeQuery("SELECT * FROM FrutaComercial WHERE Folio = '" + Folio.getText().toString() + "' and EstadoTurno = 'Turno Abierto' ");

            if (rst.next() == false) {

                Toast.makeText(getApplicationContext(), "Folio no Existe o Turno esta Cerrado", Toast.LENGTH_LONG).show();
                Folio.setText("");
                Limpiar();

            } else {

                Statement con = conexionBD().createStatement();
                ResultSet rs = con.executeQuery("SELECT Fecha, Kilos,Cajas, Estado,Csg FROM FrutaComercial WHERE Folio = '" + Folio.getText().toString() + "' ");

                if (rs.next()) {
                    Fecha.setText(rs.getString(1));
                    Kilos.setText(rs.getString(2));
                    Cajas.setText(rs.getString(3));
                    Estado.setText(rs.getString(4));
                    Csg.setText(rs.getString(5));



                }


            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }
    public void Actualizar(View view){
        actualizarRegistroPorFolio();
    }

    public void actualizarRegistroPorFolio() {

        if (Folio.getText().toString().isEmpty() || Fecha.getText().toString().isEmpty() ||
                Kilos.getText().toString().isEmpty() || Cajas.getText().toString().isEmpty() || Spn.getSelectedItem().toString().isEmpty() || Productor.getSelectedItem().toString().isEmpty()
                || Variedad.getSelectedItem().toString().isEmpty() || SpnCategoria.getSelectedItem().toString().isEmpty()
                || Turno.getSelectedItem().toString().isEmpty() || Especie.getSelectedItem().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "No debe haber campos vacios", Toast.LENGTH_LONG).show();
        } else {

            try {
                Statement ejecutor = conexionBD().createStatement();
                ResultSet rst = ejecutor.executeQuery("SELECT * FROM FrutaComercial WHERE Folio = '" + Folio.getText().toString() + "'  ");

                if (rst.next() == false) {

                    Toast.makeText(getApplicationContext(), "Folio no Existe", Toast.LENGTH_LONG).show();
                    Folio.setText("");
                    Limpiar();

                } else {
                    Connection conn = conexionBD();
                    if (conn != null) {
                        String query = "UPDATE FrutaComercial SET " +
                                "Productor = ?, Fecha = ?, Kilos = ?, Variedad = ?, " +
                                "Cajas = ?, Bins = ?, Estado = ?, categoria = ?, " +
                                "Csg = ?, Turno = ?, Especie = ? " +
                                "WHERE Folio = ?";
                        ;
                        PreparedStatement stmt = conn.prepareStatement(query);

                        stmt.setString(1, Productor.getSelectedItem().toString());
                        stmt.setString(2, Fecha.getText().toString());
                        stmt.setString(3, Kilos.getText().toString());
                        stmt.setString(4, Variedad.getSelectedItem().toString());
                        stmt.setString(5, Cajas.getText().toString());
                        stmt.setString(6, Spn.getSelectedItem().toString());
                        stmt.setString(7, Estado.getText().toString());
                        stmt.setString(8, SpnCategoria.getSelectedItem().toString());
                        stmt.setString(9, Csg.getText().toString());
                        stmt.setString(10, Turno.getSelectedItem().toString());
                        stmt.setString(11, Especie.getSelectedItem().toString());
                        stmt.setString(12, Folio.getText().toString());
                        int filas = stmt.executeUpdate();

                        if (filas > 0) {
                            Toast.makeText(this, "Registro actualizado correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "No se encontró el Registro", Toast.LENGTH_SHORT).show();
                        }

                        stmt.close();
                        conn.close();
                        Limpiar();
                    } else {
                        Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }
    public void Limpiar(){
        Folio.setText("");
        Fecha.setText("");
        Kilos.setText("");
        Cajas.setText("");
        Estado.setText("");
        Csg.setText("");

    }

}