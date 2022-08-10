package hz.mall.flashsale.mq;

import com.alibaba.fastjson.JSON;
import hz.mall.flashsale.domain.StockLogDo;
import hz.mall.flashsale.error.BusinessException;
import hz.mall.flashsale.mapper.StockLogDoMapper;
import hz.mall.flashsale.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class DecreaseStockProducer {

    private DefaultMQProducer producer;
    private TransactionMQProducer transactionProducer;

    @Value("${mq.nameserver.addr}")
    private String addr;
    @Value("${mq.topicname}")
    private String topicName;

    private final OrderService orderService;
    private final StockLogDoMapper stockLogDoMapper;

    @PostConstruct
    public void init() throws MQClientException {
        producer = new DefaultMQProducer("producer_group");
        producer.setNamesrvAddr(addr);
        producer.start();


        transactionProducer = new TransactionMQProducer("transaction_producer_group");
        transactionProducer.setNamesrvAddr(addr);
        transactionProducer.start();

        transactionProducer.setTransactionListener(new TransactionListener() {
            // tasks triggered when message was sent
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object o) {
                Integer itemId = (Integer) ((Map) o).get("itemId");
                Integer promoId = (Integer) ((Map) o).get("promoId");
                Integer userId = (Integer) ((Map) o).get("userId");
                Integer amount = (Integer) ((Map) o).get("amount");
                String stockLogId = (String) ((Map) o).get("stockLogId");

                try {
                    orderService.createOrder(userId, itemId, promoId, amount, stockLogId);
                } catch (BusinessException e) {
                    e.printStackTrace();
                    // update stock log status to show that transaction failed
                    StockLogDo stockLogDo = stockLogDoMapper.selectByPrimaryKey(stockLogId);
                    stockLogDo.setStatus(3); // 3 means exception happened
                    stockLogDoMapper.updateByPrimaryKeySelective(stockLogDo);
                    // transaction failed
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }

                // transaction successfully executed
                return LocalTransactionState.COMMIT_MESSAGE;
            }

            // repeatedly triggered for checking transaction status
            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {
                // get stock log
                String jsonString  = new String(msg.getBody());
                Map<String,Object>map = JSON.parseObject(jsonString, Map.class);
                String stockLogId = (String) map.get("stockLogId");
                StockLogDo stockLogDo = stockLogDoMapper.selectByPrimaryKey(stockLogId);

                if(stockLogDo == null){
                    return LocalTransactionState.UNKNOW;
                }
                if(stockLogDo.getStatus().intValue() == 2){
                    return LocalTransactionState.COMMIT_MESSAGE;
                }else if(stockLogDo.getStatus().intValue() == 1){
                    return LocalTransactionState.UNKNOW;
                }

                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        });


    }

    public boolean transactionAsyncReduceStock(Integer userId, Integer itemId, Integer promoId,
                                               Integer amount, String stockLogId) {

        // bodyMap: the body of the message
        Map<String,Object> bodyMap = new HashMap<>();
        bodyMap.put("itemId",itemId);
        bodyMap.put("amount",amount);
        bodyMap.put("stockLogId",stockLogId);

        // argsMap: args to be sent into executeLocalTransaction function
        Map<String,Object> argsMap = new HashMap<>();
        argsMap.put("itemId",itemId);
        argsMap.put("amount",amount);
        argsMap.put("userId",userId);
        argsMap.put("promoId",promoId);
        argsMap.put("stockLogId",stockLogId);

        Message message = new Message(topicName,"increase",
                JSON.toJSON(bodyMap).toString().getBytes(Charset.forName("UTF-8")));

        TransactionSendResult sendResult = null;

        try {
            sendResult = transactionProducer.sendMessageInTransaction(message, argsMap);
        } catch (MQClientException e) {
            e.printStackTrace();
            return false;
        }

        // return execution result according to send result
        if(sendResult.getLocalTransactionState() == LocalTransactionState.ROLLBACK_MESSAGE){
            return false;
        }else if(sendResult.getLocalTransactionState() == LocalTransactionState.COMMIT_MESSAGE){
            return true;
        }else{
            return false;
        }
    }


    public boolean asyncReduceStock(Integer itemId, Integer amount) {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("itemId", itemId);
        bodyMap.put("amount", amount);

        Message message = new Message(topicName, "increase",
                JSON.toJSON(bodyMap).toString().getBytes(Charset.forName("UTF-8")));
        try {
            producer.send(message);
        } catch (MQClientException e) {
            e.printStackTrace();
            return false;
        } catch (RemotingException e) {
            e.printStackTrace();
            return false;
        } catch (MQBrokerException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
