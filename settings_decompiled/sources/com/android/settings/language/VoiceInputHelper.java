package com.android.settings.language;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public final class VoiceInputHelper {
    final List<ResolveInfo> mAvailableRecognition;
    final ArrayList<RecognizerInfo> mAvailableRecognizerInfos = new ArrayList<>();
    final Context mContext;
    ComponentName mCurrentRecognizer;

    /* loaded from: classes.dex */
    public static class BaseInfo implements Comparable<BaseInfo> {
        public final CharSequence mAppLabel;
        public final ComponentName mComponentName;
        public final String mKey;
        public final CharSequence mLabel;
        public final String mLabelStr;
        public final ServiceInfo mService;
        public final ComponentName mSettings;

        public BaseInfo(PackageManager packageManager, ServiceInfo serviceInfo, String str) {
            this.mService = serviceInfo;
            ComponentName componentName = new ComponentName(serviceInfo.packageName, serviceInfo.name);
            this.mComponentName = componentName;
            this.mKey = componentName.flattenToShortString();
            this.mSettings = str != null ? new ComponentName(serviceInfo.packageName, str) : null;
            CharSequence loadLabel = serviceInfo.loadLabel(packageManager);
            this.mLabel = loadLabel;
            this.mLabelStr = loadLabel.toString();
            this.mAppLabel = serviceInfo.applicationInfo.loadLabel(packageManager);
        }

        @Override // java.lang.Comparable
        public int compareTo(BaseInfo baseInfo) {
            return this.mLabelStr.compareTo(baseInfo.mLabelStr);
        }
    }

    /* loaded from: classes.dex */
    public static class RecognizerInfo extends BaseInfo {
        public final boolean mSelectableAsDefault;

        public RecognizerInfo(PackageManager packageManager, ServiceInfo serviceInfo, String str, boolean z) {
            super(packageManager, serviceInfo, str);
            this.mSelectableAsDefault = z;
        }
    }

    public VoiceInputHelper(Context context) {
        this.mContext = context;
        this.mAvailableRecognition = context.getPackageManager().queryIntentServices(new Intent("android.speech.RecognitionService"), 128);
    }

    /* JADX WARN: Removed duplicated region for block: B:55:0x00d7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void buildUi() {
        /*
            Method dump skipped, instructions count: 251
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.language.VoiceInputHelper.buildUi():void");
    }
}
