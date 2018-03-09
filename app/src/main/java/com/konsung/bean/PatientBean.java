package com.konsung.bean;

import com.greendao.dao.DaoSession;
import com.greendao.dao.MeasureDataBeanDao;
import com.greendao.dao.PatientBeanDao;
import com.konsung.R;
import com.konsung.anotation.ViewMapping;
import com.konsung.util.UUIDGenerator;
import com.konsung.util.UiUitls;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * 病人信息类
 * 使用Ormlite 映射到数据库
 *
 * @author ouyangfan
 * @version 0.0.1
 */
@Entity
public class PatientBean {

    @Property(nameInDb = "id")
    @Id(autoincrement = true)
    private Long id;

    @ToMany(referencedJoinProperty = "patientId")//指定与之关联的其他类的id
    private List<MeasureDataBean> measures;


    @CSVAnnotation(name = "姓名", priority = 0)
    private String name = "";
    

    private String num = ""; //编号

    private String idCard = ""; //更改为唯一标识不做身份证识别

    @CSVAnnotation(name = "身份证", priority = 1)
    private String card = ""; //身份证标识

    private String idByServer = "";
    

    private String orgId = "";
    

    private String address = "";
    

    @CSVAnnotation(name = "腰围", priority = 5)
    private String waist = "";
    

    @CSVAnnotation(name = "臀围", priority = 5)
    private String hipline = "";
    

    @CSVAnnotation(name = "性别", type = InfoType.SEX, priority = 2)
    private int sex = 0;
    

    @CSVAnnotation(name = "年龄", priority = 3)
    private int age = -1;
    

    @CSVAnnotation(name = "血型", type = InfoType.BLOOD, priority = 4)
    private int blood = 0;
    

    @CSVAnnotation(name = "身高", priority = 4)
    private float height = 0f;
    

    @CSVAnnotation(name = "体重", priority = 4)
    private float weight = 0f;


    private Date birthday;

    
    private String phone = ""; //固话

    
    private String remark = ""; //备注


    // TODO: 2017/10/17 0017  1.3.0新加字段，后面统一数据库升级加字段
    @CSVAnnotation(name = "会员卡", priority = 1)
    private String memberShipCard = "";

    //头像字符串
    private String bmpStr = "";
    //导入的头像字符串
    private String headBmpStr = "";


    private boolean isSelected = false;


    private String company = "";
    

    private String selfmobile = "";
    

    private String contact = "";
    

    private String contactmobile = "";
    

    private String resident = "";
    

    private String nationality = "0";
    

    private String nationalityname = "";
    

    private String bloodrh = "";
    

    private String education = "";
    

    private String carrer = "";
    

    private String marriage = "";

    

    @ViewMapping(viewIds = {R.id.payType_1, R.id.payType_2, R.id.payType_3,
            R.id.payType_4, R.id.payType_5, R.id.payType_6, R.id.payType_7,
            R.id.payType_8})
    private String payType = "";
    

    private String payTypeother = "";
    //药物过敏
    

    @ViewMapping(viewIds = {R.id.allergy_1, R.id.allergy_2, R.id.allergy_3, R
            .id.allergy_4, R.id.allergy_5})
    private String allergy = "";
    

    private String allergyname = "";
    //暴露
    

    @ViewMapping(viewIds = {R.id.expose_1, R.id.expose_2, R.id.expose_3, R.id
            .expose_4, })
    private String expose = "";

    

    private String diseasename = "";
    

    private String diseasename2 = "";
    

    private String diseasename3 = "";

    //疾病
    

    @ViewMapping(viewIds = {R.id.disease_1, R.id.disease_2, R.id.disease_3, R
            .id.disease_4, R.id.disease_5, R.id.disease_6,
            R.id.disease_7, R.id.disease_8, R.id.disease_9, R.id.disease_10,
            R.id.disease_11, R.id.disease_12, R.id.disease_13})
    private String disease = "";
    

    private Date disease_time;

    

    private String disease2 = "";
    

    private Date disease_time2;

    

    private String disease3 = "";
    

    private Date disease_time3;

    

    private String disease4 = "";
    

    private Date disease_time4;

    

    private String disease5 = "";
    

    private Date disease_time5;

    

    private String disease6 = "";
    

    private Date disease_time6;


    

    private String operation = "";
    

    private String operationname = "";
    

    private Date operation_time;
    

    private String operation2 = "";
    

    private String operationname2 = "";
    

    private Date operation_time2;


    

    private String trauma = "";
    

    private String traumaname = "";
    

    private Date trauma_time;
    

    private String trauma2 = "";
    

    private String traumaname2 = "";
    

    private Date trauma_time2;


    

    private String transfusation = "";
    

    private String transfusationname = "";
    

    private Date transfusation_time;
    

    private String transfusation2 = "";
    

    private String transfusationname2 = "";
    

    private Date transfusation_time2;

    //家族病史
    

    private String homeother = "";

    

    @ViewMapping(viewIds = {R.id.father_dis_1, R.id.father_dis_2, R.id
            .father_dis_3, R.id.father_dis_4, R.id.father_dis_5, R.id
            .father_dis_6, R.id.father_dis_7, R.id.father_dis_8,
            R.id.father_dis_9, R.id.father_dis_10, R.id.father_dis_11,
            R.id.father_dis_12})
    private String father_dis = "";
    

    private String father_dis_other = "";

    

    @ViewMapping(viewIds = {R.id.mother_dis_1, R.id.mother_dis_2, R.id
            .mother_dis_3, R.id.mother_dis_4, R.id.mother_dis_5, R.id
            .mother_dis_6, R.id.mother_dis_7, R.id.mother_dis_8,
            R.id.mother_dis_9, R.id.mother_dis_10, R.id.mother_dis_11,
            R.id.mother_dis_12})
    private String mother_dis = "";
    

    private String mother_dis_other = "";

    

    @ViewMapping(viewIds = {R.id.brother_dis_1, R.id.brother_dis_2, R.id
            .brother_dis_3, R.id.brother_dis_4, R.id.brother_dis_5, R.id
            .brother_dis_6, R.id.brother_dis_7, R.id.brother_dis_8,
            R.id.brother_dis_9, R.id.brother_dis_10, R.id.brother_dis_11,
            R.id.brother_dis_12})
    private String brother_dis = "";
    

    private String brother_dis_other = "";

    

    @ViewMapping(viewIds = {R.id.child_dis_1, R.id.child_dis_2, R.id
            .child_dis_3, R.id.child_dis_4, R.id.child_dis_5, R.id.child_dis_6,
            R.id.child_dis_7, R.id.child_dis_8, R.id.child_dis_9, R.id
            .child_dis_10, R.id.child_dis_11, R.id.child_dis_12})
    private String child_dis = "";
    

    private String child_dis_other = "";

    public String getFather_dis_other() {
        return father_dis_other;
    }

    public void setFather_dis_other(String father_dis_other) {
        this.father_dis_other = father_dis_other;
    }

    public String getMother_dis_other() {
        return mother_dis_other;
    }

    public void setMother_dis_other(String mother_dis_other) {
        this.mother_dis_other = mother_dis_other;
    }

    public String getBrother_dis_other() {
        return brother_dis_other;
    }

    public void setBrother_dis_other(String brother_dis_other) {
        this.brother_dis_other = brother_dis_other;
    }

    public String getChild_dis_other() {
        return child_dis_other;
    }

    public void setChild_dis_other(String child_dis_other) {
        this.child_dis_other = child_dis_other;
    }


    //遗传病史
    

    private String inherited = "";
    

    private String inheritedname = "";

    //残疾情况
    

    @ViewMapping(viewIds = {R.id.disability_1, R.id.disability_2, R.id
            .disability_3, R.id.disability_4, R.id.disability_5,
            R.id.disability_6, R.id.disability_7, R.id.disability_8})
    private String disability = "";
    

    private String disabilityname = "";

    //生活环境
    

    private String kitchen_en = "";
    

    private String fuel_en = "";
    

    private String water_en = "";
    

    private String toilet_en = "";
    

    private String livestock_en = "";


    

    private boolean isUpdata = true;//true还没上传

    //世轩上传,true还没上传
    

    private boolean updataToSX = true;


    

    private int patient_type = 0;

    //增加时间段，显示用户时按时间先后排序
    
    private Date sortDate = new Date();

    public Date getSortDate() {
        return sortDate;
    }

    public void setSortDate(Date sortDate) {
        this.sortDate = sortDate;
    }


    public void setDate3(int i, Date date) {
        switch (i) {
            case 0:
                disease_time = date;
                break;
            case 1:
                disease_time2 = date;

                break;
            case 2:
                disease_time3 = date;

                break;
            case 3:
                disease_time4 = date;

                break;
            case 4:
                disease_time5 = date;

                break;
            case 5:
                disease_time6 = date;

                break;
            case 6:
                operation_time = date;
                break;
            case 7:
                operation_time2 = date;
                break;
            case 8:
                trauma_time = date;
                break;
            case 9:
                trauma_time2 = date;
                break;
            case 10:
                transfusation_time = date;
                break;
            case 11:
                transfusation_time2 = date;
                break;

            default:
                break;

        }
    }

    public boolean getIsUpdata() {
        return isUpdata;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setUpdata(boolean updata) {
        isUpdata = updata;
    }

    public String getDownloadResult() {
        return downloadResult;
    }

    public void setDownloadResult(String downloadResult) {
        this.downloadResult = downloadResult;
    }

    public void setIsUpdata(boolean isUpdata) {
        this.isUpdata = isUpdata;

    }

    public PatientBean() {
        // for ormlite
        try {
            Date date = UiUitls.getDateFormat(UiUitls.DateState.LONG)
                    .parse("1970-01-01 08:00:00");
            birthday = date;
            disease_time = date;
            disease_time2 = date;
            disease_time3 = date;
            disease_time4 = date;
            disease_time5 = date;
            disease_time6 = date;
            trauma_time = date;
            trauma_time2 = date;
            transfusation_time2 = date;
            transfusation_time = date;
            operation_time = date;
            operation_time2 = date;
            idCard = UUIDGenerator.getUUID();
        } catch (ParseException e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
        }

    }

    @Generated(hash = 417244493)
    public PatientBean(Long id, String name, String num, String idCard, String card, String idByServer,
            String orgId, String address, String waist, String hipline, int sex, int age, int blood,
            float height, float weight, Date birthday, String phone, String remark,
            String memberShipCard, String bmpStr, String headBmpStr, boolean isSelected, String company,
            String selfmobile, String contact, String contactmobile, String resident,
            String nationality, String nationalityname, String bloodrh, String education, String carrer,
            String marriage, String payType, String payTypeother, String allergy, String allergyname,
            String expose, String diseasename, String diseasename2, String diseasename3, String disease,
            Date disease_time, String disease2, Date disease_time2, String disease3, Date disease_time3,
            String disease4, Date disease_time4, String disease5, Date disease_time5, String disease6,
            Date disease_time6, String operation, String operationname, Date operation_time,
            String operation2, String operationname2, Date operation_time2, String trauma,
            String traumaname, Date trauma_time, String trauma2, String traumaname2, Date trauma_time2,
            String transfusation, String transfusationname, Date transfusation_time,
            String transfusation2, String transfusationname2, Date transfusation_time2,
            String homeother, String father_dis, String father_dis_other, String mother_dis,
            String mother_dis_other, String brother_dis, String brother_dis_other, String child_dis,
            String child_dis_other, String inherited, String inheritedname, String disability,
            String disabilityname, String kitchen_en, String fuel_en, String water_en, String toilet_en,
            String livestock_en, boolean isUpdata, boolean updataToSX, int patient_type, Date sortDate,
            String uploadFlag, String downloadFlag, String uploadResult, String downloadResult) {
        this.id = id;
        this.name = name;
        this.num = num;
        this.idCard = idCard;
        this.card = card;
        this.idByServer = idByServer;
        this.orgId = orgId;
        this.address = address;
        this.waist = waist;
        this.hipline = hipline;
        this.sex = sex;
        this.age = age;
        this.blood = blood;
        this.height = height;
        this.weight = weight;
        this.birthday = birthday;
        this.phone = phone;
        this.remark = remark;
        this.memberShipCard = memberShipCard;
        this.bmpStr = bmpStr;
        this.headBmpStr = headBmpStr;
        this.isSelected = isSelected;
        this.company = company;
        this.selfmobile = selfmobile;
        this.contact = contact;
        this.contactmobile = contactmobile;
        this.resident = resident;
        this.nationality = nationality;
        this.nationalityname = nationalityname;
        this.bloodrh = bloodrh;
        this.education = education;
        this.carrer = carrer;
        this.marriage = marriage;
        this.payType = payType;
        this.payTypeother = payTypeother;
        this.allergy = allergy;
        this.allergyname = allergyname;
        this.expose = expose;
        this.diseasename = diseasename;
        this.diseasename2 = diseasename2;
        this.diseasename3 = diseasename3;
        this.disease = disease;
        this.disease_time = disease_time;
        this.disease2 = disease2;
        this.disease_time2 = disease_time2;
        this.disease3 = disease3;
        this.disease_time3 = disease_time3;
        this.disease4 = disease4;
        this.disease_time4 = disease_time4;
        this.disease5 = disease5;
        this.disease_time5 = disease_time5;
        this.disease6 = disease6;
        this.disease_time6 = disease_time6;
        this.operation = operation;
        this.operationname = operationname;
        this.operation_time = operation_time;
        this.operation2 = operation2;
        this.operationname2 = operationname2;
        this.operation_time2 = operation_time2;
        this.trauma = trauma;
        this.traumaname = traumaname;
        this.trauma_time = trauma_time;
        this.trauma2 = trauma2;
        this.traumaname2 = traumaname2;
        this.trauma_time2 = trauma_time2;
        this.transfusation = transfusation;
        this.transfusationname = transfusationname;
        this.transfusation_time = transfusation_time;
        this.transfusation2 = transfusation2;
        this.transfusationname2 = transfusationname2;
        this.transfusation_time2 = transfusation_time2;
        this.homeother = homeother;
        this.father_dis = father_dis;
        this.father_dis_other = father_dis_other;
        this.mother_dis = mother_dis;
        this.mother_dis_other = mother_dis_other;
        this.brother_dis = brother_dis;
        this.brother_dis_other = brother_dis_other;
        this.child_dis = child_dis;
        this.child_dis_other = child_dis_other;
        this.inherited = inherited;
        this.inheritedname = inheritedname;
        this.disability = disability;
        this.disabilityname = disabilityname;
        this.kitchen_en = kitchen_en;
        this.fuel_en = fuel_en;
        this.water_en = water_en;
        this.toilet_en = toilet_en;
        this.livestock_en = livestock_en;
        this.isUpdata = isUpdata;
        this.updataToSX = updataToSX;
        this.patient_type = patient_type;
        this.sortDate = sortDate;
        this.uploadFlag = uploadFlag;
        this.downloadFlag = downloadFlag;
        this.uploadResult = uploadResult;
        this.downloadResult = downloadResult;
    }

    public boolean isUpdataToSX() {
        return updataToSX;
    }

    public void setUpdataToSX(boolean updataToSX) {
        this.updataToSX = updataToSX;
    }

    public int getPatient_type() {
        return patient_type;
    }

    public void setPatient_type(int patient_type) {
        this.patient_type = patient_type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getBlood() {
        return blood;
    }

    public void setBlood(int blood) {
        this.blood = blood;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSelfmobile() {
        return selfmobile;
    }

    public void setSelfmobile(String selfmobile) {
        this.selfmobile = selfmobile;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContactmobile() {
        return contactmobile;
    }

    public void setContactmobile(String contactmobile) {
        this.contactmobile = contactmobile;
    }

    public String getResident() {
        return resident;
    }

    public void setResident(String resident) {
        this.resident = resident;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getBloodrh() {
        return bloodrh;
    }

    public void setBloodrh(String bloodrh) {
        this.bloodrh = bloodrh;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getCarrer() {
        return carrer;
    }

    public void setCarrer(String carrer) {
        this.carrer = carrer;
    }

    public String getMarriage() {
        return marriage;
    }

    public void setMarriage(String marriage) {
        this.marriage = marriage;
    }


    public String getExpose() {
        return expose;
    }

    public void setExpose(String expose) {
        this.expose = expose;
    }


    public String getInherited() {
        return inherited;
    }

    public void setInherited(String inherited) {
        this.inherited = inherited;
    }


    public String getKitchen_en() {
        return kitchen_en;
    }

    public void setKitchen_en(String kitchen_en) {
        this.kitchen_en = kitchen_en;
    }

    public String getFuel_en() {
        return fuel_en;
    }

    public void setFuel_en(String fuel_en) {
        this.fuel_en = fuel_en;
    }

    public String getWater_en() {
        return water_en;
    }

    public void setWater_en(String water_en) {
        this.water_en = water_en;
    }

    public String getToilet_en() {
        return toilet_en;
    }

    public void setToilet_en(String toilet_en) {
        this.toilet_en = toilet_en;
    }

    public String getLivestock_en() {
        return livestock_en;
    }

    public void setLivestock_en(String livestock_en) {
        this.livestock_en = livestock_en;
    }

    public boolean isUpdata() {
        return isUpdata;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getAllergy() {
        return allergy;
    }

    public void setAllergy(String allergy) {
        this.allergy = allergy;
    }


    public String getDisability() {
        return disability;
    }

    public void setDisability(String disability) {
        this.disability = disability;
    }

    public String getNationalityname() {
        return nationalityname;
    }

    public void setNationalityname(String nationalityname) {
        this.nationalityname = nationalityname;
    }

    public String getPayTypeother() {
        return payTypeother;
    }

    public void setPayTypeother(String payTypeother) {
        this.payTypeother = payTypeother;
    }

    public String getAllergyname() {
        return allergyname;
    }

    public void setAllergyname(String allergyname) {
        this.allergyname = allergyname;
    }

    public String getInheritedname() {
        return inheritedname;
    }

    public void setInheritedname(String inheritedname) {
        this.inheritedname = inheritedname;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getDiseasename() {
        return diseasename;
    }

    public void setDiseasename(String diseasename) {
        this.diseasename = diseasename;
    }

    public Date getDisease_time() {
        return disease_time;
    }

    public void setDisease_time(Date disease_time) {
        this.disease_time = disease_time;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperationname() {
        return operationname;
    }

    public void setOperationname(String operationname) {
        this.operationname = operationname;
    }

    public Date getOperation_time() {
        return operation_time;
    }

    public void setOperation_time(Date operation_time) {
        this.operation_time = operation_time;
    }

    public String getTrauma() {
        return trauma;
    }

    public void setTrauma(String trauma) {
        this.trauma = trauma;
    }

    public String getTraumaname() {
        return traumaname;
    }

    public void setTraumaname(String traumaname) {
        this.traumaname = traumaname;
    }

    public Date getTrauma_time() {
        return trauma_time;
    }

    public void setTrauma_time(Date trauma_time) {
        this.trauma_time = trauma_time;
    }

    public String getTransfusation() {
        return transfusation;
    }

    public void setTransfusation(String transfusation) {
        this.transfusation = transfusation;
    }

    public String getTransfusationname() {
        return transfusationname;
    }

    public void setTransfusationname(String transfusationname) {
        this.transfusationname = transfusationname;
    }

    public Date getTransfusation_time() {
        return transfusation_time;
    }

    public void setTransfusation_time(Date transfusation_time) {
        this.transfusation_time = transfusation_time;
    }

    public String getFather_dis() {
        return father_dis;
    }

    public void setFather_dis(String father_dis) {
        this.father_dis = father_dis;
    }

    public String getMother_dis() {
        return mother_dis;
    }

    public void setMother_dis(String mother_dis) {
        this.mother_dis = mother_dis;
    }

    public String getBrother_dis() {
        return brother_dis;
    }

    public void setBrother_dis(String brother_dis) {
        this.brother_dis = brother_dis;
    }

    public String getChild_dis() {
        return child_dis;
    }

    public void setChild_dis(String child_dis) {
        this.child_dis = child_dis;
    }

    public String getDisabilityname() {
        return disabilityname;
    }

    public void setDisabilityname(String disabilityname) {
        this.disabilityname = disabilityname;
    }

    public String getDisease2() {
        return disease2;
    }

    public void setDisease2(String disease2) {
        this.disease2 = disease2;
    }

    public String getDiseasename2() {
        return diseasename2;
    }

    public void setDiseasename2(String diseasename2) {
        this.diseasename2 = diseasename2;
    }

    public Date getDisease_time2() {
        return disease_time2;
    }

    public void setDisease_time2(Date disease_time2) {
        this.disease_time2 = disease_time2;
    }

    public String getDisease3() {
        return disease3;
    }

    public void setDisease3(String disease3) {
        this.disease3 = disease3;
    }

    public String getDiseasename3() {
        return diseasename3;
    }

    public void setDiseasename3(String diseasename3) {
        this.diseasename3 = diseasename3;
    }

    public Date getDisease_time3() {
        return disease_time3;
    }

    public void setDisease_time3(Date disease_time3) {
        this.disease_time3 = disease_time3;
    }

    public String getDisease4() {
        return disease4;
    }

    public void setDisease4(String disease4) {
        this.disease4 = disease4;
    }


    public Date getDisease_time4() {
        return disease_time4;
    }

    public void setDisease_time4(Date disease_time4) {
        this.disease_time4 = disease_time4;
    }

    public String getDisease5() {
        return disease5;
    }

    public void setDisease5(String disease5) {
        this.disease5 = disease5;
    }


    public Date getDisease_time5() {
        return disease_time5;
    }

    public void setDisease_time5(Date disease_time5) {
        this.disease_time5 = disease_time5;
    }

    public String getDisease6() {
        return disease6;
    }

    public void setDisease6(String disease6) {
        this.disease6 = disease6;
    }


    public Date getDisease_time6() {
        return disease_time6;
    }

    public void setDisease_time6(Date disease_time6) {
        this.disease_time6 = disease_time6;
    }

    public String getOperation2() {
        return operation2;
    }

    public void setOperation2(String operation2) {
        this.operation2 = operation2;
    }

    public String getOperationname2() {
        return operationname2;
    }

    public void setOperationname2(String operationname2) {
        this.operationname2 = operationname2;
    }

    public Date getOperation_time2() {
        return operation_time2;
    }

    public void setOperation_time2(Date operation_time2) {
        this.operation_time2 = operation_time2;
    }

    public String getTrauma2() {
        return trauma2;
    }

    public void setTrauma2(String trauma2) {
        this.trauma2 = trauma2;
    }

    public String getTraumaname2() {
        return traumaname2;
    }

    public void setTraumaname2(String traumaname2) {
        this.traumaname2 = traumaname2;
    }

    public Date getTrauma_time2() {
        return trauma_time2;
    }

    public void setTrauma_time2(Date trauma_time2) {
        this.trauma_time2 = trauma_time2;
    }

    public String getTransfusation2() {
        return transfusation2;
    }

    public void setTransfusation2(String transfusation2) {
        this.transfusation2 = transfusation2;
    }

    public String getTransfusationname2() {
        return transfusationname2;
    }

    public void setTransfusationname2(String transfusationname2) {
        this.transfusationname2 = transfusationname2;
    }

    public Date getTransfusation_time2() {
        return transfusation_time2;
    }

    public void setTransfusation_time2(Date transfusation_time2) {
        this.transfusation_time2 = transfusation_time2;
    }

    public String getHomeother() {
        return homeother;
    }

    public void setHomeother(String homeother) {
        this.homeother = homeother;
    }

    public String getIdByServer() {
        return idByServer;
    }

    public void setIdByServer(String idByServer) {
        this.idByServer = idByServer;
    }

    public String getDownloadFlag() {
        return downloadFlag;
    }

    public void setDownloadFlag(String downloadFlag) {
        this.downloadFlag = downloadFlag;
    }

    public String getUploadResult() {
        return uploadResult;
    }

    public void setUploadResult(String uploadResult) {
        this.uploadResult = uploadResult;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    private String uploadFlag;
    

    private String downloadFlag;
    

    private String uploadResult;
    private String downloadResult;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1766472073)
    private transient PatientBeanDao myDao;

    public String getUploadFlag() {
        return uploadFlag;
    }

    public void setUploadFlag(String uploadFlag) {
        this.uploadFlag = uploadFlag;
    }

    public String getWaist() {
        return waist;
    }

    public void setWaist(String waist) {
        this.waist = waist;
    }

    public String getHipline() {
        return hipline;
    }

    public void setHipline(String hipline) {
        this.hipline = hipline;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMemberShipCard() {
        return memberShipCard;
    }

    public void setMemberShipCard(String memberShipCard) {
        this.memberShipCard = memberShipCard;
    }

    public String getBmpStr() {
        return bmpStr;
    }

    public void setBmpStr(String bmpStr) {
        this.bmpStr = bmpStr;
    }

    public boolean getUpdataToSX() {
        return this.updataToSX;
    }
    public String getHeadBmpStr() {
        return headBmpStr;
    }

    public void setHeadBmpStr(String headBmpStr) {
        this.headBmpStr = headBmpStr;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 413981566)
    public List<MeasureDataBean> getMeasures() {
        if (measures == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MeasureDataBeanDao targetDao = daoSession.getMeasureDataBeanDao();
            List<MeasureDataBean> measuresNew = targetDao
                    ._queryPatientBean_Measures(id);
            synchronized (this) {
                if (measures == null) {
                    measures = measuresNew;
                }
            }
        }
        return measures;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 369090793)
    public synchronized void resetMeasures() {
        measures = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1644017270)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPatientBeanDao() : null;
    }

    public void setMeasures(List<MeasureDataBean> measures) {
        this.measures = measures;
    }
}