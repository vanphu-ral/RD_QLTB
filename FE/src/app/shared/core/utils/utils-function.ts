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
    const initials = name
      .split(' ')
      .map((word) => word.charAt(0))
      .join('')
      .toUpperCase();

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
    return Array.from({ length }, () => chars.charAt(Math.floor(Math.random() * chars.length))).join('');
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
  static prepareModel<T extends {
    id?: any;
    code?: string;
    name?: string;
  }>(model: T): T {
    // Nếu không có code thì generate
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

  static isEmptyArray(arr: any[] | null | undefined): boolean {
    return _.isEmpty(arr);
  }

  // Message lỗi
  static handleApiError(error: any, messageService?: any): void {
    let msg = 'Đã có lỗi xảy ra.';

    if (error && error.status) {
      switch (error.status) {
        case 400:
          msg = 'Yêu cầu không hợp lệ (Bad Request).';
          break;
        case 401:
          msg = 'Bạn chưa được xác thực hoặc phiên đăng nhập đã hết hạn.';
          break;
        case 403:
          msg = 'Bạn không có quyền thực hiện thao tác này.';
          break;
        case 404:
          msg = 'Không tìm thấy dữ liệu.';
          break;
        case 409:
          msg = 'Dữ liệu đang tồn tại ở nơi khác, không được phép xóa.';
          break;
        case 500:
          msg = 'Lỗi hệ thống. Vui lòng thử lại sau.';
          break;
        default:
          msg = `Lỗi không xác định (status ${error.status}).`;
          break;
      }
    }

    if (messageService) {
      messageService.add({
        severity: 'error',
        summary: 'Lỗi',
        detail: msg,
        life: 3000
      });
    } else {
      Util.ConfirmMessage(msg, 'error');
    }
  }


}
