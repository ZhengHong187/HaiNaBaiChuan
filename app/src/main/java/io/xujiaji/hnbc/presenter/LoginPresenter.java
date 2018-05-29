/*
 * Copyright 2018 XuJiaji
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.xujiaji.hnbc.presenter;

import android.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.login.LoginResult;

import io.xujiaji.hnbc.config.C;
import io.xujiaji.hnbc.contract.LoginContract;
import io.xujiaji.hnbc.model.entity.User;
import io.xujiaji.hnbc.model.net.NetRequest;
import io.xujiaji.hnbc.utils.MD5Util;
import io.xujiaji.hnbc.utils.ToastUtil;
import io.xujiaji.hnbc.utils.check.LoginCheck;
import io.xujiaji.xmvp.presenters.XBasePresenter;

/**
 * Created by jiana on 16-11-4.
 */

public class LoginPresenter extends XBasePresenter<LoginContract.View> implements LoginContract.Presenter {

    public LoginPresenter(LoginContract.View view) {
        super(view);
    }

    @Override
    public void requestLogin(String name, String password) {
        String nameErr = LoginCheck.checkAccount(name);
        String passwordErr = LoginCheck.checkPassword(password);
        if (nameErr != null) {
            view.nameFormatError(nameErr);
            return;
        }

        if (passwordErr != null) {
            view.passwordFormatError(passwordErr);
            return;
        }

        NetRequest.Instance().login(name, password, new NetRequest.RequestListener<User>() {
            @Override
            public void success(User user) {
                view.callLoginSuccess();
            }

            @Override
            public void error(String err) {
                view.callLoginFail(err);
            }
        });
    }

    @Override
    public void requestSina() {
        ToastUtil.getInstance().showShortT("未知区域：新浪登录");
    }

    @Override
    public void requestQQ() {
        NetRequest.Instance().thirdLogin(((Fragment) view).getActivity()
                , C.login.QQ
                , new NetRequest.ThirdLoginLister<String>() {
                    @Override
                    public void thirdLoginSuccess() {
                        view.showDialog();
                    }

                    @Override
                    public void success(String s) {
                        view.callLoginSuccess();
                    }

                    @Override
                    public void error(String err) {
                        view.callLoginFail(err);
                    }
                });
    }

    @Override
    public void requestWeiXin() {
        ToastUtil.getInstance().showShortT("未知区域：微信登录");
    }

    @Override
    public void requestFacebook(LoginResult loginResult) {
        AccessToken accessToken = loginResult.getAccessToken();
        NetRequest.Instance().facebookLogin(accessToken.getUserId(), MD5Util.getMD5(accessToken.getUserId()),
                new NetRequest.RequestListener<User>() {
                    @Override
                    public void success(User user) {
                        view.callLoginSuccess();
                    }

                    @Override
                    public void error(String err) {
                        view.callLoginFail(err);
                    }
                });

    }

//    @Override
//    public void requestFacebook() {
//        NetRequest.Instance().thirdLogin(((Fragment) view).getActivity(), C.login.FACEBOOK,
//                new NetRequest.RequestListener<String>() {
//                    @Override
//                    public void success(String s) {
//
//                    }
//
//                    @Override
//                    public void error(String err) {
//
//                    }
//                });
//    }

}
