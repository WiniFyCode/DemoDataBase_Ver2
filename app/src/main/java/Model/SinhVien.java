package Model;

// Lớp đại diện cho một sinh viên
public class SinhVien {
    public String MSSV; // Mã số sinh viên
    public String TENSV; // Tên sinh viên
    public boolean GIOITINH; //Giới tính (true: Nam, false: Nữ)
    public String MAKHOA; // Mã khóa học

    // Constructor để khởi tạo một đối tượng SINHVIEN
    public SinhVien(String MSSV, String TENSV, boolean GIOITINH) {
        this.MSSV = MSSV; // Gán giá trị MSSV
        this.TENSV = TENSV; // Gán giá trị TENSV
        this.GIOITINH = GIOITINH; // Gán giá trị GIOITINH
        this.MAKHOA = MAKHOA; // Mã khóa học
    }
}