package com.mtvindia.connect.app.di;

import android.content.Context;

import com.mtvindia.connect.app.Config;
import com.mtvindia.connect.data.repository.ChatListRepository;
import com.mtvindia.connect.data.repository.ChatMessageRepository;
import com.mtvindia.connect.data.repository.realm.ChatListRepositoryRealm;
import com.mtvindia.connect.data.repository.realm.ChatMessageRepositoryRealm;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Provides all presenter class dependencies.
 *
 * @author Farhan Ali
 */
@Module(
        complete = false,
        library = true
)
public class OrmModule {

    @Provides
    @Singleton
    public Realm provideRealm(Context context) {
        RealmConfiguration config = new RealmConfiguration.Builder(context)
                .name(Config.DB_NAME)
                .schemaVersion(Config.DB_VERSION)
                .build();

        return Realm.getInstance(config);
    }

    @Provides
    @Singleton
    public ChatListRepository provideChatList() {
        return new ChatListRepositoryRealm();
    }

    @Provides
    @Singleton
    public ChatMessageRepository provideChatMessage() {
        return new ChatMessageRepositoryRealm();
    }

}
