package ccv.checkhelzio.nuevaagendacucsh.ui;

/**
 * Created by check on 23/09/2016.
 */

public class Conflictos {
    private int num_fecha;
    private int num_fecha_2;
    private String tipo;
    private Eventos queEvento;
    private String auditorio;

    public Conflictos(int n, Eventos e, String ad) {
        this.num_fecha = n;
        this.queEvento = e;
        this.auditorio = ad;
    }

    public String getAuditorio() {
        return auditorio;
    }

    public void setAuditorio(String auditorio) {
        this.auditorio = auditorio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }


    public Conflictos(int num_fecha, Eventos queEvento) {
        this.num_fecha = num_fecha;
        this.queEvento = queEvento;
    }

    public Conflictos(int num_fecha, int num_fecha_2, String tipo) {
        this.num_fecha = num_fecha;
        this.num_fecha_2 = num_fecha_2;
        this.tipo = tipo;
    }

    public int getNum_fecha() {
        return num_fecha;
    }

    public void setNum_fecha(int num_fecha) {
        this.num_fecha = num_fecha;
    }

    public Eventos getQueEvento() {
        return queEvento;
    }

    public void setQueEvento(Eventos queEvento) {
        this.queEvento = queEvento;
    }

    public int getNum_fecha_2() {
        return num_fecha_2;
    }

    public void setNum_fecha_2(int num_fecha_2) {
        this.num_fecha_2 = num_fecha_2;
    }
}
