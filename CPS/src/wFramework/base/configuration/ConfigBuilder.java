package wFramework.base.configuration;

import wFramework.base.service.SystemService;

public interface ConfigBuilder {
	public void build(PartialConfig pconfig, SystemService cm);
}
