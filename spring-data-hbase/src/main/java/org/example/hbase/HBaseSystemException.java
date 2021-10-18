package org.example.hbase;

import org.springframework.core.NestedRuntimeException;

/**
 * @author renc
 */
public class HBaseSystemException extends NestedRuntimeException {

    public HBaseSystemException(String msg, Exception ex) {
        super(msg, ex);
    }

    public HBaseSystemException(Throwable th) {
        super(th.getMessage(), th);
    }
}
