package org.wso2.api.basicAuth;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.user.core.service.RealmService;

@Component(
        name = "api.basicAuth",
        immediate = true
)
public class BasicAuthServiceComponent {
    private static final Log log = LogFactory.getLog(BasicAuthServiceComponent.class);

    @Activate
    protected void activate(ComponentContext context) {
        BundleContext bundleContext = context.getBundleContext();
        bundleContext.registerService(AuthenticateRequest.class,
                AuthenticateRequestImpl.getInstance(), null);
        log.debug("TestBasicAuthMediator bundle is activated");
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {
        log.debug("TestBasicAuthMediator bundle is deactivated");
    }

    @Reference(name = "user.realmservice.default",
            service = org.wso2.carbon.user.core.service.RealmService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetRealmService")
    protected void setRealmService(RealmService realmService) {
        BasicAuthDataHolder.getInstance().setRealmService(realmService);
    }

    protected void unsetRealmService(RealmService realmService) {

        BasicAuthDataHolder.getInstance().setRealmService(null);
    }

    @Reference(
            name = "registry.service",
            service = RegistryService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetRegistryService"
    )
    protected void setRegistryService(RegistryService registryService) {
        BasicAuthDataHolder.getInstance().setRegistryService(registryService);
    }

    protected void unsetRegistryService(RegistryService registryService) {
        BasicAuthDataHolder.getInstance().setRegistryService(null);
    }
}
