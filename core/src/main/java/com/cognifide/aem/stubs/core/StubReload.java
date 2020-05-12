package com.cognifide.aem.stubs.core;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.NumberFormat;
import java.util.Locale;

class StubReload {

  private final long startedAt = System.currentTimeMillis();

  protected int scriptsTotal;

  protected int scriptsFailed;

  protected int mappingsTotal;

  protected int mappingsFailed;

  public int scriptsSucceeded() {
    return scriptsTotal - scriptsFailed;
  }

  public int mappingsSucceeded() {
    return mappingsTotal - mappingsFailed;
  }

  public String scriptsPercent() {
    return formatPercent((double) (scriptsSucceeded()) / ((double) scriptsTotal));
  }

  public String mappingsPercent() {
    return formatPercent((double) (mappingsSucceeded()) / ((double) mappingsTotal));
  }

  private String formatPercent(double value) {
    return NumberFormat.getPercentInstance(Locale.US).format(value);
  }

  public String duration() {
    return DurationFormatUtils.formatDurationHMS(System.currentTimeMillis() - startedAt);
  }

  public String summary() {
    return String.format("Stubs reloaded: mappings %s/%s=%s, scripts %s/%s=%s | Duration: %s",
      mappingsSucceeded(), mappingsTotal, mappingsPercent(),
      scriptsSucceeded(), scriptsTotal, scriptsPercent(),
      duration()
    );
  }

  @Override
  public String toString() {
    return summary();
  }
}
