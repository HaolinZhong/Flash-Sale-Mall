package hz.mall.flashsale.service;


public interface CacheService {
    void setCommonCache(String key, Object value);
    Object getCommonCache(String key);
}
