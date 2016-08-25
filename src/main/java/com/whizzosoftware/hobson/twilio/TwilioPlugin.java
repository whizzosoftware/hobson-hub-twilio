/*******************************************************************************
 * Copyright (c) 2015 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.twilio;

import com.whizzosoftware.hobson.api.plugin.PluginStatus;
import com.whizzosoftware.hobson.api.plugin.http.AbstractHttpClientPlugin;
import com.whizzosoftware.hobson.api.plugin.http.HttpResponse;
import com.whizzosoftware.hobson.api.property.PropertyConstraintType;
import com.whizzosoftware.hobson.api.property.PropertyContainer;
import com.whizzosoftware.hobson.api.property.TypedProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * A plugin that publishes a task action for sending SMSs via Twilio.
 *
 * @author Dan Noguerol
 */
public class TwilioPlugin extends AbstractHttpClientPlugin {
    private static final Logger logger = LoggerFactory.getLogger(TwilioPlugin.class);

    private String accountSid;
    private String authToken;
    private String phoneNumber;
    private boolean actionClassPublished = false;

    public TwilioPlugin(String pluginId) {
        super(pluginId);
    }

    @Override
    protected TypedProperty[] createSupportedProperties() {
        return new TypedProperty[] {
            new TypedProperty.Builder("accountSid", "Account SID", "Your Twilio account SID", TypedProperty.Type.STRING).
                    constraint(PropertyConstraintType.required, true).
                    build(),
            new TypedProperty.Builder("authToken", "Auth Token", "Your Twilio account auth token", TypedProperty.Type.STRING).
                    constraint(PropertyConstraintType.required, true).
                    build(),
            new TypedProperty.Builder("phoneNumber", "Phone Number", "Your Twilio account phone number", TypedProperty.Type.STRING).
                    constraint(PropertyConstraintType.required, true).
                    build()
        };
    }

    @Override
    public String getName() {
        return "Twilio Plugin";
    }

    @Override
    public void onStartup(PropertyContainer config) {
        processConfig(config);
    }

    @Override
    public void onShutdown() {

    }

    @Override
    public void onPluginConfigurationUpdate(PropertyContainer config) {
        processConfig(config);
    }

    @Override
    public void onHttpResponse(HttpResponse response, Object context) {
        try {
            logger.debug("Twilio message result ({}): {}", response.getStatusCode(), response.getBody());
        } catch (IOException e) {
            logger.error("Error processing HTTP response", e);
        }
    }

    @Override
    public void onHttpRequestFailure(Throwable cause, Object context) {
        logger.error("Failed to send Twilio message", cause);
    }

    protected void processConfig(PropertyContainer config) {
        accountSid = config.getStringPropertyValue("accountSid");
        authToken = config.getStringPropertyValue("authToken");
        phoneNumber = config.getStringPropertyValue("phoneNumber");

        if (accountSid != null && authToken != null && phoneNumber != null) {
            setStatus(PluginStatus.running());
            if (!actionClassPublished) {
                publishActionClass(new TwilioSMSAction(this, accountSid, authToken, phoneNumber));
                actionClassPublished = true;
            }
        } else {
            setStatus(PluginStatus.notConfigured(""));
        }
    }
}
