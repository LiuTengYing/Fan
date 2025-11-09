package com.android.settings.enterprise;

import android.content.Context;
import java.util.Date;
/* loaded from: classes.dex */
public interface EnterprisePrivacyFeatureProvider {
    CharSequence getDeviceOwnerDisclosure();

    String getDeviceOwnerOrganizationName();

    String getImeLabelIfOwnerSet();

    Date getLastBugReportRequestTime();

    Date getLastNetworkLogRetrievalTime();

    Date getLastSecurityLogRetrievalTime();

    int getMaximumFailedPasswordsBeforeWipeInCurrentUser();

    int getMaximumFailedPasswordsBeforeWipeInManagedProfile();

    int getNumberOfActiveDeviceAdminsForCurrentUserAndManagedProfile();

    int getNumberOfOwnerInstalledCaCertsForCurrentUser();

    int getNumberOfOwnerInstalledCaCertsForManagedProfile();

    boolean hasDeviceOwner();

    boolean hasWorkPolicyInfo();

    boolean isAlwaysOnVpnSetInCurrentUser();

    boolean isAlwaysOnVpnSetInManagedProfile();

    boolean isInCompMode();

    boolean isNetworkLoggingEnabled();

    boolean isSecurityLoggingEnabled();

    boolean showParentalControls();

    boolean showWorkPolicyInfo(Context context);
}
