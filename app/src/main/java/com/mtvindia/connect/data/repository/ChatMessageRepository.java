package com.mtvindia.connect.data.repository;

import com.mtvindia.connect.data.model.ChatMessage;

import java.util.List;

/**
 * Created by Sibi on 02/12/15.
 */
public interface ChatMessageRepository extends BaseRepository<ChatMessage> {

    List<ChatMessage> searchMessage(String userId);

}

