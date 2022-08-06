package hz.mall.flashsale.domain;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item implements Serializable {

    private Integer id;

    private String title;

    private BigDecimal price;

    private Integer stock;

    private String description;

    private Integer sales;

    private String imgUrl;

    // if promo is not null, the item is in an incoming or ongoing promo
    private Promo promo;
}
