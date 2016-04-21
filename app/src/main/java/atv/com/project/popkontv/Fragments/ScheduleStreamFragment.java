package atv.com.project.popkontv.Fragments;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import atv.com.project.popkontv.Activity.LauncherActivity;
import atv.com.project.popkontv.Application.EndPoints;
import atv.com.project.popkontv.Application.Viewora;
import atv.com.project.popkontv.Interfaces.MyCallback;
import atv.com.project.popkontv.Network.MyHttp;
import atv.com.project.popkontv.Pojo.StartStreamContentStream;
import atv.com.project.popkontv.Pojo.StartStreamDetails;
import atv.com.project.popkontv.Pojo.StreamFullDetails;
import atv.com.project.popkontv.R;

/**
 * Created by arjun on 5/21/15.
 */
public class ScheduleStreamFragment extends Fragment implements TimePickerFragment.TimeSetListener {
    private static final String STREAM_SCHEDULE = "stream_schedule";
    private View rootView;
    private TextView timePicker;
//    private TextView addPhoto;
    private TextView scheduleStream;
    private long differenceInMilliSeconds;
    private ProgressBar scheduleStreamProgress;
    private String tweetMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_schedulestream, container, false);
        tweetMessage = getArguments().getString("tweetMsg");
        setUpActionBar();
        initialiseViews();
        registerAlarmReceiver();
        bindEvents();
        return rootView;
    }

    private void registerAlarmReceiver() {
        BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                NotificationManager mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Intent scheduleStreamIntent = new Intent(context, LauncherActivity.class);
                scheduleStreamIntent.putExtra(Viewora.IS_SCHEDULED_STREAM, true);
                scheduleStreamIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.app_icon)
                                .setContentTitle("Stream Schedule Notification")
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText("Go Live Now"))
                                .setAutoCancel(true)
                                .setContentText("Go Live Now");
                PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context, 0, scheduleStreamIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(pendingNotificationIntent);
                mManager.notify(0, mBuilder.build());
            }
        };
        getActivity().getApplicationContext().registerReceiver(alarmReceiver, new IntentFilter(STREAM_SCHEDULE));
    }

    private void bindEvents() {
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.setTargetFragment(ScheduleStreamFragment.this, 54321);
                timePicker.show(getFragmentManager(), "datepicker");
            }
        });
        scheduleStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(differenceInMilliSeconds == 0){
                        DialogFragment timePicker = new TimePickerFragment();
                        timePicker.setTargetFragment(ScheduleStreamFragment.this, 54321);
                        timePicker.show(getFragmentManager(), "datepicker");
                        return;
                    }
                    StartStreamDetails startStreamDetails = new StartStreamDetails();
                    startStreamDetails.stream = new StartStreamContentStream();
                    startStreamDetails.stream.message = tweetMessage;
                    startStreamDetails.stream.state = "scheduled";
                    startStreamDetails.stream.start_time_difference = String.valueOf(differenceInMilliSeconds);

                    MyHttp<StreamFullDetails> request = new MyHttp<>(getActivity(), EndPoints.baseStreamUrl, MyHttp.POST, StreamFullDetails.class)
                            .defaultHeaders()
                            .addJson(startStreamDetails)
                            .send(new MyCallback<StreamFullDetails>() {
                                @Override
                                public void success(StreamFullDetails data) {
                                    AlarmManager scheduleManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                                    Intent startIntent = new Intent(STREAM_SCHEDULE);
                                    int pendingIntentId = 123321;
                                    PendingIntent mPendingIntent = PendingIntent.getBroadcast(getActivity(), pendingIntentId, startIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                                    scheduleManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + differenceInMilliSeconds, mPendingIntent);
//                                    getFragmentManager().popBackStack();
                                    Viewora.setStringPreferenceData(Viewora.STREAM_SLUG, data.message.slug);
                                    getActivity().onBackPressed();
                                }

                                @Override
                                public void failure(String msg) {
                                    if(getActivity() != null)
                                        Toast.makeText(getActivity().getApplicationContext(), "Cannot Start The Stream", Toast.LENGTH_LONG).show();
                                    scheduleStreamProgress.setVisibility(View.GONE);
                                    scheduleStream.setText("SCHEDULE");
                                }

                                @Override
                                public void onBefore() {
                                    scheduleStreamProgress.setVisibility(View.VISIBLE);
                                    scheduleStream.setText("");
                                }

                                @Override
                                public void onFinish() {
                                    scheduleStreamProgress.setVisibility(View.GONE);
                                    scheduleStream.setText("SCHEDULE");
                                }
                            });
            }
        });
    }

    private void initialiseViews() {
        timePicker = (TextView) rootView.findViewById(R.id.timePicker);
        timePicker.setTypeface(Viewora.racho);
//        addPhoto = (TextView) rootView.findViewById(R.id.addPhoto);
        scheduleStream = (TextView) rootView.findViewById(R.id.scheduleStream);
        scheduleStream.setTypeface(Viewora.racho);
        scheduleStreamProgress = (ProgressBar) rootView.findViewById(R.id.scheduleProgress);
    }

    private void setUpActionBar() {

    }

    @Override
    public void onUserTimeSet(int hour, int minute) {
        Log.i("Chosen", hour + " " + minute);
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int currentMin = Calendar.getInstance().get(Calendar.MINUTE);


        int currentMills = convertHoursToMilliseconds(currentHour) + convertMinutesToMilliSeconds(currentMin);
        int setMills = convertHoursToMilliseconds(hour) + convertMinutesToMilliSeconds(minute);
        int diff = setMills - currentMills;
        int differenceInHours;
        int differenceInMinutes;
        if(diff > 0){
            differenceInHours = diff / (convertMinutesToMilliSeconds(60));
            diff = diff % (convertMinutesToMilliSeconds(60));
            differenceInMinutes = diff / (60 * 1000);
        }else {
            diff = (convertHoursToMilliseconds(24)) + diff;
            differenceInHours = diff / (convertMinutesToMilliSeconds(60));
            diff = diff % (convertMinutesToMilliSeconds(60));
            differenceInMinutes = diff / (60 * 1000);
        }
        timePicker.setText(differenceInHours + ":" + differenceInMinutes + " from now");
        differenceInMilliSeconds = convertHoursToMilliseconds(differenceInHours) + convertMinutesToMilliSeconds(differenceInMinutes);
    }

    private int convertMinutesToMilliSeconds(int currentMin) {
        return currentMin * 60 * 1000;
    }

    private int convertHoursToMilliseconds(int currentHour) {
        return currentHour * 60 * 60 * 1000;
    }
}
