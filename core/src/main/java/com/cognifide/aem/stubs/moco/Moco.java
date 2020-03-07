package com.cognifide.aem.stubs.moco;

import com.github.dreamhead.moco.HttpServer;

public interface Moco {
  void restartServer();

  HttpServer getServer();
}
