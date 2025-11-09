package com.google.android.setupdesign.items;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public abstract class SimpleInflater<T> {
    protected final Resources resources;

    protected abstract void onAddChildItem(T t, T t2);

    protected abstract T onCreateItem(String str, AttributeSet attributeSet);

    protected boolean onInterceptCreateItem(XmlPullParser xmlPullParser, T t, AttributeSet attributeSet) throws XmlPullParserException {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SimpleInflater(Resources resources) {
        this.resources = resources;
    }

    public Resources getResources() {
        return this.resources;
    }

    public T inflate(int i) {
        XmlResourceParser xml = getResources().getXml(i);
        try {
            return inflate(xml);
        } finally {
            xml.close();
        }
    }

    public T inflate(XmlPullParser xmlPullParser) {
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
        }
        T createItemFromTag = createItemFromTag(xmlPullParser.getName(), asAttributeSet);
        rInflate(xmlPullParser, createItemFromTag, asAttributeSet);
        return createItemFromTag;
    }

    private T createItemFromTag(String str, AttributeSet attributeSet) {
        try {
            return onCreateItem(str, attributeSet);
        } catch (InflateException e) {
            throw e;
        } catch (Exception e2) {
            throw new InflateException(attributeSet.getPositionDescription() + ": Error inflating class " + str, e2);
        }
    }

    private void rInflate(XmlPullParser xmlPullParser, T t, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next = xmlPullParser.next();
            if ((next == 3 && xmlPullParser.getDepth() <= depth) || next == 1) {
                return;
            }
            if (next == 2 && !onInterceptCreateItem(xmlPullParser, t, attributeSet)) {
                T createItemFromTag = createItemFromTag(xmlPullParser.getName(), attributeSet);
                onAddChildItem(t, createItemFromTag);
                rInflate(xmlPullParser, createItemFromTag, attributeSet);
            }
        }
    }
}
