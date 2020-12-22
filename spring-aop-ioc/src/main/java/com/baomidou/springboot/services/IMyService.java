package com.baomidou.springboot.services;

import com.gitee.linzl.commons.api.ApiResult;

public interface IMyService {
    default public void add() {
    }

    default public void doSomeThing(String someThing) {
    }

    public ApiResult doSomeThing2(String someThing);

    public void doSomeThing3(String someThing);
    public ApiResult doSomeThing4(String someThing);
    public ApiResult doSomeThing5(String someThing);
}
