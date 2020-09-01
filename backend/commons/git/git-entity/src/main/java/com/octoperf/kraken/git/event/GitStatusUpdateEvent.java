package com.octoperf.kraken.git.event;

import com.octoperf.kraken.tools.event.bus.BusEvent;
import lombok.Value;

@Value
public class GitStatusUpdateEvent implements BusEvent {
}
