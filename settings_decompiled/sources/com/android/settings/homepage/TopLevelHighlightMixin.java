package com.android.settings.homepage;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.widget.HighlightableTopLevelPreferenceAdapter;
/* loaded from: classes.dex */
public class TopLevelHighlightMixin implements Parcelable, DialogInterface.OnShowListener, DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {
    public static final Parcelable.Creator<TopLevelHighlightMixin> CREATOR = new Parcelable.Creator<TopLevelHighlightMixin>() { // from class: com.android.settings.homepage.TopLevelHighlightMixin.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TopLevelHighlightMixin createFromParcel(Parcel parcel) {
            return new TopLevelHighlightMixin(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TopLevelHighlightMixin[] newArray(int i) {
            return new TopLevelHighlightMixin[i];
        }
    };
    private boolean mActivityEmbedded;
    private String mCurrentKey;
    private DialogInterface mDialog;
    private String mHiddenKey;
    private String mPreviousKey;
    private HighlightableTopLevelPreferenceAdapter mTopLevelAdapter;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public TopLevelHighlightMixin(boolean z) {
        this.mActivityEmbedded = z;
    }

    public TopLevelHighlightMixin(Parcel parcel) {
        this.mCurrentKey = parcel.readString();
        this.mPreviousKey = parcel.readString();
        this.mHiddenKey = parcel.readString();
        this.mActivityEmbedded = parcel.readBoolean();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mCurrentKey);
        parcel.writeString(this.mPreviousKey);
        parcel.writeString(this.mHiddenKey);
        parcel.writeBoolean(this.mActivityEmbedded);
    }

    @Override // android.content.DialogInterface.OnShowListener
    public void onShow(DialogInterface dialogInterface) {
        this.mDialog = dialogInterface;
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        this.mDialog = null;
    }

    @Override // android.content.DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialogInterface) {
        HighlightableTopLevelPreferenceAdapter highlightableTopLevelPreferenceAdapter = this.mTopLevelAdapter;
        if (highlightableTopLevelPreferenceAdapter != null) {
            String str = this.mPreviousKey;
            this.mCurrentKey = str;
            this.mPreviousKey = null;
            highlightableTopLevelPreferenceAdapter.highlightPreference(str, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setActivityEmbedded(boolean z) {
        this.mActivityEmbedded = z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isActivityEmbedded() {
        return this.mActivityEmbedded;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RecyclerView.Adapter onCreateAdapter(TopLevelSettings topLevelSettings, PreferenceScreen preferenceScreen, boolean z) {
        if (TextUtils.isEmpty(this.mCurrentKey)) {
            this.mCurrentKey = getHighlightPrefKeyFromArguments(topLevelSettings.getArguments());
        }
        Log.d("TopLevelHighlightMixin", "onCreateAdapter, pref key: " + this.mCurrentKey);
        HighlightableTopLevelPreferenceAdapter highlightableTopLevelPreferenceAdapter = new HighlightableTopLevelPreferenceAdapter((SettingsHomepageActivity) topLevelSettings.getActivity(), preferenceScreen, topLevelSettings.getListView(), this.mCurrentKey, z);
        this.mTopLevelAdapter = highlightableTopLevelPreferenceAdapter;
        return highlightableTopLevelPreferenceAdapter;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reloadHighlightMenuKey(Bundle bundle) {
        if (this.mTopLevelAdapter == null) {
            return;
        }
        ensureDialogDismissed();
        this.mCurrentKey = getHighlightPrefKeyFromArguments(bundle);
        Log.d("TopLevelHighlightMixin", "reloadHighlightMenuKey, pref key: " + this.mCurrentKey);
        this.mTopLevelAdapter.highlightPreference(this.mCurrentKey, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setHighlightPreferenceKey(String str) {
        if (this.mTopLevelAdapter != null) {
            ensureDialogDismissed();
            this.mPreviousKey = this.mCurrentKey;
            this.mCurrentKey = str;
            this.mTopLevelAdapter.highlightPreference(str, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getHighlightPreferenceKey() {
        return this.mCurrentKey;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void highlightPreferenceIfNeeded() {
        HighlightableTopLevelPreferenceAdapter highlightableTopLevelPreferenceAdapter = this.mTopLevelAdapter;
        if (highlightableTopLevelPreferenceAdapter != null) {
            highlightableTopLevelPreferenceAdapter.requestHighlight();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMenuHighlightShowed(boolean z) {
        if (this.mTopLevelAdapter == null) {
            return;
        }
        ensureDialogDismissed();
        if (z) {
            this.mCurrentKey = this.mHiddenKey;
            this.mHiddenKey = null;
        } else {
            if (this.mHiddenKey == null) {
                this.mHiddenKey = this.mCurrentKey;
            }
            this.mCurrentKey = null;
        }
        this.mTopLevelAdapter.highlightPreference(this.mCurrentKey, z);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setHighlightMenuKey(String str, boolean z) {
        if (this.mTopLevelAdapter == null) {
            return;
        }
        ensureDialogDismissed();
        String lookupPreferenceKey = HighlightableMenu.lookupPreferenceKey(str);
        if (TextUtils.isEmpty(lookupPreferenceKey)) {
            Log.e("TopLevelHighlightMixin", "Invalid highlight menu key: " + str);
            return;
        }
        Log.d("TopLevelHighlightMixin", "Menu key: " + str);
        this.mCurrentKey = lookupPreferenceKey;
        this.mTopLevelAdapter.highlightPreference(lookupPreferenceKey, z);
    }

    private static String getHighlightPrefKeyFromArguments(Bundle bundle) {
        String string = bundle.getString(":settings:fragment_args_key");
        String lookupPreferenceKey = HighlightableMenu.lookupPreferenceKey(string);
        if (TextUtils.isEmpty(lookupPreferenceKey)) {
            Log.e("TopLevelHighlightMixin", "Invalid highlight menu key: " + string);
        } else {
            Log.d("TopLevelHighlightMixin", "Menu key: " + string);
        }
        return lookupPreferenceKey;
    }

    private void ensureDialogDismissed() {
        DialogInterface dialogInterface = this.mDialog;
        if (dialogInterface != null) {
            onCancel(dialogInterface);
            this.mDialog.dismiss();
        }
    }
}
