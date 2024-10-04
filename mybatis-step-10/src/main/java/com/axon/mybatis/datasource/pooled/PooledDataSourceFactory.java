package com.axon.mybatis.datasource.pooled;

import com.axon.mybatis.datasource.unpooled.UnpooledDataSourceFactory;

import javax.sql.DataSource;

/**
 * 在应用程序中，设置数据库的连接池时，可以设置到非常大吗？
 *
 * 1. 数据库的连接承载能力
 *
 * 数据库服务器能够处理的最大连接数是有限的，通常数据库（如 MySQL、PostgreSQL 等）默认的最大连接数限制是 100~1000 之间。如果你将连接池设置得过大，可能会导致以下问题：
 *
 * 	•	耗尽数据库连接：数据库无法处理过多的并发连接请求，导致拒绝新的连接。
 * 	•	性能下降：连接数过多时，数据库在管理连接的资源上消耗较大，可能影响整体性能。
 *
 * 2. 应用程序的并发需求
 *
 * 应用程序的并发请求量决定了需要多少数据库连接。过小的连接池会导致线程在获取连接时进入等待状态，增加响应延迟。因此，连接池应该与应用程序的并发需求相匹配。
 *
 * 如果你的应用有 1000 个并发请求，而连接池仅有 10 个连接，那么其余的 990 个请求需要等待释放的连接，导致性能瓶颈。
 *
 * 3. 内存和资源占用
 *
 * 每个数据库连接都需要占用一定的系统资源和内存。如果连接池太大，可能导致应用服务器和数据库服务器的内存资源消耗过多，甚至导致内存不足或服务器崩溃。特别是在 JVM 中，每个连接对象都会占用一定的堆内存，设置过大的连接池会增加 GC（垃圾回收）的负担。
 *
 * 4. 最大并发数据库连接数
 *
 * 数据库连接池的大小应结合数据库的最大并发连接数来配置。例如：
 *
 * 	•	MySQL 中可以通过 SHOW VARIABLES LIKE 'max_connections'; 来查看最大并发连接数。
 * 	•	你可以根据应用的负载以及其他系统服务对数据库的连接需求，合理设置连接池的大小。一般建议留有一定的余量，比如设置为 max_connections 的 70% 左右。
 *
 * 5. 连接池优化建议
 *
 * 	•	合理的池大小：大多数情况下，设置连接池大小为 CPU 核心数的 2~5 倍是比较常见的做法（但这要结合实际的应用场景进行调整）。
 * 	•	动态调整池大小：一些连接池实现（如 HikariCP、DBCP）支持动态调整连接池大小，可以设置 minimumPoolSize 和 maximumPoolSize，当负载增加时自动扩展，负载减少时自动缩减。
 * 	•	使用连接池监控：监控连接池的使用情况，查看是否有连接数过多或不够的情况，然后基于实际使用数据来优化配置。
 *
 * 6. 常见连接池的默认设置
 *
 * 	•	HikariCP：默认最小池大小为 10，最大池大小为 10。
 * 	•	DBCP（Apache Commons DBCP）：默认最大池大小为 8。
 * 	•	C3P0：默认最大池大小为 15，最小池大小为 3。
 *
 * 7. 如何合理配置连接池大小
 *
 * 一个常见的公式可以帮助估算连接池大小：
 *  池大小 = ((核心数 × 2) + 有效磁盘数)
 *  这个公式将 CPU 和 IO 资源都考虑在内，但最终的连接池大小仍应基于测试和实际的负载进行调整。
 *
 * 结论：
 *
 * 连接池不能设置得过大，应该根据数据库的承载能力、应用的并发需求和系统的资源情况合理配置。过大的连接池不仅会导致资源浪费，甚至可能影响数据库和服务器的性能，最终导致应用性能下降。
 */
public class PooledDataSourceFactory extends UnpooledDataSourceFactory {


    @Override
    public DataSource getDataSource() {
        return dataSource;
    }
}
