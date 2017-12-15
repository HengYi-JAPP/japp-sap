package com.hengyi.japp.sap.client;

import com.hengyi.japp.sap.client.internal.RfcClientGrpcImpl;
import com.hengyi.japp.sap.client.internal.RfcClientOkHttpImpl;

public class JSap {
    public static RfcClient grpcClient() {
        return RfcClientGrpcImpl.instance();
    }

    public static RfcClient okhttpClient() {
        return RfcClientOkHttpImpl.instance();
    }
}
