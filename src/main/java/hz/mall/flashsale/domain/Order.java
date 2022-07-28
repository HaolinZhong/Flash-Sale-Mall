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

    // if not null, the order is placed under a promo
    private Integer promoId;

    // if promoId is not null, represent promo price
    private BigDecimal itemPrice;

    private BigDecimal totalPrice;
}
