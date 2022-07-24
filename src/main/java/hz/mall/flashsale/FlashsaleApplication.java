package hz.mall.flashsale;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@MapperScan("hz.mall.flashsale.mapper")
public class FlashsaleApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlashsaleApplication.class, args);
    }

}
