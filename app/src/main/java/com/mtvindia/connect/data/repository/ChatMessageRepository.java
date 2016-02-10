package com.mtvindia.connect.data.repository;

import com.mtvindia.connect.data.model.ChatMessage;

import java.util.List;

/**
 * @author Aaditya Deowanshi
 */

public interface ChatMessageRepository extends BaseRepository<ChatMessage> {

    List<ChatMessage> searchMessage(String from, String to);

    void removeAllMessage(long from, int to);

    List<ChatMessage> unsentMessages();

}

