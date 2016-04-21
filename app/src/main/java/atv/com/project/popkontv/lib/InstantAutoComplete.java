package atv.com.project.popkontv.lib;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;


/**
 * Created by arjun on 21/1/15.
 */
public class InstantAutoComplete extends AutoCompleteTextView {
    public InstantAutoComplete(Context context) {
        super(context, null);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InstantAutoComplete(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public InstantAutoComplete(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InstantAutoComplete(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public boolean enoughToFilter(){
        return true;
    }
    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        try {
            if (focused && getAdapter() != null) {
                showDropDown();
            }
        }catch (Exception ignored){}
    }
}
