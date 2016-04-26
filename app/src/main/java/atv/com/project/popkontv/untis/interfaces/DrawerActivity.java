package atv.com.project.popkontv.untis.interfaces;

import android.content.Context;

import atv.com.project.popkontv.Application.Popkon;

/**
 * Created by LeTan 26/04/2016.
 */
public interface DrawerActivity {
    /**
     *
     * @param packageContext
     */
    void nextToStart(Context packageContext, Popkon Popkon);

    /**
     *
     * @param packageContext
     */
    void nextToSearch(Context packageContext);

    /**
     *
     * @param packageContext
     */
    void nextToShare(Context packageContext);

    /**
     *
     * @param packageContext
     */
    void nextToProfile(Context packageContext);

    /**
     *
     * @param packageContext
     */
}
