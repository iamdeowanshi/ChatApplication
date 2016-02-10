package com.mtvindia.connect.presenter;

import com.mtvindia.connect.app.base.Presenter;

/**
 * @author Aaditya Deowanshi
 */

public interface FindMatchPresenter extends Presenter<FindMatchViewInteractor> {

    void findMatches(String header);

}
