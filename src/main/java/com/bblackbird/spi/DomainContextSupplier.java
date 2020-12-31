package com.bblackbird.spi;

public interface DomainContextSupplier<C> {

    C getDomainContext();

}
