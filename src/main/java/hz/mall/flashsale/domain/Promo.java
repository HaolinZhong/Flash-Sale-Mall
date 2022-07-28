package hz.mall.flashsale.domain;

import lombok.*;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promo {

    private Integer id;

    private String promoName;

    private DateTime startDate;

    // in this project, only 1 item will be included in a promo event
    private Integer itemId;

    private BigDecimal promoItemPrice;

}
