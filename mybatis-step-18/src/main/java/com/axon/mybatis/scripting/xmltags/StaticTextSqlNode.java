package com.axon.mybatis.scripting.xmltags;

import com.axon.mybatis.scripting.xmltags.DynamicContext;
import com.axon.mybatis.scripting.xmltags.SqlNode;

public class StaticTextSqlNode  implements SqlNode {

    private String text;

    public StaticTextSqlNode(String text) {
        this.text = text;
    }

    @Override
    public boolean apply(DynamicContext context) {
        //将文本加入context
        context.appendSql(text);
        return true;
    }
}
