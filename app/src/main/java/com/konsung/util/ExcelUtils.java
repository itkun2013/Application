package com.konsung.util;

/**
 * Created by dlx on 2017/4/19 0019.
 */

import android.text.TextUtils;

import com.konsung.R;
import com.konsung.bean.CSVAnnotation;
import com.konsung.bean.InfoType;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jxl.SheetSettings;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import static com.konsung.util.UiUitls.getString;


/**
 * jxl导出excel
 *
 * @author jamboree
 * @date 2013-11-28
 */
public class ExcelUtils {

    public static final String PATIENT_SHEET_NAME = "病人信息";
    private static final String MEASURE_SHEET_NAME = "测量数据";

    /**
     * @param objData     导出内容数组
     * @param annotations 导出Excel的表头数组
     * @return
     * @author
     */
    public static <T> boolean exportToExcel(File file, List<T> objData
            , Map<String, CSVAnnotation> annotations) {

        if (annotations == null || annotations.isEmpty()) {
            return false;
        }

        boolean flag = true;
        WritableWorkbook wwb;
        try {
            //根据传进来的file对象创建可写入的Excel工作薄
            OutputStream os = new BufferedOutputStream(new FileOutputStream(file));

            wwb = Workbook.createWorkbook(os);

            String sheetName = "";
            if (file.getName().contains(GlobalConstant.PATIENT_FILE_NAME)) {
                sheetName = PATIENT_SHEET_NAME;
            } else {
                sheetName = MEASURE_SHEET_NAME;
            }

            // createSheet(sheetName, 0)一个是工作表的名称，另一个是工作表在工作薄中的位置
            WritableSheet ws = wwb.createSheet(sheetName, 0);

            SheetSettings ss = ws.getSettings();
            ss.setVerticalFreeze(1);//冻结表头
            WritableCellFormat wcf = new WritableCellFormat();


            //判断一下表头数组是否有数据
            if (annotations != null && annotations.size() > 0) {
                Set<Map.Entry<String, CSVAnnotation>> entries = annotations.entrySet();


                int head = 0;
                for (Map.Entry<String, CSVAnnotation> entry : entries) {
                    //循环写入表头
                    String fieldName = entry.getValue().name();
                    ws.addCell(new Label(head++, 0, fieldName, wcf));
                }

                if (objData != null && objData.size() > 0) {
                    //循环写入表中数据
                    for (int i = 0; i < objData.size(); i++) {

                        int column = 0;
                        for (Map.Entry<String, CSVAnnotation> entry : entries) {
                            String fieldName = entry.getKey();

                            //转换成map集合{activyName:测试功能,count:2}
                            T record = objData.get(i);

                            String field = ReflectUtil.getValFromObj(record, fieldName);
                            field = getExportData(annotations.get(fieldName).type(), field);

                            if (!TextUtils.isEmpty(field)) {
                                Label label = new Label(column, i + 1, field);
                                ws.addCell(label);
                            }
                            column++;
                        }
                    }

                }

                wwb.write();
                wwb.close();

                os.flush();
                os.close();
            }
        } catch (Exception ex) {
            flag = false;
            ex.printStackTrace();
        }
        return flag;
    }

    /**
     * 获取该type类型的导出数据（血型，性别值需要转换）
     *
     * @param type  类型值
     * @param field 数据库保存的值
     * @return
     */
    private static String getExportData(InfoType type, String field) {
        switch (type) {
            case NORMAL:
                return field;
            case SEX:
                if (TextUtils.isEmpty(field)) {
                    return getString(R.string.sex_unknown);
                }
                String sexStr;
                try {
                    int sex = Integer.valueOf(field);
                    switch (sex) {
                        case 0:
                            sexStr = getString(R.string.sex_woman);
                            break;
                        case 1:
                            sexStr = getString(R.string.sex_man);
                            break;
                        default:
                            sexStr = getString(R.string.sex_unknown);
                            break;
                    }
                    return sexStr;
                } catch (Exception e) {
                    return getString(R.string.sex_unknown);
                }

            case BLOOD:
                if (TextUtils.isEmpty(field)) {
                    return getString(R.string.detail_blood_type_5);
                }
                try {
                    int blood = Integer.valueOf(field);
                    switch (blood) {
                        case 1:
                            return getString(R.string.detail_blood_type_1);
                        case 2:
                            return getString(R.string.detail_blood_type_2);
                        case 0:
                            return getString(R.string.detail_blood_type_3);
                        case 3:
                            return getString(R.string.detail_blood_type_4);
                        case 5:
                            return getString(R.string.detail_blood_type_5);
                        default:
                            return getString(R.string.detail_blood_type_5);
                    }
                } catch (Exception e) {
                    return getString(R.string.detail_blood_type_5);
                }

            default:
                return field;
        }
    }

}