package com.sprytar.android.family;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.sprytar.android.R;
import com.sprytar.android.databinding.ItemFamilyMemberBinding;
import com.sprytar.android.data.model.FamilyMember;
import com.sprytar.android.events.FamilyMemberEvent;
import com.sprytar.android.presentation.BaseBindingAdapter;
import com.sprytar.android.presentation.BaseBindingViewHolder;
import com.sprytar.android.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.joda.time.Years;

import javax.inject.Inject;

public class FamilyMemberAdapter extends BaseBindingAdapter<FamilyMember> {

    private AdapterCallback adapterCallback;

    private Context context;
    private EventBus bus;

    @Inject
    public FamilyMemberAdapter(Context context, EventBus bus) {
        this.context = context;
        this.bus = bus;
    }

    @Override
    protected ViewDataBinding bind(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemFamilyMemberBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onBindViewHolder(BaseBindingViewHolder holder, final int position) {

        FamilyMember familyMember = items.get(position);
        ItemFamilyMemberBinding binding = (ItemFamilyMemberBinding) holder.binding;
        binding.setMember(familyMember);


        DateTime currentDate = new DateTime();
        DateTime birthdayDate = new DateTime(familyMember.getBirthday() * 1000);
        Years years = Years.yearsBetween(birthdayDate, currentDate);

        binding.nameTextView.setText(context.getString(R.string.item_member_tilte, familyMember
                .getName(), String.valueOf(Utils.getAge(familyMember.getBirthday()))));

        binding.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bus.post(new FamilyMemberEvent.Builder()
                        .setActionType(FamilyMemberEvent.DELETE)
                        .setPosition(position)
                        .build());
                //adapterCallback.onItemDelete(position);
            }
        });

        int avatarId = context.getResources().getIdentifier(familyMember.getAvatar(), "drawable",
                context.getPackageName());
        Uri avatarUri = Uri.parse("res:///" + String.valueOf(avatarId));

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(avatarUri)
                .setOldController(binding.avatarImageView.getController())
                .build();
        binding.avatarImageView.setController(controller);

    }

    public interface AdapterCallback {
        void onItemDelete(int position);
    }

    public void setCallback(AdapterCallback adapterCallback) {
        this.adapterCallback = adapterCallback;
    }

}
