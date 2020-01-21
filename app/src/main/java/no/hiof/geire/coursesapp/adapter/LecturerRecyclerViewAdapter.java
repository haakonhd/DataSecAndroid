package no.hiof.geire.coursesapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import no.hiof.geire.coursesapp.R;
import no.hiof.geire.coursesapp.model.Foreleser;

public class LecturerRecyclerViewAdapter extends RecyclerView.Adapter<LecturerRecyclerViewAdapter.ViewHolder>{

    private List<Foreleser> mData;
    private LayoutInflater mInflater;
    private LecturerRecyclerViewAdapter.ItemClickListener mClickListener;

    // data is passed into the constructor
    public LecturerRecyclerViewAdapter(Context context, List<Foreleser> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public LecturerRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.lecturer_item, parent, false);
        return new LecturerRecyclerViewAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(LecturerRecyclerViewAdapter.ViewHolder holder, int position) {
        Foreleser lecturer = mData.get(position);
        holder.lecturerNameTextView.setText(lecturer.getNavn());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView lecturerNameTextView;

        ViewHolder(View itemView) {
            super(itemView);
            lecturerNameTextView = itemView.findViewById(R.id.lecturerNameTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public Foreleser getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(LecturerRecyclerViewAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
