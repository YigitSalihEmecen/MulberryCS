package io.ysalih.mulberryCS;

import io.ysalih.mulberryCS.model.AIModels;
import io.ysalih.mulberryCS.model.HistoryEntry;
import io.ysalih.mulberryCS.service.BIService;
import io.ysalih.mulberryCS.service.GCSService;
import io.ysalih.mulberryCS.service.ListingService;
import io.ysalih.mulberryCS.service.ResulterService;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class ChatManager {

    private final BIService biService;
    private final GCSService gcsService;
    private final Pattern switchPattern;
    private final Map<String, AIModels> states;
    private final Map<String, Integer> biCounts;
    private final Map<String, List<HistoryEntry>> biChats;
    private final ResulterService resulterService;
    private final ListingService listingService;
    private String state = "gcs";

    public ChatManager(BIService biService, GCSService gcsService, ResulterService resulterService, ListingService listingService) {
        this.biService = biService;
        this.gcsService = gcsService;
        this.resulterService = resulterService;
        this.listingService = listingService;

        switchPattern = Pattern.compile("switch", Pattern.CASE_INSENSITIVE);
        states = new HashMap<>();
        biCounts = new HashMap<>();
        biChats = new HashMap<>();
    }

    public String chat(String userMessage, String conversationId) {
        var currentState = states.computeIfAbsent(conversationId, _ -> AIModels.CS);

        var response = "";

        switch (currentState) {
            case BI -> {
                response = biService.call(userMessage, conversationId);
                String r = response;

                biCounts.compute(conversationId, (_, value) -> value + 1);
                biChats.get(conversationId).add(new HistoryEntry(userMessage, r));
                biChats.computeIfPresent(conversationId, (convId, _) -> biChats.put(convId, biChats.get(conversationId)));
            }
            default -> response = gcsService.call(userMessage, conversationId);
        }

        // I'm on CS and Got 'switch to BI'
        if (currentState == AIModels.CS && switchPattern.matcher(response).find()) {
            currentState = AIModels.BI;
            states.put(conversationId, currentState);

            // Specify the prompt
            var tempPrompt = "Let's continue with asking questions.";

            // This response is just to ask llm questions to user.
            response = biService.call(tempPrompt, conversationId);
            biCounts.put(conversationId, 0);

            var list = new ArrayList<HistoryEntry>();
            list.add(new HistoryEntry(tempPrompt, response));

            biChats.put(conversationId, list);
        }

        if (currentState == AIModels.BI && biCounts.get(conversationId) == 3) {
            var realEstates = resulterService.call(biChats.get(conversationId));
            response = listingService.call(realEstates, conversationId);
            currentState = AIModels.CS;
            states.put(conversationId, currentState);
        }

        System.out.println(currentState);
        return response;
    }

}



