package layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.gcubos.android.interfaz.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import eMail.Mail;

public class CroquisDrawFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Mail SeMail;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CroquisDrawFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CroquisDrawFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CroquisDrawFragment newInstance(String param1, String param2) {
        CroquisDrawFragment fragment = new CroquisDrawFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        float fLeft = 0, fTop = 0, fRight = 0, fBottom = 0;
        float DfLeft = 0, DfTop = 0, DfRight = 0, DfBottom = 0;
        float MLargo = 0, MAlto = 0, RLargo = 0, RAlto = 0, DLargo = 0, DAlto = 0;
        float AreaPed=0;
        ;
        int Div = 5, HTop = 100;
        String TipoTela = "";
        Bitmap bg = Bitmap.createBitmap(500, 1500, Bitmap.Config.ARGB_8888);
        JSONArray JOArr;
        JSONObject JOCroquis, JOE;
        Object JObjArr;
        boolean BFist = false;
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_croquis_draw, container, false);

        Paint mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);

        Paint TPaint = new Paint();
        TPaint.setColor(Color.BLACK);
        TPaint.setTextSize(12);
        TPaint.setStyle(Paint.Style.FILL);
        TPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        Canvas canvas = new Canvas(bg);

        try {
            JSONObject Json = new JSONObject(mParam1);
            JOCroquis = Json.getJSONObject("Procesa");
            JObjArr = JOCroquis.get("Croquis");

            if (JObjArr instanceof JSONArray) {
                JOArr = (JSONArray) JObjArr;
                for (int i = 0; i < JOArr.length(); i++) {
                    JOE = JOArr.getJSONObject(i);
                    if (mParam2.equals(JOE.getString("@Peca"))) {
                        if (!BFist) {
                            BFist = true;
                            RLargo = Float.parseFloat(JOE.getString("@LarguraRetalho").replace(",", "."));
                            RAlto = Float.parseFloat(JOE.getString("@AlturaRetalho").replace(",", "."));
                            DLargo = RLargo;
                            DAlto = RAlto;
                            MLargo = Float.parseFloat(JOE.getString("@LarguraMesa").replace(",", "."));
                            MAlto = 5000;  //Float.parseFloat(JOE.getString("@ComprimentoMesa").replace(",","."));
                            if (RLargo > MLargo) RLargo = MLargo;
                            if (RAlto > MAlto) RAlto = MAlto;
                            canvas.drawRect(0, HTop / Div, RLargo / Div, (RAlto + HTop) / Div, mPaint);
                            canvas.drawText(Float.toString(DLargo), (RLargo / Div) / 2, 20, TPaint);
                            canvas.drawText(Float.toString(DAlto), RLargo / Div, (RAlto / Div) / 2, TPaint);
                            TextView TR1 = (TextView) view.findViewById(R.id.TxtR1);
                            TR1.setText(JOE.getString("@Componente") + " Lote:" + JOE.getString("@Bloco") + " Tirada:" + JOE.getString("@Puxada"));
                            TextView TR2 = (TextView) view.findViewById(R.id.TxtR2);
                            TR2.setText("Color:" + JOE.getString("@Cor"));
                            TextView TR3 = (TextView) view.findViewById(R.id.TxtR3);
                            if (JOE.getString("@TipoTela").equals("0")) TipoTela = "Rollo Nuevo:";
                            if (JOE.getString("@TipoTela").equals("1"))
                                TipoTela = "Rollo Iniciado:";
                            if (JOE.getString("@TipoTela").equals("2")) TipoTela = "Retazo:";
                            TR3.setText(TipoTela + JOE.getString("@Peca") + " Lote:" + JOE.getString("@InventBatchID"));
                            TextView TR4 = (TextView) view.findViewById(R.id.TxtR4);
                            TR4.setText("Escaño:" + JOE.getString("@NomeEscaninho"));
                        }
                        ArrayList XarrayList = new ArrayList();
                        ArrayList YarrayList = new ArrayList();
                        XarrayList.add(Float.parseFloat(JOE.getString("@AX")));
                        YarrayList.add(Float.parseFloat(JOE.getString("@AY")));
                        XarrayList.add(Float.parseFloat(JOE.getString("@BX")));
                        YarrayList.add(Float.parseFloat(JOE.getString("@BY")));
                        XarrayList.add(Float.parseFloat(JOE.getString("@CX")));
                        YarrayList.add(Float.parseFloat(JOE.getString("@CY")));
                        XarrayList.add(Float.parseFloat(JOE.getString("@DX")));
                        YarrayList.add(Float.parseFloat(JOE.getString("@DY")));
                        Collections.sort(XarrayList);
                        Collections.sort(YarrayList);

                        fLeft = (float) XarrayList.get(0);
                        DfLeft = fLeft;
                        if (fLeft > MLargo) fLeft = MLargo;
                        if (fLeft > 0) fLeft = fLeft / Div;

                        fRight = (float) XarrayList.get(XarrayList.size() - 1);
                        DfRight = fRight - DfLeft;
                        if (fRight > MLargo) fRight = MLargo;
                        if (fRight > 0) fRight = fRight / Div;

                        fTop = (float) YarrayList.get(0);
                        DfTop = fTop;
                        fTop += HTop;
                        if (fTop > MAlto) fTop = MAlto;
                        fTop = fTop / Div;

                        fBottom = (float) YarrayList.get(YarrayList.size() - 1);
                        DfBottom = fBottom - DfTop;
                        if (fBottom > MAlto) fBottom = MAlto;
                        fBottom += HTop;
                        fBottom = fBottom / Div;

                        canvas.drawText(Float.toString(DfRight), fLeft + ((DfRight / Div) / 2), fTop + 20, TPaint);
                        if (DfBottom > MAlto)
                            canvas.drawText(Float.toString(DfBottom), (fRight - 40), fTop + ((fBottom / Div) / 2), TPaint);
                        else
                            canvas.drawText(Float.toString(DfBottom), (fRight - 40), fTop + ((DfBottom / Div) / 2), TPaint);

                        switch (JOE.getString("@TipoRegistro")) {
                            case "CONSUMO":
                                canvas.drawText(JOE.getString("@WoNbr"), fLeft, fTop + ((DfBottom / Div) / 2), TPaint);
                                canvas.drawText(JOE.getString("@WCP"), fLeft, fTop + 20 + ((DfBottom / Div) / 2), TPaint);
                                canvas.drawText(JOE.getString("@Pedido") + " - " + JOE.getString("@Item"), fLeft, fTop + 40 + ((DfBottom / Div) / 2), TPaint);
                                AreaPed += Float.parseFloat(JOE.getString("@LarguraPedido").replace(",", "."))/1000 * Float.parseFloat(JOE.getString("@AlturaPedido").replace(",", "."))/1000;
                                break;
                            case "SOBRA":
                                if (JOE.getString("@Desperdicio").equals("False")) {
                                    if (DfBottom > MAlto)
                                        canvas.drawText("Ret. " + JOE.getString("@PecaGerada"), fLeft, fTop - 20 + ((fBottom / Div) / 2), TPaint);
                                    else
                                        canvas.drawText("Ret. " + JOE.getString("@PecaGerada"), fLeft, fTop - 20 + ((DfBottom / Div) / 2), TPaint);
                                } else {
                                    if (DfBottom > MAlto)
                                        canvas.drawText("Desp.", fLeft, fTop - 20 + ((fBottom / Div) / 2), TPaint);
                                    else
                                        canvas.drawText("Desp.", fLeft, fTop - 20 + ((DfBottom / Div) / 2), TPaint);
                                }
                                break;
                        }
                        TextView TR5 = (TextView) view.findViewById(R.id.TxtR5);
                        TR5.setText("Area Retazo " + (Float.parseFloat(JOE.getString("@LarguraRetalho").replace(",", "."))/1000) * (Float.parseFloat(JOE.getString("@AlturaRetalho").replace(",", "."))/1000) + " - Area Pedidos " + AreaPed);
                        TextView TR6 = (TextView) view.findViewById(R.id.TxtR6);
                        TR6.setText("Sobra(m2) " + (((Float.parseFloat(JOE.getString("@LarguraRetalho").replace(",", "."))/1000) * (Float.parseFloat(JOE.getString("@AlturaRetalho").replace(",", "."))/1000)) - AreaPed + " - Perdida(m2) " + Float.parseFloat(JOE.getString("@M2PerdaRetalho").replace(",", ".")) + " (" + (Float.parseFloat(JOE.getString("@M2PerdaRetalho").replace(",", "."))*100)/AreaPed  +"%)"));
                        canvas.drawRect(fLeft, fTop, fRight, fBottom, mPaint);
                    }
                }
            } else if (JObjArr instanceof JSONObject) {
                JOE = (JSONObject) JObjArr;
            }
            ImageView imv = (ImageView) view.findViewById(R.id.ImgCroq);
            imv.setImageBitmap(bg);
        } catch (JSONException e) {
            SendError(e, "Error. onCreate CroquisDrawFragment");
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteractionCF(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteractionCF(Uri uri);
    }

    private void SendError(Exception e, String Subject) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        SeMail.setSubject(Subject);
        SeMail.setBody(errors.toString());
        try {
            SeMail.send();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}