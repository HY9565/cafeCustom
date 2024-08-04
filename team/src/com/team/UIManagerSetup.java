package com.team;

import java.awt.Color;
import java.awt.Font;
import java.io.File;

import javax.swing.UIManager;

public class UIManagerSetup {
	public static void setup() {
		try {
			// Look and Feel 설정
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			// 폰트 설정
			String fontPath = "./fonts/GmarketSansTTFMedium.ttf"; // 경로를 실제 폰트 파일 경로로 수정하세요
			Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath)).deriveFont(Font.PLAIN, 14);

			// 폰트
			UIManager.put("Button.font", customFont);
			UIManager.put("Label.font", customFont);
			UIManager.put("TextField.font", customFont);
			UIManager.put("ComboBox.font", customFont);
			UIManager.put("Table.font", customFont);
			UIManager.put("TableHeader.font", customFont);
			UIManager.put("Menu.font", customFont);
			UIManager.put("MenuItem.font", customFont);

			// 스타일
			UIManager.put("Button.background", Color.WHITE);
			UIManager.put("Label.foreground", Color.DARK_GRAY);
			UIManager.put("TextField.background", Color.WHITE);
			UIManager.put("TextField.foreground", Color.BLACK);
			UIManager.put("ComboBox.background", Color.WHITE);
			UIManager.put("ComboBox.foreground", Color.BLACK);
			UIManager.put("Table.background", Color.WHITE);
			UIManager.put("Table.foreground", Color.BLACK);
			UIManager.put("Table.selectionBackground", Color.LIGHT_GRAY);
			UIManager.put("Table.selectionForeground", Color.BLACK);
			UIManager.put("Menu.background", Color.red);
			UIManager.put("Menu.foreground", Color.BLACK);
			UIManager.put("MenuItem.background", Color.LIGHT_GRAY);
			UIManager.put("MenuItem.foreground", Color.BLACK);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}