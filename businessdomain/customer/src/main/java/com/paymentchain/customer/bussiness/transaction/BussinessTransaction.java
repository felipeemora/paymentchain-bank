package com.paymentchain.customer.bussiness.transaction;

import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.paymentchain.customer.entities.CustomerEntity;
import com.paymentchain.customer.entities.CustomerProduct;
import com.paymentchain.customer.exception.BussinessRuleExcaption;
import com.paymentchain.customer.repositories.CustomerRepository;

import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@Service
public class BussinessTransaction {

    @Autowired
    CustomerRepository customerRepository;
    
    @Autowired
    private WebClient.Builder webClientBuilder;

    HttpClient client = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(EpollChannelOption.TCP_KEEPIDLE, 300)
            .option(EpollChannelOption.TCP_KEEPINTVL, 60)
            .responseTimeout(Duration.ofSeconds(1))
            .doOnConnected(httpClientConfig -> {
                httpClientConfig.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                httpClientConfig.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
            });


    public CustomerEntity saveCustomer(CustomerEntity input) throws BussinessRuleExcaption, UnknownHostException {
        if (!input.getProducts().isEmpty()) {
            for (CustomerProduct product : input.getProducts()) {
                String name = getProductName(product.getProductId());

                if (name.isBlank()) {
                    BussinessRuleExcaption bussinessRuleExcaption = new BussinessRuleExcaption(
                            "1025",
                            "Product id " + product.getProductId() + "does not exist",
                            HttpStatus.PRECONDITION_FAILED
                    );
                    throw bussinessRuleExcaption;
                } else {
                    product.setCustomer(input);
                }
            }
        }
        return customerRepository.save(input);
    }

    public CustomerEntity getCustomerByCode(String code) {
        CustomerEntity customer = customerRepository.findByCode(code);
        List<CustomerProduct> products = customer.getProducts();

        products.forEach(product -> {
            try {
                String productName = getProductName(product.getProductId());
                product.setProductName(productName);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        });

        // customer.setTransactions(getTransactions(customer.getIban()));
        return customer;
    }

    private String getProductName(long id) throws UnknownHostException {
        String productName = "";
        try {
            WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                    .baseUrl("http://BUSSINESSDOMAIN-PRODUCTS/product")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();
            
            JsonNode block = build.method(HttpMethod.GET).uri("/" + id)
                    .retrieve().bodyToMono(JsonNode.class).block();
    
            if (block != null && block.has("name")) {
                productName = block.get("name").asText();
            }

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return productName;
            }

            throw new UnknownHostException(e.getMessage());
        }
        return productName;
    }

    private List<?> getTransactions(String iban) {
        WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl("http://localhost:8082/transaction")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        
        return build.method(HttpMethod.GET).uri(uriBuilder -> uriBuilder
            .path("/customer/transactions")
            .queryParam("ibanAccount", iban)
            .build())
        .retrieve().bodyToFlux(Object.class).collectList().block();                
    }
    
}
