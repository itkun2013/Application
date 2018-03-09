package com.konsung.bean;

import java.util.ArrayList;

/**
 * 心电波形数据缓存Bean，预防测量心电时，因为意外退出测量而保存了心电波形数据
 */
public class EcgCacheBean {

    //字節緩存
    private ArrayList<String> wave1;
    private ArrayList<String> wave2;
    private ArrayList<String> wave3;
    private ArrayList<String> wave4;
    private ArrayList<String> wave5;
    private ArrayList<String> wave6;
    private ArrayList<String> wave7;
    private ArrayList<String> wave8;
    private ArrayList<String> wave9;
    private ArrayList<String> wave10;
    private ArrayList<String> wave11;
    private ArrayList<String> wave12;

    private String ecgWaveI = "";
    private String ecgWaveIi = "";
    private String ecgWaveIii = "";
    private String ecgWaveAvr = "";
    private String ecgWaveAvl = "";
    private String ecgWaveAvf = "";
    private String ecgWaveV1 = "";
    private String ecgWaveV2 = "";
    private String ecgWaveV3 = "";
    private String ecgWaveV4 = "";
    private String ecgWaveV5 = "";
    private String ecgWaveV6 = "";
    private static final int ECG_SIZE = 10;

    /**
     * 构造器
     */
    public EcgCacheBean() {
        wave1 = new ArrayList<>();
        wave2 = new ArrayList<>();
        wave3 = new ArrayList<>();
        wave4 = new ArrayList<>();
        wave5 = new ArrayList<>();
        wave6 = new ArrayList<>();
        wave7 = new ArrayList<>();
        wave8 = new ArrayList<>();
        wave9 = new ArrayList<>();
        wave10 = new ArrayList<>();
        wave11 = new ArrayList<>();
        wave12 = new ArrayList<>();
    }

    /**
     * 这里需要修改，ecg数据会一直发送，只需保存最后10秒的数据
     * @param param 参数
     * @param wave 波形数据
     */
    public void setEcgWave(int param, String wave) {
        switch (param) {
            case 1:
                wave1.add(wave);
                if (wave1.size() > ECG_SIZE) {
                    wave1.remove(0);
                }
                ecgWaveI = "";
                for (int i = 0; i < wave1.size(); i++) {
                    ecgWaveI += wave1.get(i);
                }
                break;
            case 2:
                wave2.add(wave);
                if (wave2.size() > ECG_SIZE) {

                    wave2.remove(0);
                }
                ecgWaveIi = "";
                for (int i = 0; i < wave2.size(); i++) {
                    ecgWaveIi += wave2.get(i);
                }
                break;
            case 3:
                wave3.add(wave);
                if (wave3.size() > ECG_SIZE) {
                    wave3.remove(0);
                }
                ecgWaveIii = "";
                for (int i = 0; i < wave3.size(); i++) {
                    ecgWaveIii += wave3.get(i);
                }
                break;
            case 4:
                wave4.add(wave);
                if (wave4.size() > ECG_SIZE) {
                    wave4.remove(0);
                }
                ecgWaveAvr = "";
                for (int i = 0; i < wave4.size(); i++) {
                    ecgWaveAvr += wave4.get(i);
                }
                break;
            case 5:
                wave5.add(wave);
                if (wave5.size() > ECG_SIZE) {
                    wave5.remove(0);
                }
                ecgWaveAvl = "";
                for (int i = 0; i < wave5.size(); i++) {
                    ecgWaveAvl += wave5.get(i);
                }
                break;
            case 6:
                wave6.add(wave);
                if (wave6.size() > ECG_SIZE) {
                    wave6.remove(0);
                }
                ecgWaveAvf = "";
                for (int i = 0; i < wave6.size(); i++) {
                    ecgWaveAvf += wave6.get(i);
                }
                break;
            case 7:
                wave7.add(wave);
                if (wave7.size() > ECG_SIZE) {
                    wave7.remove(0);
                }
                ecgWaveV1 = "";
                for (int i = 0; i < wave7.size(); i++) {
                    ecgWaveV1 += wave7.get(i);
                }
                break;
            case 8:
                wave8.add(wave);
                if (wave8.size() > ECG_SIZE) {
                    wave8.remove(0);
                }
                ecgWaveV2 = "";
                for (int i = 0; i < wave8.size(); i++) {
                    ecgWaveV2 += wave8.get(i);
                }
                break;
            case 9:
                wave9.add(wave);
                if (wave9.size() > ECG_SIZE) {
                    wave9.remove(0);
                }
                ecgWaveV3 = "";
                for (int i = 0; i < wave9.size(); i++) {
                    ecgWaveV3 += wave9.get(i);
                }
                break;
            case 10:
                wave10.add(wave);
                if (wave10.size() > ECG_SIZE) {
                    wave10.remove(0);
                }
                ecgWaveV4 = "";
                for (int i = 0; i < wave10.size(); i++) {
                    ecgWaveV4 += wave10.get(i);
                }
                break;
            case 11:
                wave11.add(wave);
                if (wave11.size() > ECG_SIZE) {
                    wave11.remove(0);
                }
                ecgWaveV5 = "";
                for (int i = 0; i < wave11.size(); i++) {
                    ecgWaveV5 += wave11.get(i);
                }
                break;
            case 12:
                wave12.add(wave);
                if (wave12.size() > ECG_SIZE) {
                    wave12.remove(0);
                }
                ecgWaveV6 = "";
                for (int i = 0; i < wave12.size(); i++) {
                    ecgWaveV6 += wave12.get(i);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 心电数据
     * @param param 参数
     * @return 心电数据(字符串)
     */
    public String getEcgWave(int param) {
        switch (param) {
            case 1:
                return ecgWaveI;
            case 2:
                return ecgWaveIi;
            case 3:
                return ecgWaveIii;
            case 4:
                return ecgWaveAvr;
            case 5:
                return ecgWaveAvl;
            case 6:
                return ecgWaveAvf;
            case 7:
                return ecgWaveV1;
            case 8:
                return ecgWaveV2;
            case 9:
                return ecgWaveV3;
            case 10:
                return ecgWaveV4;
            case 11:
                return ecgWaveV5;
            case 12:
                return ecgWaveV6;
            default:
                return "";
        }
    }

    /**
     * 清空心电数据
     */
    public void initEcgWave() {
        wave1.clear();
        wave2.clear();
        wave3.clear();
        wave4.clear();
        wave5.clear();
        wave6.clear();
        wave7.clear();
        wave8.clear();
        wave9.clear();
        wave10.clear();
        wave11.clear();
        wave12.clear();
    }
}
