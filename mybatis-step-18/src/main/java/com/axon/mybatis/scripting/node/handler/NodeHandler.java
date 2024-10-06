package com.axon.mybatis.scripting.node.handler;

import com.axon.mybatis.scripting.xmltags.SqlNode;
import org.dom4j.Element;

import java.util.List;

/**
 *  动态Node解析handle
 */
public interface NodeHandler {

    void handleNode(Element nodeToHandle, List<SqlNode> targetContents);

}
