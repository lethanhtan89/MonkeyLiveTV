package atv.com.project.popkontv.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import atv.com.project.popkontv.Application.Viewora;
import atv.com.project.popkontv.Fragments.UserProfileDialogFragment;
import atv.com.project.popkontv.Interfaces.MyCallback;
import atv.com.project.popkontv.Network.MyHttp;
import atv.com.project.popkontv.Pojo.StreamFeedDetails;
import atv.com.project.popkontv.R;
import atv.com.project.popkontv.lib.ImageDownloader;
import atv.com.project.popkontv.lib.StreamDrawable;
import it.gmariotti.cardslib.library.view.CardViewNative;
import sdk.Kickflip;

//import org.videolan.vlc.gui.video.VideoPlayerActivity;

/**
 * Created by arjun on 5/20/15.
 */
public class StreamFeedAdapter extends ArrayAdapter<StreamFeedDetails.Message> {
    private final Context context;
    private final ArrayList<StreamFeedDetails.Message> feedList;
    public ImageDownloader imageDownloader;

    public StreamFeedAdapter(Context context, ArrayList<StreamFeedDetails.Message> objects) {
        super(context, -1, objects);
        this.context = context;
        this.feedList = objects;
//        imageDownloader = new ImageDownloader();
//        imageDownloader.clearCache();
    }


    static class ViewHolder{
        public TextView streamUserName;
//        public TextView streamUserHandle;
//        public TextView streamType;
        public TextView streamMessage;
        public TextView streamStatus;
//        public TextView streamLocation;
        public TextView streamWatchersCount;
//        public TextView streamCommentsCount;
//        public TextView streamReTweetsCount;
        public ImageView streamUserImage;
//        public RelativeLayout streamBackground;
        public CardViewNative baseCardView;
        public ImageView streamCoverImageIv;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final StreamFeedDetails.Message currentFeed = feedList.get(position);
        final ViewHolder viewHolder;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.stream_feed_card, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.baseCardView = (CardViewNative) convertView.findViewById(R.id.stream_feed_card_view);
            if(Build.VERSION.SDK_INT >= 20){
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(20, 20, 20, 20);
                viewHolder.baseCardView.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(8, 8, 8, 8);
                viewHolder.baseCardView.setLayoutParams(params);
            }
            viewHolder.streamUserName = (TextView)convertView.findViewById(R.id.streamUserName);
//            viewHolder.streamUserHandle = (TextView)convertView.findViewById(R.id.streamUserHandle);
//            viewHolder.streamType = (TextView)convertView.findViewById(R.id.stream_watchers_count);
            viewHolder.streamMessage = (TextView)convertView.findViewById(R.id.streamDescription);
            viewHolder.streamStatus = (TextView)convertView.findViewById(R.id.stream_status_textview);
            viewHolder.streamWatchersCount = (TextView)convertView.findViewById(R.id.stream_watchers_count);
//            viewHolder.streamLocation = (TextView)convertView.findViewById(R.id.streamLocation);
//            viewHolder.streamCommentsCount = (TextView)convertView.findViewById(R.id.streamComments);
//            viewHolder.streamReTweetsCount = (TextView)convertView.findViewById(R.id.streamReStreams);
            viewHolder.streamUserImage = (ImageView)convertView.findViewById(R.id.streamUserImage);
            viewHolder.streamCoverImageIv = (ImageView) convertView.findViewById(R.id.streamCoverImage);
//            viewHolder.streamBackground = (RelativeLayout) convertView.findViewById(R.id.streamImageLayout);
            viewHolder.streamUserName.setTypeface(Viewora.racho);
//            viewHolder.streamStatus.setTypeface(Castasy.racho);
//            viewHolder.streamCommentsCount.setTypeface(Castasy.racho);
            viewHolder.streamWatchersCount.setTypeface(Viewora.racho);
            viewHolder.streamMessage.setTypeface(Viewora.racho);
//            viewHolder.streamReTweetsCount.setTypeface(Castasy.racho);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.streamUserName.setText(currentFeed.publisher.publisherName);
//        viewHolder.streamUserHandle.setText(currentFeed.publisher.publisherHandle);
        viewHolder.streamMessage.setText(currentFeed.streamMessage);
//        viewHolder.streamLocation.setText(currentFeed.publisher.city);
        viewHolder.streamWatchersCount.setText(String.valueOf(currentFeed.totalSubscribers));
//        viewHolder.streamCommentsCount.setText(String.valueOf(currentFeed.commentsCount));
//        viewHolder.streamReTweetsCount.setText(String.valueOf(currentFeed.retweetsCount));

        if (currentFeed.streamType.equalsIgnoreCase("scheduled")) {
            viewHolder.streamStatus.setText("SCHEDULED");
        } else if (currentFeed.streamType.equalsIgnoreCase("recorded")) {
            viewHolder.streamStatus.setText("RECORDED");
        } else if (currentFeed.streamType.equalsIgnoreCase("uploaded")) {
            viewHolder.streamStatus.setText("UPLOADED");
        } else if (currentFeed.streamType.equalsIgnoreCase("live")) {
            viewHolder.streamStatus.setText("LIVE");
        }
//        viewHolder.streamType.setText(String.valueOf(currentFeed.liveSubscribers) + " Watchers"); //todo: get from server
        MyHttp.fetchBitmap(context, currentFeed.publisher.publisherImage, new MyCallback<Bitmap>() {
            @Override
            public void success(Bitmap data) {
                StreamDrawable streamDrawable = new StreamDrawable(data);
                viewHolder.streamUserImage.setImageDrawable(streamDrawable);
            }

            @Override
            public void failure(String msg) {

            }

            @Override
            public void onBefore() {
                viewHolder.streamUserImage.setImageDrawable(context.getResources().getDrawable(R.drawable.defaultuser));
            }

            @Override
            public void onFinish() {

            }
        });
        String coverThumbnailImage;
        boolean uploaded = currentFeed.streamType.equalsIgnoreCase("uploaded");
        if(uploaded){
            coverThumbnailImage = currentFeed.uploadedVideo.uploadedVideoThumbnail;
        } else {
            coverThumbnailImage = currentFeed.coverImage;
        }
        MyHttp.fetchBitmap(context, coverThumbnailImage, new MyCallback<Bitmap>() {
            @Override
            public void success(Bitmap data) {
//                Drawable streamDrawable = new BitmapDrawable(context.getResources(), data);
//                if(Build.VERSION.SDK_INT > 15) {
//                    viewHolder.streamBackground.setBackground(streamDrawable);
//                }else {
//                    viewHolder.streamBackground.setBackgroundDrawable(streamDrawable);
//                }
//                viewHolder.streamCoverImageIv.setImageDrawable(streamDrawable);
                viewHolder.streamCoverImageIv.setImageBitmap(data);
            }

            @Override
            public void failure(String msg) {

            }

            @Override
            public void onBefore() {
//                viewHolder.streamCoverImageIv.setImageDrawable(null);
                viewHolder.streamCoverImageIv.setImageDrawable(null);
            }

            @Override
            public void onFinish() {

            }
        });
//    imageDownloader.download(currentFeed.coverImage, viewHolder.streamCoverImageIv);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //        final String MRL = "rtmp://184.72.239.149/vod/BigBuckBunny_115k.mov";
//                final String MRL = "rtmp://192.168.10.107/mytv/stream";
//                VideoPlayerActivity.start(context, MRL, true);
                if (currentFeed.streamType.equalsIgnoreCase("scheduled")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Scheduled Stream")
                            .setMessage("The stream is scheduled " + Viewora.timeElapsedAsRelativeTime(currentFeed.startTime))
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.show();
                } else if (currentFeed.streamType.equalsIgnoreCase("recorded")) {
                    Kickflip.startMediaPlayerActivity((AppCompatActivity)context, currentFeed.streamPlayLinks.playUrl, currentFeed.slug, false);
//                    VideoPlayerActivity.start(context, currentFeed.streamPlayLinks.playUrl, true, currentFeed.slug);
                } else if (currentFeed.streamType.equalsIgnoreCase("uploaded")) {
//                    VideoPlayerActivity.start(context, currentFeed.uploadedVideo.uploadedPlayUrl, true, currentFeed.slug);
//                    Kickflip.startMediaPlayerActivity((AppCompatActivity)context, currentFeed.uploadedVideo.playUrl, currentFeed.slug, false);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentFeed.uploadedVideo.browserPlayUrl));
                    context.startActivity(browserIntent);
                } else if (currentFeed.streamType.equalsIgnoreCase("live")){
//                    VideoPlayerActivity.start(context, currentFeed.livePlayUrl, true, currentFeed.slug);
                    Kickflip.startMediaPlayerActivity((AppCompatActivity)context, currentFeed.livePlayUrl, currentFeed.slug, false);
//                    Kickflip.startMediaPlayerActivity((AppCompatActivity)context,"https://s3-ap-southeast-1.amazonaws.com/castasy-vcr/hls/index.m3u8",true);
                }
            }
        });
        viewHolder.streamUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileDialogFragment userProfileDialogFragment = new UserProfileDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("userId", feedList.get(position).publisher.id);
                userProfileDialogFragment.setArguments(bundle);
                userProfileDialogFragment.show(((AppCompatActivity)context).getSupportFragmentManager(), "UserProfileView");
            }
        });

        return convertView;
    }

}
