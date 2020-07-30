package my.application;

public class URLCollection {
    /**
     * all server's api is here
     * please use your own ip address
     */
    public static final String URL = "http://***.***.***.***/api";
    public static final String LOGIN = URL + "/login";
    public static final String REGISTER = URL + "/register";
    public static final String GET_FRI_LIST = URL + "/get_fri_list";
    public static final String GET_CHATROOM_LIST = URL + "/get_chatroom_list?name=";
    public static final String GET_CHATROOM_MESSAGES = URL + "/get_chatroom_messages";
    public static final String GET_PRIVATE_CHAT_MESSAGES = URL + "/get_private_chat_messages";
    public static final String POST_PRIVATE_CHAT_MESSAGE = URL + "/post_private_chat_message";
    public static final String POST_CHATROOM_MESSAGE = URL + "/post_chatroom_message";
    public static final String CREATE_CHATROOM = URL + "/create_chatroom";
    public static final String GET_MOMENTS = URL + "/get_moments";
    public static final String POST_MOMENT = URL + "/post_moment";
    public static final String DOWNLOAD_RESOURCE =  URL + "/download/";
    public static final String SOCKETIO = "http://***.***.***.***:8001";
}
