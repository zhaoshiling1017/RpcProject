package com.ducetech;

/**
 * Created by lenzhao on 17-2-20.
 */
public class RpcServerTest {

    public static void main(String[] args) {
        try {
            RpcExporter.exporter("localhost", 8088);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
