package hz.mall.flashsale.web.controller;

import hz.mall.flashsale.converter.ItemConverter;
import hz.mall.flashsale.domain.Item;
import hz.mall.flashsale.error.BusinessException;
import hz.mall.flashsale.service.ItemService;
import hz.mall.flashsale.web.model.CommonReturnType;
import hz.mall.flashsale.web.model.ItemVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/item")
@CrossOrigin
public class ItemController extends BaseController {

    private final ItemService itemService;
    private final ItemConverter itemConverter;

    @PostMapping("/create")
    @ResponseBody
    public CommonReturnType createItem(
            @NotBlank(message = "Item title cannot be empty")
            @RequestParam(name = "title") String title,

            @NotBlank(message = "Item description cannot be empty")
            @RequestParam(name = "desc") String desc,

            @NotNull(message = "Item price cannot be empty")
            @Min(value = 0, message = "Item price must not be smaller than 0")
            @RequestParam(name = "price") BigDecimal price,

            @NotNull(message = "Item stock cannot be empty")
            @RequestParam(name = "stock") Integer stock,

            @NotBlank(message = "Item image url cannot be empty")
            @RequestParam(name = "imgUrl") String imgUrl
            ) throws BusinessException {

        Item item = Item.builder()
                .title(title)
                .description(desc)
                .price(price)
                .stock(stock)
                .imgUrl(imgUrl)
                .build();

        Item itemCreated = itemService.createItem(item);
        ItemVo itemVo = itemConverter.itemToVo(itemCreated);

        return CommonReturnType.builder().status("success").data(itemVo).build();
    }
}
