package net.ostis.jesc.agent;

import lombok.RequiredArgsConstructor;
import net.ostis.jesc.client.ScClient;

@RequiredArgsConstructor
public class ScAgentRegistry {

    private final ScClient scClient;

    public void registerAgent(ScAgent agent) {
        scClient.addEventHandler(event -> {
            if (!agent.getTriggerEventIds().contains(event.getId())) {
                return;
            }

            agent.onTrigger(event);
        });
    }

}
