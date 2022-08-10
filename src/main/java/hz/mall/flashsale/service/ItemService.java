package hz.mall.flashsale.service;

import hz.mall.flashsale.domain.Item;
import hz.mall.flashsale.error.BusinessException;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.List;

public interface ItemService {

    Item createItem(Item item) throws BusinessException;

    List<Item> listItem();

    Item getItemById(Integer id);

    Item getItemByIdInCache(Integer id);

    void publishPromo(Integer promoId);

    boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException;

    boolean asyncDecreaseStock(Integer itemId, Integer amount) throws InterruptedException, RemotingException, MQClientException, MQBrokerException;

    void increaseSales(Integer itemId, Integer amount);

    String initStockLog(Integer itemId, Integer amount);

    boolean increaseStock(Integer itemId, Integer amount) throws BusinessException;
}
