package com.example.zenaparty;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class MyEvent implements Parcelable {
    Long event_id;
    String event_name, date, location, time, type,price, description, username;

    public MyEvent() {
    }

    // Implementa Parcelable
    protected MyEvent(Parcel in) {
        if (in.readByte() == 0) {
            event_id = null;
        } else {
            event_id = in.readLong();
        }
        event_name = in.readString();
        date = in.readString();
        location = in.readString();
        time = in.readString();
        type = in.readString();
        price = in.readString();
        description = in.readString();
        username = in.readString();
    }

    public static final Creator<MyEvent> CREATOR = new Creator<MyEvent>() {
        @Override
        public MyEvent createFromParcel(Parcel in) {
            return new MyEvent(in);
        }

        @Override
        public MyEvent[] newArray(int size) {
            return new MyEvent[size];
        }
    };


    public Long getEvent_id() {
        return event_id;
    }

    public String getEvent_name() {
        return event_name;
    }

    public String getDate() {return date;}

    public String getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (event_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(event_id);
        }
        dest.writeString(event_name);
        dest.writeString(date);
        dest.writeString(location);
        dest.writeString(time);
        dest.writeString(type);
        dest.writeString(price);
        dest.writeString(description);
        dest.writeString(username);
    }
}