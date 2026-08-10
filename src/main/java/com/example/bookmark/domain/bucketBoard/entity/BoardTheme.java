package com.example.bookmark.domain.bucketBoard.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "board_theme")
public class BoardTheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_theme_id")
    private Long boardThemeId;

    @Column(name = "theme_name", nullable = false, length = 30)
    private String themeName;

    @Column(name = "theme_image_url", nullable = false)
    private String themeImageUrl;

//    @OneToMany(mappedBy = "boardTheme")
//    private List<MemoDesign> memoDesigns = new ArrayList<>();

}
