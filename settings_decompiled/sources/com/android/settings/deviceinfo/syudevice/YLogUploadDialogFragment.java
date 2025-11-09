package com.android.settings.deviceinfo.syudevice;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$style;
import com.android.settings.SettingsApplication;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.utils.NetworkUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
/* loaded from: classes.dex */
public class YLogUploadDialogFragment extends InstrumentedDialogFragment {
    private static String outputFile = "/storage/emulated/0/ylog/";
    private static String sourceFile = "/storage/emulated/0/ylog/ap";
    private Button mConfirmBtn;
    private View mRootView;
    private ViewGroup.LayoutParams params;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    public static void show(Fragment fragment, String str) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag("YLogUploadDialogFragment") == null) {
            Bundle bundle = new Bundle();
            bundle.putString("arg_key_dialog_title", str);
            YLogUploadDialogFragment yLogUploadDialogFragment = new YLogUploadDialogFragment();
            yLogUploadDialogFragment.setArguments(bundle);
            yLogUploadDialogFragment.show(childFragmentManager, "YLogUploadDialogFragment");
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        getArguments().getString("arg_key_dialog_title");
        int[] windowManeger = getWindowManeger();
        this.params = new ViewGroup.LayoutParams(windowManeger[0] / 2, windowManeger[1] / 2);
        this.mRootView = LinearLayout.inflate(SettingsApplication.mApplication, R$layout.system_ylog_file_upload_dialog, null);
        int i = SystemProperties.getInt("persist.syu.thememode", 2);
        Dialog dialog = new Dialog(SettingsApplication.mApplication, (i == 2 || i == 3) ? R$style.add_dialog_classic : R$style.add_dialog);
        dialog.getWindow().setType(2003);
        dialog.requestWindowFeature(1);
        dialog.setContentView(this.mRootView, this.params);
        dialog.show();
        init();
        return dialog;
    }

    public static int[] getWindowManeger() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) SettingsApplication.mApplication.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    private void init() {
        Button button = (Button) this.mRootView.findViewById(R$id.ylog_upload_confirm);
        this.mConfirmBtn = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.deviceinfo.syudevice.YLogUploadDialogFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (!NetworkUtils.isNetWorkAvailable(SettingsApplication.mApplication)) {
                    Toast.makeText(SettingsApplication.mApplication, SettingsApplication.mResources.getString(R$string.ylog_upload_no_network), 0).show();
                } else if (new File(YLogUploadDialogFragment.sourceFile).exists()) {
                    YLogUploadDialogFragment.this.readFile();
                    YLogUploadDialogFragment.this.mConfirmBtn.setClickable(false);
                } else {
                    Toast.makeText(SettingsApplication.mApplication, SettingsApplication.mResources.getString(R$string.ylog_upload_empty_file), 0).show();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void readFile() {
        new YlogZip().execute(new String[0]);
    }

    public void zipFolder(String str, String str2) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(str2);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
            addFolderToZip("", new File(str), zipOutputStream);
            zipOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addFolderToZip(String str, File file, ZipOutputStream zipOutputStream) throws IOException {
        File[] listFiles;
        if (file.listFiles() == null) {
            return;
        }
        for (File file2 : file.listFiles()) {
            if (file2.isDirectory()) {
                addFolderToZip(str + "/" + file2.getName(), file2, zipOutputStream);
            } else {
                byte[] bArr = new byte[4096];
                FileInputStream fileInputStream = new FileInputStream(file2);
                zipOutputStream.putNextEntry(new ZipEntry(str + "/" + file2.getName()));
                while (true) {
                    int read = fileInputStream.read(bArr);
                    if (read <= 0) {
                        break;
                    }
                    zipOutputStream.write(bArr, 0, read);
                }
                fileInputStream.close();
            }
        }
    }

    /* loaded from: classes.dex */
    public class YlogZip extends AsyncTask<String, Integer, Boolean> {
        public YlogZip() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(String... strArr) {
            String str = SystemProperties.get("serialno");
            YLogUploadDialogFragment.outputFile += str + "_" + new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + ".zip";
            YLogUploadDialogFragment.this.zipFolder(YLogUploadDialogFragment.sourceFile, YLogUploadDialogFragment.outputFile);
            try {
                UploadFile.upload("http://192.168.1.165:8081/cj/carUpload/upload", YLogUploadDialogFragment.outputFile, "ylog", str, "7870");
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onProgressUpdate(Integer... numArr) {
            Log.d("YLogUploadDialogFragment", "onProgressUpdate: " + numArr);
            super.onProgressUpdate((Object[]) numArr);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean bool) {
            super.onPostExecute((YlogZip) bool);
            Log.d("YLogUploadDialogFragment", "onPostExecute: " + bool);
            YLogUploadDialogFragment.outputFile = "/storage/emulated/0/ylog/";
            YLogUploadDialogFragment.this.dismiss();
        }
    }
}
