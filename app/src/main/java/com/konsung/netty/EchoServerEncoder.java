package com.konsung.netty;


import com.konsung.util.GlobalConstant;

import java.nio.ByteOrder;
import java.util.ArrayList;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author ouyangfan
 * @version 0.0.1
 * @date 2015-01-12 18:46
 * netty 服务器编码器
 * 此类主要是进行编码操作
 * 发送数据给AppDevice
 */
public class EchoServerEncoder extends ChannelInboundHandlerAdapter {

    // mBuf包含帧头和帧体的每包数据
    private static ByteBuf mBuf;
    private static ArrayList<ByteBuf> cmds = new ArrayList<>();
    // 帧体
    private static ByteBuf mBodyBuf;

    // 序列号，从0开始递增
    private static int mSerialNo = 0;
    private static final int HEAD_SIZE = 8;
    private static boolean mStartNibp;

    /*
     * 发送网络数据
     * mBuf.clear() 只是将readerIndex和writerIndex 置为0，并不清空数据
     * @param cmdId 命令字ID,传递16进制数据
     * @param len 长度
     *
     */
    private static void sendData(byte cmdId, int len) {
        ByteBuf buff = Unpooled.buffer();
        // 帧头
        buff.writeByte(0xFF);
        buff.order(ByteOrder.LITTLE_ENDIAN).writeShort(HEAD_SIZE + len);
        buff.writeByte(cmdId);
        buff.order(ByteOrder.LITTLE_ENDIAN).writeShort(mSerialNo++);

        // 将读写指针放在校验位,将校验位补为0
        mBodyBuf.resetReaderIndex();
        buff.markWriterIndex();
        buff.order(ByteOrder.LITTLE_ENDIAN).writeShort(0);
        // 在校验位后加上帧体内容
        int sum = checkSum(buff.writeBytes(mBodyBuf));
        // 将写指针重置到校验位重新赋值
        buff.resetWriterIndex();
        buff.order(ByteOrder.LITTLE_ENDIAN).writeShort(sum);
        // 重新赋帧体内容
        mBodyBuf.resetReaderIndex();
        buff.writeBytes(mBodyBuf);

        cmds.add(buff);
//        mBuf.resetReaderIndex();
    }

    /*
     * 向网络发送NIBP配置包
     * @param cmdId 命令字ID
     * @param value 值
     */
    public static void setNibpConfig(short cmdId, int value) {
        mBodyBuf = Unpooled.buffer();
        mBodyBuf.markReaderIndex();
        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeShort(cmdId);
        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeInt(value);
        sendData(GlobalConstant.NET_NIBP_CONFIG, mBodyBuf.readableBytes());
    }

    /**
     * 发送病人信息包
     *
     * @param cycle   形态（成人，小儿，新生儿）
     * @param sex     性别
     * @param blood   血型
     * @param weight  体重
     * @param height  身高
     * @param isbegin 起搏
     */
    public static void setPatientConfig(short cycle, short sex, short blood,
                                        float weight, float height, short
                                                isbegin) {
        mBodyBuf = Unpooled.buffer();
        mBodyBuf.markReaderIndex();
        byte[] string64 = new byte[64];
        for (byte i : string64) {
            i = 0;
        }
        byte[] time = new byte[7];
        for (byte i : time) {
            i = 0;
        }


        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeBytes(string64);//设备ID默认空

        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeBytes(string64);//用户id默认空

        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeShort(0);

        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeByte(cycle);

        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeByte(sex);

        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeByte(blood);

        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeInt((int) weight * 10);

        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeInt((int) height * 10);

        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeBytes(time);

        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeBytes(time);

        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeByte(isbegin);//起搏

        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeBytes(string64);

        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeBytes(string64);

        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeBytes(string64);

        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeBytes(string64);

        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeBytes(string64);
        sendData(GlobalConstant.NET_PATIENT_CONFIG, mBodyBuf.readableBytes());


    }

    /*
 * 向网络发送ECG配置包
 * @param cmdId 命令字ID
 * @param value 值
 */
    public static void setEcgConfig(short cmdId, int value) {
        mBodyBuf = Unpooled.buffer();
        mBodyBuf.markReaderIndex();
        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeShort(cmdId);
        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeInt(value);
        sendData(GlobalConstant.NET_ECG_CONFIG, mBodyBuf.readableBytes());
    }

    /*
    * 向网络发送Spo2配置包
    * @param cmdId 命令字ID
    * @param value 值
    */
    public static void setSpo2Config(short cmdId, int value) {
        mBodyBuf = Unpooled.buffer();
        mBodyBuf.markReaderIndex();
        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeShort(cmdId);
        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeInt(value);
        sendData(GlobalConstant.NET_SPO2_CONFIG, mBodyBuf.readableBytes());
    }

    /*
    * 向网络发送Resp配置包
    * @param cmdId 命令字ID
    * @param value 值
    */
    public static void setRespConfig(short cmdId, int value) {
        mBodyBuf = Unpooled.buffer();
        mBodyBuf.markReaderIndex();
        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeShort(cmdId);
        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeInt(value);
        sendData(GlobalConstant.NET_RESP_CONFIG, mBodyBuf.readableBytes());
    }

    /*
    * 向网络发送TEMP配置包
    * @param cmdId 命令字ID
    * @param value 值
    */
    public static void setTempConfig(short cmdId, int value) {
        mBodyBuf = Unpooled.buffer();
        mBodyBuf.markReaderIndex();
        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeShort(cmdId);
        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeInt(value);
        sendData(GlobalConstant.NET_TEMP_CONFIG, mBodyBuf.readableBytes());
    }

    /*
    * 向网络发送设备配置包
    * @param cmdId 命令字ID
    * @param value 值
    */
    public static void setDeviceConfig(short cmdId, int value) {
        mBodyBuf = Unpooled.buffer();
        mBodyBuf.markReaderIndex();
        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeShort(cmdId);
        mBodyBuf.order(ByteOrder.LITTLE_ENDIAN).writeInt(value);
        sendData(GlobalConstant.NET_DEVICE_CONFIG, mBodyBuf.readableBytes());
    }

    /**
     * 校验和
     *
     * @return sum
     */
    private static int checkSum(ByteBuf buf) {
        int sum = 0;
        for (int i = 0; i < buf.capacity(); i++) {
            // 校验位以及头不参与计算
            if (i == 0 || i == 6 || i == 7) {
                continue;
            }
            sum += buf.getUnsignedByte(i);
        }
        return sum;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws
            Exception {
        if (cmds.isEmpty()) {
            return;
        }
        for (int i = 0; i < cmds.size(); i++) {
            System.out.println("send = " + ByteBufUtil.hexDump(cmds.get(i)));
            ctx.writeAndFlush(cmds.remove(i));
        }
        cmds.clear();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    }
}
