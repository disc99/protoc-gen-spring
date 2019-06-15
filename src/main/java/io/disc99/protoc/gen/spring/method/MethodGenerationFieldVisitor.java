/*
 *
 * Copyright (C) 2009 - 2018 Turbonomic, Inc.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package io.disc99.protoc.gen.spring.method;

import io.disc99.protoc.gen.spring.generator.EnumDescriptor;
import io.disc99.protoc.gen.spring.generator.FieldDescriptor;
import io.disc99.protoc.gen.spring.generator.MessageDescriptor;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

class MethodGenerationFieldVisitor implements MessageDescriptor.MessageFieldVisitor {
    private final Deque<String> path;
    private final Set<String> boundVariables;
    private final Map<String, FieldDescriptor> queryParamsWithType = new HashMap<>();
    private final Map<String, FieldDescriptor> boundVarsWithType = new HashMap<>();

    public MethodGenerationFieldVisitor(final Set<String> boundVariables) {
        this.boundVariables = boundVariables;
        path = new ArrayDeque<>();
    }

    @Nonnull
    public Map<String, FieldDescriptor> getQueryParamFields() {
        return queryParamsWithType;
    }

    @Nonnull
    public Map<String, FieldDescriptor> getPathFields() {
        return boundVarsWithType;
    }

    @Override
    public boolean startMessageField(@Nonnull final FieldDescriptor field, @Nonnull final MessageDescriptor messageType) {
        path.push(field.getProto().getName());
        return true;
    }

    @Override
    public void endMessageField(@Nonnull final FieldDescriptor field, @Nonnull final MessageDescriptor messageType) {
        path.pop();
    }

    @Override
    public void visitBaseField(@Nonnull final FieldDescriptor field) {
        final String fullPath = getFullPath(field);
        if (!boundVariables.contains(fullPath)) {
            queryParamsWithType.put(fullPath, field);
        } else {
            boundVarsWithType.put(fullPath, field);
        }
    }

    @Override
    public void visitEnumField(@Nonnull final FieldDescriptor field, @Nonnull final EnumDescriptor enumDescriptor) {
        final String fullPath = getFullPath(field);
        if (!boundVariables.contains(fullPath)) {
            queryParamsWithType.put(fullPath, field);
        } else {
            boundVarsWithType.put(fullPath, field);
        }
    }

    @Nonnull
    private String getFullPath(@Nonnull final FieldDescriptor field) {
        if (path.isEmpty()) {
            return field.getProto().getName();
        } else {
            return path.stream().collect(Collectors.joining(".")) +
                    "." + field.getProto().getName();
        }
    }
}
