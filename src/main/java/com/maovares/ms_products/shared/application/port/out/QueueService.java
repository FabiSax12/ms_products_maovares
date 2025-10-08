package com.maovares.ms_products.shared.application.port.out;

import com.maovares.ms_products.shared.domain.model.Event;

public interface QueueService {
    void sendMessage(Event event);
}
