package com.qrgen.app.ui.history;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.WriterException;
import com.qrgen.app.R;
import com.qrgen.app.data.db.QRCodeEntity;
import com.qrgen.app.data.model.QRSource;
import com.qrgen.app.data.model.QRType;
import com.qrgen.app.databinding.ItemHistoryBinding;
import com.qrgen.app.generator.QRGeneratorService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryAdapter extends ListAdapter<QRCodeEntity, HistoryAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onClick(QRCodeEntity entity);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(QRCodeEntity entity);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(QRCodeEntity entity);
    }

    private final OnItemClickListener onItemClick;
    private final OnFavoriteClickListener onFavoriteClick;
    private final OnDeleteClickListener onDeleteClick;

    private static final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

    public HistoryAdapter(OnItemClickListener onItemClick,
                          OnFavoriteClickListener onFavoriteClick,
                          OnDeleteClickListener onDeleteClick) {
        super(DIFF_CALLBACK);
        this.onItemClick = onItemClick;
        this.onFavoriteClick = onFavoriteClick;
        this.onDeleteClick = onDeleteClick;
    }

    private static final DiffUtil.ItemCallback<QRCodeEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<QRCodeEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull QRCodeEntity oldItem,
                                               @NonNull QRCodeEntity newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull QRCodeEntity oldItem,
                                                  @NonNull QRCodeEntity newItem) {
                    return oldItem.getContent().equals(newItem.getContent())
                            && oldItem.isFavorite() == newItem.isFavorite();
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistoryBinding binding = ItemHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemHistoryBinding binding;

        ViewHolder(ItemHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(QRCodeEntity entity) {
            // Миниатюра QR-кода
            try {
                Bitmap thumbnail = QRGeneratorService.generate(
                        entity.getContent(), 100,
                        entity.getForegroundColor(), entity.getBackgroundColor());
                binding.imageThumbnail.setImageBitmap(thumbnail);
            } catch (WriterException e) {
                binding.imageThumbnail.setImageResource(R.drawable.ic_qr_code);
            }

            // Тип
            QRType type = QRType.fromValue(entity.getType());
            binding.textType.setText(type.getDisplayName());

            // Содержимое (обрезанное)
            String content = entity.getContent();
            if (content.length() > 60) {
                content = content.substring(0, 60) + "...";
            }
            binding.textContent.setText(content);

            // Дата
            binding.textDate.setText(dateFormat.format(new Date(entity.getCreatedAt())));

            // Источник
            QRSource source = QRSource.fromValue(entity.getSource());
            binding.textSource.setText(
                    source == QRSource.GENERATED ? "Создан" : "Отсканирован");

            // Избранное
            binding.buttonFavorite.setIconResource(
                    entity.isFavorite() ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);

            // Обработчики
            itemView.setOnClickListener(v -> onItemClick.onClick(entity));
            binding.buttonFavorite.setOnClickListener(v -> onFavoriteClick.onFavoriteClick(entity));

            // Свайп или длительное нажатие для удаления
            itemView.setOnLongClickListener(v -> {
                onDeleteClick.onDeleteClick(entity);
                return true;
            });
        }
    }
}