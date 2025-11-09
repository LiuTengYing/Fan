package com.android.settings.backup;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInputStream;
import android.app.backup.BackupDataOutput;
import android.app.backup.BackupHelper;
import android.os.ParcelFileDescriptor;
import com.android.settings.fuelgauge.BatteryBackupHelper;
import com.android.settings.shortcut.CreateShortcutPreferenceController;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
/* loaded from: classes.dex */
public class SettingsBackupHelper extends BackupAgentHelper {
    @Override // android.app.backup.BackupAgent
    public void onCreate() {
        super.onCreate();
        addHelper("no-op", new NoOpHelper());
        addHelper("BatteryBackupHelper", new BatteryBackupHelper(this));
    }

    @Override // android.app.backup.BackupAgent
    public void onRestoreFinished() {
        super.onRestoreFinished();
        CreateShortcutPreferenceController.updateRestoredShortcuts(this);
    }

    /* loaded from: classes.dex */
    private static class NoOpHelper implements BackupHelper {
        private final int VERSION_CODE;

        @Override // android.app.backup.BackupHelper
        public void restoreEntity(BackupDataInputStream backupDataInputStream) {
        }

        @Override // android.app.backup.BackupHelper
        public void writeNewStateDescription(ParcelFileDescriptor parcelFileDescriptor) {
        }

        private NoOpHelper() {
            this.VERSION_CODE = 1;
        }

        @Override // android.app.backup.BackupHelper
        public void performBackup(ParcelFileDescriptor parcelFileDescriptor, BackupDataOutput backupDataOutput, ParcelFileDescriptor parcelFileDescriptor2) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(parcelFileDescriptor2.getFileDescriptor());
                if (getVersionCode(parcelFileDescriptor) != 1) {
                    backupDataOutput.writeEntityHeader("placeholder", 1);
                    backupDataOutput.writeEntityData(new byte[1], 1);
                }
                fileOutputStream.write(1);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException unused) {
            }
        }

        private int getVersionCode(ParcelFileDescriptor parcelFileDescriptor) {
            if (parcelFileDescriptor == null) {
                return 0;
            }
            try {
                FileInputStream fileInputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
                int read = fileInputStream.read();
                fileInputStream.close();
                return read;
            } catch (IOException unused) {
                return 0;
            }
        }
    }
}
