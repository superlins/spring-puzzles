package org.example.hbase;

import org.apache.hadoop.hbase.client.*;

import java.util.List;

/**
 * Interface that specifies a basic set of Hbase operations.
 * <p>
 * Implemented by {@link HBaseTemplate}. Not often used, but a useful option for extensibility and testability
 * (as it can be easily mocked, stubbed, or be the target of a JDK proxy).
 * </p>
 *
 * @author renc
 */
public interface HBaseOperations {

    /**
     * Scans the target table, using the given column family.
     * The content is processed row by row by the given action, returning a list of domain objects.
     *
     * @param tableName target table
     * @param family    column family
     * @param <T>       action type
     * @return a list of objects mapping the scanned rows
     */
    <T> List<T> find(String tableName, String family, final RowMapper<T> mapper);

    /**
     * Scans the target table, using the given column family.
     * The content is processed row by row by the given action, returning a list of domain objects.
     *
     * @param tableName target table
     * @param family    column family
     * @param qualifier column qualifier
     * @param <T>       action type
     * @return a list of objects mapping the scanned rows
     */
    <T> List<T> find(String tableName, String family, String qualifier, final RowMapper<T> mapper);

    /**
     * Scans the target table using the given {@link Scan} object. Suitable for maximum control over the scanning
     * process.
     * The content is processed row by row by the given action, returning a list of domain objects.
     *
     * @param tableName target table
     * @param scan      table scanner
     * @param <T>       action type
     * @return a list of objects mapping the scanned rows
     */
    <T> List<T> find(String tableName, final Scan scan, final RowMapper<T> mapper);

    /**
     * Gets an individual row from the given table. The content is mapped by the given action.
     *
     * @param tableName target table
     * @param rowName   row name
     * @param mapper    row mapper
     * @param <T>       mapper type
     * @return object mapping the target row
     */
    <T> T get(String tableName, String rowName, final RowMapper<T> mapper);

    /**
     * Gets an individual row from the given table. The content is mapped by the given action.
     *
     * @param tableName  target table
     * @param rowName    row name
     * @param familyName column family
     * @param mapper     row mapper
     * @param <T>        mapper type
     * @return object mapping the target row
     */
    <T> T get(String tableName, String rowName, String familyName, final RowMapper<T> mapper);

    /**
     * Gets an individual row from the given table. The content is mapped by the given action.
     *
     * @param tableName  target table
     * @param rowName    row name
     * @param familyName family
     * @param qualifier  column qualifier
     * @param mapper     row mapper
     * @param <T>        mapper type
     * @return object mapping the target row
     */
    <T> T get(String tableName, final String rowName, final String familyName, final String qualifier, final RowMapper<T> mapper);

    /**
     * Executes the given action against the specified table handling resource management.
     * <p>
     * Application exceptions thrown by the action object get propagated to the caller (can only be unchecked).
     * Allows for returning a result object (typically a domain object or collection of domain objects).
     *
     * @param tableName the target table
     * @param <T>       action type
     * @return the result object of the callback action, or null
     */
    <T> T execute(String tableName, TableCallback<T> action);

    /**
     * Performs {@link Append}, {@link Increment}, {@link Put}, {@link Delete} operations against the specified table.
     * <p>
     * Application exceptions thrown by the action object get propagated to the caller (can only be unchecked).
     * Allows for returning a result object (typically a domain object or collection of domain objects).
     *
     * @param tableName the target table
     * @param action    action type
     * @see Operation
     * @see Mutation
     * @see BufferedMutator
     */
    void execute(String tableName, MutatorCallback action);

    /**
     * Sends a {@link Mutation} to the table. The mutations will be buffered and sent over the
     * wire as part of a batch. Currently only supports {@link Put} and {@link Delete} mutations.
     *
     * @param tableName the target table
     * @param mutation The data to send.
     */
    void mutate(String tableName, Mutation mutation);

    /**
     * Send some {@link Mutation}s to the table. The mutations will be buffered and sent over the
     * wire as part of a batch. There is no guarantee of sending entire content of {@code mutations}
     * in a single batch; it will be broken up according to the write buffer capacity.
     *
     * @param tableName the target table
     * @param mutations The data to send.
     */
    void mutate(String tableName, List<? extends Mutation> mutations);
}