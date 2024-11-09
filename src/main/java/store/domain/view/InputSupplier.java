package store.domain.view;

import store.common.exception.BusinessException;

@FunctionalInterface
public interface InputSupplier<T> {
    T get() throws BusinessException;
}