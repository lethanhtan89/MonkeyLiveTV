package atv.com.project.popkontv.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import atv.com.project.popkontv.untis.helper.DrawerActivityHelper;

/**
 * Created by Administrator on 12/19/15.
 */
public abstract class BaseFragment extends Fragment {
    private static boolean hadDrawer = true;
    private static boolean isBack = false;
    private static boolean isTop = true;
    protected View view;
    protected int id_menu;
    protected Menu menu;
    private Toolbar toolbar;
    private String title;
    private DrawerActivityHelper activityHelper;

    public static boolean isBack() {
        return isBack;
    }

    public static void setIsBack(boolean isBack) {
        BaseFragment.isBack = isBack;
    }

    public static boolean isTop() {
        return isTop;
    }

    public static void setIsTop(boolean isTop) {
        BaseFragment.isTop = isTop;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(getLayoutResourcesId(), container, false);
        //baseActivity = (DasersteActvitiy) getActivity();
        //activityHelper = getBaseActivity().getActivityHelper();
        setHasOptionsMenu(true);

        doWidgets();
        if (!isTop()) {
            Log.v("debug", "anim layout");
           // animLayout();
        }
        return view;
    }


    protected void saveToolbarData(Toolbar toolbar, String title) {
        this.toolbar = toolbar;
        this.title = title;

    }

    public void refreshToolBar(boolean isMain) {
    }

    /**
     * get layout from resources (example : return R.layout.activity_main)
     *
     * @return
     */
    protected abstract int getLayoutResourcesId();

    /**
     * setup all views after inflating main layout.
     */
    protected abstract void doWidgets();

    public DrawerActivityHelper getActivityHelper() {
        return activityHelper;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    public abstract void onAttach(Context context);
}

