package com.android.settings.utils;

import android.util.Log;
import java.io.BufferedReader;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.util.encoders.Base64;
/* loaded from: classes.dex */
public class AESCBCUtil {
    public static String decodeKey(String str) {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new StringReader("-----BEGIN PRIVATE KEY-----\nMIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQC78UYTcIAmZWnaaH/RITKTBaB7cHh70l72UyskH15IRSQQfNt8VUW9fmPd3RoCRAcqd5Q1CD98ccOsAmTmv2iRcrMOIWAgaMyiD2A8JzyUiRDl5qloM0k5XWrB1Em77ceJp+6KL+V7L0MRnuYbJeNtOHLdRy0UGNfpVylNBTz0RmqWNuou96KnKmqEOwHj3OfPK4Ih7+Vudy9ZkyY2bWveDymBKq7knsUN3/oI9vxsCS5tvaCagFDWvPFQVjoHwAW6bZGIVlp92OEiM6MGsKT3CRLOLeiZxfq5Q2kTIN6XRgee5mY6m+1hZF50/3/jvM0ZwZWQraD13sXGCPSp7dvnAgMBAAECggEAYYdWHxZdWg98e9uIoKTCkurzmbsGuvKzVs5X79nbWlOO4IN6fq3dj/U7q9FmAtD8ivwcy9xo3ZJHwUZIyDmB8SB9bJLDSG456sth3DVcSZRLUZJ+TNV4ZS1mDgm07bH5vySHM+yhAGjJPCaiMGpb97y4Vr3t1aUCvlL92rsiI3xXQxqOVy24aE00V7wPbPMijybdELLgxQPz1jaS+o67b0Zw4/THlpzj0Ty97TjzRf87NSPw203KZUH1npt2Hl1r4StZti4zYRIp+HrmbKcNu1nTtchT+h1vL1LBSSdreGEnw9MxSuBOZdL7/1NZBCRXxWM+45KORR1oYqXUOCCeoQKBgQDlxK4NKBYtISjp+WKi2oj/HCBI1Ay/VcghIl+7pttXdNghJO7mbeXi5/o2s4aWxXWKCR8Q4CzdhA+xEIfzgGT6YtZkkKi9EmlFsxqFvS1FaEJr9MOE7ONI0Vlvj502TozbipiDkJTrTppDWjFO9jgHp2O8S585yftuRd1edNZQ1wKBgQDRZiwjJZO5/iGf0bpV67AnVyVbf0CmoL53G+VTSuZwiBAyjGlXqZTU0tQG9dlqd/h+NCJYD4vqeV9fi0KPpsGHPTCs+YuvQ+AS8xHdwGe44AH3pN3LRZlFLoOaze6826diQRmoxx3ItLlWDLJKvFMCFvQYCtIf54D+B4L7KG6bcQKBgQCKjQojdtvAhPFl/XLkHwFjTDOPlwbEzdZrXI372wcC+rtlaR8CJpzsMdRo7eKnwhQ5k3x/MZ78SKkprX09cVvyvGzpoW2O7a47UGAa61OEOfVroeHQowP/IfZbVm+HkuBPdoi3NRUHu5M8MAPxyHlK7D4xCizOrSSHf2uYjL27awKBgQCRN/RgI41BrbaP6Zj6QUuW3N7xh1zEsjGk1NnDW+xYGmRxSeM8gkTPG68jbJhtN+pDS9CNlaqI38d+sUtl6FXKfmslaqKu17msKpo3WVoVYnS608FlWU0kJ0/pqetpWkPI5jDvVbeYG4102bVEEzrV9ikuC0MSETDp+zehCrc6YQKBgQC8Ti2+41ydPA4Ioj0W6Ayj5KwfYRKPJLioI63R4wLJygu07oVsccFsiw8HpYeHFK4FqFZpi2LLR9ReFB2Hx2l83RmkrasmjKe0J+upTWpBfN2OCNkcfWIq5cFuihizORCSEIG87z8RS+7q0DIGp8EA4qDNwKCZFNemSQJM5aeF/w==-----END PRIVATE KEY-----"));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    sb.append(readLine);
                } else {
                    PrivateKey generatePrivate = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(sb.toString().replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "").replaceAll("\\s+", ""))));
                    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                    cipher.init(2, generatePrivate);
                    return new String(cipher.doFinal(Base64.decode(str)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new String((byte[]) null);
        }
    }

    public static String decrypt(String str) {
        try {
            String[] split = str.split("\\|");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(decodeKey(split[1]).getBytes("UTF-8"));
            SecretKeySpec secretKeySpec = new SecretKeySpec(decodeKey(split[0]).getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING");
            cipher.init(2, secretKeySpec, ivParameterSpec);
            Log.i("xx", split[1]);
            return new String(cipher.doFinal(Base64.decode(split[2])));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
