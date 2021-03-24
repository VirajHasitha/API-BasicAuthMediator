package org.wso2.api.basicAuth;

import org.apache.synapse.MessageContext;
import org.wso2.carbon.core.services.authentication.AuthenticationFailureException;
import org.wso2.carbon.user.core.UserStoreException;

public interface AuthenticateRequest {

    public boolean handleAuthentication(MessageContext messageContext);

    public boolean processSecurity(String credentials, int tenantId) throws AuthenticationFailureException;
}
