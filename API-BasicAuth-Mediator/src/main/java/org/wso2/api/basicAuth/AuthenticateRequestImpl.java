package org.wso2.api.basicAuth;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.core.axis2.Axis2Sender;
import org.wso2.carbon.core.services.authentication.AuthenticationFailureException;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;
import java.util.Map;

public class AuthenticateRequestImpl implements AuthenticateRequest {

    private static final Log log = LogFactory.getLog(AuthenticateRequestImpl.class);

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    private String user;
    private String password;

    private static volatile AuthenticateRequestImpl authRequestImpl;

    public static AuthenticateRequestImpl getInstance() {

        if (authRequestImpl == null) {
            synchronized (AuthenticateRequestImpl.class) {
                if (authRequestImpl == null) {
                    authRequestImpl = new AuthenticateRequestImpl();
                }
            }
        }
        return authRequestImpl;
    }

    public boolean handleAuthentication(MessageContext messageContext) {

        org.apache.axis2.context.MessageContext axis2MessageContext
                = ((Axis2MessageContext) messageContext).getAxis2MessageContext();
        Object headers = axis2MessageContext.getProperty(
                org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);

        ConfigurationContext axis2ConfigurationContext = axis2MessageContext.getConfigurationContext();

        int tenantId = MultitenantUtils.getTenantId(axis2ConfigurationContext);
        log.debug("AuthenticateRequestImpl tenant: " + tenantId);

        try {
            if (headers != null && headers instanceof Map) {
                Map headersMap = (Map) headers;
                if (headersMap.get("Authorization") == null) {
                    headersMap.clear();
                    axis2MessageContext.setProperty("HTTP_SC", "401");
                    headersMap.put("WWW-Authenticate", "Basic realm=\"WSO2 ESB\"");
                    axis2MessageContext.setProperty("NO_ENTITY_BODY", new Boolean("true"));
                    messageContext.setProperty("RESPONSE", "true");
                    messageContext.setTo(null);
                    Axis2Sender.sendBack(messageContext);
                    return false;

                } else {
                    String authHeader = (String) headersMap.get("Authorization");
                    String credentials = authHeader.substring(6).trim();
                    if (processSecurity(credentials,tenantId)) {
                        messageContext.setProperty("UID", this.user);
                        log.info("User with username : " + this.user + " authenticated successfully");
                        return true;
                    } else {
                        headersMap.clear();
                        axis2MessageContext.setProperty("HTTP_SC", "403");
                        axis2MessageContext.setProperty("NO_ENTITY_BODY", new Boolean("true"));
                        messageContext.setProperty("RESPONSE", "true");
                        messageContext.setTo(null);
                        Axis2Sender.sendBack(messageContext);
                        return false;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            log.error("Unable to execute the authentication process : ", e);
            return false;
        }
    }

    public boolean processSecurity(String credentials, int tenantId) throws AuthenticationFailureException {

        boolean isAuthenticated = false;

        String decodedCredentials = new String(new Base64().decode(credentials.getBytes()));
        this.user = decodedCredentials.split(":")[0];
        password = decodedCredentials.split(":")[1];

        log.trace("processSecurity Username : " + this.user);
        log.trace("processSecurity Password : " + password);

        try {
            isAuthenticated = BasicAuthDataHolder.getInstance().getRealmService().getTenantUserRealm(tenantId)
                    .getUserStoreManager().authenticate(this.user, password);

        } catch (UserStoreException e) {
            log.error("Unable to authenticate using the User Store Manager from User Realm " + e);
        }

        if(!isAuthenticated)
            log.debug("Unable to authenticate the user with the UserName : " + this.user);

        return isAuthenticated;
    }
}
