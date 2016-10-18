package ccv.checkhelzio.nuevaagendacucsh.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import ccv.checkhelzio.nuevaagendacucsh.R;


public class FechasAdaptador extends RecyclerView.Adapter<FechasAdaptador.FechasViewHolder> {

    private List<Fecha> listaFechas;

    private final String auditorio1 = "Auditorio Salvador Allende";
    private final String auditorio2 = "Auditorio Silvano Barba";
    private final String auditorio3 = "Auditorio Carlos Ramírez";
    private final String auditorio4 = "Auditorio Adalberto Navarro";
    private final String auditorio5 = "Sala de Juicios Orales Mariano Otero";
    private final int ELIMINAR_EVENTO = 4;
    private Context mContext;
    protected ArrayAdapter<String> adapterHoras;
    private String[] horas = new String[]{
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

    public FechasAdaptador(List<Fecha> fechas, Context context) {
        this.listaFechas = fechas;
        adapterHoras = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, horas);
        mContext = context;
    }

    @Override
    public FechasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //return null;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nueva_fecha, parent, false);
        return new FechasViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final FechasViewHolder fechasViewHolder , int position) {

        final Fecha fecha = listaFechas.get(position);
        String label = "Fecha del evento No. " + (position + 1);
        fechasViewHolder.tv_label_fecha.setText(label);
        fechasViewHolder.tv_fecha.setText(fecha(fecha.getDia()));
        fechasViewHolder.sp_inicial.setAdapter(adapterHoras);
        fechasViewHolder.sp_final.setAdapter(adapterHoras);

        fecha.setLabel_inicial(fechasViewHolder.tv_label_inicial);
        fecha.setLabel_final(fechasViewHolder.tv_label_final);

        fechasViewHolder.sp_inicial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fecha.setHoraInicial(i);
                if (i < 29) {
                    fechasViewHolder.sp_final.setSelection(i + 2);
                    fecha.setHoraFinal(i + 2);
                } else {
                    fechasViewHolder.sp_final.setSelection(30);
                    fecha.setHoraFinal(30);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        fechasViewHolder.sp_final.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fecha.setHoraFinal(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        try {
            fechasViewHolder.sp_inicial.setSelection(fecha.getHoraInicial());
            fechasViewHolder.sp_final.setSelection(fecha.getHoraFinal());
        }catch (Exception ignored){}

    }

    @Override
    public int getItemCount() {
        return listaFechas.size();
    }

    public static class FechasViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_label_fecha, tv_fecha, tv_label_inicial, tv_label_final;
        private Spinner sp_inicial, sp_final;

        public FechasViewHolder(View itemView) {
            super(itemView);
            tv_label_fecha = (TextView) itemView.findViewById(R.id.label_fecha);
            tv_label_inicial = (TextView) itemView.findViewById(R.id.tv_hora_inicial_label);
            tv_label_final = (TextView) itemView.findViewById(R.id.tv_hora_final_label);
            tv_fecha = (TextView) itemView.findViewById(R.id.tv_fecha);
            sp_inicial = (Spinner) itemView.findViewById(R.id.sp_hora_inicial);
            sp_final = (Spinner) itemView.findViewById(R.id.sp_hora_final);
        }
    }

    public void removeItemAtPosition(int position) {

        // REMOVER PRIMERO LOS CONFLICTOS QUE TENGA LA FECHA QUE SE VA A ELIMINAR
        RegistrarEvento.listaConflictos.clear();
        listaFechas.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listaFechas.size());
    }

    private String fecha(int st_fecha) {
        Calendar fecha = Calendar.getInstance();
        fecha.set(2016, 0, 1);
        fecha.add(Calendar.DAY_OF_YEAR, (st_fecha) - 1);
        SimpleDateFormat format = new SimpleDateFormat("c ',' d 'de' MMMM 'del' yyyy");
        return format.format(fecha.getTime());
    }

}
