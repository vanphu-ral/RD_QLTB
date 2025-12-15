import { Component } from '@angular/core';
import { SharedModule } from '../../../../share.module';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { NavigationService } from '../../../service/navigation.service';


@Component({
  selector: 'report-2',
  standalone: true,
  imports: [SharedModule, FormsModule],
  providers: [ConfirmationService, MessageService],
  templateUrl: './report-2.page.html',
  styleUrls: ['./report-2.page.scss'],
})
export class Report2Page {

  filter: any = {};

  listBranchs: any[] = [];

  data: any[] = [];
  loading: boolean = false

  constructor(private navigationService: NavigationService) { }

  ngOnInit(): void { }

  public onBack(): void {
    this.navigationService.back();
  }
}
