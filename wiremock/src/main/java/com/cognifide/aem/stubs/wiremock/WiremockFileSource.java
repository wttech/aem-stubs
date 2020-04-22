package com.cognifide.aem.stubs.wiremock;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aem.stubs.core.utils.ResolverAccessor;
import com.github.tomakehurst.wiremock.common.BinaryFile;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.common.TextFile;

class WiremockFileSource implements FileSource {

  private static final Logger LOG = LoggerFactory.getLogger(WiremockFileSource.class);
  private final ResolverAccessor resolverAccessor;
  private final String filesPath;

  WiremockFileSource(ResolverAccessor resolverAccessor, String rootPath) {
    this.resolverAccessor = resolverAccessor;
    filesPath = String.format("%s/files", rootPath);
  }

  @Override
  public BinaryFile getBinaryFileNamed(String name) {
    try {
      return new BinaryFile(toUri(name)) {
        @Override
        public InputStream getStream() {
          return getInputStream(name);
        }
      };
    } catch (Exception e) {
      LOG.error("AEM Stubs cannot read file {}", name, e);
      throw new IllegalStateException(e);
    }
  }

  @Override
  public TextFile getTextFileNamed(String name) {
    try {
      return new TextFile(toUri(name)) {
        @Override
        public InputStream getStream() {
          return getInputStream(name);
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
    return filesPath;
  }

  @Override
  public URI getUri() {
    try {
      return new URI("aem", null, this.filesPath, null);
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

  private InputStream getInputStream(String name) {
    String path = String.format("%s/jcr:content", getAbsolutePath(name));
    return resolverAccessor.resolve(r -> r.getResource(path).adaptTo(InputStream.class));
  }

  private String getAbsolutePath(String name){
    return String.format("%s/%s", filesPath, name);
  }

  private URI toUri(String name) throws URISyntaxException {
    String absolutePath = getAbsolutePath(name);
    return new URI("aem", null, absolutePath, null);
  }
}
