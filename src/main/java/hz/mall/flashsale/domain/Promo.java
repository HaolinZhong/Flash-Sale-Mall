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

    // status (active or passed) of promo; 1 for not started, 2 for ongoing, 3 for outdated
    private Integer status;

    private String promoName;

    private DateTime startDate;

    private DateTime endDate;

    // in this project, only 1 item will be included in a promo event
    private Integer itemId;

    private BigDecimal promoItemPrice;

}
