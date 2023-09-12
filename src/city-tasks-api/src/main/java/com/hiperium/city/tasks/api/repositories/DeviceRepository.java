package com.hiperium.city.tasks.api.repositories;

import com.hiperium.city.tasks.api.models.Device;
import com.hiperium.city.tasks.api.models.Task;
import reactor.core.publisher.Mono;

public interface DeviceRepository {

    Mono<Device> updateStatusByTaskOperation(Task task);

    Mono<Device> findById(String id);

}
