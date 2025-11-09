package a;
/* loaded from: classes.dex */
public abstract class m {

    /* renamed from: a  reason: collision with root package name */
    private static m f0a;

    public static final String b(int i) {
        if (f0a == null) {
            if (j.a("java.util.ResourceBundle")) {
                try {
                    f0a = (m) p.class.newInstance();
                } catch (Exception unused) {
                    return "";
                }
            } else if (j.a("com.abupdate.mqtt_libs.mqttv3.internal.MIDPCatalog")) {
                try {
                    f0a = (m) Class.forName("com.abupdate.mqtt_libs.mqttv3.internal.MIDPCatalog").newInstance();
                } catch (Exception unused2) {
                    return "";
                }
            }
        }
        return f0a.a(i);
    }

    protected abstract String a(int i);
}
