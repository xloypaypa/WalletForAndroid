package layout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.xsu.walletforandroid.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import model.entity.MoneyEntity;

import static android.view.View.inflate;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnMoneyFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MoneyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoneyFragment extends Fragment {

    private OnMoneyFragmentInteractionListener mListener;

    private TableLayout tableLayout;

    private List<MoneyEntity> moneyEntities = new ArrayList<>();

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

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.tableLayout = (TableLayout) this.getView().findViewById(R.id.moneyTable);

        Button addMoneyButton = (Button) this.getView().findViewById(R.id.addMoneyButton);
        Button removeMoneyButton = (Button) this.getView().findViewById(R.id.removeMoneyButton);

        setDataOnTable(moneyEntities);

        addMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();

                View dialogView = inflater.inflate(R.layout.dialog_add_money, null);
                final EditText typenameEditText = (EditText) dialogView.findViewById(R.id.typenameEditText);
                final EditText valueEditText = (EditText) dialogView.findViewById(R.id.valueEditText);

                builder.setTitle("add money")
                        .setView(dialogView)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                String typename = typenameEditText.getText().toString();
                                double value = Double.parseDouble(valueEditText.getText().toString());
                                mListener.onAddMoneyFragmentInteraction(typename, value);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            }
        });

        removeMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();

                View dialogView = inflater.inflate(R.layout.dialog_remove_money, null);
                final Spinner typenameSpinner = (Spinner) dialogView.findViewById(R.id.moneyTypeSpriner);

                String[] moneyNames = new String[moneyEntities.size()];
                for (int i = 0; i < moneyEntities.size(); i++) {
                    moneyNames[i] = moneyEntities.get(i).getTypename();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, moneyNames);
                typenameSpinner.setAdapter(adapter);

                builder.setTitle("remove money")
                        .setView(dialogView)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                String removeItem = typenameSpinner.getSelectedItem().toString();
                                mListener.onRemoveMoneyFragmentInteraction(removeItem);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            }
        });
    }

    public void setMoneyList(List<MoneyEntity> moneyEntities) {
        this.moneyEntities.clear();
        this.moneyEntities.addAll(moneyEntities);
        if (this.tableLayout != null) {
            setDataOnTable(this.moneyEntities);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMoneyFragmentInteractionListener) {
            mListener = (OnMoneyFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMoneyFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setDataOnTable(List<MoneyEntity> moneyEntities) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        this.tableLayout.removeAllViews();
        addTitle();
        for (MoneyEntity moneyEntity : moneyEntities) {
            TableRow tableRow = (TableRow) inflate(getContext(), R.layout.tablerow_money_and_budget, null);
            TextView typeName = (TextView) tableRow.findViewById(R.id.typename);
            TextView value = (TextView) tableRow.findViewById(R.id.value);

            typeName.setText(moneyEntity.getTypename());
            value.setText(decimalFormat.format(moneyEntity.getValue()));

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
    public interface OnMoneyFragmentInteractionListener {
        void onAddMoneyFragmentInteraction(String typename, double value);

        void onRemoveMoneyFragmentInteraction(String typename);
    }
}
