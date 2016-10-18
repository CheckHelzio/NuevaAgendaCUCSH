package ccv.checkhelzio.nuevaagendacucsh.ui;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by check on 13/10/2016.
 */

public class SwipeHelper extends ItemTouchHelper.SimpleCallback {

    FechasAdaptador adaptador;

    public SwipeHelper(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    public SwipeHelper(FechasAdaptador adaptador) {
        super(ItemTouchHelper.LEFT, ItemTouchHelper.RIGHT);
        this.adaptador = adaptador;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adaptador.removeItemAtPosition(viewHolder.getAdapterPosition());
    }
}
