package com.example.cashierwebapp;

import com.example.cashierwebapp.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

/*
    RestTemplate JavaDoc:
        * https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html

    Tutorial Resources:
        * https://reflectoring.io/spring-resttemplate
        * https://www.baeldung.com/rest-template
        * https://springframework.guru/enable-pretty-print-of-json-with-jackson
 */

@Controller
@RequestMapping("/")
public class CashierController {

    @GetMapping
    public String getAction(@ModelAttribute("command") CashierCommand command, Model model) {
        return "cashier_page";
    }

    @PostMapping
    public String postAction(@ModelAttribute("command") CashierCommand command,
                             @RequestParam(value = "action", required = true) String action,
                             @RequestParam(value = "register", required = true) String register,
                             @RequestParam(value = "drink", required = true) String drink,
                             @RequestParam(value = "milk", required = true) String milk,
                             @RequestParam(value = "size", required = true) String size,
                             Errors errors, Model model, HttpServletRequest request) {

        // Initialize some variables
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String resourceUrl = "" ;
        String message = "";

        // Environment variables
        String host = "http://35.232.154.249/api";
        String apikey = "Zkfokey2311";

        // Set headers
        headers.set("apikey", "Zkfokey2311");
        headers.add("register", register);
        headers.add("size", size);
        headers.add("drink", drink);
        headers.add("milk", milk);

        // Get active order
        if (action.equals("Get Order")) {
            message = "Get Order";
            resourceUrl = host.concat("/order/register/" + register + "?apikey={apikey}");
            System.out.println(resourceUrl);
            try {
                ResponseEntity<Object> orderResponse = restTemplate.getForEntity(
                        resourceUrl, Object.class, apikey);
                // Order orderMsg = orderResponse.getBody();
                if (orderResponse.getStatusCodeValue() == 200) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonString = objectMapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(orderResponse.getBody());
                    message = "The active order:\n" + jsonString;
                }
                else if (orderResponse.getStatusCodeValue() == 400) {
                    message = "No active orders for this registry.";
                }
                else {
                    message = "Oops, something went wrong when getting an order.";
                }
            }
            catch ( Exception e ) {
                message = "No active orders for this registry.";
                System.out.println(e.getMessage());
            }
        }

        // Place new order
        if (action.equals("Place Order")) {
            resourceUrl = host.concat("/order/register/" + register + "?apikey={apikey}");
            System.out.println(resourceUrl);
            Order orderRequest = new Order();
            orderRequest.setDrink(drink);
            orderRequest.setSize(size);
            orderRequest.setMilk(milk);
            HttpEntity<Object> newOrderRequest = new HttpEntity<>(orderRequest, headers);
            try {
                ResponseEntity<Object> newOrderResponse = restTemplate.postForEntity(
                        resourceUrl, newOrderRequest, Object.class, apikey);
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonString = objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(newOrderResponse.getBody());;
                if (newOrderResponse.getStatusCodeValue() == 201) {
                    // Order newOrder = (Order) newOrderResponse.getBody();
                    message = "Placing an order:\n" + jsonString;
                }
                else if (newOrderResponse.getStatusCodeValue() == 400) {
                    message = "Active order exists or there is an input error when placing order.";
                }
                else {
                    message ="Oops, something went wrong when placing an order.";
                }
            }
            catch (Exception e) {
                message ="Active order exists or there is an input error when placing order.";
            }
        }

        // Clear active order
        if (action.equals("Clear Order")) {
            try {
                resourceUrl = host.concat("/order/register/" + register + "?apikey={apikey}");
                System.out.println(resourceUrl);
                restTemplate.delete(resourceUrl, apikey);
                message = "Active order cleared.";
            }
            catch (Exception e) {
                message = "No active order for this registry.";
                System.out.println(e.getMessage());
            }
        }

        // Display page
        model.addAttribute("message", message);
        return "cashier_page";
    }

}