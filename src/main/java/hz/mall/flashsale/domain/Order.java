package hz.mall.flashsale.domain;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    private String id;

    private Integer userId;

    private Integer itemId;

    private Integer amount;

    private BigDecimal itemPrice;

    private BigDecimal totalPrice;
}
