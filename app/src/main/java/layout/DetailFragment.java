package layout;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.xsu.walletforandroid.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import model.entity.DetailEntity;

import static android.view.View.inflate;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private TableLayout tableLayout;
    private List<DetailEntity> detailEntities;

    public DetailFragment() {
        this.detailEntities = new ArrayList<>();
    }

    public static DetailFragment newInstance() {
        return new DetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.tableLayout = (TableLayout) this.getActivity().findViewById(R.id.detailTable);

        Button updateButton = (Button) this.getActivity().findViewById(R.id.updateDetailButton);
        final EditText fromYearDate = (EditText) this.getActivity().findViewById(R.id.fromYearEditText);
        final EditText fromMonthDate = (EditText) this.getActivity().findViewById(R.id.fromMonthEditText);
        final EditText fromDayDate = (EditText) this.getActivity().findViewById(R.id.fromDayEditText);
        final EditText toYearDate = (EditText) this.getActivity().findViewById(R.id.toYearEditText);
        final EditText toMonthDate = (EditText) this.getActivity().findViewById(R.id.toMonthEditText);
        final EditText toDayDate = (EditText) this.getActivity().findViewById(R.id.toDayEditText);

        Calendar from = Calendar.getInstance();
        from.add(Calendar.MONTH, -1);

        Calendar to = Calendar.getInstance();
        to.add(Calendar.DAY_OF_MONTH, 1);

        fromYearDate.setText(String.format(Locale.getDefault(), "%d", from.get(Calendar.YEAR)));
        fromMonthDate.setText(String.format(Locale.getDefault(), "%d", from.get(Calendar.MONTH) + 1));
        fromDayDate.setText(String.format(Locale.getDefault(), "%d", from.get(Calendar.DAY_OF_MONTH)));
        toYearDate.setText(String.format(Locale.getDefault(), "%d", to.get(Calendar.YEAR)));
        toMonthDate.setText(String.format(Locale.getDefault(), "%d", to.get(Calendar.MONTH) + 1));
        toDayDate.setText(String.format(Locale.getDefault(), "%d", to.get(Calendar.DAY_OF_MONTH)));

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    String fromString = fromYearDate.getText().toString() + "-"
                            + fromMonthDate.getText().toString() + "-" + fromDayDate.getText().toString();
                    String toString = toYearDate.getText().toString() + "-"
                            + toMonthDate.getText().toString() + "-" + toDayDate.getText().toString();
                    Date from = simpleDateFormat.parse(fromString);
                    Date to = simpleDateFormat.parse(toString);
                    mListener.onUpdateDetailFragmentInteraction(from, to);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setDetailList(List<DetailEntity> detailEntities) {
        this.detailEntities.clear();
        if (detailEntities != null) {
            this.detailEntities.addAll(detailEntities);
        }
        if (this.tableLayout != null) {
            setDataOnTable(this.detailEntities);
        }
    }

    private void setDataOnTable(List<DetailEntity> detailEntities) {
        this.tableLayout.removeAllViews();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        addTitle();
        for (DetailEntity detailEntity : detailEntities) {
            TableRow tableRow = (TableRow) inflate(getContext(), R.layout.tablerow_detail, null);
            TextView date = (TextView) tableRow.findViewById(R.id.date);
            TextView event = (TextView) tableRow.findViewById(R.id.event);
            TextView eventParam = (TextView) tableRow.findViewById(R.id.eventParam);
            TextView rollBackParam = (TextView) tableRow.findViewById(R.id.rollBackParam);

            date.setText(simpleDateFormat.format(new Date(detailEntity.getDate())));
            event.setText(detailEntity.getEvent());
            eventParam.setText(detailEntity.getEventParam());
            rollBackParam.setText(detailEntity.getRollBackParam());

            this.tableLayout.addView(tableRow);
        }
    }

    private void addTitle() {
        TableRow tableRow = (TableRow) inflate(getContext(), R.layout.tablerow_detail, null);
        TextView date = (TextView) tableRow.findViewById(R.id.date);
        TextView event = (TextView) tableRow.findViewById(R.id.event);
        TextView eventParam = (TextView) tableRow.findViewById(R.id.eventParam);
        TextView rollBackParam = (TextView) tableRow.findViewById(R.id.rollBackParam);

        date.setText(R.string.date);
        event.setText(R.string.event);
        eventParam.setText(R.string.eventParam);
        rollBackParam.setText(R.string.rollBackParam);

        this.tableLayout.addView(tableRow);
    }

    public interface OnFragmentInteractionListener {
        void onUpdateDetailFragmentInteraction(Date from, Date to);
    }
}
