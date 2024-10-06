package com.axon.mybaits.scripting.node.handler;

import com.axon.mybatis.scripting.node.handler.BaseHandler;
import com.axon.mybatis.scripting.xmltags.IfSqlNode;
import com.axon.mybatis.scripting.xmltags.MixedSqlNode;
import com.axon.mybatis.scripting.xmltags.SqlNode;
import com.axon.mybatis.scripting.xmltags.XMLScriptBuilder;
import com.axon.mybatis.session.Configuration;
import org.dom4j.Element;

import java.util.List;

public class IfHandler extends BaseHandler {

    public IfHandler(Configuration configuration, XMLScriptBuilder xmlScriptBuilder) {
        super(configuration, xmlScriptBuilder);
    }

    @Override
    public void handleNode(Element nodeToHandle, List<SqlNode> targetContents) {
        List<SqlNode> contents = xmlScriptBuilder.parseDynamicTags(nodeToHandle);
        MixedSqlNode mixedSqlNode = new MixedSqlNode(contents);
        String test = nodeToHandle.attributeValue("test");
        IfSqlNode ifSqlNode = new IfSqlNode(mixedSqlNode, test);
        targetContents.add(ifSqlNode);
    }
}
