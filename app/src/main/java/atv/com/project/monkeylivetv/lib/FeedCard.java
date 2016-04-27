package atv.com.project.monkeylivetv.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import atv.com.project.monkeylivetv.Application.MonkeyLive;
import atv.com.project.monkeylivetv.Interfaces.MyCallback;
import atv.com.project.monkeylivetv.Network.MyHttp;
import atv.com.project.monkeylivetv.R;

import it.gmariotti.cardslib.library.internal.Card;

public class FeedCard extends Card {

    protected Context context;
    protected String title;

    public String streamUserName;
    public String streamUserHandle;
    public Integer streamType;
    public String streamMessage;
    public String streamLocation;
    public Integer streamLikesCount;
    public Integer streamCommentsCount;
    public Integer streamReTweetsCount;
    public String streamUserImage;
    public String streamBackground;

    public TextView streamUserNameTv;
//    public TextView streamUserHandleTv;
    public TextView streamTypeTv;
    public TextView streamMessageTv;
    public TextView streamLocationTv;
//    public TextView streamLikesCountTv;
//    public TextView streamCommentsCountTv;
//    public TextView streamReTweetsCountTv;
    public ImageView streamUserImageIv;
    public ImageView streamCoverImageIv;
    private TextView streamStatusTv;
    private boolean isRecorded;
//    public LinearLayout streamBackgroundLl;


    public FeedCard(Context context) {
        this(context, R.layout.featuredstream_item);
        this.context = context;
    }

    public FeedCard(Context context, int innerLayout) {
        super(context, innerLayout);
        //init();
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        //Retrieve elements
        streamUserNameTv = (TextView)view.findViewById(R.id.streamUserName);
        streamUserImageIv = (ImageView)view.findViewById(R.id.streamUserImage);
        streamStatusTv = (TextView)view.findViewById(R.id.stream_status_textview);
//        streamLocationTv = (TextView)view.findViewById(R.id.streamLocation);
//        streamTypeTv = (TextView)view.findViewById(R.id.stream_watchers_count);
        streamCoverImageIv = (ImageView) view.findViewById(R.id.streamCoverImage);
        streamMessageTv = (TextView)view.findViewById(R.id.streamDescription);
//        streamLikesCountTv = (TextView)view.findViewById(R.id.streamLikes);
//        streamCommentsCountTv = (TextView)view.findViewById(R.id.streamComments);
//        streamReTweetsCountTv = (TextView)view.findViewById(R.id.streamReStreams);
//        streamUserHandleTv = (TextView)view.findViewById(R.id.streamUserHandle);
//        streamBackgroundLl = (LinearLayout) view.findViewById(R.id.streamImageLayout);
        streamUserNameTv.setTypeface(MonkeyLive.racho);
        streamStatusTv.setTypeface(MonkeyLive.racho);
//        streamUserHandleTv.setTypeface(Castasy.racho);
        streamTypeTv.setTypeface(MonkeyLive.racho);
        streamMessageTv.setTypeface(MonkeyLive.racho);
//        streamLikesCountTv.setTypeface(Castasy.racho);
//        streamCommentsCountTv.setTypeface(Castasy.racho);
//        streamReTweetsCountTv.setTypeface(Castasy.racho);
        streamLocationTv.setTypeface(MonkeyLive.racho);

        streamUserNameTv.setText(streamUserName);
//        streamUserHandleTv.setText(streamUserHandle);
        if(isRecorded) {
            streamStatusTv.setText("Recorded");
        } else {
            streamStatusTv.setText("Live");
        }
        streamMessageTv.setText(streamMessage);
        streamTypeTv.setText(streamType + " watchers");
//        streamLikesCountTv.setText(streamLikesCount + "");
//        streamCommentsCountTv.setText(streamCommentsCount + "");
        streamLocationTv.setText(streamLocation);
//        streamReTweetsCountTv.setText(streamReTweetsCount + "");
        MyHttp.fetchBitmap(context, streamUserImage, new MyCallback<Bitmap>() {
            @Override
            public void success(Bitmap data) {
                StreamDrawable streamDrawable = new StreamDrawable(data);
                streamUserImageIv.setImageDrawable(streamDrawable);
            }

            @Override
            public void failure(String msg) {

            }

            @Override
            public void onBefore() {
                streamUserImageIv.setImageDrawable(context.getResources().getDrawable(R.drawable.defaultuser));
            }

            @Override
            public void onFinish() {

            }
        });
        MyHttp.fetchBitmap(context, streamBackground, new MyCallback<Bitmap>() {
            @Override
            public void success(Bitmap data) {
                Drawable streamDrawable = new BitmapDrawable(context.getResources(), data);
                streamCoverImageIv.setImageDrawable(streamDrawable);
            }

            @Override
            public void failure(String msg) {

            }

            @Override
            public void onBefore() {
//                viewHolder.streamUserImage.setImageDrawable(context.getResources().getDrawable(R.drawable.defaultuser));
            }

            @Override
            public void onFinish() {

            }
        });

    }

    public void setStreamUserHandle(String streamUserHandle) {
        this.streamUserHandle = streamUserHandle;
    }
    public void setStreamMessage(String streamMessage) {
        this.streamMessage = streamMessage;
    }
    public void setStreamUserName(String streamUserName) {
        this.streamUserName= streamUserName;
    }
    public void setStreamLocation(String streamLocation) {
        this.streamLocation = streamLocation;
    }
    public void setStreamLikesCount(Integer streamLikesCount) {
        this.streamLikesCount = streamLikesCount;
    }
    public void setStreamCommentsCount(Integer streamCommentsCount) {
        this.streamCommentsCount = streamCommentsCount;
    }
    public void setStreamReTweetsCount(Integer streamReTweetsCount) {
        this.streamReTweetsCount = streamReTweetsCount;
    }
    public void setStreamType(Integer streamType) {
        this.streamType = streamType;
    }
    public void setStreamUserImage(String streamUserImage) {
        this.streamUserImage = streamUserImage;
    }
    public void setStreamBackground(String streamBackground) {
        this.streamBackground = streamBackground;
    }
    public void setStreamStatus(boolean isRecorded) {
        this.isRecorded = isRecorded;
    }

}


