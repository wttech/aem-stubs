package com.company.wiremockonaem.aem.groovy.executor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;

import org.apache.sling.api.SlingHttpServletResponse;

public class DummyResponse implements SlingHttpServletResponse {
  @Override
  public void addCookie(Cookie cookie) {
    // not used
  }

  @Override
  public boolean containsHeader(String name) {
    return false;
  }

  @Override
  public String encodeURL(String url) {
    return url;
  }

  @Override
  public String encodeRedirectURL(String url) {
    return url;
  }

  @Override
  public String encodeUrl(String url) {
    return url;
  }

  @Override
  public String encodeRedirectUrl(String url) {
    return url;
  }

  @Override
  public void sendError(int sc, String msg) throws IOException {
    // not used
  }

  @Override
  public void sendError(int sc) throws IOException {
    // not used
  }

  @Override
  public void sendRedirect(String location) throws IOException {
    // not used
  }

  @Override
  public void setDateHeader(String name, long date) {
    // not used
  }

  @Override
  public void addDateHeader(String name, long date) {
    // not used
  }

  @Override
  public void setHeader(String name, String value) {
    // not used
  }

  @Override
  public void addHeader(String name, String value) {
    // not used
  }

  @Override
  public void setIntHeader(String name, int value) {
    // not used
  }

  @Override
  public void addIntHeader(String name, int value) {
    // not used
  }

  @Override
  public void setStatus(int sc) {
    // not used
  }

  @Override
  public void setStatus(int sc, String sm) {
    // not used
  }

  public int getStatus() {
    return 200;
  }

  public String getHeader(String name) {
    return null;
  }

//  @Override
  public Collection<String> getHeaders(String name) {
    return Collections.emptyList();
  }

//  @Override
  public Collection<String> getHeaderNames() {
    return Collections.emptyList();
  }

  @Override
  public String getCharacterEncoding() {
    return "UTF-8";
  }

  @Override
  public String getContentType() {
    return null;
  }

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    return null;
  }

  @Override
  public PrintWriter getWriter() throws IOException {
    return null;
  }

  @Override
  public void setCharacterEncoding(String charset) {
    // not used
  }

  @Override
  public void setContentLength(int len) {
    // not used
  }

//  @Override
  public void setContentLengthLong(long l) {
    // not used
  }

  @Override
  public void setContentType(String type) {
    // not used
  }

  @Override
  public void setBufferSize(int size) {
    // not used
  }

  @Override
  public int getBufferSize() {
    return 0;
  }

  @Override
  public void flushBuffer() throws IOException {
    // not used
  }

  @Override
  public void resetBuffer() {
    // not used
  }

  @Override
  public boolean isCommitted() {
    return false;
  }

  @Override
  public void reset() {
    // not used
  }

  @Override
  public void setLocale(Locale loc) {
    // not used
  }

  @Override
  public Locale getLocale() {
    return null;
  }

  @Override
  public <T> T adaptTo(Class<T> arg0) {
    return null;
  }
}
