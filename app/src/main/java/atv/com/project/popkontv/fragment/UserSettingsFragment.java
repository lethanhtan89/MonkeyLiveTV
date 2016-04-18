package atv.com.project.popkontv.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import atv.com.project.popkontv.R;
import atv.com.project.popkontv.videostream.MainActivityVideoStream;

/**
 * Created by Administrator on 4/11/2016.
 */
public class UserSettingsFragment extends Fragment {
    private Button btConnect;

    public UserSettingsFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_settings, container, false);

        btConnect = (Button) view.findViewById(R.id.btconnect);
        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivityVideoStream.class);
                startActivity(intent);
            }
        });
        return view;
    }


}
