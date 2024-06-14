package com.paymentchain.customer.controllers;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.paymentchain.customer.entities.CustomerEntity;
import com.paymentchain.customer.entities.CustomerProduct;
import com.paymentchain.customer.repositories.CustomerRepository;

import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    Environment env;

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


    @GetMapping("/check")
    public String getCheck() {
        return "Hello, your property value is " + env.getProperty("custom.activeprofileName");
    }

    @GetMapping()
    public List<CustomerEntity> list() {
        return customerRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") long id) {
        Optional<CustomerEntity> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
           return new ResponseEntity<>(customer.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable("id") long id, @RequestBody CustomerEntity input) {
        Optional<CustomerEntity> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            CustomerEntity newCustomer = customer.get();
            newCustomer.setName(input.getName());
            newCustomer.setPhone(input.getPhone());
            newCustomer.setIban(input.getIban());
            newCustomer.setAddress(input.getAddress());
            newCustomer.setSurname(input.getSurname());
            newCustomer.setProducts(input.getProducts());
            newCustomer.setTransactions(input.getTransactions());
            CustomerEntity saved = customerRepository.save(newCustomer);
            return new ResponseEntity<>(saved, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping()
    public ResponseEntity<?> post(@RequestBody CustomerEntity input) {
        input.getProducts().forEach(customerProduct -> customerProduct.setCustomer(input));
        CustomerEntity saved = customerRepository.save(input);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        customerRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/full")
    public CustomerEntity getCustomerByCode(@RequestParam("code") String code) {
        CustomerEntity customer = customerRepository.findByCode(code);
        List<CustomerProduct> products = customer.getProducts();

        products.forEach(product -> {
            String productName = getProductName(product.getProductId());
            product.setProductName(productName);
        });

        // customer.setTransactions(getTransactions(customer.getIban()));
        return customer;
    }

    private String getProductName(long id) {
        WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl("http://BUSSINESSDOMAIN-PRODUCTS/product")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        
        JsonNode block = build.method(HttpMethod.GET).uri("/" + id)
                .retrieve().bodyToMono(JsonNode.class).block();

        if (block != null && block.has("name")) {
            return block.get("name").asText();
        } else {
            return "Default product name";
        }
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
