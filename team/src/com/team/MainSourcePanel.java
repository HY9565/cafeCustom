package com.team;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainSourcePanel extends JPanel {

	CustomMenu customMenu;
	Connection conn;
	JButton sourceButton;

	int clickedSourceNum, clickedSourcePrice;
	
	int price1;
		
	public MainSourcePanel(DBConn conn, CustomMenu customMenu) {
		this.conn=conn.getConn();
		this.customMenu=customMenu;
		
		this.setLayout(new GridBagLayout());	
		
		addSource();
		
	}
	
	private void addSource() {
		
        String sql = "SELECT * FROM CUSTOM_SOURCE WHERE SOURCE_NUM >= 1 AND SOURCE_NUM <= 5";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            int index = 0;
            while (rs.next()) {
                String sourceName = rs.getString("SOURCE_NAME");
                int sourceNum = rs.getInt("SOURCE_NUM");
                int sourcePrice = rs.getInt("SOURCE_PRICE");
                addSourceButton(sourceName + ":" + sourcePrice + "  ", index++, sourceNum, sourceName, sourcePrice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	 
	 private void addSourceButton(String text, int index, int sourceNum, String sourceName, int sourcePrice) {
    
        ImageIcon icon = null;
        
        if(sourceNum==1) {
            icon = new ImageIcon("./image/bean.jpg");       
        } else if (sourceNum==2) {
            icon = new ImageIcon("./image/tea.jpg");      
        } else if (sourceNum==3) {
            icon = new ImageIcon("./image/green_tea.jpg");      
        } else if(sourceNum==4) {
            icon = new ImageIcon("./image/cacao.jpg");      
        } else if(sourceNum==5) {
            icon = new ImageIcon("./image/milk.jpg");     
        }
//        else {
//        	icon = new ImageIcon("./image/default.png");
//        }
        
        //  ̹    ũ       
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        int button_size=(int) ((screenSize.width * 0.417)/5.72);
        
        Image image = icon.getImage();       
        Image scaledImage = image.getScaledInstance(button_size, button_size, Image.SCALE_SMOOTH);    
        ImageIcon scaledIcon = new ImageIcon(scaledImage);     

        JButton sourceButton = new JButton(scaledIcon);      
        sourceButton.setBorderPainted(false);
        sourceButton.setText("<html>" + text.replace(":", " ") + "</html>"); 
        sourceButton.setHorizontalTextPosition(SwingConstants.CENTER);    
        sourceButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        sourceButton.setMargin(new Insets(0, 0, 35, 0)); 
        
        GridBagConstraints gbc=new GridBagConstraints();
        
                
        sourceButton.setPreferredSize(new Dimension(button_size, button_size));
        sourceButton.setSize(button_size, button_size);
        
        gbc.gridx = index % 5;     
        gbc.gridy = index / 5;   
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx=1.0;
        
       
        sourceButton.putClientProperty("SOURCE_NUM", sourceNum); 
        sourceButton.putClientProperty("SOURCE_NAME", sourceName);
        sourceButton.putClientProperty("SOURCE_PRICE", sourcePrice);

        sourceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String clickedSourceName = (String)sourceButton.getClientProperty("SOURCE_NAME");
                clickedSourcePrice = (int)sourceButton.getClientProperty("SOURCE_PRICE");
                clickedSourceNum = (int)sourceButton.getClientProperty("SOURCE_NUM");

                                
                customMenu.lb_select_sourceImage1.setIcon(scaledIcon);
                customMenu.lb_select_sourceImage1.setHorizontalAlignment(SwingConstants.CENTER);
                customMenu.lb_select_sourceImage1.setVerticalAlignment(SwingConstants.CENTER);   
                
                customMenu.lb_select_sourceName1.setText(clickedSourceName);
                customMenu.lb_select_sourceName1.setHorizontalAlignment(JLabel.RIGHT);

                customMenu.lb_select_sourcePrice1.setText(""+clickedSourcePrice);
                customMenu.lb_select_sourcePrice1.setHorizontalAlignment(JLabel.LEFT);
                
                price1=Integer.parseInt(customMenu.lb_select_sourcePrice1.getText());
            }
        });

        this.add(sourceButton,gbc);
    }
}

