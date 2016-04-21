package atv.com.project.popkontv.lib;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import  atv.com.project.popkontv.R;

import it.gmariotti.cardslib.library.internal.Card;

public class UserProfileCard extends Card {

    protected Context context;
    protected String title;

//    public String streamUserName;
//    public String streamUserHandle;
//    public Integer streamType;
//    public String streamMessage;
//    public String streamLocation;
//    public Integer streamWatchersCount;
//    public Integer streamCommentsCount;
//    public Integer streamReTweetsCount;
//    public String streamUserImage;
//    public String streamBackground;
//
//    public TextView streamUserNameTv;
//    public TextView streamUserHandleTv;
//    public TextView streamTypeTv;
//    public TextView streamMessageTv;
//    public TextView streamLocationTv;
//    public TextView streamLikesCountTv;
//    public TextView streamCommentsCountTv;
//    public TextView streamReTweetsCountTv;
//    public ImageView streamUserImageIv;
//    public LinearLayout streamBackgroundLl;


    public UserProfileCard(Context context) {
        this(context, R.layout.user_profile_view);
        this.context = context;
    }

    public UserProfileCard(Context context, int innerLayout) {
        super(context, innerLayout);
        //init();
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        //Retrieve elements
//        streamUserNameTv = (TextView)view.findViewById(R.id.streamUserName);
//        streamUserHandleTv = (TextView)view.findViewById(R.id.streamUserHandle);
//        streamTypeTv = (TextView)view.findViewById(R.id.streamType);
//        streamMessageTv = (TextView)view.findViewById(R.id.streamDescription);
//        streamLikesCountTv = (TextView)view.findViewById(R.id.streamLikes);
//        streamLocationTv = (TextView)view.findViewById(R.id.streamLocation);
//        streamCommentsCountTv = (TextView)view.findViewById(R.id.streamComments);
//        streamReTweetsCountTv = (TextView)view.findViewById(R.id.streamReStreams);
//        streamUserImageIv = (ImageView)view.findViewById(R.id.streamUserImage);
//        streamBackgroundLl = (LinearLayout) view.findViewById(R.id.streamImageLayout);

//        streamUserNameTv.setText(streamUserName);
//        streamUserHandleTv.setText(streamUserHandle);
//        streamMessageTv.setText(streamMessage);
//        streamTypeTv.setText(streamType + "watchers");
//        streamLikesCountTv.setText(streamWatchersCount + "");
//        streamCommentsCountTv.setText(streamCommentsCount + "");
//        streamLocationTv.setText(streamLocation);
//        streamReTweetsCountTv.setText(streamReTweetsCount + "");
//        MyHttp.fetchBitmap(context, streamUserImage, new MyCallback<Bitmap>() {
//            @Override
//            public void success(Bitmap data) {
//                StreamDrawable streamDrawable = new StreamDrawable(data);
//                streamUserImageIv.setImageDrawable(streamDrawable);
//            }
//
//            @Override
//            public void failure(String msg) {
//
//            }
//
//            @Override
//            public void onBefore() {
//                streamUserImageIv.setImageDrawable(context.getResources().getDrawable(R.drawable.defaultuser));
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        });
//        MyHttp.fetchBitmap(context, streamBackground, new MyCallback<Bitmap>() {
//            @Override
//            public void success(Bitmap data) {
//                Drawable streamDrawable = new BitmapDrawable(context.getResources(), data);
//                if(Build.VERSION.SDK_INT > 15) {
//                    streamBackgroundLl.setBackground(streamDrawable);
//                }else {
//                    streamBackgroundLl.setBackgroundDrawable(streamDrawable);
//                }
//            }
//
//            @Override
//            public void failure(String msg) {
//
//            }
//
//            @Override
//            public void onBefore() {
////                viewHolder.streamUserImage.setImageDrawable(context.getResources().getDrawable(R.drawable.defaultuser));
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        });

    }

//    public void setStreamUserHandle(String streamUserHandle) {
//        this.streamUserHandle = streamUserHandle;
//    }
//    public void setStreamMessage(String streamMessage) {
//        this.streamMessage = streamMessage;
//    }
//    public void setStreamUserName(String streamUserName) {
//        this.streamUserName= streamUserName;
//    }
//    public void setStreamLocation(String streamLocation) {
//        this.streamLocation = streamLocation;
//    }
//    public void setStreamLikesCount(Integer streamWatchersCount) {
//        this.streamWatchersCount = streamWatchersCount;
//    }
//    public void setStreamCommentsCount(Integer streamCommentsCount) {
//        this.streamCommentsCount = streamCommentsCount;
//    }
//    public void setStreamReTweetsCount(Integer streamReTweetsCount) {
//        this.streamReTweetsCount = streamReTweetsCount;
//    }
//    public void setStreamType(Integer streamType) {
//        this.streamType = streamType;
//    }
//    public void setStreamUserImage(String streamUserImage) {
//        this.streamUserImage = streamUserImage;
//    }
//    public void setStreamBackground(String streamBackground) {
//        this.streamBackground = streamBackground;
//    }

}


