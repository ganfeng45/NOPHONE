package com.example.time;

import android.app.usage.UsageStatsManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.time.R;
import com.example.time.adapter.UsageListAdapter;
import com.example.time.bean.MyUsageStats;
import com.example.time.util.AppsUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Fragment that demonstrates how to use App Usage Statistics API.
 */
public class UsageStatisticsFragment extends Fragment {

  private UsageListAdapter mUsageListAdapter;
  private RecyclerView mRecyclerView;

  public UsageStatisticsFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_usage_statistics, container, false);
  }

  @Override
  public void onViewCreated(View rootView, Bundle savedInstanceState) {
    super.onViewCreated(rootView, savedInstanceState);
    Log.d("test","onFailure -- > " );
    //设置适配器
    mUsageListAdapter = new UsageListAdapter(getActivity());
    mRecyclerView = rootView.findViewById(R.id.recyclerview_app_usage);
    //设置为网格布局
    GridLayoutManager layoutManager = new GridLayoutManager(getContext(),3);
    mRecyclerView.setLayoutManager(layoutManager);
    //动画设置
    LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.anim_layout_fall_down);
    mRecyclerView.setLayoutAnimation(controller);
    mRecyclerView.scrollToPosition(0);
    mRecyclerView.setAdapter(mUsageListAdapter);
    //下拉选项卡
    Spinner spinner = rootView.findViewById(R.id.spinner_time_span);
    SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
            R.array.action_list, android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(spinnerAdapter);
    //下拉选项卡点击事件
    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

      String[] strings = getResources().getStringArray(R.array.action_list);

      @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        StatsUsageInterval statsUsageInterval = StatsUsageInterval
                .getValue(strings[position]);
        if (statsUsageInterval != null) {
          List<MyUsageStats> list = AppsUtil.updateAppsUsageData(getActivity().getApplicationContext(), statsUsageInterval.mInterval);
          //更新view
          mUsageListAdapter.setCustomUsageStatsList(list);
          postData(list);
          mUsageListAdapter.notifyDataSetChanged();
          runLayoutAnimation(mRecyclerView);
          mRecyclerView.scrollToPosition(0);
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
  }
  //设置recycleview动画
  private void runLayoutAnimation(final RecyclerView recyclerView) {

    final LayoutAnimationController controller =
            AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.anim_layout_fall_down);
    recyclerView.setLayoutAnimation(controller);
    recyclerView.getAdapter().notifyDataSetChanged();
    recyclerView.scheduleLayoutAnimation();
  }
  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public void postData(List<MyUsageStats> customUsageStats){
    List<MyUsageStats> customUsageStats2 = AppsUtil.updateAppsUsageData(getActivity().getApplicationContext(), UsageStatsManager.INTERVAL_DAILY);
    for(MyUsageStats myUsageStats :customUsageStats2){
      Log.d("test",AppsUtil.getAppName(getContext(), myUsageStats.usageStats.getPackageName()) );
      long time = myUsageStats.usageStats.getTotalTimeInForeground();
      Log.d("test"," "+time);

    }
    //form
    //创建一个FormBody.Builder
    FormBody.Builder builder=new FormBody.Builder();
    if (customUsageStats2!=null&&customUsageStats2.size()>0){
      for (final MyUsageStats p : customUsageStats2) {
        builder.add(AppsUtil.getAppName(getContext(), p.usageStats.getPackageName()),p.usageStats.getTotalTimeInForeground()+"");
      }
    }
    RequestBody formBody=builder.build();

    String url = "https://www.auster.fun/sy/public/api/door/phonetime?token=api2020";
    //1、创建client，理解为创建浏览器
    OkHttpClient okHttpClient = new OkHttpClient();
    //2、创建请求内容
    Request request = new Request.Builder()
            .url(url)
            .post(formBody)
            .build();
    //3、用浏览器创建调用任务
    Call call = okHttpClient.newCall(request);
    //4、执行任务
    call.enqueue(new Callback() {
      @Override
      public void onFailure(@NotNull Call call,@NotNull IOException e) {
        Log.d("test","redata -- > " + e.toString());
      }

      @Override
      public void onResponse(@NotNull Call call,@NotNull Response response) throws IOException {
        Log.d("test","response -- > " + response.body().string());
      }
    });

  }
  /**
   * Enum represents the intervals for {@link UsageStatsManager} so that
   * values for intervals can be found by a String representation.
   */
  //VisibleForTesting
  // 设置
   enum StatsUsageInterval {
    DAILY("Daily", UsageStatsManager.INTERVAL_DAILY),
    WEEKLY("Weekly", UsageStatsManager.INTERVAL_WEEKLY),
    MONTHLY("Monthly", UsageStatsManager.INTERVAL_MONTHLY);

    private int mInterval;
    private String mStringRepresentation;

    StatsUsageInterval(String stringRepresentation, int interval) {
      mStringRepresentation = stringRepresentation;
      mInterval = interval;
    }

    static StatsUsageInterval getValue(String stringRepresentation) {
      for (StatsUsageInterval statsUsageInterval : values()) {
        if (statsUsageInterval.mStringRepresentation.equals(stringRepresentation)) {
          return statsUsageInterval;
        }
      }
      return null;
    }
  }
}
