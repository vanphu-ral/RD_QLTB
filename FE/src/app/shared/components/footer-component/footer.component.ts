import { Component } from '@angular/core';
import { SharedModule } from '../../../share.module';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class AppFooterComponent { }
