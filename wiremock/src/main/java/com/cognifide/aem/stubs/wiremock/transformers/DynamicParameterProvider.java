package com.cognifide.aem.stubs.wiremock.transformers;

import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonIgnore;

import groovy.lang.Closure;

public class DynamicParameterProvider {

  private final FunctionCall provider;

  public static DynamicParameterProvider dynamicParameter(Closure closure) {
    return new DynamicParameterProvider(new ClousureCall(closure));
  }

  public static DynamicParameterProvider dynamicParameter(Supplier supplier) {
    return new DynamicParameterProvider(new SupplierCall(supplier));
  }

  private DynamicParameterProvider(FunctionCall provider) {
    this.provider = provider;
  }

  public String getDynamicProvider() {
    return provider.toString();
  }

  @JsonIgnore
  public Object call() {
    return provider.call();
  }

  private interface FunctionCall {

    Object call();
  }

  private static class ClousureCall implements FunctionCall {

    private final Closure closure;

    private ClousureCall(Closure closure) {
      this.closure = closure;
    }

    @Override
    public Object call() {
      return closure.call();
    }

    @Override
    public String toString(){
      return Closure.class.getName();
    }
  }

  private static class SupplierCall implements FunctionCall {
    private final Supplier<?> supplier;

    private SupplierCall(Supplier<?> supplier) {
      this.supplier = supplier;
    }

    @Override
    public Object call() {
      return supplier.get();
    }

    @Override
    public String toString(){
      return Supplier.class.getName();
    }
  }
}
