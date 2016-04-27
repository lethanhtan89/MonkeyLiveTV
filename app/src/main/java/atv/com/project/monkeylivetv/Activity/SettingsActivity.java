package atv.com.project.monkeylivetv.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import atv.com.project.monkeylivetv.R;

/**
 * Created by arjun on 13/4/15.
 */
public class SettingsActivity extends ActionBarActivity {
    public static final String VIDEO_PREFERENCES = "VideoSetting";
    private Button btDone;
    SharedPreferences videoPreferences;
    private EditText etUrl;
    private EditText etVideoCodec;
//    private String[] resolutions = new String[]{"320 x 240", "480 x 320", "640 x 480", "720 x 480", "1280 x 720"};
//    private String[] bitRates = new String[]{"128 Kbps", "256 Kbps", "512 Kbps", "1024 Kbps", "2048 Kbps"};
    private Spinner resolutionSp;
    private Spinner bitRateSp;
    private Spinner frameRateSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoPreferences = getSharedPreferences(VIDEO_PREFERENCES, MODE_PRIVATE);
        setContentView(R.layout.activity_setting);
        resolutionSp = (Spinner) findViewById(R.id.resolution);
        bitRateSp = (Spinner) findViewById(R.id.bitRate);
        frameRateSp = (Spinner) findViewById(R.id.frameRate);
        etUrl = (EditText) findViewById(R.id.rtmpUrl);
        etUrl.setText(videoPreferences.getString("url", "rtmp://192.168.1.20/mytv/stream"));

        btDone = (Button) findViewById(R.id.btDone);
        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(VIDEO_PREFERENCES, MODE_PRIVATE)
                        .edit();
                editor.putString("width", resolutionSp.getSelectedItem().toString().split(" ")[0]);
                editor.putString("height", resolutionSp.getSelectedItem().toString().split(" ")[2]);
                editor.putString("bitRate", bitRateSp.getSelectedItem().toString().split(" ")[0]);
                editor.putString("frameRate", frameRateSp.getSelectedItem().toString());
//                editor.putString("videoCodec", etVideoCodec.getText().toString());
                editor.putString("url", etUrl.getText().toString());
                editor.commit();
//                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(StreamingActivity.SETTINGS_CHANGE));
                Intent mainIntent = new Intent(SettingsActivity.this, StreamingActivity.class);
                startActivity(mainIntent);
            }
        });
    }
}
