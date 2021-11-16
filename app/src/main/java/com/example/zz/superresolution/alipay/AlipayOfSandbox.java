package com.example.zz.superresolution.alipay;

import java.util.Map;

import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.example.zz.superresolution.R;
import com.example.zz.superresolution.alipay.OrderInfoUtil2_0;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

/**
 *  重要说明：
 *  
 *  本 Demo 只是为了方便直接向商户展示支付宝的整个支付流程，所以将加签过程直接放在客户端完成
 *  （包括 OrderInfoUtil2_0_HK 和 OrderInfoUtil2_0）。
 *
 *  在真实 App 中，私钥（如 RSA_PRIVATE 等）数据严禁放在客户端，同时加签过程务必要放在服务端完成，
 *  否则可能造成商户私密数据泄露或被盗用，造成不必要的资金损失，面临各种安全风险。
 *
 *  Warning:
 *
 *  For demonstration purpose, the assembling and signing of the request parameters are done on
 *  the client side in this demo application.
 *
 *  However, in practice, both assembling and signing must be carried out on the server side.
 */
public class AlipayOfSandbox extends AppCompatActivity {
	
	/**
	 * 用于支付宝支付业务的入参 app_id。
	 */
	public static final String APPID = "2021000118644375";//"2021002192612567";
	
	/**
	 * 用于支付宝账户登录授权业务的入参 pid。
	 */
	public static final String PID = "2088621957037053";

	/**
	 * 用于支付宝账户登录授权业务的入参 target_id。
	 */
	public static final String TARGET_ID = "";

	/**
	 *  pkcs8 格式的商户私钥。
	 *
	 * 	如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个，如果两个都设置了，本 Demo 将优先
	 * 	使用 RSA2_PRIVATE。RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议商户使用
	 * 	RSA2_PRIVATE。
	 *
	 * 	建议使用支付宝提供的公私钥生成工具生成和获取 RSA2_PRIVATE。
	 * 	工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1
	 */
	public static final String RSA2_PRIVATE = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDVp6rCphTET7eDypjQp9nVCZ8UMMmhLuAPcpF5XvqVJATIWPVoochpN88gILzwh+Xp5Z8Gs5g0mHa12ZVGVgC7h8EGxVQbzos+GjK+k95c/ubiA7us0Kj+LV46NqHTUssh1N839l9DO5pVQetmwTfKncL4HZf7KXWrpCh9XZkcwRBGeMH1e64xWdhFz3Rb1a2W6VrPoka4GZP/78aAvrZb8lFote+pjfRSbHkZ4r4gLCTie5UaLM+KGetXBrqMhgPo3yuExP2+zbv+qFPVa4H0BJkKJWNCWPQ4+Y4z3I06pxhQjWA7yXbCcmFiOFh8B5+ch/msX9RyaAsPqDF5vyYLAgMBAAECggEADBNF22p3kgDPZmt055YVAa1hiEqZmrqDf1wCTNHJZ9R5ULl2ZUt/gDLfSfmPtCh6HhYUPtS3rYFQdEav4SHLOM4bYr6UWl36ELB6XbsNvkPJ2MxNNx8z7emygafEzaTIRkk5tHgAOlSMDHWFUHFrLdBszIkfpdpaBDFq+Grp1G4aexJLLjj5t7MQyHg4L8PnF1yJwdqC7zZPGqZIILqWFW4tOQfeAB1Jou04hCV3yUs6BSWXuQq8IqzXM3ETk8IbM3i2tGUNc+E5eyei3WCFrp0aiFhOWEUrt5jI10YEOBTAKBBmnAEWQh5GNKhyAqKG2TVMziZEndKLEGPLtI4TgQKBgQD3X8mUxfDETbEkVDg8xTMAXHozWYZFAeQQMf3c72q0CRFlobnZbDuB44Mj17SXY8u+6lwuowbc0BqTVoQoZodkaKyGW4TUU0uU6/DOVB0jNN62qWOBqkEYnNJKKh457nbYAOVGPyj/i7cnalq5oJJYJtsFIhzg8nDXw4UPdT9ViQKBgQDdGuGc1snzPgGwYcNnsXIZX8gYpM/tIn6duarye5OtLG7WRLKdtIZM0fZddTAXVvwXbeukfIHtYFuND/j2ced49s3dNJoozXdG1uy9AMurqWRI1/2vyrfthtTZ380DQZ22TWgOEjYmRD2xMgPdH7TDDf5fA7DKTSfLsw7x48EN8wKBgCYpMrGur2u5CICwNVS6nxmJKpjNULdIiWQuMhlpHa7UT0i8YC04Ux3AeTSITCWmkYHMKZhQMfjSaacdC5on5u8IbsTzTVRr3RcGJsnNlAkgWNMY4m7YUt53IsGqKauKK+86hCQIDk0Nwp1j/MbyDGFo7RSnpIjXHa1LTjWKQtDxAoGBANeuFjCBESR28vzUkOGApbV8ylrHwpAmWu6Oo+O/ijGeK0xWXQobg/pBwhoua0gJMQXF0tx0G+WrXgL/drMoL0y/K9fBLTPqIZT3qJ1BvmtoZIvuLegiTkKkiNMzTfAEPM0NOximyS2Q2cEU8dHs0M6Iwv56QiVFGDHLLCPnMHXjAoGARNhLMCxO7h0HjCUzev7vIE1PEZ80Kd/wFn5gWzVxdDt+dooZB6mnw/I168pCfCq76FPAa6f7Wb5isHrpm2pl5xhrd498L1Kfg9DIFaEJJrwFMKqmKGeRbjROXQoJNbTo38jh3ZapwKxOqMgEHwdCRaCuZoLWUHW8pffY8FNhB1k=";
	public static final String RSA_PRIVATE = "";
	
	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_AUTH_FLAG = 2;

	private String price = "0.01";

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unused")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				@SuppressWarnings("unchecked")
				PayResult payResult = new PayResult((Map<String, String>) msg.obj);
				/**
				 * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息
				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为9000则代表支付成功
				if (TextUtils.equals(resultStatus, "9000")) {
					// 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
					Log.d("alipay","支付成功 " + payResult);
					showAlert(AlipayOfSandbox.this, getString(R.string.pay_success) + payResult);
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("payResult",resultInfo);
					bundle.putString("resultStatus",resultStatus);
					intent.putExtras(bundle);
					setResult(1,intent);
					finish();
				} else {
					// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
					showAlert(AlipayOfSandbox.this, getString(R.string.pay_failed) + payResult);
					Intent intent = new Intent();
					setResult(2,intent);
					finish();
				}
				break;
			}
			case SDK_AUTH_FLAG: {
				@SuppressWarnings("unchecked")
				AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
				String resultStatus = authResult.getResultStatus();

				// 判断resultStatus 为“9000”且result_code
				// 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
				if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
					// 获取alipay_open_id，调支付时作为参数extern_token 的value
					// 传入，则支付账户为该授权账户
					showAlert(AlipayOfSandbox.this, getString(R.string.auth_success) + authResult);
				} else {
					// 其他状态值则为授权失败
					showAlert(AlipayOfSandbox.this, getString(R.string.auth_failed) + authResult);
				}
				break;
			}
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_main);

		Bundle bundle = getIntent().getExtras();
		price = bundle.getString("totalprice");
	}

	/**
	 * 支付宝支付业务示例
	 */
	public void payV2(View v) {
		if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
			showAlert(this, getString(R.string.error_missing_appid_rsa_private));
			return;
		}
	
		/*
		 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
		 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
		 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险； 
		 * 
		 * orderInfo 的获取必须来自服务端；
		 */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
		Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2, price);
		String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

		String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
		String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
		final String orderInfo = orderParam + "&" + sign;
		
		final Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask alipay = new PayTask(AlipayOfSandbox.this);
				Map<String, String> result = alipay.payV2(orderInfo, true);
				Log.i("msp", result.toString());
				
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * 支付宝账户授权业务示例
	 */
	public void authV2(View v) {
		if (TextUtils.isEmpty(PID) || TextUtils.isEmpty(APPID)
				|| (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))
				|| TextUtils.isEmpty(TARGET_ID)) {
			showAlert(this, getString(R.string.error_auth_missing_partner_appid_rsa_private_target_id));
			return;
		}

		/*
		 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
		 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
		 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险； 
		 * 
		 * authInfo 的获取必须来自服务端；
		 */
		boolean rsa2 = (RSA2_PRIVATE.length() > 0);
		Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap(PID, APPID, TARGET_ID, rsa2);
		String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);
		
		String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
		String sign = OrderInfoUtil2_0.getSign(authInfoMap, privateKey, rsa2);
		final String authInfo = info + "&" + sign;
		Runnable authRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造AuthTask 对象
				AuthTask authTask = new AuthTask(AlipayOfSandbox.this);
				// 调用授权接口，获取授权结果
				Map<String, String> result = authTask.authV2(authInfo, true);

				Message msg = new Message();
				msg.what = SDK_AUTH_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread authThread = new Thread(authRunnable);
		authThread.start();
	}
	
	/**
	 * 获取支付宝 SDK 版本号。
	 */
	public void showSdkVersion(View v) {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		showAlert(this, getString(R.string.alipay_sdk_version_is) + version);
	}

	/**
	 * 将 H5 网页版支付转换成支付宝 App 支付的示例
	 */
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	public void h5Pay(View v) {
		WebView.setWebContentsDebuggingEnabled(true);
		Intent intent = new Intent(this, H5PayDemoActivity.class);
		Bundle extras = new Bundle();

		/*
		 * URL 是要测试的网站，在 Demo App 中会使用 H5PayDemoActivity 内的 WebView 打开。
		 *
		 * 可以填写任一支持支付宝支付的网站（如淘宝或一号店），在网站中下订单并唤起支付宝；
		 * 或者直接填写由支付宝文档提供的“网站 Demo”生成的订单地址
		 * （如 https://mclient.alipay.com/h5Continue.htm?h5_route_token=303ff0894cd4dccf591b089761dexxxx）
		 * 进行测试。
		 * 
		 * H5PayDemoActivity 中的 MyWebViewClient.shouldOverrideUrlLoading() 实现了拦截 URL 唤起支付宝，
		 * 可以参考它实现自定义的 URL 拦截逻辑。
		 *
		 * 注意：WebView 的 shouldOverrideUrlLoading(url) 无法拦截直接调用 open(url) 打开的第一个 url，
		 * 所以直接设置 url = "https://mclient.alipay.com/cashier/mobilepay.htm......" 是无法完成网页转 Native 的。
		 * 如果需要拦截直接打开的支付宝网页支付 URL，可改为使用 shouldInterceptRequest(view, request) 。
		 */
		String url = "https://m.taobao.com";
		extras.putString("url", url);
		intent.putExtras(extras);
		startActivity(intent);
	}

	private static void showAlert(Context ctx, String info) {
		showAlert(ctx, info, null);
	}

	private static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
		new AlertDialog.Builder(ctx)
				.setMessage(info)
				.setPositiveButton(R.string.confirm, null)
				.setOnDismissListener(onDismiss)
				.show();
	}

	private static void showToast(Context ctx, String msg) {
		Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
	}

	private static String bundleToString(Bundle bundle) {
		if (bundle == null) {
			return "null";
		}
		final StringBuilder sb = new StringBuilder();
		for (String key: bundle.keySet()) {
			sb.append(key).append("=>").append(bundle.get(key)).append("\n");
		}
		return sb.toString();
	}
}
