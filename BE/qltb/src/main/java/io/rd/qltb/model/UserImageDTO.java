package io.rd.qltb.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserImageDTO {

    private Long id;

    @Size(max = 255)
    private String username;

    @Size(max = 500)
    private String imageLink;

}
