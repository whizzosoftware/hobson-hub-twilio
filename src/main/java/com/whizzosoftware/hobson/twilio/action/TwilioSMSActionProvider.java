/*
 *******************************************************************************
 * Copyright (c) 2016 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************
*/
package com.whizzosoftware.hobson.twilio.action;

import com.whizzosoftware.hobson.api.action.Action;
import com.whizzosoftware.hobson.api.action.ActionProvider;
import com.whizzosoftware.hobson.api.plugin.http.HttpRequest;
import com.whizzosoftware.hobson.api.property.PropertyConstraintType;
import com.whizzosoftware.hobson.api.property.PropertyContainerClassContext;
import com.whizzosoftware.hobson.api.property.TypedProperty;
import com.whizzosoftware.hobson.twilio.TwilioPlugin;

import java.net.URI;
import java.util.Map;

public class TwilioSMSActionProvider extends ActionProvider {
    private TwilioPlugin plugin;

    public TwilioSMSActionProvider(TwilioPlugin plugin) {
        super(PropertyContainerClassContext.create(plugin.getContext(), "sms"), "Send Twilio SMS", "Send an SMS to {phoneNumber}", true, 2000);

        this.plugin = plugin;

        addSupportedProperty(
            new TypedProperty.Builder("phoneNumber", "Phone Number", "The phone number to send the message to", TypedProperty.Type.STRING).
                    constraint(PropertyConstraintType.required, true).
                    build()
        );
        addSupportedProperty(
            new TypedProperty.Builder("message", "Message", "The message to send", TypedProperty.Type.STRING).
                constraint(PropertyConstraintType.required, true).
                build()
        );
    }

    @Override
    public Action createAction(final Map<String, Object> properties) {
        return new TwilioSMSAction(plugin.getContext(), new TwilioExecutionContext() {
            @Override
            public String getAccountSid() {
                return plugin.getAccountSid();
            }

            @Override
            public String getAuthToken() {
                return plugin.getAuthToken();
            }

            @Override
            public String getFromPhoneNumber() {
                return plugin.getPhoneNumber();
            }

            @Override
            public void sendHttpRequest(URI uri, HttpRequest.Method method, Map<String, String> headers, byte[] body) {
                plugin.sendHttpRequest(uri, method, headers, null, body, null);
            }

            @Override
            public Map<String, Object> getProperties() {
                return properties;
            }
        }, plugin.getEventLoopExecutor());
    }
}
