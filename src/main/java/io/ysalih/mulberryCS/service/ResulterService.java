package io.ysalih.mulberryCS.service;

import com.google.gson.Gson;
import io.ysalih.mulberryCS.model.HistoryEntry;
import io.ysalih.mulberryCS.model.Realestate;
import io.ysalih.mulberryCS.repository.RealestateRepository;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResulterService {

    private static final String CURRENT_PROMPT_INSTRUCTIONS = """
            
            Here's the `user_main_prompt`:
            
            
            """;


    @Qualifier("ollamaChatModel")
    private final OllamaChatModel ollamaChatClient;
    private final RealestateRepository realestateRepository;

    public ResulterService(OllamaChatModel ollamaChatClient, RealestateRepository realestateRepository) {
        this.ollamaChatClient = ollamaChatClient;
        this.realestateRepository = realestateRepository;
    }

    public List<Realestate> call(List<HistoryEntry> chatLog) {

        var generalInstructionsSystemMessage = new SystemMessage(PROMPT_GENERAL_INSTRUCTIONS);
        var currentPromptMessage = new UserMessage(CURRENT_PROMPT_INSTRUCTIONS.concat(logToString(chatLog)));

        var prompt = new Prompt(List.of(generalInstructionsSystemMessage, currentPromptMessage));
        var filterResponse = ollamaChatClient.call(prompt)
                .getResult()
                .getOutput()
                .getContent();

        Gson gson = new Gson();
        var filter = gson.fromJson(filterResponse, RealEstateFilter.class);

        return realestateRepository.findByFilter(
                filter.getPrice(),
                filter.getType(),
                filter.getCity(),
                filter.getStatus()
        );

    }

    private String logToString(List<HistoryEntry> chatLog) {
        var sb = new StringBuilder();
        for (HistoryEntry entry : chatLog) {
            sb.append(entry.toString());
            sb.append("\n");
        }

        return sb.toString();
    }

    private final String PROMPT_GENERAL_INSTRUCTIONS = """
            You are an AI that gets a chat log as an input and outputs a formatted data.
            The chat log is between an AI and user.
            You need to analyze the chat that ıs gıven to you and try to find at least 1 property of the user's request.
            Here are the properties of the real estates: Name, Price, Type, Sqm, City, Type, YearBuilt
            
            
            Give me the output in json format and be strict about this format. If any of these fields are not provided, do not include that property:
            {
                "price": <user's price preference according to the chat log. Only numbers. No text.>
                "type": <user's type preference according to the chat log you have. One of 'flat, apartment, building, land>,
                "status": <user's status preferences according to the chat log you have. One of 'for sale, for rent'>
                "sqm": <user's sqm preference according to chat log you have. Only numbers. No text.>,
                "city": <user's city preference according to chat log you have. Text only.>,
            }
            
            
            
            ...
            
            You can add up to 4 lines of information.
            
            You can't output anything other than this formatted output.
            Do not answer any questions or interact with the user.
            Do not try to output anything other than this formatted output.
            Do not try to make any explanations or objections.
            
            """;
}

class RealEstateFilter {

    private Integer price;
    private String type;
    private String city;
    private String status;

    public RealEstateFilter() {
    }

    public RealEstateFilter(Integer price, String type, String city, String status) {
        this.price = price;
        this.type = type;
        this.city = city;
        this.status = status;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}