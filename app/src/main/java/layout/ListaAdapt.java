package layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.gcubos.android.interfaz.R;
import java.util.ArrayList;

public class ListaAdapt  extends BaseAdapter {
    private ArrayList<?> entradas;
    private int R_layout_IdView;
    private Context contexto;

    ListaAdapt(Context contexto, ArrayList<?> entradas) {
        super();
        this.contexto = contexto;
        this.entradas = entradas;
    }

    @Override
    public View getView(int posicion, View view, ViewGroup pariente) {
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate( R.layout.elemento_lista_layout, null);
        }
        String onEntrada = entradas.get(posicion).toString();
        TextView TVtexto = (TextView) view.findViewById(R.id.textView_titulo);
        if (TVtexto != null)
            TVtexto.setText(onEntrada);

        return view;
    }

    @Override
    public int getCount() {
        return entradas.size();
    }

    @Override
    public Object getItem(int posicion) {
        return entradas.get(posicion);
    }

    @Override
    public long getItemId(int posicion) {
        return posicion;
    }
}
