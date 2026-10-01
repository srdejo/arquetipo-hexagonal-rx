package co.com.srdejo.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObjectRequestDto {
    @NotBlank
    private String name;
}
