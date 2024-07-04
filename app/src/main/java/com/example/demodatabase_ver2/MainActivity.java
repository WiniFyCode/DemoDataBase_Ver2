package com.example.demodatabase_ver2;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.demodatabase_ver2.R;

import java.util.ArrayList;

import Adapter.SVAdapter;
import Model.SinhVien;
import SQLite.DBHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ListView lvSinhVien;
    EditText edtMSSV, edtTENSV;
    RadioButton rbNam, rbNu;
    AppCompatButton btnThem, btnSua, btnXoa;
    ArrayList<SinhVien> dulieu = new ArrayList<>();
    SVAdapter SinhVienAdapter;
    DBHelper qlsvOpenHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ánh xạ các thành phần giao diện
        lvSinhVien = findViewById(R.id.lvSinhVien);
        edtMSSV = findViewById(R.id.edtMSSV);
        edtTENSV = findViewById(R.id.edtTENSV);
        rbNam = findViewById(R.id.rbNam);
        rbNu = findViewById(R.id.rbNu);
        btnThem = findViewById(R.id.btnThem);
        btnSua = findViewById(R.id.btnSua);
        btnXoa = findViewById(R.id.btnXoa);

        // Đăng ký sự kiện cho các nút
        btnThem.setOnClickListener(this);
        btnSua.setOnClickListener(this);
        btnXoa.setOnClickListener(this);

        // Mở cơ sở dữ liệu
        qlsvOpenHelper = new DBHelper(this);
        db = qlsvOpenHelper.getWritableDatabase();

        // Khởi tạo adapter và gán cho ListView
        SinhVienAdapter = new SVAdapter(this, R.layout.item_sinhvien, dulieu);
        lvSinhVien.setAdapter(SinhVienAdapter);

        // Tải dữ liệu vào ListView
        loadListview();// Đăng ký sự kiện click cho ListView
        lvSinhVien.setOnItemClickListener((parent, view, position, id) -> {
            SinhVien sinhVien = dulieu.get(position);
            edtMSSV.setText(sinhVien.MSSV);
            edtTENSV.setText(sinhVien.TENSV);
            if (sinhVien.GIOITINH) {
                rbNam.setChecked(true);
                rbNu.setChecked(false); // Đảm bảo chỉ có một RadioButton được chọn
            } else {
                rbNu.setChecked(true);
                rbNam.setChecked(false); // Đảm bảo chỉ có một RadioButton được chọn
            }
        });
    }

    private void loadListview() {
        dulieu.clear(); // Xóa toàn bộ dữ liệu cũ trong danh sách dulieu
        Cursor cursor = db.query("SinhVien", null, null, null, null, null, null);
        // Thực hiện truy vấn lấy toàn bộ dữ liệutừ bảng SinhVien trong database
        while (cursor.moveToNext()) {
            // Lặp qua từng dòng kết quả của truy vấn
            dulieu.add(new SinhVien(
                    cursor.getString(0), // Lấy MSSV (cột 0)
                    cursor.getString(1), // Lấy TENSV (cột 1)
                    cursor.getInt(2) == 1 ? true : false // Lấy GIOITINH (cột 2) và chuyển đổi sang kiểu boolean
            ));
            // Thêm đối tượng SinhVien mới vào danhsách dulieu
        }
        cursor.close(); // Đóng Cursor sau khi sử dụng xong
        SinhVienAdapter.notifyDataSetChanged(); // Thông báo cho adapter biết dữ liệu đã thay đổi để cập nhật ListView
    }

    @Override
    public void onClick(View v) {
        int id = v.getId(); // Lấy ID của View được click
        ContentValues contentValues = new ContentValues(); // Tạo đối tượng ContentValues để lưu trữ dữ liệu cần thêm/sửa

        if (id == R.id.btnThem) { // Nếu nút "Thêm" được click
            String mssv = edtMSSV.getText().toString(); // Lấy MSSV từ EditText
            String tensv = edtTENSV.getText().toString(); // Lấy TENSV từ EditText

            // Kiểm tra nếu không nhập mã số hoặc tên sinh viên
            if (mssv.isEmpty() || tensv.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show(); // Hiển thị thông báo lỗi
                return; // Thoát khỏi phương thức
            }

            // Kiểm tra xem có RadioButton nào được chọnhay không
            if (rbNam.isChecked() || rbNu.isChecked()) {
                boolean gioitinh = rbNam.isChecked(); // Lấy giá trị giới tính (true: Nam, false: Nữ)

                // Thêm dữ liệu vào cơ sở dữ liệu SQLite
                contentValues.put("MSSV", mssv); // Thêm MSSV vào ContentValues
                contentValues.put("TENSV", tensv); // Thêm TENSV vào ContentValues
                contentValues.put("GIOITINH", gioitinh ? 1 : 0); // Thêm GIOITINH vào ContentValues (chuyển đổi boolean sang int)
                db.insert("SinhVien", null, contentValues); // Thêm dữ liệu vào bảng SinhVien
                loadListview(); // Tải lại dữ liệu vào ListView
                edtMSSV.setText(""); // Xóa nội dung EditText MSSV
                edtTENSV.setText(""); // Xóa nội dung EditText TENSV
                rbNam.setChecked(true); // Chọn RadioButton Nam
            } else {
                Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show(); // Hiển thị thông báo lỗi
            }
        } else if (id == R.id.btnSua) { // Nếu nút "Sửa" được click
            String mssv = edtMSSV.getText().toString(); // Lấy MSSV từ EditText
            String tensv = edtTENSV.getText().toString(); // Lấy TENSV từ EditText

            db = qlsvOpenHelper.getWritableDatabase(); // Mở lại database (nếu cần)

            // Kiểm tra nếu không nhập mã số hoặc tên sinh viên
            if (mssv.isEmpty() || tensv.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show(); // Hiển thị thông báo lỗi
                return; // Thoát khỏi phương thức
            }

            // Kiểm tra xem có RadioButton nào được chọn hay không
            if (rbNam.isChecked() || rbNu.isChecked()) {
                boolean gioitinh = rbNam.isChecked(); // Lấy giá trị giới tính (true: Nam, false: Nữ)

                contentValues.put("MSSV", mssv); // Thêm MSSV vào ContentValues
                contentValues.put("TENSV", tensv); // Thêm TENSV vào ContentValues
                contentValues.put("GIOITINH", gioitinh ? 1 : 0); // Thêm GIOITINH vào ContentValues (chuyển đổi boolean sang int)
                db.update("SinhVien", contentValues, "MSSV=?", new String[]{mssv}); // Cập nhật dữ liệu trong bảng SinhVien
                loadListview(); // Tải lại dữ liệu vào ListView
                edtMSSV.setText(""); // Xóa nội dung EditText MSSV
                edtTENSV.setText(""); // Xóa nội dung EditText TENSV
                rbNam.setChecked(true); // Chọn RadioButton Nam
            }
        } else if (id == R.id.btnXoa) { // Nếu nút "Xóa" được click
            String mssv = edtMSSV.getText().toString(); // Lấy MSSV từ EditText

            // Kiểm tra nếu không nhập mã số sinh viên
            if (mssv.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập MSSV để xóa", Toast.LENGTH_SHORT).show(); // Hiển thị thông báo lỗi
                return; // Thoát khỏi phương thức
            }

            // Tạo dialog xác nhận xóa
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận xóa") // Đặt tiêu đề cho dialog
                    .setMessage("Bạn có chắc chắn muốn xóa sinh viên có mssv " + mssv + " này?") // Đặt nội dung cho dialog
                    .setPositiveButton("Xóa", (dialog, which) -> { // Xử lý sự kiện khi nhấn nút "Xóa"
                        db.delete("SinhVien", "MSSV=?", new String[]{mssv}); // Xóa sinh viên khỏi bảng SinhVien
                        loadListview(); // Tải lại dữ liệu vào ListView
                        edtMSSV.setText(""); // Xóa nội dung EditText MSSV
                        edtTENSV.setText(""); // Xóa nội dung EditText TENSV
                        rbNam.setChecked(true); // Chọn RadioButton Nam
                    })
                    .setNegativeButton("Hủy", null) // Xử lý sự kiện khi nhấn nút "Hủy" (không làm gì)
                    .show(); // Hiển thị dialog
        }
    }
}
