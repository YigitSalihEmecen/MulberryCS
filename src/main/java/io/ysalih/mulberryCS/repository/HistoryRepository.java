package io.ysalih.mulberryCS.repository;

import io.ysalih.mulberryCS.model.HistoryEntry;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class HistoryRepository {
    private final Map<String, ArrayList<HistoryEntry>> conversationalHistoryStorage = new HashMap<>();

    public void saveHistory(String conversationId, ArrayList<HistoryEntry> history) {
        conversationalHistoryStorage.put(conversationId, history);
    }

    public ArrayList<HistoryEntry> getHistory(String conversationId) {
        return conversationalHistoryStorage.computeIfAbsent(conversationId, _ -> new ArrayList<>());
    }
}
