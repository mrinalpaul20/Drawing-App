package com.mrinal.zersey.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Exclude;

@SuppressWarnings("unused")
public class Drawing implements Parcelable {

    public static final Parcelable.Creator<Drawing> CREATOR = new Parcelable.Creator<Drawing>() {

        @Override
        public Drawing createFromParcel(Parcel parcel) {
            return new Drawing(parcel);
        }

        @Override
        public Drawing[] newArray(int i) {
            return new Drawing[i];
        }
    };

    private String id;
    private String title;
    private long uploadedAt;
    private String uploaderId;
    private String uploaderName;
    private String uri;

    public Drawing(String title, String uri, FirebaseUser currentUser) {
        this.title = title;
        uploadedAt = Timestamp.now().getSeconds();
        uploaderId = currentUser.getUid();
        uploaderName = currentUser.getDisplayName();
        this.uri = uri;
    }

    public Drawing() {
    }

    private Drawing(Parcel parcel) {
        id = parcel.readString();
        title = parcel.readString();
        uploadedAt = parcel.readLong();
        uploaderId = parcel.readString();
        uploaderName = parcel.readString();
        uri = parcel.readString();
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(long uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(String uploaderId) {
        this.uploaderId = uploaderId;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeLong(uploadedAt);
        parcel.writeString(uploaderId);
        parcel.writeString(uploaderName);
        parcel.writeString(uri);
    }
}