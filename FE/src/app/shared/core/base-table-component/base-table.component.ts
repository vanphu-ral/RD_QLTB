import { Component, Input, OnInit, ContentChildren, QueryList, TemplateRef, AfterContentInit, ViewChild } from '@angular/core';
import { Table } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { FormsModule } from '@angular/forms';
import { CustomFilterDirective } from '../../directive/app.custom-filter.directive';
import { BaseApiService } from '../../service/base-api.service';
import { SharedModule } from '../../../share.module';
import { SelectButtonModule } from 'primeng/selectbutton'; // p-select alternative
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-base-table',
  standalone: true,
  imports: [SharedModule, TableModule, ButtonModule, FormsModule, SelectButtonModule],
  templateUrl: './base-table.component.html',
  styleUrls: ['./base-table.component.scss'],
})
export class BaseTableComponent<T> implements OnInit, AfterContentInit {
  @Input() apiService!: BaseApiService<T>;
  @Input() columns: {
    Field: string;
    Header: string;
    IsSearch?: boolean;
    IsHide?: boolean;
    TypeSearch?: 'text' | 'date' | 'select';
    Options?: { label: string; value: any }[];
  }[] = [];
  @Input() onAddClick?: () => void;  // callback override từ component cha
  @Input() title?: string;
  @Input() showAddButton = false;
  @Input() addButtonText = 'Thêm mới';
  @ContentChildren(CustomFilterDirective) customFilters!: QueryList<CustomFilterDirective>;
  private filterTpls = new Map<string, TemplateRef<any>>();
  @ViewChild('dt') dt!: Table;

  data: any[] = [];
  loading = false;

  constructor(private router: Router, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.columns = this.columns.map(c => ({
      ...c,
      IsSearch: c.IsSearch ?? false,
      IsHide: c.IsHide ?? false,
    }));

    // Demo data
    this.data = [
      { id: 1, code: 'PB001', name: 'Phòng Kế Toán', status: 'active' },
      { id: 2, code: 'PB002', name: 'Phòng Nhân Sự', status: 'inactive' },
      { id: 3, code: 'PB003', name: 'Phòng Kỹ Thuật', status: 'active' },
      { id: 4, code: 'PB004', name: 'Phòng Kinh Doanh', status: 'inactive' },
      { id: 5, code: 'PB005', name: 'Phòng IT', status: 'active' }
    ];
    // this.loadData();
  }

  ngAfterContentInit(): void {
    const rebuild = () => {
      this.filterTpls.clear();
      this.customFilters?.forEach(d => this.filterTpls.set(d.field, d.template));
    };
    rebuild();
    this.customFilters?.changes.subscribe(rebuild);
  }

  getFilterTemplate(field: string): TemplateRef<any> | null {
    return this.filterTpls.get(field) ?? null;
  }

  loadData(): void {
    this.loading = true;
    this.apiService.getAll().subscribe({
      next: (res) => {
        this.data = res;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  addItem() {
    if (this.onAddClick) {
      // Nếu cha truyền hàm thì gọi hàm cha
      this.onAddClick();
    } else {
      // Nếu không truyền thì navigate mặc định tới router hiện tại + /add
      this.router.navigate(['add'], { relativeTo: this.route });
    }
  }

  editItem(row: any) {
    this.router.navigate([row.id, 'edit'], { relativeTo: this.route });
  }

  viewItem(row: any) {
    this.router.navigate([row.id, 'view'], { relativeTo: this.route });
  }

  deleteItem(item: T & { id: number | string }) {
    if (this.apiService) {
      this.apiService.delete(item.id).subscribe(() => {
        this.data = this.data.filter(d => d !== item);
      });
    } else {
      this.data = this.data.filter(d => d !== item);
    }
  }
}
