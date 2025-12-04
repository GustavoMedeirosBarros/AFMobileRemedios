package com.example.afremedios.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.afremedios.R;
import com.example.afremedios.model.Remedio;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RemedioAdapter extends RecyclerView.Adapter<RemedioAdapter.ViewHolder> {
    private List<Remedio> remedios;

    public List<Remedio> getRemedios() {
        return remedios;
    }

    public RemedioAdapter(List<Remedio> remedios) {
        this.remedios = remedios;
    }

    public interface OnItemClickListener {
        void onItemClick(Remedio remedio);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_remedio, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        Remedio r = remedios.get(pos);
        holder.txtNome.setText(r.getNome());
        holder.txtDetalhes.setText(String.format("%s | Horário: %s", r.getDescricao(), r.getHorario()));

        corCheckBox(holder, r.isTomado());

        holder.checkBoxTomado.setChecked(r.isTomado());

        holder.checkBoxTomado.setOnCheckedChangeListener((buttonView, isChecked) -> {
            r.setTomado(isChecked);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(r);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            deletarRemedio(r.getId(), holder.getBindingAdapterPosition(), v);
            return true;
        });
    }

    private void corCheckBox(ViewHolder holder, boolean tomado) {
        if (tomado) {
            holder.txtStatus.setText("Tomado");
            holder.checkBoxTomado.setButtonTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50));
        } else {
            holder.txtStatus.setText("Não tomado");
            holder.checkBoxTomado.setButtonTintList(android.content.res.ColorStateList.valueOf(0xFFF44336));
        }
    }
    private void deletarRemedio(String idDocumento, int position, View view) {
        FirebaseFirestore.getInstance().collection("remedios")
                .document(idDocumento)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    remedios.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(view.getContext(), "Remedio deletado!", Toast.LENGTH_SHORT).show();
                });
    }
    @Override
    public int getItemCount() {
        return remedios.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome, txtDetalhes, txtStatus;
        CheckBox checkBoxTomado;
        public ViewHolder(View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.tvNome);
            txtDetalhes = itemView.findViewById(R.id.tvDetalhes);
            checkBoxTomado = itemView.findViewById(R.id.cbTomado);
            txtStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
