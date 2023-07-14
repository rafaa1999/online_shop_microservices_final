package com.rafaa.orderservice.service;

import java.util.UUID;
import java.util.Arrays;
import java.util.List;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import com.rafaa.orderservice.dto.InventoryResponse;
import com.rafaa.orderservice.dto.OrderLineItemsDto;
import com.rafaa.orderservice.dto.OrderRequest;
import com.rafaa.orderservice.model.Order;
import com.rafaa.orderservice.model.OrderLineItems;
import com.rafaa.orderservice.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

	private final OrderRepository orderRepository;
	private final WebClient webClient;
	
	public void placeOrder(@RequestBody OrderRequest orderRequest) {
		
		Order order = new Order();
		order.setOrderNumber(UUID.randomUUID().toString());
		
		 List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
					.stream()
					.map(this::mapTpDto)
					.collect(Collectors.toList());
		order.setOrderLineItemsList(orderLineItems);
		
		List<String> skuCodes= order.getOrderLineItemsList().stream()
									 .map(orderLineItem -> orderLineItem.getSkuCode())
									 .collect(Collectors.toList());
		
		// call inventory service, and place order if product is in stock
		InventoryResponse[] inventoryResponsesArray = webClient.get()
								  .uri("http://localhost:8082/api/inventory", 
									    UriBuilder -> UriBuilder.queryParam("skuCode", skuCodes).build())
								  .retrieve()
								  .bodyToMono(InventoryResponse[].class)
								  .block();
		
		boolean allProductsInStock = Arrays.stream(inventoryResponsesArray).allMatch(inventoryResponse -> inventoryResponse.isInStock());
		
		if(allProductsInStock) {
			orderRepository.save(order);			
		}else {
			throw new IllegalArgumentException("Product is not in stock, try again later");
		}
	}
	
	public OrderLineItems mapTpDto(OrderLineItemsDto orderLineItemsDto) {
		
		OrderLineItems orderLineItems = new OrderLineItems();
		orderLineItems.setPrice(orderLineItemsDto.getPrice());
		orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
		orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
		return orderLineItems;
		
	}
}
