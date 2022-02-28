package com.xsoft.sevn.dtk;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommoditMapper {
    @Insert("INSERT INTO commodit_list(id, brand_name, brand_id, add_time, category_name, cid, commission_rate, " +
            "coupon_amount, coupon_end_time, coupon_end_time_stamp, coupon_num, coupon_over, d_title, " +
            "is_chaoshi, is_choice, is_tmall, main_pic, original_price, price, shop_name, commission, dtkCommoditURL, shortLink, redirectsURL, " +
            "taobaoCommoditURL, tmallDetailUrl, tmallPromStartTime, tmallPromEndTime, campaignName, promPlanMsg) " +
            "VALUES(#{id}, #{brand_name}, #{brand_id}, #{add_time}, #{category_name}, #{cid}, " +
            "#{commission_rate}, #{coupon_amount}, #{coupon_end_time}, #{coupon_end_time_stamp}, " +
            "#{coupon_num}, #{coupon_over}, #{d_title}, #{is_chaoshi}, " +
            "#{is_choice}, #{is_tmall}, #{main_pic}, #{original_price}, " +
            "#{price}, #{shop_name}, #{commission}, #{dtkCommoditURL}, " +
            "#{shortLink}, #{redirectsURL}, #{taobaoCommoditURL}, #{tmallDetailUrl}, #{tmallPromStartTime}, #{tmallPromEndTime}, #{campaignName}, #{promPlanMsg})")
    int insert(Commodit commodit);

    @Delete ("DELETE FROM commodit_list")
    int cleanData();

    @Update("UPDATE commodit_list SET dtkCommoditURL=#{dtkCommoditURL}, shortLink=#{shortLink}, redirectsURL=#{redirectsURL}, taobaoCommoditURL=#{taobaoCommoditURL}, tmallDetailUrl=#{tmallDetailUrl}, tmallPromStartTime=#{tmallPromStartTime}, tmallPromEndTime=#{tmallPromEndTime},campaignName=#{campaignName}, promPlanMsg=#{promPlanMsg}, addPromTime=#{addPromTime} WHERE id=#{id}")
    int updateCommodit(Commodit commodit);

    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "brand_name", column = "brand_name")
    })
    @Select("SELECT * FROM commodit_list WHERE addPromTime is null")
    List<Commodit> queryAllNoCommoditList();
}
