package atv.com.project.monkeylivetv.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import atv.com.project.monkeylivetv.Application.MonkeyLive;
import atv.com.project.monkeylivetv.R;

/**
 * Created by arjun on 6/22/15.
 */
public class SettingsFragment extends Fragment {
    private View rootView;
    private TextView notifySettingPhTv;
    private TextView notifyFollowedSettingPhTv;
    private TextView notifyNewStreamSettingPhTv;
    private Switch followNotificationToggle;
    private Switch newStreamNotificationToggle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        initialiseViews();
        bindEvents();
        return rootView;
    }

    private void bindEvents() {
        followNotificationToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MonkeyLive.setBooleanPreferenceData(MonkeyLive.SHOW_FOLLOW_NOTIFICATION, isChecked);
            }
        });




        newStreamNotificationToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MonkeyLive.setBooleanPreferenceData(MonkeyLive.SHOW_NEW_STREAM_NOTIFICATION, isChecked);
            }
        });
    }

    private void initialiseViews() {
        notifySettingPhTv = (TextView) rootView.findViewById(R.id.notifySettingPhTv);
        notifyFollowedSettingPhTv = (TextView) rootView.findViewById(R.id.notifyFollowedSettingPhTv);
        notifyNewStreamSettingPhTv = (TextView) rootView.findViewById(R.id.notifyNewStreamSettingPhTv);
        followNotificationToggle = (Switch) rootView.findViewById(R.id.followNotificationToggle);
        newStreamNotificationToggle = (Switch) rootView.findViewById(R.id.newStreamNotificationToggle);

        followNotificationToggle.setChecked(MonkeyLive.getBooleanPreference(MonkeyLive.SHOW_FOLLOW_NOTIFICATION, true));
        newStreamNotificationToggle.setChecked(MonkeyLive.getBooleanPreference(MonkeyLive.SHOW_NEW_STREAM_NOTIFICATION, true));

        notifySettingPhTv.setTypeface(MonkeyLive.racho);
        notifyFollowedSettingPhTv.setTypeface(MonkeyLive.racho);
        notifyNewStreamSettingPhTv.setTypeface(MonkeyLive.racho);
    }
}
