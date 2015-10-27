package com.mtvindia.connect.util.social;

/**
 * Created by Sibi on 26/10/15.
 */
public interface SocialAuthCallback {

    void onSuccess(AuthResult result);

    void onCancel();

    void onError(Throwable throwable);

}
