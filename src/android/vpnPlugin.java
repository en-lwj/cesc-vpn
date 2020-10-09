package com.cesc.ewater.cordovaPlugin;


import com.sangfor.ssl.BaseMessage;
import com.sangfor.ssl.LoginResultListener;
import com.sangfor.ssl.SFException;
import com.sangfor.ssl.SangforAuthManager;
import com.sangfor.ssl.common.ErrorCode;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.net.URL;
import java.util.Random;


public class vpnPlugin extends CordovaPlugin implements LoginResultListener {
    private SangforAuthManager mSFManager = null;
    private VPNMode mVpnMode = VPNMode.L3VPN;
    private URL mVpnAddressURL = null;
    private String mVpnAddress = "https://61.144.51.116";
    private String mUserName = "public";
    private String mUserPassword = "PUB_USER@2019";
    private CallbackContext callbackContext;
	private String[] mUserNameArray= {"PUB_USER1","PUB_USER3"};

    /**
     * 初始化登录参数
     */
    private void initLoginParms() {

        // 1.构建SangforAuthManager对象
        mSFManager = SangforAuthManager.getInstance();
        // 2.设置VPN认证结果回调
        try {
            mSFManager.setLoginResultListener(this);
        } catch (SFException e) {
//            Log.info(TAG, "SFException:%s", e);
        }

        //3.设置登录超时时间，单位为秒
        mSFManager.setAuthConnectTimeOut(8);
    }

    @Override
    public void onLoginFailed(ErrorCode errorCode, String s) {
        callbackContext.error("连接VPN失败:" + s + "错误代码编码222" + errorCode);
    }

    @Override
    public void onLoginProcess(int i, BaseMessage baseMessage) {
    }

    @Override
    public void onLoginSuccess() {
        callbackContext.success("连接VPN成功");
    }

    @Override
    public boolean execute(String action, String rawArgs, CallbackContext callbackContext) throws JSONException {
        return super.execute(action, rawArgs, callbackContext);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        return super.execute(action, args, callbackContext);
    }

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("conn")) {
            //以下用原生代码打开一个Act1Activity（可以理解为让界面跳转到Act1Activity这个界面）
            //PS：原生很多地方都要获取当前Activity的实例对象（如果在Activity里就用this），在CordovaPlugin用的是cordova.getActivity()
            this.callbackContext = callbackContext;
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * 初始登录统一接口
                     */
                    initLoginParms();

                    //开启登录进度框
                    //createWaitingProgressDialog();
                    try {
                        mVpnAddressURL = new URL(mVpnAddress);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                          // addStatusChangedListener(); //添加vpn状态变化监听器
                        //该接口做了两件事：1.vpn初始化；2.用户名/密码主认证过程
						mUserName=mUserNameArray[new Random().nextInt(2)];
                        mSFManager.startPasswordAuthLogin(cordova.getActivity().getApplication(), cordova.getActivity(), mVpnMode,
                                mVpnAddressURL, mUserName, mUserPassword);

                    } catch (SFException e) {
                                //关闭登录进度框
        //            cancelWaitingProgressDialog();
        //            Log.info(TAG, "SFException:%s", e);
                    }
                }
            });
            return true;
        }
        if (action.equals("logout")) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    // 注销VPN登录.
                    SangforAuthManager.getInstance().vpnLogout();
                    cordova.getActivity().finish();
                }
            });
            this.callbackContext = callbackContext;
            callbackContext.success("注销VPN成功");
            return true;
        }
        return false;
    }
}
