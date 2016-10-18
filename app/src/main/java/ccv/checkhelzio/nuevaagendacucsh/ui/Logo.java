package ccv.checkhelzio.nuevaagendacucsh.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import ccv.checkhelzio.nuevaagendacucsh.R;

public class Logo extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo);

        //DIBUJAR EL LOGO DEBAJO DE LA STATUS BAR
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        new TextView(Logo.this).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent("REGISTRO");
                startActivity(i);
                finish();
            }
        },1000);

    }
}
