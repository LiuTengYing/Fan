package com.android.settings.factory.protocol;

import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.android.settings.SettingsApplication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class TestDataProvider {
    public static int icarbox = -1;
    public static int icarset = -1;
    public static int icartype = -1;
    public static int icomp = -1;
    List<companydata> companydataList = new ArrayList();
    List<canbusdata> canbusdataList = new ArrayList();
    List<cartypedata> cartypedataList = new ArrayList();
    List<canboxdata> canboxdataList = new ArrayList();
    List<canbusdata> filtelistCarSet = new ArrayList();
    Map<Integer, Integer> hashcanbusMap = new HashMap();
    List<cartypedata> filtelistCarType = new ArrayList();
    Map<Integer, Integer> hashcantypemap = new HashMap();
    List<canboxdata> filtelistCarBox = new ArrayList();

    public Boolean getAllDate() {
        String string;
        String string2;
        try {
            Cursor query = SettingsApplication.mApplication.getContentResolver().query(Uri.parse("content://com.syu.protocolProvider/protocol_company_list"), null, null, null, "order_num");
            while (query.moveToNext()) {
                companydata companydataVar = new companydata();
                companydataVar.canbus_company_id = query.getInt(0);
                companydataVar.canbus_company_ch = query.getString(1);
                companydataVar.canbus_company_en = query.getString(2);
                companydataVar.canbus_company_tw = query.getString(3);
                companydataVar.company_name = query.getString(4);
                companydataVar.company_order_num = query.getInt(5);
                this.companydataList.add(companydataVar);
            }
            query.close();
            Cursor query2 = SettingsApplication.mApplication.getContentResolver().query(Uri.parse("content://com.syu.protocolProvider/protocol_carset_listall"), null, null, null, null);
            while (query2.moveToNext()) {
                canbusdata canbusdataVar = new canbusdata();
                canbusdataVar.canbus_carset_id = query2.getInt(0);
                canbusdataVar.canbus_carset_ch = query2.getString(1);
                canbusdataVar.canbus_carset_en = query2.getString(2);
                canbusdataVar.canbus_carset_tw = query2.getString(3);
                canbusdataVar.carset_name = query2.getString(4);
                this.canbusdataList.add(canbusdataVar);
            }
            Cursor query3 = SettingsApplication.mApplication.getContentResolver().query(Uri.parse("content://com.syu.protocolProvider/protocol_cartype_listall"), null, null, null, null);
            while (query3.moveToNext()) {
                cartypedata cartypedataVar = new cartypedata();
                cartypedataVar.canbus_cartype_id = query3.getInt(0);
                cartypedataVar.canbus_cartype_ch = query3.getString(1);
                cartypedataVar.canbus_cartype_en = query3.getString(2);
                cartypedataVar.canbus_cartype_tw = query3.getString(3);
                cartypedataVar.cartype_name = query3.getString(4);
                cartypedataVar.cartype_msg = query3.getString(5);
                this.cartypedataList.add(cartypedataVar);
            }
            Cursor query4 = SettingsApplication.mApplication.getContentResolver().query(Uri.parse("content://com.syu.protocolProvider/protocol_carbox_listall"), null, null, null, null);
            while (query4.moveToNext()) {
                canboxdata canboxdataVar = new canboxdata();
                canboxdataVar.canbus_canbox_id = query4.getInt(0);
                canboxdataVar.canbus_canbox_ch = query4.getString(1);
                canboxdataVar.canbus_canbox_en = query4.getString(2);
                canboxdataVar.canbus_canbox_tw = query4.getString(3);
                canboxdataVar.id_value = query4.getInt(4);
                canboxdataVar.disp = query4.getInt(5);
                canboxdataVar.canbox_name = query4.getString(6);
                canboxdataVar.company_id = query4.getInt(7);
                canboxdataVar.carsetid = query4.getInt(8);
                canboxdataVar.cartypeid = query4.getInt(9);
                canboxdataVar.canbox_limits = null;
                if (query4.getColumnCount() > 10 && (string2 = query4.getString(10)) != null) {
                    canboxdataVar.canbox_limits = string2;
                }
                canboxdataVar.canbox_limits_byid = null;
                if (query4.getColumnCount() > 11 && (string = query4.getString(11)) != null) {
                    canboxdataVar.canbox_limits_byid = string;
                }
                this.canboxdataList.add(canboxdataVar);
            }
        } catch (Exception e) {
            Log.d("MyTest", e.toString());
            e.printStackTrace();
        }
        return Boolean.TRUE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class companydata {
        String canbus_company_ch;
        String canbus_company_en;
        int canbus_company_id;
        String canbus_company_tw;
        String company_name;
        int company_order_num;

        companydata() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class canbusdata {
        String canbus_carset_ch;
        String canbus_carset_en;
        int canbus_carset_id;
        String canbus_carset_tw;
        String carset_name;

        canbusdata() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class cartypedata {
        String canbus_cartype_ch;
        String canbus_cartype_en;
        int canbus_cartype_id;
        String canbus_cartype_tw;
        String cartype_msg;
        String cartype_name;

        cartypedata() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class canboxdata {
        String canbox_limits;
        String canbox_limits_byid;
        String canbox_name;
        String canbus_canbox_ch;
        String canbus_canbox_en;
        int canbus_canbox_id;
        String canbus_canbox_tw;
        int carsetid;
        int cartypeid;
        int company_id;
        int disp;
        int id_value;

        canboxdata() {
        }
    }

    public Boolean hasCompanydata() {
        if (icomp >= this.companydataList.size() - 1) {
            return Boolean.FALSE;
        }
        icomp++;
        return Boolean.TRUE;
    }

    public companydata getCompanyData() {
        return this.companydataList.get(icomp);
    }

    public List<canbusdata> getCanbusData(int i) {
        for (canboxdata canboxdataVar : this.canboxdataList) {
            if (canboxdataVar.company_id == i) {
                for (canbusdata canbusdataVar : this.canbusdataList) {
                    int i2 = canbusdataVar.canbus_carset_id;
                    if (i2 == canboxdataVar.carsetid && !this.hashcanbusMap.containsKey(Integer.valueOf(i2))) {
                        this.filtelistCarSet.add(canbusdataVar);
                        this.hashcanbusMap.put(Integer.valueOf(canbusdataVar.canbus_carset_id), Integer.valueOf(canbusdataVar.canbus_carset_id));
                    }
                }
            }
        }
        return this.filtelistCarSet;
    }

    public Boolean hasCarSetdata() {
        if (icarset >= this.filtelistCarSet.size() - 1) {
            return Boolean.FALSE;
        }
        icarset++;
        return Boolean.TRUE;
    }

    public canbusdata getCarSetData() {
        return this.filtelistCarSet.get(icarset);
    }

    public List<cartypedata> getCanTypeData(int i, int i2) {
        for (canboxdata canboxdataVar : this.canboxdataList) {
            if (canboxdataVar.company_id == i && canboxdataVar.carsetid == i2) {
                for (cartypedata cartypedataVar : this.cartypedataList) {
                    int i3 = cartypedataVar.canbus_cartype_id;
                    if (i3 == canboxdataVar.cartypeid && !this.hashcantypemap.containsKey(Integer.valueOf(i3))) {
                        this.filtelistCarType.add(cartypedataVar);
                        this.hashcantypemap.put(Integer.valueOf(cartypedataVar.canbus_cartype_id), Integer.valueOf(cartypedataVar.canbus_cartype_id));
                    }
                }
            }
        }
        return this.filtelistCarType;
    }

    public Boolean hasCarTypedata() {
        if (icartype >= this.filtelistCarType.size() - 1) {
            return Boolean.FALSE;
        }
        icartype++;
        return Boolean.TRUE;
    }

    public cartypedata getCarTypeData() {
        return this.filtelistCarType.get(icartype);
    }

    public List<canboxdata> getCanBoxData(int i, int i2, int i3) {
        for (canboxdata canboxdataVar : this.canboxdataList) {
            if (canboxdataVar.company_id == i && canboxdataVar.carsetid == i2 && canboxdataVar.cartypeid == i3) {
                this.filtelistCarBox.add(canboxdataVar);
            }
        }
        return this.filtelistCarBox;
    }

    public Boolean hasCarBoxdata() {
        if (icarbox >= this.filtelistCarBox.size() - 1) {
            return Boolean.FALSE;
        }
        icarbox++;
        return Boolean.TRUE;
    }

    public canboxdata getCarBoxData() {
        return this.filtelistCarBox.get(icarbox);
    }
}
