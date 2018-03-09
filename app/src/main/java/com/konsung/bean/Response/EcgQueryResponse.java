package com.konsung.bean.Response;

import java.util.List;

/**
 * <p>Title:EcgQueryResponse </p>
 * <p>Description: 心电诊断结果查询返回对象</p>
 * <p>Company: Konsung</p>
 * 远程心电结果类
 * @author  HWB
 */
public class EcgQueryResponse extends BaseResponse {

    //总记录数
    public int total;
    //具体数据集合
    public List<RowData> rows;

    /**
     * 心电状态类
     */
    public class RowData {
        /** 1. [可空] 测量记录业务主键 */
        public String equipmentDataId;

        /** 2. [可空] 申请日期 */
        public String requestDate;

        /**3. 诊断医生名字**/
        public String conDoctorName;

        /** 4. [可空] 诊断 结果*/
        public String conResult;

        /** 5. [可空] 诊断日期 */
        public String conResultDate;

        /** 6. [可空] 诊断建议 */
        public String conAdvice;

        /**7.姓名**/
        public String name;

        /**8 性别**/
        public String sex;

        /**9 身份证**/
        public String idNumber;

        /**10联系方式**/
        public String telephone;

        /**11处理状态**/
        public String conState;

        /** 12. [可空] 心率值 */
        public String hr;

        /** 13. [可空] 脉率值 */
        public String pr;

        /** 14. [可空] 心电QRS值 */
        public String qrs;

        /** 15. [可空] 心电QT/QTC值 */
        public String qtQtc;

        /** 16. [可空] 心电P/QRS/T值 */
        public String pQrsT;

        /** 17. [可空] 心电RV5/SV1 */
        public String rv5Sv1;
    }
}
