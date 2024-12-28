package io.ysalih.mulberryCS.model;

public class MessageJson {

    public String messageBody;

    public MessageJson(String messageBody) {
        this.messageBody = messageBody;
    }

    public MessageJson() {
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }
}
