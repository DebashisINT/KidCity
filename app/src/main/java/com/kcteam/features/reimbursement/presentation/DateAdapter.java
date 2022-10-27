package com.kcteam.features.reimbursement.presentation;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kcteam.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {

    private Context mContext;
    private List<Date> dateList;
    private onPetSelectedListener dateItemClickListener;
    private int mSelectedPosition = 0;
    private boolean isFromApplyClass;

    public DateAdapter(Context context, boolean isFromApplyClass, onPetSelectedListener _dateItemClickListener) {
        mContext = context;
        dateList = new ArrayList<>();
        dateItemClickListener = _dateItemClickListener;
        this.isFromApplyClass = isFromApplyClass;
    }

    public void refreshAdapter(List<Date> _dateList) {
        dateList.clear();
        dateList.addAll(_dateList);
        notifyDataSetChanged();
    }

    public void refreshAdapter(List<Date> _dateList, int selectedPosition) {
        mSelectedPosition = selectedPosition;
        dateList.clear();
        dateList.addAll(_dateList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_date_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (position == mSelectedPosition) {
            holder.clDateParent.setBackgroundResource(R.drawable.layerlist_date_selected);
            holder.tvDateText.setTextColor(mContext.getResources().getColor(R.color.date_text_selected_color));
            holder.tvMonthText.setTextColor(mContext.getResources().getColor(R.color.date_month_selected_color));
        } else {
            holder.clDateParent.setBackgroundResource(R.drawable.layerlist_date_normal);
            holder.tvDateText.setTextColor(mContext.getResources().getColor(R.color.date_text_unselected_color));
            holder.tvMonthText.setTextColor(mContext.getResources().getColor(R.color.date_month_unselected_color));
        }


        holder.clDateParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFromApplyClass) {
                    mSelectedPosition = position;
                    if (dateItemClickListener != null) {
                        dateItemClickListener.onDateItemClick(position);
                    }
                    notifyDataSetChanged();
                }
            }
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
        String formattedDate = dateFormat.format(dateList.get(position));
        String formattedMonth = monthFormat.format(dateList.get(position));
        holder.tvDateText.setText("" + formattedDate);
        holder.tvMonthText.setText("" + formattedMonth);


    }

    @Override
    public int getItemCount() {
        return dateList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View clDateParent;
        TextView tvDateText, tvMonthText;

        public ViewHolder(View itemView) {
            super(itemView);
            clDateParent = itemView.findViewById(R.id.clDateParent);
            tvDateText = itemView.findViewById(R.id.tvDateText);
            tvMonthText = itemView.findViewById(R.id.tvMonthText);

        }
    }

    public interface onPetSelectedListener {
        void onDateItemClick(int pos);
    }
}
