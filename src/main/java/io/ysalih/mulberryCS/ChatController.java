package io.ysalih.mulberryCS;

import io.ysalih.mulberryCS.model.MessageJson;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatManager chatService;

    public ChatController (ChatManager chatService){
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<MessageJson> sendUserMessage(@RequestBody MessageJson message){
        return ResponseEntity.ok(new MessageJson(chatService.chat(message.getMessageBody(), "1")));
    }

}

