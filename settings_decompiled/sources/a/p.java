package a;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
/* loaded from: classes.dex */
public class p extends m {
    private ResourceBundle b = ResourceBundle.getBundle("com.abupdate.mqtt_libs.mqttv3.internal.nls.messages");

    @Override // a.m
    protected String a(int i) {
        try {
            return this.b.getString(Integer.toString(i));
        } catch (MissingResourceException unused) {
            return "MqttException";
        }
    }
}
