package com.sprytar.android.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sprytar.android.R;
import com.sprytar.android.databinding.ActivityPickUserBinding;
import com.sprytar.android.databinding.ItemAvatarFamilyBinding;
import com.sprytar.android.login.DaggerPickUserComponent;
import com.sprytar.android.SprytarApplication;
import com.sprytar.android.data.model.FamilyMember;
import com.sprytar.android.main.MainActivity;
import com.sprytar.android.presentation.BaseActivity;
import com.sprytar.android.presentation.BaseBindingAdapter;
import com.sprytar.android.presentation.BaseBindingViewHolder;
import com.sprytar.android.util.bottomsheet.CustomGridLayoutManager;
import com.sprytar.android.util.ui.SimpleDividerDecoration;

import org.joda.time.DateTime;
import org.joda.time.Years;

import java.util.List;

import javax.inject.Inject;

public class PickUserActivity extends BaseActivity implements PickUserView {

  private final BaseBindingAdapter.ItemClickListener<FamilyMember> memberItemClickListener =
      new BaseBindingAdapter.ItemClickListener<FamilyMember>() {
        @Override public void onClick(FamilyMember item, int position) {
          presenter.onMemberItemClicked(item);
        }
      };

  @Inject
  PickUserPresenter presenter;

  @Inject FamilyMemberAdapter adapter;

  private ActivityPickUserBinding binding;
  private ActionBar actionBar;
  private Tracker mTracker;
  private AlertDialog confirmPasswordDialog = null;

  public static void start(Context context) {

    Intent starter = new Intent(context, PickUserActivity.class);
    context.startActivity(starter);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_pick_user);

    createComponent().inject(this);
    presenter.attachView(this);

    presenter.loadFamilyMembers();

    mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
    mTracker.setScreenName("Pick User Screen");
    mTracker.send(new HitBuilders.EventBuilder().build());
    mTracker.enableAutoActivityTracking(true);

    initUi();
  }

  private void initUi() {

    setSupportActionBar(binding.toolbar);
    actionBar = getSupportActionBar();
    //actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle(getString(R.string.pick_user_title));

    final CustomGridLayoutManager layoutManager = new CustomGridLayoutManager(getApplicationContext());

    adapter.setItemClickListener(memberItemClickListener);

    binding.itemsRecyclerView.setLayoutManager(layoutManager);
    binding.itemsRecyclerView.addItemDecoration(new SimpleDividerDecoration(getApplicationContext()));
    binding.itemsRecyclerView.setAdapter(adapter);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    presenter.detachView();
  }

  @Override public boolean onSupportNavigateUp() {
    //onBackPressed();
    return true;
  }

  private PickUserComponent createComponent() {
    return DaggerPickUserComponent.builder().sessionComponent(getSessionComponent()).loginModule(new LoginModule()).build();
  }

  @Override public void showError(String message) {
    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
  }

  @Override public void showMainActivity() {
    MainActivity.start(this);
  }

  @Override public void showLoadingIndicator() {
    binding.progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoadingIndicator() {
    binding.progressBar.setVisibility(View.GONE);
  }

  @Override public void showItems(List<FamilyMember> items) {
  }

  @Override public void showItems(List<FamilyMember> items, ArrayMap<String, Uri> avatarMap) {
    adapter.setItems(items, avatarMap);
    adapter.notifyDataSetChanged();
  }

  @Override public void clearItems() {
    adapter.clear();
  }

  @Override public void displayConfirmPasswordDialog(ConfirmPasswordDialog.OnConfirmPasswordListener listener) {
    confirmPasswordDialog = ConfirmPasswordDialog.getDialog(this, listener);
    confirmPasswordDialog.show();
  }

  @Override public void hideConfirmPasswordDialog() {
    if (confirmPasswordDialog != null) {
      confirmPasswordDialog.dismiss();
    }
  }

  @Override public void showMessage(String text) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
  }

  public void onAvatarClick(View view) {
    MainActivity.start(this);
  }

  public static class FamilyMemberAdapter extends BaseBindingAdapter<FamilyMember> {
    private final Context context;

    private ArrayMap<String, Uri> avatarMap;

    @Inject public FamilyMemberAdapter(Context context) {
      this.context = context;
      this.avatarMap = new ArrayMap<>();
    }

    public void setItems(List<FamilyMember> items, ArrayMap<String, Uri> avatarMap) {
      this.items = items;
      this.avatarMap = avatarMap;
    }

    @Override protected ViewDataBinding bind(LayoutInflater inflater, ViewGroup parent, int viewType) {
      return ItemAvatarFamilyBinding.inflate(inflater, parent, false);
    }

    @Override public void onBindViewHolder(BaseBindingViewHolder holder, int position) {
      ItemAvatarFamilyBinding binding = (ItemAvatarFamilyBinding) holder.binding;

      FamilyMember familyMember = items.get(position);

      DateTime currentDate = new DateTime();
      DateTime birthdayDate = new DateTime(familyMember.getBirthday() * 1000);
      Years years = Years.yearsBetween(birthdayDate, currentDate);

      binding.nameTextView.setText(context.getString(R.string.item_member_tilte, familyMember.getName(), String.valueOf(years.getYears())));
      binding.currentUserCheck.setChecked(familyMember.isCurrentUser());

      Uri avatarUri = avatarMap.get(familyMember.getAvatar());

      familyMember.setAvatarUri(avatarUri);

      DraweeController controller =
          Fresco.newDraweeControllerBuilder().setUri(avatarUri).setOldController(binding.avatarImageView.getController()).build();
      binding.avatarImageView.setController(controller);
    }
  }
}
