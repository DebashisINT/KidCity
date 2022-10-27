package com.kcteam.features.attendance;

import android.content.Context;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kcteam.R;
import com.kcteam.app.Pref;
import com.kcteam.app.utils.AppUtils;
import com.kcteam.features.login.UserLoginDataEntity;

import java.util.ArrayList;

/**
 * Created by sayantan.sarkar on 1/11/17.
 */

public class AttendanceRecyclerViewAdapter extends RecyclerView.Adapter<AttendanceRecyclerViewAdapter.AttendanceFragmentViewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    private onScrollEndListener onScrollEndListener;
    private ArrayList<UserLoginDataEntity> mUserLoginDataEntityArray;

    public AttendanceRecyclerViewAdapter(Context context, ArrayList<UserLoginDataEntity> userLoginDataEntityArray, onScrollEndListener onScrollEndListener) {
        this.context = context;
        this.onScrollEndListener = onScrollEndListener;
        mUserLoginDataEntityArray = userLoginDataEntityArray;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public AttendanceFragmentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.inflate_attendance_rcv_item, viewGroup, false);
        return new AttendanceFragmentViewHolder(view);
    }


    @Override
    public void onBindViewHolder(AttendanceFragmentViewHolder attendanceFragmentViewHolder, int position) {
        if (position % 2 == 0) {
           // attendanceFragmentViewHolder.rcv_item_bg.setBackgroundColor(ContextCompat.getColor(context, R.color.report_screen_bg));
        }
        else {
           // attendanceFragmentViewHolder.rcv_item_bg.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }
        if (position == mUserLoginDataEntityArray.size() - 1)
            onScrollEndListener.onScrollEnd();


//        String formattedDate = AppUtils.Companion.getFormattedDateAtt(FTStorageUtils.getStrinTODateType3(mUserLoginDataEntityArray.get(position).getLogindate()));
        //Comment out to display AM/PM in caps
//        String formattedLoginTime = AppUtils.Companion.convertTime(FTStorageUtils.getStringToDate(mUserLoginDataEntityArray.get(position).getLogintime()));//.replace("am","AM").replace("pm","PM");
//        String formattedLgoutTime = "";
//        if(!TextUtils.isEmpty(mUserLoginDataEntityArray.get(position).getLogouttime()))
//            formattedLgoutTime = AppUtils.Companion.convertTime(FTStorageUtils.getStringToDate(mUserLoginDataEntityArray.get(position).getLogouttime()));//.replace("am", "AM").replace("pm","PM");

        attendanceFragmentViewHolder.date.setText(mUserLoginDataEntityArray.get(position).getLogindate());

        if (AppUtils.Companion.getCurrentDateChanged().equals(mUserLoginDataEntityArray.get(position).getLogindate())) {
            attendanceFragmentViewHolder.logout.setVisibility(View.INVISIBLE);
            attendanceFragmentViewHolder.duration.setVisibility(View.INVISIBLE);
//            String formattedLoginTime = AppUtils.Companion.convertTime(FTStorageUtils.getStringToDate(AppDatabase.Companion.getDBInstance().userAttendanceDataDao().getLoginTime(Pref.INSTANCE.getUser_id())));

            if (!TextUtils.isEmpty(mUserLoginDataEntityArray.get(position).getLogintime())) {
                if (!TextUtils.isEmpty(mUserLoginDataEntityArray.get(position).getIsonleave())) {
                    if (mUserLoginDataEntityArray.get(position).getIsonleave().equalsIgnoreCase("false")) {
                        attendanceFragmentViewHolder.login.setText(mUserLoginDataEntityArray.get(position).getLogintime());
                    /*attendanceFragmentViewHolder.logout.setText(mUserLoginDataEntityArray.get(position).getLogouttime());
                    attendanceFragmentViewHolder.duration.setText(mUserLoginDataEntityArray.get(position).getDuration());*/

                        long loginTime = AppUtils.Companion.convertTimeWithMeredianToLong(mUserLoginDataEntityArray.get(position).getLogintime());

                        long approvedLoginTime = 0L;

                        if (!TextUtils.isEmpty(Pref.INSTANCE.getApprovedInTime()))
                            approvedLoginTime = AppUtils.Companion.convertTimeWithMeredianToLong(Pref.INSTANCE.getApprovedInTime());
                        else {
                            if (mUserLoginDataEntityArray.get(position).getLogintime().contains("a.m.") || mUserLoginDataEntityArray.get(position).getLogintime().contains("p.m.") ||
                                    mUserLoginDataEntityArray.get(position).getLogintime().contains("A.M.") || mUserLoginDataEntityArray.get(position).getLogintime().contains("P.M.")) {
                                approvedLoginTime = AppUtils.Companion.convertTimeWithMeredianToLong("10:15 A.M.");
                            } else
                                approvedLoginTime = AppUtils.Companion.convertTimeWithMeredianToLong("10:15 AM");
                        }

                        if (loginTime > approvedLoginTime) {
                            attendanceFragmentViewHolder.login.setTextColor(context.getResources().getColor(R.color.red));
                            attendanceFragmentViewHolder.login.setTypeface(null, Typeface.BOLD);
                        } else {
                            attendanceFragmentViewHolder.login.setTextColor(context.getResources().getColor(R.color.login_txt_color));
                            attendanceFragmentViewHolder.login.setTypeface(null, Typeface.NORMAL);
                        }


                        /*if (mUserLoginDataEntityArray.get(position).getLogintime().compareToIgnoreCase("10:15 AM") > 0) {
                            //attendanceFragmentViewHolder.rcv_item_bg.setBackgroundColor(Color.parseColor("#FFECD8D8"));
                            attendanceFragmentViewHolder.login.setTextColor(context.getResources().getColor(R.color.red));
                            attendanceFragmentViewHolder.login.setTypeface(null, Typeface.BOLD);
                        } else {
                            attendanceFragmentViewHolder.login.setTextColor(context.getResources().getColor(R.color.login_txt_color));
                        }*/
                    } else {
                        attendanceFragmentViewHolder.logout.setVisibility(View.VISIBLE);
                        attendanceFragmentViewHolder.duration.setVisibility(View.VISIBLE);
                        attendanceFragmentViewHolder.login.setText("On Leave");
                        attendanceFragmentViewHolder.logout.setText("On Leave");
                        attendanceFragmentViewHolder.duration.setText("On Leave");
                    }
                } else {
                    attendanceFragmentViewHolder.login.setText(mUserLoginDataEntityArray.get(position).getLogintime());
                }
            } else {
                attendanceFragmentViewHolder.logout.setVisibility(View.VISIBLE);
                attendanceFragmentViewHolder.duration.setVisibility(View.VISIBLE);
                attendanceFragmentViewHolder.login.setText("On Leave");
                attendanceFragmentViewHolder.logout.setText("On Leave");
                attendanceFragmentViewHolder.duration.setText("On Leave");
            }


//            if(AppDatabase.Companion.getDBInstance().userAttendanceDataDao().getLoginTime(Pref.INSTANCE.getUser_id()).contains(":")){
//                String formattedLoginTime = AppUtils.Companion.convertTime(FTStorageUtils.getStringToDate(AppDatabase.Companion.getDBInstance().userAttendanceDataDao().getLoginTime(Pref.INSTANCE.getUser_id())));
//                attendanceFragmentViewHolder.login.setText(formattedLoginTime);
//            }else
//                attendanceFragmentViewHolder.login.setText(AppDatabase.Companion.getDBInstance().userAttendanceDataDao().getLoginTime(Pref.INSTANCE.getUser_id()));
        } else {
            attendanceFragmentViewHolder.logout.setVisibility(View.VISIBLE);
            attendanceFragmentViewHolder.duration.setVisibility(View.VISIBLE);
            /*attendanceFragmentViewHolder.logout.setText(mUserLoginDataEntityArray.get(position).getLogouttime());
            attendanceFragmentViewHolder.duration.setText(mUserLoginDataEntityArray.get(position).getDuration());
            attendanceFragmentViewHolder.login.setText(mUserLoginDataEntityArray.get(position).getLogintime());*/

            if (!TextUtils.isEmpty(mUserLoginDataEntityArray.get(position).getLogintime())) {
                if (!TextUtils.isEmpty(mUserLoginDataEntityArray.get(position).getIsonleave())) {
                    if (mUserLoginDataEntityArray.get(position).getIsonleave().equalsIgnoreCase("false")) {
                        attendanceFragmentViewHolder.logout.setText(mUserLoginDataEntityArray.get(position).getLogouttime());
                        attendanceFragmentViewHolder.duration.setText(mUserLoginDataEntityArray.get(position).getDuration());
                        attendanceFragmentViewHolder.login.setText(mUserLoginDataEntityArray.get(position).getLogintime());

                        long loginTime = AppUtils.Companion.convertTimeWithMeredianToLong(mUserLoginDataEntityArray.get(position).getLogintime());
                        long approvedLoginTime = 0L;

                        if (!TextUtils.isEmpty(Pref.INSTANCE.getApprovedInTime()))
                            approvedLoginTime = AppUtils.Companion.convertTimeWithMeredianToLong(Pref.INSTANCE.getApprovedInTime());
                        else {
                            if (mUserLoginDataEntityArray.get(position).getLogintime().contains("a.m.") || mUserLoginDataEntityArray.get(position).getLogintime().contains("p.m.") ||
                                    mUserLoginDataEntityArray.get(position).getLogintime().contains("A.M.") || mUserLoginDataEntityArray.get(position).getLogintime().contains("P.M.")) {
                                approvedLoginTime = AppUtils.Companion.convertTimeWithMeredianToLong("10:15 A.M.");
                            } else
                                approvedLoginTime = AppUtils.Companion.convertTimeWithMeredianToLong("10:15 AM");
                        }

                        if (loginTime > approvedLoginTime) {
                            attendanceFragmentViewHolder.login.setTextColor(context.getResources().getColor(R.color.red));
                            attendanceFragmentViewHolder.login.setTypeface(null, Typeface.BOLD);
                        } else {
                            attendanceFragmentViewHolder.login.setTextColor(context.getResources().getColor(R.color.login_txt_color));
                            attendanceFragmentViewHolder.login.setTypeface(null, Typeface.NORMAL);
                        }

                        /*if (mUserLoginDataEntityArray.get(position).getLogintime().compareToIgnoreCase("10:15 AM") > 0) {
                            //attendanceFragmentViewHolder.rcv_item_bg.setBackgroundColor(Color.parseColor("#FFECD8D8"));
                            attendanceFragmentViewHolder.login.setTextColor(context.getResources().getColor(R.color.red));
                            attendanceFragmentViewHolder.login.setTypeface(null, Typeface.BOLD);
                        } else {
                            attendanceFragmentViewHolder.login.setTextColor(context.getResources().getColor(R.color.login_txt_color));
                        }*/

                /*if (mUserLoginDataEntityArray.get(position).getLogintime().compareToIgnoreCase("10.30 AM") > 0) {
                    attendanceFragmentViewHolder.rcv_item_bg.setBackgroundColor(Color.YELLOW);
                }*/
                    } else {
                        attendanceFragmentViewHolder.login.setText("On Leave");
                        attendanceFragmentViewHolder.logout.setText("On Leave");
                        attendanceFragmentViewHolder.duration.setText("On Leave");
                    }
                } else {
                    attendanceFragmentViewHolder.logout.setText(mUserLoginDataEntityArray.get(position).getLogouttime());
                    attendanceFragmentViewHolder.duration.setText(mUserLoginDataEntityArray.get(position).getDuration());
                    attendanceFragmentViewHolder.login.setText(mUserLoginDataEntityArray.get(position).getLogintime());
                }
            } else {
                attendanceFragmentViewHolder.login.setText("On Leave");
                attendanceFragmentViewHolder.logout.setText("On Leave");
                attendanceFragmentViewHolder.duration.setText("On Leave");
            }
        }
    }


    @Override
    public int getItemCount() {
        if (mUserLoginDataEntityArray == null)
            return 0;
        return mUserLoginDataEntityArray.size();
    }

    class AttendanceFragmentViewHolder extends RecyclerView.ViewHolder {
        TextView date, login, logout, duration;
        LinearLayout rcv_item_bg;

        public AttendanceFragmentViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tv_date);
            login = itemView.findViewById(R.id.tv_login);
            logout = itemView.findViewById(R.id.tv_logout);
            duration = itemView.findViewById(R.id.tv_duration);
            rcv_item_bg = itemView.findViewById(R.id.rcv_item_bg);


        }
    }

    public interface onScrollEndListener {
        void onScrollEnd();
    }

    public void notifyAdapter(ArrayList<UserLoginDataEntity> list) {

        mUserLoginDataEntityArray = list;
        notifyDataSetChanged();

    }
}
