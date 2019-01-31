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

public class ScheduleOverviewFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    SharedPreferences sharedPreferences;

    String[] days;

    public ScheduleOverviewFragment() {
        //"Required empty public constructor" - Android
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ScheduleOverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScheduleOverviewFragment newInstance() {
        ScheduleOverviewFragment fragment = new ScheduleOverviewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        days = getMain().getWeek();

        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_schedule_overview, container, false);

        buildSchedule((TableLayout) v.findViewById(R.id.table));

        // Inflate the layout for this fragment
        return v;
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
        void onFragmentInteraction(Uri uri);
    }

    private MainSchedule getMain() {
        return new MainSchedule();
    }

    private void buildSchedule(TableLayout table) {
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
