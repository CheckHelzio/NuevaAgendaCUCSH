package ccv.checkhelzio.nuevaagendacucsh.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ccv.checkhelzio.nuevaagendacucsh.R;
import ccv.checkhelzio.nuevaagendacucsh.transitions.ChangeBoundBackground;

public class DialogListaEventosHelzio extends Activity {

    @BindView(R.id.fondo) RelativeLayout fondo;
    @BindView(R.id.recycle) RecyclerView rvEventos;
    @BindView(R.id.tv_mensaje_no_evento) TextView tv_mensaje_no_eventos;
    @BindView(R.id.tv_mensaje_con_evento) TextView tv_mensaje_con_eventos;
    @BindView(R.id.tv_num_dia) TextView tv_num_dia;
    @BindView(R.id.tv_nom_dia) TextView tv_nom_dia;
    private List<Eventos> listaEventos;
    private  EventosAdaptador adaptador;
    private Boolean animando = false;
    private Handler handler;
    private static int REGISTRAR = 1313;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_lista_eventos);
        ButterKnife.bind(this);
        postponeEnterTransition();

        handler = new Handler();
        tv_num_dia.setText(getIntent().getStringExtra("DIA_MES"));
        String nom = getIntent().getStringExtra("NOMBRE_DIA").substring(0,3) + ".";
        tv_nom_dia.setText(nom);

        //rvEventos.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvEventos.setLayoutManager(mLayoutManager);

        inicializarDatos();

        fondo.postDelayed(new Runnable() {
            @Override
            public void run() {
                final Rect endBounds = new Rect(fondo.getLeft(), fondo.getTop(), fondo.getRight(), fondo.getBottom());
                ChangeBoundBackground.setup(DialogListaEventosHelzio.this, fondo, true, endBounds, getViewBitmap(fondo));
                getWindow().getSharedElementEnterTransition();
                startPostponedEnterTransition();
            }
        }, 30);
    }

    private void inicializarDatos() {
        try {
            listaEventos = getIntent().getParcelableArrayListExtra("LISTA_EVENTOS");

            if (listaEventos.size() > 0){
                tv_mensaje_no_eventos.setVisibility(View.GONE);
                if (getIntent().getBooleanExtra("REGISTRAR", false)){
                    tv_mensaje_con_eventos.setText(R.string.toca_resgitrar_evento);
                    tv_mensaje_con_eventos.setTextColor(Color.BLACK);
                    tv_num_dia.setTextColor(Color.BLACK);
                    tv_nom_dia.setTextColor(Color.BLACK);

                }else {
                    tv_mensaje_con_eventos.setVisibility(View.GONE);
                }
            }else {
                tv_mensaje_con_eventos.setVisibility(View.GONE);
                tv_mensaje_no_eventos.setVisibility(View.VISIBLE);
                if (getIntent().getBooleanExtra("REGISTRAR", false)){
                    tv_mensaje_no_eventos.setText(R.string.no_eventos_registra);
                    tv_mensaje_no_eventos.setTextColor(Color.BLACK);
                    tv_num_dia.setTextColor(Color.BLACK);
                    tv_nom_dia.setTextColor(Color.BLACK);
                }else {
                    tv_mensaje_no_eventos.setText("No hay eventos registrados este día.");
                }
            }
            iniciarAdaptador();

        }catch (Exception ignored){

        }

        if (getIntent().getBooleanExtra("ES_HOY", false)){
            tv_num_dia.setTextColor(getResources().getColor(R.color.colorAcento));
            tv_nom_dia.setTextColor(getResources().getColor(R.color.colorAcento));
            tv_mensaje_con_eventos.setTextColor(getResources().getColor(R.color.colorAcento));
            tv_mensaje_no_eventos.setTextColor(getResources().getColor(R.color.colorAcento));
        }
    }

    @OnClick ({R.id.tv_mensaje_con_evento, R.id.tv_mensaje_no_evento})
    public void RegistrarEvento(){
        if (getIntent().getBooleanExtra("REGISTRAR", false)){
            Intent intent = new Intent(this, RegistrarEvento.class);
            intent.putExtra("DIA_AÑO", getIntent().getIntExtra("DIA_AÑO", 0));
            intent.putExtra("DONDE", "LISTA");
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivityForResult(intent, REGISTRAR, bundle);
        }
    }

    private void iniciarAdaptador() {
        adaptador = new EventosAdaptador(listaEventos, DialogListaEventosHelzio.this);
        rvEventos.setAdapter(adaptador);
    }

    @Override
    public void onBackPressed() {
        dismiss(null);
    }

    public void dismiss(View view) {
        if (animando){
            Log.v("ANIMACION", "SIN ANIMACION");
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }else {
            finishAfterTransition();
            Log.v("ANIMACION", "CON ANIMACION");
        }
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

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (requestCode == REGISTRAR){
                    if (resultCode == RESULT_OK) {
                        animando = true;
                        ArrayList<Eventos> listaDeEventos = data.getParcelableArrayListExtra("LISTA");
                        for (Eventos e : listaDeEventos){
                            adaptador.addItem(e);
                        }
                    }
                    Principal.esperar = false;
                }else {
                    animando = true;
                    if (resultCode == RESULT_OK){

                        Log.v("ELIMINAR", "REGRESANDOA  LISTA... RESULTADO OK");
                        listaEventos.remove(data.getIntExtra("POSITION", 0));
                        rvEventos.removeViewAt(data.getIntExtra("POSITION", 0));
                        adaptador.removeItemAtPosition(data.getIntExtra("POSITION", 0));
                    }

                    Log.v("ELIMINAR", "ESPERANDO = FALSE");
                    Principal.esperar = false;

                }
            }
        },150);
    }
}
