package com.novp.sprytar.support;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.novp.sprytar.R;
import com.novp.sprytar.SprytarApplication;
import com.novp.sprytar.data.model.Faq;
import com.novp.sprytar.databinding.ActivityFaqBinding;
import com.novp.sprytar.databinding.ItemFaqActivityBinding;
import com.novp.sprytar.presentation.BaseActivity;
import com.novp.sprytar.presentation.BaseBindingAdapter;
import com.novp.sprytar.presentation.BaseBindingViewHolder;
import java.util.List;
import javax.inject.Inject;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class FaqActivity extends BaseActivity implements FaqActivityView {

  @Inject FaqActivityPresenter presenter;

  @Inject FaqAdapter adapter;

  private ActivityFaqBinding binding;
  private Tracker mTracker;

  public static void start(Context context) {
    Intent starter = new Intent(context, FaqActivity.class);
    context.startActivity(starter);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_faq);
    createFaqActivityComponent().inject(this);
    presenter.attachView(this);
    initUi();

    mTracker = ((SprytarApplication) getApplication()).getDefaultTracker();
    mTracker.setScreenName("FAQ Screen");
    mTracker.send(new HitBuilders.EventBuilder().build());
    mTracker.enableAutoActivityTracking(true);

    presenter.loadItems();
  }

  private void initUi() {

    setSupportActionBar(binding.toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getString(R.string.faq_title_short));

    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    binding.itemsRecyclerView.setLayoutManager(layoutManager);
    //        DividerItemDecoration decor = new DividerItemDecoration(this, layoutManager.getOrientation());
    //        decor.setDrawable(getResources().getDrawable(R.drawable.feed_divider));
    //binding.itemsRecyclerView.addItemDecoration(decor);
    binding.itemsRecyclerView.setAdapter(adapter);

    binding.swipeRefreshLayout.setEnabled(true);

    binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

    binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        presenter.loadItems();
      }
    });
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
    inflater.inflate(R.menu.menu_search, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  private FaqActivityComponent createFaqActivityComponent() {
    return DaggerFaqActivityComponent.builder().applicationComponent(getApplicationComponent()).build();
  }

  @Override public void showError(String message) {

  }

  @Override public void showLoadingIndicator() {
    binding.progressBar.setVisibility(VISIBLE);
  }

  @Override public void hideLoadingIndicator() {
    hideRefreshingIndicator();
    binding.progressBar.setVisibility(GONE);
  }

  private void hideRefreshingIndicator() {
    if (binding.swipeRefreshLayout.isRefreshing()) {
      binding.swipeRefreshLayout.setRefreshing(false);
    }
  }

  @Override public void showItems(List<Faq> items) {
    adapter.setItems(items);
    adapter.notifyDataSetChanged();
  }

  @Override public void clearItems() {

  }

  @Override public void showMessage(String text) {

  }

  public static class FaqAdapter extends BaseBindingAdapter<Faq> {

    @Inject public FaqAdapter() {
    }

    @Override protected ViewDataBinding bind(LayoutInflater inflater, ViewGroup parent, int viewType) {
      return ItemFaqActivityBinding.inflate(inflater, parent, false);
    }

    @Override public void onBindViewHolder(BaseBindingViewHolder holder, int position) {
      ((ItemFaqActivityBinding) holder.binding).setFaq(items.get(position));
    }
  }
}
