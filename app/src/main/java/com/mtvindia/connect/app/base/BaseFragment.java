package com.mtvindia.connect.app.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.mtvindia.connect.app.di.Injector;

import net.danlew.android.joda.JodaTimeAndroid;

import butterknife.ButterKnife;

/**
 * Provides some basic operations, all fragments should extend this class.
 *
 * @author Farhan Ali
 */
public abstract class BaseFragment extends Fragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        JodaTimeAndroid.init(view.getContext());
        bindViews(view);
    }

    /**
     * Injects all of dependencies.
     */
    protected void injectDependencies() {
        Injector.instance().inject(this);
    }

    /**
     * Binds the viewInteractor objects within the fragment.
     *
     * @param view
     */
    protected void bindViews(View view) {
        ButterKnife.bind(this, view);
    }

    /**
     * Starts an activity with a bundle set to the intent.
     *  @param activityClass Class<? extends Activity>
     * @param bundle Bundle
     */
    protected void startActivity(Class activityClass, Bundle bundle) {
        Intent intent = new Intent(getActivity(), activityClass);

        if (bundle != null) intent.putExtras(bundle);

        startActivity(intent);
    }

    /**
     * Shows a LENGTH_SHORT toast message.
     *
     * @param message
     */
    protected void toastShort(String message) {
        toast(message, Toast.LENGTH_SHORT);
    }

    /**
     * Shows a LENGTH_LONG toast message.
     *
     * @param message
     */
    protected void toastLong(String message) {
        toast(message, Toast.LENGTH_LONG);
    }

    /**
     * Shows a toast message.
     *
     * @param message
     * @param length
     */
    protected void toast(String message, int length) {
        Toast.makeText(getActivity(), message, length).show();
    }

    /**
     * Return class name as TAG.
     *
     * @return String tag
     */
    public String tag() {
        return getClass().getName();
    }

}
