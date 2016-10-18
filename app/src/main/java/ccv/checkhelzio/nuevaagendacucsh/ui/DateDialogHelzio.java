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

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ccv.checkhelzio.nuevaagendacucsh.R;
import ccv.checkhelzio.nuevaagendacucsh.transitions.FabTransition;
import ccv.checkhelzio.nuevaagendacucsh.util.AnimUtils;

public class DateDialogHelzio extends Activity {

    @BindView(R.id.conteDialog) DatePicker datePicker;
    @BindView(R.id.conte) ViewGroup conte;
    @BindView(R.id.bt_dialog_aceptar) Button aceptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_datepicker);
        postponeEnterTransition();
        ButterKnife.bind(this);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 0, 1);
        datePicker.setMinDate(calendar.getTimeInMillis());
        datePicker.setFirstDayOfWeek(Calendar.MONDAY);

        Slide slide = new Slide(Gravity.BOTTOM);
        slide.setInterpolator(AnimUtils.getLinearOutSlowInInterpolator(DateDialogHelzio.this));
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(slide);

        startPostponedEnterTransition();
        //FabTransition.setup(this, conte);
        //getWindow().getSharedElementEnterTransition();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        cerrar(null);
    }


    @OnClick (R.id.bt_dialog_aceptar)
    public void irDia(View view) {

        int mes;
        if (datePicker.getYear() == 2016) {
            mes = datePicker.getMonth();
        } else {
            mes = datePicker.getMonth();
            for (int x = 2016; x < datePicker.getYear(); x++) {
                mes += 12;
            }
        }

        Intent i = getIntent();
        i.putExtra("NUMERO_DE_MES", mes);
        setResult(RESULT_OK, i);
        cerrar(null);
    }

    @OnClick (R.id.bt_dialog_cancenlar)
    public void cerrar(View view) {
        finishAfterTransition();
    }
}
