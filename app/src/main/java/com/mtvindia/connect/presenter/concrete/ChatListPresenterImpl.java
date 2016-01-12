package com.mtvindia.connect.presenter.concrete;

import com.mtvindia.connect.app.base.BaseNetworkPresenter;
import com.mtvindia.connect.data.api.ApiObserver;
import com.mtvindia.connect.data.api.MtvConnectApi;
import com.mtvindia.connect.data.model.ChatList;
import com.mtvindia.connect.data.model.ChatListResponse;
import com.mtvindia.connect.data.repository.ChatListRepository;
import com.mtvindia.connect.presenter.ChatListPresenter;
import com.mtvindia.connect.util.UserPreference;

import java.util.List;

import javax.inject.Inject;

import retrofit.client.Response;
import rx.Observable;
import timber.log.Timber;

/**
 * Created by Sibi on 24/12/15.
 */
public class ChatListPresenterImpl extends BaseNetworkPresenter implements ChatListPresenter {

    @Inject MtvConnectApi mtvConnectApi;
    @Inject UserPreference userPreference;
    @Inject ChatListRepository chatListRepository;

    public ChatListPresenterImpl() {
        injectDependencies();
    }

    private ApiObserver<Response> addUserResponse = new ApiObserver<Response>() {
        @Override
        public void onResult(Response result) {
            Timber.d("Added to server");
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }
    };

    private ApiObserver<Response> deleteUserResponse = new ApiObserver<Response>() {
        @Override
        public void onResult(Response result) {
            Timber.d("Deleted User from Server");
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }
    };

    private ApiObserver<List<ChatListResponse>> getListObservable = new ApiObserver<List<ChatListResponse>>() {
        @Override
        public void onResult(List<ChatListResponse> result) {
            ChatList chatList;
            for (ChatListResponse response : result) {
                chatList = chatListRepository.find(response.getUserId(), userPreference.readUser().getId());

                if (chatList == null) {
                    chatList = new ChatList();
                    chatList.setName(response.getFullName());
                    chatList.setImage(response.getProfilePic());
                    chatList.setUserId(response.getUserId());
                    chatList.setLogedinUser(userPreference.readUser().getId());
                    chatList.setLastMessage("");
                    chatList.setTime("");
                    chatListRepository.save(chatList);
                }
            }
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }
    };


    @Override
    public void addUser(int id, int chatUserId, String header) {
        Observable<Response> responseObservable = mtvConnectApi.addUser(id, chatUserId, header);
        subscribeForNetwork(responseObservable, addUserResponse);
    }

    @Override
    public void removeUser(int chatUserId, String header) {
        Observable<Response> responseObservable = mtvConnectApi.removeUser(chatUserId, header);
        subscribeForNetwork(responseObservable, deleteUserResponse);
    }

    @Override
    public void getChatUsers(String header) {
        Observable<List<ChatListResponse>> resultObservable = mtvConnectApi.getChatList(header);

        subscribeForNetwork(resultObservable, getListObservable);
    }
}
