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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IngresoFruta extends AppCompatActivity {

    EditText Folio, Fecha, Kilos, Cajas, Csg;
    Spinner Spn, SpnCategoria, Productor, Turno , Variedad, Especie;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ingreso_fruta);

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

        obtenerFolio();
        Fecha.setOnClickListener(v -> seleccionarFecha());

        String[] turno = {"Selecciona Turno","Turno 1", "Turno 2"};

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

            exportadoras.add(0, "Seleccione una exportadora");

            runOnUiThread(() -> {
                SpinnerTextoGrande adapterExportadora =
                        new SpinnerTextoGrande(this, exportadoras);

                Productor.setAdapter(adapterExportadora);
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
                    IngresoFruta.this,
                    Inicio.class
            );

            startActivity(volver);
            finish();

        });

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
                                IngresoFruta.this,
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
// ==============================================
// OBTENER EL SIGUIENTE FOLIO AUTOMÁTICO
// ==============================================

    public void obtenerFolio() {
        try {
            Statement st = conexionBD().createStatement();

            ResultSet rs = st.executeQuery(
                    "SELECT ISNULL(MAX(Folio), 200000) + 1 AS NuevoFolio " +
                            "FROM FrutaComercial"
            );

            if (rs.next()) {
                Folio.setText(rs.getString("NuevoFolio"));
            }

            rs.close();
            st.close();

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Error al obtener Folio",
                    Toast.LENGTH_LONG
            ).show();
        }
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
    public void Registrar(View view) throws Exception {
        AgregarPallets();
    }
    public void Turno(View view){
        actualizarTurno();
    }
    public void AgregarPallets() throws Exception {


        if (Folio.getText().toString().isEmpty() || Fecha.getText().toString().isEmpty() ||
             Kilos.getText().toString().isEmpty() || Cajas.getText().toString().isEmpty() || Spn.getSelectedItem().toString().isEmpty()|| Productor.getSelectedItem().toString().isEmpty()
                || Variedad.getSelectedItem().toString().isEmpty() || SpnCategoria.getSelectedItem().toString().isEmpty()
                || Turno.getSelectedItem().toString().isEmpty() || Especie.getSelectedItem().toString().isEmpty() ) {
            Toast.makeText(getApplicationContext(), "No debe haber campos vacios", Toast.LENGTH_LONG).show();
        } else {

            try {
                Statement ejecutor = conexionBD().createStatement();
                ResultSet rs = ejecutor.executeQuery("SELECT * FROM FrutaComercial WHERE Folio = '" + Folio.getText().toString() + "' ");

                if (rs.next() == true) {

                    Toast.makeText(getApplicationContext(), "Folio Existe", Toast.LENGTH_LONG).show();
                    Folio.setText("");
                } else {
                    PreparedStatement ps = conexionBD().prepareStatement("insert into FrutaComercial values (?,?,?,?,?,?,?,?,?,?,?,?,?)");
                    ps.setString(1, Folio.getText().toString());
                    ps.setString(2, Productor.getSelectedItem().toString());
                    ps.setString(3, Fecha.getText().toString());
                    ps.setString(5, Variedad.getSelectedItem().toString());
                    ps.setString(4, Kilos.getText().toString());
                    ps.setString(6, Cajas.getText().toString());
                    ps.setString(7, Spn.getSelectedItem().toString());
                    ps.setString(8, "Existencia");
                    ps.setString(9, SpnCategoria.getSelectedItem().toString());
                    ps.setString(10,Csg.getText().toString());
                    ps.setString(11, Turno.getSelectedItem().toString());
                    ps.setString(12, "Turno Abierto");
                    ps.setString(13, Especie.getSelectedItem().toString());

                    ps.executeUpdate();
                    Toast.makeText(getApplicationContext(),"Guardado", Toast.LENGTH_LONG).show();
                    limpiar();
                    obtenerFolio();


                }
            }
            catch(Exception e){
                Toast.makeText(IngresoFruta.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }


    }
    public void limpiar(){
        Folio.setText("");
        Fecha.setText("");
        Kilos.setText("");
        Cajas.setText("");
        Csg.setText("");

    }
    private void actualizarTurno() {
        try {
            Connection conn = conexionBD();
            if (conn != null) {
                String query = "UPDATE FrutaComercial SET EstadoTurno = 'Turno Cerrado' WHERE EstadoTurno= 'Turno Abierto'";
                PreparedStatement stmt = conn.prepareStatement(query);


                int filas = stmt.executeUpdate();

                if (filas > 0) {
                    Toast.makeText(this, "Turno actualizado correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No se encontró el turno", Toast.LENGTH_SHORT).show();
                }

                stmt.close();
                conn.close();
            } else {
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
