/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hengyi.japp.sap.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.google.common.collect.Lists;
import com.hengyi.japp.sap.server.domain.Operator;
import com.sap.conn.jco.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author jzb
 */
public class Util {
    public static String jwtToken(Operator operator) {
        return Jwts.builder()
                .setSubject(operator.getId())
                .signWith(SignatureAlgorithm.HS512, Constant.JWT_KEY)
                .compressWith(CompressionCodecs.DEFLATE)
                .compact();
    }

    public static Claims claims(String token) {
        return Jwts.parser()
                .setSigningKey(Constant.JWT_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public static final void setParam(final JCoRecord record, final JsonNode node) {
        if (record == null || node == null) {
            return;
        }
        StreamSupport.stream(record.spliterator(), true).forEach(field -> {
            final String fieldName = field.getName();
            final JsonNode fieldNode = node.get(fieldName);
            Optional.ofNullable(getValue(fieldNode, field))
                    .ifPresent(field::setValue);
        });
    }

    private static Object getValue(final JsonNode fieldNode, final JCoField field) {
        if (fieldNode == null || fieldNode.isNull()) {
            return null;
        }
        switch (field.getType()) {
            case JCoMetaData.TYPE_STRUCTURE: {
                JCoStructure structure = field.getStructure();
                setParam(structure, fieldNode);
                return structure;
            }
            case JCoMetaData.TYPE_TABLE: {
                JCoTable table = field.getTable();
                if (!fieldNode.isArray()) {
                    throw new RuntimeException("JcoField[" + field + "]，为 JCoTable，但 fieldNode 不是 ArrayNode");
                }
                ArrayNode arrayNode = (ArrayNode) fieldNode;
                arrayNode.forEach(rowNode -> {
                    table.appendRow();
                    JCoRecord record = table;
                    setParam(record, rowNode);
                });
                return table;
            }
        }
        return fieldNode.asText();
    }

    public static final Map<String, Object> toMap(final JCoRecord record) {
        if (record == null) {
            return Collections.EMPTY_MAP;
        }
        return StreamSupport.stream(record.spliterator(), true)
                .collect(Collectors.toMap(JCoField::getName, Util::getValue));
    }

    private static final Object getValue(final JCoField field) {
        switch (field.getType()) {
            case JCoMetaData.TYPE_STRUCTURE: {
                JCoStructure structure = field.getStructure();
                return toMap(structure);
            }
            case JCoMetaData.TYPE_TABLE: {
                JCoTable table = field.getTable();
                int numRows = table.getNumRows();
                if (numRows < 1) {
                    return Collections.EMPTY_LIST;
                }
                Collection collection = Lists.newArrayList();
                do {
                    JCoRecord record = table;
                    Map<String, Object> map = toMap(record);
                    collection.add(map);
                } while (table.nextRow());
                return collection;
            }
        }
        return Optional.ofNullable(field.getValue()).orElse(NullNode.instance);
    }

}
