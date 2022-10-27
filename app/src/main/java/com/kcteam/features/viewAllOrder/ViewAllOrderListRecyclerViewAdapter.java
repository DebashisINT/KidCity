package com.kcteam.features.viewAllOrder;

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
import com.kcteam.app.AppDatabase;
import com.kcteam.app.Pref;
import com.kcteam.app.domain.AddShopDBModelEntity;
import com.kcteam.app.domain.OrderDetailsListEntity;
import com.kcteam.app.utils.AppUtils;

import java.util.ArrayList;

/**
 * Created by sayantan.sarkar on 1/11/17.
 */

public class ViewAllOrderListRecyclerViewAdapter extends RecyclerView.Adapter<ViewAllOrderListRecyclerViewAdapter.AttendanceFragmentViewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    private onScrollEndListener onScrollEndListener;
    private ArrayList<OrderDetailsListEntity> mViewAllOrderListEntityArray;
    private OnItemClickListener listener;

    public ViewAllOrderListRecyclerViewAdapter(Context context, ArrayList<OrderDetailsListEntity> ViewAllOrderListEntityArray,
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

            attendanceFragmentViewHolder.tv_order_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onViewClick(position);
                }
            });


            AddShopDBModelEntity shop = AppDatabase.Companion.getDBInstance().addShopEntryDao().getShopByIdN(mViewAllOrderListEntityArray.get(position).getShop_id());

            if (Pref.INSTANCE.isOrderMailVisible()) {
                if (Integer.parseInt(shop.getType()) != 1 && Integer.parseInt(shop.getType()) != 2 &&
                        Integer.parseInt(shop.getType()) != 3 && Integer.parseInt(shop.getType()) != 4 && Integer.parseInt(shop.getType()) != 5)

                    attendanceFragmentViewHolder.email_iv.setVisibility(View.GONE);
                else
                    attendanceFragmentViewHolder.email_iv.setVisibility(View.VISIBLE);
            }
            else
                attendanceFragmentViewHolder.email_iv.setVisibility(View.GONE);

            if (Pref.INSTANCE.isCollectioninMenuShow())
                attendanceFragmentViewHolder.collection_iv.setVisibility(View.VISIBLE);
            else
                attendanceFragmentViewHolder.collection_iv.setVisibility(View.GONE);

            attendanceFragmentViewHolder.email_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onEmailClick(position);
                }
            });

            attendanceFragmentViewHolder.collection_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCollectionClick(position);
                }
            });

            attendanceFragmentViewHolder.location_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onLocationClick(position);
                }
            });

            if (!TextUtils.isEmpty(mViewAllOrderListEntityArray.get(position).getDate()))
                attendanceFragmentViewHolder.order_date_tv.setText(AppUtils.Companion.convertCorrectDateTimeToOrderDate/*convertDateTimeToCommonFormat*/(mViewAllOrderListEntityArray.get(position).getDate()));

            if (Pref.INSTANCE.isPatientDetailsShowInOrder()) {
                if (!TextUtils.isEmpty(mViewAllOrderListEntityArray.get(position).getPatient_name())) {
                    attendanceFragmentViewHolder.order_no_tv.setText("Order #" + mViewAllOrderListEntityArray.get(position).getOrder_id() +
                            "\nPatient: " + mViewAllOrderListEntityArray.get(position).getPatient_name());
                }
                else {
                    attendanceFragmentViewHolder.order_no_tv.setText("Order #" + mViewAllOrderListEntityArray.get(position).getOrder_id() +
                            "\nPatient: N.A.");
                }
            }
            else
                attendanceFragmentViewHolder.order_no_tv.setText("Order #" + mViewAllOrderListEntityArray.get(position).getOrder_id());

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
        ImageView tv_order_view, sync_status_iv, email_iv, collection_iv, location_iv;

        public AttendanceFragmentViewHolder(View itemView) {
            super(itemView);
            order_date_tv = itemView.findViewById(R.id.order_date_tv);
            ordered_amount_tv = itemView.findViewById(R.id.ordered_amount_tv);
            rcv_item_bg = itemView.findViewById(R.id.rcv_item_bg);
            tv_order_view = itemView.findViewById(R.id.tv_order_view);
            sync_status_iv = itemView.findViewById(R.id.sync_status_iv);
            email_iv = itemView.findViewById(R.id.email_iv);
            collection_iv = itemView.findViewById(R.id.collection_iv);
            location_iv = itemView.findViewById(R.id.location_iv);
            order_no_tv = itemView.findViewById(R.id.order_no_tv);
        }
    }

    public interface onScrollEndListener {
        void onScrollEnd();
    }

    public void notifyAdapter(ArrayList<OrderDetailsListEntity> list) {
        mViewAllOrderListEntityArray = list;
        notifyDataSetChanged();
    }

    interface OnItemClickListener {
        void onViewClick(int position);

        void onSyncClick(int position);

        void onEmailClick(int position);

        void onCollectionClick(int position);

        void onLocationClick(int position);
    }
}
