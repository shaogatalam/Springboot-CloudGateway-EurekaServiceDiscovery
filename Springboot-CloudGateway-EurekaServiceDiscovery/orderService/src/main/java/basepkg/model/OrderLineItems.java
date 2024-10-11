//package basepkg.model;
//
//import basepkg.dto.OrderLineItemsDto;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.math.BigDecimal;
//
////
////
////import jakarta.persistence.*;
////import lombok.AllArgsConstructor;
////import lombok.Getter;
////import lombok.NoArgsConstructor;
////import lombok.Setter;
////
////import java.math.BigDecimal;
////
////@Getter
////@Setter
////@AllArgsConstructor
////@NoArgsConstructor
////public class OrderLineItems {
////    private Long id;
////    private String skuCode;
////    private BigDecimal price;
////    private Integer quantity;
////}
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//public class OrderLineItems {
//    private String skuCode;
//    private BigDecimal price;
//    private Integer quantity;
//    private Integer productId;
//    private Integer variationId;
//
//    public OrderLineItems(OrderLineItemsDto orderLineItemsDto) {
//        this.skuCode = orderLineItemsDto.getSkuCode();
//        this.price = orderLineItemsDto.getPrice();
//        this.quantity = orderLineItemsDto.getQuantity();
//        this.productId = orderLineItemsDto.getProductId();
//        this.variationId = orderLineItemsDto.getVariationId();
//    }
//
//    public String getSkuCode() {
//        return skuCode;
//    }
//
//    public void setSkuCode(String skuCode) {
//        this.skuCode = skuCode;
//    }
//
//    public BigDecimal getPrice() {
//        return price;
//    }
//
//    public void setPrice(BigDecimal price) {
//        this.price = price;
//    }
//
//    public Integer getQuantity() {
//        return quantity;
//    }
//
//    public void setQuantity(Integer quantity) {
//        this.quantity = quantity;
//    }
//
//    public Integer getProductId() {
//        return productId;
//    }
//
//    public void setProductId(Integer productId) {
//        this.productId = productId;
//    }
//
//    public Integer getVariationId() {
//        return variationId;
//    }
//
//    public void setVariationId(Integer variationId) {
//        this.variationId = variationId;
//    }
//}