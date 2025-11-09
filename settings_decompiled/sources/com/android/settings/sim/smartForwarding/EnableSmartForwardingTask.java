package com.android.settings.sim.smartForwarding;

import android.content.Context;
import android.telephony.CallForwardingInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.settings.sim.smartForwarding.EnableSmartForwardingTask;
import com.google.common.util.concurrent.SettableFuture;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class EnableSmartForwardingTask implements Callable<FeatureResult> {
    private final String[] mCallForwardingNumber;
    private final SubscriptionManager sm;
    private final TelephonyManager tm;
    FeatureResult mResult = new FeatureResult(false, null);
    SettableFuture<FeatureResult> client = SettableFuture.create();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface Command {
        boolean process() throws Exception;
    }

    public EnableSmartForwardingTask(Context context, String[] strArr) {
        this.tm = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        this.sm = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        this.mCallForwardingNumber = strArr;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.concurrent.Callable
    public FeatureResult call() throws TimeoutException, InterruptedException, ExecutionException {
        FlowController flowController = new FlowController();
        if (flowController.init(this.mCallForwardingNumber)) {
            flowController.startProcess();
        } else {
            this.client.set(this.mResult);
        }
        return (FeatureResult) this.client.get(20L, TimeUnit.SECONDS);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class FlowController {
        private SlotUTData[] mSlotUTData;
        private final ArrayList<Command> mSteps = new ArrayList<>();

        FlowController() {
        }

        public boolean init(String[] strArr) {
            if (initObject(strArr)) {
                initSteps();
                return true;
            }
            return false;
        }

        private boolean initObject(String[] strArr) {
            ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
            if (EnableSmartForwardingTask.this.tm == null || EnableSmartForwardingTask.this.sm == null) {
                Log.e("SmartForwarding", "TelephonyManager or SubscriptionManager is null");
                return false;
            } else if (strArr.length != EnableSmartForwardingTask.this.tm.getActiveModemCount()) {
                Log.e("SmartForwarding", "The length of PhoneNum array should same as phone count.");
                return false;
            } else {
                this.mSlotUTData = new SlotUTData[EnableSmartForwardingTask.this.tm.getActiveModemCount()];
                for (int i = 0; i < this.mSlotUTData.length; i++) {
                    int[] subscriptionIds = EnableSmartForwardingTask.this.sm.getSubscriptionIds(i);
                    if (subscriptionIds.length < 1) {
                        Log.e("SmartForwarding", "getSubscriptionIds() return empty sub id list.");
                        return false;
                    }
                    int i2 = subscriptionIds[0];
                    if (!EnableSmartForwardingTask.this.sm.isActiveSubId(i2)) {
                        EnableSmartForwardingTask.this.mResult.setReason(FeatureResult.FailedReason.SIM_NOT_ACTIVE);
                        return false;
                    }
                    QueryCallWaitingCommand queryCallWaitingCommand = new QueryCallWaitingCommand(EnableSmartForwardingTask.this.tm, newSingleThreadExecutor, i2);
                    QueryCallForwardingCommand queryCallForwardingCommand = new QueryCallForwardingCommand(EnableSmartForwardingTask.this.tm, newSingleThreadExecutor, i2);
                    this.mSlotUTData[i] = new SlotUTData(i2, strArr[i], queryCallWaitingCommand, queryCallForwardingCommand, new UpdateCallWaitingCommand(EnableSmartForwardingTask.this.tm, newSingleThreadExecutor, queryCallWaitingCommand, i2), new UpdateCallForwardingCommand(EnableSmartForwardingTask.this.tm, newSingleThreadExecutor, queryCallForwardingCommand, i2, strArr[i]));
                }
                return true;
            }
        }

        private void initSteps() {
            for (SlotUTData slotUTData : this.mSlotUTData) {
                this.mSteps.add(slotUTData.getQueryCallWaitingCommand());
            }
            for (SlotUTData slotUTData2 : this.mSlotUTData) {
                this.mSteps.add(slotUTData2.getQueryCallForwardingCommand());
            }
            for (SlotUTData slotUTData3 : this.mSlotUTData) {
                this.mSteps.add(slotUTData3.getUpdateCallWaitingCommand());
            }
            for (SlotUTData slotUTData4 : this.mSlotUTData) {
                this.mSteps.add(slotUTData4.getUpdateCallForwardingCommand());
            }
        }

        public void startProcess() {
            boolean z;
            int i = 0;
            boolean z2 = true;
            while (i < this.mSteps.size() && z2) {
                Command command = this.mSteps.get(i);
                Log.d("SmartForwarding", "processing : " + command);
                try {
                    z = command.process();
                } catch (Exception e) {
                    Log.d("SmartForwarding", "Failed on : " + command, e);
                    z = false;
                }
                if (z) {
                    i++;
                } else {
                    Log.d("SmartForwarding", "Failed on : " + command);
                }
                z2 = z;
            }
            if (z2) {
                EnableSmartForwardingTask.this.mResult.result = true;
                EnableSmartForwardingTask.this.mResult.slotUTData = this.mSlotUTData;
                Log.d("SmartForwarding", "Smart forwarding successful");
                EnableSmartForwardingTask enableSmartForwardingTask = EnableSmartForwardingTask.this;
                enableSmartForwardingTask.client.set(enableSmartForwardingTask.mResult);
                return;
            }
            restoreAllSteps(i);
            EnableSmartForwardingTask enableSmartForwardingTask2 = EnableSmartForwardingTask.this;
            enableSmartForwardingTask2.client.set(enableSmartForwardingTask2.mResult);
        }

        private void restoreAllSteps(int i) {
            List<Command> subList = this.mSteps.subList(0, i);
            Collections.reverse(subList);
            for (Command command : subList) {
                Log.d("SmartForwarding", "restoreStep: " + command);
                if (command instanceof UpdateCommand) {
                    ((UpdateCommand) command).onRestore();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public final class SlotUTData {
        String mCallForwardingNumber;
        QueryCallForwardingCommand mQueryCallForwarding;
        QueryCallWaitingCommand mQueryCallWaiting;
        UpdateCallForwardingCommand mUpdateCallForwarding;
        UpdateCallWaitingCommand mUpdateCallWaiting;
        int subId;

        public SlotUTData(int i, String str, QueryCallWaitingCommand queryCallWaitingCommand, QueryCallForwardingCommand queryCallForwardingCommand, UpdateCallWaitingCommand updateCallWaitingCommand, UpdateCallForwardingCommand updateCallForwardingCommand) {
            this.subId = i;
            this.mCallForwardingNumber = str;
            this.mQueryCallWaiting = queryCallWaitingCommand;
            this.mQueryCallForwarding = queryCallForwardingCommand;
            this.mUpdateCallWaiting = updateCallWaitingCommand;
            this.mUpdateCallForwarding = updateCallForwardingCommand;
        }

        public QueryCallWaitingCommand getQueryCallWaitingCommand() {
            return this.mQueryCallWaiting;
        }

        public QueryCallForwardingCommand getQueryCallForwardingCommand() {
            return this.mQueryCallForwarding;
        }

        public UpdateCallWaitingCommand getUpdateCallWaitingCommand() {
            return this.mUpdateCallWaiting;
        }

        public UpdateCallForwardingCommand getUpdateCallForwardingCommand() {
            return this.mUpdateCallForwarding;
        }
    }

    /* loaded from: classes.dex */
    static abstract class QueryCommand<T> implements Command {
        Executor executor;
        int subId;
        TelephonyManager tm;

        public QueryCommand(TelephonyManager telephonyManager, Executor executor, int i) {
            this.subId = i;
            this.tm = telephonyManager;
            this.executor = executor;
        }

        public String toString() {
            return getClass().getSimpleName() + "[SubId " + this.subId + "]";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static abstract class UpdateCommand<T> implements Command {
        Executor executor;
        int subId;
        TelephonyManager tm;

        abstract void onRestore();

        public UpdateCommand(TelephonyManager telephonyManager, Executor executor, int i) {
            this.subId = i;
            this.tm = telephonyManager;
            this.executor = executor;
        }

        public String toString() {
            return getClass().getSimpleName() + "[SubId " + this.subId + "] ";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class QueryCallWaitingCommand extends QueryCommand<Integer> {
        int result;
        SettableFuture<Boolean> resultFuture;

        public QueryCallWaitingCommand(TelephonyManager telephonyManager, Executor executor, int i) {
            super(telephonyManager, executor, i);
            this.resultFuture = SettableFuture.create();
        }

        @Override // com.android.settings.sim.smartForwarding.EnableSmartForwardingTask.Command
        public boolean process() throws Exception {
            this.tm.createForSubscriptionId(this.subId).getCallWaitingStatus(this.executor, new Consumer() { // from class: com.android.settings.sim.smartForwarding.EnableSmartForwardingTask$QueryCallWaitingCommand$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    EnableSmartForwardingTask.QueryCallWaitingCommand.this.queryStatusCallBack(((Integer) obj).intValue());
                }
            });
            return ((Boolean) this.resultFuture.get()).booleanValue();
        }

        Integer getResult() {
            return Integer.valueOf(this.result);
        }

        public void queryStatusCallBack(int i) {
            this.result = i;
            if (i == 1 || i == 2) {
                Log.d("SmartForwarding", "Call Waiting result: " + i);
                this.resultFuture.set(Boolean.TRUE);
                return;
            }
            this.resultFuture.set(Boolean.FALSE);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class QueryCallForwardingCommand extends QueryCommand<CallForwardingInfo> {
        CallForwardingInfo result;
        SettableFuture<Boolean> resultFuture;

        public QueryCallForwardingCommand(TelephonyManager telephonyManager, Executor executor, int i) {
            super(telephonyManager, executor, i);
            this.resultFuture = SettableFuture.create();
        }

        @Override // com.android.settings.sim.smartForwarding.EnableSmartForwardingTask.Command
        public boolean process() throws Exception {
            this.tm.createForSubscriptionId(this.subId).getCallForwarding(3, this.executor, new TelephonyManager.CallForwardingInfoCallback() { // from class: com.android.settings.sim.smartForwarding.EnableSmartForwardingTask.QueryCallForwardingCommand.1
                public void onCallForwardingInfoAvailable(CallForwardingInfo callForwardingInfo) {
                    Log.d("SmartForwarding", "Call Forwarding result: " + callForwardingInfo);
                    QueryCallForwardingCommand queryCallForwardingCommand = QueryCallForwardingCommand.this;
                    queryCallForwardingCommand.result = callForwardingInfo;
                    queryCallForwardingCommand.resultFuture.set(Boolean.TRUE);
                }

                public void onError(int i) {
                    Log.d("SmartForwarding", "Query Call Forwarding failed.");
                    QueryCallForwardingCommand.this.resultFuture.set(Boolean.FALSE);
                }
            });
            return ((Boolean) this.resultFuture.get()).booleanValue();
        }

        CallForwardingInfo getResult() {
            return this.result;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class UpdateCallWaitingCommand extends UpdateCommand<Integer> {
        QueryCallWaitingCommand queryResult;
        SettableFuture<Boolean> resultFuture;

        public UpdateCallWaitingCommand(TelephonyManager telephonyManager, Executor executor, QueryCallWaitingCommand queryCallWaitingCommand, int i) {
            super(telephonyManager, executor, i);
            this.resultFuture = SettableFuture.create();
            this.queryResult = queryCallWaitingCommand;
        }

        @Override // com.android.settings.sim.smartForwarding.EnableSmartForwardingTask.Command
        public boolean process() throws Exception {
            this.tm.createForSubscriptionId(this.subId).setCallWaitingEnabled(true, this.executor, new Consumer() { // from class: com.android.settings.sim.smartForwarding.EnableSmartForwardingTask$UpdateCallWaitingCommand$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    EnableSmartForwardingTask.UpdateCallWaitingCommand.this.updateStatusCallBack(((Integer) obj).intValue());
                }
            });
            return ((Boolean) this.resultFuture.get()).booleanValue();
        }

        public void updateStatusCallBack(int i) {
            Log.d("SmartForwarding", "UpdateCallWaitingCommand updateStatusCallBack result: " + i);
            if (i == 1 || i == 2) {
                this.resultFuture.set(Boolean.TRUE);
            } else {
                this.resultFuture.set(Boolean.FALSE);
            }
        }

        @Override // com.android.settings.sim.smartForwarding.EnableSmartForwardingTask.UpdateCommand
        void onRestore() {
            Log.d("SmartForwarding", "onRestore: " + this);
            if (this.queryResult.getResult().intValue() != 1) {
                this.tm.createForSubscriptionId(this.subId).setCallWaitingEnabled(false, null, null);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class UpdateCallForwardingCommand extends UpdateCommand<Integer> {
        String phoneNum;
        QueryCallForwardingCommand queryResult;
        SettableFuture<Boolean> resultFuture;

        public UpdateCallForwardingCommand(TelephonyManager telephonyManager, Executor executor, QueryCallForwardingCommand queryCallForwardingCommand, int i, String str) {
            super(telephonyManager, executor, i);
            this.resultFuture = SettableFuture.create();
            this.phoneNum = str;
            this.queryResult = queryCallForwardingCommand;
        }

        @Override // com.android.settings.sim.smartForwarding.EnableSmartForwardingTask.Command
        public boolean process() throws Exception {
            this.tm.createForSubscriptionId(this.subId).setCallForwarding(new CallForwardingInfo(true, 3, this.phoneNum, 3), this.executor, new Consumer() { // from class: com.android.settings.sim.smartForwarding.EnableSmartForwardingTask$UpdateCallForwardingCommand$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    EnableSmartForwardingTask.UpdateCallForwardingCommand.this.updateStatusCallBack(((Integer) obj).intValue());
                }
            });
            return ((Boolean) this.resultFuture.get()).booleanValue();
        }

        public void updateStatusCallBack(int i) {
            Log.d("SmartForwarding", "UpdateCallForwardingCommand updateStatusCallBack : " + i);
            if (i == 0) {
                this.resultFuture.set(Boolean.TRUE);
            } else {
                this.resultFuture.set(Boolean.FALSE);
            }
        }

        @Override // com.android.settings.sim.smartForwarding.EnableSmartForwardingTask.UpdateCommand
        void onRestore() {
            Log.d("SmartForwarding", "onRestore: " + this);
            this.tm.createForSubscriptionId(this.subId).setCallForwarding(this.queryResult.getResult(), null, null);
        }
    }

    /* loaded from: classes.dex */
    public static class FeatureResult {
        private FailedReason reason;
        private boolean result;
        private SlotUTData[] slotUTData;

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public enum FailedReason {
            NETWORK_ERROR,
            SIM_NOT_ACTIVE
        }

        public FeatureResult(boolean z, SlotUTData[] slotUTDataArr) {
            this.result = z;
            this.slotUTData = slotUTDataArr;
        }

        public boolean getResult() {
            return this.result;
        }

        public SlotUTData[] getSlotUTData() {
            return this.slotUTData;
        }

        public void setReason(FailedReason failedReason) {
            this.reason = failedReason;
        }

        public FailedReason getReason() {
            return this.reason;
        }
    }
}
