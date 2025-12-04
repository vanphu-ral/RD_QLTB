package io.rd.qltb.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DetailLogResponseDTO {
private Map<String,Object> data;
private List<DetailLogDTO> detailLog;
}
