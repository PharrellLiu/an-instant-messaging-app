package my.application;

public class URLCollection {
    public static final String URL = "http://192.168.0.102:5000/api";
    public static final String LOGIN = URL + "/login";
    public static final String REGISTER = URL + "/register";
    public static final String GET_FRI_LIST = URL + "/get_fri_list";
    public static final String GET_CHATROOM_LIST = URL + "/get_chatroom_list?name=";
    public static final String GET_CHATROOM_MESSAGES = URL + "/get_chatroom_messages?page=";
    public static final String GET_PRIVATE_CHAT_MESSAGES = URL + "/get_private_chat_messages?page=";
    public static final String POST_PRIVATE_CHAT_MESSAGE = URL + "/post_private_chat_message";
    public static final String POST_CHATROOM_MESSAGE = URL + "/post_chatroom_message";
}
