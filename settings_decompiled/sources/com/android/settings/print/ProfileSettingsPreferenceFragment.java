package com.android.settings.print;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R$layout;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.dashboard.profileselector.UserAdapter;
/* loaded from: classes.dex */
public abstract class ProfileSettingsPreferenceFragment extends SettingsPreferenceFragment {
    protected abstract String getIntentActionString();

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        final UserAdapter createUserSpinnerAdapter = UserAdapter.createUserSpinnerAdapter((UserManager) getSystemService("user"), getActivity());
        if (createUserSpinnerAdapter != null) {
            final Spinner spinner = (Spinner) setPinnedHeaderView(R$layout.spinner_view);
            spinner.setAdapter((SpinnerAdapter) createUserSpinnerAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.android.settings.print.ProfileSettingsPreferenceFragment.1
                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onNothingSelected(AdapterView<?> adapterView) {
                }

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onItemSelected(AdapterView<?> adapterView, View view2, int i, long j) {
                    UserHandle userHandle = createUserSpinnerAdapter.getUserHandle(i);
                    if (userHandle.getIdentifier() != UserHandle.myUserId()) {
                        FragmentActivity activity = ProfileSettingsPreferenceFragment.this.getActivity();
                        Intent intent = new Intent(ProfileSettingsPreferenceFragment.this.getIntentActionString());
                        intent.addFlags(268435456);
                        intent.addFlags(32768);
                        activity.startActivityAsUser(intent, userHandle);
                        spinner.setSelection(0);
                        activity.finish();
                    }
                }
            });
        }
    }
}
