package hz.mall.flashsale.mapper;

import hz.mall.flashsale.domain.ItemStockDo;
import org.apache.ibatis.annotations.Param;

public interface ItemStockDoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item_stock
     *
     * @mbg.generated Wed Jul 27 01:28:06 EDT 2022
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item_stock
     *
     * @mbg.generated Wed Jul 27 01:28:06 EDT 2022
     */
    int insert(ItemStockDo row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item_stock
     *
     * @mbg.generated Wed Jul 27 01:28:06 EDT 2022
     */
    int insertSelective(ItemStockDo row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item_stock
     *
     * @mbg.generated Wed Jul 27 01:28:06 EDT 2022
     */
    ItemStockDo selectByPrimaryKey(Integer id);

    ItemStockDo selectByItemId(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item_stock
     *
     * @mbg.generated Wed Jul 27 01:28:06 EDT 2022
     */
    int updateByPrimaryKeySelective(ItemStockDo row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item_stock
     *
     * @mbg.generated Wed Jul 27 01:28:06 EDT 2022
     */
    int updateByPrimaryKey(ItemStockDo row);

    int decreaseStock(@Param("itemId") Integer itemId, @Param("amount") Integer amount);
}