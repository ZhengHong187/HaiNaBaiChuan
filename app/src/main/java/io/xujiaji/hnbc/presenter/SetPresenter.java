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

import java.io.File;

import io.xujiaji.hnbc.contract.SetContract;
import io.xujiaji.hnbc.utils.FileUtils;
import io.xujiaji.hnbc.utils.LogUtil;
import io.xujiaji.xmvp.presenters.XBasePresenter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by jiana on 16-11-20.
 * 设置
 */

public class SetPresenter extends XBasePresenter<SetContract.View> implements SetContract.Presenter {
    public SetPresenter(SetContract.View view) {
        super(view);
    }

    private String cacheSize = "0.0KB";

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void scanCacheSize() {
        Observable.from(FileUtils.getAllCacheFile())
                .flatMap(new Func1<File, Observable<File>>() {
                    @Override
                    public Observable<File> call(File file) {
                        return FileUtils.listFiles(file);
                    }
                })
                .map(new Func1<File, Long>() {
                    @Override
                    public Long call(File file) {
                        LogUtil.e3(file.getAbsolutePath());
                        return file.length();
                    }
                })
                .scan(new Func2<Long, Long, Long>() {

                    @Override
                    public Long call(Long aLong, Long aLong2) {
                        return aLong + aLong2;
                    }
                })
                .map(new Func1<Long, String>() {
                    @Override
                    public String call(Long aLong) {
                        return FileUtils.formatSize(aLong);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        view.showCacheSize(cacheSize);
                        cacheSize = "0.0KB";
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        view.showCacheSize("扫描失败！");
                    }

                    @Override
                    public void onNext(String aLong) {
                        cacheSize = aLong;
                        view.showCacheSize(cacheSize);
                    }
                });
    }

    @Override
    public void cleanCache() {
        Observable.from(FileUtils.getAllCacheFile())
                .flatMap(new Func1<File, Observable<File>>() {
                    @Override
                    public Observable<File> call(File file) {
                        return FileUtils.listFiles(file);
                    }
                })
                .doOnNext(new Action1<File>() {
                    @Override
                    public void call(File file) {
                        file.delete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<File>() {
                    @Override
                    public void onCompleted() {
                        view.showCleanCacheSuccess();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        view.showCleanCacheFail("清理失败！");
                    }

                    @Override
                    public void onNext(File file) {

                    }
                });
    }
}
