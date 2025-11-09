package androidx.core.app;

import android.app.PendingIntent;
import android.os.Bundle;
import androidx.core.graphics.drawable.IconCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
/* loaded from: classes.dex */
public class NotificationCompat$Action {
    public PendingIntent actionIntent;
    @Deprecated
    public int icon;
    private boolean mAllowGeneratedReplies;
    private boolean mAuthenticationRequired;
    private final RemoteInput[] mDataOnlyRemoteInputs;
    final Bundle mExtras;
    private IconCompat mIcon;
    private final boolean mIsContextual;
    private final RemoteInput[] mRemoteInputs;
    private final int mSemanticAction;
    boolean mShowsUserInterface;
    public CharSequence title;

    NotificationCompat$Action(IconCompat iconCompat, CharSequence charSequence, PendingIntent pendingIntent, Bundle bundle, RemoteInput[] remoteInputArr, RemoteInput[] remoteInputArr2, boolean z, int i, boolean z2, boolean z3, boolean z4) {
        this.mShowsUserInterface = true;
        this.mIcon = iconCompat;
        if (iconCompat != null && iconCompat.getType() == 2) {
            this.icon = iconCompat.getResId();
        }
        this.title = NotificationCompat$Builder.limitCharSequenceLength(charSequence);
        this.actionIntent = pendingIntent;
        this.mExtras = bundle == null ? new Bundle() : bundle;
        this.mRemoteInputs = remoteInputArr;
        this.mDataOnlyRemoteInputs = remoteInputArr2;
        this.mAllowGeneratedReplies = z;
        this.mSemanticAction = i;
        this.mShowsUserInterface = z2;
        this.mIsContextual = z3;
        this.mAuthenticationRequired = z4;
    }

    public IconCompat getIconCompat() {
        int i;
        if (this.mIcon == null && (i = this.icon) != 0) {
            this.mIcon = IconCompat.createWithResource(null, "", i);
        }
        return this.mIcon;
    }

    public CharSequence getTitle() {
        return this.title;
    }

    public PendingIntent getActionIntent() {
        return this.actionIntent;
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    public boolean getAllowGeneratedReplies() {
        return this.mAllowGeneratedReplies;
    }

    public boolean isAuthenticationRequired() {
        return this.mAuthenticationRequired;
    }

    public RemoteInput[] getRemoteInputs() {
        return this.mRemoteInputs;
    }

    public int getSemanticAction() {
        return this.mSemanticAction;
    }

    public boolean isContextual() {
        return this.mIsContextual;
    }

    public boolean getShowsUserInterface() {
        return this.mShowsUserInterface;
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private boolean mAllowGeneratedReplies;
        private boolean mAuthenticationRequired;
        private final Bundle mExtras;
        private final IconCompat mIcon;
        private final PendingIntent mIntent;
        private boolean mIsContextual;
        private ArrayList<RemoteInput> mRemoteInputs;
        private int mSemanticAction;
        private boolean mShowsUserInterface;
        private final CharSequence mTitle;

        public Builder(int i, CharSequence charSequence, PendingIntent pendingIntent) {
            this(i != 0 ? IconCompat.createWithResource(null, "", i) : null, charSequence, pendingIntent, new Bundle(), null, true, 0, true, false, false);
        }

        private Builder(IconCompat iconCompat, CharSequence charSequence, PendingIntent pendingIntent, Bundle bundle, RemoteInput[] remoteInputArr, boolean z, int i, boolean z2, boolean z3, boolean z4) {
            this.mAllowGeneratedReplies = true;
            this.mShowsUserInterface = true;
            this.mIcon = iconCompat;
            this.mTitle = NotificationCompat$Builder.limitCharSequenceLength(charSequence);
            this.mIntent = pendingIntent;
            this.mExtras = bundle;
            this.mRemoteInputs = remoteInputArr == null ? null : new ArrayList<>(Arrays.asList(remoteInputArr));
            this.mAllowGeneratedReplies = z;
            this.mSemanticAction = i;
            this.mShowsUserInterface = z2;
            this.mIsContextual = z3;
            this.mAuthenticationRequired = z4;
        }

        private void checkContextualActionNullFields() {
            if (this.mIsContextual && this.mIntent == null) {
                throw new NullPointerException("Contextual Actions must contain a valid PendingIntent");
            }
        }

        public NotificationCompat$Action build() {
            checkContextualActionNullFields();
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            ArrayList<RemoteInput> arrayList3 = this.mRemoteInputs;
            if (arrayList3 != null) {
                Iterator<RemoteInput> it = arrayList3.iterator();
                while (it.hasNext()) {
                    RemoteInput next = it.next();
                    if (next.isDataOnly()) {
                        arrayList.add(next);
                    } else {
                        arrayList2.add(next);
                    }
                }
            }
            RemoteInput[] remoteInputArr = arrayList.isEmpty() ? null : (RemoteInput[]) arrayList.toArray(new RemoteInput[arrayList.size()]);
            return new NotificationCompat$Action(this.mIcon, this.mTitle, this.mIntent, this.mExtras, arrayList2.isEmpty() ? null : (RemoteInput[]) arrayList2.toArray(new RemoteInput[arrayList2.size()]), remoteInputArr, this.mAllowGeneratedReplies, this.mSemanticAction, this.mShowsUserInterface, this.mIsContextual, this.mAuthenticationRequired);
        }
    }
}
