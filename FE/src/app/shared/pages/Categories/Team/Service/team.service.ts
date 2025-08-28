// branch.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseApiService } from '../../../../service/base-api.service';
import { Team } from '../../../../models/Catogories/team.model';

@Injectable({ providedIn: 'root' })
export class TeamService extends BaseApiService<Team> {
  constructor(http: HttpClient) {
    super(http, 'api/teams'); 
  }
}
