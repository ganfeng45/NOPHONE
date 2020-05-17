
package com.example.time.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.time.R;
import com.example.time.bean.MyUsageStats;
import com.example.time.util.AppsUtil;
import com.example.time.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Provide views to RecyclerView with the directory entries.
 */
public class UsageListAdapter extends RecyclerView.Adapter<UsageListAdapter.ViewHolder> {

    private List<MyUsageStats> mCustomUsageStatsList = new ArrayList<>();
    private Random mRandom = new Random();
    private Context mContext;
    private int[] mColors;
    //接收数据
    public UsageListAdapter(Context context) {
        this.mContext = context;
        this.mColors = TimeUtil.getColorArray(context);
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    //单个recycleview item布局绑定
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView mCardView;
        private final TextView mPackageName;
        private final TextView mUsageInfo;
        private final ImageView mAppIcon;
        View appview;

        private ViewHolder(View v) {
            super(v);
            appview=v;
            mCardView = v.findViewById(R.id.cardview);
            mPackageName = v.findViewById(R.id.package_text);
            mUsageInfo = v.findViewById(R.id.usage_text);
            mAppIcon = v.findViewById(R.id.app_icon);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.usage_card, viewGroup, false);
        final ViewHolder holder=new ViewHolder(v);
        holder.appview.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                MyUsageStats mys=mCustomUsageStatsList.get(position);
                Toast.makeText(v.getContext(),"pck:"+mys.usageStats.getPackageName(),Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(viewGroup.getContext(),com.example.time.setapp.class);
                intent.putExtra("pckname",mys.usageStats.getPackageName());
                intent.putExtra("appname",AppsUtil.getAppName(mContext,mys.usageStats.getPackageName()));
                viewGroup.getContext().startActivity(intent);
            }
        });
        return holder;
    }
    //绑定数据
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        //根据位置绑定
        MyUsageStats myUsageStats = mCustomUsageStatsList.get(position);
        //绑定背景颜色
        viewHolder.mCardView.setCardBackgroundColor(mColors[mRandom.nextInt(5)]);
        //绑定app名字
        viewHolder.mPackageName.setText(AppsUtil.getAppName(mContext, myUsageStats.usageStats.getPackageName()));
        //绑定时间
        String time = TimeUtil.timeToString(myUsageStats.usageStats.getTotalTimeInForeground());
        viewHolder.mUsageInfo.setText(time);
        //绑定图标
        viewHolder.mAppIcon.setImageDrawable(myUsageStats.appIcon);
    }
    // 获取数据总数
    @Override
    public int getItemCount() {
        return mCustomUsageStatsList.size();
    }

    public void setCustomUsageStatsList(List<MyUsageStats> customUsageStats) {
        mCustomUsageStatsList = customUsageStats;
    }
}