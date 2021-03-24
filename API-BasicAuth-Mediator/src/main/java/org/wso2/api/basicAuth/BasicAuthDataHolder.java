package org.wso2.api.basicAuth;

import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.user.core.service.RealmService;

public class BasicAuthDataHolder {

    private static BasicAuthDataHolder dataHolder = new BasicAuthDataHolder();
    private RealmService realmService;
    private RegistryService registryService;

    public static BasicAuthDataHolder getInstance() {

        return dataHolder;
    }

    public RealmService getRealmService() {

        return realmService;
    }

    public void setRealmService(RealmService realmService) {

        this.realmService = realmService;
    }

    public RegistryService getRegistryService() {

        return registryService;
    }

    public void setRegistryService(RegistryService registryService) {

        this.registryService = registryService;
    }
}
