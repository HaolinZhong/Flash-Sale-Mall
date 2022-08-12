package hz.mall.flashsale.web.controller;

import hz.mall.flashsale.domain.Order;
import hz.mall.flashsale.domain.User;
import hz.mall.flashsale.error.BusinessErrEnum;
import hz.mall.flashsale.error.BusinessException;
import hz.mall.flashsale.mq.DecreaseStockProducer;
import hz.mall.flashsale.service.IntegratedService;
import hz.mall.flashsale.service.ItemService;
import hz.mall.flashsale.service.OrderService;
import hz.mall.flashsale.service.PromoService;
import hz.mall.flashsale.web.model.CommonReturnType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.MQProducer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.concurrent.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/order")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*", originPatterns = "*")
@Validated
public class OrderController {

    private final HttpServletRequest httpServletRequest;
    private final RedisTemplate redisTemplate;
    private final ItemService itemService;
    private final IntegratedService integratedService;
    private final DecreaseStockProducer decreaseStockProducer;
    private ExecutorService executorService;

    @PostConstruct
    public void init(){
        executorService = Executors.newFixedThreadPool(20);

    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public CommonReturnType createOrder(
            @NotNull @RequestParam("itemId") Integer itemId,
            @NotNull @RequestParam("amount") Integer amount,
            @RequestParam(value = "promoId", required = false) Integer promoId,
            @RequestParam(value = "promoToken", required = false) String promoToken
    ) throws BusinessException {

        // get user login information
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if (StringUtils.isEmpty(token))
            throw new BusinessException(BusinessErrEnum.USER_NOT_LOGIN, "user has not login");

        // get user information
        User user = (User) redisTemplate.opsForValue().get(token);
        if (user == null) throw new BusinessException(BusinessErrEnum.USER_NOT_LOGIN, "user has not login");


        if (promoId != null) {
            String inRedisPromoToken = (String) redisTemplate.opsForValue().get("promo_token_" + promoId + "_userid_" + user.getId() + "_itemid_" + itemId);
            if (inRedisPromoToken == null || !StringUtils.equals(promoToken, inRedisPromoToken)) {
                throw new BusinessException(BusinessErrEnum.PARAMETER_VALIDATION_ERROR, "token validation failed");
            }
        }

        // incoming create order requests will be placed in this queue.
        Future<Object> future = executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                // init a stock log into db for facilitating data consistency
                String stockLogId = itemService.initStockLog(itemId, amount);

                /**
                 * place the order by transactional async message
                 *
                 * upon sending of the message, producer will execute the transaction of creating an order.
                 * logic inside ensured final consistency of stock data.
                 *
                 * returns a boolean suggesting successful creation of order and commit of transaction
                 */
                boolean result = decreaseStockProducer.transactionAsyncReduceStock(user.getId(), itemId, promoId, amount, stockLogId);

                if (!result) throw new BusinessException(BusinessErrEnum.UNKNOW_ERROR, "failed to send create order message");

                return null;
            }
        });

        try {
            future.get();
        } catch (InterruptedException e) {
            throw new BusinessException(BusinessErrEnum.UNKNOW_ERROR);
        } catch (ExecutionException e) {
            throw new BusinessException(BusinessErrEnum.UNKNOW_ERROR);
        }


        return CommonReturnType.builder().status("success").build();
    }

    @PostMapping(value = "/generatetoken", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public CommonReturnType generateToken(
            @RequestParam(name = "itemId") Integer itemId,
            @RequestParam(name = "promoId") Integer promoId
    ) throws BusinessException {
        // get user login information
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if (StringUtils.isEmpty(token))
            throw new BusinessException(BusinessErrEnum.USER_NOT_LOGIN, "user has not login");

        // get user information
        User user = (User) redisTemplate.opsForValue().get(token);
        if (user == null) throw new BusinessException(BusinessErrEnum.USER_NOT_LOGIN, "user has not login");

        String promoToken = integratedService.generateFlashSaleToken(promoId, itemId, user.getId());
        if (promoToken == null) throw new BusinessException(BusinessErrEnum.PARAMETER_VALIDATION_ERROR, "failed to generate token");

        return CommonReturnType.builder().status("success").data(promoToken).build();
    }

}
