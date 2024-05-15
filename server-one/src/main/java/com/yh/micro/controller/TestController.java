package com.yh.micro.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author yusher
 * @date 4/22/24 11:48 AM
 */
@RestController
@RequestMapping(value = "/server-one")
public class TestController {
    @GetMapping(value = "/test")
    public String test(HttpServletRequest request){
        return "port=" + request.getServerPort() + ", time=" + LocalDateTime.now();
    }
}
