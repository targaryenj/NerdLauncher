package com.example.jdm.nerdlauncher.fragment;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jdm.nerdlauncher.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by JDM on 2016/5/17.
 */
public class NerdLauncherFragment extends Fragment {
    private static final String TAG = "NerdLauncherFragment";

    private RecyclerView mRecyclerView;

    public static NerdLauncherFragment newInstance(){
        return new NerdLauncherFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nerd_launcher,container,false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_nerd_launcher_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));

        setupAdapter();
        return v;
    }

    private void setupAdapter() {
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent,0);
        List<AppHolder> appList = new ArrayList<>();

        //Sort App by AppName
        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                PackageManager pm = getActivity().getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(
                        a.loadLabel(pm).toString(),
                        b.loadLabel(pm).toString()
                );
            }
        });

        for(ResolveInfo resolveInfo : activities){
            AppHolder appHolder = new AppHolder();
            appHolder.setAppName(resolveInfo.loadLabel(pm).toString());
            appHolder.setAppIcon(resolveInfo.loadIcon(pm));
            appHolder.setActivityInfo(resolveInfo.activityInfo);
            appList.add(appHolder);
        }

        Log.i(TAG,"Found " + activities.size() + " activities.");
        mRecyclerView.setAdapter(new ActivityAdapter(appList));

    }

    private class ActivityHolder extends RecyclerView.ViewHolder{
        private ImageView mImageView;
        private TextView mNameTextView;
        private AppHolder appHolder;

        public ActivityHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.activityNameTextView);
            mImageView = (ImageView) itemView.findViewById(R.id.activityImageView);
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityInfo activityInfo = appHolder.getActivityInfo();

                    // To start a new task when you start a new activity,
                    // add a flag to the intent
                    // 添加Flag为Activity开启新的Task
                    Intent i = new Intent(Intent.ACTION_MAIN)
                            .setClassName(activityInfo.applicationInfo.packageName,activityInfo.name)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Log.d(TAG,"packageName = " + activityInfo.applicationInfo.packageName  + ", name= " + activityInfo.name);
                    startActivity(i);
                }
            });
        }

        /**
         * 绑定app信息
         * @param app
         */
        public void bindActivity(AppHolder app){
            appHolder = app;
            mNameTextView.setText(appHolder.getAppName());
            mImageView.setImageDrawable(appHolder.getAppIcon());
            Log.d(TAG,"bind activity " + appHolder.getAppName());
        }
    }

    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder>{

        private final List<AppHolder> appList;

        private ActivityAdapter(List<AppHolder> apps) {
            this.appList = apps;
        }

        @Override
        public ActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_activity_item,parent,false);
            return new ActivityHolder(view);
        }

        @Override
        public void onBindViewHolder(ActivityHolder holder, int position) {
            AppHolder appHolder = appList.get(position);
            holder.bindActivity(appHolder);
        }

        @Override
        public int getItemCount() {
            return appList.size();
        }
    }

    /**
     * app信息model，缓存解决RecyclerView加载图片卡顿
     */
    private class AppHolder{
        private String appName;
        private Drawable appIcon;
        private ActivityInfo activityInfo;

        public Drawable getAppIcon() {
            return appIcon;
        }

        public void setAppIcon(Drawable appIcon) {
            this.appIcon = appIcon;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public ActivityInfo getActivityInfo() {
            return activityInfo;
        }

        public void setActivityInfo(ActivityInfo activityInfo) {
            this.activityInfo = activityInfo;
        }
    }

}
