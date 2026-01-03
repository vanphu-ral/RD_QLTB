package io.rd.qltb.model;

import io.rd.qltb.domain.Criterial;
import io.rd.qltb.domain.SampleReport;
import io.rd.qltb.enums.OperationsStaff;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class KeyMappingDTO {

    private Long id;

    private String frequency;

    private OperationsStaff performer;

    private SampleReport sampleReport;

    private Criterial criterial;

}
