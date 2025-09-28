package io.rd.qltb.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
@Setter
@Getter
public class ApprovalResponseDTO {
    Map<String,Object> data;
    ApprovalDTO approval;
}
