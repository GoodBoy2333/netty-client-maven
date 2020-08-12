package com.fy;

import com.fy.CustomClient.CustomClient;
import com.fy.echoclient.Echoclient;

/**
 * <p>
 *
 * </p >
 *
 * @author fangyan
 * @since 2020/8/9 16:24
 */
public class Client {
    public static void main(String[] args) {
        new Echoclient().bind(8080);
    }
}
