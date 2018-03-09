package com.konsung.util.global;

/**
 * 血蛋白常量
 *
 * @author yuchunhui
 * @time
 */

public class BloodReference {

    //成年男性红细胞积压值
    public static final int HTC_MALE_HIGH = 50;
    public static final int HTC_MALE_LOW = 40;

    //普通人红细胞积压值
    public static final int HTC_NORMAL_HIGH = 50;
    public static final int HTC_NORMAL_LOW = 40;

    //成年女性红细胞积压值
    public static final int HTC_FEMALE_HIGH = 45;
    public static final int HTC_FEMALE_LOW = 37;

    /**
     * 成年男性:120～160g/L
     * 成年女性:110～150g/L
     * 新生儿:170～200g/L
     * 青少年（儿童）:110～160g/L
     * 1L=10dL
     * 当前单位是g/dL
     */
    public static final float HGB_MALE_HIGH = 10.5f;
    public static final float HGB_MALE_LOW = 8.1f;

    public static final float HGB_FEMALE_HIGH = 9.3f;
    public static final float HGB_FEMALE_LOW = 7.4f;

    public static final float HGB_YOUTH_HIGH = 8.7f;
    public static final float HGB_YOUTH_LOW = 6.8f;

    public static final float HGB_BABY_HIGH = 12.4f;
    public static final float HGB_BABY_LOW = 10.5f;
}
