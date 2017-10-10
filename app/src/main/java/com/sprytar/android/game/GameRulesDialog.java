package com.sprytar.android.game;

import android.app.AlertDialog;
import android.app.Dialog;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sprytar.android.R;
import com.sprytar.android.databinding.DialogRulesBinding;
import com.sprytar.android.databinding.ItemGameRuleBinding;
import com.sprytar.android.data.model.GameRule;
import com.sprytar.android.presentation.BaseBindingAdapter;
import com.sprytar.android.presentation.BaseBindingViewHolder;

import java.util.ArrayList;
import java.util.List;

public class GameRulesDialog extends DialogFragment {

    public static final String RULES_PARAM = "com.sprytar.android.game.rulesParam";
    public static final int TYPE_TREASURE_RULES = 0;
    public static final int TYPE_PHOTO_HINT = 1;

    public static int type = TYPE_TREASURE_RULES;

    private DialogRulesBinding binding;

    public static GameRulesDialog newInstance() {
        //  Bundle args = new Bundle();
        //   args.putParcelable(RULES_PARAM, Parcels.wrap(getRules()));
        //    fragment.setArguments(args);
        GameRulesDialog fragment = new GameRulesDialog();
        return fragment;
    }

    private List<GameRule> getRules() {
        List<GameRule> rules = new ArrayList<>();
        rules.add(new GameRule(1, getResources().getString(R.string.game_rule_1_name), getResources().getString(R.string.game_rule_1_desc)));
        rules.add(new GameRule(2, getResources().getString(R.string.game_rule_2_name), getResources().getString(R.string.game_rule_2_desc)));
        rules.add(new GameRule(3, getResources().getString(R.string.game_rule_3_name), getResources().getString(R.string.game_rule_3_desc)));

        return rules;
    }

    private List<GameRule> getQuizRules(){
        List<GameRule> rules = new ArrayList<>();
        rules.add(new GameRule(1, getResources().getString(R.string.quiz_rule_1_title), getResources().getString(R.string.quiz_rule_1_desc)));
        rules.add(new GameRule(2, getResources().getString(R.string.quiz_rule_2_title), getResources().getString(R.string.quiz_rule_2_desc)));
        rules.add(new GameRule(3, getResources().getString(R.string.quiz_rule_3_title), getResources().getString(R.string.quiz_rule_3_desc)));

        return rules;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        binding = DialogRulesBinding.inflate(LayoutInflater.from(getContext()), null, false);
        dialogBuilder.setView(binding.getRoot());

        TextView title = new TextView(this.getContext());
        title.setText(getActivity().getString(R.string.rules));
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryText));
        title.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorBackground));
        title.setTextSize(20);

        binding.itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        List<GameRule> rules = new ArrayList<>();
        if(type == TYPE_TREASURE_RULES){
            rules = getRules();
        }else if(type == TYPE_PHOTO_HINT){
            rules = getQuizRules();
        }

        RuleAdapter adapter = new RuleAdapter();

        binding.itemsRecyclerView.setAdapter(adapter);

        adapter.setItems(rules);
        adapter.notifyDataSetChanged();

        dialogBuilder.setCustomTitle(title);

        binding.yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return dialogBuilder.create();
    }

    public static class RuleAdapter extends BaseBindingAdapter<GameRule> {

        public RuleAdapter() {
        }

        @Override
        protected ViewDataBinding bind(LayoutInflater inflater, ViewGroup parent, int viewType) {
            return ItemGameRuleBinding.inflate(inflater, parent, false);
        }

        @Override
        public void onBindViewHolder(BaseBindingViewHolder holder, int position) {
            ((ItemGameRuleBinding) holder.binding).setRule(items.get(position));
        }

    }

}
