package hz.mall.flashsale;

import hz.mall.flashsale.domain.User;
import hz.mall.flashsale.mapper.UserMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = {"hz.mall.flashsale"})
@MapperScan("hz.mall.flashsale.mapper")
@RestController
public class FlashsaleApplication {

    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/test")
    public String home(){
        User user = userMapper.selectByPrimaryKey(1);
        if (user == null) {
            return "user does not exist!";
        } else {
            return user.toString();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(FlashsaleApplication.class, args);
    }

}
