package com.sprytar.android.family;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sprytar.android.R;
import com.sprytar.android.databinding.ActivityFamilyMemberBinding;
import com.sprytar.android.family.DaggerFamilyMemberComponent;
import com.sprytar.android.SprytarApplication;
import com.sprytar.android.data.model.FamilyMember;
import com.sprytar.android.events.FamilyMemberEvent;
import com.sprytar.android.login.PickUserActivity;
import com.sprytar.android.presentation.BaseActivity;
import com.sprytar.android.presentation.BaseBindingAdapter;
import com.sprytar.android.util.ui.SimpleDividerDecoration;

import java.util.List;

import javax.inject.Inject;

public class FamilyMemberActivity extends BaseActivity implements FamilyMemberView, FamilyMemberAdapter.AdapterCallback {

  private static final String SIGNUP_EXTRA = "com.sprytar.android.family.signupExtra";

  private final BaseBindingAdapter.ItemClickListener<FamilyMember> memberItemClickListener =
      new BaseBindingAdapter.ItemClickListener<FamilyMember>() {
        @Override public void onClick(FamilyMember item, int position) {
          presenter.onMemberItemClicked(item);
        }
      };

  @Inject FamilyMemberPresenter presenter;

  @Inject
  FamilyMemberAdapter adapter;

  private ActivityFamilyMemberBinding binding;
  private boolean signUp;

  private MenuItem finishMenuItem;
  private Tracker mTracker;

  public static void start(Context context, boolean signUp) {
    Intent starter = new Intent(context, FamilyMemberActivity.class);

    starter.putExtra(SIGNUP_EXTRA, signUp);

    context.startActivity(starter);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_family_member);
    createComponent().inject(this);
    presenter.attachView(this);

    signUp = getIntent().getBooleanExtra(SIGNUP_EXTRA, false);

    presenter.getFamilyMembers();

    mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
    mTracker.setScreenName("Fmaily Member Screen");
    mTracker.send(new HitBuilders.EventBuilder().build());
    mTracker.enableAutoActivityTracking(true);
    initUi();
  }

  private void initUi() {

    setSupportActionBar(binding.toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getString(R.string.family_members_title));

    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    binding.itemsRecyclerView.setLayoutManager(layoutManager);
    binding.itemsRecyclerView.addItemDecoration(new SimpleDividerDecoration(this));

    //        DividerItemDecoration decor = new DividerItemDecoration(this, layoutManager.getOrientation());
    //        decor.setDrawable(getResources().getDrawable(R.drawable.feed_divider));
    //binding.itemsRecyclerView.addItemDecoration(decor);

    adapter.setCallback(this);
    adapter.setItemClickListener(memberItemClickListener);
    binding.itemsRecyclerView.setAdapter(adapter);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    presenter.detachView();
  }

  @Override public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_finish, menu);

    finishMenuItem = menu.findItem(R.id.action_finish);
    presenter.onCreateOptionsMenu();
    return true;
  }

  @Override public void setFinishMenuItemVisibility(boolean visible) {
    finishMenuItem.setVisible(visible);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_finish) {
      presenter.onFinishClick(signUp);
    }

    return super.onOptionsItemSelected(item);
  }

  private FamilyMemberComponent createComponent() {
    return DaggerFamilyMemberComponent.builder().sessionComponent(getSessionComponent()).familyMemberModule(new FamilyMemberModule()).build();
  }

  @Override public void showError(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }

  @Override public void addNewMember(FamilyMember familyMember) {
    AddMemberDialog fragment = AddMemberDialog.newInstance(familyMember);

    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.add(fragment, null);
    ft.commitAllowingStateLoss();
  }

  public void onAddMemberClick(View view) {
    presenter.onAddMemberClick();
  }

  public void onAddMemberDialogResult(int resultCode) {
    if (resultCode == RESULT_OK) {
      presenter.updateMemberList();
      hideNoMembersView();
    }
  }

  @Override
  public void showLoadingIndicator() {
    showThrobber();
  }

  @Override
  public void hideLoadingIndicator() {
    hideThrobber();
  }

  @Override
  public void showErrorDialog(boolean hasNoInternet) {
    if(hasNoInternet){
      showErrorDialog(getResources().getString(R.string.alert_message_title),getResources().getString(R.string.connection_problem_message),
              getResources().getString(R.string.connection_problem_description));
    }else{
      showErrorDialog(getResources().getString(R.string.other_error_title),getResources().getString(R.string.other_error_message),
              getResources().getString(R.string.other_error_descr));
    }

  }

  @Override public void showItems(List<FamilyMember> items) {
    adapter.setItems(items);
    adapter.notifyDataSetChanged();
    finishMenuItem.setVisible(items.size() > 0);
  }

  @Override public void clearItems() {
    adapter.clear();
    finishMenuItem.setVisible(false);
  }

  @Override public void showMessage(String text) {
    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
  }

  @Override public void onItemDelete(int position) {
    presenter.deleteItem(position,this);
  }

  @Override public void showPickUserUi() {
    PickUserActivity.start(this);
  }

  @Override public void closeUi() {
    onBackPressed();
  }

  @Override public void showConfirmDeleteDialog(final FamilyMemberEvent event) {
    new AlertDialog.Builder(this, R.style.DialogTheme).setMessage(getString(R.string.confirm_delete_dialog))
        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            presenter.deleteItem(event.getPosition(),FamilyMemberActivity.this);
          }
        })
        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

          @Override public void onClick(DialogInterface paramDialogInterface, int paramInt) {
            // TODO Auto-generated method stub

          }
        })
        .show();
  }

  @Override public void hideNoMembersView() {
    binding.familyMemberTitle.setVisibility(View.GONE);
    binding.familyMemberImage.setVisibility(View.GONE);
    binding.warningNoticeTextView.setVisibility(View.VISIBLE);
  }

  @Override public void showNoMembersView() {
    binding.familyMemberTitle.setVisibility(View.VISIBLE);
    binding.familyMemberImage.setVisibility(View.VISIBLE);
    binding.warningNoticeTextView.setVisibility(View.GONE);
  }
}
