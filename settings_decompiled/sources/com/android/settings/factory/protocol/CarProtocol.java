package com.android.settings.factory.protocol;

import android.content.Context;
import android.content.res.Resources;
import android.os.SystemProperties;
import android.text.TextUtils;
import com.android.settings.SettingsApplication;
import com.android.settings.factory.listener.ProtocolNotifyListener;
import com.android.settings.factory.protocol.data.FinalCarCompany;
import com.android.settings.factory.protocol.data.FinalCarSet;
import com.android.settings.factory.protocol.data.FinalCarType;
import com.android.settings.factory.protocol.data.FinalCompany;
import com.syu.ipcself.module.main.Main;
import com.syu.util.FuncUtils;
import com.syu.util.Markup;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class CarProtocol {
    private static Context mContext;
    public static String mPkgName;
    public static Resources mResources;
    private static CarProtocol sInstance;
    private List<ProtocolNotifyListener> mListener = new ArrayList();
    public static HashMap<String, Integer> mMapCompany = new HashMap<>();
    public static HashMap<String, Integer> mMapCarset = new HashMap<>();
    public static HashMap<String, Integer> mMapCartype = new HashMap<>();
    public static HashMap<String, Integer> mMapCarcompany = new HashMap<>();
    public static ArrayList<CarSet> mListCarSets = new ArrayList<>();
    public static ArrayList<CarCompany> mListCarCompanys = new ArrayList<>();
    public static CanBox mCurCanBox = null;
    public static CarType mCurCarType = null;
    public static CarSet mCurCarSet = null;
    public static CarCompany mCurCarcompany = null;
    private static int mIdCustomer = -1;
    private static boolean bIsVertical = false;

    public CarProtocol(Context context) {
        mContext = context;
    }

    public static CarProtocol getInstance(Context context) {
        mContext = context;
        if (sInstance == null) {
            sInstance = new CarProtocol(context);
            PutHashMap(mMapCarcompany, FinalCarCompany.class);
            PutHashMap(mMapCarset, FinalCarSet.class);
            PutHashMap(mMapCartype, FinalCarType.class);
            PutHashMap(mMapCompany, FinalCompany.class);
        }
        init();
        return sInstance;
    }

    private static void init() {
        mResources = mContext.getResources();
        mPkgName = mContext.getPackageName();
        mIdCustomer = SystemProperties.getInt("ro.build.fytmanufacturer", 2);
    }

    public void parseXml(String str) {
        String GetAttr;
        String GetAttr2;
        if (TextUtils.isEmpty(str)) {
            return;
        }
        try {
            final ArrayList arrayList = new ArrayList();
            final ArrayList arrayList2 = new ArrayList();
            int identifier = mResources.getIdentifier(str, "raw", mPkgName);
            if (identifier <= 0) {
                return;
            }
            String readStrFromStream = FuncUtils.readStrFromStream(mResources.openRawResource(identifier));
            Markup markup = new Markup();
            markup.ReadXML(readStrFromStream);
            if (markup.IntoItem()) {
                do {
                    Markup.XmlItem xmlItem = markup.mItemCur;
                    String FindToken = markup.FindToken(xmlItem.mStrBuff, xmlItem.mPosEnd - xmlItem.mPosStart, 0, null);
                    if (mMapCarcompany.containsKey(FindToken)) {
                        CarCompany carCompany = new CarCompany();
                        carCompany.strCompany = getString(FindToken);
                        if (markup.IntoItem()) {
                            do {
                                Markup.XmlItem xmlItem2 = markup.mItemCur;
                                String FindToken2 = markup.FindToken(xmlItem2.mStrBuff, xmlItem2.mPosEnd - xmlItem2.mPosStart, 0, null);
                                if (mMapCarset.containsKey(FindToken2)) {
                                    CarSet carSet = new CarSet();
                                    carSet.strSet = getString(FindToken2);
                                    if (markup.IntoItem()) {
                                        do {
                                            Markup.XmlItem xmlItem3 = markup.mItemCur;
                                            String FindToken3 = markup.FindToken(xmlItem3.mStrBuff, xmlItem3.mPosEnd - xmlItem3.mPosStart, 0, null);
                                            if (mMapCartype.containsKey(FindToken3)) {
                                                CarType carType = new CarType();
                                                carType.strType = getString(FindToken3);
                                                if (markup.IntoItem()) {
                                                    do {
                                                        Markup.XmlItem xmlItem4 = markup.mItemCur;
                                                        String FindToken4 = markup.FindToken(xmlItem4.mStrBuff, xmlItem4.mPosEnd - xmlItem4.mPosStart, 0, null);
                                                        if (mMapCompany.containsKey(FindToken4) && (GetAttr = markup.GetAttr("disp")) != null && GetAttr.charAt(0) == '1' && (GetAttr2 = markup.GetAttr("id")) != null) {
                                                            CanBox canBox = new CanBox();
                                                            canBox.strCompany = getString(FindToken4);
                                                            canBox.id = myParseInt(GetAttr2);
                                                            canBox.carType = carType;
                                                            carType.mListCanBox.add(canBox);
                                                        }
                                                    } while (markup.NextItem());
                                                    markup.ExitItem();
                                                }
                                                if (carType.mListCanBox.size() > 0) {
                                                    carType.carSet = carSet;
                                                    carSet.mListCarType.add(carType);
                                                }
                                            }
                                        } while (markup.NextItem());
                                        markup.ExitItem();
                                    }
                                    if (carSet.mListCarType.size() > 0) {
                                        carSet.company = carCompany;
                                        carCompany.mListCarSet.add(carSet);
                                        arrayList.add(carSet);
                                    }
                                }
                            } while (markup.NextItem());
                            markup.ExitItem();
                        }
                        if (carCompany.mListCarSet.size() > 0) {
                            arrayList2.add(carCompany);
                        }
                    }
                } while (markup.NextItem());
                markup.ExitItem();
                Main.postRunnable_Ui(false, new Runnable() { // from class: com.android.settings.factory.protocol.CarProtocol.1
                    @Override // java.lang.Runnable
                    public void run() {
                        CarProtocol.mListCarSets.clear();
                        CarProtocol.mListCarSets.addAll(arrayList);
                        CarProtocol.mListCarCompanys.clear();
                        CarProtocol.mListCarCompanys.addAll(arrayList2);
                        SettingsApplication.is_UpdatingCartype = Boolean.FALSE;
                        for (ProtocolNotifyListener protocolNotifyListener : CarProtocol.this.mListener) {
                            protocolNotifyListener.notifyCarType();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeIndex_CarType(ArrayList<CarType> arrayList, int i, int i2) {
        if (arrayList == null || arrayList.size() <= 0 || i >= arrayList.size() || i2 >= arrayList.size()) {
            return;
        }
        arrayList.set(i, arrayList.get(i2));
        arrayList.set(i2, arrayList.get(i));
    }

    private void changeIndex_CarSet(ArrayList<CarSet> arrayList, int i, int i2) {
        if (arrayList == null || arrayList.size() <= 0 || i >= arrayList.size() || i2 >= arrayList.size()) {
            return;
        }
        arrayList.set(i, arrayList.get(i2));
        arrayList.set(i2, arrayList.get(i));
    }

    public static CanBox getCanBoxById(int i) {
        ArrayList<CarCompany> arrayList = mListCarCompanys;
        if (arrayList != null) {
            Iterator<CarCompany> it = arrayList.iterator();
            while (it.hasNext()) {
                if (it.next().mListCarSet != null) {
                    Iterator<CarSet> it2 = mListCarSets.iterator();
                    while (it2.hasNext()) {
                        ArrayList<CarType> arrayList2 = it2.next().mListCarType;
                        if (arrayList2 != null) {
                            Iterator<CarType> it3 = arrayList2.iterator();
                            while (it3.hasNext()) {
                                ArrayList<CanBox> arrayList3 = it3.next().mListCanBox;
                                if (arrayList3 != null) {
                                    Iterator<CanBox> it4 = arrayList3.iterator();
                                    while (it4.hasNext()) {
                                        CanBox next = it4.next();
                                        if (next.id == i) {
                                            return next;
                                        }
                                    }
                                    continue;
                                }
                            }
                            continue;
                        }
                    }
                    continue;
                }
            }
            return null;
        }
        return null;
    }

    public static boolean isNumber(String str) {
        if (str == null || str.equals("")) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public String getString(String str) {
        int identifier = mResources.getIdentifier(str, "string", mPkgName);
        return identifier == 0 ? str : myString(identifier);
    }

    public static String myString(int i) {
        Resources resources = mResources;
        if (resources != null) {
            try {
                return resources.getString(i);
            } catch (Exception unused) {
                return "";
            }
        }
        return "";
    }

    public static int myParseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception unused) {
            return 0;
        }
    }

    public static <T> void PutHashMap(HashMap<String, Integer> hashMap, Class<T> cls) {
        Field[] declaredFields;
        if (hashMap != null) {
            for (Field field : cls.getDeclaredFields()) {
                try {
                    hashMap.put(field.getName(), Integer.valueOf(field.getInt(cls)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setProtocolNotifyListener(ProtocolNotifyListener protocolNotifyListener) {
        List<ProtocolNotifyListener> list = this.mListener;
        if (list != null) {
            list.add(protocolNotifyListener);
        }
    }

    public void removeProtocolNotifyListsner(ProtocolNotifyListener protocolNotifyListener) {
        List<ProtocolNotifyListener> list = this.mListener;
        if (list == null || list.size() <= 0) {
            return;
        }
        this.mListener.remove(protocolNotifyListener);
    }

    /* JADX WARN: Code restructure failed: missing block: B:229:0x0526, code lost:
        if ("智航".equals(r7) != false) goto L87;
     */
    /* JADX WARN: Code restructure failed: missing block: B:291:0x0605, code lost:
        if (r2.contains(java.lang.Integer.valueOf(r14)) != false) goto L158;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0089 A[Catch: Exception -> 0x0772, TryCatch #3 {Exception -> 0x0772, blocks: (B:3:0x0014, B:6:0x0039, B:9:0x0048, B:11:0x0056, B:15:0x0067, B:18:0x0076, B:20:0x007c, B:22:0x0082, B:25:0x0086, B:27:0x0089, B:29:0x0091, B:30:0x00a3, B:31:0x00a6, B:33:0x00b1, B:35:0x00bb, B:37:0x00e6, B:42:0x00f4, B:44:0x010d, B:45:0x0114, B:47:0x011c, B:49:0x0126, B:38:0x00e9, B:40:0x00ef, B:41:0x00f2), top: B:368:0x0014 }] */
    /* JADX WARN: Removed duplicated region for block: B:33:0x00b1 A[Catch: Exception -> 0x0772, LOOP:1: B:33:0x00b1->B:353:0x0752, LOOP_START, PHI: r1 r3 r4 r5 r6 r8 r9 r11 
      PHI: (r1v3 com.android.settings.factory.protocol.CarProtocol) = (r1v0 com.android.settings.factory.protocol.CarProtocol), (r1v6 com.android.settings.factory.protocol.CarProtocol) binds: [B:32:0x00af, B:353:0x0752] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r3v4 java.lang.String) = (r3v0 java.lang.String), (r3v7 java.lang.String) binds: [B:32:0x00af, B:353:0x0752] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r4v2 java.lang.String) = (r4v0 java.lang.String), (r4v6 java.lang.String) binds: [B:32:0x00af, B:353:0x0752] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r5v1 com.android.settings.factory.protocol.TestDataProvider) = (r5v0 com.android.settings.factory.protocol.TestDataProvider), (r5v3 com.android.settings.factory.protocol.TestDataProvider) binds: [B:32:0x00af, B:353:0x0752] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r6v3 boolean) = (r6v1 boolean), (r6v6 boolean) binds: [B:32:0x00af, B:353:0x0752] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r8v2 java.util.ArrayList) = (r8v0 java.util.ArrayList), (r8v5 java.util.ArrayList) binds: [B:32:0x00af, B:353:0x0752] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r9v2 java.util.ArrayList) = (r9v0 java.util.ArrayList), (r9v7 java.util.ArrayList) binds: [B:32:0x00af, B:353:0x0752] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r11v8 java.lang.String) = (r11v5 java.lang.String), (r11v11 java.lang.String) binds: [B:32:0x00af, B:353:0x0752] A[DONT_GENERATE, DONT_INLINE], TryCatch #3 {Exception -> 0x0772, blocks: (B:3:0x0014, B:6:0x0039, B:9:0x0048, B:11:0x0056, B:15:0x0067, B:18:0x0076, B:20:0x007c, B:22:0x0082, B:25:0x0086, B:27:0x0089, B:29:0x0091, B:30:0x00a3, B:31:0x00a6, B:33:0x00b1, B:35:0x00bb, B:37:0x00e6, B:42:0x00f4, B:44:0x010d, B:45:0x0114, B:47:0x011c, B:49:0x0126, B:38:0x00e9, B:40:0x00ef, B:41:0x00f2), top: B:368:0x0014 }] */
    /* JADX WARN: Removed duplicated region for block: B:372:0x00a6 A[EDGE_INSN: B:372:0x00a6->B:31:0x00a6 ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r11v23 */
    /* JADX WARN: Type inference failed for: r11v25 */
    /* JADX WARN: Type inference failed for: r11v29, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r11v30 */
    /* JADX WARN: Type inference failed for: r11v32 */
    /* JADX WARN: Type inference failed for: r11v35, types: [int] */
    /* JADX WARN: Type inference failed for: r11v36 */
    /* JADX WARN: Type inference failed for: r11v38 */
    /* JADX WARN: Type inference failed for: r11v41 */
    /* JADX WARN: Type inference failed for: r11v42 */
    /* JADX WARN: Type inference failed for: r11v43 */
    /* JADX WARN: Type inference failed for: r11v44 */
    /* JADX WARN: Type inference failed for: r11v6 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void getTestDataByProvider() {
        /*
            Method dump skipped, instructions count: 1916
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.factory.protocol.CarProtocol.getTestDataByProvider():void");
    }
}
