/*******************************************************************************
 * Copyright (c) 2015 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.hobson.twilio;

import com.whizzosoftware.hobson.api.plugin.http.AbstractHttpClientPlugin;
import com.whizzosoftware.hobson.api.property.PropertyConstraintType;
import com.whizzosoftware.hobson.api.property.PropertyContainer;
import com.whizzosoftware.hobson.api.property.TypedProperty;
import com.whizzosoftware.hobson.api.task.action.TaskActionClass;
import com.whizzosoftware.hobson.api.task.action.TaskActionExecutor;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An action class that defines an action to send SMSs using Twilio.
 *
 * @author Dan Noguerol
 */
public class TwilioSMSAction extends TaskActionClass implements TaskActionExecutor {
    private final static Logger logger = LoggerFactory.getLogger(TwilioSMSAction.class);

    private AbstractHttpClientPlugin plugin;
    private String accountSid;
    private String authToken;
    private String phoneNumber;

    public TwilioSMSAction(AbstractHttpClientPlugin plugin, String accountSid, String authToken, String phoneNumber) {
        super(plugin.getContext(), "sms", "Send Twilio SMS", "Send an SMS to {phoneNumber}");

        this.plugin = plugin;
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public TaskActionExecutor getExecutor() {
        return this;
    }

    @Override
    public void executeAction(PropertyContainer pc) {
        logger.info("Sending to " + pc.getPropertyValue("phoneNumber") + ": " + pc.getPropertyValue("message"));

        try {
            String auth = accountSid + ":" + authToken;
            StringBuilder data = new StringBuilder("To=")
                .append(URLEncoder.encode(pc.getStringPropertyValue("phoneNumber"), "UTF8"))
                .append("&From=")
                .append(URLEncoder.encode(phoneNumber, "UTF8"))
                .append("&Body=")
                .append(URLEncoder.encode(pc.getStringPropertyValue("message"), "UTF8"));

            plugin.sendHttpPostRequest(
                new URI("https://api.twilio.com/2010-04-01/Accounts/" + accountSid + "/Messages.json"),
                Collections.singletonMap("Authorization", "Basic " + Base64.encodeBase64String(auth.getBytes())),
                data.toString().getBytes(),
                null
            );
        } catch (Exception e) {
            logger.error("Error sending SMS", e);
        }
    }

    @Override
    protected List<TypedProperty> createProperties() {
        List<TypedProperty> props = new ArrayList<>();
        props.add(
            new TypedProperty.Builder("phoneNumber", "Phone Number", "The phone number to send the message to", TypedProperty.Type.STRING).
                    constraint(PropertyConstraintType.required, true).
                    build()
        );
        props.add(
            new TypedProperty.Builder("message", "Message", "The message to send", TypedProperty.Type.STRING).
                    constraint(PropertyConstraintType.required, true).
                    build()
        );
        return props;
    }
}
