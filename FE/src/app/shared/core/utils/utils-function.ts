import Swal from 'sweetalert2';
import dayjs from 'dayjs';
import * as _ from 'lodash';

export class Util {
  /**
   * Sinh mã code từ tên + thời gian (ddMMyyyyHHmm)
   * @param name Chuỗi tên (ví dụ: "Nguyen Van A")
   * @returns Ví dụ: "NVA-260820250929"
   */
  static generateCode(name: string): string {
    const initials = _.chain(name)
      .split(' ')
      .map(w => w.charAt(0))
      .join('')
      .toUpper()
      .value();
    const timestamp = dayjs().format('DDMMYYYYHHmm');
    return `${initials}-${timestamp}`;
  }
  

  /**
   * Format ngày giờ theo pattern
   */
  static formatDate(date: string | Date, pattern = 'DD/MM/YYYY HH:mm'): string {
    return dayjs(date).format(pattern);
  }

  /**
   * Sinh chuỗi random (vd: dùng làm mã tạm)
   */
  static randomString(length = 6): string {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    return _.sampleSize(chars, length).join('');
  }

  /**
   * Format ngày giờ theo dd/MM/yyyy HH:mm
   */
  static nowFormatted(): string {
    return dayjs().format('DD/MM/YYYY HH:mm');
  }

  /**
   * Hoàn thiện model trước khi lưu
   * - Nếu chưa có code thì generate từ name
 */
  static prepareModel<T extends { id?: any; code?: string; name?: string }>(model: T): T {
    if (!model.code && model.name) {
      model.code = Util.generateCode(model.name);
    }
    return model;
  }

  static ConfirmMessage(message: string, type: 'success' | 'error'): void {
    Swal.fire({
      icon: type,
      title: type === 'success' ? 'Thành công' : 'Thất bại',
      text: message,
      confirmButtonText: 'OK'
    });
  }

  static toastMessage(message: string, type: 'success' | 'error' | 'info'): void {
    Swal.fire({
      toast: true,
      position: 'top-end',
      icon: type,
      title: message,
      showConfirmButton: false,
      timer: 3000
    });
  }

  // Message lỗi
  static handleApiError(error: any, messageService?: any): void {
    let msg = 'Đã có lỗi xảy ra.';
    if (error && error.status) {
      const statusMessages: Record<number, string> = {
        400: 'Yêu cầu không hợp lệ (Bad Request).',
        401: 'Bạn chưa được xác thực hoặc phiên đăng nhập đã hết hạn.',
        403: 'Bạn không có quyền thực hiện thao tác này.',
        404: 'Không tìm thấy dữ liệu.',
        409: 'Dữ liệu đang tồn tại ở nơi khác, không được phép xóa.',
        500: 'Lỗi hệ thống. Vui lòng thử lại sau.'
      };
      msg = statusMessages[error.status] || `Lỗi không xác định (status ${error.status}).`;
    }

    if (messageService) {
      messageService.add({ severity: 'error', summary: 'Lỗi', detail: msg, life: 3000 });
    } else {
      Util.ConfirmMessage(msg, 'error');
    }
  }

  static isEmptyArray(arr: any[] | null | undefined): boolean {
    return _.isEmpty(arr);
  }

  /**
   * Kiểm tra chuỗi rỗng hoặc chỉ chứa khoảng trắng
   */
  static isEmptyString(str: string | null | undefined): boolean {
    return _.isNil(str) || _.trim(str) === '';
  }
  

  /**
   * Chuyển đổi string sang số an toàn, trả về defaultValue nếu không phải số
   */
  static toNumber(value: any, defaultValue = 0): number {
    const n = _.toNumber(value);
    return _.isNaN(n) ? defaultValue : n;
  }

  /**
   * Chuyển đổi string sang boolean
   * "true", "1" => true; "false", "0" => false
   */
  static toBoolean(value: any): boolean {
    if (_.isBoolean(value)) return value;
    if (_.isNumber(value)) return value !== 0;
    if (_.isString(value)) return ['true', '1', 'yes', 'y'].includes(value.toLowerCase());
    return false;
  }

  /**
   * Format số theo định dạng tiền tệ
   */
  static formatCurrency(value: number, locale = 'vi-VN', currency = 'VND'): string {
    return new Intl.NumberFormat(locale, { style: 'currency', currency }).format(value);
  }

  /**
   * Lấy giá trị random từ một mảng
   */
  static randomFromArray<T>(arr: T[]): T | undefined {
    return _.sample(arr);
  }

  /**
   * Xoá khoảng trắng đầu cuối của tất cả thuộc tính string trong object
   */
  static trimObjectStrings<T extends Record<string, any>>(obj: T): T {
    _.forOwn(obj, (value, key) => {
      if (_.isString(value)) {
        (obj as any)[key] = _.trim(value);
      }
    });
    return obj;
  }

  /**
   * Deep clone một object
   */
  static cloneDeep<T>(obj: T): T {
    return _.cloneDeep(obj);
  }

  /**
   * Merge 2 object, không ghi đè giá trị null/undefined
   */
  static mergeObjects<T>(target: T, source: Partial<T>): T {
    return _.mergeWith(target, source, (objValue, srcValue) => {
      return _.isNil(srcValue) ? objValue : undefined;
    });
  }

  /**
   * Delay (await được)
   * Ví dụ: await Util.delay(1000);
   */
  static delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  /**
   * Check nếu 1 object rỗng
   */
  static isEmptyObject(obj: any): boolean {
    return _.isEmpty(obj);
  }

  /**
   * Tạo slug từ tên
   * Ví dụ: "Nguyen Van A" => "nguyen-van-a"
   */
  static toSlug(str: string): string {
    return _.kebabCase(str);
  }

  /**
   * Lấy random màu hex
   */
  static randomColor(): string {
    return `#${_.random(0, 0xffffff).toString(16).padStart(6, '0')}`;
  }

  /**
   * Kiểm tra object có key nhất định hay không
   */
  static hasKey(obj: any, key: string): boolean {
    return _.has(obj, key);
  }

  /**
   * Lấy ngày đầu tuần (Monday) hoặc cuối tuần (Sunday)
   */
  static startOfWeek(date: Date | string = new Date()): string {
    return dayjs(date).startOf('week').add(1, 'day').format('DD/MM/YYYY');
  }

  static endOfWeek(date: Date | string = new Date()): string {
    return dayjs(date).endOf('week').add(1, 'day').format('DD/MM/YYYY');
  }

  /**
   * Lấy ngày đầu tháng
   */
  static startOfMonth(date: Date | string = new Date()): string {
    return dayjs(date).startOf('month').format('DD/MM/YYYY');
  }

  /**
   * Lấy ngày cuối tháng
   */
  static endOfMonth(date: Date | string = new Date()): string {
    return dayjs(date).endOf('month').format('DD/MM/YYYY');
  }

  /**
   * Kiểm tra chuỗi có phải JSON hợp lệ hay không
   */
  static isValidJson(str: string): boolean {
    try {
      JSON.parse(str);
      return true;
    } catch {
      return false;
    }
  }

  /**
   * Chuyển đổi chuỗi JSON sang object an toàn
   */
  static parseJson<T>(str: string, defaultValue: T): T {
    if (this.isValidJson(str)) {
      return JSON.parse(str);
    }
    return defaultValue;
  }

  /**
   * Chuyển đổi object sang chuỗi JSON an toàn
   */
  static toJsonString(obj: any, defaultValue = ''): string {
    try {
      return JSON.stringify(obj);
    } catch {
      return defaultValue;
    }
  }

  /**
   * Kiểm tra 2 mảng có phần tử giống nhau không
   */
  static arraysIntersect<T>(arr1: T[], arr2: T[]): boolean {
    return !_.isEmpty(_.intersection(arr1, arr2));
  }

  /**
   * Chuyển đổi một chuỗi sang arr option cho dropdown
   * Ví dụ: "A,B,C" => [{name: 'A', code: 'A'}, {name: 'B', code: 'B'}, {name: 'C', code: 'C'}]
   */
  static stringToDropdownOptions(str: string, separator = ','): { name: string; code: string }[] {
    return str.split(separator).map(item => ({
      name: _.trim(item),
      code: _.trim(item)
    }));
  }


}