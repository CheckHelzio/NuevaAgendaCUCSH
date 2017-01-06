package ccv.checkhelzio.nuevaagendacucsh.ui;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ccv.checkhelzio.nuevaagendacucsh.R;
import ccv.checkhelzio.nuevaagendacucsh.transitions.ChangeBoundBackground2;
import ccv.checkhelzio.nuevaagendacucsh.transitions.FabTransition;

public class DialogInfoEventosHelzio extends AppCompatActivity {

    @BindView(R.id.fondo)
    RelativeLayout fondo;
    @BindView(R.id.titulo)
    TextView tv_titulo;
    @BindView(R.id.tv_auditorios)
    TextView tv_auditorios;
    @BindView(R.id.tv_tipo_actividad)
    TextView tv_tipoActividad;
    @BindView(R.id.tv_fecha)
    TextView tv_fecha;
    @BindView(R.id.tv_nombre_organizador)
    TextView tv_organizador;
    @BindView(R.id.tv_num_tel)
    TextView tv_num_tel;
    @BindView(R.id.tv_notas)
    TextView tv_nota;
    @BindView(R.id.tv_id)
    TextView tv_id;
    @BindView(R.id.tv_horario)
    TextView tv_horario;
    @BindView(R.id.marca_agua)
    TextView tv_marca_agua;
    @BindView(R.id.et_pin)
    EditText et_pin;
    @BindView(R.id.reveal)
    View reveal;
    @BindView(R.id.iv_delete)
    ImageView iv_delete;
    @BindView(R.id.snackposs)
    CoordinatorLayout snackposs;
    @BindView(R.id.fab_edit)
    ImageButton fab_edit;

    private final String auditorio1 = "Auditorio Salvador Allende";
    private final String auditorio2 = "Auditorio Silvano Barba";
    private final String auditorio3 = "Auditorio Carlos Ramírez";
    private final String auditorio4 = "Auditorio Adalberto Navarro";
    private final String auditorio5 = "Sala de Juicios Orales Mariano Otero";
    private Boolean pin_correcto_eliminar = false;
    private String st_quien = "";
    private String alerta = "";
    private Eventos evento;
    private String st_evento_para_guardar = "";
    private String st_eventos_guardados = "";
    private String datos = "";
    private Handler handler;
    private boolean registroCorrecto = false;


    private boolean wifiConnected = false;
    private boolean mobileConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_info_evento);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);
        postponeEnterTransition();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        evento = getIntent().getParcelableExtra("EVENTO");
        tv_titulo.setText(evento.getTitulo());
        tv_auditorios.setText(nombreAuditorio(evento.getAuditorio()));
        tv_tipoActividad.setText(evento.getTipoEvento());
        tv_fecha.setText(fecha(evento.getFecha()));
        tv_horario.setText(horasATetxto(Integer.parseInt(evento.getHoraInicial().replaceAll("[^0-9]+",""))) +" - " + horasATetxto(Integer.parseInt(evento.getHoraFinal().replaceAll("[^0-9]+",""))));
        tv_organizador.setText(evento.getNombreOrganizador());
        tv_num_tel.setText(evento.getNumTelOrganizador().equals("") ? "Sin número telefónico" : evento.getNumTelOrganizador());
        tv_nota.setText(evento.getNotas());
        tv_id.setText(evento.getId());
        tv_marca_agua.setText(status(evento.getStatusEvento()) + " por " + evento.getQuienR() + "  -  " + evento.getCuandoR().split("~")[0] + " a las " + evento.getCuandoR().split("~")[1]);
        fondo.setBackgroundColor(evento.getFondo());
        handler = new Handler();

        et_pin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                switch (et_pin.getText().toString()) {
                    case "1308":
                        pin_correcto_eliminar = true;
                        et_pin.setTextColor(Color.WHITE);
                        st_quien = "Susy";
                        break;
                    case "2886":
                        pin_correcto_eliminar = true;
                        et_pin.setTextColor(Color.WHITE);
                        st_quien = "Tere";
                        break;
                    case "4343":
                        pin_correcto_eliminar = true;
                        et_pin.setTextColor(Color.WHITE);
                        st_quien = "CTA";
                        break;
                    default:
                        pin_correcto_eliminar = false;
                        et_pin.setTextColor(Color.RED);
                        st_quien = "";
                        break;
                }
            }
        });

        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pin_correcto_eliminar) {
                    if (Eliminar(evento.getFecha(), evento.getHoraInicial(), evento.getHoraFinal())) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DialogInfoEventosHelzio.this);
                        alertDialogBuilder.setMessage("\n" + (alerta.equals("") ? "¿Deseas eliminar este evento?" : alerta));
                        alertDialogBuilder.setPositiveButton("ACEPTAR",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        checkNetworkConnection();
                                    }
                                });
                        alertDialogBuilder.setNegativeButton("CANCELAR",
                                null);

                        alertDialogBuilder.create().show();

                        Log.v("ELIMINAR", "ELIMINAR LISTO... ESPERANDO = TRUE");
                        Principal.esperar = true;
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DialogInfoEventosHelzio.this);
                        alertDialogBuilder.setMessage("\n" + (alerta.equals("") ? "Este evento ya ha finalizado, no se puede eliminar" : alerta));
                        alertDialogBuilder.setPositiveButton("ACEPTAR", null);

                        alertDialogBuilder.create().show();
                    }
                } else {
                    Snackbar snack = Snackbar.make(snackposs, "La contraseña que ingresaste es incorrecta", Snackbar.LENGTH_LONG);
                    snack.show();
                }
            }
        });

        fondo.postDelayed(new Runnable() {
                              @Override
                              public void run() {
                                  final Rect endBounds = new Rect(fondo.getLeft(), fondo.getTop(), fondo.getRight(), fondo.getBottom());
                                  ChangeBoundBackground2.setup(DialogInfoEventosHelzio.this, fondo, true, endBounds, getViewBitmap(fondo));
                                  getWindow().getSharedElementEnterTransition();
                                  startPostponedEnterTransition();
                              }
                          }

                , 30);
    }

    private String status(String statusEvento) {
        switch (statusEvento){
            case "R":
                statusEvento = "Registrado";
                break;
            case "E":
                statusEvento = "Editado";
                break;
            case "X":
                statusEvento = "Eliminado";
                break;
        }
        return statusEvento;
    }

    private boolean Eliminar(String fecha, String hora_inicial, String hora_final) {
        Boolean eliminar = false;
        //CALENDARIO CON LA FECHA DE HOY
        Calendar c = Calendar.getInstance();

        //CALENDARIO CON LA FECHA FINAL Y HORA FINAL DEL EVENTO
        Calendar e = Calendar.getInstance();
        e.set(2016, 0, 1);
        e.set(Calendar.DAY_OF_YEAR, Integer.parseInt(fecha));
        horasaCalendario(e, hora_final);

        //SI LA FECHA FINAL Y HORA FINAL DEL EVENTO EN ANTERIOR A LA FECHA DE HOY EL EVENTO NO SE PUEDE ELIMINAR PORQUE YA TERMINO
        if (e.getTimeInMillis() < c.getTimeInMillis()) {
            eliminar = false;
        } else if (e.getTimeInMillis() > c.getTimeInMillis()) {
            //LA HORA FINAL ES MAYOR, HAY QUE CHECAR LA INICIAL PARA SABER SI EL EVENTO ESTA AHORITA O NO
            horasaCalendario(e, hora_inicial);

            //SI LA HORA INICIAL ES MAYOR TAMBIEN QUIERE DECIR QUE EL EVENTO AUN NO COMIENZA
            if (e.getTimeInMillis() > c.getTimeInMillis()) {
                eliminar = true;
            } else {
                //SI LA HORA INICIAL ES MENOR ENTONCES EL EVENTO YA COMENZO
                eliminar = false;
                alerta = "Una sesión de este evento esta ocurriendo ahora mismo, no se puede eliminar";
            }
        }
        return eliminar;
    }

    private void horasaCalendario(Calendar e, String hora_inicial) {

        int hora = (Integer.parseInt(hora_inicial) / 2) + 7;
        e.set(Calendar.HOUR_OF_DAY, hora);

        if (Integer.parseInt(hora_inicial) % 2 != 0) {
            e.set(Calendar.MINUTE, 30);
        }
    }

    @OnClick(R.id.fab_edit)
    public void EditarEnvento() {
        if (Eliminar(evento.getFecha(), evento.getHoraInicial(), evento.getHoraFinal())) {
            Intent intent = new Intent(this, DialogInfoEventosHelzio.class);
            intent.putExtra("EVENTO", evento);
            FabTransition.setCustomDuration(500);
            FabTransition.addExtras(intent, (Integer) getAcentColor(), R.drawable.ic_edit);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, fab_edit,
                    getString(R.string.transition_date_dialog_helzio));
            startActivity(intent, options.toBundle());
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DialogInfoEventosHelzio.this);
            alertDialogBuilder.setMessage("\n" + (alerta.equals("") ? "Este evento ya ha finalizado, no se puede editar" : alerta.replace("eliminar", "editar")));
            alertDialogBuilder.setPositiveButton("ACEPTAR", null);

            alertDialogBuilder.create().show();
        }
    }

    public Object getAcentColor() {
        int colorAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorAccent;
        } else {
            //Get colorAccent defined for AppCompat
            colorAttr = getResources().getIdentifier("colorAccent", "attr", getPackageName());
        }
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }

    private String fecha(String fecha_inicial) {
        Calendar fecha = Calendar.getInstance();
        fecha.set(2016, 0, 1);
        fecha.add(Calendar.DAY_OF_YEAR, Integer.valueOf(fecha_inicial.replaceAll("[^0-9]+","")) - 1);
        SimpleDateFormat format = new SimpleDateFormat("cccc',' d 'de' MMMM 'del' yyyy");
        fecha_inicial = (format.format(fecha.getTime()));
        return fecha_inicial;
    }

    private String nombreAuditorio(String numero) {
        String st = "";
        switch (numero) {
            case "1":
                st = auditorio1;
                break;
            case "2":
                st = auditorio2;
                break;
            case "3":
                st = auditorio3;
                break;
            case "4":
                st = auditorio4;
                break;
            case "5":
                st = auditorio5;
                break;
        }
        return st;
    }

    @Override
    public void onBackPressed() {
        dismiss(null);
    }

    public void dismiss(View view) {
        finishAfterTransition();
    }

    private Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    class EliminarEventoBaseDatos extends AsyncTask<String, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Calendar calendarioRegistro = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("d/MM/yyyy'~'h:mm a");

            Intent inte = getIntent();

            Eventos nuevoEvento = evento;
            nuevoEvento.setStatusEvento("X");
            nuevoEvento.setQuienR(st_quien);
            nuevoEvento.setCuandoR(format.format(calendarioRegistro.getTime()));

            int i = 0;
            for (Eventos eev : Principal.lista_eventos) {
                if (eev.getTag().equals(evento.getTag())) {
                    Principal.lista_eventos.set(i, nuevoEvento);
                }
                i++;
            }

            Collections.sort(Principal.lista_eventos, new Comparator<Eventos>() {
                @Override
                public int compare(Eventos e1, Eventos e2) {
                    Integer i1 = Integer.parseInt(e1.getFecha().replaceAll("[^0-9]+", ""));
                    Integer i2 = Integer.parseInt(e2.getFecha().replaceAll("[^0-9]+", ""));
                    if (i1 == i2) {
                        Integer i3 = Integer.parseInt(e1.getHoraInicial());
                        Integer i4 = Integer.parseInt(e2.getHoraInicial());
                        if (i3 == i4) {
                            Integer i5 = Integer.parseInt(e1.getHoraFinal());
                            Integer i6 = Integer.parseInt(e2.getHoraFinal());
                            return i5.compareTo(i6);
                        } else {
                            return i3.compareTo(i4);
                        }
                    } else {
                        return i1.compareTo(i2);
                    }
                }
            });

            st_eventos_guardados = "";
            for (Eventos item : Principal.lista_eventos) {
                st_eventos_guardados += item.aTag() + "¦";
            }
            datos = "";
            registroCorrecto = false;
        }

        @Override
        protected Void doInBackground(String... aa12) {
            if (st_eventos_guardados.length() > 333) {
                try {
                    URL url = new URL("http://148.202.6.72/aplicacion/datos2.php");
                    HttpURLConnection aaaaa = (HttpURLConnection) url.openConnection();
                    aaaaa.setReadTimeout(0);
                    aaaaa.setConnectTimeout(0);
                    aaaaa.setRequestMethod("POST");
                    aaaaa.setDoInput(true);
                    aaaaa.setDoOutput(true);

                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("comentarios", st_eventos_guardados);
                    String query = builder.build().getEncodedQuery();

                    OutputStream os = aaaaa.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(query);
                    writer.flush();
                    writer.close();
                    os.close();

                    aaaaa.connect();

                    int aaaaaaa = aaaaa.getResponseCode();
                    if (aaaaaaa == HttpsURLConnection.HTTP_OK) {
                        registroCorrecto = true;
                        String aaaaaaaa;
                        BufferedReader br = new BufferedReader(new InputStreamReader(aaaaa.getInputStream(), "UTF-8"));
                        while ((aaaaaaaa = br.readLine()) != null) {
                            datos += aaaaaaaa;
                        }
                    } else {
                        datos = "error code: " + aaaaaaa;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (datos.contains("error code: ") || !registroCorrecto) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new EliminarEventoBaseDatos().execute();
                    }
                }, 1000);
            } else {
                //Toast.makeText(DialogInfoEventosHelzio.this, "El evento con el ID " + evento.getId() + " ha sido eliminado", Toast.LENGTH_LONG).show();
                SharedPreferences prefs = getSharedPreferences("EVENTOS CUCSH", Context.MODE_PRIVATE);
                prefs.edit().putString("EVENTOS GUARDADOS", st_eventos_guardados).apply();
                Intent i = getIntent();
                i.putExtra("POSITION", i.getIntExtra("POSITION", 0));
                setResult(RESULT_OK, i);
                finishAfterTransition();
            }
        }
    }

    private String horasATetxto(int numero) {
        String am_pm, st_min, st_hora;

        int hora = (numero / 2) + 7;
        if (hora > 12) {
            hora = hora - 12;
            am_pm = " PM";
        } else {
            am_pm = " AM";
        }

        if (hora < 10) {
            st_hora = "0" + hora;
        } else {
            st_hora = "" + hora;
        }

        if (numero % 2 == 0) {
            st_min = "00";
        } else {
            st_min = "30";
        }

        return st_hora + ":" + st_min + am_pm;

    }

    private void checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            if (wifiConnected) {
                new EliminarEventoBaseDatos().execute();
            } else if (mobileConnected) {
                new EliminarEventoBaseDatos().execute();
            }
        } else {
            Snackbar.make(snackposs, "Hay un problema con la conexión a la base de datos. Verifica tu conexión a internet.", Snackbar.LENGTH_LONG).show();
        }
    }
}
