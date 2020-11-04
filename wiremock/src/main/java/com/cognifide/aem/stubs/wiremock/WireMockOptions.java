package com.cognifide.aem.stubs.wiremock;

import static com.github.tomakehurst.wiremock.extension.ExtensionLoader.valueAssignableFrom;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.cognifide.aem.stubs.core.util.ResolverAccessor;
import com.github.tomakehurst.wiremock.common.AsynchronousResponseSettings;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.common.HttpsSettings;
import com.github.tomakehurst.wiremock.common.JettySettings;
import com.github.tomakehurst.wiremock.common.Notifier;
import com.github.tomakehurst.wiremock.common.ProxySettings;
import com.github.tomakehurst.wiremock.core.MappingsSaver;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.extension.Extension;
import com.github.tomakehurst.wiremock.http.CaseInsensitiveKey;
import com.github.tomakehurst.wiremock.http.HttpServerFactory;
import com.github.tomakehurst.wiremock.http.ThreadPoolFactory;
import com.github.tomakehurst.wiremock.http.trafficlistener.DoNothingWiremockNetworkTrafficListener;
import com.github.tomakehurst.wiremock.http.trafficlistener.WiremockNetworkTrafficListener;
import com.github.tomakehurst.wiremock.security.Authenticator;
import com.github.tomakehurst.wiremock.security.NoAuthenticator;
import com.github.tomakehurst.wiremock.servlet.NotImplementedMappingsSaver;
import com.github.tomakehurst.wiremock.standalone.MappingsLoader;
import com.github.tomakehurst.wiremock.verification.notmatched.NotMatchedRenderer;
import com.github.tomakehurst.wiremock.verification.notmatched.PlainTextStubNotMatchedRenderer;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyMethods"})
class WireMockOptions implements Options {

  private final ResolverAccessor resolverAccessor;
  private final String rootPath;
  private final boolean requestJournalEnabled;
  private Integer maxRequestJournalEntries;
  private final Map<String, Extension> extensions;

  public WireMockOptions(ResolverAccessor resolverAccessor, String rootPath,
    boolean requestJournalEnabled, int maxRequestJournalEntries,
    Map<String, Extension> extensions) {
    this.resolverAccessor = resolverAccessor;
    this.rootPath = rootPath;
    this.requestJournalEnabled = requestJournalEnabled;
    this.maxRequestJournalEntries = maxRequestJournalEntries;
    this.extensions = extensions;
  }

  @Override
  public int portNumber() {
    return 0;
  }

  @Override
  public boolean getHttpDisabled() {
    return false;
  }

  @Override
  public HttpsSettings httpsSettings() {
    return new HttpsSettings(-1, "", "", "", null, "", "", false);
  }

  @Override
  public JettySettings jettySettings() {
    return null;
  }

  @Override
  public int containerThreads() {
    return 0;
  }

  @Override
  public boolean browserProxyingEnabled() {
    return false;
  }

  @Override
  public ProxySettings proxyVia() {
    return ProxySettings.NO_PROXY;
  }

  @Override
  public FileSource filesRoot() {
    return new WireMockFileSource(resolverAccessor, rootPath);
  }

  @Override
  public MappingsLoader mappingsLoader() {
    return stubMappings -> {}; // intentionally, loading is just done later
  }

  @Override
  public MappingsSaver mappingsSaver() {
    return new NotImplementedMappingsSaver();
  }

  @Override
  public Notifier notifier() {
    return null;
  }

  @Override
  public boolean requestJournalDisabled() {
    return !requestJournalEnabled;
  }

  @Override
  public Optional<Integer> maxRequestJournalEntries() {
    if(maxRequestJournalEntries > 0) {
      return Optional.of(maxRequestJournalEntries);
    }
    return Optional.absent();
  }

  @Override
  public String bindAddress() {
    return null;
  }

  @Override
  public List<CaseInsensitiveKey> matchingHeaders() {
    return Collections.emptyList();
  }

  @Override
  public boolean shouldPreserveHostHeader() {
    return false;
  }

  @Override
  public String proxyHostHeader() {
    return null;
  }

  @Override
  public HttpServerFactory httpServerFactory() {
    return null;
  }

  @Override
  public ThreadPoolFactory threadPoolFactory() {
    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Extension> Map<String, T> extensionsOfType(final Class<T> extensionType) {
    return (Map<String, T>) Maps.filterEntries(extensions, valueAssignableFrom(extensionType));
  }

  @Override
  public WiremockNetworkTrafficListener networkTrafficListener() {
    return new DoNothingWiremockNetworkTrafficListener();
  }

  @Override
  public Authenticator getAdminAuthenticator() {
    return new NoAuthenticator();
  }

  @Override
  public boolean getHttpsRequiredForAdminApi() {
    return false;
  }

  @Override
  public NotMatchedRenderer getNotMatchedRenderer() {
    return new PlainTextStubNotMatchedRenderer();
  }

  @Override
  public AsynchronousResponseSettings getAsynchronousResponseSettings() {
    return new AsynchronousResponseSettings(false, 0);
  }

  @Override
  public ChunkedEncodingPolicy getChunkedEncodingPolicy() {
    return ChunkedEncodingPolicy.NEVER;
  }

  @Override
  public boolean getGzipDisabled() {
    return false;
  }

  @Override
  public boolean getStubRequestLoggingDisabled() {
    return true;
  }
}
