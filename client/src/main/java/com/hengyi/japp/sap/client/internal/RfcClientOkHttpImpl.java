package com.hengyi.japp.sap.client.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.ImmutableMap;
import com.hengyi.japp.sap.RfcExeCommand;
import com.hengyi.japp.sap.client.RfcClient;
import com.hengyi.japp.sap.grpc.server.ExeRfcReply;
import okhttp3.*;
import org.jzb.J;
import org.jzb.exception.JNonAuthenticationError;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

import static org.jzb.Constant.ErrorCode.NON_AUTHENTICATION;
import static org.jzb.Constant.ErrorCode.TOKEN_EXPIRED;
import static org.jzb.Constant.MAPPER;

public class RfcClientOkHttpImpl implements RfcClient {
    public static final MediaType OK_JSON = MediaType.parse("application/json; charset=utf-8");
    // private static final String baseApiUrl = "http://localhost:8080/sap/api/";
    private static final String baseApiUrl = "http://fp.hengyi.com:8888/sap/api/";
    private static final String urlTpl = baseApiUrl + "rfcs/${rfcName}";
    private static final OkHttpClient OK_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.HOURS)
            .writeTimeout(1, TimeUnit.HOURS)
            .readTimeout(1, TimeUnit.HOURS)
            .build();
    private static RfcClientOkHttpImpl INSTANCE;
    private final Token token;

    private RfcClientOkHttpImpl(Token token) {
        this.token = token;
    }

    public static RfcClientOkHttpImpl instance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        return create();
    }

    private synchronized static RfcClientOkHttpImpl create() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        INSTANCE = new RfcClientOkHttpImpl(new Token());
        return INSTANCE;
    }

    @Override
    public ExeRfcReply exeRfc(String rfcName, RfcExeCommand command) throws IOException {
        try {
            return _exeRfc(rfcName, command);
        } catch (JNonAuthenticationError e) {
            token.token = null;
            return _exeRfc(rfcName, command);
        }
    }

    private ExeRfcReply _exeRfc(String rfcName, RfcExeCommand command) throws IOException {
        final Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + token.get())
                .url(J.strTpl(urlTpl, ImmutableMap.of("rfcName", rfcName)))
                .post(RequestBody.create(OK_JSON, RfcClient.jsonString(command)))
                .build();
        try (Response response = OK_CLIENT.newCall(request).execute(); ResponseBody body = response.body(); InputStream is = body.byteStream()) {
            final JsonNode node = MAPPER.readTree(is);
            if (response.isSuccessful()) {
                final ExeRfcReply.Builder builder = ExeRfcReply.newBuilder();
                Optional.ofNullable(node.get("exports"))
                        .map(RfcClient::jsonString)
                        .ifPresent(builder::setExports);
                Optional.ofNullable(node.get("changings"))
                        .map(RfcClient::jsonString)
                        .ifPresent(builder::setChangings);
                Optional.ofNullable(node.get("tables"))
                        .map(RfcClient::jsonString)
                        .ifPresent(builder::setTables);
                return builder.build();
            }

            final Boolean nonAuth = Optional.ofNullable(node)
                    .filter(JsonNode::isArray)
                    .map(it -> (ArrayNode) node)
                    .map(errors -> StreamSupport.stream(errors.spliterator(), true)
                            .map(error -> error.get("errorCode"))
                            .filter(Objects::nonNull)
                            .map(JsonNode::asText)
                            .filter(it -> Objects.equals(it, NON_AUTHENTICATION) || Objects.equals(it, TOKEN_EXPIRED))
                            .findFirst()
                    )
                    .map(it -> true)
                    .orElse(false);
            if (nonAuth) {
                throw new JNonAuthenticationError();
            }
            throw new RuntimeException();
        }
    }

    @Override
    public void shutdown() {
    }

    static class Token {
        private static final String urlTpl = baseApiUrl + "auth?id=${id}&password=${password}";
        private String token;

        private String get() throws IOException {
            if (token != null) {
                return token;
            }
            return fetch();
        }

        private synchronized String fetch() throws IOException {
            if (token != null) {
                return token;
            }
            final Map<String, String> paramMap = ImmutableMap.of("id", "LBMhKp5ML4qPVWZJJjGVKK", "password", "japp-report");
            final Request request = new Request.Builder()
                    .url(J.strTpl(urlTpl, paramMap))
                    .build();
            try (Response response = OK_CLIENT.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException();
                }
                try (ResponseBody body = response.body();
                     InputStream is = body.byteStream()) {
                    final JsonNode node = MAPPER.readTree(is);
                    token = node.get("token").asText();
                    return token;
                }
            }
        }
    }
}