package com.peter1303.phonograph.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Genres implements Parcelable {
    public final int id;
    public final String name;
    public final int songCount;

    public Genres(final int id, final String name, final int songCount) {
        this.id = id;
        this.name = name;
        this.songCount = songCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Genres genres = (Genres) o;

        if (id != genres.id) return false;
        if (!name.equals(genres.name)) return false;
        return songCount == genres.songCount;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + songCount;
        return result;
    }

    @Override
    public String toString() {
        return "Genres{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", songCount=" + songCount + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.songCount);
    }

    protected Genres(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.songCount = in.readInt();
    }

    public static final Creator<Genres> CREATOR = new Creator<Genres>() {
        public Genres createFromParcel(Parcel source) {
            return new Genres(source);
        }

        public Genres[] newArray(int size) {
            return new Genres[size];
        }
    };
}
