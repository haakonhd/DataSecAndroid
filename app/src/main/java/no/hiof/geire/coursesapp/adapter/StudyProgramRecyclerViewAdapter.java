package no.hiof.geire.coursesapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import no.hiof.geire.coursesapp.R;
import no.hiof.geire.coursesapp.model.Studieretning;

public class StudyProgramRecyclerViewAdapter extends RecyclerView.Adapter<StudyProgramRecyclerViewAdapter.ViewHolder> {

    private List<Studieretning> mData;
    private LayoutInflater mInflater;
    private StudyProgramRecyclerViewAdapter.ItemClickListener mClickListener;

    // data is passed into the constructor
    public StudyProgramRecyclerViewAdapter(Context context, List<Studieretning> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.study_program_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Studieretning studyProgram = mData.get(position);
        holder.studyProgramTextView.setText(studyProgram.getStudieretningNavn());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView studyProgramTextView;

        ViewHolder(View itemView) {
            super(itemView);
            studyProgramTextView = itemView.findViewById(R.id.studyProgramTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public Studieretning getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
