package hotel.convenient.com.activity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import hotel.convenient.com.R;
import hotel.convenient.com.base.BaseActivity;
import hotel.convenient.com.http.HostUrl;
import hotel.convenient.com.http.HttpUtils;
import hotel.convenient.com.http.RequestParams;
import hotel.convenient.com.http.ResultJson;
import hotel.convenient.com.http.SimpleCallback;
import hotel.convenient.com.view.LinearLayoutEditTextView;

/**
 * Created by cwy on 2015/11/27 11:29
 */
public class RegisterActivity extends BaseActivity {
    @Bind(R.id.mobile_phone)
     LinearLayoutEditTextView mobile_phone;
    @Bind(R.id.password)
     LinearLayoutEditTextView password;
    @Bind(R.id.verifyPassword)
     LinearLayoutEditTextView verifyPassword;
    @Bind(R.id.vCode)
     LinearLayoutEditTextView vCode;
    @Bind(R.id.username)
     LinearLayoutEditTextView username;
    @Bind(R.id.register_confirm)
     Button register_confirm;
    @Bind(R.id.register_checkbox)
     CheckBox register_checkbox;
    @Bind(R.id.register_upload_url)
     TextView register_upload_url;
    
    private String mobile;
    private String str_password = "";
    private String str_verifyPassword = "";
    private String str_username = "";
    private String str_vCode = "";
    private SimpleCallback simpleCallback = new SimpleCallback() {
            @Override
            public <T> void simpleSuccess(String url, String result, ResultJson<T> resultJson) {
                if (resultJson.getCode() == CODE_SUCCESS) {
                    showShortToast(resultJson.getMsg());
                    RegisterActivity.this.finish();
                } else {
                    showShortToast(resultJson.getMsg());
                }
            }
        };
    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle("用户注册");
        showBackPressed();
        String checkbox_text = "<a href=''>《注册协议》</a>";
        register_upload_url.setText(Html.fromHtml(checkbox_text));
        vCode.setButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkMobilPhone()) return;
                getCheckCode();
            }
        });
    }

    @Override
    public int setLayoutView() {
        return R.layout.register_activity;
    }

    @OnClick({R.id.register_confirm,R.id.register_upload_url})
     void onButtonClick(View v){
        switch (v.getId()){
            case R.id.register_confirm:
                if(!register_checkbox.isChecked()){
                    showShortToast("同意注册协议后才能注册");
                    return ;
                }
                if(!setRegisterParams()){
                    return;
                }
                httpRegister();
                break;
            case R.id.register_upload_url:
                break;
        }
    }
    private boolean setRegisterParams() {
        mobile = mobile_phone.getText();
        str_password = password.getText();
        str_verifyPassword = verifyPassword.getText();
        str_username = username.getText();
        str_vCode = vCode.getText();

        if (checkParams(str_password, str_verifyPassword, str_username, str_vCode)) return false;
        return true;
    }
    /**
     * 向服务器发送注册请求
     */
    private void httpRegister() {
        RequestParams params = new RequestParams(HostUrl.HOST+ HostUrl.URL_REGISTER);
        params.addBodyParameter("phone",mobile );
        params.addBodyParameter("nickname",str_username );
        params.addBodyParameter("password",str_password );
        params.addBodyParameter("code",str_vCode );
        HttpUtils.post(params, simpleCallback);
    }

    private boolean checkParams(String str_password, String str_verifyPassword, String str_username, String str_vCode) {
        if(!checkMobilPhone()){
            return true;
        }
        if (!checkPassword(str_password, str_verifyPassword)) return true;
        if (isEmpty(str_username)){
            showShortToast("用户名不能为空!");
            return true;
        }
        if (isEmpty(str_vCode)){
            showShortToast("验证码不能为空!");
            return true;
        }
        return false;
    }

    private boolean checkPassword(String str_password, String str_verifyPassword) {
        if (isEmpty(str_password)){
            showShortToast("密码不能为空!");
            return false;
        }
        if (!str_password.equals(str_verifyPassword)){
            showShortToast("两次输入的密码不一致");
            return false;
        }
        return true;
    }

    /**
     * 检查电话号码
     */
    private boolean checkMobilPhone() {
        mobile = mobile_phone.getText().toString().trim();
         if(isEmpty(mobile)){
             showShortToast("电话号码不能为空!");
             return false;
         }
        if(mobile.length()!=11){
            showShortToast("电话号码格式错误!");
            return false;
        }
        return true;
    }

    /**
     * 获取验证码
     */
    public void getCheckCode() {
        RequestParams params = new RequestParams(HostUrl.HOST+ HostUrl.URL_GET_CODE);
        params.addQueryStringParameter("phone", mobile);
        HttpUtils.get(params, new SimpleCallback() {
            @Override
            public <T> void simpleSuccess(String url, String result, ResultJson<T> resultJson) {
                if (resultJson.getCode() == CODE_SUCCESS) {
                    showShortToast("验证码获取成功");
                    vCode.startChangeButtonName("重新获取", 30);
                } else {
                    showShortToast(resultJson.getMsg());
                }
            }
        });
    }
}
