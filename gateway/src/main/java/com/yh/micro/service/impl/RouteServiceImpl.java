package com.yh.micro.service.impl;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.yh.micro.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author yusher
 * @date 4/23/24 3:20 PM
 */
@Service
public class RouteServiceImpl implements RouteService, ApplicationEventPublisherAware {
    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;

    @Autowired
    private RouteDefinitionLocator routeDefinitionLocator;

    @Override
    public void update(RouteDefinition routeDefinition) {
        this.routeDefinitionWriter.delete(Mono.just(routeDefinition.getId()));
        routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    @Override
    public void add(RouteDefinition routeDefinition) {
        routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    @Override
    public void delete(String id) {
        this.routeDefinitionWriter.delete(Mono.just(id)).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    @Override
    public void batchAdd(List<RouteDefinition> routeDefinitions) {
        if (routeDefinitions.isEmpty()) {
            return;
        }
        routeDefinitions.forEach(this::add);
    }

    @Override
    public void clear() {
        List<RouteDefinition> routeDefinitionsExits =  routeDefinitionLocator.getRouteDefinitions().buffer().blockFirst();
        if (!CollectionUtils.isEmpty(routeDefinitionsExits)) {
            routeDefinitionsExits.forEach(routeDefinition -> delete(routeDefinition.getId()));
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
