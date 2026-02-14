package com.qrgen.app.data.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "qr_codes")
public class QRCodeEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "source")
    private String source;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "foreground_color")
    private int foregroundColor;

    @ColumnInfo(name = "background_color")
    private int backgroundColor;

    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    // Конструктор
    public QRCodeEntity() {
        this.foregroundColor = 0xFF000000; // Чёрный
        this.backgroundColor = 0xFFFFFFFF; // Белый
        this.isFavorite = false;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters
    public long getId() { return id; }
    public String getContent() { return content; }
    public String getType() { return type; }
    public String getSource() { return source; }
    public String getTitle() { return title; }
    public int getForegroundColor() { return foregroundColor; }
    public int getBackgroundColor() { return backgroundColor; }
    public boolean isFavorite() { return isFavorite; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(long id) { this.id = id; }
    public void setContent(String content) { this.content = content; }
    public void setType(String type) { this.type = type; }
    public void setSource(String source) { this.source = source; }
    public void setTitle(String title) { this.title = title; }
    public void setForegroundColor(int foregroundColor) { this.foregroundColor = foregroundColor; }
    public void setBackgroundColor(int backgroundColor) { this.backgroundColor = backgroundColor; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}