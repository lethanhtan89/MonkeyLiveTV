package io.kickflip.sdk.activity;

import android.os.Bundle;
//import sdk.fragment.BroadcastFragment;

/**
// * BroadcastActivity manages a single live broadcast. It's a thin wrapper around {@link io.kickflip.sdk.fragment
 */
public class BroadcastActivity extends ImmersiveActivity {
    private static final String TAG = "BroadcastActivity";

//    private BroadcastFragment mFragment;
//    private BroadcastListener mMainBroadcastListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_broadcast);

//        mMainBroadcastListener = Kickflip.getBroadcastListener();
//        Kickflip.setBroadcastListener(this);

//        if (savedInstanceState == null) {
//            mFragment = BroadcastFragment.getInstance();
//            getFragmentManager().beginTransaction()
//                    .replace(R.id.container, mFragment)
//                    .commit();
//        }
    }

    @Override
    public void onBackPressed() {
//        if (mFragment != null) {
//            mFragment.stopBroadcasting();
//        }
//        super.onBackPressed();
    }

//    @Override
//    public void onBroadcastStart() {
//        mMainBroadcastListener.onBroadcastStart();
//    }
//
//    @Override
//    public void onBroadcastLive(Stream stream) {
//        mMainBroadcastListener.onBroadcastLive(stream);
//    }
//
//    @Override
//    public void onBroadcastStop() {
//        finish();
//        mMainBroadcastListener.onBroadcastStop();
//    }
//
//    @Override
//    public void onBroadcastError(KickflipException error) {
//        mMainBroadcastListener.onBroadcastError(error);
//    }

}
