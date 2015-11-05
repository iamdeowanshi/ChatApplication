package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.Presenter;

/**
 * Created by Sibi on 05/11/15.
 */
public interface FindMatchPresenter extends Presenter<FindMatchViewInteractor> {

    void findMatches(String header);

}
