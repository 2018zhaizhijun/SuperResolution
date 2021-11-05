package com.example.zz.superresolution;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mob.MobSDK;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends Activity implements LoaderCallbacks<Cursor>, OnClickListener {

    String base_url = "http://101.35.24.184:9008/user/registry";
    String APPKEY = "349f7272485a8";
    String APPSECRETE = "3302a31df896659c443a0079575a81a3";
    int timecnt = 30;

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
//    private static final String[] DUMMY_CREDENTIALS = new String[]{
//            "abcd:1234"
//    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    //private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mTelephoneView;
    private EditText mNameView;
    private EditText mPasswordView;
    private EditText mCodeView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mSendcodeButton;
    private Button mSignInButton;
    ProgressBar mProBar;
    FrameLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 启动短信验证sdk
        MobSDK.init(this, APPKEY, APPSECRETE);
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        //注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);

        // Set up the login form.
        mTelephoneView = (AutoCompleteTextView) findViewById(R.id.telephone_register);
        populateAutoComplete();

        mCodeView = (EditText) findViewById(R.id.inputcode);
        mNameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password_register);

        mSendcodeButton = (Button) findViewById(R.id.sendcode_button);
        mSendcodeButton.setOnClickListener(this);

        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(this);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    @Override
    public void onClick(View v) {
        String phoneNums = mTelephoneView.getText().toString();
        switch (v.getId()) {
            case R.id.sendcode_button:
                // 1. 通过规则判断手机号
                if (!judgePhoneNums(phoneNums)) {
                    return;
                } // 2. 通过sdk发送短信验证
                SMSSDK.getVerificationCode("86", phoneNums);

                // 3. 把按钮变成不可点击，并且显示倒计时（正在获取）
                mSendcodeButton.setClickable(false);
                mSendcodeButton.setText("重新发送(" + timecnt + ")");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (; timecnt > 0; timecnt--) {
                            handler.sendEmptyMessage(-9);
                            if (timecnt <= 0) {
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        handler.sendEmptyMessage(-8);
                    }
                }).start();
                break;

            case R.id.sign_in_button:
                Register(RegisterActivity.this);
                createProgressBar();
                break;
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == -9) {
                mSendcodeButton.setText("重新发送(" + timecnt + ")");
            } else if (msg.what == -8) {
                mSendcodeButton.setText("获取验证码");
                mSendcodeButton.setClickable(true);
                timecnt = 30;
            } else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                Log.e("event", "event=" + event);
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 短信登录成功后，跳转NavigationActivity,然后提示
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                        Register(RegisterActivity.this);
//                        Toast.makeText(getApplicationContext(), "注册成功",
//                                Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(RegisterActivity.this,
//                                LoginActivity.class);
//                        startActivity(intent);
//                        finish();
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Toast.makeText(getApplicationContext(), "正在获取验证码",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        ((Throwable) data).printStackTrace();
                    }
                }
            }
        }
    };

    private void Register(Context context){
        String username = mNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String telephone = mTelephoneView.getText().toString();
        String code = mCodeView.getText().toString();

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("password",password);
        formBody.add("telephone", telephone);
        formBody.add("userName", username);
        formBody.add("verifyCode", code);

        Request request = new Request.Builder()
                .url(base_url)
                .post(formBody.build())
                .build();

        Call call = client.newCall(request);

//        OkHttpClient client = new OkHttpClient.Builder().build();
//        MediaType mediaType = MediaType.parse("image/jpeg");
//        RequestBody fileBody = RequestBody.create(mediaType,imgBytes);
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .build();
//
//        Request request = new Request.Builder()
//                .url(url)
//                .post(requestBody)
//                .build();
//        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("register", "onFailure: ");
                e.printStackTrace();
                showResult("注册失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("register", "onResponse: ");
                int code = response.code();
                if(code == HttpURLConnection.HTTP_OK){
                    //String resposeBody = response.body().string();
                    showResult("注册成功");
                    Intent intent = new Intent(RegisterActivity.this,
                            LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    //Toast.makeText(ResultActivity.this,"超分辨率转换失败", Toast.LENGTH_SHORT).show();
                    Log.d("register", "register failed");
                    showResult("注册失败");
                }
            }
        });
    }

    public void showResult(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RegisterActivity.this,msg, Toast.LENGTH_SHORT).show();
                layout.removeView(mProBar);
            }
        });
    }

    private boolean judgePhoneNums(String phoneNums) {
        if (isMatchLength(phoneNums, 11)
                && isMobileNO(phoneNums)) {
            return true;
        }
        Toast.makeText(this, "手机号码输入有误！", Toast.LENGTH_SHORT).show();
        return false;
    }

    public static boolean isMatchLength(String str, int length) {
        if (str.isEmpty()) {
            return false;
        } else {
            return str.length() == length ? true : false;
        }
    }

    public static boolean isMobileNO(String mobileNums) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
         * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
         * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
         */
        String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }

    private void createProgressBar() {
        layout = (FrameLayout) findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        mProBar = new ProgressBar(this);
        mProBar.setLayoutParams(layoutParams);
        mProBar.setVisibility(View.VISIBLE);
        layout.addView(mProBar);
    }

    @Override
    protected void onDestroy() {
        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            // TODO: alert the user with a Snackbar/AlertDialog giving them the permission rationale
            // To use the Snackbar from the design support library, ensure that the activity extends
            // AppCompatActivity and uses the Theme.AppCompat theme.
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mTelephoneView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

}

