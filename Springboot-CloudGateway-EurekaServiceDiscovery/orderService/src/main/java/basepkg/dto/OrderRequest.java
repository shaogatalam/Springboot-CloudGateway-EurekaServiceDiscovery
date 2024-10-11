package basepkg.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private String Customermail;
    //    private List<OrderLineItemsDto> orderLineItemsDtoList;
    private List<OrderLineItemsDto> orderLineItemsDtoList = new ArrayList<>();

    public String getCustomermail() {
        return Customermail;
    }

    public void setCustomermail(String customermail) {
        Customermail = customermail;
    }

    public List<OrderLineItemsDto> getOrderLineItemsDtoList() {
        return orderLineItemsDtoList;
    }

    public void setOrderLineItemsDtoList(List<OrderLineItemsDto> orderLineItemsDtoList) {
        this.orderLineItemsDtoList = orderLineItemsDtoList;
    }
}