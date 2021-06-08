/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.net.websocket.client.listener;

import io.ballerina.runtime.api.Future;
import io.ballerina.runtime.api.values.BObject;
import org.ballerinalang.net.http.HttpUtil;
import org.ballerinalang.net.transport.contract.websocket.ClientHandshakeListener;
import org.ballerinalang.net.transport.contract.websocket.WebSocketConnection;
import org.ballerinalang.net.transport.message.HttpCarbonResponse;
import org.ballerinalang.net.websocket.WebSocketConstants;
import org.ballerinalang.net.websocket.WebSocketService;
import org.ballerinalang.net.websocket.WebSocketUtil;
import org.ballerinalang.net.websocket.observability.WebSocketObservabilityUtil;
import org.ballerinalang.net.websocket.server.WebSocketConnectionInfo;

/**
 * The `WebSocketHandshakeListener` implements the `{@link ClientHandshakeListener}` interface directly.
 *
 * @since 1.2.0
 */
public class WebSocketHandshakeListener implements ClientHandshakeListener {

    private final WebSocketService wsService;
    private final SyncClientConnectorListener connectorListener;
    private final BObject webSocketClient;
    private WebSocketConnectionInfo connectionInfo;
    private Future balFuture;

    public WebSocketHandshakeListener(BObject webSocketClient, WebSocketService wsService,
            SyncClientConnectorListener connectorListener, Future future) {
        this.webSocketClient = webSocketClient;
        this.wsService = wsService;
        this.connectorListener = connectorListener;
        this.balFuture = future;
    }

    @Override
    public void onSuccess(WebSocketConnection webSocketConnection, HttpCarbonResponse carbonResponse) {
        webSocketClient.addNativeData(WebSocketConstants.HTTP_RESPONSE, HttpUtil.createResponseStruct(carbonResponse));
        WebSocketUtil.populateClientWebSocketEndpoint(webSocketConnection, webSocketClient);
        setWebSocketOpenConnectionInfo(webSocketConnection, webSocketClient, wsService);
        connectorListener.setConnectionInfo(connectionInfo);
        balFuture.complete(null);
        WebSocketObservabilityUtil.observeConnection(connectionInfo);
    }

    @Override
    public void onError(Throwable t, HttpCarbonResponse response) {
        if (response != null) {
            webSocketClient.addNativeData(WebSocketConstants.HTTP_RESPONSE, HttpUtil.createResponseStruct(response));
        }
        setWebSocketOpenConnectionInfo(null, webSocketClient, wsService);
        balFuture.complete(WebSocketUtil.createErrorByType(t));
    }

    private void setWebSocketOpenConnectionInfo(WebSocketConnection webSocketConnection,
            BObject webSocketClient, WebSocketService wsService) {
        this.connectionInfo = new WebSocketConnectionInfo(wsService, webSocketConnection, webSocketClient);
        webSocketClient.addNativeData(WebSocketConstants.NATIVE_DATA_WEBSOCKET_CONNECTION_INFO, connectionInfo);
    }
}
