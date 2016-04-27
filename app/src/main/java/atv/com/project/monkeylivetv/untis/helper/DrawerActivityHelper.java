package atv.com.project.monkeylivetv.untis.helper;

import android.content.Context;
import android.support.v7.app.ActionBar;

import atv.com.project.monkeylivetv.Application.MonkeyLive;
import atv.com.project.monkeylivetv.MainActivity;
import atv.com.project.monkeylivetv.untis.interfaces.DrawerActivity;

/**
 * Created by Administrator on 4/26/2016.
 */
public class DrawerActivityHelper implements DrawerActivity{

    ActionBar actionBar;
    public DrawerActivityHelper(MainActivity activity) {
        //this.actionBar = activity.getActionBar();
    }

    @Override
    public void nextToStart(Context packageContext, MonkeyLive Popkon) {

    }

    @Override
    public void nextToSearch(Context packageContext) {

    }

    @Override
    public void nextToShare(Context packageContext) {

    }

    @Override
    public void nextToProfile(Context packageContext) {

    }
}
