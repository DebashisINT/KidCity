package com.kcteam.features.TA;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kcteam.R;
import com.kcteam.app.domain.TaListDBModelEntity;

import java.util.ArrayList;

/**
 * Created by sayantan.sarkar on 1/11/17.
 */

public class ViewAllTAListRecyclerViewAdapter extends RecyclerView.Adapter<ViewAllTAListRecyclerViewAdapter.AttendanceFragmentViewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    private onScrollEndListener onScrollEndListener;
    private onItemClickListener mOnItemClickListener;
    private ArrayList<TaListDBModelEntity> mTaListArray;

    public ViewAllTAListRecyclerViewAdapter(Context context, ArrayList<TaListDBModelEntity> taListArray, onScrollEndListener onScrollEndListener, onItemClickListener monItemClickListener) {
        this.context = context;
        this.onScrollEndListener = onScrollEndListener;
        mTaListArray = taListArray;
        this.mOnItemClickListener = monItemClickListener;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public AttendanceFragmentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.inflater_ta_history_item, viewGroup, false);
        return new AttendanceFragmentViewHolder(view);
    }


    @Override
    public void onBindViewHolder(AttendanceFragmentViewHolder attendanceFragmentViewHolder, int position) {
        String status = "";
        if (position == 0 || position == 3 || position == 5) {
            status = "Pending";
        } else {
            status = "Approved";
        }
        if (position % 2 == 0)
            attendanceFragmentViewHolder.rcv_item_bg.setBackgroundColor(ContextCompat.getColor(context, R.color.report_screen_bg));
        else
            attendanceFragmentViewHolder.rcv_item_bg.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        if (position == mTaListArray.size() - 1)
            onScrollEndListener.onScrollEnd();

        attendanceFragmentViewHolder.ta_date_tv.setText(mTaListArray.get(position).getFrom_date());
        attendanceFragmentViewHolder.amount_tv.setText("\u20B9 " + mTaListArray.get(position).getAmount());
        attendanceFragmentViewHolder.status_tv.setText(mTaListArray.get(position).getStatus());
        /*attendanceFragmentViewHolder.view_tv.setText("Vieweed");*/

        attendanceFragmentViewHolder.view_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onActionItemClick();
            }
        });
    }


    @Override
    public int getItemCount() {
        if (mTaListArray == null)
            return 0;
        return mTaListArray.size();
    }

    class AttendanceFragmentViewHolder extends RecyclerView.ViewHolder {
        TextView ta_date_tv, amount_tv, status_tv;
        ImageView view_tv;
        LinearLayout rcv_item_bg;

        public AttendanceFragmentViewHolder(View itemView) {
            super(itemView);
            ta_date_tv = itemView.findViewById(R.id.ta_date_tv);
            amount_tv = itemView.findViewById(R.id.amount_tv);
            status_tv = itemView.findViewById(R.id.status_tv);
            view_tv = itemView.findViewById(R.id.view_tv);
            rcv_item_bg = itemView.findViewById(R.id.rcv_item_bg);
        }
    }

    public interface onScrollEndListener {
        void onScrollEnd();
    }

    public interface onItemClickListener {
        void onActionItemClick();
    }

    public void notifyAdapter(ArrayList<TaListDBModelEntity> list) {
        mTaListArray = list;
        notifyDataSetChanged();

    }
}
