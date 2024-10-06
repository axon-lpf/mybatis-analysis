package com.axon.mybaits.scripting.node.handler;

import com.axon.mybatis.scripting.node.handler.BaseHandler;
import com.axon.mybatis.scripting.xmltags.MixedSqlNode;
import com.axon.mybatis.scripting.xmltags.SqlNode;
import com.axon.mybatis.scripting.xmltags.TrimSqlNode;
import com.axon.mybatis.scripting.xmltags.XMLScriptBuilder;
import com.axon.mybatis.session.Configuration;
import org.dom4j.Element;

import java.util.List;

public class TrimHandler extends BaseHandler {

    public TrimHandler(Configuration configuration, XMLScriptBuilder xmlScriptBuilder) {
        super(configuration, xmlScriptBuilder);
    }

    @Override
    public void handleNode(Element nodeToHandle, List<SqlNode> targetContents) {
        List<SqlNode> contents = xmlScriptBuilder.parseDynamicTags(nodeToHandle);
        MixedSqlNode mixedSqlNode = new MixedSqlNode(contents);
        String prefix = nodeToHandle.attributeValue("prefix");
        String prefixOverrides = nodeToHandle.attributeValue("prefixOverrides");
        String suffix = nodeToHandle.attributeValue("suffix");
        String suffixOverrides = nodeToHandle.attributeValue("suffixOverrides");
        TrimSqlNode trim = new TrimSqlNode(configuration, mixedSqlNode, prefix, prefixOverrides, suffix, suffixOverrides);
        targetContents.add(trim);
    }
}
