package com.kcteam.features.stock;

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
import com.kcteam.app.domain.StockDetailsListEntity;
import com.kcteam.app.utils.AppUtils;

import java.util.ArrayList;

/**
 * Created by Saikat on 10-09-2019.
 */

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    private onScrollEndListener onScrollEndListener;
    private ArrayList<StockDetailsListEntity> mViewAllOrderListEntityArray;
    private OnItemClickListener listener;

    public StockAdapter(Context context, ArrayList<StockDetailsListEntity> ViewAllOrderListEntityArray,
                                               onScrollEndListener onScrollEndListener, OnItemClickListener listener) {
        this.context = context;
        this.onScrollEndListener = onScrollEndListener;
        mViewAllOrderListEntityArray = ViewAllOrderListEntityArray;
        this.listener = listener;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.inflater_order_history_item, viewGroup, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder attendanceFragmentViewHolder, final int position) {
        try {
            if (position % 2 == 0)
                attendanceFragmentViewHolder.rcv_item_bg.setBackgroundColor(ContextCompat.getColor(context, R.color.report_screen_bg));
            else
                attendanceFragmentViewHolder.rcv_item_bg.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

            attendanceFragmentViewHolder.email_iv.setVisibility(View.GONE);
            attendanceFragmentViewHolder.order_no_tv.setVisibility(View.GONE);

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

            attendanceFragmentViewHolder.tv_order_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onViewClick(position);
                }
            });

            if (!TextUtils.isEmpty(mViewAllOrderListEntityArray.get(position).getDate()))
                attendanceFragmentViewHolder.order_date_tv.setText(AppUtils.Companion.convertCorrectDateTimeToOrderDate/*convertDateTimeToCommonFormat*/(mViewAllOrderListEntityArray.get(position).getDate()));


            if (!TextUtils.isEmpty(mViewAllOrderListEntityArray.get(position).getQty())) {
                if (mViewAllOrderListEntityArray.get(position).getQty().contains("."))
                    attendanceFragmentViewHolder.ordered_amount_tv.setText(String.valueOf(Integer.valueOf((int) Float.parseFloat(mViewAllOrderListEntityArray.get(position).getQty()))));
                else {
                    attendanceFragmentViewHolder.ordered_amount_tv.setText(mViewAllOrderListEntityArray.get(position).getQty());
                }

                //attendanceFragmentViewHolder.ordered_amount_tv.setText(mViewAllOrderListEntityArray.get(position).getQty());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        if (mViewAllOrderListEntityArray == null)
            return 0;
        return mViewAllOrderListEntityArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView order_date_tv, ordered_amount_tv, order_no_tv;
        LinearLayout rcv_item_bg;
        ImageView tv_order_view, sync_status_iv, email_iv;

        public ViewHolder(View itemView) {
            super(itemView);
            order_date_tv = itemView.findViewById(R.id.order_date_tv);
            ordered_amount_tv = itemView.findViewById(R.id.ordered_amount_tv);
            order_no_tv = itemView.findViewById(R.id.order_no_tv);
            rcv_item_bg = itemView.findViewById(R.id.rcv_item_bg);
            tv_order_view = itemView.findViewById(R.id.tv_order_view);
            sync_status_iv = itemView.findViewById(R.id.sync_status_iv);
            email_iv = itemView.findViewById(R.id.email_iv);
        }
    }

    public interface onScrollEndListener {
        void onScrollEnd();
    }

    public void notifyAdapter(ArrayList<StockDetailsListEntity> list) {
        mViewAllOrderListEntityArray = list;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onViewClick(int position);

        void onSyncClick(int position);
    }
}
