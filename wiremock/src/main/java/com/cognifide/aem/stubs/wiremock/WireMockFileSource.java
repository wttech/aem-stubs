package com.cognifide.aem.stubs.wiremock;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import com.cognifide.aem.stubs.wiremock.util.JcrFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aem.stubs.core.util.ResolverAccessor;
import com.github.tomakehurst.wiremock.common.BinaryFile;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.common.TextFile;

class WireMockFileSource implements FileSource {

  private static final Logger LOG = LoggerFactory.getLogger(WireMockFileSource.class);

  private final JcrFileReader jcrFileReader;

  private final String rootPath;

  public WireMockFileSource(ResolverAccessor resolverAccessor, String rootPath) {
    this.jcrFileReader = new JcrFileReader(resolverAccessor, rootPath);
    this.rootPath = rootPath;
  }

  @Override
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  public BinaryFile getBinaryFileNamed(String name) {
    try {
      return new BinaryFile(toUri(name)) {
        @Override
        public InputStream getStream() {
          return jcrFileReader.readStream(name);
        }
      };
    } catch (Exception e) {
      LOG.error("AEM Stubs cannot read file {}", name, e);
      throw new IllegalStateException(e);
    }
  }

  @Override
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  public TextFile getTextFileNamed(String name) {
    try {
      return new TextFile(toUri(name)) {
        @Override
        public InputStream getStream() {
          return jcrFileReader.readStream(name);
        }
      };
    } catch (Exception e) {
      LOG.error("AEM Stubs cannot read file {}", name, e);
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void createIfNecessary() {
    //ignore
  }

  @Override
  public FileSource child(String subDirectoryName) {
    return this;
  }

  @Override
  public String getPath() {
    return rootPath;
  }

  @Override
  public URI getUri() {
    try {
      return toUri(rootPath);
    } catch (URISyntaxException e) {
      LOG.error("AEM Stubs cannot create URI", e);
      throw new IllegalStateException(e);
    }
  }

  @Override
  public List<TextFile> listFilesRecursively() {
    return Collections.emptyList();
  }

  @Override
  public void writeTextFile(String name, String contents) {
    //ignore
  }

  @Override
  public void writeBinaryFile(String name, byte[] contents) {
    //ignore
  }

  @Override
  public boolean exists() {
    return true;
  }

  @Override
  public void deleteFile(String name) {
    //ignore
  }

  private URI toUri(String name) throws URISyntaxException {
    String absolutePath = jcrFileReader.getAbsolutePath(name);
    return new URI("aem", null, absolutePath, null);
  }
}