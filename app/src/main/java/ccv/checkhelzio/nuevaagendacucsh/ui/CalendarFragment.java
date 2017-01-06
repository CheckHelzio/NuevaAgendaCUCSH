package ccv.checkhelzio.nuevaagendacucsh.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import ccv.checkhelzio.nuevaagendacucsh.R;
import ccv.checkhelzio.nuevaagendacucsh.transitions.ChangeBoundBackground;

import static ccv.checkhelzio.nuevaagendacucsh.ui.Principal.calendarioActualizarDiasMes;
import static ccv.checkhelzio.nuevaagendacucsh.ui.Principal.irHoyAño;
import static ccv.checkhelzio.nuevaagendacucsh.ui.Principal.irHoyDiaSemana;
import static ccv.checkhelzio.nuevaagendacucsh.ui.Principal.irHoyMes;
import static ccv.checkhelzio.nuevaagendacucsh.ui.Principal.irHoyNumeroDiaMes;


public class CalendarFragment extends Fragment {
    int fragVal;
    private final int HELZIO_ELIMINAR_EVENTO = 4;
    static CalendarFragment init(int val) {
        CalendarFragment truitonFrag = new CalendarFragment();
        // Supply val input as an argument.
        Bundle args = new Bundle();
        args.putInt("val", val);
        truitonFrag.setArguments(args);
        return truitonFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragVal = getArguments() != null ? getArguments().getInt("val") : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.aba_a, container,
                false);

        calendarioActualizarDiasMes.set(2016, 0, 1);
        calendarioActualizarDiasMes.set(Calendar.MONTH, fragVal);
        actualizarDiasDelMes((GridLayout) layout.findViewById(R.id.grid), (LinearLayout) layout.findViewById(R.id.aba_b));
        Log.v("FRAGMENT", "FRAGMENT: " + fragVal);
        return layout;
    }


    private void actualizarDiasDelMes(GridLayout grid_layout, LinearLayout aba_b) {
        final int diasemana = obtenerDiaSemana(calendarioActualizarDiasMes.get(Calendar.DAY_OF_WEEK));
        int actuDias_Año = calendarioActualizarDiasMes.get(Calendar.YEAR);
        int dia_inicial_del_mes;

        if (actuDias_Año == 2016) {
            dia_inicial_del_mes = calendarioActualizarDiasMes.get(Calendar.DAY_OF_YEAR);
        } else {
            dia_inicial_del_mes = calendarioActualizarDiasMes.get(Calendar.DAY_OF_YEAR);
            for (int x = 2016; x < actuDias_Año; x++) {
                Calendar cr = Calendar.getInstance();
                cr.set(x, 11, 31);
                dia_inicial_del_mes += cr.get(Calendar.DAY_OF_YEAR);
            }
        }

        for (int y = 0; y < diasemana; y++) {
            String st_nd = "";
            ((TextView) ((RelativeLayout) ((LinearLayout) grid_layout.getChildAt(y)).getChildAt(0)).getChildAt(0)).setText(st_nd);
            ((TextView) ((LinearLayout) grid_layout.getChildAt(y)).getChildAt(1)).setText(st_nd);
            ((TextView) ((LinearLayout) grid_layout.getChildAt(y)).getChildAt(2)).setText(st_nd);
            ((TextView) ((LinearLayout) grid_layout.getChildAt(y)).getChildAt(3)).setText(st_nd);
            ((LinearLayout) grid_layout.getChildAt(y)).getChildAt(1).setBackgroundColor(Color.TRANSPARENT);
            ((LinearLayout) grid_layout.getChildAt(y)).getChildAt(2).setBackgroundColor(Color.TRANSPARENT);
            ((LinearLayout) grid_layout.getChildAt(y)).getChildAt(3).setBackgroundColor(Color.TRANSPARENT);
        }

        for (int x = 1; x <= calendarioActualizarDiasMes.getActualMaximum(Calendar.DAY_OF_MONTH); x++) {
            // NUMERO DE DIA DEL MES
            final String st_nd = "" + x;
            ((TextView) ((RelativeLayout) ((LinearLayout) grid_layout.getChildAt(x - 1 + diasemana)).getChildAt(0)).getChildAt(0)).setText(st_nd);

            final ArrayList<Eventos> lista_pequeña_eventos = new ArrayList<>();
            for (Eventos evento_suelto : Principal.lista_eventos){
                if (Integer.parseInt(evento_suelto.getFecha().replaceAll("[^0-9]+","")) == dia_inicial_del_mes - 1 + x && !evento_suelto.getStatusEvento().equals("X") && filtro(evento_suelto.getAuditorio())){
                    lista_pequeña_eventos.add(evento_suelto);
                }
            }

            int i = 1;
            for (Eventos eve : lista_pequeña_eventos) {
                if (i == 1 || i ==2) {
                    ((TextView) ((LinearLayout) grid_layout.getChildAt(x - 1 + diasemana)).getChildAt(i)).setText(eve.getTitulo());
                    ((LinearLayout) grid_layout.getChildAt(x - 1 + diasemana)).getChildAt(i).setBackgroundResource(getFondo(eve.getAuditorio()));

                } else if (i == 3) {
                    int cuantos = lista_pequeña_eventos.size() - 2;
                    String cu = "" + cuantos + " más";
                    ((TextView) ((LinearLayout) grid_layout.getChildAt(x - 1 + diasemana)).getChildAt(i)).setText(cu);
                    break;
                }
                i++;
            }

            final int finalX = x;
            final int finalDia_inicial_del_mes = dia_inicial_del_mes;
            grid_layout.getChildAt(x - 1 + diasemana).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // INTENT A LA LISTA DE EVENTOS
                    Intent intent = new Intent(getActivity(), DialogListaEventosHelzio.class);
                    // PASAMOS EL NUMERO DE DIA
                    intent.putExtra("DIA_MES", st_nd);

                    // PASAMOS EL NUMERO DE DIA DESDE EL 2016
                    intent.putExtra("DIA_AÑO", (finalDia_inicial_del_mes - 1 + finalX));

                    // PASAMOS EL NOMBRE DEL DIA
                    String st_dia_semana = "" + (finalX - 1 + diasemana);
                    intent.putExtra("NOMBRE_DIA", view.getResources().getResourceEntryName(view.getId()));

                    // PASAMOS LA LISTA DE EVENTOS
                    intent.putExtra("LISTA_EVENTOS", lista_pequeña_eventos);

                    //BOLEAN PARA SABER SI ESTAMOS CLICKEANDO EL DIA DE HOY Y COLOREAR EL TEXTO EN EL DIALOG
                    if (fragVal == irHoyMes && finalX == irHoyNumeroDiaMes) {
                        intent.putExtra("ES_HOY", true);
                    } else {
                        intent.putExtra("ES_HOY", false);
                    }

                    //BOOLEAN PARA SABER SI SE PUEDE REGISTRAR O NO
                    //SI ESTAMOS EN UN MES ANTERIOR AL ACTUAL NO SE PUEDE REGISTRAR
                    if (fragVal < irHoyMes) {
                        intent.putExtra("REGISTRAR", false);
                    }
                    //SI ESTAMOS EN EL MISMO MES
                    else if (fragVal == irHoyMes) {
                        //SI EL DIA DEL MES ES ANTERIOR AL DIA DE HOY NO PODEMOS REGISTRAR
                        if (finalX < irHoyNumeroDiaMes) {
                            intent.putExtra("REGISTRAR", false);
                        }
                        //SI EL DIA DEL MES ES HOY NO SE PUEDE AGENDAR DESPUES DE LAS 6PM
                        else if (finalX == irHoyNumeroDiaMes) {
                            Calendar calendarioIrHoy = Calendar.getInstance();
                            if (calendarioIrHoy.get(Calendar.HOUR_OF_DAY) > 17) {
                                int hora = calendarioIrHoy.get(Calendar.HOUR_OF_DAY);
                                int minuto = calendarioIrHoy.get(Calendar.MINUTE);
                                if (hora == 18) {
                                    if (minuto < 30) {
                                        intent.putExtra("REGISTRAR", true);
                                    } else {
                                        intent.putExtra("REGISTRAR", false);
                                    }
                                } else {
                                    intent.putExtra("REGISTRAR", false);
                                }
                            } else {
                                intent.putExtra("REGISTRAR", true);
                            }
                        }
                        //SI ES DESPUES DE HOY SI SE PEUDE REGISTRAR
                        else {
                            intent.putExtra("REGISTRAR", true);
                        }
                    }
                    //SI ES EN UN MES FUTURO SE PUEDE REGISTRAR
                    else {
                        intent.putExtra("REGISTRAR", true);
                    }

                    final Rect startBounds = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                    ChangeBoundBackground.addExtras(intent, getViewBitmap(view), startBounds);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), view, "fondo");
                    startActivityForResult(intent, HELZIO_ELIMINAR_EVENTO, options.toBundle());
                }
            });
        }

        for (int x = calendarioActualizarDiasMes.getActualMaximum(Calendar.DAY_OF_MONTH) + diasemana; x <= 35; x++) {
            String st_nd = "";
            ((TextView) ((RelativeLayout) ((LinearLayout) grid_layout.getChildAt(x)).getChildAt(0)).getChildAt(0)).setText(st_nd);

            ((TextView) ((LinearLayout) grid_layout.getChildAt(x)).getChildAt(1)).setText(st_nd);
            ((TextView) ((LinearLayout) grid_layout.getChildAt(x)).getChildAt(2)).setText(st_nd);
            ((TextView) ((LinearLayout) grid_layout.getChildAt(x)).getChildAt(3)).setText(st_nd);
            ((LinearLayout) grid_layout.getChildAt(x)).getChildAt(1).setBackgroundColor(Color.TRANSPARENT);
            ((LinearLayout) grid_layout.getChildAt(x)).getChildAt(2).setBackgroundColor(Color.TRANSPARENT);
            ((LinearLayout) grid_layout.getChildAt(x)).getChildAt(3).setBackgroundColor(Color.TRANSPARENT);
        }

        if (calendarioActualizarDiasMes.get(Calendar.MONTH) == irHoyMes && irHoyAño == calendarioActualizarDiasMes.get(Calendar.YEAR)) {
            TextView tv_fondo_hoy = ((TextView) ((RelativeLayout) ((LinearLayout) grid_layout.getChildAt(diasemana - 1 + irHoyNumeroDiaMes)).getChildAt(0)).getChildAt(0));
            tv_fondo_hoy.setBackgroundResource(R.drawable.fondo_hoy);
            tv_fondo_hoy.setTextColor(Color.WHITE);
            ((TextView) aba_b.getChildAt(obtenerDiaSemana(irHoyDiaSemana))).setTextColor((Integer) getAcentColor());
        }

        int z = diasemana + calendarioActualizarDiasMes.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (z >= 36) {
            grid_layout.getChildAt(35).setVisibility(View.VISIBLE);
            grid_layout.getChildAt(36).setVisibility(View.VISIBLE);
            grid_layout.getChildAt(37).setVisibility(View.VISIBLE);
            grid_layout.getChildAt(38).setVisibility(View.VISIBLE);
            grid_layout.getChildAt(39).setVisibility(View.VISIBLE);
            grid_layout.getChildAt(40).setVisibility(View.VISIBLE);
            grid_layout.getChildAt(41).setVisibility(View.VISIBLE);
        } else {
            grid_layout.getChildAt(35).setVisibility(View.GONE);
            grid_layout.getChildAt(36).setVisibility(View.GONE);
            grid_layout.getChildAt(37).setVisibility(View.GONE);
            grid_layout.getChildAt(38).setVisibility(View.GONE);
            grid_layout.getChildAt(39).setVisibility(View.GONE);
            grid_layout.getChildAt(40).setVisibility(View.GONE);
            grid_layout.getChildAt(41).setVisibility(View.GONE);
        }
    }

    private boolean filtro(String auditorio) {
        boolean f = false;
        switch (auditorio) {
            case "1":
                f = Principal.filtro1;
                break;
            case "2":
                f = Principal.filtro2;
                break;
            case "3":
                f = Principal.filtro3;
                break;
            case "4":
                f = Principal.filtro4;
                break;
            case "5":
                f = Principal.filtro5;
                break;
        }
        return f;
    }

    private Integer obtenerDiaSemana(int i) {
        switch (i) {
            //domingo
            case 1:
                i = 6;
                break;
            //lunes
            case 2:
                i = 0;
                break;
            //martes
            case 3:
                i = 1;
                break;
            //miercoles
            case 4:
                i = 2;
                break;
            //jueves
            case 5:
                i = 3;
                break;
            //viernes
            case 6:
                i = 4;
                break;
            //sabado
            case 7:
                i = 5;
                break;
        }
        return i;
    }

    public Object getAcentColor() {
        int colorAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorAccent;
        } else {
            //Get colorAccent defined for AppCompat
            colorAttr = getResources().getIdentifier("colorAccent", "attr", getActivity().getPackageName());
        }
        TypedValue outValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
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

    private int getFondo(String trim) {
        int a = 0;
        switch (trim) {
            case "1":
                a = (R.drawable.fondo1);
                break;
            case "2":
                a = (R.drawable.fondo2);
                break;
            case "3":
                a = (R.drawable.fondo3);
                break;
            case "4":
                a = (R.drawable.fondo4);
                break;
            case "5":
                a = (R.drawable.fondo5);
                break;
        }
        return a;
    }

}