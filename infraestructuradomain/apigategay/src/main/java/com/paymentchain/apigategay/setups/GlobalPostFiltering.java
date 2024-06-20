package com.paymentchain.apigategay.setups;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class GlobalPostFiltering {
    
    @Bean
    public GlobalFilter customGlobalPostFilter() {
        return (exchange, chain) -> {
            return chain.filter(exchange)
                .then(Mono.fromRunnable(
                    () -> log.info("Global post filtering executed")
                )
            );
        };
    }
}
