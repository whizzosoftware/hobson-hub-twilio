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

import com.whizzosoftware.hobson.api.action.ActionExecutionContext;
import com.whizzosoftware.hobson.api.plugin.http.HttpRequest;

import java.net.URI;
import java.util.Map;

public interface TwilioExecutionContext extends ActionExecutionContext {
    String getAccountSid();
    String getAuthToken();
    String getFromPhoneNumber();
    void sendHttpRequest(URI uri, HttpRequest.Method method, Map<String, String> headers, byte[] body);
}
