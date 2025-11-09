package com.android.settings.network;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.Settings;
import com.android.settings.network.telephony.MobileNetworkUtils;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class MobileNetworkIntentConverter implements Function<Intent, Intent> {
    private final Context mAppContext;
    private final ComponentName mComponent;
    private static final ComponentName sTargetComponent = ComponentName.createRelative("com.android.settings", Settings.MobileNetworkActivity.class.getTypeName());
    private static final String[] sPotentialActions = {null, "android.intent.action.MAIN", "android.settings.NETWORK_OPERATOR_SETTINGS", "android.settings.DATA_ROAMING_SETTINGS", "android.settings.MMS_MESSAGE_SETTING", "android.telephony.ims.action.SHOW_CAPABILITY_DISCOVERY_OPT_IN"};
    private static final AtomicReference<String> mCachedClassName = new AtomicReference<>();

    public MobileNetworkIntentConverter(Activity activity) {
        this.mAppContext = activity.getApplicationContext();
        this.mComponent = activity.getComponentName();
    }

    @Override // java.util.function.Function
    public Intent apply(final Intent intent) {
        Function andThen;
        long elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        if (isAttachedToExposedComponents()) {
            intent = convertFromDeepLink(intent);
        } else if (!mayRequireConvert(intent)) {
            return null;
        }
        String action = intent.getAction();
        final int extractSubscriptionId = extractSubscriptionId(intent);
        Function identity = Function.identity();
        if (TextUtils.equals(action, "android.settings.NETWORK_OPERATOR_SETTINGS") || TextUtils.equals(action, "android.settings.DATA_ROAMING_SETTINGS")) {
            andThen = identity.andThen(new Function() { // from class: com.android.settings.network.MobileNetworkIntentConverter$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Bundle lambda$apply$0;
                    lambda$apply$0 = MobileNetworkIntentConverter.this.lambda$apply$0(extractSubscriptionId, (Intent) obj);
                    return lambda$apply$0;
                }
            }).andThen(new Function() { // from class: com.android.settings.network.MobileNetworkIntentConverter$$ExternalSyntheticLambda7
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Intent lambda$apply$1;
                    lambda$apply$1 = MobileNetworkIntentConverter.this.lambda$apply$1(intent, (Bundle) obj);
                    return lambda$apply$1;
                }
            }).andThen(new Function() { // from class: com.android.settings.network.MobileNetworkIntentConverter$$ExternalSyntheticLambda8
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Intent lambda$apply$2;
                    lambda$apply$2 = MobileNetworkIntentConverter.this.lambda$apply$2(extractSubscriptionId, (Intent) obj);
                    return lambda$apply$2;
                }
            });
        } else if (TextUtils.equals(action, "android.settings.MMS_MESSAGE_SETTING")) {
            andThen = identity.andThen(new Function() { // from class: com.android.settings.network.MobileNetworkIntentConverter$$ExternalSyntheticLambda9
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Bundle lambda$apply$3;
                    lambda$apply$3 = MobileNetworkIntentConverter.this.lambda$apply$3(extractSubscriptionId, (Intent) obj);
                    return lambda$apply$3;
                }
            }).andThen(new Function() { // from class: com.android.settings.network.MobileNetworkIntentConverter$$ExternalSyntheticLambda10
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Bundle lambda$apply$4;
                    lambda$apply$4 = MobileNetworkIntentConverter.this.lambda$apply$4((Bundle) obj);
                    return lambda$apply$4;
                }
            }).andThen(new Function() { // from class: com.android.settings.network.MobileNetworkIntentConverter$$ExternalSyntheticLambda11
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Intent lambda$apply$5;
                    lambda$apply$5 = MobileNetworkIntentConverter.this.lambda$apply$5(intent, (Bundle) obj);
                    return lambda$apply$5;
                }
            }).andThen(new Function() { // from class: com.android.settings.network.MobileNetworkIntentConverter$$ExternalSyntheticLambda12
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Intent lambda$apply$6;
                    lambda$apply$6 = MobileNetworkIntentConverter.this.lambda$apply$6(extractSubscriptionId, (Intent) obj);
                    return lambda$apply$6;
                }
            });
        } else if (TextUtils.equals(action, "android.telephony.ims.action.SHOW_CAPABILITY_DISCOVERY_OPT_IN")) {
            andThen = identity.andThen(new Function() { // from class: com.android.settings.network.MobileNetworkIntentConverter$$ExternalSyntheticLambda13
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Bundle lambda$apply$7;
                    lambda$apply$7 = MobileNetworkIntentConverter.this.lambda$apply$7(extractSubscriptionId, (Intent) obj);
                    return lambda$apply$7;
                }
            }).andThen(new Function() { // from class: com.android.settings.network.MobileNetworkIntentConverter$$ExternalSyntheticLambda14
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Bundle lambda$apply$8;
                    lambda$apply$8 = MobileNetworkIntentConverter.this.lambda$apply$8(extractSubscriptionId, (Bundle) obj);
                    return lambda$apply$8;
                }
            }).andThen(new Function() { // from class: com.android.settings.network.MobileNetworkIntentConverter$$ExternalSyntheticLambda15
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Intent lambda$apply$9;
                    lambda$apply$9 = MobileNetworkIntentConverter.this.lambda$apply$9(intent, (Bundle) obj);
                    return lambda$apply$9;
                }
            }).andThen(new Function() { // from class: com.android.settings.network.MobileNetworkIntentConverter$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Intent lambda$apply$10;
                    lambda$apply$10 = MobileNetworkIntentConverter.this.lambda$apply$10(extractSubscriptionId, (Intent) obj);
                    return lambda$apply$10;
                }
            });
        } else if (sTargetComponent.compareTo(this.mComponent) != 0 || (action != null && !"android.intent.action.MAIN".equals(action))) {
            return null;
        } else {
            Log.d("MobileNetworkIntentConverter", "Support default actions direct to this component");
            andThen = identity.andThen(new Function() { // from class: com.android.settings.network.MobileNetworkIntentConverter$$ExternalSyntheticLambda2
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Bundle lambda$apply$11;
                    lambda$apply$11 = MobileNetworkIntentConverter.this.lambda$apply$11(extractSubscriptionId, (Intent) obj);
                    return lambda$apply$11;
                }
            }).andThen(new Function() { // from class: com.android.settings.network.MobileNetworkIntentConverter$$ExternalSyntheticLambda3
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Intent lambda$apply$12;
                    lambda$apply$12 = MobileNetworkIntentConverter.this.lambda$apply$12(intent, (Bundle) obj);
                    return lambda$apply$12;
                }
            }).andThen(new Function() { // from class: com.android.settings.network.MobileNetworkIntentConverter$$ExternalSyntheticLambda4
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Intent lambda$apply$13;
                    lambda$apply$13 = MobileNetworkIntentConverter.this.lambda$apply$13((Intent) obj);
                    return lambda$apply$13;
                }
            }).andThen(new Function() { // from class: com.android.settings.network.MobileNetworkIntentConverter$$ExternalSyntheticLambda5
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Intent lambda$apply$14;
                    lambda$apply$14 = MobileNetworkIntentConverter.this.lambda$apply$14(extractSubscriptionId, (Intent) obj);
                    return lambda$apply$14;
                }
            });
        }
        if (!isAttachedToExposedComponents()) {
            andThen = andThen.andThen(new Function() { // from class: com.android.settings.network.MobileNetworkIntentConverter$$ExternalSyntheticLambda6
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Intent lambda$apply$15;
                    lambda$apply$15 = MobileNetworkIntentConverter.this.lambda$apply$15((Intent) obj);
                    return lambda$apply$15;
                }
            });
        }
        Intent intent2 = (Intent) andThen.apply(intent);
        if (intent2 != null) {
            long elapsedRealtimeNanos2 = SystemClock.elapsedRealtimeNanos();
            Log.d("MobileNetworkIntentConverter", this.mComponent.toString() + " intent conversion: " + (elapsedRealtimeNanos2 - elapsedRealtimeNanos) + " ns");
        }
        return intent2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Intent lambda$apply$2(int i, Intent intent) {
        return updateFragment(intent, this.mAppContext, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Intent lambda$apply$6(int i, Intent intent) {
        return updateFragment(intent, this.mAppContext, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Bundle lambda$apply$8(int i, Bundle bundle) {
        return supportContactDiscoveryDialog(bundle, this.mAppContext, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Intent lambda$apply$10(int i, Intent intent) {
        return updateFragment(intent, this.mAppContext, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Intent lambda$apply$14(int i, Intent intent) {
        return updateFragment(intent, this.mAppContext, i);
    }

    protected boolean isAttachedToExposedComponents() {
        return sTargetComponent.compareTo(this.mComponent) == 0;
    }

    protected int extractSubscriptionId(Intent intent) {
        return intent.getIntExtra("android.provider.extra.SUB_ID", -1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: extractArguments */
    public Bundle lambda$apply$7(Intent intent, int i) {
        Bundle bundleExtra = intent.getBundleExtra(":settings:show_fragment_args");
        Bundle bundle = bundleExtra != null ? new Bundle(bundleExtra) : new Bundle();
        bundle.putParcelable("intent", intent);
        bundle.putInt("android.provider.extra.SUB_ID", i);
        return bundle;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: convertMmsArguments */
    public Bundle lambda$apply$4(Bundle bundle) {
        bundle.putString(":settings:fragment_args_key", "mms_message");
        return bundle;
    }

    protected boolean mayShowContactDiscoveryDialog(Context context, int i) {
        return MobileNetworkUtils.isContactDiscoveryVisible(context, i) && !MobileNetworkUtils.isContactDiscoveryEnabled(context, i);
    }

    protected Bundle supportContactDiscoveryDialog(Bundle bundle, Context context, int i) {
        boolean mayShowContactDiscoveryDialog = mayShowContactDiscoveryDialog(context, i);
        Log.d("MobileNetworkIntentConverter", "maybeShowContactDiscoveryDialog subId=" + i + ", show=" + mayShowContactDiscoveryDialog);
        bundle.putBoolean("show_capability_discovery_opt_in", mayShowContactDiscoveryDialog);
        return bundle;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: rePackIntent */
    public Intent lambda$apply$9(Bundle bundle, Intent intent) {
        Intent intent2 = new Intent(intent);
        intent2.setComponent(sTargetComponent);
        intent2.putExtra("android.provider.extra.SUB_ID", bundle.getInt("android.provider.extra.SUB_ID"));
        intent2.putExtra(":settings:show_fragment_args", bundle);
        return intent2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: replaceIntentAction */
    public Intent lambda$apply$13(Intent intent) {
        intent.setAction("android.settings.NETWORK_OPERATOR_SETTINGS");
        return intent;
    }

    protected CharSequence getFragmentTitle(Context context, int i) {
        return SubscriptionUtil.getUniqueSubscriptionDisplayName(SubscriptionUtil.getSubscriptionOrDefault(context, i), context);
    }

    protected Intent updateFragment(Intent intent, Context context, int i) {
        CharSequence fragmentTitle;
        if (intent.getStringExtra(":settings:show_fragment_title") == null && (fragmentTitle = getFragmentTitle(context, i)) != null) {
            intent.putExtra(":settings:show_fragment_title", fragmentTitle.toString());
        }
        intent.putExtra(":settings:show_fragment", getFragmentClass(context));
        return intent;
    }

    protected String getFragmentClass(Context context) {
        Bundle bundle;
        AtomicReference<String> atomicReference = mCachedClassName;
        String str = atomicReference.get();
        if (str != null) {
            return str;
        }
        try {
            ActivityInfo activityInfo = context.getPackageManager().getActivityInfo(sTargetComponent, 128);
            if (activityInfo == null || (bundle = activityInfo.metaData) == null) {
                return null;
            }
            String string = bundle.getString("com.android.settings.FRAGMENT_CLASS");
            if (string != null) {
                atomicReference.set(string);
            }
            return string;
        } catch (PackageManager.NameNotFoundException unused) {
            Log.d("MobileNetworkIntentConverter", "Cannot get Metadata for: " + sTargetComponent.toString());
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: configForReRoute */
    public Intent lambda$apply$15(Intent intent) {
        if (intent.hasExtra(":reroute:MobileNetworkIntentConverter")) {
            Log.d("MobileNetworkIntentConverter", "Skip re-routed intent " + intent);
            return null;
        }
        return intent.putExtra(":reroute:MobileNetworkIntentConverter", intent.getAction()).setComponent(null);
    }

    protected static boolean mayRequireConvert(Intent intent) {
        if (intent == null) {
            return false;
        }
        final String action = intent.getAction();
        return Arrays.stream(sPotentialActions).anyMatch(new Predicate() { // from class: com.android.settings.network.MobileNetworkIntentConverter$$ExternalSyntheticLambda16
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean equals;
                equals = TextUtils.equals(action, (String) obj);
                return equals;
            }
        });
    }

    protected Intent convertFromDeepLink(Intent intent) {
        if (intent == null) {
            return null;
        }
        if (TextUtils.equals(intent.getAction(), "android.settings.SETTINGS_EMBED_DEEP_LINK_ACTIVITY")) {
            try {
                return Intent.parseUri(intent.getStringExtra("android.provider.extra.SETTINGS_EMBEDDED_DEEP_LINK_INTENT_URI"), 1);
            } catch (URISyntaxException e) {
                Log.d("MobileNetworkIntentConverter", "Intent URI corrupted", e);
                return null;
            }
        }
        return intent;
    }
}
