package com.cognifide.aem.stubs.core.script;

import com.cognifide.aem.stubs.core.Stubs;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
  immediate = true,
  service = StubScriptExecutor.class
)
public class StubScriptExecutor {

  @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL)
  private volatile Stubs stubs;

  private static final Logger LOG = LoggerFactory.getLogger(StubScriptExecutor.class);

  public Object execute(ResourceResolver resolver, String path) {
    final StubScript script = new StubScript(resolver, path);
    LOG.info("Executing Stub Script '{}'", script.getPath());
    stubs.prepare(script);
    final Object result = script.run();
    LOG.info("Executed Stub Script '{}'", script.getPath());
    return result;
  }

}
