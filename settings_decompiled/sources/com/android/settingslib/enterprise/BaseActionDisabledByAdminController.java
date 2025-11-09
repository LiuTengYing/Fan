package com.android.settingslib.enterprise;

import com.android.internal.util.Preconditions;
import com.android.settingslib.RestrictedLockUtils;
import java.util.Objects;
/* loaded from: classes2.dex */
abstract class BaseActionDisabledByAdminController implements ActionDisabledByAdminController {
    protected RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
    protected int mEnforcementAdminUserId;
    protected ActionDisabledLearnMoreButtonLauncher mLauncher;
    protected final DeviceAdminStringProvider mStringProvider;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BaseActionDisabledByAdminController(DeviceAdminStringProvider deviceAdminStringProvider) {
        this.mStringProvider = deviceAdminStringProvider;
    }

    @Override // com.android.settingslib.enterprise.ActionDisabledByAdminController
    public final void initialize(ActionDisabledLearnMoreButtonLauncher actionDisabledLearnMoreButtonLauncher) {
        Objects.requireNonNull(actionDisabledLearnMoreButtonLauncher, "launcher cannot be null");
        this.mLauncher = actionDisabledLearnMoreButtonLauncher;
    }

    @Override // com.android.settingslib.enterprise.ActionDisabledByAdminController
    public final void updateEnforcedAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin, int i) {
        assertInitialized();
        this.mEnforcementAdminUserId = i;
        Objects.requireNonNull(enforcedAdmin, "admin cannot be null");
        this.mEnforcedAdmin = enforcedAdmin;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void assertInitialized() {
        Preconditions.checkState(this.mLauncher != null, "must call initialize() first");
    }
}
