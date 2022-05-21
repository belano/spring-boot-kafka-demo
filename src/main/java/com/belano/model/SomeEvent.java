package com.belano.model;

import java.util.UUID;
import lombok.Getter;

@Getter
public class SomeEvent {
    private final String id = UUID.randomUUID().toString();
}
