package com.kcteam.features.returnsOrder;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kcteam.R;
import com.kcteam.app.AppDatabase;
import com.kcteam.app.domain.AddShopDBModelEntity;
import com.kcteam.app.domain.ReturnDetailsEntity;
import com.kcteam.app.utils.AppUtils;

import java.util.ArrayList;


public class ViewAllReturnListRecyclerViewAdapter extends RecyclerView.Adapter<ViewAllReturnListRecyclerViewAdapter.AttendanceFragmentViewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    private onScrollEndListener onScrollEndListener;
    private ArrayList<ReturnDetailsEntity> mViewAllOrderListEntityArray;
    private OnItemClickListener listener;

    public ViewAllReturnListRecyclerViewAdapter(Context context, ArrayList<ReturnDetailsEntity> ViewAllOrderListEntityArray,
                                                onScrollEndListener onScrollEndListener, OnItemClickListener listener) {
        this.context = context;
        this.onScrollEndListener = onScrollEndListener;
        mViewAllOrderListEntityArray = ViewAllOrderListEntityArray;
        this.listener = listener;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public AttendanceFragmentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.inflater_return_history_item, viewGroup, false);
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

            attendanceFragmentViewHolder.tv_order_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onViewClick(position);
                }
            });


            AddShopDBModelEntity shop = AppDatabase.Companion.getDBInstance().addShopEntryDao().getShopByIdN(mViewAllOrderListEntityArray.get(position).getShop_id());


            attendanceFragmentViewHolder.location_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onLocationClick(position);
                }
            });

            if (!TextUtils.isEmpty(mViewAllOrderListEntityArray.get(position).getDate()))
                attendanceFragmentViewHolder.order_date_tv.setText(AppUtils.Companion.convertCorrectDateTimeToOrderDate/*convertDateTimeToCommonFormat*/(mViewAllOrderListEntityArray.get(position).getDate()));


                attendanceFragmentViewHolder.order_no_tv.setText("Return #" + mViewAllOrderListEntityArray.get(position).getReturn_id());

            if (!TextUtils.isEmpty(mViewAllOrderListEntityArray.get(position).getAmount())) {
                if (mViewAllOrderListEntityArray.get(position).getAmount().contains("\u20B9"))
                    attendanceFragmentViewHolder.ordered_amount_tv.setText(mViewAllOrderListEntityArray.get(position).getAmount());
                else {
                    String finalAmount = String.format("%.2f", Float.parseFloat(mViewAllOrderListEntityArray.get(position).getAmount()));
                    attendanceFragmentViewHolder.ordered_amount_tv.setText(context.getString(R.string.rupee_symbol) +
                            " " + finalAmount);
                }
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

    class AttendanceFragmentViewHolder extends RecyclerView.ViewHolder {
        TextView order_date_tv, ordered_amount_tv, order_no_tv;
        LinearLayout rcv_item_bg;
        ImageView tv_order_view, sync_status_iv,location_iv;

        public AttendanceFragmentViewHolder(View itemView) {
            super(itemView);
            order_date_tv = itemView.findViewById(R.id.return_date_tv);
            ordered_amount_tv = itemView.findViewById(R.id.return_amount_tv);
            rcv_item_bg = itemView.findViewById(R.id.rcv_item_bg);
            tv_order_view = itemView.findViewById(R.id.tv_return_view);
            sync_status_iv = itemView.findViewById(R.id.sync_status_iv);
            location_iv = itemView.findViewById(R.id.location_iv);
            order_no_tv = itemView.findViewById(R.id.order_noo_tv);
        }
    }

    public interface onScrollEndListener {
        void onScrollEnd();
    }

    public void notifyAdapter(ArrayList<ReturnDetailsEntity> list) {
        mViewAllOrderListEntityArray = list;
        notifyDataSetChanged();
    }

    interface OnItemClickListener {
        void onViewClick(int position);

        void onSyncClick(int position);

        void onLocationClick(int position);
    }
}
