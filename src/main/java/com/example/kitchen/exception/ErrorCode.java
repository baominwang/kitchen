package com.example.kitchen.exception;

import java.io.Serializable;

public enum ErrorCode implements Serializable {
    /* common error code */
    ReachOrderLimit,
    NoIdleCourier,
}
