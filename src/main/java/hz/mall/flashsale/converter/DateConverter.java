package hz.mall.flashsale.converter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.springframework.stereotype.Component;


@Component
public class DateConverter {
    public String asString(DateTime dateTime) {
        return DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss").withZone(DateTimeZone.getDefault())
                .print( dateTime ).trim();
    }
}
