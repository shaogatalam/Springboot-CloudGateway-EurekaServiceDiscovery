//package basepkg.model;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.persistence.AttributeConverter;
//import jakarta.persistence.Converter;
//
//import java.io.IOException;
//import java.util.List;
//
//@Converter
//public class OrderLineItemsConverter implements AttributeConverter<List<OrderLineItems>, String> {
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Override
//    public String convertToDatabaseColumn(List<OrderLineItems> orderLineItems) {
//        try {
//            return objectMapper.writeValueAsString(orderLineItems);
//        } catch (JsonProcessingException e) {
//            throw new IllegalArgumentException("Error converting list to JSON", e);
//        }
//    }
//
//    @Override
//    public List<OrderLineItems> convertToEntityAttribute(String orderLineItemsJson) {
//        try {
//            return objectMapper.readValue(orderLineItemsJson,
//                    objectMapper.getTypeFactory().constructCollectionType(List.class, OrderLineItems.class));
//        } catch (IOException e) {
//            throw new IllegalArgumentException("Error converting JSON to list", e);
//        }
//    }
//}
