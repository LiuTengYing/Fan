package com.unisoc.settings.timerpower;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import com.android.settings.R$string;
import java.text.DateFormatSymbols;
import java.util.Calendar;
/* loaded from: classes2.dex */
public final class Alarm implements Parcelable {
    public static final Parcelable.Creator<Alarm> CREATOR = new Parcelable.Creator<Alarm>() { // from class: com.unisoc.settings.timerpower.Alarm.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Alarm createFromParcel(Parcel parcel) {
            return new Alarm(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Alarm[] newArray(int i) {
            return new Alarm[i];
        }
    };
    public Uri alert;
    public DaysOfWeek daysOfWeek;
    public boolean enabled;
    public int hour;
    public int id;
    public String label;
    public int minutes;
    public boolean silent;
    public long time;
    public boolean vibrate;

    /* loaded from: classes2.dex */
    public static class Columns implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://com.android.settings.alarm/timerpower");
        static final String[] ALARM_QUERY_COLUMNS = {"_id", "hour", "minutes", "daysofweek", "alarmtime", "enabled", "vibrate", "message", "alert"};
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeInt(this.enabled ? 1 : 0);
        parcel.writeInt(this.hour);
        parcel.writeInt(this.minutes);
        parcel.writeInt(this.daysOfWeek.getCoded());
        parcel.writeLong(this.time);
        parcel.writeInt(this.vibrate ? 1 : 0);
        parcel.writeString(this.label);
        parcel.writeParcelable(this.alert, i);
        parcel.writeInt(this.silent ? 1 : 0);
    }

    public Alarm(Context context, Cursor cursor) {
        this.id = cursor.getInt(0);
        this.enabled = cursor.getInt(5) == 1;
        this.hour = cursor.getInt(1);
        this.minutes = cursor.getInt(2);
        this.daysOfWeek = new DaysOfWeek(cursor.getInt(3));
        this.time = cursor.getLong(4);
        this.vibrate = cursor.getInt(6) == 1;
        this.label = cursor.getString(7);
        String string = cursor.getString(8);
        Log.v("Alarm :::: hour = " + this.hour + ",minutes = " + this.minutes + ",daysofweek = " + this.daysOfWeek + ",time = " + this.time + ",vibrate = " + this.vibrate + ",label = " + this.label + ",alertString = " + string);
        if ("silent".equals(string)) {
            Log.v("Alarm :::: Alarm is marked as silent");
            this.silent = true;
        } else if (string == null || string.length() == 0) {
        } else {
            this.alert = Uri.parse(string);
            Log.v("Alarm :::: alert = " + this.alert);
        }
    }

    public Alarm(Parcel parcel) {
        this.id = parcel.readInt();
        this.enabled = parcel.readInt() == 1;
        this.hour = parcel.readInt();
        this.minutes = parcel.readInt();
        this.daysOfWeek = new DaysOfWeek(parcel.readInt());
        this.time = parcel.readLong();
        this.vibrate = parcel.readInt() == 1;
        this.label = parcel.readString();
        this.alert = (Uri) parcel.readParcelable(null);
        this.silent = parcel.readInt() == 1;
    }

    public Alarm() {
        this.id = -1;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        this.hour = calendar.get(11);
        this.minutes = calendar.get(12);
        this.daysOfWeek = new DaysOfWeek(0);
    }

    /* loaded from: classes2.dex */
    static final class DaysOfWeek {
        private static int DAYS_ERROR = -1;
        private static int DAY_COUNT;
        private static int[] DAY_MAP;
        private int mDays;

        static {
            int[] iArr = {2, 3, 4, 5, 6, 7, 1};
            DAY_MAP = iArr;
            DAY_COUNT = iArr.length;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public DaysOfWeek(int i) {
            this.mDays = i;
        }

        public String toString(Context context, boolean z) {
            StringBuilder sb = new StringBuilder();
            int i = this.mDays;
            if (i == 0) {
                return z ? context.getText(R$string.never).toString() : "";
            } else if (i == 127) {
                return context.getText(R$string.every_day).toString();
            } else {
                int i2 = 0;
                while (i > 0) {
                    if ((i & 1) == 1) {
                        i2++;
                    }
                    i >>= 1;
                }
                String[] shortWeekdays = new DateFormatSymbols().getShortWeekdays();
                for (int i3 = 0; i3 < 7; i3++) {
                    if ((this.mDays & (1 << i3)) != 0) {
                        sb.append(shortWeekdays[DAY_MAP[i3]]);
                        i2--;
                        if (i2 > 0) {
                            sb.append(" , ");
                        }
                    }
                }
                return sb.toString();
            }
        }

        private boolean isSet(int i) {
            return (this.mDays & (1 << i)) > 0;
        }

        public void set(int i, boolean z) {
            if (z) {
                this.mDays = (1 << i) | this.mDays;
                return;
            }
            this.mDays = (~(1 << i)) & this.mDays;
        }

        public void set(DaysOfWeek daysOfWeek) {
            this.mDays = daysOfWeek.mDays;
        }

        public int getCoded() {
            return this.mDays;
        }

        public boolean[] getBooleanArray() {
            boolean[] zArr = new boolean[7];
            for (int i = 0; i < 7; i++) {
                zArr[i] = isSet(i);
            }
            return zArr;
        }

        public boolean isRepeatSet() {
            return this.mDays != 0;
        }

        private int[] getDaysOfWeekFromCode(int i) {
            int[] iArr = new int[7];
            int i2 = DAY_COUNT;
            iArr[0] = i2;
            iArr[1] = i2;
            iArr[2] = i2;
            iArr[3] = i2;
            iArr[4] = i2;
            iArr[5] = i2;
            iArr[6] = i2;
            int i3 = 0;
            for (int i4 = 0; i4 < DAY_COUNT; i4++) {
                if (((1 << i4) & i) != 0) {
                    iArr[i3] = i4;
                    i3++;
                }
            }
            return iArr;
        }

        public boolean hasDuplicateDate(int i, int i2) {
            int[] daysOfWeekFromCode;
            int i3 = DAYS_ERROR;
            if (i3 != i && i3 != i2) {
                int[] daysOfWeekFromCode2 = getDaysOfWeekFromCode(i);
                for (int i4 : getDaysOfWeekFromCode(i2)) {
                    for (int i5 : daysOfWeekFromCode2) {
                        int i6 = DAY_COUNT;
                        if (i4 != i6 && i5 != i6 && i4 == i5) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        public int getNextAlarm(Calendar calendar) {
            if (this.mDays == 0 || calendar == null) {
                return -1;
            }
            int i = (calendar.get(7) + 5) % 7;
            int i2 = 0;
            while (i2 < 7 && !isSet((i + i2) % 7)) {
                i2++;
            }
            return i2;
        }
    }
}
