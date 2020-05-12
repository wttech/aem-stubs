package com.cognifide.aem.stubs.core;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.NumberFormat;
import java.util.Locale;

class StubReload {

    private final long startedAt = System.currentTimeMillis();

    protected int total;

    protected int failed;

    public int succeed() {
      return total - failed;
    }

    public String succeedPercent() {
      return NumberFormat.getPercentInstance(Locale.US).format((double) (total - failed) / ((double) total));
    }

    public String duration() {
      return DurationFormatUtils.formatDurationHMS(System.currentTimeMillis() - startedAt);
    }

    @Override
    public String toString() {
      return String.format("Success ratio: %s/%s=%s | Duration: %s", succeed(), total, succeedPercent(), duration());
    }
  }
