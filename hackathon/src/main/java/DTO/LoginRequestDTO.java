package DTO;

import java.time.Instant;

public record LoginRequestDTO(String password, int user_id, String email, Instant CreatedAt) {
}
