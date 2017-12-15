package test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hengyi.japp.sap.RfcExeCommand;
import com.hengyi.japp.sap.client.JSap;
import com.hengyi.japp.sap.client.RfcClient;
import org.junit.Test;

import java.util.stream.IntStream;

import static org.jzb.Constant.MAPPER;

public class ClientTest {
    private static int times = 3;

    @Test
    public void testGrpc() {
        RfcClient rfcClient = JSap.grpcClient();
        IntStream.range(0, times)
                .parallel()
                .forEach(it -> {
                    test(rfcClient);
                });
    }

    @Test
    public void testOkHttp() {
        final RfcClient rfcClient = JSap.okhttpClient();
        IntStream.range(0, times)
                .parallel()
                .forEach(it -> {
                    test(rfcClient);
                });
    }

    private void test(RfcClient rfcClient) {
        RfcExeCommand command = new RfcExeCommand();
        ObjectNode imports = MAPPER.createObjectNode().put("I_DATE", "20170706");
        command.setImports(imports);
        try {
            rfcClient.exeRfc("ZRFC_SD_SALES_PRD_INFO", command);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
