package atv.com.project.popkontv.Fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by arjun on 5/21/15.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener{
    private TimeSetListener mTimeSetListener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        this.mTimeSetListener = (TimeSetListener)getTargetFragment();
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Log.i("Chosen time", hourOfDay + " " + minute);
        mTimeSetListener.onUserTimeSet(hourOfDay, minute);
    }

    public interface TimeSetListener{
        public abstract void onUserTimeSet(int hour, int minute);
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try{
//            this.mTimeSetListener = (TimeSetListener) activity;
//        } catch (Exception e){
//            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
//        }
//    }
}
