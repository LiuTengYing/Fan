package com.unisoc.settings.storageclearmanager;
/* loaded from: classes2.dex */
public class FileDetailModel implements Comparable<FileDetailModel> {
    private String filePath;
    private long fileSize;

    public FileDetailModel() {
    }

    public FileDetailModel(String str, long j) {
        this.filePath = str;
        this.fileSize = j;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public long getFileSize() {
        return this.fileSize;
    }

    @Override // java.lang.Comparable
    public int compareTo(FileDetailModel fileDetailModel) {
        int i = ((this.fileSize - fileDetailModel.fileSize) > 0L ? 1 : ((this.fileSize - fileDetailModel.fileSize) == 0L ? 0 : -1));
        if (i == 0) {
            return this.filePath.compareTo(fileDetailModel.filePath);
        }
        return i > 0 ? 1 : -1;
    }
}
