package com.cognifide.aem.stubs.core.script;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.NumberFormat;
import java.util.Locale;

public class StubScriptRun {

    private static final NumberFormat PERCENT_FORMAT = NumberFormat.getPercentInstance(Locale.US);

    private final long startedAt = System.currentTimeMillis();

    protected int total = 0;

    protected int failed = 0;

    public int succeed() {
      return total - failed;
    }

    public String succeedPercent() {
      return PERCENT_FORMAT.format((double) (total - failed) / ((double) total));
    }

    public String duration() {
      return DurationFormatUtils.formatDurationHMS(System.currentTimeMillis() - startedAt);
    }

    @Override
    public String toString() {
      return String.format("Success ratio: %s/%s=%s | Duration: %s", succeed(), total, succeedPercent(), duration());
    }
  }
