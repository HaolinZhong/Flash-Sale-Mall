package hz.mall.flashsale.web.controller;

import hz.mall.flashsale.converter.ItemConverter;
import hz.mall.flashsale.domain.Item;
import hz.mall.flashsale.error.BusinessException;
import hz.mall.flashsale.service.ItemService;
import hz.mall.flashsale.web.model.CommonReturnType;
import hz.mall.flashsale.web.model.ItemVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/item")
@CrossOrigin(allowCredentials="true", allowedHeaders = "*", originPatterns = "*")
@Validated
public class ItemController {

    private final ItemService itemService;
    private final ItemConverter itemConverter;
    private final RedisTemplate redisTemplate;

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
        ItemVo itemVo = itemConverter.itemToItemVo(itemCreated);

        return CommonReturnType.builder().status("success").data(itemVo).build();
    }


    @GetMapping(value = "/get")
    @ResponseBody
    public CommonReturnType getItem(@RequestParam("id") Integer id) {

        // get item by id from redis at first
        Item item = (Item) redisTemplate.opsForValue().get("item_" + id);

        // if item not exist in redis
        if (item == null) {
            item = itemService.getItemById(id);
            // store item into redis
            redisTemplate.opsForValue().set("item_" + id, item);
            redisTemplate.expire("item_" + id, 10, TimeUnit.MINUTES);
        }

        ItemVo itemVo = itemConverter.itemToItemVo(item);

        return CommonReturnType.builder().status("success").data(itemVo).build();
    }

    @GetMapping(value = "/list")
    @ResponseBody
    public CommonReturnType listItem() {
        List<Item> itemList = itemService.listItem();
        List<ItemVo> itemVoList = itemList
                .stream().map(item -> itemConverter.itemToItemVo(item))
                .collect(Collectors.toList());
        return CommonReturnType.builder().status("success").data(itemVoList).build();
    }
}
