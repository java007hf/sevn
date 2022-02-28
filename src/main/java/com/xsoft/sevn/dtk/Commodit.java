package com.xsoft.sevn.dtk;

public class Commodit {
    String brand_name; //商品名称 王老吉
    String brand_id; //商品id 3378051
    String add_time; //上架时间 2022-02-14 21:31:02
    String category_name; //商品类型 凉茶
    int cid; //商品大类别 8
    float commission_rate; //佣金百分比 4.25
    float coupon_amount; //优惠券 5
    String coupon_end_time; //优惠券结束时间 2022-02-15 23:59:59
    int coupon_end_time_stamp; //优惠券结束时间 1644940799
    int coupon_num; //优惠券总数 1000
    int coupon_over; //优惠券使用 0
    String d_title; //商品title 降火必备！王老吉凉茶植物饮料310ml*24罐
    String id; //商品详情id 37257489
    boolean is_chaoshi; //是否超市 1
    boolean is_choice; //是否精选 1
    boolean is_tmall; //是否天猫 1
    String main_pic; //商品图片 https://img.alicdn.com/i1/725677994/O1CN01qqhx7s28vIpdX0WhM_!!725677994.jpg
    float original_price; //原价格 45.0
    float price; //现价 30.0
    String shop_name; //商店名称
    float commission; //佣金计算

    String dtkCommoditURL; //大淘客原始URL
    String shortLink; //从大淘客获取的短链
    String redirectsURL; //最终跳转的长链
    String taobaoCommoditURL; //长链里获取的阿里购物链接
    String tmallDetailUrl; //天猫购物链接

    Long tmallPromStartTime; //天猫促销活动开始时间 1645149600000
    Long tmallPromEndTime; //天猫促销活动结束时间 1645444799000
    String campaignName; //促销名称 淘客零食大礼包
    String promPlanMsg; //促销信息 满49.9元,省20元

    String addPromTime; //获取活动的时间 2022-02-14 21:31:02

    @Override
    public String toString () {
        return "Commodit{" +
                "brand_name='" + brand_name + '\'' +
                ", brand_id='" + brand_id + '\'' +
                ", add_time='" + add_time + '\'' +
                ", category_name='" + category_name + '\'' +
                ", cid='" + cid + '\'' +
                ", commission_rate='" + commission_rate + '\'' +
                ", coupon_amount='" + coupon_amount + '\'' +
                ", coupon_end_time='" + coupon_end_time + '\'' +
                ", coupon_end_time_stamp='" + coupon_end_time_stamp + '\'' +
                ", coupon_num='" + coupon_num + '\'' +
                ", coupon_over='" + coupon_over + '\'' +
                ", d_title='" + d_title + '\'' +
                ", id='" + id + '\'' +
                ", is_chaoshi='" + is_chaoshi + '\'' +
                ", is_choice='" + is_choice + '\'' +
                ", is_tmall='" + is_tmall + '\'' +
                ", main_pic='" + main_pic + '\'' +
                ", original_price='" + original_price + '\'' +
                ", price='" + price + '\'' +
                ", shop_name='" + shop_name + '\'' +
                ", commission='" + commission + '\'' +
                ", dtkCommoditURL='" + dtkCommoditURL + '\'' +
                ", shortLink='" + shortLink + '\'' +
                ", redirectsURL='" + redirectsURL + '\'' +
                ", taobaoCommoditURL='" + taobaoCommoditURL + '\'' +
                ", tmallDetailUrl='" + tmallDetailUrl + '\'' +
                ", campaignName='" + campaignName + '\'' +
                ", tmallPromStartTime='" + tmallPromStartTime + '\'' +
                ", tmallPromEndTime='" + tmallPromEndTime + '\'' +
                ", promPlanMsg='" + promPlanMsg + '\'' +
                ", addPromTime='" + addPromTime + '\'' +
                '}';
    }
}
