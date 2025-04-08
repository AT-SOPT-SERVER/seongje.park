package org.sopt.util;

public class PostIdUtil {

    private static Long sequence = 0L;

    public static Long createId(){
        return ++sequence;
    }

}
