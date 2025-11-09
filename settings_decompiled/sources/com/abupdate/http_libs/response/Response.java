package com.abupdate.http_libs.response;

import com.abupdate.http_libs.exception.HttpException;
/* loaded from: classes.dex */
public interface Response {
    String getContent();

    HttpException getException();

    boolean isResultOk();
}
