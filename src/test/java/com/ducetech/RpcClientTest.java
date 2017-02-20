package com.ducetech;

import com.ducetech.service.EchoService;
import com.ducetech.service.impl.EchoServiceImpl;

import java.net.InetSocketAddress;

/**
 * Created by lenzhao on 17-2-20.
 */
public class RpcClientTest {

    public static void main(String[] args) {
        RpcImporter<EchoService> importer = new RpcImporter<EchoService>();
        EchoService echo = importer.importer(EchoServiceImpl.class, new InetSocketAddress("localhost", 8088));
        System.out.println(echo.echo("Are you ok ?"));
    }
}
