package io.disc99.protoc.gen.spring;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class Dto {
    String packageName;
    String serviceName;
    String stubClass;
    List<Rpc> rpcList;

    @Builder
    @Getter
    static class Rpc {
        String name;
        String in;
        String out;
        String methodName;
        String httpPath;
        String httpMethod;
    }
}
