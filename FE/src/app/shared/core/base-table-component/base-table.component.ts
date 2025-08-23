import { Component, Input, OnInit, ContentChildren, QueryList, TemplateRef, AfterContentInit, ViewChild, ChangeDetectorRef } from '@angular/core';
import { Table } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { FormsModule } from '@angular/forms';
import { CustomFilterDirective } from '../../directive/app.custom-filter.directive';
import { BaseApiService } from '../../service/base-api.service';
import { SharedModule } from '../../../share.module';
import { SelectButtonModule } from 'primeng/selectbutton'; // p-select alternative
import { ActivatedRoute, Router } from '@angular/router';
import { Column } from '../../models/Core/column.model';
import { CustomColumnDirective } from '../../directive/app.custom-column.directive';

@Component({
  selector: 'app-base-table',
  standalone: true,
  imports: [SharedModule, TableModule, ButtonModule, FormsModule, SelectButtonModule],
  templateUrl: './base-table.component.html',
  styleUrls: ['./base-table.component.scss'],
})
export class BaseTableComponent<T> implements OnInit, AfterContentInit {
  @Input() apiService!: BaseApiService<T>;
  @Input() columns: Column[] = []
  @Input() onAddClick?: () => void;  // callback override từ component cha
  @Input() title?: string;
  @Input() showAddButton = false;
  @Input() addButtonText = 'Thêm mới';
  @ContentChildren(CustomFilterDirective) customFilters!: QueryList<CustomFilterDirective>;
  @ContentChildren(CustomColumnDirective) columnTemplates!: QueryList<CustomColumnDirective>;
  private filterTpls = new Map<string, TemplateRef<any>>();
  @ViewChild('dt') dt!: Table;

  data: any[] = [];
  loading = false;

  constructor(private router: Router, private route: ActivatedRoute, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.columns = this.columns.map(c => ({
      ...c,
      IsSearch: c.IsSearch ?? false,
      IsHide: c.IsHide ?? false,
    }));
    this.loadData();
  }

  // function support template
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

  getColumnTemplate(field: string): TemplateRef<any> | null {
    const template = this.columnTemplates.find(t => t.field === field);
    return template ? template.template : null;
  }


  getColumnStyle(col: { style?: string }): { [key: string]: string } | null {
    if (!col.style) return null;
    const styleObj: { [key: string]: string } = {};
    col.style.split(';').forEach(pair => {
      const [key, value] = pair.split(':').map(s => s.trim());
      if (key && value) {
        styleObj[key] = value;
      }
    });
    return Object.keys(styleObj).length ? styleObj : null;
  }

  // prepare data
  loadData(): void {
    this.loading = true;
    this.apiService.getAll().subscribe({
      next: (res) => {
        this.data = res;
        this.cdr.detectChanges();
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }


  // function
  addItem() {
    if (this.onAddClick) {
      this.onAddClick();
    } else {
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
