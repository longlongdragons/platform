package org.example.platformspringboot.utils;

/** 线程级当前用户 */
public class UserContext {
    private static final ThreadLocal<Long> UID = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();

    public static void set(long id, String username) {
        UID.set(id); USERNAME.set(username);
    }
    public static Long getUserId()       { return UID.get(); }
    public static String getUsername()   { return USERNAME.get(); }
    public static void clear()           { UID.remove(); USERNAME.remove(); }
}
