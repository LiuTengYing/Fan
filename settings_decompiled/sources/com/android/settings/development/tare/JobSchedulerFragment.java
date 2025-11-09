package com.android.settings.development.tare;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import com.android.settings.R$array;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.development.tare.TareFactorController;
/* loaded from: classes.dex */
public class JobSchedulerFragment extends Fragment implements TareFactorController.DataChangeListener {
    private String[][] mChildren;
    private TareFactorExpandableListAdapter mExpandableListAdapter;
    private TareFactorController mFactorController;
    private String[] mGroups;
    private String[][] mKeys;

    @Override // android.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mFactorController = TareFactorController.getInstance(getContext());
        populateArrays();
    }

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R$layout.tare_policy_fragment, (ViewGroup) null);
        ExpandableListView expandableListView = (ExpandableListView) inflate.findViewById(R$id.factor_list);
        this.mExpandableListAdapter = new TareFactorExpandableListAdapter(this.mFactorController, LayoutInflater.from(getActivity()), this.mGroups, this.mChildren, this.mKeys);
        expandableListView.setGroupIndicator(null);
        expandableListView.setAdapter(this.mExpandableListAdapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() { // from class: com.android.settings.development.tare.JobSchedulerFragment.1
            @Override // android.widget.ExpandableListView.OnChildClickListener
            public boolean onChildClick(ExpandableListView expandableListView2, View view, int i, int i2, long j) {
                String key = JobSchedulerFragment.this.mExpandableListAdapter.getKey(i, i2);
                JobSchedulerFragment.this.mFactorController.createDialog(key).show(JobSchedulerFragment.this.getFragmentManager(), key);
                return true;
            }
        });
        return inflate;
    }

    @Override // android.app.Fragment
    public void onStart() {
        super.onStart();
        this.mFactorController.registerListener(this);
    }

    @Override // android.app.Fragment
    public void onStop() {
        this.mFactorController.unregisterListener(this);
        super.onStop();
    }

    @Override // com.android.settings.development.tare.TareFactorController.DataChangeListener
    public void onDataChanged() {
        this.mExpandableListAdapter.notifyDataSetChanged();
    }

    private void populateArrays() {
        Resources resources = getResources();
        this.mGroups = new String[]{resources.getString(R$string.tare_consumption_limits), resources.getString(R$string.tare_balances), resources.getString(R$string.tare_actions_ctp), resources.getString(R$string.tare_actions_base_price), resources.getString(R$string.tare_rewards_instantaneous), resources.getString(R$string.tare_rewards_ongoing), resources.getString(R$string.tare_rewards_max)};
        int i = R$array.tare_job_scheduler_actions;
        int i2 = R$array.tare_rewards_subfactors;
        this.mChildren = new String[][]{resources.getStringArray(R$array.tare_consumption_limit_subfactors), resources.getStringArray(R$array.tare_app_balance_subfactors), resources.getStringArray(i), resources.getStringArray(i), resources.getStringArray(i2), new String[]{resources.getString(R$string.tare_top_activity)}, resources.getStringArray(i2)};
        this.mKeys = new String[][]{new String[]{"js_initial_consumption_limit", "js_hard_consumption_limit"}, new String[]{"js_max_satiated_balance", "js_min_satiated_balance_exempted", "js_min_satiated_balance_headless_system_app", "js_min_satiated_balance_other_app"}, new String[]{"js_action_job_max_start_ctp", "js_action_job_max_running_ctp", "js_action_job_high_start_ctp", "js_action_job_high_running_ctp", "js_action_job_default_start_ctp", "js_action_job_default_running_ctp", "js_action_job_low_start_ctp", "js_action_job_low_running_ctp", "js_action_job_min_start_ctp", "js_action_job_min_running_ctp", "js_action_job_timeout_penalty_ctp"}, new String[]{"js_action_job_max_start_base_price", "js_action_job_max_running_base_price", "js_action_job_high_start_base_price", "js_action_job_high_running_base_price", "js_action_job_default_start_base_price", "js_action_job_default_running_base_price", "js_action_job_low_start_base_price", "js_action_job_low_running_base_price", "js_action_job_min_start_base_price", "js_action_job_min_running_base_price", "js_action_job_timeout_penalty_base_price"}, new String[]{"js_reward_top_activity_instant", "js_reward_notification_seen_instant", "js_reward_notification_interaction_instant", "js_reward_widget_interaction_instant", "js_reward_other_user_interaction_instant"}, new String[]{"js_reward_top_activity_ongoing"}, new String[]{"js_reward_top_activity_max", "js_reward_notification_seen_max", "js_reward_notification_interaction_max", "js_reward_widget_interaction_max", "js_reward_other_user_interaction_max"}};
    }
}
