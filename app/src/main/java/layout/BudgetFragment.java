package layout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
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

import model.entity.BudgetEntity;

import static android.view.View.inflate;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnBudgetFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BudgetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BudgetFragment extends Fragment {

    private OnBudgetFragmentInteractionListener mListener;
    private TableLayout tableLayout;

    private List<BudgetEntity> budgetEntities = new ArrayList<>();

    public BudgetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BudgetFragment.
     */
    public static BudgetFragment newInstance() {
        return new BudgetFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_budget, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //noinspection ConstantConditions
        this.tableLayout = (TableLayout) this.getView().findViewById(R.id.budgetTable);
        setDataOnTable(this.budgetEntities);

        Button addBudgetButton = (Button) this.getActivity().findViewById(R.id.addBudgetButton);
        Button transferBudgetButton = (Button) this.getActivity().findViewById(R.id.transferBudgetButton);
        Button removeBudgetButton = (Button) this.getActivity().findViewById(R.id.removeBudgetButton);

        addBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();

                View dialogView = inflater.inflate(R.layout.dialog_add_money_or_budget, null);
                final EditText typenameEditText = (EditText) dialogView.findViewById(R.id.typenameEditText);
                final EditText valueEditText = (EditText) dialogView.findViewById(R.id.valueEditText);

                builder.setTitle("add budget")
                        .setView(dialogView)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                String typename = typenameEditText.getText().toString();
                                double value = Double.parseDouble(valueEditText.getText().toString());
                                mListener.onAddBudgetFragmentInteraction(typename, value);
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

        transferBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();

                View dialogView = inflater.inflate(R.layout.dialog_transfer_money_or_budget, null);
                final Spinner fromSpinner = (Spinner) dialogView.findViewById(R.id.fromSpinner);
                final Spinner toSpinner = (Spinner) dialogView.findViewById(R.id.toSpinner);
                final EditText valueEditText = (EditText) dialogView.findViewById(R.id.valueEditText);

                String[] budgetNames = new String[budgetEntities.size()];
                for (int i = 0; i < budgetEntities.size(); i++) {
                    budgetNames[i] = budgetEntities.get(i).getTypename();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, budgetNames);
                fromSpinner.setAdapter(adapter);
                toSpinner.setAdapter(adapter);

                builder.setTitle("add money")
                        .setView(dialogView)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                String from = fromSpinner.getSelectedItem().toString();
                                String to = toSpinner.getSelectedItem().toString();
                                double value = Double.parseDouble(valueEditText.getText().toString());
                                mListener.onTransferBudgetFragmentInteraction(from, to, value);
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

        removeBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();

                View dialogView = inflater.inflate(R.layout.dialog_remove_money_or_budget, null);
                final Spinner typenameSpinner = (Spinner) dialogView.findViewById(R.id.typenameSpriner);

                String[] budgetNames = new String[budgetEntities.size()];
                for (int i = 0; i < budgetEntities.size(); i++) {
                    budgetNames[i] = budgetEntities.get(i).getTypename();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, budgetNames);
                typenameSpinner.setAdapter(adapter);

                builder.setTitle("remove money")
                        .setView(dialogView)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                String removeItem = typenameSpinner.getSelectedItem().toString();
                                mListener.onRemoveBudgetFragmentInteraction(removeItem);
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

    public void setBudgetList(List<BudgetEntity> budgetEntities) {
        this.budgetEntities.clear();
        this.budgetEntities.addAll(budgetEntities);
        if (this.tableLayout != null) {
            setDataOnTable(budgetEntities);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBudgetFragmentInteractionListener) {
            mListener = (OnBudgetFragmentInteractionListener) context;
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

    private void setDataOnTable(List<BudgetEntity> budgetEntities) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        this.tableLayout.removeAllViews();
        addTitle();
        for (BudgetEntity budgetEntity : budgetEntities) {
            TableRow tableRow = (TableRow) inflate(getContext(), R.layout.tablerow_money_and_budget, null);
            TextView typeName = (TextView) tableRow.findViewById(R.id.typename);
            TextView value = (TextView) tableRow.findViewById(R.id.value);

            typeName.setText(budgetEntity.getTypename());
            value.setText(decimalFormat.format(budgetEntity.getValue()));

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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnBudgetFragmentInteractionListener {
        void onAddBudgetFragmentInteraction(String typename, double value);

        void onRemoveBudgetFragmentInteraction(String typename);

        void onTransferBudgetFragmentInteraction(String from, String to, double value);
    }
}
