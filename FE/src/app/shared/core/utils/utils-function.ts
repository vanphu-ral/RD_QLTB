import Swal from 'sweetalert2';
import dayjs from 'dayjs';

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
   * - Gán createdBy = email đăng nhập (nếu thêm mới)
   * - Gán createdAt / updatedAt = thời gian hiện tại
   */
  static prepareModel<T extends {
    id?: any;
    code?: string;
    name?: string;
    createdBy?: string;
  }>(model: T, currentUserEmail: string): T {
    // Nếu không có code thì generate
    if (!model.code && model.name) {
      model.code = Util.generateCode(model.name);
    }

    // Nếu là thêm mới (chưa có id) thì set createdBy
    if (!model.id) {
      model.createdBy = currentUserEmail;
    }

    return model;
  }

  static toastMessage(message: string, type: 'success' | 'error'): void {
    Swal.fire({
      icon: type,
      title: type === 'success' ? 'Thành công' : 'Thất bại',
      text: message,
      confirmButtonText: 'OK'
    });
  }

}
