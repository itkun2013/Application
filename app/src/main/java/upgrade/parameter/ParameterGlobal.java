package upgrade.parameter;

/**
 * 一些用于参数板升级的常量
 * @author yuchunhui
 **/
public class ParameterGlobal {
    /**
     * 参数板是否需要升级sp字段
     * 配置文件
     * GlobalConstant.APP_CONFIG
     * true  = 需要升级
     * false = 不需要升级
     */
    public static final String SP_KEY_CSB_UPDATE = "csb_update";

    /**
     * 检测参数板是否需要校验升级
     * true  = 参数板已经升级，需要校验升级
     * false = 不需要校验
     */
    public static final String SP_KEY_CSB_CHECK_VERSION = "csb_check";

    /**
     * 参数板升级文件名
     */
    public static final String SP_KEY_CSB_FILE_NAME = "csb_name";
    /**
     * 参数板升级文件名，固定的，lib下已经固定，如果不是这个名字，会返回-1
     */
    public static final String CSB_NAME = "KS-boot.image";

    /**
     * 参数版升级文件名保存标记
     */
    public static final String CSB_NAME_FLAG = "csb_name_flag";

}
