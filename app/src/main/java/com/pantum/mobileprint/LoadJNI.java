package com.pantum.mobileprint;

public class LoadJNI
{

	private static String libJNIName = "PT_MobilePrint_JNILib";
	//private static String libJNIName = "CPlatform_MobilePrint_JNILib";
	//private static String libJNIName = "MPlatform_MobilePrint_JNILib";

	static {
		System.loadLibrary(libJNIName);
	}
	
	/**
	 * 初始化LLD
	 * 
	 * @param (String)json
	 *			JSON格式字符串
	 * @param (int)jsonCount
	 *			JSON中Key-Value键值对个数
	 * 
	 * @return (int)初始化结果
	 * 			1:初始化成功；0:初始化失败；	
	 */
	public native int initLLD(String json, int keyValueCount);
	
	public native int connectToServer(String serverIp, int serverPort, int isCreateSocket);

	public native int closeConnectToServer();
	
	/**
	 * 搜索同一网段中的打印机
	 * 
	 * @param serverIp
	 *            打印机IP
	 * 
	 * @return JSON格式的Key-Value字符串；(eg:{})
	 */
	public native byte[] searchNetPrinter(String localIp);

	/**
	 * 发送打印数据
	 * 
	 * @param serverIp
	 *            打印机IP
	 * @param serverPort
	 *            打印机端口
	 * @param printPageWidth
	 *            可打印纸张宽度
	 * @param printPageHeight
	 *            可印印纸张高度
	 * @param printNumCopies
	 *            打印份数
	 * @param srcImgDataSize
	 *            图像大小(纯图像数据大小)
	 * @param srcImgWidthPixel
	 *            图像宽像数点大小
	 * @param srcImgHeightPixel
	 *            图像高像数点大小
	 * @param srcImgBitCount
	 *            图像位深
	 * @param isCloudPrint
	 *            是否云打印 (0:本地打印；1:云打印)
	 * @param cloudPrintType
	 *            云打印类型（1:京东、2:百度，3:谷歌......）
	 * 
	 * @return >0 发送打印数据成功 -1 打印失败 -2 打印取消成功 -11 与打印机连接通信失败 -12 创建打印机连接失败 <12
	 *         打印未知错误
	 */
	public native int sendDataToServer(String serverIp, int serverPort, int printPageWidth, int printPageHeight,
			int printNumCopies, int printPages, int srcImgDataSize, int srcImgWidthPixel, int srcImgHeightPixel,
			int srcImgBitCount, int isCloudPrint, int cloudPrintType);

	
	public native byte[] getPrintJobData();
	
	/**
	 * 取消打印作业
	 * 
	 * @param serverIp
	 *            打印机IP
	 * @return 1 打印作业取消成功 -1 打印作业取消失败
	 */
	public native int cancelSendDataToServer();

	/**
	 * 组包
	 * 
	 * @param packetIn
	 *            [] 每个小包数据
	 * @param packetInSize
	 *            每个小包数据大小
	 * @param srcImgPacketInSize
	 *            每个原图小包数据大小
	 * @param srcImgPackageInSize
	 *            每个原图大小(未拆分为小包的数据大小)
	 * @return >0 每个小包成功组包的大小
	 */
	public native int combinationPackage(char packetIn[], int packetInSize, int srcImgPacketInSize, int srcImgPackageInSize);

	/**
	 * 获取打印作业是否已完成
	 * 
	 * @return >0 当前作业已完成发送 =0 当前作业未完成发送
	 */
	public native int getCurrentPrintFinishState();

	/**
	 * 初始化低层开关
	 */
	public native void initParameter();

	/**
	 * 设置Log文件的是否开启
	 * 
	 * @param isOpenOrCloseLog
	 *            打开或关闭
	 * @param className
	 *            指定某类中输出
	 * @param functionName
	 *            指定某类某函数输出
	 * @param logFileName
	 *            Log文件名
	 * @return
	 */
	public native int setOutLogFile(int isOpenOrCloseLog, String className, String functionName, String logFileName);

}
