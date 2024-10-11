//package basepkg.model;
//import lombok.*;
//import jakarta.persistence.*;
//import java.util.List;
//
//@Entity
//@Table(name = "shop_orders")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Data
//public class Order {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private String Customermail;
//    private String orderNumber;
//    //    @OneToMany(cascade = CascadeType.ALL)
//    //    private List<OrderLineItems> orderLineItemsList;
//
//    @Convert(converter = OrderLineItemsConverter.class)
//    @Column(name = "line-items", columnDefinition = "json")
//    private List<OrderLineItems> orderLineItemsList;
//
//}