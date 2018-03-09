package com.konsung.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.defineview.WaveFormEcg;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.UnitConvertUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GetEcgWaveRecordFor5 extends BaseFragment {

    @InjectView(R.id.wave_i)
    WaveFormEcg waveI;
    @InjectView(R.id.wave_ii)
    WaveFormEcg waveIi;
    @InjectView(R.id.wave_iii)
    WaveFormEcg waveIii;
    @InjectView(R.id.wave_AVR)
    WaveFormEcg waveAvr;
    @InjectView(R.id.wave_AVL)
    WaveFormEcg waveAvl;
    @InjectView(R.id.wave_AVF)
    WaveFormEcg waveAvf;
    @InjectView(R.id.wave_V1)
    WaveFormEcg waveV1;

    private Handler handler = new Handler();
    private int hrVaule = GlobalConstant.INVALID_DATA;
    private int measureCount = 0;
    private MeasureDataBean dataBean;
    /*    List<PatientBean> patient;*/
    private PatientBean patient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout
                .fragment_get_ecg_wave_recordfor5, null);
        Bundle bundle = getArguments();
        String idcard = bundle.getString("idcard");

        dataBean = DBDataUtil.getMeasures(idcard).get(0);
        dataBean.setIdcard(patient.getIdCard());
        ButterKnife.inject(this, view);

        waveI.setSampleRate(500);
        waveIi.setSampleRate(500);
        waveIii.setSampleRate(500);
        waveAvr.setSampleRate(500);
        waveAvl.setSampleRate(500);
        waveAvf.setSampleRate(500);
        waveV1.setSampleRate(500);

        waveI.setTitle(getRecString(R.string.ecg_title_I), 1);
        waveIi.setTitle(getRecString(R.string.ecg_title_II), 2);
        waveIii.setTitle(getRecString(R.string.ecg_title_III), 3);
        waveAvr.setTitle(getRecString(R.string.ecg_title_AVR), 4);
        waveAvl.setTitle(getRecString(R.string.ecg_title_AVL), 5);
        waveAvf.setTitle(getRecString(R.string.ecg_title_AVF), 6);
        waveV1.setTitle(getRecString(R.string.ecg_title_V), 7);

        new Thread(updateThread).start();
        return view;
    }


    /*
     * ʵʱ��������
     */
    private Runnable updateThread = new Runnable() {
        @Override
        public void run() {
            byte[] bytes = UnitConvertUtil.hexStringToBytes(dataBean
                    .get_ecgWave(1));
                  /*  Log.d("Test","һ�� = "+Arrays.toString(dataBean
                  .get_ecgWave(1).getBytes("ISO-8859-1")));*/
            waveI.setData(bytes);
            bytes = UnitConvertUtil.hexStringToBytes(dataBean.get_ecgWave(2));

            waveIi.setData(bytes);
            bytes = UnitConvertUtil.hexStringToBytes(dataBean.get_ecgWave(3));
            waveIii.setData(bytes);
            bytes = UnitConvertUtil.hexStringToBytes(dataBean.get_ecgWave(4));
            waveAvr.setData(bytes);
            bytes = UnitConvertUtil.hexStringToBytes(dataBean.get_ecgWave(5));
            waveAvl.setData(bytes);
            bytes = UnitConvertUtil.hexStringToBytes(dataBean.get_ecgWave(6));
            waveAvf.setData(bytes);
            bytes = UnitConvertUtil.hexStringToBytes(dataBean.get_ecgWave(7));
            waveV1.setData(bytes);
        }

    };

    @Override
    public void onDetach() {
        super.onDetach();
        isAttach = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        waveI.stop();
        waveIi.stop();
        waveIii.stop();
        waveAvr.stop();
        waveAvl.stop();
        waveAvf.stop();
        waveV1.stop();

   /*     getActivity().unbindService(serviceConnection);*/

    }

}
