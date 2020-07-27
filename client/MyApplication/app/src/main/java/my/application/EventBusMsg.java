package my.application;

public class EventBusMsg {

    public static class ChatActivityStart {
        private int isChatroom;
        private String nameOfChatroomOrFri;

        public ChatActivityStart(int isChatroom, String nameOfChatroomOrFri){
            this.isChatroom = isChatroom;
            this.nameOfChatroomOrFri = nameOfChatroomOrFri;
        }

        public String getNameOfChatroomOrFri() { return this.nameOfChatroomOrFri; }
        public int getIsChatroom() { return this.isChatroom; }
    }

    public static class ChatActivityEnd {
        public ChatActivityEnd() { }
    }

    public static class PushMsgToChat {
        private String sendName;
        private String message;
        private String messageTime;

        public PushMsgToChat(String sendName, String message, String messageTime) {
            this.sendName = sendName;
            this.message = message;
            this.messageTime = messageTime;
        }

        public String getSendName() { return this.sendName; }
        public String getMessage() { return this.message; }
        public String getMessageTime() { return this.messageTime; }
    }

}
