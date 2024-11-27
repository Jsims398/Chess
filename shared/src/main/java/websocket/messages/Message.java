package websocket.messages;

public class Message extends ServerMessage{
    private final String message;

    public Message(String text){
        super(ServerMessageType.NOTIFICATION);
        this.message = text;
    }
    public String getMessageText(){
        return message;
    }
}
