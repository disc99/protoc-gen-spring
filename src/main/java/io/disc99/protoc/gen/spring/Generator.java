package io.disc99.protoc.gen.spring;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.github.jknack.handlebars.EscapingStrategy;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import io.disc99.protoc.gen.spring.generator.*;
import io.disc99.protoc.gen.spring.method.MethodGenerator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label;

/**
 * An implementation of {@link ProtocPluginCodeGenerator} that generates Spring Framework-compatible
 * REST controllers and swagger-annotated POJOs for gRPC services.
 */
@Slf4j
class Generator extends ProtocPluginCodeGenerator {

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected String getPluginName() {
        return "protoc-gen-spring";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected String generatePluginJavaClass(@Nonnull final String protoJavaClass) {
        return protoJavaClass + "REST";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected String generateImports() {
//        return SpringRestTemplates.imports();
        return template("imports").text();
    }

    @SneakyThrows // TODO
    Template template(String file) {
        TemplateLoader loader = new ClassPathTemplateLoader();
        Handlebars handlebars = new Handlebars(loader).prettyPrint(true).with(EscapingStrategy.NOOP);
        return handlebars.compile(file);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    @SneakyThrows
    protected Optional<String> generateEnumCode(@Nonnull final EnumDescriptor enumDescriptor) {
        HashMap<String, Object> context = new HashMap<>();
        context.put("enumName", enumDescriptor.getName());
        context.put("comment", enumDescriptor.getComment());
        context.put("originalProtoType", enumDescriptor.getQualifiedOriginalName());
        context.put("values", enumDescriptor.getValues().entrySet().stream()
                .map(valueEntry -> {
                    HashMap<String, Object> value = new HashMap<>();
                    value.put("comment", enumDescriptor.getValueComment(valueEntry.getKey()));
                    value.put("name", valueEntry.getKey());
                    value.put("value", valueEntry.getValue());
                    return value;
//                    String valueTemplate =
//                            "@ApiModelProperty(value=<comment>)" +
//                                    "<name>(<value>)";
//                    ST valueMsg = new ST(valueTemplate);
//                    valueMsg.add("comment", enumDescriptor.getValueComment(valueEntry.getKey()));
//                    valueMsg.add("name", valueEntry.getKey());
//                    valueMsg.add("value", valueEntry.getValue());
//                    return valueMsg.render();
                })
                .collect(Collectors.toList()));

        return Optional.of(template("enumerator").apply(context));
//        return Optional.of(SpringRestTemplates.enumerator()
//                .add("enumName", enumDescriptor.getName())
//                .add("comment", enumDescriptor.getComment())
//                .add("originalProtoType", enumDescriptor.getQualifiedOriginalName())
//                .add("values", enumDescriptor.getValues().entrySet().stream()
//                        .map(valueEntry -> {
//                            String valueTemplate =
//                                    "@ApiModelProperty(value=<comment>)" +
//                                            "<name>(<value>)";
//                            ST valueMsg = new ST(valueTemplate);
//                            valueMsg.add("comment", enumDescriptor.getValueComment(valueEntry.getKey()));
//                            valueMsg.add("name", valueEntry.getKey());
//                            valueMsg.add("value", valueEntry.getValue());
//                            return valueMsg.render();
//                        })
//                        .collect(Collectors.toList()))
//                .render());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    @SneakyThrows // TODO
    protected Optional<String> generateServiceCode(@Nonnull final ServiceDescriptor serviceDescriptor) {
        final String responseWrapper = serviceDescriptor.getName() + "Response";

        HashMap<String, Object> context = new HashMap<>();
        context.put("serviceName", serviceDescriptor.getName());
        context.put("responseWrapper", responseWrapper);
        context.put("package", serviceDescriptor.getJavaPkgName());
        context.put("methodDefinitions", serviceDescriptor.getMethodDescriptors().stream()
                        .map(serviceMethodDescriptor -> new MethodGenerator(serviceDescriptor,
                                serviceMethodDescriptor, responseWrapper).generateCode())
                        .collect(Collectors.toList()));
        return Optional.of(template("service").apply(context));

//        return Optional.of(SpringRestTemplates.service()
//                .add("serviceName", serviceDescriptor.getName())
//                .add("responseWrapper", responseWrapper)
//                .add("package", serviceDescriptor.getJavaPkgName())
//                .add("methodDefinitions", serviceDescriptor.getMethodDescriptors().stream()
//                        .map(serviceMethodDescriptor -> new MethodGenerator(serviceDescriptor,
//                                serviceMethodDescriptor, responseWrapper).generateCode())
//                        .collect(Collectors.toList()))
//                .render());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    @SneakyThrows // TODO
    protected Optional<String> generateMessageCode(@Nonnull final MessageDescriptor messageDescriptor) {
        final Map<Integer, String> oneofNameMap = new HashMap<>();
        final DescriptorProto descriptorProto = messageDescriptor.getDescriptorProto();
        for (int i = 0; i < descriptorProto.getOneofDeclCount(); ++i) {
            oneofNameMap.put(i, descriptorProto.getOneofDecl(i).getName());
        }

        HashMap<String, Object> context = new HashMap<>();
        context.put("comment", messageDescriptor.getComment());
        context.put("className", messageDescriptor.getName());
        context.put("originalProtoType", messageDescriptor.getQualifiedOriginalName());
        context.put("nestedDefinitions", messageDescriptor.getNestedMessages().stream()
                .filter(nestedDescriptor -> !(nestedDescriptor instanceof MessageDescriptor &&
                        ((MessageDescriptor) nestedDescriptor).isMapEntry()))
                .map(this::generateCode)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList()));
        context.put("fieldDeclarations", messageDescriptor.getFieldDescriptors().stream()
                .map(descriptor -> generateFieldDeclaration(descriptor, oneofNameMap))
                .collect(Collectors.toList()));
        context.put("setBuilderFields", messageDescriptor.getFieldDescriptors().stream()
                .map(this::addFieldToProtoBuilder)
                .collect(Collectors.toList()));
        context.put("setMsgFields", messageDescriptor.getFieldDescriptors().stream()
                .map(descriptor -> addFieldSetFromProto(descriptor, "newMsg"))
                .collect(Collectors.toList()));

        return Optional.of(template("message").apply(context));

//        return Optional.of(SpringRestTemplates.message()
//                .add("comment", messageDescriptor.getComment())
//                .add("className", messageDescriptor.getName())
//                .add("originalProtoType", messageDescriptor.getQualifiedOriginalName())
//                .add("nestedDefinitions", messageDescriptor.getNestedMessages().stream()
//                        .filter(nestedDescriptor -> !(nestedDescriptor instanceof MessageDescriptor &&
//                                ((MessageDescriptor) nestedDescriptor).isMapEntry()))
//                        .map(this::generateCode)
//                        .filter(Optional::isPresent)
//                        .map(Optional::get)
//                        .collect(Collectors.toList()))
//                .add("fieldDeclarations", messageDescriptor.getFieldDescriptors().stream()
//                        .map(descriptor -> generateFieldDeclaration(descriptor, oneofNameMap))
//                        .collect(Collectors.toList()))
//                .add("setBuilderFields", messageDescriptor.getFieldDescriptors().stream()
//                        .map(this::addFieldToProtoBuilder)
//                        .collect(Collectors.toList()))
//                .add("setMsgFields", messageDescriptor.getFieldDescriptors().stream()
//                        .map(descriptor -> addFieldSetFromProto(descriptor, "newMsg"))
//                        .collect(Collectors.toList()))
//                .render());
    }

    @Nonnull
    @SneakyThrows
    private String generateFieldDeclaration(@Nonnull final FieldDescriptor fieldDescriptor,
                                            @Nonnull final Map<Integer, String> oneofNameMap) {
        HashMap<String, Object> context = new HashMap<>();


        context.put("type", fieldDescriptor.getType());
        context.put("displayName", fieldDescriptor.getName());
        context.put("name", fieldDescriptor.getSuffixedName());
        context.put("isRequired", fieldDescriptor.getProto().getLabel() == Label.LABEL_REQUIRED);
        context.put("comment", fieldDescriptor.getComment());

//        final ST template = SpringRestTemplates.fieldDeclaration()
//                .add("type", fieldDescriptor.getType())
//                .add("displayName", fieldDescriptor.getName())
//                .add("name", fieldDescriptor.getSuffixedName())
//                .add("isRequired", fieldDescriptor.getProto().getLabel() == Label.LABEL_REQUIRED)
//                .add("comment", fieldDescriptor.getComment());
        if (fieldDescriptor.getProto().hasOneofIndex()) {
            context.put("hasOneOf", true);
            context.put("oneof", FieldDescriptor.formatFieldName(
                    oneofNameMap.get(fieldDescriptor.getProto().getOneofIndex())));
//            template.add("hasOneOf", true);
//            template.add("oneof", FieldDescriptor.formatFieldName(
//                    oneofNameMap.get(fieldDescriptor.getProto().getOneofIndex())));
        } else {
            context.put("hasOneOf", false);
//            template.add("hasOneOf", false);
        }
        return template("field_decl_template").apply(context);
//        return template.render();
    }

    /**
     * Generate code to add this field to the builder that creates
     * a protobuf object from the generated Java object.
     *
     * @return The generated code string.
     */
    @Nonnull
    @SneakyThrows
    private String addFieldToProtoBuilder(@Nonnull final FieldDescriptor fieldDescriptor) {
        HashMap<String, Object> context = new HashMap<>();
        context.put("name", fieldDescriptor.getSuffixedName());
        context.put("capProtoName", StringUtils.capitalize(fieldDescriptor.getName()));
        context.put("isList", fieldDescriptor.isList());
        context.put("isMsg", fieldDescriptor.getContentMessage().isPresent());
        context.put("isMap", fieldDescriptor.isMapField());;


//        final ST template = SpringRestTemplates.addFieldToProtoBuilder()
//                .add("name", fieldDescriptor.getSuffixedName())
//                .add("capProtoName", StringUtils.capitalize(fieldDescriptor.getName()))
//                .add("isList", fieldDescriptor.isList())
//                .add("isMsg", fieldDescriptor.getContentMessage().isPresent())
//                .add("isMap", fieldDescriptor.isMapField());

        if (fieldDescriptor.isMapField()) {
            FieldDescriptor value = fieldDescriptor.getContentMessage()
                    .map(descriptor -> ((MessageDescriptor) descriptor).getMapValue())
                    .orElseThrow(() -> new IllegalStateException("Content message not present in map field."));

            context.put("isMapMsg", value.getContentMessage().isPresent());
//            template.add("isMapMsg", value.getContentMessage().isPresent());

        }
        return template("add_field_to_proto_builder").apply(context);
//        return template.render();
    }

    /**
     * Generate code to set this field in the generated Java
     * object from a protobuf object.
     *
     * @param msgName The variable name of the protobuf object.
     * @return The generated code string.
     */
    @Nonnull
    @SneakyThrows
    private String addFieldSetFromProto(@Nonnull final FieldDescriptor fieldDescriptor,
                                        @Nonnull final String msgName) {
        HashMap<String, Object> context = new HashMap<>();

        context.put("isMsg", fieldDescriptor.getContentMessage().isPresent());
        context.put("isProto3", fieldDescriptor.isProto3Syntax());
        context.put("msgName", msgName);
        context.put("fieldName", fieldDescriptor.getSuffixedName());
        context.put("fieldNumber", fieldDescriptor.getProto().getNumber());
        context.put("capProtoName", StringUtils.capitalize(fieldDescriptor.getName()));
        context.put("msgType", fieldDescriptor.getTypeName());
        context.put("isList", fieldDescriptor.isList());
        context.put("isMap", fieldDescriptor.isMapField());
        context.put("isOneOf", fieldDescriptor.getOneofName().isPresent());

//        final ST template = SpringRestTemplates.setFieldFromProto()
//                .add("isMsg", fieldDescriptor.getContentMessage().isPresent())
//                .add("isProto3", fieldDescriptor.isProto3Syntax())
//                .add("msgName", msgName)
//                .add("fieldName", fieldDescriptor.getSuffixedName())
//                .add("fieldNumber", fieldDescriptor.getProto().getNumber())
//                .add("capProtoName", StringUtils.capitalize(fieldDescriptor.getName()))
//                .add("msgType", fieldDescriptor.getTypeName())
//                .add("isList", fieldDescriptor.isList())
//                .add("isMap", fieldDescriptor.isMapField())
//                .add("isOneOf", fieldDescriptor.getOneofName().isPresent());

        fieldDescriptor.getOneofName().ifPresent(oneOfName ->
                context.put("oneOfName", StringUtils.capitalize(oneOfName)));
//        fieldDescriptor.getOneofName().ifPresent(oneOfName ->
//                template.add("oneOfName", StringUtils.capitalize(oneOfName)));

        if (fieldDescriptor.isMapField()) {
            FieldDescriptor value = fieldDescriptor.getContentMessage()
                    .map(descriptor -> ((MessageDescriptor) descriptor).getMapValue())
                    .orElseThrow(() -> new IllegalStateException("Content message not present in map field."));
            context.put("isMapMsg", value.getContentMessage().isPresent());
            context.put("mapValType", value.getTypeName());
//            template.add("isMapMsg", value.getContentMessage().isPresent());
//            template.add("mapValType", value.getTypeName());
        }

        return template("set_field_from_proto_template").apply(context);
//        return template.render();
    }
}
