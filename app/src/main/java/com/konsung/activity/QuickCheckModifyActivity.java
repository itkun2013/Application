package com.konsung.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.QuickBean;
import com.konsung.bean.QuickTagBean;
import com.konsung.defineview.ParamPopupWindow;
import com.konsung.defineview.QuickSetDialog;
import com.konsung.defineview.TipsDialog;
import com.konsung.fragment.QuickCheckFragmentNew;
import com.konsung.util.GlobalConstant;
import com.konsung.util.UiUitls;
import com.konsung.util.global.GlobalNumber;
import com.konsung.view.MoveLinearLayout;
import com.konsung.view.MoveRelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置快检页面的方法
 */
public class QuickCheckModifyActivity extends BaseActivity {
    LinearLayout ll1;
    LinearLayout ll2;
    LinearLayout ll3;
    LinearLayout ll4;
    LinearLayout ll5;
    LinearLayout ll6;
    MoveLinearLayout llRoot;
    MoveLinearLayout llRoot1;
    MoveRelativeLayout relaRoot;
    QuickSetDialog quickSetDialog; //显示坐标的方法
    float startx = 0; //手指按压时的坐标 x
    float upx = 0;  //手指抬起时的坐标 x
    float starty = 0; //手指按压时的坐标 y
    float upy = 0;  //手指抬起时的坐标 y
    int moveH = 0;  //控件移动中的高度
    int moveW = 0;  //控件移动中的宽度
    int height = GlobalNumber.HUNDRED_NINETY_NUMBER; //控件的高度
    int startlx = 0; //移动控件容器索引
    int startly = 0; //移动控件的容器的子控件索引
    private int lastx; //移动控件后容器索引
    private int lastY; //移动控件后容器子控件索引
    boolean is = false; //根据移动控件判断，是否手指按下的开始的移动坐标
    boolean isUseful = false; //判断当前点击的控件是否有值
    boolean isUp = false; //记录手指是否抬起
    Button btnAdd;
    TextView btnDelete;
    //对应配置项视图集合
    private List<QuickBean> listView = new ArrayList<QuickBean>();
    List<LinearLayout> linearLayouts = new ArrayList<>(); //所有线性布局的容器
    //所有视图集合
    private List<Integer> listAllView = new ArrayList<Integer>();
    private LayoutInflater inflaterHelper;
    int lastly = 0; //上一次选择移动容器索引
    int lastYlx = 0; //上一次选择移动控件容器子控件索引
    int load = 0; //记录现在加载布局的最后一个容器索引
    ParamPopupWindow paramPopupWindow; //显示配置弹出框
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_check_modify);
        setLeftButtonText(getString(R.string.measurement_manage));
        inflaterHelper = getLayoutInflater();
        getAllLayout();
        initDatas();
        initViewLayout(inflaterHelper);
        initEvent();
        initMenu();
    }

    /**
     * 初始化菜单的方法
     */
    private void initMenu() {
        View menu = View.inflate(this, R.layout.quick_list_menu, null);
        TextView btnMenuSave = (TextView) menu.findViewById(R.id.btn_menu_save);
        getRightContainer().addView(menu);
        btnMenuSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUitls.showTitle(QuickCheckModifyActivity.this, UiUitls.getString(R.string
                        .save_param_config), UiUitls.getString(R.string
                        .save_config), new TipsDialog.UpdataButtonState() {
                            @Override
                            public void getButton(Boolean pressed) {
                                Intent intent = new Intent();
                                if (pressed) {
                                    replace();
                                    QuickBean bean = new QuickBean();
                                    bean.setQuickBeen(listView);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(QuickCheckFragmentNew.SET, bean);
                                    intent.putExtras(bundle);
                                    setResult(QuickCheckFragmentNew.RESULT_CODE, intent);
                                    finish();
                                } else {
                                    setResult(0, intent);
                                    finish();
                                }
                            }
                        });
            }
        });
    }

    /**
     * 屏蔽返回键
     */
    @Override
    public void onBackPressed() {
        //屏蔽返回，该代码可不删除
//        super.onBackPressed();
    }

    /**
     * 初始化数据
     */
    private void initDatas() {
        //该标志用于首页是否重新加载fragment的标识
        GlobalConstant.isParamOperate = true;
        Intent intent = getIntent();
        QuickBean serializableExtra = (QuickBean) intent.getSerializableExtra(
                QuickCheckFragmentNew.SET);
        listView = serializableExtra.getQuickBeen();
    }

    /**
     * 初始化监听事件
     */
    private void initEvent() {
        llRoot.setOnTouch(new MoveLinearLayout.OnTouch() {
            @Override
            public void onDown(float x, float y) {
                isUp = false;
                startx = x;
                starty = y;
                if (relaRoot.getChildCount() <= 0) {
                    getCHildView(relaRoot, x, y);
                    is = true;
                }
            }

            @Override
            public void onMove(MotionEvent event) {
                if (!isUseful) {
                    return;
                }
                int dx = (int) event.getX() - lastx;
                int dy = (int) event.getY() - lastY;
                if (event.getY() > height * GlobalNumber.THREE_NUMBER
                        && event.getRawX() > btnAdd.getWidth() * 1
                        && event.getRawX() < btnAdd.getWidth() * 2) {
                    btnDelete.setBackgroundResource(R.drawable.tv_bg_selector_de);
                } else {
                    btnDelete.setBackgroundResource(R.drawable.tv_bg_selector);
                }
                int left = 0;
                int top = 0;
                if (is) {
                    is = false;
                    left = relaRoot.getChildAt(0).getLeft() + dx;
                    top = relaRoot.getChildAt(0).getTop() + dy;
                } else {
                    left = relaRoot.getChildAt(0).getLeft() + dx + moveW / 2;
                    top = relaRoot.getChildAt(0).getTop() + dy + moveH / 2;
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        moveW,
                        moveH
                );

                layoutParams.height = moveH;
                layoutParams.width = moveW;
                layoutParams.leftMargin = left - moveW / 2;
                layoutParams.topMargin = top - moveH / 2;
                relaRoot.getChildAt(0).setLayoutParams(layoutParams);
                lastx = (int) event.getX();
                lastY = (int) event.getY();
            }

            @Override
            public void onUp(float x, float y, MotionEvent event) {
                btnDelete.setBackgroundResource(R.drawable.tv_bg_selector);
                if (isUseful) {
                    isUp = true;
                    linearLayouts.get(lastYlx).getChildAt(lastly).setAlpha(1f);
                    linearLayouts.get(startlx).getChildAt(startly).setVisibility(View.VISIBLE);
                    relaRoot.removeAllViews();
                }
                if (event.getY() > height * GlobalNumber.THREE_NUMBER && event.getRawX() >
                        btnAdd.getWidth() * 1 && event.getRawX() < btnAdd.getWidth() * 2) {
                    delete();
                }
                upx = x;
                upy = y;
                if (event.getY() > 0) {
                    move();
                }
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paramPopupWindow = new ParamPopupWindow(QuickCheckModifyActivity.this);
                replace();
                int value = 0;
                value = value | (0x01 << 0);

                for (int j = 0; j < listView.size(); j++) {
                    QuickBean bean = listView.get(j);
                    for (int i = 0; i < listAllView.size(); i++) {
                        //血氧
                        if (bean.getType() == i) {
                            value = value | (0x01 << (i + 1));
                        }
                    }
                }
                paramPopupWindow.initParamBox(value);
                paramPopupWindow.addCallBackListener(new ParamPopupWindow.OnCallBackListener() {
                    @Override
                    public void onCommit(String checkedParamTxt) {

                    }

                    @Override
                    public void onCommitParam(List<Integer> params) {
                        UiUitls.showProgress(QuickCheckModifyActivity.this, UiUitls.getString(R
                                .string.loading_form));
                        List<Integer>  paras = new ArrayList();
                        int uri = GlobalNumber.UN_TWO;
                        int listUri = GlobalNumber.UN_TWO;
                        for (int i = 0; i < params.size(); i++) {
                            int integer = params.get(i);
                            boolean isAra = false;
                            if (integer == GlobalNumber.FOUR_NUMBER
                                    || integer == GlobalNumber.FIVE_NUMBER) {
                                uri = integer;
                            }
                            for (int j = 0; j < listView.size(); j++) {
                                int type = listView.get(j).getType();
                                if (type == GlobalNumber.FOUR_NUMBER
                                        || type == GlobalNumber.FIVE_NUMBER) {
                                    listUri = type;
                                }
                                if (integer == type) {
                                    isAra = true;
                                    break;
                                }
                            }
                            if (!isAra) {
                                paras.add(integer);
                            }
                        }
                        if (uri != GlobalNumber.UN_TWO && listUri != GlobalNumber.UN_TWO) {
                            if (uri != listUri) {
                                for (int i = 0; i < listView.size(); i++) {
                                    QuickBean bean = listView.get(i);
                                    if (bean.getType() == listUri) {
                                        deleteView(bean.getIndex(), bean.getChildIndex());
                                        replace();
                                        break;
                                    }
                                }
                            }
                        }
                        for (int k = 0; k < paras.size(); k++) {
                            int pressed = paras.get(k);
                            if (pressed == -1) {
                                continue;
                            }
                            QuickBean bean = new QuickBean();
                            bean.setType(pressed);
                            if (pressed == GlobalNumber.FOUR_NUMBER
                                    || pressed == GlobalNumber.THREE_NUMBER) {
                                bean.setWeight(2);
                            } else if (pressed == GlobalNumber.FIVE_NUMBER) {
                                bean.setWeight(GlobalNumber.THREE_NUMBER);
                            } else {
                                bean.setWeight(1);
                            }
                            LinearLayout linearLayout = linearLayouts.get(load);
                            int tag = (int) linearLayout.getTag();
                            if (tag == 1) {
                                if (bean.getWeight() == 1) {
                                    if (linearLayout.getChildCount()
                                            != GlobalNumber.THREE_NUMBER) {
                                        bean.setIndex(load);
                                        bean.setChildIndex(linearLayout.getChildCount());
                                    } else {
                                        bean.setIndex(load + 1);
                                        bean.setChildIndex(0);
                                    }
                                } else if (bean.getWeight() == 2) {
                                    if (linearLayout.getChildCount() == 1) {
                                        bean.setIndex(load);
                                        bean.setChildIndex(linearLayout.getChildCount());
                                    } else if (linearLayout.getChildCount() == 2) {
                                        bean.setIndex(load);
                                        bean.setChildIndex(0);
                                        for (int i = 0; i < listView.size(); i++) {
                                            QuickBean bean1 = listView.get(i);
                                            if (bean1.getIndex() == load && bean1.getChildIndex()
                                                    == 0) {
                                                bean1.setIndex(load + 1);
                                                bean1.setChildIndex(0);
                                            }
                                        }
                                    } else {
                                        bean.setIndex(load + 1);
                                        bean.setChildIndex(0);
                                    }
                                } else if (bean.getWeight() == GlobalNumber.THREE_NUMBER) {
                                    for (int i = 0; i < listView.size(); i++) {
                                        QuickBean bean1 = listView.get(i);
                                        if (bean1.getIndex() == load) {
                                            bean1.setIndex(load + 1);
                                        }
                                    }
                                    bean.setIndex(load);
                                    bean.setChildIndex(0);
                                }
                            } else if (tag == 2) {
                                if (bean.getWeight() == 1) {
                                    if (linearLayout.getChildCount() == 1) {
                                        bean.setIndex(load);
                                        bean.setChildIndex(linearLayout.getChildCount());
                                    } else {
                                        bean.setIndex(load + 1);
                                        bean.setChildIndex(0);
                                    }
                                } else if (bean.getWeight() == 2) {
                                    bean.setIndex(load + 1);
                                    bean.setChildIndex(0);
                                } else if (bean.getWeight() == GlobalNumber.THREE_NUMBER) {
                                    for (int i = 0; i < listView.size(); i++) {
                                        QuickBean bean1 = listView.get(i);
                                        if (bean1.getIndex() == load) {
                                            bean1.setIndex(load + 1);
                                        }
                                    }
                                    bean.setIndex(load);
                                    bean.setChildIndex(0);
                                }
                            } else if (tag == GlobalNumber.THREE_NUMBER) {
                                bean.setIndex(load + 1);
                                bean.setChildIndex(0);
                            }
                            listView.add(bean);
                            typography(inflaterHelper);
                        }
                        initEvent();
                        UiUitls.hideProgress();
                    }
                });
                paramPopupWindow.show();

            }
        });
    }

    /**
     * 删除当前点击的控件
     */
    private void delete() {
        int width = linearLayouts.get(0).getChildAt(0).getWidth();
        int count = (int) (startx / width);
        int childCount = (int) (starty / height);
        if (count > load) {
            return;
        }
        int childCount3 = linearLayouts.get(count).getChildCount();
        if (childCount3 <= 0) {
            return;
        }
        childCount = distinguish(count, childCount, height);
        deleteView(count, childCount);
    }

    /**
     * 删除view的方法
     * @param count  控件
     * @param childCount  子控件
     */
    private void deleteView(int count, int childCount) {
        View childAt = linearLayouts.get(count).getChildAt(childCount);
        if (childAt == null) {
            return;
        }
        QuickTagBean tag = (QuickTagBean) childAt.getTag();
        if (tag == null) {
            return;
        }
        //血氧和血压模块禁止删除
        if (tag.getPosi() == 0 || tag.getPosi() == 1) {
            UiUitls.toast(getApplicationContext(), getRecString(R.string.not_delete));
            return;
        }
        if (tag.getWeight() == 1) {
            linearLayouts.get(count).removeView(childAt);
            LinearLayout linearLayout1 = linearLayouts.get(count);
            for (int i = count; i < linearLayouts.size(); i++) {
                if (count == linearLayouts.size() + 1) {
                    break;
                }
                LinearLayout linearLayout = linearLayouts.get(i + 1);
                int childCount1 = linearLayout.getChildCount();
                if (childCount1 == 0) {
                    break;
                }
                if ((int) linearLayout.getTag() == GlobalNumber.THREE_NUMBER) {
                    continue;
                }
                if ((int) linearLayout.getTag() == 2) {
                    int childCount2 = linearLayout.getChildCount();
                    if (childCount2 > 1) {
                        View childAt1 = linearLayout.getChildAt(0);
                        if (((QuickTagBean) childAt1.getTag()).getWeight() == 1) {
                            View childAt2 = linearLayout.getChildAt(1);
                            linearLayout.removeAllViews();
                            linearLayout1.addView(childAt1, childCount);
                            linearLayout.addView(childAt2);
                            linearLayout1 = linearLayout;
                            childCount = 1;
                        } else {
                            View childAt2 = linearLayout.getChildAt(1);
                            linearLayout.removeAllViews();
                            linearLayout1.addView(childAt2, childCount);
                            linearLayout.addView(childAt1);
                            linearLayout1 = linearLayout;
                            childCount = 1;
                        }
                    } else {
                        break;
                    }
                }
                if ((int) linearLayout.getTag() == 1) {
                    View childAt1 = linearLayout.getChildAt(0);
                    View childAt2 = linearLayout.getChildAt(1);
                    View childAt3 = linearLayout.getChildAt(2);
                    linearLayout.removeAllViews();
                    linearLayout1.addView(childAt1, childCount);
                    if (null != childAt2) {
                        linearLayout.addView(childAt2);
                    }
                    if (null != childAt3) {
                        linearLayout.addView(childAt3);
                        linearLayout1 = linearLayout;
                        childCount = 2;
                    }
                }
            }
        } else if (tag.getWeight() == 2) {
            linearLayouts.get(count).removeView(childAt);
            LinearLayout linearLayout1 = linearLayouts.get(count);
            int t = 2;
            for (int i = count; i < linearLayouts.size(); i++) {
                if (count == linearLayouts.size() + 1) {
                    break;
                }
                LinearLayout linearLayout = linearLayouts.get(i + 1);
                int childCount1 = linearLayout.getChildCount();
                if (childCount1 == 0) {
                    break;
                }
                if ((int) linearLayout.getTag() == GlobalNumber.THREE_NUMBER) {
                    continue;
                }
                if ((int) linearLayout.getTag() == 2) {
                    if (t == 2) {
                        int childCount2 = linearLayout.getChildCount();
                        if (childCount2 > 1) {
                            View childAt1 = linearLayout.getChildAt(0);

                            if (((QuickTagBean) childAt1.getTag()).getWeight() == 1) {
                                View childAt2 = linearLayout.getChildAt(1);
                                linearLayout.removeAllViews();
                                linearLayout1.addView(childAt2, childCount);
                                linearLayout.addView(childAt1);
                                linearLayout1 = linearLayout;
                                childCount = 1;
                                t = 1;
                            } else {
                                View childAt2 = linearLayout.getChildAt(1);
                                linearLayout.removeAllViews();
                                linearLayout1.addView(childAt1, childCount);
                                linearLayout.addView(childAt2);
                                linearLayout1 = linearLayout;
                                childCount = 1;
                                t = 1;
                            }
                        } else {
                            View childAt1 = linearLayout.getChildAt(0);
                            linearLayout.removeAllViews();
                            linearLayout1.addView(childAt1, childCount);
                            break;
                        }
                    } else {
                        int childCount2 = linearLayout.getChildCount();
                        if (childCount2 > 1) {
                            View childAt1 = linearLayout.getChildAt(0);
                            if (((QuickTagBean) childAt1.getTag()).getWeight() == 1) {
                                View childAt2 = linearLayout.getChildAt(1);
                                linearLayout.removeAllViews();
                                linearLayout1.addView(childAt1, childCount);
                                linearLayout.addView(childAt2);
                                linearLayout1 = linearLayout;
                                childCount = 1;
                            } else {
                                View childAt2 = linearLayout.getChildAt(1);
                                linearLayout.removeAllViews();
                                linearLayout1.addView(childAt2, childCount);
                                linearLayout.addView(childAt1);
                                linearLayout1 = linearLayout;
                                childCount = 1;
                            }
                        } else {
                            break;
                        }
                    }
                }
                if ((int) linearLayout.getTag() == 1) {
                    if (t == 1) {
                        View childAt1 = linearLayout.getChildAt(0);
                        View childAt2 = linearLayout.getChildAt(1);
                        View childAt3 = linearLayout.getChildAt(2);
                        linearLayout.removeAllViews();
                        linearLayout1.addView(childAt1, childCount);
                        if (null != childAt2) {
                            linearLayout.addView(childAt2);
                        }
                        if (null != childAt3) {
                            linearLayout.addView(childAt3);
                            linearLayout1 = linearLayout;
                            childCount = 2;
                        }
                    } else {
                        View childAt1 = linearLayout.getChildAt(0);
                        View childAt2 = linearLayout.getChildAt(1);
                        View childAt3 = linearLayout.getChildAt(2);
                        int childCount2 = linearLayout.getChildCount();
                        linearLayout.removeAllViews();
                        if (childCount2 >= 2) {
                            linearLayout1.addView(childAt1, childCount);
                            linearLayout1.addView(childAt2, 2);
                        } else {
                            linearLayout1.addView(childAt1, childCount);
                        }
                        if (null != childAt3) {
                            linearLayout.addView(childAt3);
                            linearLayout1 = linearLayout;
                            childCount = 1;
                        }
                    }
                }
            }
        } else {
            linearLayouts.get(count).removeView(childAt);
            LinearLayout linearLayout1 = linearLayouts.get(count);
            for (int i = count; i < linearLayouts.size(); i++) {
                if (count == linearLayouts.size() + 1) {
                    break;
                }
                LinearLayout linearLayout = linearLayouts.get(i + 1);
                int childCount1 = linearLayout.getChildCount();
                if (childCount1 == 0) {
                    break;
                }
                int tag1 = (int) linearLayout.getTag();
                View childAt1 = linearLayout.getChildAt(0);
                View childAt2 = linearLayout.getChildAt(1);
                View childAt3 = linearLayout.getChildAt(2);
                linearLayout.removeAllViews();
                if (null != childAt1) {
                    linearLayout1.addView(childAt1, 0);
                }
                if (null != childAt2) {
                    linearLayout1.addView(childAt2, 1);
                }
                if (null != childAt3) {
                    linearLayout1.addView(childAt3, 2);
                }
                if (tag1 == 1) {
                    if (childCount1 > 2) {
                        linearLayout1 = linearLayout;
                    } else {
                        break;
                    }
                }
                if (tag1 == 2) {
                    if (childCount1 > 1) {
                        linearLayout1 = linearLayout;
                    } else {
                        break;
                    }
                }
            }
        }
        replace();
        typography(inflaterHelper);
    }

    /**
     * 获取当前点击位置
     * @param relaRoot 容器控件
     * @param x x点击的位置
     * @param y y点击的位置
     */
    private void getCHildView(MoveRelativeLayout relaRoot, float x, float y) {
        int width = linearLayouts.get(0).getChildAt(0).getWidth();
        int count = (int) (x / width);
        int childCount = (int) (y / height);
        if (count > load) {
            isUseful = false;
            return;
        }
        LinearLayout linearLayout = linearLayouts.get(count);
        if (linearLayout.getHeight() < y) {
            isUseful = false;
            return;
        }
        isUseful = true;
        childCount = distinguish(count, childCount, height);
        View childAt = linearLayouts.get(count).getChildAt(childCount);
        if (null == childAt) {
            isUseful = false;
            return;
        }
        QuickTagBean tag = (QuickTagBean) childAt.getTag();
        int posi = tag.getPosi();
        View view = inflaterHelper.inflate(listAllView.get(posi), null);
        moveW = childAt.getWidth();
        moveH = childAt.getHeight();
        childAt.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                moveW,
                moveH
        );
        view.setAlpha(GlobalNumber.ZERO_FIVE_FLOAT);
        relaRoot.addView(view, p);
        startlx = count;
        startly = childCount;
        lastx = 0;
        lastY = 0;
    }

    /**
     * 计算移动的方法
     */
    private void move() {
        float v = upx - startx;
        View childAt = llRoot.getChildAt(0);
        int width = childAt.getWidth();
        int count = (int) (upx / width);
        if (count > load) {
            return;
        }
        LinearLayout linearLayout1 = linearLayouts.get(count);
        if (linearLayout1.getHeight() < upy) {
            return;
        }
        int countStart = (int) (startx / width);
        int childCountStart = (int) (starty / height);
        int countEnd = (int) (upx / width);
        int childCountEnd = (int) (upy / height);
        childCountStart = distinguish(countStart, childCountStart, height);
        childCountEnd = distinguish(countEnd, childCountEnd, height);
        exchange(linearLayouts.get(countStart), childCountStart, linearLayouts.get(countEnd),
                childCountEnd, startx, starty,
                height, width, countStart, countEnd);
    }

    /**
     * 更加不同布局的大小计算布局替换的索引
     * @param i 控件再容器类的坐标索引
     * @param c 按照标准高度的索引结果
     * @param h 控件的高度
     * @return 实际控件再容器内的索引值
     */
    private int distinguish(int i, int c, float h) {
        LinearLayout linearLayout = linearLayouts.get(i);
        Object tag = linearLayout.getTag();
        //计划结果值
        int results = 0;
        //在控件内做的标记值
        int count = (int) tag;
        //计算第一个控件是不是标准控件
        int height = linearLayout.getChildAt(0).getHeight();
        int poor = count - 1;
        if (height > h) {
            results = c - poor;
            if (results < 0) {
                results = 0;
            }
        } else {
            results = c;
            if (c > 1 && linearLayout.getChildAt(1).getHeight() > height) {
                results = 1;
            }
        }

        return results;
    }

    /**
     * 交换2个控件的位置
     * @param linearLayout 第一个控件的容器
     * @param ly 控件位置
     * @param linearLayout1 第二个控件的容器
     * @param ly1 控件位置
     * @param x 开始点击的x坐标位置
     * @param y 开始惦记的y坐标位置
     * @param h 控件的高度
     * @param w 控件的宽度
     * @param c 开始控件再容器内的索引
     * @param l 结束控件再容器内的索引
     */
    private void exchange(LinearLayout linearLayout, int ly, LinearLayout linearLayout1, int ly1,
            float x, float y, float h, float w, int c, int l) {
        if (c == l && ly == ly1) {
            return;
        }
        int count = (int) (x / w);
        int childCount = (int) (y / h);
        childCount = distinguish(count, childCount, h);
        View childAt = linearLayout.getChildAt(ly);
        View childAt1 = linearLayout1.getChildAt(ly1);
        int weight1 = ((QuickTagBean) childAt.getTag()).getWeight();
        int weight2 = ((QuickTagBean) childAt1.getTag()).getWeight();

        if (weight1 != 1 || weight2 != 1) {
            int tag = weight1;
            int tag1 = weight2;
            if (count == c) {
                if (tag == 2) {
                    if (tag1 == 2) {
                        linearLayout.removeView(childAt);
                        linearLayout1.removeView(childAt1);
                        linearLayout.addView(childAt1, ly);
                        linearLayout1.addView(childAt, ly1);
                    } else if (tag1 == 1) {
                        if (ly1 < 2 && (int) (linearLayout1.getTag()) != 2) {
                            View childAt2 = linearLayout1.getChildAt(ly1 + 1);
                            if (childAt2 == null) {
                                return;
                            }
                            linearLayout.removeView(childAt);
                            linearLayout1.removeView(childAt1);
                            linearLayout1.removeView(childAt2);
                            linearLayout.addView(childAt2, ly);
                            linearLayout.addView(childAt1, ly);
                            linearLayout1.addView(childAt, ly1);
                            linearLayout.setTag(1);
                            linearLayout1.setTag(2);
                        } else {
                            return;
                        }
                    }
                } else if (tag == GlobalNumber.THREE_NUMBER) {
                    int tagThree = (int) linearLayout1.getTag();
                    List<View> views = new ArrayList<>();
                    for (int i = 0; i < linearLayout1.getChildCount(); i++) {
                        views.add(linearLayout1.getChildAt(i));
                    }
                    linearLayout.removeView(childAt);
                    linearLayout1.removeAllViews();
                    for (int i = views.size() - 1; i >= 0; i--) {
                        linearLayout.addView(views.get(i));
                    }
                    linearLayout1.addView(childAt);
                    linearLayout.setTag(tagThree);
                    linearLayout1.setTag(tag);
                }
            } else {
                if (tag1 == 2) {
                    if (tag == 2) {
                        linearLayout.removeView(childAt);
                        linearLayout1.removeView(childAt1);
                        linearLayout.addView(childAt1, ly);
                        linearLayout1.addView(childAt, ly1);
                    } else if (tag == 1) {
                        if (ly < 2 && (int) (linearLayout.getTag()) != 2) {
                            View childAt2 = linearLayout.getChildAt(ly + 1);
                            if (childAt2 == null) {
                                return;
                            }
                            linearLayout.removeView(childAt);
                            linearLayout1.removeView(childAt1);
                            linearLayout.removeView(childAt2);
                            linearLayout.addView(childAt1, ly);
                            linearLayout1.addView(childAt2, ly1);
                            linearLayout1.addView(childAt, ly1);
                            linearLayout.setTag(2);
                            linearLayout1.setTag(1);
                        } else {
                            return;
                        }
                    }
                } else if (tag1 == GlobalNumber.THREE_NUMBER) {
                    int tagThree = (int) linearLayout.getTag();
                    List<View> views = new ArrayList<>();
                    for (int i = 0; i < linearLayout.getChildCount(); i++) {
                        views.add(linearLayout.getChildAt(i));
                    }
                    linearLayout1.removeView(childAt1);
                    linearLayout.removeAllViews();
                    for (int i = views.size() - 1; i >= 0; i--) {
                        linearLayout1.addView(views.get(i));
                    }
                    linearLayout.addView(childAt1);
                    linearLayout1.setTag(tagThree);
                    linearLayout.setTag(tag1);
                }
            }
        } else {
            if (linearLayout == linearLayout1) {
                linearLayout.removeView(childAt1);
                linearLayout.addView(childAt1, ly);
                linearLayout.removeView(childAt);
                linearLayout.addView(childAt, ly1);
            } else {
                linearLayout.removeView(childAt);
                linearLayout1.removeView(childAt1);
                linearLayout.addView(childAt1, ly);
                linearLayout1.addView(childAt, ly1);
            }
        }
    }

    /**
     * 控件的内容转换成ListView的数据
     */
    private void replace() {
        listView.clear();
        for (int i = 0; i < linearLayouts.size(); i++) {
            LinearLayout linearLayout = linearLayouts.get(i);
            for (int j = 0; j < linearLayout.getChildCount(); j++) {
                View childAt = linearLayout.getChildAt(j);
                QuickTagBean bean = (QuickTagBean) childAt.getTag();
                QuickBean quickBean = new QuickBean();
                quickBean.setWeight(bean.getWeight());
                quickBean.setType(bean.getPosi());
                quickBean.setIndex(i);
                quickBean.setChildIndex(j);
                listView.add(quickBean);
            }
        }
    }

    /**
     * 得到所有子布局
     */
    private void getAllLayout() {
        listAllView.clear();
        //血氧
        listAllView.add(R.layout.quick_check_spo_layout_modify);
        //血压
        listAllView.add(R.layout.quick_check_bp_layout_modify);
        //体温
        listAllView.add(R.layout.quick_check_temp_layout_modify);
        //血液三项
        listAllView.add(R.layout.quick_check_blood_sugar_layout_modify);
        //显示11项
        listAllView.add(R.layout.quick_check_urine_11_layout_modify);
        //14项
        listAllView.add(R.layout.quick_check_urine_14_layout_modify);
        //血红蛋白
        listAllView.add(R.layout.quick_check_blood_red_layout_modify);
        //血脂
        listAllView.add(R.layout.quick_check_xuezhi_layout_modify);

        //糖化
        listAllView.add(R.layout.quick_check_sugar_bhd_layout_modify);
        //bmi
        listAllView.add(R.layout.quick_check_bmi_layout_modify);
        //白细胞
        listAllView.add(R.layout.quick_check_wbc_layout_modify);
    }

    /**
     * 初始化容器的布局
     * @param inflater 布局器
     */
    private void initViewLayout(LayoutInflater inflater) {
        //对应的四列布局
        ll1 = (LinearLayout) findViewById(R.id.ll_1);
        ll2 = (LinearLayout) findViewById(R.id.ll_2);
        ll3 = (LinearLayout) findViewById(R.id.ll_3);
        ll4 = (LinearLayout) findViewById(R.id.ll_4);
        ll5 = (LinearLayout) findViewById(R.id.ll_5);
        ll6 = (LinearLayout) findViewById(R.id.ll_6);
        btnAdd = (Button) findViewById(R.id.btn_add);
        btnDelete = (TextView) findViewById(R.id.btn_delete);
        llRoot = (MoveLinearLayout) findViewById(R.id.ll_root);
        llRoot1 = (MoveLinearLayout) findViewById(R.id.ll_root1);
        relaRoot = (MoveRelativeLayout) findViewById(R.id.rela_root);
        linearLayouts.clear();
        linearLayouts.add(ll1);
        linearLayouts.add(ll2);
        linearLayouts.add(ll3);
        linearLayouts.add(ll4);
        linearLayouts.add(ll5);
        linearLayouts.add(ll6);
        typography(inflater);
    }

    /**
     * 初始化排版
     * @param inflater  布局加载器
     */
    private void typography(LayoutInflater inflater) {
        //这一列专门用于放未选中视图，可任意放，只是为了让视图存在绑定id不异常

        for (int i = 0; i < linearLayouts.size(); i++) {
            LinearLayout linearLayout = linearLayouts.get(i);
            linearLayout.removeAllViews();
            linearLayout.setTag(0);
            int max = 0;
            for (int j = 0; j < listView.size(); j++) {
                if (max >= GlobalNumber.THREE_NUMBER) {
                    break;
                } else {
                    QuickBean bean = listView.get(j);
                    if (bean.getIndex() == i) {
                        int weight = bean.getWeight();
                        View view = inflater.inflate(listAllView.get(bean.getType()), null);
                        linearLayout.addView(view, getLayoutParams(weight));
                        load = i;
                        Object tag = linearLayout.getTag();
                        //给每一组控件加上标记，为了移动操作计算
                        QuickTagBean tagBean = new QuickTagBean();
                        tagBean.setWeight(weight);
                        tagBean.setPosi(bean.getType());
                        view.setTag(tagBean);
                        if (null != tag) {
                            int tcount = (int) tag;
                            if (weight > tcount) {
                                linearLayout.setTag(weight);
                            }
                        } else {
                            linearLayout.setTag(weight);
                        }
                        max += weight;
                    }
                }
            }
        }
    }

    /**
     * 获得布局参数
     * @param isBig 不同高度模块获取的参数不同
     * @return 控件的高度
     */
    private LinearLayout.LayoutParams getLayoutParams(int isBig) {
        if (isBig == 2) {
            return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , height * 2);
        } else if (isBig == 1) {
            return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        } else {
            return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , height * GlobalNumber.THREE_NUMBER);
        }
    }

    @Override
    public boolean back() {
        Intent intent = new Intent();
        setResult(0, intent);
        finish();
        return false;
    }
}
