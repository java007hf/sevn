package com.xsoft.sevn.dtk;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

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
}
