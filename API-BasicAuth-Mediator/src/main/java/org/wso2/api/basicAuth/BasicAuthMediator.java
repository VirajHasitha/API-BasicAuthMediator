package org.wso2.api.basicAuth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

public class BasicAuthMediator extends AbstractMediator {

    private static final Log log = LogFactory.getLog(BasicAuthMediator.class);

    public boolean mediate(MessageContext messageContext) {
        String userName = null;
        AuthenticateRequestImpl authImpl = new AuthenticateRequestImpl();

        if (authImpl.handleAuthentication(messageContext)) {
            userName = (String)messageContext.getProperty("UID");
            log.info("Authentication Successful : Basic Auth successful for user '" + userName + "'");
        } else {
            log.info("Authentication failed : Basic Auth failed for user '" + userName + "'");
            return false;
        }
        return true;
    }
}
