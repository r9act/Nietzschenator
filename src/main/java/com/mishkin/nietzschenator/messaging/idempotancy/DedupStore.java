package com.mishkin.nietzschenator.messaging.idempotancy;

public interface DedupStore {

    /**
     * @return true если мы событие обработано первый раз
     */
    boolean claim(String correlationId);
}
