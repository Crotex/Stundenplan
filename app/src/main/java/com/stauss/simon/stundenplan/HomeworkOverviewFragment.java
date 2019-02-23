package com.stauss.simon.stundenplan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeworkOverviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeworkOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeworkOverviewFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    RecyclerView homeworkList;

    List<String> homework;

    boolean sortBySubject;

    public HomeworkOverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeworkOverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeworkOverviewFragment newInstance() {
        return new HomeworkOverviewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sortBySubject = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_homework_overview, container, false);

        homework = getMain().getHomework();

        Collections.sort(homework, new Comparator<String>() {
            @Override
            public int compare(String h1, String h2) {
                // -1 - less than,
                // 1 - greater than,
                // 0 - equal,
                // all inversed for descending

                String[] part1 = h1.split(getMain().homeworkSubregex);
                String[] part2 = h2.split(getMain().homeworkSubregex);

                if(!sortBySubject) {
                    Date date1, date2;
                    DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

                    try {
                        date1 = format.parse(part1[2]);
                        date2 = format.parse(part2[2]);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }

                    if(date1.after(date2)) {
                        return 1;
                    } else if (date1.before(date2)){
                        return -1;
                    } else {
                        return 0;
                    }
                } else{
                    return part1[0].compareTo(part2[0]);
                }
            }
        });

        if(homework.size() == 0) {
            v.findViewById(R.id.noHomework).setVisibility(View.VISIBLE);
            v.findViewById(R.id.deleteHomework).setVisibility(View.INVISIBLE);
        } else {
            homeworkList = v.findViewById(R.id.list);
            homeworkList.setLayoutManager(new LinearLayoutManager(getContext()));
            homeworkList.setAdapter(new ListAdapter(homework));
        }

        v.findViewById(R.id.deleteHomework).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                getMain().clearHomework();
                                Toast.makeText(getContext(), R.string.homework_cleared, Toast.LENGTH_SHORT).show();
                                reloadFragment();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.ask_clear_homework).setPositiveButton("Ja", dialogClickListener)
                        .setNegativeButton("Nein", dialogClickListener).show();

            }
        });





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

    private Main getMain() {
        Main main = new Main();
        main.sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        main.prefEdit = main.sharedPreferences.edit();
        return main;
    }

    private void reloadFragment() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}
