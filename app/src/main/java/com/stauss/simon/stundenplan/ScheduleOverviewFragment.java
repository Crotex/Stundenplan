package com.stauss.simon.stundenplan;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScheduleOverviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScheduleOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleOverviewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefEdit;

    String[] days;

    MainSchedule main;

    public ScheduleOverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScheduleOverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScheduleOverviewFragment newInstance(String param1, String param2) {
        ScheduleOverviewFragment fragment = new ScheduleOverviewFragment();
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

        days = getMain().getWeek();

        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        prefEdit = sharedPreferences.edit();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_schedule_overview, container, false);

        buildTable((TableLayout) v.findViewById(R.id.table));
        // Inflate the layout for this fragment
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private MainSchedule getMain() {
        main = new MainSchedule();
        return main;
    }

    private void buildTable(TableLayout table) {
        TableRow row;
        int c;
        for (int i = 1; i <= 11; i++) {
            c = 0;

            row = new TableRow(getContext());
            row.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            row.setPadding(0, 5, 0, 5);

            TextView h = new TextView(getContext());
            h.setText("" + i);
            h.setGravity(Gravity.CENTER);
            row.addView(h, new TableRow.LayoutParams(c));
            c++;

            TextView subMo = new TextView(getContext());
            subMo.setText(sharedPreferences.getString( days[c] + i + "s", "-"));
            subMo.setGravity(Gravity.CENTER);
            row.addView(subMo, new TableRow.LayoutParams(c));
            c++;

            TextView subDi = new TextView(getContext());
            subDi.setText(sharedPreferences.getString( days[c] + i + "s", "-"));
            subDi.setGravity(Gravity.CENTER);
            row.addView(subDi, new TableRow.LayoutParams(c));
            c++;

            TextView subMi = new TextView(getContext());
            subMi.setText(sharedPreferences.getString( days[c] + i + "s", "-"));
            subMi.setGravity(Gravity.CENTER);
            row.addView(subMi, new TableRow.LayoutParams(c));
            c++;

            TextView subDo = new TextView(getContext());
            subDo.setText(sharedPreferences.getString( days[c] + i + "s", "-"));
            subDo.setGravity(Gravity.CENTER);
            row.addView(subDo, new TableRow.LayoutParams(c));
            c++;

            TextView subFr = new TextView(getContext());
            subFr.setText(sharedPreferences.getString( days[c] + i + "s", "-"));
            subFr.setGravity(Gravity.CENTER);
            row.addView(subFr, new TableRow.LayoutParams(c));

            table.addView(row);
        }
    }
}
