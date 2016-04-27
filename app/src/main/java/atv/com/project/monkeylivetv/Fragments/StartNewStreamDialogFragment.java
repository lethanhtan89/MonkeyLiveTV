package atv.com.project.monkeylivetv.Fragments;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import atv.com.project.monkeylivetv.Pojo.StreamCategoryResponse;
import atv.com.project.monkeylivetv.Application.MonkeyLive;
import atv.com.project.monkeylivetv.R;
import atv.com.project.monkeylivetv.lib.InstantAutoComplete;
import io.kickflip.sdk.Kickflip;

/**
 * Created by arjun on 6/18/15.
 */
public class StartNewStreamDialogFragment extends DialogFragment {
    private View rootView;
    private EditText tweetText;
    private Button tweetButton;
    private Button scheduleButton;
    private TextView streamPhTv;
    private CheckBox recordStreamCb;
    private InstantAutoComplete categoryAtv;
    private ArrayList<StreamCategoryResponse.Message> categoriesDetailsList;
    private ArrayList<String> categoryNameList = new ArrayList<>();
    private ArrayAdapter<String> categoriesAdapter;
    private Integer selectedCategoryId = -1;
    private ArrayList<Integer> categoryIdList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_new_stream, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        initialiseViews();
        bindEvents();
        return rootView;
    }
    @Override
    public void onStart() {
        super.onStart();

        if(getDialog() == null){
            return;
        }
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        int width;
        if(Build.VERSION.SDK_INT > 12) {
            display.getSize(size);
            width = size.x;
        }else{
            //noinspection deprecation
            width = display.getWidth();
        }
        int dialogWidth = width - 20;
        int dialogHeight = WindowManager.LayoutParams.WRAP_CONTENT;

        Window currentWindow =  getDialog().getWindow();
        currentWindow.setLayout(dialogWidth, dialogHeight);
    }
    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }
    private void bindEvents() {
        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent streamIntent = new Intent(getActivity(), StreamingActivity.class);
//                streamIntent.putExtra("tweetMsg", tweetText.getText().toString());
//                streamIntent.putExtra("doRecord", recordStreamCb.isChecked());
//                startActivity(streamIntent);
                Kickflip.sContext = MonkeyLive.context;
                String selectedCategory = categoryAtv.getText().toString();
                int selectedIndex = categoryNameList.indexOf(selectedCategory);
                if(selectedIndex != -1) {
                    selectedCategoryId = categoryIdList.get(selectedIndex);
                }
                Kickflip.startBroadcastActivity(getActivity(), tweetText.getText().toString(), recordStreamCb.isChecked(), selectedCategoryId);
                tweetText.setText("");
                getDialog().dismiss();
//                Intent streamIntent = new Intent(getActivity(), BroadcastActivity.class);
//                startActivity(streamIntent);
            }
        });

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleStream();
            }
        });
    }
    private void scheduleStream() {
//        AlarmManager scheduleManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
//        Intent startIntent = new Intent(getActivity(), LauncherActivity.class);
//        int pendingIntentId = 123321;
//        PendingIntent mPendingIntent = PendingIntent.getActivity(getActivity(), pendingIntentId, startIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//        scheduleManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, mPendingIntent);
        ScheduleStreamFragment scheduleStreamFragment = new ScheduleStreamFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tweetMsg", tweetText.getText().toString());
        scheduleStreamFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.launcherContainer, scheduleStreamFragment)
                .addToBackStack("mainfragment")
                .commit();
        tweetText.setText("");
        getDialog().dismiss();
    }


    private void initialiseViews() {
        tweetText = (EditText) rootView.findViewById(R.id.tweetMessage);
        streamPhTv = (TextView) rootView.findViewById(R.id.streamsPhTv);

        categoryAtv = (InstantAutoComplete) rootView.findViewById(R.id.stream_category);
        categoriesAdapter = new ArrayAdapter<>(getActivity(), R.layout.single_list_item, categoryNameList);
        categoryAtv.setAdapter(categoriesAdapter);
        categoryAtv.setTypeface(MonkeyLive.racho);

        tweetButton = (Button) rootView.findViewById(R.id.streamBtn);
        scheduleButton = (Button) rootView.findViewById(R.id.scheduleBtn);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tweetText.setPadding(5, 5, 5, 5);

        tweetText.setTypeface(MonkeyLive.racho);
        streamPhTv.setTypeface(MonkeyLive.racho);
        tweetButton.setTypeface(MonkeyLive.racho);
        scheduleButton.setTypeface(MonkeyLive.racho);
        recordStreamCb = (CheckBox) rootView.findViewById(R.id.recordStreamCb);
        recordStreamCb.setTypeface(MonkeyLive.racho);
    }

    public void populateCategories(ArrayList<StreamCategoryResponse.Message> categoriesDetailsList){
        this.categoriesDetailsList = categoriesDetailsList;
        for(StreamCategoryResponse.Message category : categoriesDetailsList){
            categoryNameList.add(category.categoryName);
            categoryIdList.add(category.categoryId);
        }
    }
}