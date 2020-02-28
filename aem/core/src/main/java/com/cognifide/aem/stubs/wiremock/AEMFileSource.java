package com.cognifide.aem.stubs.wiremock;

import java.io.File;

import org.osgi.framework.FrameworkUtil;

import com.github.tomakehurst.wiremock.common.AbstractFileSource;
import com.github.tomakehurst.wiremock.common.FileSource;

class AEMFileSource extends AbstractFileSource {
  private final String rootPath;

  AEMFileSource(String rootPath) {
    super(getRootFile(rootPath));
    this.rootPath = rootPath;
  }

  private static File getRootFile(String rootPath) {
    return FrameworkUtil.getBundle(AEMFileSource.class).getBundleContext().getDataFile(rootPath);
  }


  @Override
  public FileSource child(String subDirectoryName) {
    return new AEMFileSource(rootPath + '/' + subDirectoryName);
  }

  @Override
  protected boolean readOnly() {
    return true;
  }
}
