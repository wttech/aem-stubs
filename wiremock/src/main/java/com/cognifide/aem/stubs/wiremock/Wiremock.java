package com.cognifide.aem.stubs.wiremock;

import java.util.Map;

import com.github.tomakehurst.wiremock.client.CountMatchingStrategy;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.matching.BinaryEqualToPattern;
import com.github.tomakehurst.wiremock.matching.MatchesXPathPattern;
import com.github.tomakehurst.wiremock.matching.MultipartValuePatternBuilder;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import com.github.tomakehurst.wiremock.matching.ValueMatcher;

@SuppressWarnings({"PMD.ClassNamingConventions", "PMD.TooManyMethods", "PMD.ExcessivePublicCount"})
final public class Wiremock {

  private Wiremock(){
    //pmd
  }
  public static StringValuePattern equalTo(String value) {
    return WireMock.equalTo(value);
  }

  public static BinaryEqualToPattern binaryEqualTo(byte[] content) {
    return WireMock.binaryEqualTo(content);
  }

  public static BinaryEqualToPattern binaryEqualTo(String content) {
    return WireMock.binaryEqualTo(content);
  }

  public static StringValuePattern equalToIgnoreCase(String value) {
    return WireMock.equalToIgnoreCase(value);
  }

  public static StringValuePattern equalToJson(String value) {
    return WireMock.equalToJson(value);
  }

  @SuppressWarnings("PMD.LongVariable")
  public static StringValuePattern equalToJson(String value, boolean ignoreArrayOrder, boolean ignoreExtraElements) {
    return WireMock.equalToJson(value, ignoreArrayOrder, ignoreExtraElements);
  }

  public static StringValuePattern matchingJsonPath(String value) {
    return WireMock.matchingJsonPath(value);
  }

  public static StringValuePattern matchingJsonPath(String value, StringValuePattern valuePattern) {
    return WireMock.matchingJsonPath(value, valuePattern);
  }

  public static StringValuePattern equalToXml(String value) {
    return WireMock.equalToXml(value);
  }

  public static MatchesXPathPattern matchingXPath(String value) {
    return WireMock.matchingXPath(value);
  }

  public static StringValuePattern matchingXPath(String value, Map<String, String> namespaces) {
    return WireMock.matchingXPath(value, namespaces);
  }

  public static StringValuePattern matchingXPath(String value, StringValuePattern valuePattern) {
    return new MatchesXPathPattern(value, valuePattern);
  }

  public static StringValuePattern containing(String value) {
    return WireMock.containing(value);
  }

  public static StringValuePattern matching(String regex) {
    return WireMock.matching(regex);
  }

  public static StringValuePattern notMatching(String regex) {
    return WireMock.notMatching(regex);
  }

  public static StringValuePattern absent() {
    return WireMock.absent();
  }

  public static UrlPattern urlEqualTo(String testUrl) {
    return WireMock.urlEqualTo(testUrl);
  }

  public static UrlPattern urlMatching(String urlRegex) {
    return WireMock.urlMatching(urlRegex);
  }

  public static UrlPathPattern urlPathEqualTo(String testUrl) {
    return WireMock.urlPathEqualTo(testUrl);
  }

  public static UrlPathPattern urlPathMatching(String urlRegex) {
    return WireMock.urlPathMatching(urlRegex);
  }

  public static UrlPattern anyUrl() {
    return WireMock.anyUrl();
  }

  public static CountMatchingStrategy lessThan(int expected) {
    return WireMock.lessThan(expected);
  }

  public static CountMatchingStrategy lessThanOrExactly(int expected) {
    return WireMock.lessThanOrExactly(expected);
  }

  public static CountMatchingStrategy exactly(int expected) {
    return WireMock.exactly(expected);
  }

  public static CountMatchingStrategy moreThanOrExactly(int expected) {
    return WireMock.moreThanOrExactly(expected);
  }

  public static CountMatchingStrategy moreThan(int expected) {
    return WireMock.moreThan(expected);
  }

  public static MappingBuilder get(UrlPattern urlPattern) {
    return WireMock.get(urlPattern);
  }

  public static MappingBuilder post(UrlPattern urlPattern) {
    return WireMock.post(urlPattern);
  }

  public static MappingBuilder put(UrlPattern urlPattern) {
    return WireMock.put(urlPattern);
  }

  public static MappingBuilder delete(UrlPattern urlPattern) {
    return WireMock.delete(urlPattern);
  }

  public static MappingBuilder patch(UrlPattern urlPattern) {
    return WireMock.patch(urlPattern);
  }

  public static MappingBuilder head(UrlPattern urlPattern) {
    return WireMock.head(urlPattern);
  }

  public static MappingBuilder options(UrlPattern urlPattern) {
    return WireMock.options(urlPattern);
  }

  public static MappingBuilder trace(UrlPattern urlPattern) {
    return WireMock.trace(urlPattern);
  }

  public static MappingBuilder any(UrlPattern urlPattern) {
    return WireMock.any(urlPattern);
  }

  public static MappingBuilder request(String method, UrlPattern urlPattern) {
    return WireMock.request(method, urlPattern);
  }
  public static MappingBuilder requestMatching(String matcherName) {
    return WireMock.requestMatching(matcherName);
  }
  public static MappingBuilder requestMatching(String matcherName, Parameters parameters) {
    return WireMock.requestMatching(matcherName, parameters);
  }

  public static MappingBuilder requestMatching(ValueMatcher<Request> requestMatcher) {
    return WireMock.requestMatching(requestMatcher);
  }

  public static ResponseDefinitionBuilder aResponse() {
    return WireMock.aResponse();
  }

  @SuppressWarnings("PMD.ShortMethodName")
  public static ResponseDefinitionBuilder ok() {
    return WireMock.ok();
  }

  @SuppressWarnings("PMD.ShortMethodName")
  public static ResponseDefinitionBuilder ok(String body) {
    return WireMock.ok(body);
  }

  public static ResponseDefinitionBuilder okForContentType(String contentType, String body) {
    return WireMock.okForContentType(contentType, body);
  }

  public static ResponseDefinitionBuilder okJson(String body) {
    return WireMock.okJson(body);
  }

  public static ResponseDefinitionBuilder okXml(String body) {
    return WireMock.okXml(body);
  }

  public static ResponseDefinitionBuilder okTextXml(String body) {
    return WireMock.okTextXml(body);
  }

  public static MappingBuilder proxyAllTo(String url) {
    return WireMock.proxyAllTo(url);
  }

  public static MappingBuilder get(String url) {
    return WireMock.get(url);
  }

  public static MappingBuilder post(String url) {
    return WireMock.post(url);
  }

  public static MappingBuilder put(String url) {
    return WireMock.put(url);
  }

  public static MappingBuilder delete(String url) {
    return WireMock.delete(url);
  }

  public static ResponseDefinitionBuilder created() {
    return WireMock.created();
  }

  public static ResponseDefinitionBuilder noContent() {
    return WireMock.noContent();
  }

  public static ResponseDefinitionBuilder permanentRedirect(String location) {
    return WireMock.permanentRedirect(location);
  }

  public static ResponseDefinitionBuilder temporaryRedirect(String location) {
    return WireMock.temporaryRedirect(location);
  }

  public static ResponseDefinitionBuilder seeOther(String location) {
    return WireMock.seeOther(location);
  }

  public static ResponseDefinitionBuilder badRequest() {
    return WireMock.badRequest();
  }

  public static ResponseDefinitionBuilder badRequestEntity() {
    return WireMock.badRequestEntity();
  }

  public static ResponseDefinitionBuilder unauthorized() {
    return WireMock.unauthorized();
  }

  public static ResponseDefinitionBuilder forbidden() {
    return WireMock.forbidden();
  }

  public static ResponseDefinitionBuilder notFound() {
    return WireMock.notFound();
  }

  public static ResponseDefinitionBuilder serverError() {
    return WireMock.serverError();
  }

  public static ResponseDefinitionBuilder serviceUnavailable() {
    return WireMock.serviceUnavailable();
  }

  public static ResponseDefinitionBuilder status(int status) {
    return WireMock.status(status);
  }


  public static RequestPatternBuilder getRequestedFor(UrlPattern urlPattern) {
    return WireMock.getRequestedFor(urlPattern);
  }

  public static RequestPatternBuilder postRequestedFor(UrlPattern urlPattern) {
    return WireMock.postRequestedFor(urlPattern);
  }

  public static RequestPatternBuilder putRequestedFor(UrlPattern urlPattern) {
    return WireMock.putRequestedFor(urlPattern);
  }

  public static RequestPatternBuilder deleteRequestedFor(UrlPattern urlPattern) {
    return WireMock.deleteRequestedFor(urlPattern);
  }

  public static RequestPatternBuilder patchRequestedFor(UrlPattern urlPattern) {
    return WireMock.patchRequestedFor(urlPattern);
  }

  public static RequestPatternBuilder headRequestedFor(UrlPattern urlPattern) {
    return WireMock.headRequestedFor(urlPattern);
  }

  public static RequestPatternBuilder optionsRequestedFor(UrlPattern urlPattern) {
    return WireMock.optionsRequestedFor(urlPattern);
  }

  public static RequestPatternBuilder traceRequestedFor(UrlPattern urlPattern) {
    return WireMock.traceRequestedFor(urlPattern);
  }

  public static RequestPatternBuilder anyRequestedFor(UrlPattern urlPattern) {
    return WireMock.anyRequestedFor(urlPattern);
  }

  public static RequestPatternBuilder requestMadeFor(String customMatcherName, Parameters parameters) {
    return WireMock.requestMadeFor(customMatcherName, parameters);
  }

  public static RequestPatternBuilder requestMadeFor(ValueMatcher<Request> requestMatcher) {
    return WireMock.requestMadeFor(requestMatcher);
  }

  public static MultipartValuePatternBuilder aMultipart() {
    return WireMock.aMultipart();
  }

  public static MultipartValuePatternBuilder aMultipart(String name) {
    return WireMock.aMultipart(name);
  }
}
