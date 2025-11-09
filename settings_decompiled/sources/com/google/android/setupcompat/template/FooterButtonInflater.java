package com.google.android.setupcompat.template;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
class FooterButtonInflater {
    protected final Context context;

    public FooterButtonInflater(Context context) {
        this.context = context;
    }

    public Resources getResources() {
        return this.context.getResources();
    }

    public FooterButton inflate(int i) {
        XmlResourceParser xml = getResources().getXml(i);
        try {
            return inflate(xml);
        } finally {
            xml.close();
        }
    }

    private FooterButton inflate(XmlPullParser xmlPullParser) {
        int next;
        AttributeSet asAttributeSet = Xml.asAttributeSet(xmlPullParser);
        while (true) {
            try {
                next = xmlPullParser.next();
                if (next == 2 || next == 1) {
                    break;
                }
            } catch (IOException e) {
                throw new InflateException(xmlPullParser.getPositionDescription() + ": " + e.getMessage(), e);
            } catch (XmlPullParserException e2) {
                throw new InflateException(e2.getMessage(), e2);
            }
        }
        if (next != 2) {
            throw new InflateException(xmlPullParser.getPositionDescription() + ": No start tag found!");
        } else if (!xmlPullParser.getName().equals("FooterButton")) {
            throw new InflateException(xmlPullParser.getPositionDescription() + ": not a FooterButton");
        } else {
            return new FooterButton(this.context, asAttributeSet);
        }
    }
}
