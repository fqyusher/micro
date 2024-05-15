package com.yh.micro.service;

import org.springframework.cloud.gateway.route.RouteDefinition;

import java.util.List;

/**
 * @author yusher
 * @date 4/23/24 3:17 PM
 */
public interface RouteService {
    void update(RouteDefinition routeDefinition);

    void add(RouteDefinition routeDefinition);

    void delete(String id);

    void batchAdd(List<RouteDefinition> routeDefinitions);

    void clear();
}
