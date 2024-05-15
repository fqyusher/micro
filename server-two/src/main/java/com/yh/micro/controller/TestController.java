package com.yh.micro.controller;

import com.yh.micro.controller.rpc.ServerOneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yusher
 * @date 4/22/24 11:46 AM
 */
@RestController
@RequestMapping(value = "/server-two")
public class TestController {
    @Autowired
    private ServerOneService serverOneService;

    @GetMapping(value = "/test")
    public String test() {
        return "server one result: " + serverOneService.test();
    }
}
