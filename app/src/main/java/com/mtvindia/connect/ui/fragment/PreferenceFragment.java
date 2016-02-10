package com.mtvindia.connect.ui.fragment;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.base.BaseFragment;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.presenter.UpdatePresenter;
import com.mtvindia.connect.presenter.UpdateViewInteractor;
import com.mtvindia.connect.ui.activity.NavigationActivity;
import com.mtvindia.connect.data.model.NavigationItem;
import com.mtvindia.connect.util.DialogUtil;
import com.mtvindia.connect.util.UserPreference;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * @author Aaditya Deowanshi
 *
 *         Preference screen, where user can set his/her prefernce i.e. whom they would like to meet.
 */

public class PreferenceFragment extends BaseFragment implements UpdateViewInteractor {

    @Inject UpdatePresenter presenter;
    @Inject UserPreference userPreference;
    @Inject DialogUtil dialogUtil;

    @Bind(R.id.txt_interested) TextView textInterested;
    @Bind(R.id.txt_meet) TextView textMeet;
    @Bind(R.id.progress) ProgressBar progress;

    private User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.preference_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.darkRed));
        }

        presenter.setViewInteractor(this);
        user = userPreference.readUser();
        loadPreferences();
    }

    @Override
    public void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progress.setVisibility(View.GONE);
    }

    @Override
    public void updateDone(User user) {
        userPreference.saveUser(user);
        toastShort("Saved");

        if (userPreference.readLoginStatus()) {
            NavigationActivity navigationActivity = (NavigationActivity) getContext();
            Fragment fragment = ProfileFragment.getInstance(null);
            navigationActivity.addFragment(fragment);

            NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
            navigationDrawerFragment.onItemSelected(NavigationItem.PROFILE);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        Timber.e(throwable, "Error");
        toastShort("Error: " + throwable);
        hideProgress();
    }

    @OnClick(R.id.btn_save)
    void onSave() {
        user.setInterestedIn((String) textInterested.getText());
        user.setLikeToMeet((String) textMeet.getText());
        if (isDataValid()) {
            presenter.update(user);
        }
    }

    @OnClick(R.id.layout_dialog_interested)
    void onLayoutInterestedClick() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(" Pick interested in:");
        builder.setIcon(R.drawable.icon_interested);
        builder.setItems(R.array.interested_array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] interested = getResources().getStringArray(R.array.interested_array);
                textInterested.setText(interested[which]);
            }
        });
        builder.show();
    }

    @OnClick(R.id.layout_dialog_meet)
    void onLayoutMeetClick() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(" Meet :");
        builder.setIcon(R.drawable.icon_meet);
        builder.setItems(R.array.meet_array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] meet = getResources().getStringArray(R.array.meet_array);
                textMeet.setText(meet[which]);
            }
        });
        builder.show();
    }

    /**
     * Validating preferences.
     *
     * @return
     */
    boolean isDataValid() {
        if (textInterested == null || textInterested.getText().equals("")) {
            return validateDialogInterested();
        } else if (textMeet == null || textMeet.getText().equals("")) {
            return validateDialogMeet();
        }

        return true;
    }

    /**
     * Validation for user like to meet dialog.
     *
     * @return
     */
    boolean validateDialogMeet() {
        final android.app.AlertDialog alertDialog = (android.app.AlertDialog) dialogUtil.createAlertDialog(getActivity(), "Select Like to Meet", "No Value Selected", "Ok", "");
        alertDialog.show();
        Button positiveButton = (Button) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        return false;
    }

    /**
     * Validation for user interested dialog.
     *
     * @return
     */
    boolean validateDialogInterested() {
        final android.app.AlertDialog alertDialog = (android.app.AlertDialog) dialogUtil.createAlertDialog(getActivity(), "Select Interested In", "No Value Selected", "Ok", "");
        alertDialog.show();
        Button positiveButton = (Button) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        return false;
    }

    /**
     * Method to set user preferences.
     */
    private void loadPreferences() {
        if (!userPreference.readLoginStatus()) {
            textMeet.setText(user.getLikeToMeet());
            textInterested.setText(user.getInterestedIn());
        }
    }

    public static Fragment getInstance(Bundle bundle) {
        PreferenceFragment preferenceFragment = new PreferenceFragment();
        preferenceFragment.setArguments(bundle);

        return preferenceFragment;
    }

}
