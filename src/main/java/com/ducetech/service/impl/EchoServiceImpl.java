package com.ducetech.service.impl;

import com.ducetech.service.EchoService;

/**
 * Created by lenzhao on 17-2-20.
 */
public class EchoServiceImpl implements EchoService {

    @Override
    public String echo(String ping) {
        return ping != null ? ping + "--> I an ok." : "I am ok.";
    }
}
