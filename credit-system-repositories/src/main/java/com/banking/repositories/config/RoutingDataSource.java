package com.banking.repositories.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Routing data source for read/write operations.
 * Routes read operations to replica and write operations to primary.
 */
public class RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return TransactionSynchronizationManager.isCurrentTransactionReadOnly() ?
                ReadReplicaConfig.DataSourceType.REPLICA : ReadReplicaConfig.DataSourceType.PRIMARY;
    }
} 