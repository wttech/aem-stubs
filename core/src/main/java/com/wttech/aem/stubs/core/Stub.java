package com.wttech.aem.stubs.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Stub {

    String getId();

    boolean isRequested(HttpServletRequest request) throws StubException;

    void respond(HttpServletRequest request, HttpServletResponse response) throws StubException;
}
