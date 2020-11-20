package com.example.musicplayer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.databinding.ActivityLoginBinding;
import com.example.musicplayer.entity.ResultBean;
import com.example.musicplayer.entity.User;
import com.example.musicplayer.util.ContextUtils;
import com.example.musicplayer.util.LoginUtils;
import com.example.musicplayer.util.NetworkRequestUtils;

import java.lang.ref.WeakReference;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG="LoginActivity";
    ActivityLoginBinding loginBinding;
    private LoginUtils loginUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());

        loginUtils=new LoginUtils(this);
        loginBinding.loginButton.setOnClickListener(this);
        String account=loginUtils.getLoginUserAccount();
        if (account!=null) {
            loginBinding.nameEdit.setText(account);
        }else if (BuildConfig.DEBUG){
            loginBinding.nameEdit.setText("admin");
            loginBinding.passwordEdit.setText("123456");
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v!=null) {
                ContextUtils.hideSoftInput(this,loginBinding.nameEdit);
            }
        }
        return super.onTouchEvent(event);

    }
    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.login_button:
                startLogin();
                //testDate();
                break;
        }
    }

    private void startLogin() {
        String name = loginBinding.nameEdit.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, R.string.hint_user_name,Toast.LENGTH_SHORT).show();
            return;
        }
        String password = loginBinding.passwordEdit.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.hint_user_password,Toast.LENGTH_SHORT).show();
            return;
        }
        ContextUtils.hideSoftInput(this, loginBinding.nameEdit);
        //new LoginAsyncTask(this, name,this).execute();
        new LoginAsyncTask(this,name,password).execute();
    }
    private void startMainActivity(){
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    private static class LoginAsyncTask extends AsyncTask<Void, Void, ResultBean<User>> {
        private final WeakReference<LoginActivity> loginActivityWeakReference;
        private final String account;
        private final String password;
        private ProgressDialog progressDialog;
        private LoginAsyncTask(LoginActivity loginActivity, String account, String password){
            loginActivityWeakReference=new WeakReference<>(loginActivity);
            this.account=account;
            this.password=password;
            try{
                progressDialog=ProgressDialog.show(loginActivity,loginActivity.getString(R.string.app_name),"正在登录，请稍候...");
            }catch(Throwable tr){
                tr.printStackTrace();
            }
        }
        @Override
        protected ResultBean<User> doInBackground(Void... voids) {
            LoginActivity loginActivity=loginActivityWeakReference.get();
            if (loginActivity==null){
                return null;
            }
            try {
                ResultBean<User> resultBean = NetworkRequestUtils.login(account,password);

                if (resultBean!=null&&resultBean.isSuccess()){
                    loginActivity.loginUtils.setLoginUser(resultBean.getData().getAccount(),resultBean.getData().getName());
                }
                return resultBean;
            }catch(Throwable tr){
                tr.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResultBean<User> resultBean) {
            super.onPostExecute(resultBean);
            try {
                progressDialog.dismiss();
            } catch (Throwable tr) {
                tr.printStackTrace();
            }
            LoginActivity loginActivity=loginActivityWeakReference.get();
            if (loginActivity==null){
                return;
            }
            if(resultBean==null){
                Toast.makeText(loginActivity, R.string.network_error_msg,Toast.LENGTH_SHORT).show();
            }else if(resultBean.isSuccess()) {
                loginActivity.startMainActivity();
            }else{
                Toast.makeText(loginActivity, resultBean.getText(),Toast.LENGTH_SHORT).show();
            }
        }
    }
}
