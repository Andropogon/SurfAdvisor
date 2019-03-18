package livewind.example.andro.liveWind.Comments;

import android.content.Context;

import livewind.example.andro.liveWind.R;

public class Comment {
    private String userUsername;
    private String userPhotoName;
    private String commentText;
    private int likes;
    private long timestamp;
    private String commentNumber;

    public Comment(){};
    public Comment(String mUsername, String mUserPhotoId, String mCommentText,long mTimestamp){
        userUsername = mUsername;
        userPhotoName = mUserPhotoId;
        commentText = mCommentText;
        timestamp = mTimestamp;
    }
    public Comment(String mUsername, String mUserPhotoId, String mCommentText, String mCommentNumber){
        userUsername = mUsername;
        userPhotoName = mUserPhotoId;
        commentText = mCommentText;
        commentNumber = mCommentNumber;
    }
    public Comment(String mUsername, String mCommentText, long mTimestamp,Context context){
        userUsername = mUsername;
        userPhotoName = context.getResources().getResourceEntryName(R.drawable.user_icon_shaka_24);;
        commentText = mCommentText;
        timestamp = mTimestamp;
    }
    public Comment(String mUsername, String mCommentText, Context context){
        userUsername = mUsername;
        userPhotoName = context.getResources().getResourceEntryName(R.drawable.user_icon_shaka_24);
        commentText = mCommentText;
    }
    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setUserPhotoName(String userPhotoName) {
        this.userPhotoName = userPhotoName;
    }

    public void setUserUsername(String userUsername) {
        this.userUsername = userUsername;
    }

    public int getLikes() {
        return likes;
    }

    public String getUserPhotoName() {
        return userPhotoName;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getUserUsername() {
        return userUsername;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setCommentNumber(String commentNumber) {
        this.commentNumber = commentNumber;
    }

    public String getCommentNumber() {
        return commentNumber;
    }
}
