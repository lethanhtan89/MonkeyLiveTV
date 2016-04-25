package atv.com.project.popkontv.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import atv.com.project.popkontv.Application.Popkon;
import atv.com.project.popkontv.R;

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
                Popkon.setBooleanPreferenceData(Popkon.SHOW_FOLLOW_NOTIFICATION, isChecked);
            }
        });




        newStreamNotificationToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Popkon.setBooleanPreferenceData(Popkon.SHOW_NEW_STREAM_NOTIFICATION, isChecked);
            }
        });
    }

    private void initialiseViews() {
        notifySettingPhTv = (TextView) rootView.findViewById(R.id.notifySettingPhTv);
        notifyFollowedSettingPhTv = (TextView) rootView.findViewById(R.id.notifyFollowedSettingPhTv);
        notifyNewStreamSettingPhTv = (TextView) rootView.findViewById(R.id.notifyNewStreamSettingPhTv);
        followNotificationToggle = (Switch) rootView.findViewById(R.id.followNotificationToggle);
        newStreamNotificationToggle = (Switch) rootView.findViewById(R.id.newStreamNotificationToggle);

        followNotificationToggle.setChecked(Popkon.getBooleanPreference(Popkon.SHOW_FOLLOW_NOTIFICATION, true));
        newStreamNotificationToggle.setChecked(Popkon.getBooleanPreference(Popkon.SHOW_NEW_STREAM_NOTIFICATION, true));

        notifySettingPhTv.setTypeface(Popkon.racho);
        notifyFollowedSettingPhTv.setTypeface(Popkon.racho);
        notifyNewStreamSettingPhTv.setTypeface(Popkon.racho);
    }
}
