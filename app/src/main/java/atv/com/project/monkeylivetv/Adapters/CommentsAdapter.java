package atv.com.project.monkeylivetv.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import atv.com.project.monkeylivetv.Application.MonkeyLive;
import atv.com.project.monkeylivetv.Pojo.Feed;
import atv.com.project.monkeylivetv.R;

/**
 * Created by arjun on 5/12/15.
 */
public class CommentsAdapter extends ArrayAdapter<Feed>{
    private final Context context;
    private final List<Feed> commentsList;

    public CommentsAdapter(Context context, int resource, List<Feed> objects) {
        super(context, resource, objects);
        this.context = context;
        this.commentsList = objects;
    }

    static class ViewHolder{
        TextView comment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Feed feed = commentsList.get(position);
        ViewHolder viewHolder;
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.comment_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.comment = (TextView) convertView.findViewById(R.id.commentListItem);
            viewHolder.comment.setTypeface(MonkeyLive.racho);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        SpannableString s = new SpannableString(feed.handle + " " +  feed.message);

        ForegroundColorSpan fcp = new ForegroundColorSpan(Color.parseColor("#FF496E"));
        s.setSpan(fcp,0,feed.handle.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan fcp1;
        switch (feed.getType()){
            case Feed.TYPE_COMMENT:
                fcp1 = new ForegroundColorSpan(Color.parseColor("#FFFFFF"));
                break;


            case Feed.TYPE_LIKE:
                fcp1 = new ForegroundColorSpan(Color.parseColor("#6D6284"));
                break;


            case Feed.TYPE_RESTREAM:
                fcp1 = new ForegroundColorSpan(Color.parseColor("#2B8792"));
                break;

            default:
                fcp1 = new ForegroundColorSpan(Color.parseColor("#FFFFFF"));
        }

        s.setSpan(fcp1,feed.handle.length()+1,feed.handle.length() + 1 + feed.message.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewHolder.comment.setText(s);
        return convertView;
    }
}
