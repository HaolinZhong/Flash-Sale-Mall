package hz.mall.flashsale.mapper;

import hz.mall.flashsale.domain.PromoDo;

public interface PromoDoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbg.generated Wed Jul 27 23:17:27 EDT 2022
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbg.generated Wed Jul 27 23:17:27 EDT 2022
     */
    int insert(PromoDo row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbg.generated Wed Jul 27 23:17:27 EDT 2022
     */
    int insertSelective(PromoDo row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbg.generated Wed Jul 27 23:17:27 EDT 2022
     */
    PromoDo selectByPrimaryKey(Integer id);

    PromoDo selectByItemId(Integer itemId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbg.generated Wed Jul 27 23:17:27 EDT 2022
     */
    int updateByPrimaryKeySelective(PromoDo row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbg.generated Wed Jul 27 23:17:27 EDT 2022
     */
    int updateByPrimaryKey(PromoDo row);
}