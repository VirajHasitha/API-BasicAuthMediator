# API-BasicAuthMediator

This API-BasicAuthMediator can be used to authenticate users extracted from the Basic Authorization header with the User Store. If the username is testuser and the password is testuser, the Authorization header should be as follows.

```
-H 'Authorization: Basic dGVzdHVzZXI6dGVzdHVzZXI='

```

Upon validation the rest of the mediation flow will continue or discard.

After building the mediator project with maven, **org.wso2.api.basicAuth-1.0.0.jar** can add to the <EI_HOME>/dropins directory.

### The following is a sample API which uses the custom mediator.

```
<?xml version="1.0" encoding="UTF-8"?>
<api xmlns="http://ws.apache.org/ns/synapse" name="TestAPI" context="/context">
    <resource methods="GET">
        <inSequence>
            <class name="org.wso2.api.basicAuth.BasicAuthMediator"/>
            <call>
                <endpoint>
                    <address uri="http://www.mocky.io/v2/5c19e3293200002c0064adb2"/>
                </endpoint>
            </call>
            <respond/>
        </inSequence>
    </resource>
</api>

```

