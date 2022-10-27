package com.kcteam.features.shopdetail.presentation;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kcteam.R;
import com.kcteam.app.Pref;
import com.kcteam.app.domain.CollectionDetailsEntity;

import java.util.ArrayList;

/**
 * Created by Saikat on 26-10-2018.
 */

public class CollectionDetailsAdapter extends RecyclerView.Adapter<CollectionDetailsAdapter.AttendanceFragmentViewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    private onScrollEndListener onScrollEndListener;
    private ArrayList<CollectionDetailsEntity> mViewAllOrderListEntityArray;
    private OnItemClickListener listener;

    public CollectionDetailsAdapter(Context context, ArrayList<CollectionDetailsEntity> ViewAllOrderListEntityArray,
                                               onScrollEndListener onScrollEndListener, OnItemClickListener listener) {
        this.context = context;
        this.onScrollEndListener = onScrollEndListener;
        mViewAllOrderListEntityArray = ViewAllOrderListEntityArray;
        this.listener = listener;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public AttendanceFragmentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.inflater_order_history_item, viewGroup, false);
        return new AttendanceFragmentViewHolder(view);
    }


    @Override
    public void onBindViewHolder(AttendanceFragmentViewHolder attendanceFragmentViewHolder, final int position) {
        try {
            if (position % 2 == 0)
                attendanceFragmentViewHolder.rcv_item_bg.setBackgroundColor(ContextCompat.getColor(context, R.color.report_screen_bg));
            else
                attendanceFragmentViewHolder.rcv_item_bg.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

            if (position == mViewAllOrderListEntityArray.size() - 1)
                onScrollEndListener.onScrollEnd();

            if (mViewAllOrderListEntityArray.get(position).isUploaded())
                attendanceFragmentViewHolder.sync_status_iv.setImageResource(R.drawable.ic_registered_shop_sync);
            else {
                attendanceFragmentViewHolder.sync_status_iv.setImageResource(R.drawable.ic_registered_shop_not_sync);

                attendanceFragmentViewHolder.sync_status_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onSyncClick(position);
                    }
                });
            }
            attendanceFragmentViewHolder.tv_order_view.setVisibility(View.GONE);
            attendanceFragmentViewHolder.tv_order_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onViewClick(position);
                }
            });

            if (!TextUtils.isEmpty(mViewAllOrderListEntityArray.get(position).getDate()))
                attendanceFragmentViewHolder.order_date_tv.setText(/*AppUtils.Companion.convertDateTimeToCommonFormat(*/mViewAllOrderListEntityArray.get(position).getDate()/*)*/);

            if (!TextUtils.isEmpty(mViewAllOrderListEntityArray.get(position).getCollection())) {
                if (mViewAllOrderListEntityArray.get(position).getCollection().contains("\u20B9"))
                    attendanceFragmentViewHolder.ordered_amount_tv.setText(mViewAllOrderListEntityArray.get(position).getCollection());
                else {
                    String totalPrice = String.format("%.2f", Float.parseFloat(mViewAllOrderListEntityArray.get(position).getCollection()));
                    attendanceFragmentViewHolder.ordered_amount_tv.setText("\u20B9 " + totalPrice);
                }
            }

            if (Pref.INSTANCE.isPatientDetailsShowInCollection()) {
                attendanceFragmentViewHolder.order_no_tv.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(mViewAllOrderListEntityArray.get(position).getPatient_name()))
                    attendanceFragmentViewHolder.order_no_tv.setText("Patient: " + mViewAllOrderListEntityArray.get(position).getPatient_name());
                else
                    attendanceFragmentViewHolder.order_no_tv.setText("Patient: N.A.");
            }
            else
                attendanceFragmentViewHolder.order_no_tv.setVisibility(View.GONE);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        if (mViewAllOrderListEntityArray == null)
            return 0;
        return mViewAllOrderListEntityArray.size();
    }

    class AttendanceFragmentViewHolder extends RecyclerView.ViewHolder {
        TextView order_date_tv, ordered_amount_tv, order_no_tv;
        LinearLayout rcv_item_bg;
        ImageView tv_order_view, sync_status_iv;

        public AttendanceFragmentViewHolder(View itemView) {
            super(itemView);
            order_date_tv = itemView.findViewById(R.id.order_date_tv);
            ordered_amount_tv = itemView.findViewById(R.id.ordered_amount_tv);
            rcv_item_bg = itemView.findViewById(R.id.rcv_item_bg);
            tv_order_view = itemView.findViewById(R.id.tv_order_view);
            sync_status_iv = itemView.findViewById(R.id.sync_status_iv);
            order_no_tv = itemView.findViewById(R.id.order_no_tv);
        }
    }

    public interface onScrollEndListener {
        void onScrollEnd();
    }

    public void notifyAdapter(ArrayList<CollectionDetailsEntity> list) {
        mViewAllOrderListEntityArray = list;
        notifyDataSetChanged();
    }

    interface OnItemClickListener {
        void onViewClick(int position);

        void onSyncClick(int position);
    }
}
