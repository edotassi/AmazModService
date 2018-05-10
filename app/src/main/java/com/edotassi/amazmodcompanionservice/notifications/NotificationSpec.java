package com.edotassi.amazmodcompanionservice.notifications;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class NotificationSpec implements Parcelable {

    public static final String EXTRA = "notificationSpec";

    private int id;
    private String title;
    private String text;
    private Bitmap icon;
    private int vibration;
    private boolean enableCutomUI;
    private boolean isDeviceLocked;
    private int timeoutRelock;

    public NotificationSpec() {}

    protected NotificationSpec(Parcel in) {
        id = in.readInt();
        title = in.readString();
        text = in.readString();
        icon = in.readParcelable(Bitmap.class.getClassLoader());
        vibration = in.readInt();
        enableCutomUI = in.readByte() != 0;
        isDeviceLocked = in.readByte() != 0;
        timeoutRelock = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(text);
        dest.writeParcelable(icon, flags);
        dest.writeInt(vibration);
        dest.writeByte((byte) (enableCutomUI ? 1 : 0));
        dest.writeByte((byte) (isDeviceLocked ? 1 : 0));
        dest.writeInt(timeoutRelock);
    }

    public static final Creator<NotificationSpec> CREATOR = new Creator<NotificationSpec>() {
        @Override
        public NotificationSpec createFromParcel(Parcel in) {
            return new NotificationSpec(in);
        }

        @Override
        public NotificationSpec[] newArray(int size) {
            return new NotificationSpec[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public boolean isEnableCutomUI() {
        return enableCutomUI;
    }

    public void setEnableCutomUI(boolean enableCutomUI) {
        this.enableCutomUI = enableCutomUI;
    }

    public boolean isDeviceLocked() {
        return isDeviceLocked;
    }

    public void setDeviceLocked(boolean deviceLocked) {
        isDeviceLocked = deviceLocked;
    }

    public int getVibration() {
        return vibration;
    }

    public void setVibration(int vibration) {
        this.vibration = vibration;
    }

    public int getTimeoutRelock() {
        return timeoutRelock;
    }

    public void setTimeoutRelock(int timeoutRelock) {
        this.timeoutRelock = timeoutRelock;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
