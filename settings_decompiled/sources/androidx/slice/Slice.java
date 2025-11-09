package androidx.slice;

import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.net.Uri;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.util.Preconditions;
import androidx.versionedparcelable.CustomVersionedParcelable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
/* loaded from: classes.dex */
public final class Slice extends CustomVersionedParcelable {
    static final String[] NO_HINTS = new String[0];
    static final SliceItem[] NO_ITEMS = new SliceItem[0];
    String[] mHints;
    SliceItem[] mItems;
    SliceSpec mSpec;
    String mUri;

    public void onPreParceling(boolean z) {
    }

    Slice(ArrayList<SliceItem> arrayList, String[] strArr, Uri uri, SliceSpec sliceSpec) {
        this.mSpec = null;
        this.mItems = NO_ITEMS;
        this.mUri = null;
        this.mHints = strArr;
        this.mItems = (SliceItem[]) arrayList.toArray(new SliceItem[arrayList.size()]);
        this.mUri = uri.toString();
        this.mSpec = sliceSpec;
    }

    public Slice() {
        this.mSpec = null;
        this.mItems = NO_ITEMS;
        this.mHints = NO_HINTS;
        this.mUri = null;
    }

    public SliceSpec getSpec() {
        return this.mSpec;
    }

    public Uri getUri() {
        return Uri.parse(this.mUri);
    }

    public List<SliceItem> getItems() {
        return Arrays.asList(this.mItems);
    }

    public SliceItem[] getItemArray() {
        return this.mItems;
    }

    public List<String> getHints() {
        return Arrays.asList(this.mHints);
    }

    public String[] getHintArray() {
        return this.mHints;
    }

    public boolean hasHint(String str) {
        return ArrayUtils.contains(this.mHints, str);
    }

    public void onPostParceling() {
        for (int length = this.mItems.length - 1; length >= 0; length--) {
            SliceItem[] sliceItemArr = this.mItems;
            SliceItem sliceItem = sliceItemArr[length];
            if (sliceItem.mObj == null) {
                SliceItem[] sliceItemArr2 = (SliceItem[]) ArrayUtils.removeElement(SliceItem.class, sliceItemArr, sliceItem);
                this.mItems = sliceItemArr2;
                if (sliceItemArr2 == null) {
                    this.mItems = new SliceItem[0];
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private int mChildId;
        private SliceSpec mSpec;
        private final Uri mUri;
        private ArrayList<SliceItem> mItems = new ArrayList<>();
        private ArrayList<String> mHints = new ArrayList<>();

        public Builder(Uri uri) {
            this.mUri = uri;
        }

        public Builder(Builder builder) {
            this.mUri = builder.getChildUri();
        }

        private Uri getChildUri() {
            Uri.Builder appendPath = this.mUri.buildUpon().appendPath("_gen");
            int i = this.mChildId;
            this.mChildId = i + 1;
            return appendPath.appendPath(String.valueOf(i)).build();
        }

        public Builder setSpec(SliceSpec sliceSpec) {
            this.mSpec = sliceSpec;
            return this;
        }

        public Builder addHints(String... strArr) {
            this.mHints.addAll(Arrays.asList(strArr));
            return this;
        }

        public Builder addHints(List<String> list) {
            return addHints((String[]) list.toArray(new String[list.size()]));
        }

        public Builder addSubSlice(Slice slice) {
            Preconditions.checkNotNull(slice);
            return addSubSlice(slice, null);
        }

        public Builder addSubSlice(Slice slice, String str) {
            Preconditions.checkNotNull(slice);
            this.mItems.add(new SliceItem(slice, "slice", str, slice.getHintArray()));
            return this;
        }

        public Builder addAction(PendingIntent pendingIntent, Slice slice, String str) {
            Preconditions.checkNotNull(pendingIntent);
            Preconditions.checkNotNull(slice);
            this.mItems.add(new SliceItem(pendingIntent, slice, "action", str, slice.getHintArray()));
            return this;
        }

        public Builder addText(CharSequence charSequence, String str, String... strArr) {
            this.mItems.add(new SliceItem(charSequence, "text", str, strArr));
            return this;
        }

        public Builder addText(CharSequence charSequence, String str, List<String> list) {
            return addText(charSequence, str, (String[]) list.toArray(new String[list.size()]));
        }

        public Builder addIcon(IconCompat iconCompat, String str, String... strArr) {
            Preconditions.checkNotNull(iconCompat);
            if (Slice.isValidIcon(iconCompat)) {
                this.mItems.add(new SliceItem(iconCompat, "image", str, strArr));
            }
            return this;
        }

        public Builder addIcon(IconCompat iconCompat, String str, List<String> list) {
            Preconditions.checkNotNull(iconCompat);
            return Slice.isValidIcon(iconCompat) ? addIcon(iconCompat, str, (String[]) list.toArray(new String[list.size()])) : this;
        }

        public Builder addRemoteInput(RemoteInput remoteInput, String str, List<String> list) {
            Preconditions.checkNotNull(remoteInput);
            return addRemoteInput(remoteInput, str, (String[]) list.toArray(new String[list.size()]));
        }

        public Builder addRemoteInput(RemoteInput remoteInput, String str, String... strArr) {
            Preconditions.checkNotNull(remoteInput);
            this.mItems.add(new SliceItem(remoteInput, "input", str, strArr));
            return this;
        }

        public Builder addInt(int i, String str, String... strArr) {
            this.mItems.add(new SliceItem(Integer.valueOf(i), "int", str, strArr));
            return this;
        }

        public Builder addInt(int i, String str, List<String> list) {
            return addInt(i, str, (String[]) list.toArray(new String[list.size()]));
        }

        public Builder addLong(long j, String str, String... strArr) {
            this.mItems.add(new SliceItem(Long.valueOf(j), "long", str, strArr));
            return this;
        }

        public Builder addLong(long j, String str, List<String> list) {
            return addLong(j, str, (String[]) list.toArray(new String[list.size()]));
        }

        @Deprecated
        public Builder addTimestamp(long j, String str, String... strArr) {
            this.mItems.add(new SliceItem(Long.valueOf(j), "long", str, strArr));
            return this;
        }

        public Builder addItem(SliceItem sliceItem) {
            this.mItems.add(sliceItem);
            return this;
        }

        public Slice build() {
            ArrayList<SliceItem> arrayList = this.mItems;
            ArrayList<String> arrayList2 = this.mHints;
            return new Slice(arrayList, (String[]) arrayList2.toArray(new String[arrayList2.size()]), this.mUri, this.mSpec);
        }
    }

    public String toString() {
        return toString("");
    }

    public String toString(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("Slice ");
        String[] strArr = this.mHints;
        if (strArr.length > 0) {
            appendHints(sb, strArr);
            sb.append(' ');
        }
        sb.append('[');
        sb.append(this.mUri);
        sb.append("] {\n");
        String str2 = str + "  ";
        int i = 0;
        while (true) {
            SliceItem[] sliceItemArr = this.mItems;
            if (i < sliceItemArr.length) {
                sb.append(sliceItemArr[i].toString(str2));
                i++;
            } else {
                sb.append(str);
                sb.append('}');
                return sb.toString();
            }
        }
    }

    public static void appendHints(StringBuilder sb, String[] strArr) {
        if (strArr == null || strArr.length == 0) {
            return;
        }
        sb.append('(');
        int length = strArr.length - 1;
        for (int i = 0; i < length; i++) {
            sb.append(strArr[i]);
            sb.append(", ");
        }
        sb.append(strArr[length]);
        sb.append(")");
    }

    public static Slice bindSlice(Context context, Uri uri, Set<SliceSpec> set) {
        return callBindSlice(context, uri, set);
    }

    private static Slice callBindSlice(Context context, Uri uri, Set<SliceSpec> set) {
        return SliceConvert.wrap(((android.app.slice.SliceManager) context.getSystemService(android.app.slice.SliceManager.class)).bindSlice(uri, SliceConvert.unwrap(set)), context);
    }

    static boolean isValidIcon(IconCompat iconCompat) {
        if (iconCompat == null) {
            return false;
        }
        if (iconCompat.mType == 2 && iconCompat.getResId() == 0) {
            throw new IllegalArgumentException("Failed to add icon, invalid resource id: " + iconCompat.getResId());
        }
        return true;
    }
}
