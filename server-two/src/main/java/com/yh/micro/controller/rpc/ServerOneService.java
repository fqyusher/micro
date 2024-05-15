package com.yh.micro.controller.rpc;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author yusher
 * @date 4/25/24 5:01 PM
 */
@FeignClient(value = "server-one")
public interface ServerOneService {
    @GetMapping(value = "/server-one/test")
    String test();
}
