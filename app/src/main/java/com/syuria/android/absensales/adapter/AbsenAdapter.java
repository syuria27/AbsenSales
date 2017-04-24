package com.syuria.android.absensales.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.syuria.android.absensales.R;
import com.syuria.android.absensales.model.Absen;

import java.util.List;

/**
 * Created by HP on 30/11/2016.
 */

public class AbsenAdapter extends ArrayAdapter<Absen> {

    // View lookup cache
    private static class ViewHolder {
        TextView txtKodeAbsen;
        TextView txtTanggal;
        TextView txtJam;
        TextView txtLokasi;
    }

    public AbsenAdapter(Context context, int resource, List<Absen> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        Absen absen = getItem(position);

        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lv_history,parent,false);

            viewHolder.txtKodeAbsen = (TextView) convertView.findViewById(R.id.txtKodeAbsen);
            viewHolder.txtTanggal = (TextView) convertView.findViewById(R.id.txtTanggal);
            viewHolder.txtJam = (TextView) convertView.findViewById(R.id.txtJam);
            viewHolder.txtLokasi = (TextView) convertView.findViewById(R.id.txtLokasi);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        }else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtKodeAbsen.setText(absen.getKode_absen());
        viewHolder.txtTanggal.setText(absen.getTanggal());
        viewHolder.txtJam.setText(absen.getJam());
        viewHolder.txtLokasi.setText(absen.getLokasi());

        return convertView;
    }
}
