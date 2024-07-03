package Adapter;

import android.content.Context;
import android.view.LayoutInflater;import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.demodatabase_ver2.R;

import java.util.ArrayList;

import Model.SinhVien;

public class SVAdapter extends BaseAdapter {

    Context context;
    int layoutitem;
    ArrayList<SinhVien> dulieu;

    public SVAdapter(Context context, int layoutitem, ArrayList<SinhVien> dulieu) {
        this.context = context;
        this.layoutitem = layoutitem;
        this.dulieu = dulieu;
    }

    @Override
    public int getCount() {
        return dulieu.size();
    }

    @Override
    public SinhVien getItem(int position) {
        return dulieu.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutitem, parent, false);
        }

        TextView tvMasv = convertView.findViewById(R.id.tvMasv);
        TextView tvTensv = convertView.findViewById(R.id.tvTensv);
        ImageView imgGioiTinh = convertView.findViewById(R.id.imgGioiTinh);

        SinhVien sv = dulieu.get(position);
        tvMasv.setText(sv.MSSV);
        tvTensv.setText(sv.TENSV);
        imgGioiTinh.setImageResource(sv.GIOITINH ? R.drawable.sharp_boy_24 : R.drawable.sharp_girl_24);

        return convertView;
    }
}