package atv.com.project.popkontv.youtube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.HashMap;
import java.util.Map;

import atv.com.project.popkontv.R;
import atv.com.project.popkontv.content.YouTubeContent;

/**
 * Created by Administrator on 4/7/2016.
 */
public class VideoListAdapter extends BaseAdapter implements YouTubeThumbnailView.OnInitializedListener {

    private Context context;
    private Map<View, YouTubeThumbnailLoader> loaderMap;

    public VideoListAdapter(final Context context){
        this.context = context;
        this.loaderMap = new HashMap<>();
    }
    @Override
    public int getCount() {
        return YouTubeContent.ITEMS.size();
    }

    @Override
    public Object getItem(int position) {
        return YouTubeContent.ITEMS.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VideoHolder holder;
        //The item at the cureent possion
        final YouTubeContent.YouTubeVideo item = YouTubeContent.ITEMS.get(position);
        if(convertView == null){
            final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_layout, parent,false);

            //Create the video Holder
            holder = new VideoHolder();
            //Set the title
            holder.title = (TextView) convertView.findViewById(R.id.textView_title);
            holder.title.setText(item.title);

            //Initialize the thumbnail
            holder.thumb = (YouTubeThumbnailView) convertView.findViewById(R.id.imageView_thumbnail);
            holder.thumb.setTag(item.id);
            holder.thumb.initialize("" + R.string.DEVELOPER_KEY, this);
            convertView.setTag(holder);
        }
        else{
            //Create it again
            holder = (VideoHolder) convertView.getTag();
            final  YouTubeThumbnailLoader loader = loaderMap.get(holder.thumb);

            if(item != null){
                //set the title
                holder.title.setText(item.title);

                //Setting the video id can take a while to actually change the image
                //in the meantime the old image is shown.
                //Removing the image will cause the background color to show instead, not ideal
                //but preferable to flickering images.
                holder.thumb.setImageBitmap(null);

                if(loader == null){
                    //Loader iscurently initialissing
                    holder.thumb.setTag(item.id);
                }
                else {
                    // The loader is readly
                    try{
                        loader.setVideo(item.id);
                    }
                    catch (IllegalStateException e){
                        //If the Loader has been released then remove it from the map and re-init
                        loaderMap.remove(holder.thumb);
                        holder.thumb.initialize("" + R.string.DEVELOPER_KEY, (YouTubeThumbnailView.OnInitializedListener) this);
                    }
                }
            }
        }
        return convertView;
    }

    @Override
    public void onInitializationSuccess(YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
        loaderMap.put(view, loader);
        loader.setVideo((String) view.getTag());
    }

    @Override
    public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult errorReason) {
        final String errorMessage = errorReason.toString();
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
    }

    static class VideoHolder{
        YouTubeThumbnailView thumb;
        TextView title;
    }
}
