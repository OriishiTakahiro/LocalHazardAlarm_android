package com.example.takahiro.localhazardmap_01.entity;

import android.util.Log;

import java.nio.charset.Charset;
import java.util.Comparator;

/**
 * Created by takahiro on 西暦15/09/30.
 */
public class WarningInfo {

    public String title;
    public String description;
    public String organization;
    public int risk_level;
    public byte[] img;

    public WarningInfo(String title, String description, String organization, int risk_level, String string_img) {
        this.title = title;
        this.description = description;
        this.organization = organization;
        this.risk_level = risk_level;
        if(string_img != null) this.img = string_img.getBytes(Charset.forName("ISO-8859-1"));
    }

    public static class WarningComparatorDecOrderByRisk implements Comparator<WarningInfo> {
        @Override
        public int compare(WarningInfo war_info_1, WarningInfo war_info_2) {
            return war_info_1.risk_level > war_info_2.risk_level ? -1 : 1;
        }
    }

}
