package com.konsung.bean;

import java.util.List;

/**
 * 创建者     abel.xie
 * 创建时间   2017/11/14 0014 下午 3:51
 * 描述	      ${TODO}
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */

public class PersonDownloadResponse<T> {
    public String resultCode;
    public String entity;
    public List<T> list;
}
