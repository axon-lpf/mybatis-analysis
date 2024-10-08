package com.axon.mybatis.mapping;

import java.util.Map;

/**
 * sql语句的解析类
 *
 * BoundSql 的主要作用和功能：
 *
 * 	1.	存储解析后的 SQL 语句：
 * 当 MyBatis 处理动态 SQL 时（例如带有 <if>, <choose>, <foreach> 等标签的动态 SQL 语句），它首先会将这些动态 SQL 模板解析成一个最终的可执行 SQL 语句。BoundSql 就是用来存储这个最终的 SQL 语句的。这个 SQL 已经不再包含动态部分，它是一个完整的、可以被数据库执行的 SQL 语句。
 * 	2.	存储 SQL 语句中的参数：
 * 除了存储最终的 SQL 语句，BoundSql 还负责存储 SQL 中所需要的参数。通常，在 SQL 语句中会有 ? 占位符（如 WHERE id = ?），BoundSql 保存了这些占位符对应的参数及其顺序，确保在 SQL 执行时可以将参数绑定到正确的位置。
 * 	3.	提供元数据信息：
 * BoundSql 还保存了一些与 SQL 相关的元数据信息，比如：
 * 	•	参数映射：参数的名称和位置，以及它们在 Java 对象中的对应字段。
 * 	•	附加参数（additionalParameters）：这些是 MyBatis 在处理某些特殊 SQL 语句时生成的附加参数，通常用于复杂的动态 SQL。
 * 	•	参数属性（parameterMappings）：参数映射信息，描述每个参数在 SQL 中的位置以及其在 Java 对象中的属性名。
 * 	4.	辅助执行 SQL 语句：
 * 在 MyBatis 的执行器（Executor）准备执行 SQL 语句时，它会从 BoundSql 中获取已经解析好的 SQL 语句和对应的参数列表，然后将这些参数传递给 JDBC 执行引擎。这一过程确保了 SQL 语句的动态部分被正确替换，并且参数能够准确地绑定到 SQL 语句中。
 *
 * BoundSql 的主要属性：
 *
 * 	•	sql：表示最终生成的 SQL 语句，已经解析完毕，带有 ? 占位符。
 * 	•	parameterMappings：参数映射的列表，描述了 SQL 中的每个 ? 对应的参数信息。
 * 	•	parameterObject：原始的参数对象，通常是用户传入的参数，用于从中提取具体的值。
 * 	•	additionalParameters：附加参数的 Map，通常用于存储动态生成的额外参数。
 * 	•	metaParameters：元参数，通常用于帮助处理复杂的动态 SQL 情况。
 *
 * BoundSql 的使用流程：
 *
 * 	1.	SQL 解析：当用户调用一个带有动态 SQL 的 MyBatis 方法时，MyBatis 首先会解析动态 SQL 模板，将其生成一个完整的 SQL 语句，并生成 BoundSql 对象。
 * 	2.	参数绑定：在生成 BoundSql 的同时，MyBatis 还会将 SQL 语句中的参数占位符 ? 与实际的参数进行绑定，将参数信息存储在 BoundSql 中。
 * 	3.	SQL 执行：当 SQL 被传递给 JDBC 进行执行时，MyBatis 会通过 BoundSql 中的参数列表和 SQL 语句，使用 JDBC API 将参数注入到 SQL 语句的占位符位置，并执行最终的 SQL。
 *
 * 总结：
 *
 * BoundSql 类在 MyBatis 中的主要作用是存储已经解析好的 SQL 语句以及与该 SQL 语句相关的参数映射信息。它确保 SQL 语句在 MyBatis 内部可以被正确执行，同时帮助将参数绑定到 SQL 占位符中，使得 MyBatis 的动态 SQL 功能能够正确运行。在 SQL 执行前，BoundSql 提供了所有需要的 SQL 和参数信息。
 *
 */
public class BoundSql {

    private String sql;
    private Map<Integer, String> parameterMappings;
    private String parameterType;
    private String resultType;

    public BoundSql(String sql, Map<Integer, String> parameterMappings, String parameterType, String resultType) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.parameterType = parameterType;
        this.resultType = resultType;
    }

    public String getSql() {
        return sql;
    }

    public Map<Integer, String> getParameterMappings() {
        return parameterMappings;
    }

    public String getParameterType() {
        return parameterType;
    }

    public String getResultType() {
        return resultType;
    }
}
