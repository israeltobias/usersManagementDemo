package es.users.context;

import es.users.dto.UserRequest;

public class UserRequestHolder {

    private static final ThreadLocal<UserRequest> holder = new ThreadLocal<>();

    private UserRequestHolder() {
        super();
    }


    public static void set(UserRequest request) {
        holder.set(request);
    }


    public static UserRequest get() {
        return holder.get();
    }


    public static void clear() {
        holder.remove();
    }
}
