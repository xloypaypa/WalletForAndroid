package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.xsu.walletforandroid.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.view.View.inflate;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MoneyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MoneyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoneyFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private TableLayout tableLayout;

    public MoneyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MoneyFragment.
     */
    public static MoneyFragment newInstance() {
        return new MoneyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_money, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.tableLayout = (TableLayout) this.getActivity().findViewById(R.id.moneyTable);
        try {
            setDataOnTable(new JSONArray("[]"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setMoneyList(JSONArray jsonArray) {
        try {
            setDataOnTable(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
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

    private void setDataOnTable(JSONArray jsonArray) throws JSONException {
        this.tableLayout.removeAllViews();
        addTitle();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            TableRow tableRow = (TableRow) inflate(getContext(), R.layout.tablerow_money_and_budget, null);
            TextView typeName = (TextView) tableRow.findViewById(R.id.typename);
            TextView value = (TextView) tableRow.findViewById(R.id.value);

            typeName.setText(jsonObject.getString("typename"));
            value.setText(jsonObject.getString("value"));

            this.tableLayout.addView(tableRow);
        }
    }

    private void addTitle() {
        TableRow tableRow = (TableRow) inflate(getContext(), R.layout.tablerow_money_and_budget, null);
        TextView typeName = (TextView) tableRow.findViewById(R.id.typename);
        TextView value = (TextView) tableRow.findViewById(R.id.value);

        typeName.setText(R.string.typename);
        value.setText(R.string.value);

        this.tableLayout.addView(tableRow);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
