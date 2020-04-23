package com.cognifide.aem.stubs.wiremock;

import static com.github.tomakehurst.wiremock.extension.ExtensionLoader.valueAssignableFrom;
import static com.google.common.collect.Maps.newLinkedHashMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.cognifide.aem.stubs.core.utils.ResolverAccessor;
import com.cognifide.aem.stubs.wiremock.jcr.JcrFileReader;
import com.cognifide.aem.stubs.wiremock.transformers.PebbleTransformer;
import com.github.tomakehurst.wiremock.common.AsynchronousResponseSettings;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.common.HttpsSettings;
import com.github.tomakehurst.wiremock.common.JettySettings;
import com.github.tomakehurst.wiremock.common.Notifier;
import com.github.tomakehurst.wiremock.common.ProxySettings;
import com.github.tomakehurst.wiremock.core.MappingsSaver;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.extension.Extension;
import com.github.tomakehurst.wiremock.extension.ExtensionLoader;
import com.github.tomakehurst.wiremock.http.CaseInsensitiveKey;
import com.github.tomakehurst.wiremock.http.HttpServerFactory;
import com.github.tomakehurst.wiremock.http.ThreadPoolFactory;
import com.github.tomakehurst.wiremock.http.trafficlistener.DoNothingWiremockNetworkTrafficListener;
import com.github.tomakehurst.wiremock.http.trafficlistener.WiremockNetworkTrafficListener;
import com.github.tomakehurst.wiremock.security.Authenticator;
import com.github.tomakehurst.wiremock.security.NoAuthenticator;
import com.github.tomakehurst.wiremock.servlet.NotImplementedMappingsSaver;
import com.github.tomakehurst.wiremock.standalone.JsonFileMappingsSource;
import com.github.tomakehurst.wiremock.standalone.MappingsLoader;
import com.github.tomakehurst.wiremock.verification.notmatched.NotMatchedRenderer;
import com.github.tomakehurst.wiremock.verification.notmatched.PlainTextStubNotMatchedRenderer;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;

class WiremockConfig implements Options {
  private final ResolverAccessor resolverAccessor;
  private final String rootPath;
  private Map<String, Extension> extensions = newLinkedHashMap();


  WiremockConfig(ResolverAccessor resolverAccessor, String rootPath) {
    this.resolverAccessor = resolverAccessor;
    this.rootPath = rootPath;
    addExtenstions();
  }

  private void addExtenstions() {
    JcrFileReader jcrFileReader= new JcrFileReader(resolverAccessor, rootPath);
    extensions.putAll(ExtensionLoader.asMap(
      Arrays.asList(new PebbleTransformer(jcrFileReader))));
  }

  @Override
  public int portNumber() {
    return 0;
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
    return new WiremockFileSource(resolverAccessor, rootPath);
  }

  @Override
  public MappingsLoader mappingsLoader() {
    return new JsonFileMappingsSource(filesRoot()); // TODO
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
    return false;
  }

  @Override
  public Optional<Integer> maxRequestJournalEntries() {
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
}
