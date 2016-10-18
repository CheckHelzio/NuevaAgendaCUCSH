package ccv.checkhelzio.nuevaagendacucsh.ui;

import android.widget.TextView;

/**
 * Created by check on 23/09/2016.
 */

public class Fecha {
    private int dia;
    private int horaInicial;
    private int horaFinal;
    private TextView label_inicial;
    private TextView label_final;

    public Fecha(int dia, int horaInicial, int horaFinal) {
        this.dia = dia;
        this.horaInicial = horaInicial;
        this.horaFinal = horaFinal;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getHoraInicial() {
        return horaInicial;
    }

    public void setHoraInicial(int horaInicial) {
        this.horaInicial = horaInicial;
    }

    public int getHoraFinal() {
        return horaFinal;
    }

    public void setHoraFinal(int horaFinal) {
        this.horaFinal = horaFinal;
    }

    public TextView getLabel_inicial() {
        return label_inicial;
    }

    public void setLabel_inicial(TextView label_inicial) {
        this.label_inicial = label_inicial;
    }

    public TextView getLabel_final() {
        return label_final;
    }

    public void setLabel_final(TextView label_final) {
        this.label_final = label_final;
    }
}
