package ccv.checkhelzio.nuevaagendacucsh.ui;

import android.animation.Animator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ccv.checkhelzio.nuevaagendacucsh.R;
import ccv.checkhelzio.nuevaagendacucsh.transitions.FabTransition;

public class EditarEvento extends AppCompatActivity {

    @BindView(R.id.fondo) LinearLayout fondo;
    @BindView(R.id.color_reveal) View color_reveal;
    @BindView(R.id.atv_tituto_evento) AutoCompleteTextView atv_titulo_evento;
    @BindView(R.id.atv_tipo_evento) AutoCompleteTextView atv_tipo_evento;
    @BindView(R.id.atv_nombre_org) AutoCompleteTextView atv_nombre_org;
    @BindView(R.id.toolbar_dialog) RelativeLayout full_header;
    @BindView(R.id.sp_auditorios) Spinner sp_auditorios;
    @BindView(R.id.sp_hora_inicial) Spinner sp_hora_inicial;
    @BindView(R.id.sp_hora_final) Spinner sp_hora_final;
    @BindView(R.id.tv_repeticion) TextView tv_repeticion;
    @BindView(R.id.tv_titulo_label) TextView tv_titulo_label;
    @BindView(R.id.tv_hora_inicial_label) TextView tv_hora_inicial_label;
    @BindView(R.id.tv_tipo_evento_label) TextView tv_tipo_evento_label;
    @BindView(R.id.tv_hora_final_label) TextView tv_hora_final_label;
    @BindView(R.id.tv_nom_org_label) TextView tv_nom_org_label;
    @BindView(R.id.tv_contraseña_label) TextView tv_contraseña_label;
    @BindView(R.id.et_numero_tel) EditText et_num_tel;
    @BindView(R.id.et_contraseña) EditText et_contraseña;
    @BindView(R.id.et_nota) EditText et_nota;
    @BindView(R.id.snackposs)
    CoordinatorLayout snackposs;

    private Eventos evento;
    private Handler handler;
    private boolean cerrar = false;
    private boolean pin_correcto_eliminar = false;

    private final String auditorio1 = "Auditorio Salvador Allende";
    private final String auditorio2 = "Auditorio Silvano Barba";
    private final String auditorio3 = "Auditorio Carlos Ramírez";
    private final String auditorio4 = "Auditorio Adalberto Navarro";
    private final String auditorio5 = "Sala de Juicios Orales Mariano Otero";
    private String st_quien;
    private String data;
    private boolean registroCorrecto;
    private String st_eventos_guardados;
    private String stComprobarEv;
    private String AD;
    private int int_fecha_inicial;
    private int int_fecha_final;
    private int int_hora_inicial;
    private int int_hora_final;
    private boolean hora_correcta;
    private Snackbar snackHora;
    private String tagHora = "";
    private final static int INICIAL = 333;
    private final static int FINAL = 334;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //PONEMOS LAYOT QUE VAMOS A USAR EN ESTA ACTIVITY
        setContentView(R.layout.dialog_editar_evento);

        //OCULTAR EL TECLADO PARA QUE NO SE ABRA AL INICIAR LA ACTIVITY
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //INICIAR EL PLUGIN QUE PERMITE HACER LAS DECLARACIONES MAS RAPIDO
        ButterKnife.bind(this);

        //PASAMOS EL VIEW PARA LA ANIMACION Y COMPLETAMOS LOS AJUSTES PARA LA TRANSICION
        FabTransition.setup(this, fondo);
        getWindow().getSharedElementEnterTransition();

        //INICIAMOS TODOS LOS VIEWS QUE VAMOS A UTILIZAR
        iniciarObjetos();
        llenarDatos();
        stringComprobarCupos();

    }

    private void iniciarObjetos() {
        // CREAMOS UN HANDLER PARA TAREAS CON TIEMPO DE RETRASO
        handler = new Handler();
    }

    private void llenarDatos() {
        //RECUPERAMOS EL EVENTO QUE PASAMOS DESDE LA ACTIVITY ANTERIOR EL CUAL CONTIENE TODOS LOS DATOS DEL EVENTO
        evento = (Eventos) getIntent().getSerializableExtra("EVENTO");

        //TITULO DEL EVENTO
        atv_titulo_evento.setText(evento.getTitulo());
        atv_titulo_evento.setSelection(atv_titulo_evento.getText().length());
        atv_titulo_evento.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll("::", "").replace("¦", "");
                if (!s.toString().equals(result)) {
                    atv_titulo_evento.setText(result);
                    atv_titulo_evento.setSelection(result.length());
                }

                if (atv_titulo_evento.getText().toString().trim().length() == 0) {
                    tv_titulo_label.setTextColor(Color.RED);
                }else {
                    tv_titulo_label.setTextColor(Color.WHITE);
                }
            }
        });

        // DESPUES DE PONER EL TITULO INICIAMOS EL AUTOCOMPLETAR PARA EL NOMBRE DEL EVENTO PARA QUE NO SALGA LA LISTA DE EVENTOS
        ArrayAdapter<String> nombresEAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Principal.titulos.split("¦"));
        atv_titulo_evento.setAdapter(nombresEAdapter);

        //FONDO DEL EVENTO
        full_header.setBackgroundColor(evento.getFondo());

        // CONFIGURAR SPINER PARA SELECCIONAR EL AUDITORIO
        String[] items = new String[]{
                auditorio1, auditorio2, auditorio3, auditorio4, auditorio5};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        sp_auditorios.setAdapter(adapter);
        sp_auditorios.setSelection(Integer.parseInt(evento.getAuditorio()) - 1);
        sp_auditorios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //CONFIGURAR EL COLOR DEL HEADER SEGUN EL AUDITORIO SELECCIOANDO
                colorReveal(fondoAuditorio((i + 1) + ""));
                AD = "" + (i + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //TIPO DE EVENTO
        atv_tipo_evento.setText(evento.getTipoEvento());
        atv_tipo_evento.setSelection(atv_tipo_evento.getText().length());

        // INICIAMOS EL AUTOCOMPLETAR DE TIPOS DE EVENTO Y COLOCAMOS EL TIPO DE EVENTO CORRESPONDIENTE
        ArrayAdapter<String> tiposEventoAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Principal.tiposDeEvento.split("¦"));
        atv_tipo_evento.setAdapter(tiposEventoAdapter);
        atv_tipo_evento.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll("::", "").replace("¦", "");
                if (!s.toString().equals(result)) {
                    atv_tipo_evento.setText(result);
                    atv_tipo_evento.setSelection(result.length());
                }

                if (atv_tipo_evento.getText().toString().trim().length() == 0) {
                    tv_tipo_evento_label.setTextColor(Color.RED);
                }else {
                    tv_tipo_evento_label.setTextColor(Color.parseColor("#121212"));
                }
            }
        });

        // COLOCAMOS LA FECHA INICIAL Y FINAL DEL EVENTO Y LAS GUARDAMOS EN VARIABLES NUMERICAS
        int_fecha_inicial = Integer.parseInt(evento.getFecha());

        // CONFIGURAR SPINER PARA SELECCIONAR LA HORA INICIAL Y FINAL DEL EVENTO
        String[] horas = new String[]{
                "07:00 AM", //0
                "07:30 AM", //1
                "08:00 AM", //2
                "08:30 AM", //3
                "09:00 AM", //4
                "09:30 AM", //5
                "10:00 AM", //6
                "10:30 AM", //7
                "11:00 AM", //8
                "11:30 AM", //9
                "12:00 PM", //10
                "12:30 PM", //11
                "01:00 PM", //12
                "01:30 PM", //13
                "02:00 PM", //14
                "02:30 PM", //15
                "03:00 PM", //16
                "03:30 PM", //17
                "04:00 PM", //18
                "04:30 PM", //19
                "05:00 PM", //20
                "05:30 PM", //21
                "06:00 PM", //22
                "06:30 PM", //23
                "07:00 PM", //24
                "07:30 PM", //25
                "08:00 PM", //26
                "08:30 PM", //27
                "09:00 PM", //28
                "09:30 PM", //29
                "10:00 PM", //30
        };

        ArrayAdapter<String> adapterHoras = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, horas);
        sp_hora_inicial.setAdapter(adapterHoras);
        sp_hora_inicial.setSelection(Integer.parseInt(evento.getHoraInicial()));

        // CUANDO CAMBIA LA HORA INICIAL CAMBIA LA HORA FINAL A UNA HORA DESPUES
        sp_hora_inicial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i < 29){
                    sp_hora_final.setSelection(i + 2);
                }else {
                    sp_hora_final.setSelection(30);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_hora_final.setAdapter(adapterHoras);
        sp_hora_final.setSelection(Integer.parseInt(evento.getHoraFinal()) + 1);


        //CONFIGUAR NOMBRE DEL ORGANIZADOR
        atv_nombre_org.setText(evento.getNombreOrganizador());
        atv_nombre_org.setSelection(atv_nombre_org.getText().length());

        atv_nombre_org.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll("::", "").replace("¦", "");
                if (!s.toString().equals(result)) {
                    atv_nombre_org.setText(result);
                    atv_nombre_org.setSelection(result.length());
                }

                if (atv_nombre_org.getText().toString().trim().length() == 0) {
                    tv_nom_org_label.setTextColor(Color.RED);
                }else {
                    tv_nom_org_label.setTextColor(Color.parseColor("#121212"));
                }
            }
        });

        //CONFIGURAR AUTOCOMPLETAR PARA EL NOMBRE DEL ORGANIZADOR
        ArrayAdapter<String> nombresOrganizadorAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Principal.nombresOrganizador.split("¦"));
        atv_nombre_org.setAdapter(nombresOrganizadorAdapter);

        //CONFIGURAR NUMERO DE TELEFONO
        et_num_tel.setText(evento.getNumTelOrganizador());

        // CONFIGURAMOS EL EDIT TEXT DE LA CONTRASEÑA
        tv_contraseña_label.setTextColor(Color.RED);
        et_contraseña.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_contraseña.getText().toString().trim().length() == 0){
                    tv_contraseña_label.setTextColor(Color.RED);
                }else {
                    tv_contraseña_label.setTextColor(Color.parseColor("#121212"));
                }

                switch (et_contraseña.getText().toString()) {
                    case "1308":
                        pin_correcto_eliminar = true;
                        et_contraseña.setTextColor(Color.parseColor("#121212"));
                        st_quien = "Susy";
                        break;
                    case "2886":
                        pin_correcto_eliminar = true;
                        et_contraseña.setTextColor(Color.parseColor("#121212"));
                        st_quien = "Tere";
                        break;
                    case "4343":
                        pin_correcto_eliminar = true;
                        et_contraseña.setTextColor(Color.parseColor("#121212"));
                        st_quien = "CTA";
                        break;
                    default:
                        pin_correcto_eliminar = false;
                        et_contraseña.setTextColor(Color.RED);
                        st_quien = "";
                        break;
                }
            }
        });
    }

    private void stringComprobarCupos() {
        if (!cerrar){
            // AQUI SACAMOS UN STRING CON LOS EVENTOS DE LOS DIAS SELECCIONADOS PARA EL EVENTO, PARA HACER EL LOOP DE COMPROBACION MAS LIGERO
            stComprobarEv = "";
            for (int x = int_fecha_inicial; x <= int_fecha_final; x++) {
                String j = "";

                try {
                    final String s = "";

                    if (s != null) {
                        for (String ev_suelto : s.split("¦")) {
                            if (!ev_suelto.contains("X~")) {
                                if (!ev_suelto.equals("")) {
                                    j += ev_suelto + "¦";
                                }
                            }
                        }
                    } else {
                        j += ("");
                    }
                } catch (Exception ignored) {
                }
                stComprobarEv += j;
            }
            stComprobarEv = stComprobarEv.replaceFirst("null", "");
            stComprobarEv = stComprobarEv.replace(evento.getTag() + "¦", "");

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stringComprobarCupos();
                }
            },200);
        }
    }

    private void loopComprobarhoras() {
        if (!cerrar){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // EL EVENTO NO PUEDE COMENZAR DESPUES DE LAS 8:30 PM
                    if (sp_hora_inicial.getSelectedItemPosition() > 27) {
                        hora_correcta = false;
                        tv_hora_inicial_label.setTextColor(Color.RED);
                        tv_hora_final_label.setTextColor(Color.RED);

                        if (!tagHora.equals("1")){
                            tagHora = "1";
                            mostrarSnack("Los eventos no pueden comenzar a después de las 8:30 PM");
                        }
                    }
                    else {
                        // SI EL STRING DE COMPROBAR CUPO TIENE EVENTOS PROCEDEMOS CON LA COMPROBACION
                        if (stComprobarEv.trim().length() > 0) {
                            // SEPARAMOS EN EVENTOS INDIVIDUALES
                            for (String z : stComprobarEv.split("¦")) {
                                // COMPROBAMOS SI CADA EVENTO SUELTO SE REALIZA EN EL AUDITORIO A COMPROBAR CUPO
                                if (z.split("::")[5].trim().equals(AD)) {

                                    // EL EVENTO COMIENZA 30 MINUTOS ANTES DE QUE COMIENCE OTRO
                                    if (sp_hora_inicial.getSelectedItemPosition() == Integer.valueOf(z.split("::")[0]) - 1) {
                                        hora_correcta = false;
                                        tv_hora_inicial_label.setTextColor(Color.RED);
                                        tv_hora_final_label.setTextColor(Color.parseColor("#121212"));

                                        if (!tagHora.equals("4")){
                                            tagHora = "4";
                                            mostrarSnack("Conflicto de horario:\nEl evento que quieres editar comienza 30 minutos antes de que comience el evento (" + z.split("::")[13] + ").");
                                        }
                                        break;
                                    }
                                    //LOS EVENTOS COMIENZAN A LA MISMA HORA
                                    else if (sp_hora_inicial.getSelectedItemPosition() == Integer.valueOf(z.split("::")[0])) {
                                        hora_correcta = false;
                                        tv_hora_inicial_label.setTextColor(Color.RED);
                                        tv_hora_final_label.setTextColor(Color.parseColor("#121212"));

                                        if (!tagHora.equals("2")){
                                            tagHora = "2";
                                            mostrarSnack("Conflicto de horario:\nEl evento que quieres editar comienza a la misma hora que el evento (" + z.split("::")[13] + ").");
                                        }
                                        break;
                                    }
                                    // EL EVENTO COMIENZA 30 MINUTOS ANTES DE QUE TERMINE EL ANTERIOR
                                    else if (sp_hora_inicial.getSelectedItemPosition() == Integer.valueOf(z.split("::")[1])) {
                                        hora_correcta = false;
                                        tv_hora_inicial_label.setTextColor(Color.RED);
                                        tv_hora_final_label.setTextColor(Color.parseColor("#121212"));

                                        if (!tagHora.equals("3")){
                                            tagHora = "3";
                                            mostrarSnack("Conflicto de horario:\nEl evento que quieres editar comienza 30 minutos antes de que finalice el evento (" + z.split("::")[13] + ").");
                                        }
                                        break;
                                    }
                                    // LA HORA INICIAL DEL EVENTO ESTA JUSTO EN MEDIO DEL HORARIO DE OTRO
                                    else if (sp_hora_inicial.getSelectedItemPosition() > Integer.valueOf(z.split("::")[0]) && sp_hora_inicial.getSelectedItemPosition() < Integer.valueOf(z.split("::")[1])) {
                                        hora_correcta = false;
                                        tv_hora_inicial_label.setTextColor(Color.RED);
                                        tv_hora_final_label.setTextColor(Color.parseColor("#121212"));

                                        if (!tagHora.equals("5")){
                                            tagHora = "5";
                                            mostrarSnack("Conflicto de horario:\nEl evento que quieres editar comienza dentro del horario del evento (" + z.split("::")[13] + ").");
                                        }
                                        break;

                                    }
                                    // LA HORA INICIAL DEL EVENTO ES MENOR A LA HORA INICIAL DEL PROX EVENTO
                                    else if (sp_hora_inicial.getSelectedItemPosition() < Integer.valueOf(z.split("::")[0])) {

                                        // EL EVENTO FINALIZA DENTRO DEL HORARIO DEL PROXIMO
                                        if (sp_hora_final.getSelectedItemPosition() - 1 == Integer.valueOf(z.split("::")[0]) || sp_hora_final.getSelectedItemPosition() - 1 == Integer.valueOf(z.split("::")[1])) {
                                            hora_correcta = false;

                                            if (!tagHora.equals("6")){
                                                tagHora = "6";
                                                mostrarSnack("Conflicto de horario:\nEl evento que quieres editar termina dentro del horario del evento (" + z.split("::")[13] + ").");
                                            }

                                            tv_hora_final_label.setTextColor(Color.RED);
                                            tv_hora_inicial_label.setTextColor(Color.parseColor("#121212"));
                                            break;
                                        }
                                        // EL EVENTO FINALIZA DENTRO DEL HORARIO DEL PROXIMO
                                        else if (sp_hora_final.getSelectedItemPosition() - 1 > Integer.valueOf(z.split("::")[0]) && sp_hora_final.getSelectedItemPosition() - 1 < Integer.valueOf(z.split("::")[1])) {
                                            hora_correcta = false;
                                            tv_hora_final_label.setTextColor(Color.RED);
                                            tv_hora_inicial_label.setTextColor(Color.parseColor("#121212"));

                                            if (!tagHora.equals("6")){
                                                tagHora = "6";
                                                mostrarSnack("Conflicto de horario:\nEl evento que quieres editar termina dentro del horario del evento (" + z.split("::")[13] + ").");
                                            }

                                            break;
                                        }
                                        // EL EVENTO COMIENZA ANTES Y TERMINA DESPUES DEL PROXIMO
                                        else if (sp_hora_final.getSelectedItemPosition() - 1 > Integer.valueOf(z.split("::")[1])) {
                                            hora_correcta = false;
                                            tv_hora_final_label.setTextColor(Color.RED);
                                            tv_hora_inicial_label.setTextColor(Color.parseColor("#121212"));

                                            if (!tagHora.equals("7")){
                                                tagHora = "7";
                                                mostrarSnack("Conflicto de horario:\nHay un evento registrado en un horario intermedio al que has elegido (" + z.split("::")[13] + ").");
                                            }
                                            break;
                                        } else if (sp_hora_final.getSelectedItemPosition() - 1 < sp_hora_inicial.getSelectedItemPosition()){
                                            hora_correcta = false;
                                            tv_hora_final_label.setTextColor(Color.RED);
                                            tv_hora_inicial_label.setTextColor(Color.parseColor("#121212"));

                                            if (!tagHora.equals("8")){
                                                tagHora = "8";
                                                mostrarSnack("La hora final del evento no puede ser igual o menor a la inicial.");
                                            }
                                        } else if (sp_hora_final.getSelectedItemPosition() - 1 == sp_hora_inicial.getSelectedItemPosition()){
                                            hora_correcta = false;
                                            tv_hora_final_label.setTextColor(Color.RED);
                                            tv_hora_inicial_label.setTextColor(Color.parseColor("#121212"));

                                            if (!tagHora.equals("9")){
                                                tagHora = "9";
                                                mostrarSnack("El evento debe durar más de 30 minutos.");
                                            }
                                        }else {
                                            hora_correcta = true;
                                            tv_hora_final_label.setTextColor(Color.parseColor("#121212"));
                                            tv_hora_inicial_label.setTextColor(Color.parseColor("#121212"));
                                            if (z.equals(stComprobarEv.split("¦")[stComprobarEv.split("¦").length - 1])){
                                                try {
                                                    snackHora.dismiss();
                                                    tagHora = "";
                                                }catch (Exception ignored){}
                                            }
                                        }

                                    }
                                    //LA HORA INICIAL ES MAYOR AL EVENTO ANTERIOR POR LO TANTO ESTA BIEN
                                    else if (sp_hora_inicial.getSelectedItemPosition() > Integer.valueOf(z.split("::")[0]) && sp_hora_inicial.getSelectedItemPosition() > Integer.valueOf(z.split("::")[1])) {
                                        if (sp_hora_final.getSelectedItemPosition() - 1 < sp_hora_inicial.getSelectedItemPosition()){
                                            hora_correcta = false;
                                            tv_hora_final_label.setTextColor(Color.RED);
                                            tv_hora_inicial_label.setTextColor(Color.parseColor("#121212"));

                                            if (!tagHora.equals("8")){
                                                tagHora = "8";
                                                mostrarSnack("La hora final del evento no puede ser igual o menor a la inicial.");
                                            }
                                        } else if (sp_hora_final.getSelectedItemPosition() - 1 == sp_hora_inicial.getSelectedItemPosition()){
                                            hora_correcta = false;
                                            tv_hora_final_label.setTextColor(Color.RED);
                                            tv_hora_inicial_label.setTextColor(Color.parseColor("#121212"));

                                            if (!tagHora.equals("9")){
                                                tagHora = "9";
                                                mostrarSnack("El evento debe durar más de 30 minutos.");
                                            }
                                        }else {
                                            hora_correcta = true;
                                            tv_hora_final_label.setTextColor(Color.parseColor("#121212"));
                                            tv_hora_inicial_label.setTextColor(Color.parseColor("#121212"));
                                            if (z.equals(stComprobarEv.split("¦")[stComprobarEv.split("¦").length - 1])){
                                                try {
                                                    snackHora.dismiss();
                                                    tagHora = "";
                                                }catch (Exception ignored){}
                                            }
                                        }
                                    }

                                }
                                //EL EVENTO NO ES EN ESE AUDITORIO
                                else {
                                    if (sp_hora_final.getSelectedItemPosition() - 1 < sp_hora_inicial.getSelectedItemPosition()){
                                        hora_correcta = false;
                                        tv_hora_final_label.setTextColor(Color.RED);
                                        tv_hora_inicial_label.setTextColor(Color.parseColor("#121212"));

                                        if (!tagHora.equals("8")){
                                            tagHora = "8";
                                            mostrarSnack("La hora final del evento no puede ser igual o menor a la inicial.");
                                        }
                                    } else if (sp_hora_final.getSelectedItemPosition() - 1 == sp_hora_inicial.getSelectedItemPosition()){
                                        hora_correcta = false;
                                        tv_hora_final_label.setTextColor(Color.RED);
                                        tv_hora_inicial_label.setTextColor(Color.parseColor("#121212"));

                                        if (!tagHora.equals("9")){
                                            tagHora = "9";
                                            mostrarSnack("El evento debe durar más de 30 minutos.");
                                        }
                                    }else {
                                        hora_correcta = true;
                                        tv_hora_final_label.setTextColor(Color.parseColor("#121212"));
                                        tv_hora_inicial_label.setTextColor(Color.parseColor("#121212"));
                                        if (z.equals(stComprobarEv.split("¦")[stComprobarEv.split("¦").length - 1])){
                                            try {
                                                snackHora.dismiss();
                                                tagHora = "";
                                            }catch (Exception ignored){}
                                        }
                                    }
                                }
                            }
                        }
                        // SI EL STRING PARA COMPROBAR DATOS ESTA VACIO QUIERE DECIR QUE NO HAY EVENTO REGISTRADOS Y POR LO TANTO EL CUPO ESTA LIBRE
                        else {
                            if (sp_hora_final.getSelectedItemPosition() - 1 < sp_hora_inicial.getSelectedItemPosition()){
                                hora_correcta = false;
                                tv_hora_final_label.setTextColor(Color.RED);
                                tv_hora_inicial_label.setTextColor(Color.parseColor("#121212"));

                                if (!tagHora.equals("8")){
                                    tagHora = "8";

                                    mostrarSnack("La hora final del evento no puede ser igual o menor a la inicial.");
                                }
                            } else if (sp_hora_final.getSelectedItemPosition() - 1 == sp_hora_inicial.getSelectedItemPosition()){
                                hora_correcta = false;
                                tv_hora_final_label.setTextColor(Color.RED);
                                tv_hora_inicial_label.setTextColor(Color.parseColor("#121212"));

                                if (!tagHora.equals("9")){
                                    tagHora = "9";
                                    mostrarSnack("El evento debe durar más de 30 minutos.");
                                }
                            }else {
                                hora_correcta = true;
                                tv_hora_final_label.setTextColor(Color.parseColor("#121212"));
                                tv_hora_inicial_label.setTextColor(Color.parseColor("#121212"));
                                try {
                                    snackHora.dismiss();
                                    tagHora = "";
                                }catch (Exception ignored){}
                            }
                        }
                    }
                    loopComprobarhoras();
                }
            }, 200);
        }
    }

    private void mostrarSnack(String s) {
        snackHora = Snackbar.make(snackposs, s, Snackbar.LENGTH_INDEFINITE);
        final View view = snackHora.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setMaxLines(3);
        snackHora.show();
    }

    private void colorReveal(final int fondo) {

        //COLOREAMOS DEL COLOR DEL AUDITORIO EL FONDO INVISIBLE
        color_reveal.setBackgroundColor(fondo);

        //OBTENEMOS EL CENTRO DEL FONDO INVISIBLE, SERA EL ORIGEN DEL EFECTO REVELAR
        int cx = (color_reveal.getLeft() + color_reveal.getRight()) / 2;
        int cy = (color_reveal.getTop());

        //OBTENEMOS EL RADIO FINAL PARA EL CIRCULO DEL EFECTO REVELAR
        int finalRadius = Math.max(color_reveal.getWidth(), color_reveal.getHeight());

        //Creamos un animator para la view, el radio del efecto comienza en cero;
        Animator anim = ViewAnimationUtils.createCircularReveal(color_reveal, cx, cy, 0, finalRadius);
        anim.setDuration(500L);

        //AGREGAMOS UN LISTENER PARA CUANDO TERMINE LA ANIMACION ESCONDER DE NUEVO EL VIEW
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //CUANDO LA ANIMACION PONEMOS DEL MISMO COLOR EL FONDO INICIAL
                full_header.setBackgroundColor(fondo);

                //DESPUES OCULTAMOS NUEVAMENTE EL VIEW PARA EL EFECTO REVELAR
                color_reveal.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        //HACEMOS VISIBLE EL FONDO INVISIBLE Y COMENZAMOS LA ANIMACION
        color_reveal.setVisibility(View.VISIBLE);
        anim.start();

    }

    @OnClick(R.id.iv_cerrar_dialog)
    public void cerrar(View view) {
        cerrar = true;
        finishAfterTransition();
    }

    private int fondoAuditorio(String numero) {
        int st = 0;
        switch (numero) {
            case "1":
                st = getResources().getColor(R.color.ed_a);
                break;
            case "2":
                st = getResources().getColor(R.color.ed_b);
                break;
            case "3":
                st = getResources().getColor(R.color.ed_c);
                break;
            case "4":
                st = getResources().getColor(R.color.ed_d);
                break;
            case "5":
                st = getResources().getColor(R.color.ed_e);
                break;
        }
        return st;
    }

    private String fecha(String st_fecha) {
        Calendar fecha = Calendar.getInstance();
        fecha.set(2016, 0, 1);
        fecha.add(Calendar.DAY_OF_YEAR, Integer.valueOf(st_fecha) - 1);
        SimpleDateFormat format = new SimpleDateFormat("cccc',' d 'de' MMMM 'del' yyyy");
        return format.format(fecha.getTime());
    }

    private String stringRepeticion(String repeticion) {
        if (!repeticion.equals("NSR~~")) {
            if (repeticion.split(getString(R.string.separador_repeticion))[0].contains("S")) {
                Calendar repe_info = Calendar.getInstance();
                repe_info.set(2016, 0, 1);
                repe_info.set(Calendar.DAY_OF_YEAR, Integer.valueOf(repeticion.split(getString(R.string.separador_repeticion))[2]));
                SimpleDateFormat format = new SimpleDateFormat("d 'de' MMMM 'del' yyyy");
                if (repeticion.split(getString(R.string.separador_repeticion))[0].substring(0, 1).equals("1")) {
                    repeticion = ("Se repite cada semana hasta el " + format.format(repe_info.getTime()));
                } else {
                    repeticion = ("Se repite cada " + repeticion.split(getString(R.string.separador_repeticion))[0].substring(0, 1) + " semanas hasta el " + format.format(repe_info.getTime()));
                }
            }
        } else {
            repeticion = "Éste evento no se repite.";
        }
        return repeticion;
    }


    @OnClick(R.id.tv_repeticion)
    public void AbrirDialogRepeticion() {
        Intent intent = new Intent(this, DateDialogHelzioRegistrar.class);
        intent.putExtra("DIA", int_fecha_final);
        intent.putExtra("MIN_DIA", int_fecha_inicial);
        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivityForResult(intent, FINAL, bundle);
    }


}

