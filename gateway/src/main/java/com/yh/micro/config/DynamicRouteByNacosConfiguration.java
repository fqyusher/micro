package com.yh.micro.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yh.micro.property.GatewayProperty;
import com.yh.micro.service.RouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * @author yusher
 * @date 4/22/24 3:23 PM
 */
@Configuration
@Slf4j
@RefreshScope
public class DynamicRouteByNacosConfiguration {
    @Autowired
    private RouteService routeService;

    @Autowired
    private GatewayProperty gatewayProperty;

    @Autowired
    private ObjectMapper objectMapper;

//    private ConfigService configService;

    @PostConstruct
    public void init() throws NacosException {
        Properties properties = new Properties();
        properties.setProperty("serverAddr", gatewayProperty.getServerAddr());
        properties.setProperty("namespace", gatewayProperty.getNamespace());
        ConfigService configService = NacosFactory.createConfigService(properties);
        String configInfo = configService.getConfigAndSignListener(gatewayProperty.getDataId(), gatewayProperty.getGroup(), 3000L, new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                log.info("网关路由配置更新: \n{}", configInfo);
                refreshRoutes(configInfo);
            }
        });
        // initialize
        log.info("网关路由配置初始化:\n {}", configInfo);
        refreshRoutes(configInfo);
        // add listener
//        configService.addListener(gatewayProperty.getDataId(), gatewayProperty.getGroup(), new Listener()  {
//            @Override
//            public void receiveConfigInfo(String configInfo) {
//                log.info("网关路由配置更新: \n{}", configInfo);
//                dealRoutes(configInfo);
//            }
//            @Override
//            public Executor getExecutor() {
//                return null;
//            }
//        });

//        log.info("获取网关当前配置:\r\n{}",configInfo);
//        List<RouteDefinition> definitionList = new ArrayList<>();
//        try {
//            TypeReference<List<RouteDefinition>> listRef = new TypeReference<List<RouteDefinition>>() {};
//            definitionList = objectMapper.readValue(configInfo, listRef);
//        } catch (JsonProcessingException e) {
//            log.error("json processing error:{}", e.getMessage());
//        }
//        routeService.multiUpdate(definitionList);
//        for(RouteDefinition definition : definitionList){
//            log.info("update route : {}",definition.toString());
//            routeService.add(definition);
//        }
//        try {
//            configService = initConfigService();
//            String initConfigInfo = configService.getConfigAndSignListener(gatewayProperty.getDataId(),
//                gatewayProperty.getGroup(), 3000L, new Listener() {
//                    @Override
//                    public Executor getExecutor() {
//                        return null;
//                    }
//
//                    @Override
//                    public void receiveConfigInfo(String configInfo) {
//                        if (StringUtils.isNotEmpty(configInfo)) {
//                            log.info("接收到网关路由更新配置：\r\n{}", configInfo);
//                            List<RouteDefinition> routeDefinitions = null;
//                            try {
//                                routeDefinitions = objectMapper.readValue(configInfo,
//                                        new TypeReference<List<RouteDefinition>>() {
//                                        });
//                            } catch (JsonProcessingException e) {
//                                log.error("解析路由配置出错，" + e.getMessage(), e);
//                            }
//                            for (RouteDefinition definition : Objects.requireNonNull(routeDefinitions)) {
//                                routeService.update(definition);
//                            }
//                        } else {
//                            log.warn("当前网关无动态路由相关配置");
//                        }
//                    }
//                });
//            log.info("获取网关当前动态路由配置:\r\n{}", initConfigInfo);
//            if (StringUtils.isNotEmpty(initConfigInfo)) {
//                List<RouteDefinition> routeDefinitions = objectMapper.readValue(initConfigInfo,
//                        new TypeReference<List<RouteDefinition>>() {});
//                for (RouteDefinition definition : routeDefinitions) {
//                    routeService.add(definition);
//                }
//            } else {
//                log.warn("当前网关无动态路由相关配置");
//            }
//            log.info("结束网关动态路由初始化...");
//        } catch (Exception e) {
//            log.error("初始化网关路由时发生错误", e);
//        }
//        try {
//            String configInfo = configService.getConfig(gatewayProperty.getDataId(), gatewayProperty.getGroup(), 3000L);
//            log.info("获取网关当前配置:\r\n{}",configInfo);
//            TypeReference<List<RouteDefinition>> listRef = new TypeReference<List<RouteDefinition>>() {};
//            List<RouteDefinition> definitionList = objectMapper.readValue(configInfo, listRef);
//            for(RouteDefinition definition : definitionList){
//                log.info("update route : {}",definition.toString());
//                routeService.add(definition);
//            }
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//        routesListener(gatewayProperty.getDataId(), gatewayProperty.getGroup());
    }

    private void refreshRoutes(String configInfo) {
        List<RouteDefinition> routeDefinitions = new ArrayList<>();
        try {
            TypeReference<List<RouteDefinition>> listRef = new TypeReference<List<RouteDefinition>>() {};
            routeDefinitions = objectMapper.readValue(configInfo, listRef);
        } catch (JsonProcessingException e) {
            log.error("json processing error:{}", e.getMessage());
        }
        routeService.clear();
        routeService.batchAdd(routeDefinitions);
//        routeDefinitions.forEach(v -> routeService.add(v));
//        routeService.batchAdd(routeDefinitions);
    }

//    public void routesListener(String dataId, String group){
//        try {
//            configService.addListener(dataId, group, new Listener()  {
//                @Override
//                public void receiveConfigInfo(String configInfo) {
//                    log.info("进行网关更新:\n\r{}",configInfo);
//                    TypeReference<List<RouteDefinition>> listRef = new TypeReference<List<RouteDefinition>>() {};
//                    List<RouteDefinition> definitionList = null;
//                    try {
//                        definitionList = objectMapper.readValue(configInfo, listRef);
//                    } catch (JsonProcessingException e) {
//                        throw new RuntimeException(e);
//                    }
////                    List<RouteDefinition> definitionList = JSON.parseArray(configInfo, RouteDefinition.class);
//                    log.info("update route : {}", definitionList.toString());
//                    routeService.batchAdd(definitionList);
//                }
//                @Override
//                public Executor getExecutor() {
//                    log.info("getExecutor\n\r");
//                    return null;
//                }
//            });
//        } catch (NacosException e) {
//            log.error("从nacos接收动态路由配置出错!!!",e);
//        }
//    }
}
