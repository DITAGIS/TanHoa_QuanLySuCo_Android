package vn.ditagis.com.tanhoa.qlsc.entities;

import com.esri.arcgisruntime.geometry.Point;

import java.util.Date;

public class DiemSuCo {
    public String idSuCo;
    public String vitri;
    public Date ngayPhanAnh;
    private String nguoiPhanAnh;
    public String sdtPhanAnh;
    public short trangThai;
    public short hinhThucPhatHien;
    public String quan;
    public String phuong;
    public String ghiChu;
    public String nguoiCapNhat;
    public Date ngayCapNhat;
    public String nguyenNhan;
    public Point point;
    public byte[] image;

    public DiemSuCo() {
    }

    public String getIdSuCo() {
        return idSuCo;
    }

    public void setIdSuCo(String idSuCo) {
        this.idSuCo = idSuCo;
    }

    public String getVitri() {
        return vitri;
    }

    public void setVitri(String vitri) {
        this.vitri = vitri;
    }

    public Date getNgayPhanAnh() {
        return ngayPhanAnh;
    }

    public void setNgayPhanAnh(Date ngayPhanAnh) {
        this.ngayPhanAnh = ngayPhanAnh;
    }

    public String getNguoiPhanAnh() {
        return nguoiPhanAnh;
    }

    public void setNguoiPhanAnh(String nguoiPhanAnh) {
        this.nguoiPhanAnh = nguoiPhanAnh;
    }

    public String getSdtPhanAnh() {
        return sdtPhanAnh;
    }

    public void setSdtPhanAnh(String sdtPhanAnh) {
        this.sdtPhanAnh = sdtPhanAnh;
    }

    public short getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(short trangThai) {
        this.trangThai = trangThai;
    }

    public short getHinhThucPhatHien() {
        return hinhThucPhatHien;
    }

    public void setHinhThucPhatHien(short hinhThucPhatHien) {
        this.hinhThucPhatHien = hinhThucPhatHien;
    }

    public String getQuan() {
        return quan;
    }

    public void setQuan(String quan) {
        this.quan = quan;
    }

    public String getPhuong() {
        return phuong;
    }

    public void setPhuong(String phuong) {
        this.phuong = phuong;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getNguoiCapNhat() {
        return nguoiCapNhat;
    }

    public void setNguoiCapNhat(String nguoiCapNhat) {
        this.nguoiCapNhat = nguoiCapNhat;
    }

    public Date getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(Date ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }

    public String getNguyenNhan() {
        return nguyenNhan;
    }

    public void setNguyenNhan(String nguyenNhan) {
        this.nguyenNhan = nguyenNhan;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
