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
import { ConfirmationService, MessageService } from 'primeng/api';
import { Util } from '../utils/utils-function';

@Component({
  selector: 'app-base-table',
  standalone: true,
  imports: [SharedModule, TableModule, ButtonModule, FormsModule, SelectButtonModule],
  providers: [ConfirmationService, MessageService],
  templateUrl: './base-table.component.html',
  styleUrls: ['./base-table.component.scss'],
})
export class BaseTableComponent<T> implements OnInit, AfterContentInit {
  @Input() apiService!: BaseApiService<T>;
  @Input() columns: Column[] = []
  @Input() onAddClick?: () => void;
  @Input() title?: string;
  @Input() showAddButton = false;
  @Input() showEditButton = true;
  @Input() showViewButton = true;
  @Input() showDeleteButton = true;
  @Input() addButtonText = 'Thêm mới';
  @Input() actionTemplate?: TemplateRef<any>;
  @Input() onDeleteItem?: (row: any, event: Event) => void;
  @ContentChildren(CustomFilterDirective) customFilters!: QueryList<CustomFilterDirective>;
  @ContentChildren(CustomColumnDirective) columnTemplates!: QueryList<CustomColumnDirective>;
  private filterTpls = new Map<string, TemplateRef<any>>();
  @ViewChild('dt') dt!: Table;

  data: any[] = [];
  loading = false;
  selectedColumns: any[] = [];

  constructor(private router: Router, private route: ActivatedRoute, private cdr: ChangeDetectorRef, private confirmationService: ConfirmationService, private messageService: MessageService) { }

  ngOnInit(): void {
    this.columns = this.columns.map(c => ({
      ...c,
      IsSearch: c.IsSearch ?? false,
      IsHide: c.IsHide ?? false,
    }));
    this.selectedColumns = [...this.columns];
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

  getValueByPath(obj: any, path: string): any {
    if (!obj || !path) return null;
    return path.split('.').reduce((acc, part) => acc && acc[part], obj);
  }

  // prepare data
  loadData(): void {
    this.loading = true;
    this.apiService.getAll().subscribe({
      next: (res) => {
        this.data = res;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
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

  deleteItem(item: T & { id: number | string }, event: Event) {
    this.confirmationService.confirm({
      target: event.currentTarget as EventTarget,
      message: 'Bạn có muốn xóa bản ghi này?',
      header: 'Xóa bản ghi',
      icon: 'pi pi-info-circle',
      rejectButtonProps: {
        label: 'Hủy',
        severity: 'secondary',
        outlined: true
      },
      acceptButtonProps: {
        label: 'Xóa',
        severity: 'danger'
      },
      accept: () => {
        if (this.onDeleteItem) {
          const result: any = this.onDeleteItem(item, event);
          if (result && 'subscribe' in result) {
            result.subscribe({
              next: () => {
                this.messageService.add({
                  severity: 'info',
                  summary: 'Đã xác nhận',
                  detail: 'Xóa thành công!',
                  life: 3000
                });
              },
              error: () => this.loadData(),
              complete: () => this.loadData()
            });
          } else {
            this.loadData();
          }
        } else if (this.apiService) {
          this.apiService.delete(item.id).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'info',
                summary: 'Đã xác nhận',
                detail: 'Xóa thành công!',
                life: 3000
              });
            },
            error: (error) => {
              Util.handleApiError(error, this.messageService);
            },
            complete: () => {
              this.loadData();
            }
          });
        } else {
          this.data = this.data.filter(d => d !== item);
          this.loadData();
        }
      },
      reject: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Từ chối',
          detail: 'Từ chối xóa bản ghi',
          life: 3000
        });
      }
    });
  }

}
