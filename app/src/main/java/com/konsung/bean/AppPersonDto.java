package com.konsung.bean;

/**
* <p>Title:AppPersonDto </p>
* <p>Description: 返回app客户端病人信息对象</p>
* <p>Company: Konsung</p>
* @author  HWB
 */

public class AppPersonDto {

    /**数据id**/
    private String dataId;

    /**身份证--不能为空**/
    private String cardNo;

    /**姓名--不能为空**/
    private String name;

    /**性别--不能为空 0 1 2 9**/
    private String sex;

    /**年龄**/
    private String age;

    /**手机号码**/
    private String telePhone;

    /**病人类型**/
    private String patientType;

    /**居住地址**/
    private String address;

    /**备注**/
    private String desc;

    /**会员卡**/
    private String  membershipCard;

    /**用户头像**/
    private String  headPortrait;

    /**血型**/
    private String bloodType;

    /**体检时间,精确到秒**/
    private String healthTime;

    /**
     * 获取数据id
     * @return 数据id
     */
    public String getDataId() {
        return dataId;
    }

    /**
     * 设置数据id
     * @param dataId 数据id
     */
    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    /**
     * 获取身份证号
     * @return 身份证号
     */
    public String getCardNo() {
        return cardNo;
    }

    /**
     * 设置身份证号
     * @param cardNo 身份证号
     */
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    /**
     * 获取姓名
     * @return 姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置姓名
     * @param name 姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取性别
     * @return 性别
     */
    public String getSex() {
        return sex;
    }

    /**
     * 设置性别
     * @param sex 性别
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * 获取年龄
     * @return 年龄
     */
    public String getAge() {
        return age;
    }

    /**
     * 设置年龄
     * @param age 年龄
     */
    public void setAge(String age) {
        this.age = age;
    }

    /**
     * 获取电话
     * @return 电话
     */
    public String getTelePhone() {
        return telePhone;
    }

    /**
     * 设置电话号码
     * @param telePhone 电话号码
     */
    public void setTelePhone(String telePhone) {
        this.telePhone = telePhone;
    }

    /**
     * 获取病人类型
     * @return 病人类型
     */
    public String getPatientType() {
        return patientType;
    }

    /**
     * 设置病人类型
     * @param patientType 病人类型
     */
    public void setPatientType(String patientType) {
        this.patientType = patientType;
    }

    /**
     * 获取地址
     * @return 地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置地址
     * @param address 地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 获取描述信息
     * @return 描述信息
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 设置描述信息
     * @param desc 描述信息
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * 获取会员卡号
     * @return 会员卡号
     */
    public String getMembershipCard() {
        return membershipCard;
    }

    /**
     * 设置会员卡号
     * @param membershipCard 会员卡号
     */
    public void setMembershipCard(String membershipCard) {
        this.membershipCard = membershipCard;
    }

    /**
     * 获取用户头像
     * @return 头像字符串
     */
    public String getHeadPortrait() {
        return headPortrait;
    }

    /**
     * 设置用户头像
     * @param headPortrait 头像字符串
     */
    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    /**
     * 获取血型
     * @return 血型
     */
    public String getBloodType() {
        return bloodType;
    }

    /**
     * 设置血型
     * @param bloodType 血型
     */
    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    /**
     * 获取体检时间
     * @return 体检时间
     */
    public String getHealthTime() {
        return healthTime;
    }

    /**
     * 设置体检时间
     * @param healthTime 体检时间
     */
    public void setHealthTime(String healthTime) {
        this.healthTime = healthTime;
    }
}
