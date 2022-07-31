package hz.mall.flashsale.web.controller;

import hz.mall.flashsale.domain.Order;
import hz.mall.flashsale.domain.User;
import hz.mall.flashsale.error.BusinessErrEnum;
import hz.mall.flashsale.error.BusinessException;
import hz.mall.flashsale.service.OrderService;
import hz.mall.flashsale.web.model.CommonReturnType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/order")
@CrossOrigin(allowCredentials="true", allowedHeaders = "*", originPatterns = "*")
@Validated
public class OrderController {

    private final OrderService orderService;
    private final HttpServletRequest httpServletRequest;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public CommonReturnType createOrder(
            @NotNull @RequestParam("itemId") Integer itemId,
            @NotNull @RequestParam("amount") Integer amount,
            @RequestParam(value = "promoId", required = false) Integer promoId
    ) throws BusinessException {

        // get user login information
        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if (isLogin == null || !isLogin.booleanValue()) {
            throw new BusinessException(BusinessErrEnum.USER_NOT_LOGIN, "user has not login");
        }

        // get user information
        User user = (User) httpServletRequest.getSession().getAttribute("LOGIN_USER");

        Order order= orderService.createOrder(user.getId(), itemId, amount, promoId);

        return CommonReturnType.builder().status("success").build();
    }
}
