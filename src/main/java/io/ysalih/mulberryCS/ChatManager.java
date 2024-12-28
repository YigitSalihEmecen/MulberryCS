package io.ysalih.mulberryCS;

import io.ysalih.mulberryCS.model.HistoryEntry;
import io.ysalih.mulberryCS.service.BIService;
import io.ysalih.mulberryCS.service.GCSService;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class ChatManager {

    private final BIService biService;
    private final GCSService gcsService;
    private final Pattern switchPattern;
    private String state = "gcs";

    public ChatManager(BIService biService, GCSService gcsService) {
        this.biService = biService;
        this.gcsService = gcsService;

        switchPattern = Pattern.compile("switch", Pattern.CASE_INSENSITIVE);
    }

    public String chat(String userMessage, String historyId) {
        if (Objects.equals(state, "gcs")) {
            String gcsResponse = gcsService.call(userMessage, historyId);
            if (Objects.equals(gcsResponse, "bi") || switchPattern.matcher(gcsResponse).find()) {
                state = "bi";
                return biService.call(userMessage, historyId);
            }
            return gcsResponse;
        }

        return biService.call(userMessage, historyId);
    }
}
