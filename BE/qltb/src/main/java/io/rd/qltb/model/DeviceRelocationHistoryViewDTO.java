package io.rd.qltb.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceRelocationHistoryViewDTO extends DeviceRelocationHistoryDTO {

    private String oldFactoryName;
    private String newFactoryName;
    private String oldBranchName;
    private String newBranchName;
    private String oldTeamName;
    private String newTeamName;
    private String oldLineName;
    private String newLineName;
}
