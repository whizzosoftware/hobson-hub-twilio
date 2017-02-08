/*
 *******************************************************************************
 * Copyright (c) 2015 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************
*/
package com.whizzosoftware.hobson.twilio.action;

import com.whizzosoftware.hobson.api.action.ActionLifecycleContext;
import com.whizzosoftware.hobson.api.action.SingleAction;
import com.whizzosoftware.hobson.api.plugin.EventLoopExecutor;
import com.whizzosoftware.hobson.api.plugin.PluginContext;
import com.whizzosoftware.hobson.api.plugin.http.HttpRequest;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Collections;

/**
 * An action class that defines an action to send SMSs using Twilio.
 *
 * @author Dan Noguerol
 */
public class TwilioSMSAction extends SingleAction {
    private final static Logger logger = LoggerFactory.getLogger(TwilioSMSAction.class);

    private String accountSid;
    private String authToken;
    private String fromPhoneNumber;
    private String toPhoneNumber;
    private String message;

    TwilioSMSAction(PluginContext pctx, TwilioExecutionContext aectx, EventLoopExecutor executor) {
        super(pctx, aectx, executor);
        this.accountSid = aectx.getAccountSid();
        this.authToken = aectx.getAuthToken();
        this.fromPhoneNumber = aectx.getFromPhoneNumber();

        this.toPhoneNumber = (String)aectx.getProperties().get("phoneNumber");
        this.message = (String)aectx.getProperties().get("message");
    }

    @Override
    public void onStart(ActionLifecycleContext ctx) {
        logger.info("Sending to " + toPhoneNumber + ": " + message);

        try {
            String auth = accountSid + ":" + authToken;
            StringBuilder data = new StringBuilder("To=")
                    .append(URLEncoder.encode(toPhoneNumber, "UTF8"))
                    .append("&From=")
                    .append(URLEncoder.encode(fromPhoneNumber, "UTF8"))
                    .append("&Body=")
                    .append(URLEncoder.encode(message, "UTF8"));

            ((TwilioExecutionContext)getContext()).sendHttpRequest(
                new URI("https://api.twilio.com/2010-04-01/Accounts/" + accountSid + "/Messages.json"),
                HttpRequest.Method.POST,
                Collections.singletonMap("Authorization", "Basic " + Base64.encodeBase64String(auth.getBytes())),
                data.toString().getBytes()
            );

            ctx.complete();
        } catch (Exception e) {
            ctx.fail("Error sending SMS: " + e);
        }
    }

    @Override
    public void onMessage(ActionLifecycleContext ctx, String msgName, Object prop) {

    }

    @Override
    public void onStop(ActionLifecycleContext ctx) {

    }
}
