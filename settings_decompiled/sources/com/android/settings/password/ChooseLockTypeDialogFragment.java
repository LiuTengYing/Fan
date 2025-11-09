package com.android.settings.password;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.android.settings.R$drawable;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.password.ChooseLockGenericController;
import com.google.android.setupcompat.util.WizardManagerHelper;
import java.util.List;
/* loaded from: classes.dex */
public class ChooseLockTypeDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    private ScreenLockAdapter mAdapter;
    private ChooseLockGenericController mController;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 990;
    }

    public static ChooseLockTypeDialogFragment newInstance(int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("userId", i);
        ChooseLockTypeDialogFragment chooseLockTypeDialogFragment = new ChooseLockTypeDialogFragment();
        chooseLockTypeDialogFragment.setArguments(bundle);
        return chooseLockTypeDialogFragment;
    }

    /* loaded from: classes.dex */
    public interface OnLockTypeSelectedListener {
        void onLockTypeSelected(ScreenLockType screenLockType);

        default void startChooseLockActivity(ScreenLockType screenLockType, Activity activity) {
            Intent intent = activity.getIntent();
            Intent intent2 = new Intent(activity, SetupChooseLockGeneric.class);
            intent2.addFlags(33554432);
            ChooseLockTypeDialogFragment.copyBooleanExtra(intent, intent2, "request_gk_pw_handle", false);
            ChooseLockTypeDialogFragment.copyBooleanExtra(intent, intent2, "show_options_button", false);
            if (intent.hasExtra("choose_lock_generic_extras")) {
                intent2.putExtras(intent.getBundleExtra("choose_lock_generic_extras"));
            }
            intent2.putExtra("lockscreen.password_type", screenLockType.defaultQuality);
            WizardManagerHelper.copyWizardManagerExtras(intent, intent2);
            activity.startActivity(intent2);
            activity.finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void copyBooleanExtra(Intent intent, Intent intent2, String str, boolean z) {
        intent2.putExtra(str, intent.getBooleanExtra(str, z));
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mController = new ChooseLockGenericController.Builder(getContext(), getArguments().getInt("userId")).setHideInsecureScreenLockTypes(true).build();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        OnLockTypeSelectedListener onLockTypeSelectedListener;
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof OnLockTypeSelectedListener) {
            onLockTypeSelectedListener = (OnLockTypeSelectedListener) parentFragment;
        } else {
            Context context = getContext();
            onLockTypeSelectedListener = context instanceof OnLockTypeSelectedListener ? (OnLockTypeSelectedListener) context : null;
        }
        if (onLockTypeSelectedListener != null) {
            onLockTypeSelectedListener.onLockTypeSelected(this.mAdapter.getItem(i));
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Context context = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        ScreenLockAdapter screenLockAdapter = new ScreenLockAdapter(context, this.mController.getVisibleAndEnabledScreenLockTypes(), this.mController);
        this.mAdapter = screenLockAdapter;
        builder.setAdapter(screenLockAdapter, this);
        builder.setTitle(R$string.setup_lock_settings_options_dialog_title);
        return builder.create();
    }

    /* loaded from: classes.dex */
    private static class ScreenLockAdapter extends ArrayAdapter<ScreenLockType> {
        private final ChooseLockGenericController mController;

        ScreenLockAdapter(Context context, List<ScreenLockType> list, ChooseLockGenericController chooseLockGenericController) {
            super(context, R$layout.choose_lock_dialog_item, list);
            this.mController = chooseLockGenericController;
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            Context context = viewGroup.getContext();
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R$layout.choose_lock_dialog_item, viewGroup, false);
            }
            ScreenLockType item = getItem(i);
            TextView textView = (TextView) view;
            textView.setText(this.mController.getTitle(item));
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(getIconForScreenLock(context, item), (Drawable) null, (Drawable) null, (Drawable) null);
            return view;
        }

        private static Drawable getIconForScreenLock(Context context, ScreenLockType screenLockType) {
            int i = AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType[screenLockType.ordinal()];
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        return null;
                    }
                    return context.getDrawable(R$drawable.ic_password);
                }
                return context.getDrawable(R$drawable.ic_pin);
            }
            return context.getDrawable(R$drawable.ic_pattern);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.password.ChooseLockTypeDialogFragment$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$password$ScreenLockType;

        static {
            int[] iArr = new int[ScreenLockType.values().length];
            $SwitchMap$com$android$settings$password$ScreenLockType = iArr;
            try {
                iArr[ScreenLockType.PATTERN.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$settings$password$ScreenLockType[ScreenLockType.PIN.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$settings$password$ScreenLockType[ScreenLockType.PASSWORD.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$settings$password$ScreenLockType[ScreenLockType.NONE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$settings$password$ScreenLockType[ScreenLockType.SWIPE.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$settings$password$ScreenLockType[ScreenLockType.MANAGED.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }
}
