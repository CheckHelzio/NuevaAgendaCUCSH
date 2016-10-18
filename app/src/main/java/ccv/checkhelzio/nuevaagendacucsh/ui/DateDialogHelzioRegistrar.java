package ccv.checkhelzio.nuevaagendacucsh.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ccv.checkhelzio.nuevaagendacucsh.R;
import ccv.checkhelzio.nuevaagendacucsh.util.AnimUtils;

public class DateDialogHelzioRegistrar extends Activity {

    @BindView(R.id.conteDialog) DatePicker datePicker;
    @BindView(R.id.conte) ViewGroup conte;
    @BindView(R.id.bt_dialog_aceptar) Button aceptar;
    @BindView(R.id.dateDialogToolbar) TextView titulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_datepicker);
        postponeEnterTransition();
        ButterKnife.bind(this);

        Calendar calendar = Calendar.getInstance();
        datePicker.setMinDate(calendar.getTimeInMillis());
        datePicker.setFirstDayOfWeek(Calendar.MONDAY);

        calendar.set(2016,0,1);
        calendar.set(Calendar.DAY_OF_YEAR, getIntent().getIntExtra("DIA", 0));
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        titulo.setText(getIntent().getStringExtra("M"));

        Slide slide = new Slide(Gravity.BOTTOM);
        slide.setInterpolator(AnimUtils.getLinearOutSlowInInterpolator(DateDialogHelzioRegistrar.this));
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
        Calendar c = Calendar.getInstance();
        c.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

        if (c.getTimeInMillis() >= datePicker.getMinDate()){
            int dia_de_año;
            if (datePicker.getYear() == 2016) {
                dia_de_año = c.get(Calendar.DAY_OF_YEAR);
            } else {
                dia_de_año = c.get(Calendar.DAY_OF_YEAR);
                for (int x = 2016; x < datePicker.getYear(); x++) {
                    c.set(x,0,1);
                    dia_de_año += c.getActualMaximum(Calendar.DAY_OF_YEAR);
                }
            }

            Intent i = getIntent();
            i.putExtra("DIA_DEL_AÑO", dia_de_año);
            setResult(RESULT_OK, i);
            cerrar(null);
        }
    }

    @OnClick (R.id.bt_dialog_cancenlar)
    public void cerrar(View view) {
        Slide slide = new Slide(Gravity.BOTTOM);
        slide.setInterpolator(AnimUtils.getFastOutLinearInInterpolator(DateDialogHelzioRegistrar.this));
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setExitTransition(slide);
        finishAfterTransition();
    }
}
