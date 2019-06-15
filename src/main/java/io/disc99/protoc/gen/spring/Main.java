package io.disc99.protoc.gen.spring;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.google.api.AnnotationsProto;
import com.google.api.HttpRule;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.compiler.PluginProtos;
import io.disc99.protoc.gen.spring.generator.ProtocPluginCodeGenerator;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {
        new Generator().generate();
//        gen(args);
        return;
    }
//
//    static byte[] convertFile(File file) throws IOException {
//        return Files.readAllBytes(file.toPath());
//    }
//
//    @SneakyThrows
//    String generateCode() {
//        TemplateLoader loader = new ClassPathTemplateLoader();
//        Handlebars handlebars = new Handlebars(loader).prettyPrint(true);
//
//        Template template = handlebars.compile("controller");
//
//        Dto dto = Dto.builder()
//                .packageName("echo.v1")
//                .serviceName("EchoService")
//                .stubClass("EchoServiceGrpc.EchoServiceBlockingStub")
//                .rpcList(Arrays.asList(
//                        Dto.Rpc.builder()
//                                .in("EchoRequest")
//                                .out("EchoResponse")
//                                .methodName("getEcho")
//                                .httpMethod("PostMapping")
//                                .httpPath("/echo.v1.EchoService.EchoRequest")
//                                .build())
//                )
//                .build();
//
//        return template.apply(dto);
//    }
//
//    private static void gen(String[] args) throws IOException {
////                System.out.println("START!!!!!!");
//
//        InputStream is = System.in;
//
//        if (args.length > 0) {
//            File replayFile = new File(args[0]);
//            FileOutputStream fos = new FileOutputStream(replayFile);
//
//            ByteStreams.copy(System.in, fos);
//            fos.close();
//
//            is = new FileInputStream(replayFile);
//        }
//
//        ExtensionRegistryLite registryLite = ExtensionRegistryLite.newInstance();
//        AnnotationsProto.registerAllExtensions(registryLite);
//
////        CodeGenerator codeGenerator = new CodeGenerator();
//        PluginProtos.CodeGeneratorRequest request = PluginProtos.CodeGeneratorRequest.parseFrom(is, registryLite);
////        PluginProtos.CodeGeneratorResponse response = generate(request);
////
////        File file = new File("/Users/ar-mac-005/src/github.com/disc99/protoc-gen-spring/raw.data");
////        try {
////            Files.copy(is, file.toPath());
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
//        PluginProtos.CodeGeneratorResponse response = PluginProtos.CodeGeneratorResponse.newBuilder()
//                .addFile(PluginProtos.CodeGeneratorResponse.File.newBuilder()
//                        .setContent(request.getProtoFileCount() + "")
//                        .setName("./PtoroTest.java")
//                        .build())
//                .build();
//
//        response.writeTo(System.out);
//
////        System.out.println("END!!!!!");
//        System.exit(0);
//    }
//
//    private static PluginProtos.CodeGeneratorResponse generate(PluginProtos.CodeGeneratorRequest request) {
//        Set<String> options = Sets.newHashSet(Splitter.on(',').split(request.getParameter()));
//
//        boolean isProxy = !options.contains("direct");
//
//        Map<String, Descriptors.Descriptor> lookup = new HashMap<>();
//        PluginProtos.CodeGeneratorResponse.Builder response = PluginProtos.CodeGeneratorResponse.newBuilder();
//
//        List<Descriptors.FileDescriptor> fileDescriptors = Lists.newArrayList(
//                DescriptorProtos.MethodOptions.getDescriptor().getFile(),
//                AnnotationsProto.getDescriptor(),
//                HttpRule.getDescriptor().getFile()
//        );
//
//
////        Optional<YamlHttpConfig> yamlConfig = YamlHttpConfig.getFromOptions(options);
////
////        for (DescriptorProtos.FileDescriptorProto fdProto : request.getProtoFileList()) {
////            // Descriptors are provided in dependency-topological order
////            // each time we collect a new FileDescriptor, we add it to a
////            // mutable list of descriptors and append the entire dependency
////            // chain to each new FileDescriptor to allow crossLink() to function.
////            // TODO(xorlev): might have to be more selective about deps in future
////            Descriptors.FileDescriptor fd = Descriptors.FileDescriptor.buildFrom(
////                    fdProto, fileDescriptors.toArray(new Descriptors.FileDescriptor[]{})
////            );
////            fileDescriptors.add(fd);
////
////            // if type starts with a ".", it's in this package
////            // otherwise it's fully qualified
////            String protoPackage = fdProto.getPackage();
////            for (DescriptorProtos.DescriptorProto d : fdProto.getMessageTypeList()) {
////                String prefix = ".";
////
////                if (!Strings.isNullOrEmpty(protoPackage)) {
////                    prefix += protoPackage + ".";
////                }
////
////                lookup.put(prefix + d.getName(), fd.findMessageTypeByName(d.getName()));
////            }
////
////            // Find RPC methods with HTTP extensions
////            List<ServiceAndMethod> methodsToGenerate = new ArrayList<>();
////            for (Descriptors.ServiceDescriptor serviceDescriptor : fd.getServices()) {
////                DescriptorProtos.ServiceDescriptorProto serviceDescriptorProto = serviceDescriptor.toProto();
////                for (DescriptorProtos.MethodDescriptorProto methodProto : serviceDescriptorProto.getMethodList()) {
////                    String fullMethodName = serviceDescriptor.getFullName() + "." + methodProto.getName();
////                    if (yamlConfig.isPresent()) {   //Check to see if the rules are defined in the YAML
////                        for (YamlHttpRule rule : yamlConfig.get().getRules()) {
////                            if (rule.getSelector().equals(fullMethodName) || rule.getSelector()
////                                    .equals("*")) { //TODO:  com.foo.*
////                                DescriptorProtos.MethodOptions yamlOptions = DescriptorProtos.MethodOptions.newBuilder()
////                                        .setExtension(AnnotationsProto.http, rule.buildHttpRule())
////                                        .build();
////                                methodProto = DescriptorProtos.MethodDescriptorProto.newBuilder()
////                                        .mergeFrom(methodProto)
////                                        .setOptions(yamlOptions)
////                                        .build();
////                            }
////                        }
////                    }
////                    if (methodProto.getOptions().hasExtension(AnnotationsProto.http)) {
////                        // TODO(xorlev): support server streaming
////                        if (methodProto.getClientStreaming()) {
////                            throw new IllegalArgumentException("grpc-jersey does not support client streaming");
////                        }
////
////                        methodsToGenerate.add(new ServiceAndMethod(serviceDescriptor, methodProto));
////                    }
////                }
////            }
////            if (!methodsToGenerate.isEmpty()) {
////                generateResource(response, lookup, fdProto, methodsToGenerate, isProxy);
////            }
////        }
//
//        response.addFile(PluginProtos.CodeGeneratorResponse.File.newBuilder()
//                .setContent("Test !!!!!")
//                .setName("./PtoroTest.java")
//                .build());
//
//        return response.build();
//    }
}
