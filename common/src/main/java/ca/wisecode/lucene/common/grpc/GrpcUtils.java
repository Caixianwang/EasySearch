package ca.wisecode.lucene.common.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 9/5/2024 10:26 AM
 * @Version: 1.0
 * @description:
 */
@Slf4j
public class GrpcUtils {

    public static ManagedChannel getManagedChannel(String host, int port) {
        return ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .idleTimeout(10, TimeUnit.MINUTES)
                .build();
    }

    public static String getLocalHost() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

}
