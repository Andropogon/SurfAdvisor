package livewind.example.andro.liveWind;

import android.content.Context;

public class MyMember {

    private String mName;
    private int mType;
    private String mPhotoName;

    public MyMember() {
        mName = "default";
    }

    public MyMember(String name,int type, String photoName, Context context){
        if(name==null){
            mName = "Unknown";
            mType = 0;
        }

        if(photoName==null){
            mPhotoName = context.getResources().getResourceEntryName(R.drawable.user_icon_shaka_24);
        }
        mName=name;
        mType=type;
        mPhotoName =photoName;
    }
    public MyMember(String name,int type){
        mName=name;
        mType=type;
    }

    public String getmName() {
        return mName;
    }
    public void setmName(String name){
        mName=name;
    }
    public int getmType(){return mType;}

    public String getmPhotoName() {
        return mPhotoName;
    }

    public void setmPhotoName(String mPhotoName) {
        this.mPhotoName = mPhotoName;
    }
}
