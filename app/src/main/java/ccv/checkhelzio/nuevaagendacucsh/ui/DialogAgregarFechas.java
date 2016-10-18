package ccv.checkhelzio.nuevaagendacucsh.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;

import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ccv.checkhelzio.nuevaagendacucsh.R;
import ccv.checkhelzio.nuevaagendacucsh.util.AnimUtils;

public class DialogAgregarFechas extends Activity {

    List<Integer> listaFechas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_agregar_fechas);
        postponeEnterTransition();
        ButterKnife.bind(this);

        // CALENDIARIO CON HORA ACTUAL
        Calendar c = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        if (c.get(Calendar.HOUR_OF_DAY) > 17) {
            int hora = c.get(Calendar.HOUR_OF_DAY);
            int minuto = c.get(Calendar.MINUTE);
            if (hora == 18) {
                if (minuto > 30){
                    c.add(Calendar.DAY_OF_YEAR, 1);
                    c2.add(Calendar.DAY_OF_YEAR, 1);
                }
            } else {
                c.add(Calendar.DAY_OF_YEAR, 1);
                c2.add(Calendar.DAY_OF_YEAR, 1);
            }
        }

        if (getIntent().getIntExtra("DIA", 0) != -1){

            // c1 con la hora que se paso desde el click a la fecha
            c.set(2016,0,1);
            c.set(Calendar.DAY_OF_YEAR, getIntent().getIntExtra("DIA", 0));

            // c2 tiene la fecha del dia de hoy, comprobar las dos fechas para ver si son iguales

            if (c.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)){
                if (c.get(Calendar.HOUR_OF_DAY) > 17) {

                    int hora = c.get(Calendar.HOUR_OF_DAY);
                    int minuto = c.get(Calendar.MINUTE);
                    if (hora == 18) {
                        if (minuto > 30){

                            c2.set(2016,0,1);
                            c2.set(Calendar.DAY_OF_YEAR, getIntent().getIntExtra("DIA", 0));

                            c.add(Calendar.DAY_OF_YEAR, 1);
                            c2.add(Calendar.DAY_OF_YEAR, 1);
                        }
                    } else {

                        c2.set(2016,0,1);
                        c2.set(Calendar.DAY_OF_YEAR, getIntent().getIntExtra("DIA", 0));

                        c.add(Calendar.DAY_OF_YEAR, 1);
                        c2.add(Calendar.DAY_OF_YEAR, 1);
                    }
                }
            }else {
                c2.set(2016,0,1);
                c2.set(Calendar.DAY_OF_YEAR, getIntent().getIntExtra("DIA", 0));
            }

        }
        c2.add(Calendar.YEAR, 2);

        CalendarPickerView calendar = (CalendarPickerView) findViewById(R.id.conteDialog);
        calendar.init(c.getTime(), c2.getTime()).inMode(CalendarPickerView.SelectionMode.MULTIPLE);
        calendar.setOnInvalidDateSelectedListener(null);

        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                Calendar c = Calendar.getInstance();
                c.setTime(date);

                int dia_de_año;
                int c_año = c.get(Calendar.YEAR);
                if (c_año == 2016) {
                    dia_de_año = c.get(Calendar.DAY_OF_YEAR);
                } else {
                    dia_de_año = c.get(Calendar.DAY_OF_YEAR);
                    for (int x = 2016; x < c_año; x++) {
                        c.set(x,0,1);
                        dia_de_año += c.getActualMaximum(Calendar.DAY_OF_YEAR);
                    }
                }

                listaFechas.add(dia_de_año);

            }

            @Override
            public void onDateUnselected(Date date) {

                try {
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);

                    int dia_de_año;
                    int c_año = c.get(Calendar.YEAR);
                    if (c_año == 2016) {
                        dia_de_año = c.get(Calendar.DAY_OF_YEAR);
                    } else {
                        dia_de_año = c.get(Calendar.DAY_OF_YEAR);
                        for (int x = 2016; x < c_año; x++) {
                            c.set(x,0,1);
                            dia_de_año += c.getActualMaximum(Calendar.DAY_OF_YEAR);
                        }
                    }

                    int x = 0;
                    for (Integer fecha : listaFechas){
                        if (fecha == dia_de_año){
                            listaFechas.remove(x);
                        }
                        x++;
                    }
                }catch (Exception ignored){}

            }
        });

        Slide slide = new Slide(Gravity.BOTTOM);
        slide.setInterpolator(AnimUtils.getLinearOutSlowInInterpolator(DialogAgregarFechas.this));
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(slide);

        startPostponedEnterTransition();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        cerrar(null);
    }

    @OnClick (R.id.bt_dialog_aceptar)
    public void irDia(View view) {
        if (listaFechas.size() > 0){
            Intent i = getIntent();
            i.putIntegerArrayListExtra("LISTA_FECHAS", (ArrayList<Integer>) listaFechas);
            setResult(RESULT_OK, i);
        }
        cerrar(null);
    }

    @OnClick (R.id.bt_dialog_cancenlar)
    public void cerrar(View view) {
        Slide slide = new Slide(Gravity.BOTTOM);
        slide.setInterpolator(AnimUtils.getFastOutLinearInInterpolator(DialogAgregarFechas.this));
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setExitTransition(slide);
        finishAfterTransition();
    }
}
