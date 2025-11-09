package com.android.settingslib.license;

import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
class LicenseHtmlGeneratorFromXml {
    private final List<File> mXmlFiles;
    private final Map<String, Map<String, Set<String>>> mFileNameToLibraryToContentIdMap = new HashMap();
    private final Map<String, String> mContentIdToFileContentMap = new HashMap();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class ContentIdAndFileNames {
        final String mContentId;
        final Map<String, List<String>> mLibraryToFileNameMap = new TreeMap();

        ContentIdAndFileNames(String str) {
            this.mContentId = str;
        }
    }

    private LicenseHtmlGeneratorFromXml(List<File> list) {
        this.mXmlFiles = list;
    }

    public static boolean generateHtml(List<File> list, File file, String str) {
        return new LicenseHtmlGeneratorFromXml(list).generateHtml(file, str);
    }

    private boolean generateHtml(File file, String str) {
        PrintWriter printWriter;
        for (File file2 : this.mXmlFiles) {
            parse(file2);
        }
        if (!this.mFileNameToLibraryToContentIdMap.isEmpty() && !this.mContentIdToFileContentMap.isEmpty()) {
            PrintWriter printWriter2 = null;
            try {
                printWriter = new PrintWriter(file);
            } catch (FileNotFoundException | SecurityException e) {
                e = e;
            }
            try {
                generateHtml(this.mFileNameToLibraryToContentIdMap, this.mContentIdToFileContentMap, printWriter, str);
                printWriter.flush();
                printWriter.close();
                return true;
            } catch (FileNotFoundException | SecurityException e2) {
                e = e2;
                printWriter2 = printWriter;
                Log.e("LicenseGeneratorFromXml", "Failed to generate " + file, e);
                if (printWriter2 != null) {
                    printWriter2.close();
                }
                return false;
            }
        }
        return false;
    }

    private void parse(File file) {
        InputStreamReader fileReader;
        if (file == null || !file.exists() || file.length() == 0) {
            return;
        }
        InputStreamReader inputStreamReader = null;
        try {
            if (file.getName().endsWith(".gz")) {
                fileReader = new InputStreamReader(new GZIPInputStream(new FileInputStream(file)));
            } else {
                fileReader = new FileReader(file);
            }
            inputStreamReader = fileReader;
            parse(inputStreamReader, this.mFileNameToLibraryToContentIdMap, this.mContentIdToFileContentMap);
            inputStreamReader.close();
        } catch (IOException | XmlPullParserException e) {
            Log.e("LicenseGeneratorFromXml", "Failed to parse " + file, e);
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException unused) {
                    Log.w("LicenseGeneratorFromXml", "Failed to close " + file);
                }
            }
        }
    }

    static void parse(InputStreamReader inputStreamReader, Map<String, Map<String, Set<String>>> map, Map<String, String> map2) throws XmlPullParserException, IOException {
        HashMap hashMap = new HashMap();
        Map<? extends String, ? extends String> hashMap2 = new HashMap<>();
        XmlPullParser newPullParser = Xml.newPullParser();
        newPullParser.setInput(inputStreamReader);
        newPullParser.nextTag();
        newPullParser.require(2, "", "licenses");
        for (int eventType = newPullParser.getEventType(); eventType != 1; eventType = newPullParser.next()) {
            if (eventType == 2) {
                if ("file-name".equals(newPullParser.getName())) {
                    String attributeValue = newPullParser.getAttributeValue("", "contentId");
                    String attributeValue2 = newPullParser.getAttributeValue("", "lib");
                    if (!TextUtils.isEmpty(attributeValue)) {
                        String trim = readText(newPullParser).trim();
                        if (!TextUtils.isEmpty(trim)) {
                            ((Set) ((Map) hashMap.computeIfAbsent(trim, new Function() { // from class: com.android.settingslib.license.LicenseHtmlGeneratorFromXml$$ExternalSyntheticLambda2
                                @Override // java.util.function.Function
                                public final Object apply(Object obj) {
                                    Map lambda$parse$0;
                                    lambda$parse$0 = LicenseHtmlGeneratorFromXml.lambda$parse$0((String) obj);
                                    return lambda$parse$0;
                                }
                            })).computeIfAbsent(attributeValue2, new Function() { // from class: com.android.settingslib.license.LicenseHtmlGeneratorFromXml$$ExternalSyntheticLambda3
                                @Override // java.util.function.Function
                                public final Object apply(Object obj) {
                                    Set lambda$parse$1;
                                    lambda$parse$1 = LicenseHtmlGeneratorFromXml.lambda$parse$1((String) obj);
                                    return lambda$parse$1;
                                }
                            })).add(attributeValue);
                        }
                    }
                } else if ("file-content".equals(newPullParser.getName())) {
                    String attributeValue3 = newPullParser.getAttributeValue("", "contentId");
                    if (!TextUtils.isEmpty(attributeValue3) && !map2.containsKey(attributeValue3) && !hashMap2.containsKey(attributeValue3)) {
                        String readText = readText(newPullParser);
                        if (!TextUtils.isEmpty(readText)) {
                            hashMap2.put(attributeValue3, readText);
                        }
                    }
                }
            }
        }
        for (Map.Entry entry : hashMap.entrySet()) {
            map.merge((String) entry.getKey(), (Map) entry.getValue(), new BiFunction() { // from class: com.android.settingslib.license.LicenseHtmlGeneratorFromXml$$ExternalSyntheticLambda4
                @Override // java.util.function.BiFunction
                public final Object apply(Object obj, Object obj2) {
                    Map lambda$parse$3;
                    lambda$parse$3 = LicenseHtmlGeneratorFromXml.lambda$parse$3((Map) obj, (Map) obj2);
                    return lambda$parse$3;
                }
            });
        }
        map2.putAll(hashMap2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Map lambda$parse$0(String str) {
        return new HashMap();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Set lambda$parse$1(String str) {
        return new HashSet();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Map lambda$parse$3(Map map, Map map2) {
        for (Map.Entry entry : map2.entrySet()) {
            map.merge((String) entry.getKey(), (Set) entry.getValue(), new BiFunction() { // from class: com.android.settingslib.license.LicenseHtmlGeneratorFromXml$$ExternalSyntheticLambda5
                @Override // java.util.function.BiFunction
                public final Object apply(Object obj, Object obj2) {
                    return LicenseHtmlGeneratorFromXml.lambda$parse$2((Set) obj, (Set) obj2);
                }
            });
        }
        return map;
    }

    /*  JADX ERROR: NullPointerException in pass: MarkMethodsForInline
        java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.isRegister()" because "arg" is null
        	at jadx.core.dex.instructions.args.RegisterArg.sameRegAndSVar(RegisterArg.java:173)
        	at jadx.core.dex.instructions.args.InsnArg.isSameVar(InsnArg.java:269)
        	at jadx.core.dex.visitors.MarkMethodsForInline.isSyntheticAccessPattern(MarkMethodsForInline.java:118)
        	at jadx.core.dex.visitors.MarkMethodsForInline.inlineMth(MarkMethodsForInline.java:86)
        	at jadx.core.dex.visitors.MarkMethodsForInline.process(MarkMethodsForInline.java:53)
        	at jadx.core.dex.visitors.MarkMethodsForInline.visit(MarkMethodsForInline.java:37)
        */
    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ java.util.Set lambda$parse$2(java.util.Set r0, java.util.Set r1) {
        /*
            r0.addAll(r1)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.license.LicenseHtmlGeneratorFromXml.lambda$parse$2(java.util.Set, java.util.Set):java.util.Set");
    }

    private static String readText(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        StringBuffer stringBuffer = new StringBuffer();
        int next = xmlPullParser.next();
        while (next == 4) {
            stringBuffer.append(xmlPullParser.getText());
            next = xmlPullParser.next();
        }
        return stringBuffer.toString();
    }

    static void generateHtml(Map<String, Map<String, Set<String>>> map, Map<String, String> map2, PrintWriter printWriter, String str) {
        int i;
        ArrayList<String> arrayList = new ArrayList();
        arrayList.addAll(map.keySet());
        Collections.sort(arrayList);
        TreeMap treeMap = new TreeMap();
        for (Map<String, Set<String>> map3 : map.values()) {
            for (Map.Entry<String, Set<String>> entry : map3.entrySet()) {
                if (!TextUtils.isEmpty(entry.getKey())) {
                    treeMap.merge(entry.getKey(), entry.getValue(), new BiFunction() { // from class: com.android.settingslib.license.LicenseHtmlGeneratorFromXml$$ExternalSyntheticLambda0
                        @Override // java.util.function.BiFunction
                        public final Object apply(Object obj, Object obj2) {
                            return LicenseHtmlGeneratorFromXml.lambda$generateHtml$4((Set) obj, (Set) obj2);
                        }
                    });
                }
            }
        }
        printWriter.println("<html><head>\n<style type=\"text/css\">\nbody { padding: 0; font-family: sans-serif; }\n.same-license { background-color: #eeeeee;\n                border-top: 20px solid white;\n                padding: 10px; }\n.label { font-weight: bold; }\n.file-list { margin-left: 1em; color: blue; }\n</style>\n</head><body topmargin=\"0\" leftmargin=\"0\" rightmargin=\"0\" bottommargin=\"0\">\n<div class=\"toc\">");
        if (!TextUtils.isEmpty(str)) {
            printWriter.println(str);
        }
        HashMap hashMap = new HashMap();
        ArrayList<ContentIdAndFileNames> arrayList2 = new ArrayList();
        if (treeMap.isEmpty()) {
            i = 0;
        } else {
            printWriter.println("<strong>Libraries</strong>\n<ul class=\"libraries\">");
            i = 0;
            for (Map.Entry entry2 : treeMap.entrySet()) {
                Object obj = (String) entry2.getKey();
                for (String str2 : (Set) entry2.getValue()) {
                    if (!hashMap.containsKey(str2)) {
                        hashMap.put(str2, Integer.valueOf(i));
                        arrayList2.add(new ContentIdAndFileNames(str2));
                        i++;
                    }
                    printWriter.format("<li><a href=\"#id%d\">%s</a></li>\n", Integer.valueOf(((Integer) hashMap.get(str2)).intValue()), obj);
                }
            }
            printWriter.println("</ul>\n<strong>Files</strong>");
        }
        printWriter.println("<ul class=\"files\">");
        for (String str3 : arrayList) {
            for (Map.Entry<String, Set<String>> entry3 : map.get(str3).entrySet()) {
                String key = entry3.getKey();
                if (key == null) {
                    key = "";
                }
                for (String str4 : entry3.getValue()) {
                    if (!hashMap.containsKey(str4)) {
                        hashMap.put(str4, Integer.valueOf(i));
                        arrayList2.add(new ContentIdAndFileNames(str4));
                        i++;
                    }
                    int intValue = ((Integer) hashMap.get(str4)).intValue();
                    ((ContentIdAndFileNames) arrayList2.get(intValue)).mLibraryToFileNameMap.computeIfAbsent(key, new Function() { // from class: com.android.settingslib.license.LicenseHtmlGeneratorFromXml$$ExternalSyntheticLambda1
                        @Override // java.util.function.Function
                        public final Object apply(Object obj2) {
                            List lambda$generateHtml$5;
                            lambda$generateHtml$5 = LicenseHtmlGeneratorFromXml.lambda$generateHtml$5((String) obj2);
                            return lambda$generateHtml$5;
                        }
                    }).add(str3);
                    if (TextUtils.isEmpty(key)) {
                        printWriter.format("<li><a href=\"#id%d\">%s</a></li>\n", Integer.valueOf(intValue), str3);
                    } else {
                        printWriter.format("<li><a href=\"#id%d\">%s - %s</a></li>\n", Integer.valueOf(intValue), str3, key);
                    }
                }
            }
        }
        printWriter.println("</ul>\n</div><!-- table of contents -->\n<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
        int i2 = 0;
        for (ContentIdAndFileNames contentIdAndFileNames : arrayList2) {
            printWriter.format("<tr id=\"id%d\"><td class=\"same-license\">\n", Integer.valueOf(i2));
            for (Map.Entry<String, List<String>> entry4 : contentIdAndFileNames.mLibraryToFileNameMap.entrySet()) {
                String key2 = entry4.getKey();
                if (TextUtils.isEmpty(key2)) {
                    printWriter.println("<div class=\"label\">Notices for file(s):</div>");
                } else {
                    printWriter.format("<div class=\"label\"><strong>%s</strong> used by:</div>\n", key2);
                }
                printWriter.println("<div class=\"file-list\">");
                Iterator<String> it = entry4.getValue().iterator();
                while (it.hasNext()) {
                    printWriter.format("%s <br/>\n", (String) it.next());
                }
                printWriter.println("</div><!-- file-list -->");
                i2++;
            }
            printWriter.println("<pre class=\"license-text\">");
            printWriter.println(map2.get(contentIdAndFileNames.mContentId));
            printWriter.println("</pre><!-- license-text -->");
            printWriter.println("</td></tr><!-- same-license -->");
        }
        printWriter.println("</table></body></html>");
    }

    /*  JADX ERROR: NullPointerException in pass: MarkMethodsForInline
        java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.isRegister()" because "arg" is null
        	at jadx.core.dex.instructions.args.RegisterArg.sameRegAndSVar(RegisterArg.java:173)
        	at jadx.core.dex.instructions.args.InsnArg.isSameVar(InsnArg.java:269)
        	at jadx.core.dex.visitors.MarkMethodsForInline.isSyntheticAccessPattern(MarkMethodsForInline.java:118)
        	at jadx.core.dex.visitors.MarkMethodsForInline.inlineMth(MarkMethodsForInline.java:86)
        	at jadx.core.dex.visitors.MarkMethodsForInline.process(MarkMethodsForInline.java:53)
        	at jadx.core.dex.visitors.MarkMethodsForInline.visit(MarkMethodsForInline.java:37)
        */
    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ java.util.Set lambda$generateHtml$4(java.util.Set r0, java.util.Set r1) {
        /*
            r0.addAll(r1)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.license.LicenseHtmlGeneratorFromXml.lambda$generateHtml$4(java.util.Set, java.util.Set):java.util.Set");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ List lambda$generateHtml$5(String str) {
        return new ArrayList();
    }
}
