package hz.mall.flashsale.mapper;

import hz.mall.flashsale.domain.ItemDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemDoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Wed Jul 27 01:28:06 EDT 2022
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Wed Jul 27 01:28:06 EDT 2022
     */
    int insert(ItemDo row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Wed Jul 27 01:28:06 EDT 2022
     */
    int insertSelective(ItemDo row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Wed Jul 27 01:28:06 EDT 2022
     */
    ItemDo selectByPrimaryKey(Integer id);

    List<ItemDo> listAllItems();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Wed Jul 27 01:28:06 EDT 2022
     */
    int updateByPrimaryKeySelective(ItemDo row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Wed Jul 27 01:28:06 EDT 2022
     */
    int updateByPrimaryKey(ItemDo row);

    int increaseSales(@Param("id") Integer itemId, @Param("amount") Integer amount);
}