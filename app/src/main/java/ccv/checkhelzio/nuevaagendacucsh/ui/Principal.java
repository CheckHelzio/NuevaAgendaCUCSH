package ccv.checkhelzio.nuevaagendacucsh.ui;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ccv.checkhelzio.nuevaagendacucsh.R;
import ccv.checkhelzio.nuevaagendacucsh.transitions.FabTransition;
import ccv.checkhelzio.nuevaagendacucsh.util.DescargarBD;

public class Principal extends AppCompatActivity {

    @BindView(R.id.fab)
    ImageButton fab;
    @BindView(R.id.principal_coordinatorlayout)
    CoordinatorLayout coordinator;
    @BindView(R.id.nv)
    NavigationView nv;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    protected static ArrayList<Eventos> lista_eventos = new ArrayList<>();
    private static boolean wifiConnected = false;
    private static boolean mobileConnected = false;
    protected static Calendar calendarioActualizarDiasMes;
    private Calendar calendarioMinimo;
    private Calendar calendarioIrHoy;

    private Handler handler;

    static final int HELZIO_DATE_DIALOG = 13;
    static final int HELZIO_ELIMINAR_EVENTO = 4;
    protected static int irHoyMes;
    protected static int irHoyDiaSemana;
    protected static int irHoyNumeroDiaMes;
    protected static int irHoyAño;
    private int irHoyNumeroMesAño;

    private String st_eventos_guardados = "";
    protected static String stNuevoId = "";

    private TextView tv_header2;
    private TextView tv_conexion;

    protected static ViewPager viewPager;
    private SharedPreferences prefs;
    protected static String titulos = "";
    protected static String tiposDeEvento = "";
    protected static String nombresOrganizador = "";
    private boolean pagerIniciado = false;
    private int id_prox = 0;
    protected static boolean filtro1 = true, filtro2 = true, filtro3  = true, filtro4 = true, filtro5 = true;
    private boolean actualizando = false;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a);
        ButterKnife.bind(this);

        if (isScreenLarge()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        iniciarObjetos();

        iniciarDrawer();

        // ir hoy regresa el numero de mes a partir del año 2016
        irHoy();
        
        // actualizar fecha actualiza la etiqueta verde con la fecha y año correspondientes
        actualizarFecha();
        
        // revisa si hay conexcion a internet y si la hay descarga la base de datos
        checkNetworkConnection();
        
        //una vez descargada la base de datos llenamos la lista de eventos
        new LlenarListaEventos().execute();
    }

    private void iniciarDrawer() {
        // Initializing Toolbar and setting it as the actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.header);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Checking if the item is in checked state or not, if not make it in checked state
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){

                }
                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    private void iniciarObjetos() {
        prefs = getSharedPreferences("EVENTOS CUCSH BELENES", Context.MODE_PRIVATE);
        st_eventos_guardados = prefs.getString("EVENTOS GUARDADOS", "");

        tv_header2 = (TextView) findViewById(R.id.header2);
        tv_conexion = (TextView) findViewById(R.id.conexion);

        calendarioMinimo = Calendar.getInstance();
        calendarioMinimo.set(2016, 0, 1);

        calendarioIrHoy = Calendar.getInstance();
        irHoyNumeroDiaMes = calendarioIrHoy.get(Calendar.DAY_OF_MONTH);
        irHoyDiaSemana = calendarioIrHoy.get(Calendar.DAY_OF_WEEK);
        irHoyAño = calendarioIrHoy.get(Calendar.YEAR);
        irHoyMes = calendarioIrHoy.get(Calendar.MONTH);

        calendarioActualizarDiasMes = Calendar.getInstance();
        calendarioActualizarDiasMes.set(Calendar.DAY_OF_MONTH, 1);

        handler = new Handler();
    }

    private void irHoy() {
        if (irHoyAño == 2016) {
            irHoyNumeroMesAño = calendarioIrHoy.get(Calendar.MONTH);
        } else {
            irHoyNumeroMesAño = calendarioIrHoy.get(Calendar.MONTH);
            for (int x = 2016; x < irHoyAño; x++) {
                irHoyNumeroMesAño += 12;
            }
        }
    }

    private void setListenners() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                calendarioActualizarDiasMes.set(2016, viewPager.getCurrentItem(), 1);
                actualizarFecha();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void actualizarFecha() {
        SimpleDateFormat format = new SimpleDateFormat("MMMM 'del' yyyy", Locale.forLanguageTag("es-MX"));
        String f = format.format(calendarioActualizarDiasMes.getTime());
        tv_header2.setText(capitalize(f));
    }

    private void checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            if (wifiConnected) {
                new DescargarBD().execute("http://148.202.6.72/aplicacion/datos2.txt", Principal.this);
            } else if (mobileConnected) {
                // SE HA DESCARGADO LA BASE DE DATOS DESDE LOS DATOS MOBILES, ENVIAMOS UNA ALERTA PARA QUE SE DESCARGUEN MANUALMENTE PARA NO CONSUMIR LOS DATOS DEL USUARIO
                tv_conexion.setText("Para no consumir datos, la actualización de la base de datos es manual mientras no estes conectado a una red WIFI.");
            }
        }else {
            // NO HAY CONEXCION A INTERNET MANDAR UN AVISO AL USUARIO Y COMPROBAR CADA SEGUNDO PARA NO SATURAR EL HILO PRINCIPAL
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    // SI NO HAY INTERNET AVISAMOS AL USUARIO PARA QUE VERIFIQUE LAS CONEXIONES
                    tv_conexion.setText("Hay un problema con la conexión a la base de datos. Verifica tu conexión a internet.");

                    // A PESAR DE NO TENER INTENET SEGUIMOS INTENTANDO PARA CUANDO SE RECUPERE LA CONEXION
                    checkNetworkConnection();
                }
            },1000);
        }
    }

    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    private boolean isScreenLarge() {
        final int screenSize = getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
                || screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    // SI TO DO SALIO BIEN CON LA DESCARGA LLAMAMOS A ESTE METODO PARA FINALIZAR ESTA ETAPA
    public void postDescargar(String s) {

        // ESCRIBIMOS EN EL FOOTER LA HORA DE ACTUALIZACION Y LA GUARDAMOS EN LA BASE DE DATOS PARA DAR REFERENCIA AL USUARIO DE LA ULTIMA VEZ QUE FUE ACTUALIZADA SI SE UTILIZA SIN INTERNET
        calendarioIrHoy = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("'Actualizado el 'd 'de' MMMM 'del' yyyy 'a las' h:mm a", Locale.forLanguageTag("es-MX"));
        tv_conexion.setText(format.format(calendarioIrHoy.getTime()));
        prefs.edit().putString("ACTUALIZACION", format.format(calendarioIrHoy.getTime())).apply();

        try {
            // MUCHAS VECES LA BASE DE DATOS ES DESCARGADA CON CODIGO HTML QUE NO NECESITOS, POR ESO AQUI LO REEMPLAZAMOS
            s = s.split("</form>")[1].trim();
        } catch (Exception ignored) {
        }
        if (!s.trim().equals(st_eventos_guardados.trim())) {
            // SI LA BASE DE DATOS QUE DESCARGARMOS NO ES IGUAL A LA QUE YA TENEMOS LA SOBREESCRIBIMOS Y DESPUES LLEMANOS NUEVAMENTE LA LISTA DE EVENTOS
            st_eventos_guardados = s;
            prefs.edit().putString("EVENTOS GUARDADOS", st_eventos_guardados).apply();
            new LlenarListaEventos().execute();
        }

        // CADA MEDIO SEGUNDO COMPROBAMOS NUEVAMENTE LA CONEXION A INTERNET PARA DESCARGAR CONSTANTEMENTE LA BASE DE DATOS Y CHECAR CAMBIOS
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (wifiConnected) {
                        // SI HAY INTERNET DESCARGAMOS NUEVAMENTE LA BASE DE DATOS
                        new DescargarBD().execute("http://148.202.6.72/aplicacion/datos2.txt", Principal.this);
                    }
                }
            },500);
        }else {
            // SI NO HAY INTERNET AVISAMOS AL USUARIO PARA QUE VERIFIQUE LAS CONEXIONES
            tv_conexion.setText("Hay un problema con la conexión a la base de datos. Verifica tu conexión a internet.");

            // A PESAR DE NO TENER INTENET SEGUIMOS INTENTANDO PARA CUANDO SE RECUPERE LA CONEXION
            checkNetworkConnection();
        }
    }

    @OnClick(R.id.fab)
    public void fabClick() {
        Intent intent = new Intent(this, RegistrarEvento.class);
        intent.putExtra("DONDE", "PRINCIPAL");
        FabTransition.addExtras(intent, (Integer) getAcentColor(), R.drawable.ic_mas);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, fab,
                getString(R.string.transition_date_dialog_helzio));
        startActivityForResult(intent, HELZIO_DATE_DIALOG, options.toBundle());
    }

    protected class LlenarListaEventos extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... aa12) {
            // EN OCACIONES LA BASE DE DATOS DESCARGA CODIGO HTML QUE NO NECESITAMOS, LO QUITAMOS AQUI
            if (st_eventos_guardados.contains("</form>")) {
                st_eventos_guardados = st_eventos_guardados.split("</form>")[1].trim();
            }

            lista_eventos.clear();
            if (st_eventos_guardados.trim().length() > 0) {
                for (String eventos_suelto : st_eventos_guardados.trim().split("¦")) {
                    if (!eventos_suelto.trim().equals("")) {

                        if (!titulos.contains(eventos_suelto.trim().split("::")[4].trim())) {
                            titulos += eventos_suelto.trim().split("::")[4].trim() + "¦";
                        }
                        if (!tiposDeEvento.contains(eventos_suelto.trim().split("::")[6].trim())) {
                            tiposDeEvento += eventos_suelto.trim().split("::")[6].trim() + "¦";
                        }
                        if (!nombresOrganizador.contains(eventos_suelto.trim().split("::")[7].trim())) {
                            nombresOrganizador += eventos_suelto.trim().split("::")[7].trim() + "¦";
                        }

                        lista_eventos.add(
                        new Eventos(
                                // FECHA
                                eventos_suelto.split("::")[0].trim().replaceAll("[^0-9]+",""),
                                // HORA INCIAL
                                eventos_suelto.split("::")[1].trim().replaceAll("[^0-9]+",""),
                                // HORA FINAL
                                eventos_suelto.split("::")[2].trim().replaceAll("[^0-9]+",""),
                                // TITULO
                                eventos_suelto.split("::")[3].trim(),
                                // AUDITORIO
                                eventos_suelto.split("::")[4].trim().replaceAll("[^0-9]+",""),
                                // TIPO DE EVENTO
                                eventos_suelto.split("::")[5].trim(),
                                // NOMBRE DEL ORGANIZADOR
                                eventos_suelto.split("::")[6].trim(),
                                // NUMERO TELEFONICO DEL ORGANIZADOR
                                eventos_suelto.split("::")[7].trim(),
                                // STATUS DEL EVENTO
                                eventos_suelto.split("::")[8].trim(),
                                // QUIEN REGISTRO
                                eventos_suelto.split("::")[9].trim(),
                                // CUANDO REGISTRO
                                eventos_suelto.split("::")[10].trim(),
                                // NOTAS
                                eventos_suelto.split("::")[11].trim(),
                                // ID
                                eventos_suelto.split("::")[12].trim().replaceAll("[^0-9]+",""),
                                // TAG
                                eventos_suelto.trim(),
                                // FONDO
                                fondoAuditorio(eventos_suelto.split("::")[4].trim())
                        ));
                    }

                    // COMPROBAMOS EL ID DE CADA EVENTO PARA DETERMINAR SI ES MAYOR AL ANTERIOR Y AL FINAL OBTENER EL ID MAS ALTO
                    if (Integer.parseInt(eventos_suelto.split("::")[12].trim()) > id_prox){
                        id_prox = Integer.parseInt(eventos_suelto.split("::")[12].trim());
                    }
                }

                stNuevoId = "" + (id_prox + 1);
                if (stNuevoId.length() == 1) {
                    stNuevoId = "000" + stNuevoId;
                } else if (stNuevoId.length() == 2) {
                    stNuevoId = "00" + stNuevoId;
                } else if (stNuevoId.length() == 3) {
                    stNuevoId = "0" + stNuevoId;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // COMPROBAMOS QUE NO HAYA ANIMACIONES PENDIENTES ANTES DE LLENAR EL PAGER CON LA INFO DE CADA MES
            loopAnimando();
        }
    }

    private void loopAnimando() {
        // COMPROBAMOS QUE NO HAYA ANIMACIONES PENDIENTES ANTES DE LLENAR EL PAGER CON LA INFO DE CADA MES
        // COMPROBAMOS CADA 300 MILISEGUNDOS PARA TENER LA INFO ACTUALIZADA EN CADA MOMENTO
        if (!DialogListaEventosHelzio.animando){

            // SI NO HAY ANIMACIONES PENDIENTES INICIAMOS EL PAGER
            iniciarPager();

            // DESPUES DE INICIAR EL PAGER INICIAMOS LOS LISTENERS CORRESPONDIENTES
            setListenners();
        }else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loopAnimando();
                }
            },300);
        }

    }

    private void iniciarPager() {
        if (!pagerIniciado){
            // SI EL PAGER NO ESTA INICIADO LO INICIAMOS POR PRIMERA VEZ
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.setOffscreenPageLimit(1);
            viewPager.setPageMargin((int) getResources().getDimension(R.dimen.fab_size));
            viewPager.setAdapter(new HelzioAdapter(getSupportFragmentManager()));
            viewPager.setCurrentItem(irHoyNumeroMesAño);
            pagerIniciado = true;
        }else {
            // SI EL PAGER YA ESTA INICIADO NO LO RECONSTRUIMOS SOLO LO ACTUALZIAMOS
            viewPager.getAdapter().notifyDataSetChanged();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case HELZIO_DATE_DIALOG:
                if (resultCode == RESULT_OK) {
                    viewPager.setCurrentItem(data.getExtras().getInt("NUMERO_DE_MES"), true);
                }
                break;
            case HELZIO_ELIMINAR_EVENTO:

                break;
        }
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

    @OnClick ({R.id.filtro1, R.id.filtro2,R.id.filtro3,R.id.filtro4,R.id.filtro5})
    public void checkAuditorios(View v){

        ((CheckBox)((ViewGroup)v).getChildAt(0)).setChecked(!((CheckBox)((ViewGroup)v).getChildAt(0)).isChecked());

        switch (v.getId()){
            case R.id.filtro1:
                filtro1 = ((CheckBox)((ViewGroup)v).getChildAt(0)).isChecked();
                break;
            case R.id.filtro2:
                filtro2 = ((CheckBox)((ViewGroup)v).getChildAt(0)).isChecked();
                break;
            case R.id.filtro3:
                filtro3 = ((CheckBox)((ViewGroup)v).getChildAt(0)).isChecked();
                break;
            case R.id.filtro4:
                filtro4 = ((CheckBox)((ViewGroup)v).getChildAt(0)).isChecked();
                break;
            case R.id.filtro5:
                filtro5 = ((CheckBox)((ViewGroup)v).getChildAt(0)).isChecked();
                break;
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewPager.getAdapter().notifyDataSetChanged();
            }
        },300);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_fecha:
                Intent intent = new Intent(this, DateDialogHelzio.class);
                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
                startActivityForResult(intent, HELZIO_DATE_DIALOG, bundle);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
