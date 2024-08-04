package com.team;

import java.awt.event.*;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import java.util.*;

public class CoffeeMenu extends JPanel { 
	// 메뉴버튼들을 표시, 버튼을 클릭할 때 해당 메뉴를 주문 목록에 추가하는 기능을 구현

    JScrollPane scroll;
    Connection conn; // 데이터베이스와 연결을 나타냄
    OrderMenu orderMenu; 
    /* OrderMenu 참조 , 주문목록을 관리하는 클래스
       나중에 메뉴버튼을 클릭할 때 주문 목록을 업데이트 하는데 사용 */
    
    HashMap<String,JButton> jButtons ;
    
    // CoffeeMenu 생성자: DBConn과 OrderMenu를 인자로 받아서 초기화
    public CoffeeMenu(DBConn conn, OrderMenu orderMenu) {
        this.conn = conn.getConn(); // 전달받은 DBConn 객체에서 실제 Connection 객체를 가져와 conn 필드에 저장
        this.orderMenu = orderMenu; //  전달받은 OrderMenu 객체를 orderMenu 필드에 저장
        this.setLayout(new GridBagLayout()); // GridBagLayout을 사용하여 이 패널의 레이아웃을 설정
        this.setBackground(Color.WHITE);
        scroll = new JScrollPane(this); // 이 패널을 스크롤 가능하게 하기 위해 JScrollPane으로 감쌉니다.
        
        jButtons = new  HashMap<>();
        
        addMenu(); // 메뉴를 추가하는 메서드를 호출
    }

    private void addMenu() {
        String sql = "SELECT * FROM MENU WHERE MENU_NUM > 1000 AND MENU_NUM < 2000";
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

    private void addMenuButton(String text, int index, int menuNum, String menuName, int menuPrice) {
    	
    	
    	 // 이미지 파일 경로 설정 (프로젝트 폴더 내의 경로)
        ImageIcon icon = null;
        if(menuName.equals("아메리카노 HOT")) {
            icon = new ImageIcon("./image/americano_hot.png"); // 이미지 파일 경로를 설정하세요.
        } else if (menuName.equals("아메리카노 ICE")) {
            icon = new ImageIcon("./image/americano_ice.png"); // 다른 메뉴에 대한 기본 이미지 설정
        } else if (menuName.equals("카페라떼 HOT")) {
            icon = new ImageIcon("./image/cafelette_hot.png"); // 다른 메뉴에 대한 기본 이미지 설정
        } else if (menuName.equals("카페라떼 ICE")) {
            icon = new ImageIcon("./image/cafelette_ice.png"); // 다른 메뉴에 대한 기본 이미지 설정
        } else if (menuName.equals("카푸치노 HOT")){
            icon = new ImageIcon("./image/cappuccino_hot.png"); // 기본 이미지 설정
        } else if (menuName.equals("카푸치노 ICE")) {
            icon = new ImageIcon("./image/cappuccino_ice.png"); // 기본 이미지 설정
        }  else if (menuName.equals("카페모카 HOT")){
            icon = new ImageIcon("./image/cafemoca_hot.png"); // 기본 이미지 설정
        } else if (menuName.equals("카페모카 ICE")){
            icon = new ImageIcon("./image/cafemoca_ice.png"); // 기본 이미지 설정
        } 
          else  {
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
        menuButton.setFocusPainted(false);
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
