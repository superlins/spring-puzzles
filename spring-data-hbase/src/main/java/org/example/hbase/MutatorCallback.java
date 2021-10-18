package org.example.hbase;

import org.apache.hadoop.hbase.client.BufferedMutator;
import org.apache.hadoop.hbase.client.Table;

/**
 * Callback interface for Hbase code. To be used with {@link HBaseTemplate}'s
 * execution methods, often as anonymous classes within a method implementation without
 * having to worry about exception handling.
 *
 * @author renc
 */
public interface MutatorCallback {

    /**
     * Used to communicate with a single HBase table similar to {@link Table} but meant for
     * batched, asynchronous puts.
     *
     * @param mutator BufferedMutator instance
     * @throws Throwable thrown by the Hbase API
     */
    void doInMutator(BufferedMutator mutator) throws Throwable;
}