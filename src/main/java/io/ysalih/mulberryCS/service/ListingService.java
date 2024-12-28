package io.ysalih.mulberryCS.service;

import io.ysalih.mulberryCS.model.HistoryEntry;
import io.ysalih.mulberryCS.model.Realestate;
import io.ysalih.mulberryCS.repository.HistoryRepository;
import io.ysalih.mulberryCS.repository.RealestateRepository;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListingService {

    private static final String PROMPT_CONVERSATION_HISTORY_INSTRUCTIONS = """        
        The object `conversational_history` below represents the past interaction between the user and you (the LLM).
        Each `history_entry` is represented as pair of `prompt` and `response`.
        `prompt` is a past user prompt and `response` was your response for that `prompt`.
                
        Use the information in `conversational_history` if you need to recall things from the conversation
        , or in other words, if the `user_main_prompt` needs any information from past `prompt` or `response`.
        If you don't need the `conversational_history` information, simply respond the prompt with your built-in knowledge.
                
        `conversational_history`:
                
        """;


    private static final String CURRENT_REAL_ESTATE_PROMPT = """
                
        Here is the currently available real estate properties: Analyze the user's preferences and provide the most suitable real estate property.
        You should use the history to shape your sentences to user.
        
        `real_estate_properties`:
        
        """;


    @Qualifier("ollamaChatModel")
    private final OllamaChatModel ollamaChatClient;
    private final HistoryRepository historyRepository;


    public ListingService(OllamaChatModel ollamaChatClient, HistoryRepository historyRepository) {
        this.ollamaChatClient = ollamaChatClient;
        this.historyRepository = historyRepository;
    }

    public String call(List<Realestate> realestates, String historyId) {
        var currentHistory = historyRepository.getHistory(historyId);

        var historyPrompt = new StringBuilder(PROMPT_CONVERSATION_HISTORY_INSTRUCTIONS);
        currentHistory.forEach(entry -> historyPrompt.append(entry.toString()));

        var contextSystemMessage = new SystemMessage(historyPrompt.toString());
        var realEstatesAsString = getRealEstateProperties(realestates);
        var currentPromptMessage = new UserMessage(CURRENT_REAL_ESTATE_PROMPT.concat(realEstatesAsString));

        var prompt = new Prompt(List.of(contextSystemMessage, currentPromptMessage));
        var response = ollamaChatClient.call(prompt)
                .getResult()
                .getOutput()
                .getContent();
        var contextHistoryEntry = new HistoryEntry(realEstatesAsString, response);
        currentHistory.add(contextHistoryEntry);

        historyRepository.saveHistory(historyId, currentHistory);
        return response;
    }

    private String getRealEstateProperties(List<Realestate> realestates) {
        StringBuilder sb = new StringBuilder();

        for (Realestate realestate : realestates) {
            sb.append(realestate.toString()).append("\n");
        }

        return sb.toString();
    }


}
