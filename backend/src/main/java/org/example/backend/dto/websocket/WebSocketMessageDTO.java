package org.example.backend.dto.websocket;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebSocketMessageDTO {

    private String type;
    private Object data;
}
