package com.axon.mybaits.scripting.node.handler;

import com.axon.mybatis.scripting.xmltags.XMLScriptBuilder;
import com.axon.mybatis.session.Configuration;

public abstract class BaseHandler implements NodeHandler {

    protected XMLScriptBuilder xmlScriptBuilder;

    protected Configuration configuration;

    public BaseHandler(Configuration configuration, XMLScriptBuilder xmlScriptBuilder) {

        this.xmlScriptBuilder = xmlScriptBuilder;
        this.configuration = configuration;
    }
}
