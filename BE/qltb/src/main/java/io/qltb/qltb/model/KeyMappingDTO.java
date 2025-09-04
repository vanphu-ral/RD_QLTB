package io.qltb.qltb.model;

import io.qltb.qltb.domain.Criterial;
import io.qltb.qltb.domain.SampleReport;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class KeyMappingDTO {

    private Long id;
    private SampleReport sampleReport;
    private Criterial criterial;

}
