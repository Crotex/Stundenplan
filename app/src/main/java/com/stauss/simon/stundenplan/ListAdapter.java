package com.stauss.simon.stundenplan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView subject;
        TextView dueDate;
        TextView description;
        CheckBox done;

        public ViewHolder(View v) {
            super(v);
            subject = v.findViewById(R.id.subject);
            dueDate = v.findViewById(R.id.dueDate);
            description = v.findViewById(R.id.description);
            done = v.findViewById(R.id.done);
            done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    removeItemAt(getAdapterPosition());
                }
            });
        }
    }

    private List<String> homework;
    private String subject, date, description;

    public ListAdapter(List<String> data) {
        homework = data;
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context c = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(c);

        View listItem = inflater.inflate(R.layout.homework_overview_item, viewGroup, false);

        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder viewHolder, int i) {
        splitListItems(i);

        TextView s = viewHolder.subject;
        s.setText(subject);

        TextView d = viewHolder.dueDate;
        d.setText(date);

        TextView desc = viewHolder.description;
        desc.setText(description);
    }

    @Override
    public int getItemCount() {
        return homework.size();
    }

    private Main getMain() {
        return new Main();
    }

    private void splitListItems(int index) {
        String listItem = homework.get(index);
        List<String> sublist = getMain().stringToList(listItem, getMain().homeworkSubregex);
        subject = sublist.get(0);
        description = sublist.get(1);
        date = sublist.get(2);
    }

    private void removeItemAt(int position) {
        homework.remove(position);
        getMain().saveHomework(homework);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, homework.size());
    }
}
