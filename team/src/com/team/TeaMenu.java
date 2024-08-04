package com.team;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class TeaMenu extends JPanel{

   JScrollPane scroll;
   Connection conn;
   OrderMenu orderMenu; 
    /* OrderMenu 참조 , 주문목록을 관리하는 클래스
       나중에 메뉴버튼을 클릭할 때 주문 목록을 업데이트 하는데 사용 */
   
   HashMap<String,JButton> jButtons ;
   
   public TeaMenu(DBConn conn,OrderMenu orderMenu) {
      
      this.conn = conn.getConn();
      this.orderMenu = orderMenu; 
      this.setLayout(new GridBagLayout());
      this.setBackground(Color.WHITE);
      scroll = new JScrollPane(this);
      
      jButtons = new  HashMap<>();
      
      
      addMenu();
      
   }
   
    private void addMenu() {
        String sql = "SELECT * FROM MENU WHERE MENU_NUM > 2000 AND MENU_NUM < 3000";
        try {
           PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            int index = 0;
            while (rs.next()) {
                String menuName = rs.getString("MENU_NAME");
                int menuPrice = rs.getInt("MENU_PRICE");
                int menuNum = rs.getInt("MENU_NUM");
                addMenuButton(menuName + ":" + menuPrice + "원", index++, menuNum, menuName, menuPrice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
   
   // 메뉴 버튼 추가
    private void addMenuButton(String text, int index, int menuNum, String menuName, int menuPrice) {
        
       // 이미지 파일 경로 설정 (프로젝트 폴더 내의 경로)
        ImageIcon icon = null;
        if(menuName.equals("민트티 HOT")) {
            icon = new ImageIcon("./image/minttea_hot.png"); // 이미지 파일 경로를 설정하세요.
        } else if (menuName.equals("민트티 ICE")) {
            icon = new ImageIcon("./image/minttea_ice.png"); // 다른 메뉴에 대한 기본 이미지 설정
        } else if (menuName.equals("히비스커스 HOT")) {
            icon = new ImageIcon("./image/Hibiscus_hot.png"); // 다른 메뉴에 대한 기본 이미지 설정
        }  else if (menuName.equals("히비스커스 ICE")) {
            icon = new ImageIcon("./image/Hibiscus_ice.png"); // 다른 메뉴에 대한 기본 이미지 설정
        } else if (menuName.equals("유자차 HOT")) {
            icon = new ImageIcon("./image/ujatea_hot.png"); // 다른 메뉴에 대한 기본 이미지 설정
        } else if (menuName.equals("녹차 HOT")) {
            icon = new ImageIcon("./image/greentea_hot.png"); // 다른 메뉴에 대한 기본 이미지 설정
        } else if (menuName.equals("녹차 ICE")) {
            icon = new ImageIcon("./image/greentea_ice.png"); // 다른 메뉴에 대한 기본 이미지 설정
        } else if (menuName.equals("블랙티 HOT")) {
            icon = new ImageIcon("./image/blacktea_hot.png"); // 다른 메뉴에 대한 기본 이미지 설정
        } else if (menuName.equals("블랙티 ICE")) {
            icon = new ImageIcon("./image/blacktea_ice.png"); // 다른 메뉴에 대한 기본 이미지 설정
        } else  {
            icon = new ImageIcon("./image/default.jpg"); // 기본 이미지 설정
        } 
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        int button_size=(int) (screenSize.width * 0.417)/4;
        
        // 이미지 크기 조정
        Image image = icon.getImage(); // ImageIcon에서 Image 객체를 가져옴
        Image scaledImage = image.getScaledInstance(button_size, button_size, Image.SCALE_SMOOTH); // 이미지 크기를 버튼 크기에 맞게 조정
        ImageIcon scaledIcon = new ImageIcon(scaledImage); // 조정된 이미지를 사용하여 새로운 ImageIcon 생성

        JButton menuButton = new JButton(scaledIcon); // 조정된 이미지 아이콘을 버튼에 설정
        menuButton.setBorderPainted(false);
        menuButton.setText("<html>" + text.replace(":", "<br>") + "</html>"); // 이미지 아래에 텍스트를 추가
        menuButton.setHorizontalTextPosition(SwingConstants.CENTER); // 텍스트 위치 설정
        menuButton.setVerticalTextPosition(SwingConstants.BOTTOM); // 텍스트 위치 설정
        menuButton.setMargin(new Insets(0, 0, 50, 0)); // 여백 설정 (상단, 좌측, 하단, 우측)

        jButtons.put(menuName, menuButton);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = index % 3; // 열의 인덱스
        gbc.gridy = index / 3; // 행의 인덱스
        gbc.insets = new Insets(30, 5, 30, 5); // 버튼 간격 설정
        gbc.weightx = 1.0; // 열의 가중치
        gbc.fill = GridBagConstraints.NONE; // 버튼이 셀을 채우지 않도록 설정

        // 버튼의 크기를 정사각형으로 설정
        menuButton.setPreferredSize(new Dimension(button_size, button_size));
        
        /* 버튼에 메뉴 정보 속성을 추가 
        putClientProperty : 컴포넌트에 키-값 쌍으로 데이터 저장  */
      menuButton.putClientProperty("MENU_NUM", menuNum); 
      // menuButton 객체에 "MENU_NUM"이라는 키로 menuNum 값을 저장
      menuButton.putClientProperty("MENU_NAME", menuName);
      menuButton.putClientProperty("MENU_PRICE", menuPrice);
      menuButton.putClientProperty("MENU_NUM", menuNum);
      
      jButtons.put(menuName, menuButton);
      
      // 버튼에 액션 리스너 추가
      menuButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
              // 클릭된 버튼의 메뉴 정보를 가져옴
              String clickedMenuName = (String) menuButton.getClientProperty("MENU_NAME");
              int clickedMenuPrice = (int) menuButton.getClientProperty("MENU_PRICE");
              int clickedMenuNum = (int)menuButton.getClientProperty("MENU_NUM");
      
              menuButton.setEnabled(false);
              
              // OrderMenu의 addCart 메서드 호출하여 주문 목록에 추가
              orderMenu.addCart(clickedMenuName, clickedMenuPrice,clickedMenuNum);
          }
      });
        
        this.add(menuButton, gbc);
    }
    
    public void enableButton(String bt_name) {
       JButton button = jButtons.get(bt_name);
       
        if (button != null) {
            button.setEnabled(true);
            
        }
    }
    
    public void enableAllButtons() {
        for (JButton button : jButtons.values()) {
            button.setEnabled(true);
        }
    }
}