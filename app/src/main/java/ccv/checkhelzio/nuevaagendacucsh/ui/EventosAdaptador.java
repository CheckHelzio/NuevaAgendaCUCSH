package ccv.checkhelzio.nuevaagendacucsh.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ccv.checkhelzio.nuevaagendacucsh.R;
import ccv.checkhelzio.nuevaagendacucsh.transitions.ChangeBoundBackground2;

public class EventosAdaptador extends RecyclerView.Adapter<EventosAdaptador.EventosViewHolder> {

    private List<Eventos> eventos;

    private final String auditorio1 = "Auditorio Salvador Allende";
    private final String auditorio2 = "Auditorio Silvano Barba";
    private final String auditorio3 = "Auditorio Carlos Ramírez";
    private final String auditorio4 = "Auditorio Adalberto Navarro";
    private final String auditorio5 = "Sala de Juicios Orales Mariano Otero";
    private final int ELIMINAR_EVENTO = 4;
    private Context mContext;

    public EventosAdaptador(List<Eventos> eventos, Context context) {
        this.eventos = eventos;
        mContext = context;
    }

    @Override
    public EventosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //return null;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_eventos, parent, false);
        return new EventosViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final EventosViewHolder eventosViewHolder , int position) {
        final Eventos evento = eventos.get(position);
        eventosViewHolder.titulo_evento.setText(evento.getTitulo());
        eventosViewHolder.nombre_org.setText(evento.getNombreOrganizador());
        eventosViewHolder.auditorio.setText(nombreAuditorio(evento.getAuditorio()));
        eventosViewHolder.horario.setText(horasATetxto(Integer.parseInt(evento.getHoraInicial().replaceAll("[^0-9]+",""))) + " - " + horasATetxto(Integer.parseInt(evento.getHoraFinal().replaceAll("[^0-9]+",""))));
        eventosViewHolder.contenedor.setCardBackgroundColor(evento.getFondo());

        eventosViewHolder.contenedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DialogInfoEventosHelzio.class);
                intent.putExtra("EVENTO", evento);
                intent.putExtra("POSITION", eventosViewHolder.getAdapterPosition());
                final Rect startBounds = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                ChangeBoundBackground2.addExtras(intent, getViewBitmap(view), startBounds);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, view, "fondo");
                ((Activity) mContext).startActivityForResult(intent, ELIMINAR_EVENTO, options.toBundle());
            }
        });

    }

    private String horasATetxto(int numero) {
        String am_pm, st_min, st_hora;

        int hora = (numero / 2) + 7;
        if (hora > 12) {
            hora = hora - 12;
            am_pm = " PM";
        } else {
            am_pm = " AM";
        }

        if (hora < 10) {
            st_hora = "0" + hora;
        } else {
            st_hora = "" + hora;
        }

        if (numero % 2 == 0) {
            st_min = "00";
        } else {
            st_min = "30";
        }

        return st_hora + ":" + st_min + am_pm;

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

    private String nombreAuditorio(String numero) {
        String st = "";
        switch (numero) {
            case "1":
                st = auditorio1;
                break;
            case "2":
                st = auditorio2;
                break;
            case "3":
                st = auditorio3;
                break;
            case "4":
                st = auditorio4;
                break;
            case "5":
                st = auditorio5;
                break;
        }
        return st;
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    public static class EventosViewHolder extends RecyclerView.ViewHolder{
        private TextView titulo_evento, nombre_org, auditorio, horario;
        private CardView contenedor;

        public EventosViewHolder(View itemView) {
            super(itemView);
            titulo_evento = (TextView) itemView.findViewById(R.id.tv_titulo);
            nombre_org = (TextView) itemView.findViewById(R.id.tv_organizador);
            auditorio = (TextView) itemView.findViewById(R.id.tv_auditorio);
            horario = (TextView) itemView.findViewById(R.id.tv_horario);
            contenedor = (CardView) itemView.findViewById(R.id.boton_eventos);
        }
    }

    public void removeItemAtPosition(int position) {
        eventos.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, eventos.size());
    }

}
