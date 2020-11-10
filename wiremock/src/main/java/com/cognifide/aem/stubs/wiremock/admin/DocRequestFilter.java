package com.cognifide.aem.stubs.wiremock.admin;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.responseDefinition;
import static com.google.common.io.ByteStreams.toByteArray;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.framework.FrameworkUtil;

import com.github.tomakehurst.wiremock.extension.requestfilter.AdminRequestFilter;
import com.github.tomakehurst.wiremock.extension.requestfilter.RequestFilterAction;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.google.common.collect.ImmutableMap;

public class DocRequestFilter extends AdminRequestFilter {

  private final Map<String, DocFile> mappings = ImmutableMap.<String, DocFile>builder()
    .put("__admin/docs", html("doc-index.html"))
    .put("__admin/docs/swagger", json("wiremock-admin-api.json"))
    .build();

  @Override
  public RequestFilterAction filter(Request request) {
    return mappings.entrySet()
      .stream()
      .filter(e -> request.getUrl().endsWith(e.getKey()))
      .findFirst()
      .map(Entry::getValue)
      .map(s -> RequestFilterAction.stopWith(response(s)))
      .orElseGet(() -> RequestFilterAction.continueWith(request));
  }

  private ResponseDefinition response(DocFile file) {
    try {
      byte[] content = readFile(file.getPath());
      return responseDefinition()
        .withStatus(200)
        .withBody(content)
        .withHeader(CONTENT_TYPE, file.getMime())
        .build();
    } catch (IOException e) {
      return responseDefinition().withStatus(500).build();
    }
  }

  private byte[] readFile(String path) throws IOException {
    return toByteArray(FrameworkUtil.getBundle(this.getClass()).getEntry(path).openStream());
  }


  @Override
  public String getName() {
    return "DOC_FILTER";
  }

  public static DocFile json(String path) {
    return new DocFile(path, "application/json");
  }

  public static DocFile html(String path) {
    return new DocFile(path, "text/html");
  }

  private static class DocFile {
    private final String path;
    private final String mime;

    private DocFile(String path, String mime) {
      this.path = path;
      this.mime = mime;
    }

    public String getPath() {
      return path;
    }

    public String getMime() {
      return mime;
    }
  }
}
