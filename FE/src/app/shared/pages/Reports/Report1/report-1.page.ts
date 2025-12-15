import { Component } from '@angular/core';
import { SharedModule } from '../../../../share.module';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { NavigationService } from '../../../service/navigation.service';


@Component({
  selector: 'report-1',
  standalone: true,
  imports: [SharedModule, FormsModule],
  providers: [ConfirmationService, MessageService],
  templateUrl: './report-1.page.html',
  styleUrls: ['./report-1.page.scss'],
})
export class Report1Page {

  filter: any = {}

  listBranchs: any[] = []
  listTeams: any[] = []
  data: any[] = []
  
  loading: boolean = false

  constructor(private navigationService: NavigationService) { }

  public onBack(): void {
    this.navigationService.back();
  }
}
