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

        //Anh xa
        lvSinhVien = findViewById(R.id.lvSinhVien);
        edtMSSV = findViewById(R.id.edtMSSV);
        edtTENSV = findViewById(R.id.edtTENSV);
        rbNam = findViewById(R.id.rbNam);
        rbNu = findViewById(R.id.rbNu);
        btnThem = findViewById(R.id.btnThem);
        btnSua = findViewById(R.id.btnSua);
        btnXoa = findViewById(R.id.btnXoa);

        //Event
        btnThem.setOnClickListener(this);
        btnSua.setOnClickListener(this);
        btnXoa.setOnClickListener(this);

        //Mo database
        qlsvOpenHelper = new DBHelper(this);
        db = qlsvOpenHelper.getWritableDatabase();

        SinhVienAdapter = new SVAdapter(this, R.layout.item_sinhvien, dulieu);
        lvSinhVien.setAdapter(SinhVienAdapter);

        loadListview();

        lvSinhVien.setOnItemClickListener((parent, view, position, id) -> {
            SinhVien sinhVien = dulieu.get(position); // Lấy sinh viên tại vị trí được click

            // Gán dữ liệu lên các trường nhập liệu
            edtMSSV.setText(sinhVien.MSSV);
            edtTENSV.setText(sinhVien.TENSV);
            if (sinhVien.GIOITINH) {
                rbNam.setChecked(true);
            } else {
                rbNu.setChecked(true);
            }
        });

    }

    private void loadListview() {
        dulieu.clear();
        Cursor cursor = db.query("SinhVien", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            dulieu.add(new SinhVien(cursor.getString(0), cursor.getString(1), cursor.getInt(2) == 1 ? true : false));
        }
        cursor.close();
        SinhVienAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        ContentValues contentValues = new ContentValues();

        if (id == R.id.btnThem) {
            String mssv = edtMSSV.getText().toString();
            String tensv = edtTENSV.getText().toString();

            // Kiểm tra nếu không nhập mã số hoặc tên sinh viên
            if (mssv.isEmpty() || tensv.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra xem có RadioButton nào được chọn hay không
            if (rbNam.isChecked() || rbNu.isChecked()) {
                boolean gioitinh = rbNam.isChecked();

                // Thêm dữ liệu vào cơ sở dữ liệu SQLite
                contentValues.put("MSSV", mssv);
                contentValues.put("TENSV", tensv);
                contentValues.put("GIOITINH", gioitinh ? 1 : 0);
                db.insert("SinhVien", null, contentValues);
                loadListview();
                edtMSSV.setText("");
                edtTENSV.setText("");
                rbNam.setChecked(true);
            } else {
                Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.btnSua) {
            String mssv = edtMSSV.getText().toString();
            String tensv = edtTENSV.getText().toString();

            db = qlsvOpenHelper.getWritableDatabase();

            // Kiểm tra nếu không nhập mã số hoặc tên sinh viên
            if (mssv.isEmpty() || tensv.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra xem có RadioButton nào được chọn hay không
            if (rbNam.isChecked() || rbNu.isChecked()) {
                boolean gioitinh = rbNam.isChecked();

                contentValues.put("MSSV", mssv);
                contentValues.put("TENSV", tensv);
                contentValues.put("GIOITINH", gioitinh ? 1 : 0);
                db.update("SinhVien", contentValues, "MSSV=?", new String[]{mssv});
                loadListview();
                edtMSSV.setText("");
                edtTENSV.setText("");
                rbNam.setChecked(true);
            }
        } else if (id == R.id.btnXoa) {
            String mssv = edtMSSV.getText().toString();

            // Kiểm tra nếu không nhập mã số sinh viên
            if (mssv.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập MSSV để xóa", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo dialog xác nhận xóa
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa sinh viên có mssv " + mssv + " này?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        db.delete("SinhVien", "MSSV=?", new String[]{mssv});
                        loadListview();
                        edtMSSV.setText("");
                        edtTENSV.setText("");
                        rbNam.setChecked(true);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        }
    }
}
