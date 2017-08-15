/*
 * Copyright (c) 2017. Vitus Lehner. UrbanLife+. Universität der Bundeswehr München.
 */

package org.sociotech.urbanlifeplus.microinforadiator.service;

import com.google.common.eventbus.EventBus;
import org.sociotech.urbanlifeplus.microinforadiator.CoreConfiguration;
import org.sociotech.urbanlifeplus.microinforadiator.model.event.MirStatusEvent;
import org.sociotech.urbanlifeplus.microinforadiator.mqtt.MqttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author vituslehner 12.08.17
 */
@Service
public class StatusPushService implements ApplicationListener<ApplicationReadyEvent> {

    private final TimingService timingService;
    private final MqttService mqttService;
    private final CoreConfiguration coreConfiguration;
    private final EventBus reactorEventBus;

    @Autowired
    public StatusPushService(TimingService timingService,
                             MqttService mqttService,
                             CoreConfiguration coreConfiguration,
                             @Qualifier("reactorEventBus") EventBus reactorEventBus) {
        this.timingService = timingService;
        this.mqttService = mqttService;
        this.coreConfiguration = coreConfiguration;
        this.reactorEventBus = reactorEventBus;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if(coreConfiguration.getPushStatus()) {
            timingService.scheduleAtFixedrate(this::publishStatus, 0, 5, TimeUnit.SECONDS);
        }
    }

    private void publishStatus() {
        MirStatusEvent status = new MirStatusEvent(coreConfiguration.getMirId(), null, Collections.emptySet());
        reactorEventBus.post(status);
    }
}