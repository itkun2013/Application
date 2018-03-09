package com.konsung.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.greendao.dao.UserBeanDao;
import com.konsung.R;
import com.konsung.bean.UserBean;
import com.konsung.defineview.CustomToast;
import com.konsung.util.DBDataUtil;
import com.konsung.util.UiUitls;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
/**
 * 注册页面
 */
public class RegisterActivity extends Activity {

    @InjectView(R.id.username_ed)
    EditText usernameEd;
    @InjectView(R.id.password_ed)
    EditText passwordEd;
    @InjectView(R.id.password_correct_ed)
    EditText passwordCorrectEd;

    @InjectView(R.id.register_btn)
    Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //让屏幕保持常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_register);
        //防止软键盘遮挡编辑框
        getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_ADJUST_RESIZE);
        ButterKnife.inject(this);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEd.getText()
                        .toString();
                String password = passwordEd.getText()
                        .toString();
                String passwordCorrect = passwordCorrectEd.getText()
                        .toString();
                //字符串检查
                //检查是否输入
                if ("".equals(username) || "".equals(password) || "".equals(passwordCorrect)) {
                    //                    Toast.makeText(RegisterActivity
                    // .this, getString(R.string.pls_enter), Toast
                    // .LENGTH_SHORT).show();
                    CustomToast.showMessage(RegisterActivity.this, getString(R.string.pls_enter));
                    return;
                }
                //排除空格
                if (username.contains(" ")) {
                    //                    Toast.makeText(RegisterActivity
                    // .this, getString(R.string.username_isexist_nbsp),
                    // Toast.LENGTH_SHORT).show();
                    CustomToast.showMessage(RegisterActivity.this,
                            getString(R.string.username_isexist_nbsp));
                    return;
                }
                if (password.contains(" ")) {
                    //                    Toast.makeText(RegisterActivity
                    // .this, getString(R.string.password_isexist_nbsp),
                    // Toast.LENGTH_SHORT).show();
                    CustomToast.showMessage(RegisterActivity.this,
                            getString(R.string.password_isexist_nbsp));
                    return;
                }
                if (!passwordCorrect.equals(password)) {
                    //                    Toast.makeText(RegisterActivity
                    // .this, getString(R.string.password_is_wrong), Toast
                    // .LENGTH_SHORT).show();
                    CustomToast.showMessage(RegisterActivity.this,
                            getString(R.string.password_is_wrong));

                    // ### 密码不正确,清空###
                    passwordEd.setText("");
                    passwordEd.requestFocus();
                    passwordCorrectEd.setText("");
                    return;
                }
                if (UiUitls.isConSpeCharacters(username) || UiUitls
                        .isConSpeCharacters(password)) {
                    CustomToast.showMessage(RegisterActivity.this,
                            getString(R.string.password_isexist_special));
                    return;
                }
                if (username.equals(getString(R.string.test_user)) &&
                        password.equals(getString(R.string.test_password))) {
                    CustomToast.showMessage(RegisterActivity.this, getString(
                            R.string.password_isexist_repet));
                    return; //不允许用户注册管理员密码
                }
                if (username.equals(getString(R.string.test_user)) &&
                        password.equals(getString(R.string
                        .test_common_password))) {
                    CustomToast.showMessage(RegisterActivity.this,
                            getString(R.string.password_isexist_repet));
                    return; //不允许用户注册管理员密码
                }
                UserBean bean = new UserBean();
                bean.setPassword(password);
                bean.setUsername(username);


                UserBeanDao dao = DBDataUtil.getUserDao();
                List<UserBean> list = dao.queryBuilder().where(UserBeanDao.Properties
                        .Username.eq(username)).list();

                if (list.size() > 0) {
                    UiUitls.toast(RegisterActivity.this, getString(R.string.username_is_exist));
                    return;
                } else {
                    dao.insert(bean);
                    UiUitls.toast(RegisterActivity.this, getString(R.string
                            .congratulate_for_register));
                    finish();
                }

            }
        });
    }

}
