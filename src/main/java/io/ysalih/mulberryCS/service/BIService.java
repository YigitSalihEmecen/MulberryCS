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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BIService {

    private static final String PROMPT_CONVERSATION_HISTORY_INSTRUCTIONS = """        
        The object `conversational_history` below represents the past interaction between the user and you (the LLM).
        Each `history_entry` is represented as pair of `prompt` and `response`.
        `prompt` is a past user prompt and `response` was your response for that `prompt`.
                
        Use the information in `conversational_history` if you need to recall things from the conversation
        , or in other words, if the `user_main_prompt` needs any information from past `prompt` or `response`.
        If you don't need the `conversational_history` information, simply respond the prompt with your built-in knowledge.
                
        `conversational_history`:
                
        """;

    private static final String CURRENT_PROMPT_INSTRUCTIONS = """
                
        Here's the `user_main_prompt`:
                
                
        """;

    private static final String CURRENT_REAL_ESTATE_PROMPT = """
                
        Here is the currently available real estate properties: Analyze the user's preferences and provide the most suitable real estate property.
        
        `real_estate_properties`:
        
        """;


    @Qualifier("ollamaChatModel")
    private final OllamaChatModel ollamaChatClient;
    private final RealestateRepository repository;
    private final HistoryRepository historyRepository;


    public BIService(OllamaChatModel ollamaChatClient, RealestateRepository repository, HistoryRepository historyRepository) {
        this.ollamaChatClient = ollamaChatClient;
        this.repository = repository;
        this.historyRepository = historyRepository;
    }

    public String call(String userMessage, String historyId) {
        var currentHistory = historyRepository.getHistory(historyId);

        var historyPrompt = new StringBuilder(PROMPT_CONVERSATION_HISTORY_INSTRUCTIONS);
        currentHistory.forEach(entry -> historyPrompt.append(entry.toString()));

        var contextSystemMessage = new SystemMessage(historyPrompt.toString());
        var generalInstructionsSystemMessage = new SystemMessage(PROMPT_GENERAL_INSTRUCTIONS);
        var currentPromptMessage = new UserMessage(CURRENT_PROMPT_INSTRUCTIONS.concat(userMessage));
//        var askQuestionSystemMessage = new SystemMessage(PROMPT_CONVERSATION_HISTORY_INSTRUCTIONS);
//        var realEstatePromptMessage = new UserMessage(CURRENT_REAL_ESTATE_PROMPT.concat(getRealEstateProperties()));

        var prompt = new Prompt(List.of(generalInstructionsSystemMessage, contextSystemMessage, currentPromptMessage));
        var response = ollamaChatClient.call(prompt)
                .getResult()
                .getOutput()
                .getContent();
        var contextHistoryEntry = new HistoryEntry(userMessage, response);
        currentHistory.add(contextHistoryEntry);

        historyRepository.saveHistory(historyId, currentHistory);
        return response;
    }

    private String getRealEstateProperties() {
        StringBuilder sb = new StringBuilder();
        repository.findAll().stream().map(Realestate::toString).forEach(sb::append);
        return sb.toString();
    }

    private final String PROMPT_GENERAL_INSTRUCTIONS = """
                You are an AI assistant that asks questions to users to get as much information as possible about the user's preferences about the real estate.
                
                This is the json format of the information you are trying to gather.
                {
                    "price": <Only numbers. No text.>
                    "type": <One of 'flat, apartment, building, land>,
                    "status": <One of 'for sale, for rent'>
                    "sqm": <Only numbers. No text.>,
                    "city": <Text only.>,
                }
                
                Keep your responses really short.
                Avoid asking same type of questions twice.
                
                Here Are some examples:
                
                Question: Can you tell me about your budget?
                Answer: around 100k
                
                Question: What is the type of the property?
                Answer: I am looking for a flat.
                
                Question: Do you have preferences about build date?
                Answer: Yes, I would like to look at the buildings that have been built after 2015.
                
                
                Do not interact in any other forms other than asking questions about the preferences about the real estate.
                Respond to requests that are not related to your purpose with 'I am not allowed to answer that'.
                """;
}
