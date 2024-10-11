package basepkg.service;

import basepkg.dto.InventoryResponse;
import basepkg.dto.OrderLineItemsDto;
import basepkg.dto.OrderNotification;
import basepkg.dto.OrderRequest;
import basepkg.model.Order;
import basepkg.model.OrderLineItems;
import basepkg.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@AllArgsConstructor
@Transactional
@Slf4j
public class OrderService {
    private final WebClient webClient;
    // Q1o03xpd94VPmo

    @Autowired
    public OrderRepository orderRepository;

    @Autowired
    private KafkaTemplate<String, OrderNotification> kafkaTemplate;

    @Autowired
    public OrderService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8282/realms/springbootmicroservice").build();
    }

    public String placeOrder(OrderRequest orderRequest) {

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList().stream()
                .map(OrderLineItems::new)
                .toList();

        Order order = new Order();
        order.setCustomermail(orderRequest.getCustomermail());
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderLineItemsList(orderLineItems);
        orderRepository.save(order);

        List<String> skuCodes = order.getOrderLineItemsList().stream()
            .map(OrderLineItems::getSkuCode)
            .toList();


        InventoryResponse[] inventoryResponseArray = webClient.get()
            .uri("http://INVENTORYSERVICE/api/inventory",uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
            .retrieve()
            .bodyToMono(InventoryResponse[].class)
            .block();

        //Check which products are not in stock and store their SKU codes
        List<String> outOfStockProducts = Arrays.stream(inventoryResponseArray)
                                        .filter(response -> !response.isInStock())
                                        .map(InventoryResponse::getSkuCode)
                                        .collect(Collectors.toList());

        if (outOfStockProducts.isEmpty()) {

            //orderRepository.save(order);
            Order savedOrder = orderRepository.save(order);
            String orderId   = String.valueOf(savedOrder.getId());
            OrderNotification ordernotification = new OrderNotification();
            ordernotification.setOrderId(orderId);
            ordernotification.setCustomermail(orderRequest.getCustomermail());

            kafkaTemplate.send("OrderCreateTopic", ordernotification);
            return "OrderPlaced";

        } else {
            throw new IllegalArgumentException("The following products are not in stock: " + String.join(", ", outOfStockProducts));
        }

    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}