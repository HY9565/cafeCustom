package com.team;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

public class OrderMenu extends JPanel implements ActionListener {
    // 여러 패널과 버튼, 라벨을 선언
    JPanel panel_top, panel_middle, panel_bottom, panel_category, panel_cmenu, panel_cart, panel_cartTotal,
            bt_top_button_panel;
    JButton bt_top_goback, bt_cg_cmenu, bt_cg_tmenu, bt_cg_amenu, bt_cg_bmenu, bt_cg_kmenu, cartTotal_reset,
            cartTotal_pay, bottomlist_paybt, bottomlist_rtbt;
    JLabel orderlist, cartTotal_name, cartTotal_price, cartTotal_number, cartTotal_number_field, card_ing;

    DBConn conn;

    CardLayout cardLayout;
    JPanel cardPanel;
    CardLayout dialogCardLayout;
    JPanel dialogCardPanel;
    JDialog newdia;

    private int cartIndex = 0; // 카트 항목 인덱스
    private int totalQuantity = 0; // 총 수량 변수 추가

    // 주문 항목을 저장하는 리스트
    private List<OrderItem> orderList = new ArrayList<>();

    CoffeeMenu cm;
    AdeMenu am;
    TeaMenu tm;
    BlendMenu bm;
    CustomMenu km; // 추가된 부분

    int takeout_takein;
    
    //주문번호화면
    Label lb_orderNum_kor,lb_orderNum,lb_order_info;
    JPanel paymentMethodPanel;
    CardLayout main_cardLayout;
    JPanel main_cardPanel;

    public OrderMenu(DBConn conn, int takeout_takein, CardLayout cardLayout2, JPanel cardPanel2) {

        this.takeout_takein = takeout_takein;
        this.conn = conn;
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        main_cardLayout=cardLayout2;
        main_cardPanel=cardPanel2;

        // OrderMenu의 패널의 레이아웃을 BorderLayout 으로 설정
        this.setLayout(new BorderLayout());

        // top, middle, bottom 패널을 생성하고 OrderMenu 패널에 추가
        panel_top = new JPanel(new BorderLayout());
        panel_top.setBackground(Color.WHITE);
        panel_middle = new JPanel(new BorderLayout());
        panel_bottom = new JPanel(new BorderLayout());
        this.add(panel_top, BorderLayout.NORTH);
        this.add(panel_middle, BorderLayout.CENTER);
        this.add(panel_bottom, BorderLayout.SOUTH);

        // 상단 버튼을 생성하고 아이콘 설정
        bt_top_goback = new JButton();
        bt_top_goback.setBorderPainted(false);
        // 이미지 아이콘의 크기를 가져와서 버튼 크기 설정
        ImageIcon icon = new ImageIcon("./image/home_icon.png");
        bt_top_goback.setIcon(icon); // 이미지를 아이콘으로 받는다.
        bt_top_goback.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight())); // 크기를 설정
        bt_top_button_panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bt_top_button_panel.add(bt_top_goback);

        // 상단 버튼 패널을 상단 패널에 추가
        panel_top.add(bt_top_button_panel, BorderLayout.WEST);

        // 중간 패널의 상단에 카테고리 버튼을 추가
        panel_category = new JPanel(new GridLayout(1, 5));
        panel_middle.add(panel_category, BorderLayout.NORTH);
        bt_cg_cmenu = new JButton("커피");
        bt_cg_tmenu = new JButton("티");
        bt_cg_amenu = new JButton("에이드");
        bt_cg_bmenu = new JButton("블렌디드");
        bt_cg_kmenu = new JButton("커스텀");
        panel_category.add(bt_cg_cmenu);
        panel_category.add(bt_cg_tmenu);
        panel_category.add(bt_cg_amenu);
        panel_category.add(bt_cg_bmenu);
        panel_category.add(bt_cg_kmenu);

        // ActionListener 등록
        bt_cg_cmenu.addActionListener(this);
        bt_cg_tmenu.addActionListener(this);
        bt_cg_amenu.addActionListener(this);
        bt_cg_bmenu.addActionListener(this);
        bt_cg_kmenu.addActionListener(this);

        // middle( Center cardPanel )
        cm = new CoffeeMenu(conn, this);
        tm = new TeaMenu(conn, this);
        am = new AdeMenu(conn, this);
        bm = new BlendMenu(conn, this);
        km = new CustomMenu(conn, this); // 추가된 부분

        cardPanel.add(new JScrollPane(cm), "커피");
        cardPanel.add(new JScrollPane(tm), "티");
        cardPanel.add(new JScrollPane(am), "에이드");
        cardPanel.add(new JScrollPane(bm), "블렌디드");
        cardPanel.add(km, "커스텀"); // 추가된 부분

        panel_middle.add(cardPanel, BorderLayout.CENTER);

        // bottom( West Label )
        // 하단 패널의 좌측에 주문 목록 레이블을 추가
        orderlist = new JLabel("주문목록");
        orderlist.setHorizontalAlignment(SwingConstants.CENTER);
        orderlist.setPreferredSize(new Dimension(100, 120));
        panel_bottom.add(orderlist, BorderLayout.WEST);

        // bottom( Center grid orderInfoScroll )
        // 하단 패널의 중앙에 카트 패널을 추가
        panel_cart = new JPanel(new GridBagLayout());
        JScrollPane scroll_cart = new JScrollPane(panel_cart);
        scroll_cart.setPreferredSize(new Dimension(0, 100));
        panel_bottom.add(scroll_cart, BorderLayout.CENTER);

        // bottom( South Total )
        // 하단 패널의 하단에 총합패널을 추가
        panel_cartTotal = new JPanel(new GridLayout(1, 6));
        panel_cartTotal.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
        panel_cartTotal.setPreferredSize(new Dimension(panel_cartTotal.getPreferredSize().width, 50));
        panel_bottom.add(panel_cartTotal, BorderLayout.SOUTH);
        cartTotal_name = new JLabel("Total");
        cartTotal_price = new JLabel("0");
        cartTotal_number = new JLabel("수량");
        cartTotal_number_field = new JLabel("0");
        cartTotal_reset = new JButton("전체삭제");
        cartTotal_pay = new JButton("결제");
        panel_cartTotal.add(cartTotal_name);
        panel_cartTotal.add(cartTotal_price);
        panel_cartTotal.add(cartTotal_number);
        panel_cartTotal.add(cartTotal_number_field);
        panel_cartTotal.add(cartTotal_reset);
        panel_cartTotal.add(cartTotal_pay);
        cartTotal_pay.addActionListener(this);

        // Initialize bottomlist_paybt and bottomlist_rtbt
        bottomlist_paybt = new JButton("카드");
        bottomlist_rtbt = new JButton("현금");

        bottomlist_paybt.addActionListener(this);
        bottomlist_rtbt.addActionListener(this);

    }

    // 카트에 메뉴 추가
    public void addCart(String menuName, int menuPrice, int menuNum) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 2, 2, 2); // 여백 설정
        gbc.weightx = 1.0; // 가로 방향으로 동일한 크기
        gbc.anchor = GridBagConstraints.NORTH; // 상단에 정렬

        // 메뉴 이름, 가격, 수량 등을 라벨과 버튼으로 생성하여 카트 패널에 추가
        // 메뉴 이름을 표시하는 JLabel 생성
        JLabel cart1_menu_name = new JLabel(menuName);
        gbc.gridx = 0; // GridBagConstraints 설정: 0번째 열에 배치
        gbc.gridy = cartIndex; // GridBagConstraints 설정: 현재 카트 인덱스에 해당하는 행에 배치

        panel_cart.add(cart1_menu_name, gbc); // JLabel을 panel_cart에 추가
        cart1_menu_name.putClientProperty("cart_name", menuName);

        // 메뉴 가격을 표시하는 Jlabel 생성
        JLabel cart1_menu_price = new JLabel(String.valueOf(menuPrice));
        gbc.gridx = 1; // 설정: 1번째 열에 배치
        panel_cart.add(cart1_menu_price, gbc); // JLabel을 panel_cart에 추가

        // 주문 항목을 리스트에 추가
        OrderItem item = new OrderItem(menuName, menuPrice, 1, menuNum);
        orderList.add(item);

        // JLabel cart1_menu_number = new JLabel("1");
        // gbc.gridx = 2;
        // panel_cart.add(cart1_menu_number, gbc);

        JButton cart1_plus = new JButton("+");
        cart1_plus.putClientProperty("cart_plus", cartIndex + 1);

        gbc.gridx = 3;
        panel_cart.add(cart1_plus, gbc);

        JTextField cart1_number_field = new JTextField("1");
        gbc.gridx = 4;
        panel_cart.add(cart1_number_field, gbc);

        JButton cart1_minus = new JButton("-");
        cart1_minus.putClientProperty("cart_minus", cartIndex + 1);
        gbc.gridx = 5;
        panel_cart.add(cart1_minus, gbc);

        JButton cart1_delete = new JButton("삭제");
        gbc.gridx = 6;
        panel_cart.add(cart1_delete, gbc);

        cartIndex++; // 카트 항목의 인덱스를 나타내며 각 항목을 추가할 때 마다 증가한다.

        // 총 가격 및 수량 업데이트
        int currentTotalPrice = Integer.parseInt(cartTotal_price.getText()) + menuPrice;
        cartTotal_price.setText(String.valueOf(currentTotalPrice));
        int currentTotalNumber = Integer.parseInt(cartTotal_number_field.getText()) + 1;
        cartTotal_number_field.setText(String.valueOf(currentTotalNumber));

        // 총 수량 업데이트
        totalQuantity += 1;

        // 패널 업데이트
        panel_cart.revalidate();
        panel_cart.repaint();

        // 액션 리스너를 버튼에 추가
        cart1_plus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int num = Integer.parseInt(cart1_number_field.getText()) + 1;
                cart1_number_field.setText(String.valueOf(num));

                // +버튼 누를 시 토탈수량,금액 변경
                cartTotal_number_field.setText(String.valueOf(Integer.parseInt(cartTotal_number_field.getText()) + 1));
                cartTotal_price.setText(String.valueOf(Integer.parseInt(cartTotal_price.getText()) + menuPrice));
                totalQuantity += 1; // 총 수량 증가

                for (OrderItem orderitem : orderList) {
                    if (orderitem.getName().equals(cart1_menu_name.getClientProperty("cart_name"))) {
                        orderitem.setQuantity(orderitem.getQuantity() + 1);
                    }
                }
            }
        });

        // 액션 리스너를 버튼에 추가
        cart1_minus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int num = Integer.parseInt(cart1_number_field.getText()) - 1;
                if (num <= 0) {
                    num = 1;
                    return;
                }
                cart1_number_field.setText(String.valueOf(num));

                // -버튼 누를 시 토탈수량 변경
                cartTotal_number_field.setText(String.valueOf(Integer.parseInt(cartTotal_number_field.getText()) - 1));
                cartTotal_price.setText(String.valueOf(Integer.parseInt(cartTotal_price.getText()) - menuPrice));
                totalQuantity -= 1; // 총 수량 감소

                for (OrderItem orderitem : orderList) {
                    if (orderitem.getName().equals(cart1_menu_name.getClientProperty("cart_name"))) {
                        orderitem.setQuantity(orderitem.getQuantity() - 1);
                    }
                }
            }
        });

        // 삭제
        cart1_delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 현재 수량과 총 가격을 가져옵니다.
                int quantity = Integer.parseInt(cart1_number_field.getText());
                int price = menuPrice * quantity;

                // 총 가격과 수량에서 해당 항목을 제거합니다.
                cartTotal_price.setText(String.valueOf(Integer.parseInt(cartTotal_price.getText()) - price));
                cartTotal_number_field.setText(String.valueOf(Integer.parseInt(cartTotal_number_field.getText()) - quantity));

                // 카트에서 항목을 제거합니다.
                panel_cart.remove(cart1_menu_name);
                panel_cart.remove(cart1_menu_price);
                panel_cart.remove(cart1_number_field);
                panel_cart.remove(cart1_plus);
                panel_cart.remove(cart1_minus);
                panel_cart.remove(cart1_delete);

                // 삭제버튼을 누르면 버튼 다시 활성화
                cm.enableButton(cart1_menu_name.getText());
                am.enableButton(cart1_menu_name.getText());
                tm.enableButton(cart1_menu_name.getText());
                bm.enableButton(cart1_menu_name.getText());

                // 패널을 다시 그립니다.
                panel_cart.revalidate();
                panel_cart.repaint();

                // list에서 삭제
                Iterator<OrderItem> iterator = orderList.iterator();
                while (iterator.hasNext()) {
                    OrderItem orderitem = iterator.next();
                    if (orderitem.getName().equals(cart1_menu_name.getClientProperty("cart_name"))) {
                        iterator.remove();
                    }
                }
            }
        });

        // 전체삭제버튼
        cartTotal_reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel_cart.removeAll();
                panel_cart.revalidate();
                panel_cart.repaint();

                // 총 가격 및 수량 초기화
                cartTotal_price.setText("0");
                cartTotal_number_field.setText("0");
                totalQuantity = 0; // 총 수량 초기화

                cm.enableAllButtons();
                am.enableAllButtons();
                tm.enableAllButtons();
                bm.enableAllButtons();

                // orderList의 모든 항목을 제거
                orderList.clear();
            }
        });

        //홈버튼 
        bt_top_goback.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel_cart.removeAll();
                panel_cart.revalidate();
                panel_cart.repaint();

                // 총 가격 및 수량 초기화
                cartTotal_price.setText("0");
                cartTotal_number_field.setText("0");
                totalQuantity = 0; // 총 수량 초기화

                cm.enableAllButtons();
                am.enableAllButtons();
                tm.enableAllButtons();
                bm.enableAllButtons();

                orderList.clear();
                
                km.lb_select_sourceImage1.setIcon(null);
                km.lb_select_sourceImage2.setIcon(null);
                km.lb_select_sourceImage3.setIcon(null);
                km.lb_select_sourceName1.setText("");
                km.lb_select_sourceName2.setText("");
                km.lb_select_sourceName3.setText("");
                km.lb_select_sourcePrice1.setText("");
                km.lb_select_sourcePrice2.setText("");
                km.lb_select_sourcePrice3.setText("");
                km.sourceNum1=0;
                km.sourceNum2=0;
                km.sourceNum3=0;
            }
        });

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();

        if (obj == bt_cg_cmenu) {
            cardLayout.show(cardPanel, "커피");
        } else if (obj == bt_cg_tmenu) {
            cardLayout.show(cardPanel, "티");
        } else if (obj == bt_cg_amenu) {
            cardLayout.show(cardPanel, "에이드");
        } else if (obj == bt_cg_bmenu) {
            cardLayout.show(cardPanel, "블렌디드");
        } else if (obj == bt_cg_kmenu) { // 추가된 부분
            cardLayout.show(cardPanel, "커스텀");
        } else if (obj == cartTotal_pay) {
        	
        	if(km.lb_select_sourceImage1.getIcon() != null && km.lb_select_sourceImage2.getIcon() != null && km.lb_select_sourceImage2.getIcon() != null) {
        		km.warningDialog("커스텀메뉴 재료창을 초기화해주세요");
        		return;
        	} else {
        		//선택했던 커스텀 재료창 초기화
    			km.lb_select_sourceName1.setText("");
    			km.lb_select_sourceName2.setText("");
    			km.lb_select_sourceName3.setText("");   
    			km.lb_select_sourcePrice1.setText("");
    			km.lb_select_sourcePrice2.setText("");
    			km.lb_select_sourcePrice3.setText("");
    			//선택했던 재료번호 초기화
    			km.sourceNum1=0;
    			km.sourceNum2=0;
    			km.sourceNum3=0;     
    			km.lb_select_sourceImage1.setIcon(null);
    			km.lb_select_sourceImage2.setIcon(null);
    			km.lb_select_sourceImage3.setIcon(null);
        	}
        	
        	
        	if (Integer.parseInt(cartTotal_number_field.getText()) != 0) {
        		
                // 결제 버튼을 눌렀을 때 다이얼로그를 띄우는 코드
                newdia = new JDialog(SwingUtilities.getWindowAncestor(OrderMenu.this),
                        "결제", Dialog.ModalityType.APPLICATION_MODAL);
                newdia.setSize(300, 400);
                newdia.setLayout(new BorderLayout());

                //카드레이아웃
                dialogCardLayout = new CardLayout();
                dialogCardPanel = new JPanel(dialogCardLayout);

                JPanel orderListPanel = new JPanel(new BorderLayout());
                paymentMethodPanel = new JPanel();

                JLabel top_list = new JLabel("주 문 목 록");
                top_list.setHorizontalAlignment(SwingConstants.CENTER);
                orderListPanel.add(top_list, BorderLayout.NORTH);

                JPanel middle_list = new JPanel();
                orderListPanel.add(middle_list, BorderLayout.CENTER);
                middle_list.setLayout(new GridLayout(orderList.size() + 1, 1)); // 동적 행 수

                // 주문 항목을 다이얼로그에 추가
                for (OrderItem item : orderList) {
                    JPanel itemPanel = new JPanel();
                    itemPanel.setLayout(new GridLayout(1, 3));

                    JLabel nameLabel = new JLabel(item.name);
                    nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    itemPanel.add(nameLabel);

                    JLabel priceLabel = new JLabel(String.valueOf(item.price));
                    priceLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    itemPanel.add(priceLabel);

                    JLabel quantityLabel = new JLabel(String.valueOf(item.quantity));
                    quantityLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    itemPanel.add(quantityLabel);

                    middle_list.add(itemPanel);
                }

                // 총 금액 패널 추가
                JPanel totalPanel = new JPanel();
                totalPanel.setLayout(new GridLayout(1, 2));

                JLabel totalLabel = new JLabel("총 금액");
                totalLabel.setHorizontalAlignment(SwingConstants.CENTER);
                totalPanel.add(totalLabel);

                JLabel totalPriceLabel = new JLabel(cartTotal_price.getText());
                totalPriceLabel.setHorizontalAlignment(SwingConstants.CENTER);
                totalPanel.add(totalPriceLabel);

                middle_list.add(totalPanel);

                // 하단 패널 및 버튼 추가
                JPanel bottom_list = new JPanel();
                orderListPanel.add(bottom_list, BorderLayout.SOUTH);
                bottom_list.setLayout(new GridLayout(1, 2));

                bottom_list.add(bottomlist_paybt);
                bottom_list.add(bottomlist_rtbt);

                //결제내역화면
                dialogCardPanel.add(orderListPanel, "OrderList");
                //결제후화면
                dialogCardPanel.add(paymentMethodPanel, "PaymentMethod");

                newdia.add(dialogCardPanel);
                newdia.setLocationRelativeTo(null); // 화면 중앙에 프레임 위치
                newdia.setVisible(true);

            } else {
                JOptionPane.showMessageDialog(null, "1개 이상 주문해주세요");
            }
        } else if (obj == bottomlist_paybt) {
            JDialog cardDialog = new JDialog(SwingUtilities.getWindowAncestor(OrderMenu.this),
                    "카드 결제", Dialog.ModalityType.APPLICATION_MODAL);
            cardDialog.setSize(300, 200);
            cardDialog.setLayout(new BorderLayout());

            card_ing = new JLabel("카드를 넣어주세요", JLabel.CENTER);
            cardDialog.add(card_ing, BorderLayout.CENTER);

            cardDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // 닫기 버튼 동작 설정
            
            
            try {
                String sql_receipt_insert = "insert into RECEIPT (rec_num, rec_time, rec_card, rec_total, rec_takeout) "
                		+ "values(seq_rec_num.nextval,sysdate,1,?,?)";
                 PreparedStatement pstmt = conn.getConn().prepareStatement(sql_receipt_insert, new String[]{"rec_num"});
                 pstmt.setInt(1, Integer.parseInt(cartTotal_price.getText()));
                 pstmt.setInt(2, takeout_takein);

                 int i = pstmt.executeUpdate();
                 int seq_rec_num=0;
                 
                 if (i >= 1) {
                     ResultSet rs = pstmt.getGeneratedKeys();
                     if (rs.next()) {
                         seq_rec_num = rs.getInt(1);
                         //System.out.println("seq_rec_num ="+seq_rec_num);
                     }
                     for (OrderItem item : orderList) {
                        String sql_detail_insert="";
                         
                         if(item.getNum()>1000) {
                            sql_detail_insert ="insert into RECEIPT_DETAIL values(seq_de_num.nextval,seq_rec_num.currval,?,null,?)";
                         }else if(item.getNum()>=100 && item.getNum()<1000) {
                            sql_detail_insert ="insert into RECEIPT_DETAIL values(seq_de_num.nextval,seq_rec_num.currval,null,?,?)";
                         }
                         pstmt = conn.getConn().prepareStatement(sql_detail_insert);
                         pstmt.setInt(1, item.getNum());
                         pstmt.setInt(2, item.getQuantity());
                         pstmt.executeUpdate();
                      }
                   }

                 Font f = new Font("SansSerif", Font.BOLD,24);
                 lb_orderNum_kor = new Label("주문번호",Label.CENTER);
                 lb_orderNum_kor.setFont(f);
                 lb_orderNum = new Label(String.valueOf(seq_rec_num),Label.CENTER);
                 lb_orderNum.setFont(f);
                 
                 lb_order_info = new Label("주문용지와 카드를 챙겨주세요",Label.CENTER);
                 Font f1 = new Font("SansSerif", Font.BOLD,16);
                 lb_order_info.setFont(f1);
                 
                 BorderLayout final_bl = new BorderLayout();
                 paymentMethodPanel.setLayout(final_bl);
                 
        
                 JPanel pn_center_ordernum = new JPanel(new BorderLayout());
                 pn_center_ordernum.add(lb_order_info,BorderLayout.SOUTH);
                 pn_center_ordernum.add(lb_orderNum,BorderLayout.CENTER);

                 // 구성 요소 추가
                 paymentMethodPanel.add(lb_orderNum_kor, BorderLayout.NORTH);
                 paymentMethodPanel.add(pn_center_ordernum, BorderLayout.CENTER);
                 //paymentMethodPanel.add(bt_go_back_panel, BorderLayout.SOUTH);
                 
                 newdia.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // 닫기 버튼 동작 설정
                 
                 dialogCardLayout.show(dialogCardPanel, "PaymentMethod");
                 
                 //카트삭제
                 panel_cart.removeAll();
                 panel_cart.revalidate();
                 panel_cart.repaint();

                 // 총 가격 및 수량 초기화
                 cartTotal_price.setText("0");
                 cartTotal_number_field.setText("0");
                 totalQuantity = 0; // 총 수량 초기화

                 cm.enableAllButtons();
                 am.enableAllButtons();
                 tm.enableAllButtons();
                 bm.enableAllButtons();

                 orderList.clear();
                 
                 // 닫기 버튼을 누를 때의 동작 설정
                 newdia.addWindowListener(new WindowAdapter() {
                     @Override
                     public void windowClosing(WindowEvent e) {
                         main_cardLayout.show(main_cardPanel, "main");
                         newdia.dispose(); // 다이얼로그 닫기
                         
                     }
                 });

             } catch (SQLException e1) {
                 // TODO Auto-generated catch block
                 e1.printStackTrace();
             }
            
            

            // 타이머를 사용하여 일정 시간 후에 다이얼로그 닫기
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    cardDialog.setVisible(false);
                    cardDialog.dispose(); // 다이얼로그 자원 해제
                    

                    dialogCardLayout.show(dialogCardPanel, "PaymentMethod");
                }
            }, 3000); // 3000 밀리초 = 3초

            cardDialog.setLocationRelativeTo(null); // 화면 중앙에 프레임 위치
            cardDialog.setVisible(true);

            // System.out.print("카드결제");

            

            //현금
        } else if (obj == bottomlist_rtbt) {
            JOptionPane.showConfirmDialog(null, "현금 결제를 선택했습니다.");
            // System.out.print("현금결제");

            try {
               String sql_receipt_insert = "insert into RECEIPT (rec_num, rec_time, rec_card, rec_total, rec_takeout) "
               		+ "values(seq_rec_num.nextval,sysdate,0,?,?)";
                PreparedStatement pstmt = conn.getConn().prepareStatement(sql_receipt_insert, new String[]{"rec_num"});
                pstmt.setInt(1, Integer.parseInt(cartTotal_price.getText()));
                pstmt.setInt(2, takeout_takein);

                int i = pstmt.executeUpdate();

                int seq_rec_num = 0;
                if (i >= 1) {
                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        seq_rec_num = rs.getInt(1);
                        //System.out.println("seq_rec_num ="+seq_rec_num);
                    }
                    for (OrderItem item : orderList) {
                       String sql_detail_insert="";
                        
                        if(item.getNum()>1000) {
                           sql_detail_insert ="insert into RECEIPT_DETAIL values(seq_de_num.nextval,seq_rec_num.currval,?,null,?)";
                        }else if(item.getNum()>=100 && item.getNum()<1000) {
                           sql_detail_insert ="insert into RECEIPT_DETAIL values(seq_de_num.nextval,seq_rec_num.currval,null,?,?)";
                        }
                        
                        pstmt = conn.getConn().prepareStatement(sql_detail_insert);
                        pstmt.setInt(1, item.getNum());
                        pstmt.setInt(2, item.getQuantity());
                        pstmt.executeUpdate();

                    }
                }
                Font f = new Font("SansSerif", Font.BOLD,24);
                lb_orderNum_kor = new Label("주문번호",Label.CENTER);
                lb_orderNum_kor.setFont(f);
                lb_orderNum = new Label(String.valueOf(seq_rec_num),Label.CENTER);
                lb_orderNum.setFont(f);
                
                lb_order_info = new Label("카운터로 오셔서 결제를 마쳐주세요",Label.CENTER);
                Font f1 = new Font("SansSerif", Font.BOLD,16);
                lb_order_info.setFont(f1);
                
                BorderLayout final_bl = new BorderLayout();
                paymentMethodPanel.setLayout(final_bl);
                
                
                JPanel pn_center_ordernum = new JPanel(new BorderLayout());
                pn_center_ordernum.add(lb_order_info,BorderLayout.SOUTH);
                pn_center_ordernum.add(lb_orderNum,BorderLayout.CENTER);

                // 구성 요소 추가
                paymentMethodPanel.add(lb_orderNum_kor, BorderLayout.NORTH);
                paymentMethodPanel.add(pn_center_ordernum, BorderLayout.CENTER);
                //paymentMethodPanel.add(bt_go_back_panel, BorderLayout.SOUTH);
                
                newdia.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // 닫기 버튼 동작 설정
                
                panel_cart.removeAll();
                panel_cart.revalidate();
                panel_cart.repaint();

                // 총 가격 및 수량 초기화
                cartTotal_price.setText("0");
                cartTotal_number_field.setText("0");
                totalQuantity = 0; // 총 수량 초기화

                cm.enableAllButtons();
                am.enableAllButtons();
                tm.enableAllButtons();
                bm.enableAllButtons();

                orderList.clear();
                
                dialogCardLayout.show(dialogCardPanel, "PaymentMethod");
                
                // 닫기 버튼을 누를 때의 동작 설정
                newdia.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        main_cardLayout.show(main_cardPanel, "main");
                        newdia.dispose(); // 다이얼로그 닫기
                        
                    }
                });

            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }
}
