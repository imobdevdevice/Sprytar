package com.sprytar.android.profile;

import android.app.AlertDialog;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.sprytar.android.R;
import com.sprytar.android.databinding.FragmentProfileBinding;
import com.sprytar.android.profile.DaggerProfileComponent;
import com.sprytar.android.data.SpSession;
import com.sprytar.android.data.model.ProfileEarnedBadges;
import com.sprytar.android.data.model.ProfilePlayedGame;
import com.sprytar.android.data.model.ProfileVisitedVenue;
import com.sprytar.android.presentation.BaseFragment;
import com.sprytar.android.presentation.BasePresenterPage;
import com.sprytar.android.util.ui.SimpleDividerDecoration;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ProfileFragment extends BaseFragment implements ProfileView, AdapterView.OnItemClickListener {

    private BadgeDialog.BadgeDialogListener badgeListener = new BadgeDialog.BadgeDialogListener() {
        @Override
        public void onClose() {
            badgeDialog.dismiss();
        }
    };

    @SuppressWarnings("WeakerAccess")
    @Inject
    ProfilePresenter presenter;

    @SuppressWarnings("WeakerAccess")
    @Inject
    SpSession spSession;

    ViewPagerAdapter adapter;
    VisitedVenuesAdapter visitedVenuesAdapter;
    EarnedBadgesAdapter earnedBadgesAdapter;
    PlayedGamesAdapter playedGamesAdapter;

    List<CharSequence> titleList;
    ProfileLocationPagePresenter presenterLocations;
    ProfileLocationPagePresenter presenterGames;
    ProfileLocationPagePresenter presenterBadges;

    int numLocations;
    int numGames;
    int numBadges;

    private AlertDialog badgeDialog = null;

    private FragmentProfileBinding binding;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createComponent().inject(this);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        getActivity().setTitle(R.string.profile);

        presenterLocations = new ProfileLocationPagePresenter(binding.locationsLayout);
        presenterGames = new ProfileLocationPagePresenter(binding.gamesLayout);
        presenterBadges = new ProfileLocationPagePresenter(binding.badgesLayout);

        visitedVenuesAdapter = new VisitedVenuesAdapter();
        binding.locationsLayout.gridview.setAdapter(visitedVenuesAdapter);

        earnedBadgesAdapter = new EarnedBadgesAdapter(getContext());
        binding.badgesLayout.gridview.setAdapter(earnedBadgesAdapter);
        binding.badgesLayout.gridview.setOnItemClickListener(this);

        playedGamesAdapter = new PlayedGamesAdapter(getContext());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        binding.gamesLayout.itemsRecyclerView.setLayoutManager(layoutManager);
        binding.gamesLayout.itemsRecyclerView.addItemDecoration(new SimpleDividerDecoration(getActivity()));
        binding.gamesLayout.itemsRecyclerView.setAdapter(playedGamesAdapter);

        initUi();

        return binding.getRoot();

    }
    private void initUi() {

        createTitles();

        ViewPager viewPager = binding.viewPager;
        viewPager.setOffscreenPageLimit(2);

        adapter = new ViewPagerAdapter();

        adapter.setData(titleList);
        adapter.addView(presenterLocations);
        adapter.addView(presenterGames);
        adapter.addView(presenterBadges);
        viewPager.setAdapter(adapter);

        binding.tabs.setupWithViewPager(viewPager);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(spSession.getAvatar())
                .setOldController(binding.avatarImageView.getController())
                .build();
        binding.avatarImageView.setController(controller);

        String name = "";
        if (spSession.isLoggedIn()) {
            name = spSession.getName();
        } else {
            name = getString(R.string.unregistered_user);
        }
        binding.nameTextView.setText(name);

    }

    private void createTitles() {

        SpannableStringBuilder spanLocations = new SpannableStringBuilder("Locations");
        spanLocations.append("\n");
        spanLocations.append(String.valueOf(numLocations));

        SpannableStringBuilder spanGames = new SpannableStringBuilder("Games");
        spanGames.append("\n");
        spanGames.append(String.valueOf(numGames));

        SpannableStringBuilder spanBadges = new SpannableStringBuilder("Badges");
        spanBadges.append("\n");
        spanBadges.append(String.valueOf(numBadges));

        titleList = new ArrayList<CharSequence>();
        titleList.add(spanLocations);
        titleList.add(spanGames);
        titleList.add(spanBadges);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this);
        presenter.loadData(getActivity());
    }

    @Override
    public void onRefreshClick() {
        super.onRefreshClick();
        presenter.loadData(getActivity());
    }

    private ProfileComponent createComponent() {
        return DaggerProfileComponent.builder()
                .sessionComponent(getSessionComponent())
                .profileModule(new ProfileModule())
                .build();
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
    public void showNetworkErrorDialog(boolean hasNoInternet) {
        if(hasNoInternet){
            showErrorDialog(getResources().getString(R.string.alert_message_title),getResources().getString(R.string.connection_problem_message),
                    getResources().getString(R.string.connection_problem_description));
        }else{
            showErrorDialog(getResources().getString(R.string.other_error_title),getResources().getString(R.string.other_error_message),
                    getResources().getString(R.string.other_error_descr));
        }

    }

    @Override
    public void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        presenter.detachView();
        presenter.onDestroyed();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateVisitedVenuesUi(List<ProfileVisitedVenue> items) {
        int tabIndex = 0;
        this.numLocations = items.size();
        createTitles();
        updateTabTitle(tabIndex);
        visitedVenuesAdapter.setItems(items);
        visitedVenuesAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateEarnedBadgesUi(List<ProfileEarnedBadges> items) {
        int tabIndex = 2;

        numBadges = items.size();
        createTitles();
        updateTabTitle(tabIndex);

        earnedBadgesAdapter.setItems(items);
        earnedBadgesAdapter.notifyDataSetChanged();
    }

    @Override
    public void updatePlayedGamesUi(List<ProfilePlayedGame> items) {
        int tabIndex = 1;

        numGames = items.size();
        createTitles();
        updateTabTitle(tabIndex);

        playedGamesAdapter.setItems(items);
        playedGamesAdapter.notifyDataSetChanged();

    }

    private void updateTabTitle(int tabIndex) {

        TabLayout.Tab tab = binding.tabs.getTabAt(tabIndex);

        if (tab != null) {
            tab.setText(titleList.get(tabIndex));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        badgeDialog = BadgeDialog.getDialog(getActivity(), badgeListener,earnedBadgesAdapter.getItem(position),-1);
        badgeDialog.show();
    }

    private class ViewPagerAdapter extends PagerAdapter {

        private final List<BasePresenterPage> mPresenterList = new ArrayList<>();
        private List<CharSequence> titles = new ArrayList<>();

        public void setData(List<CharSequence> titles) {
            this.titles = titles;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return mPresenterList.size();
        }

        public void addView(BasePresenterPage presenter) {
            mPresenterList.add(presenter);
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            ViewDataBinding currentView = mPresenterList.get(position).getView();
            return currentView.getRoot();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }

}
