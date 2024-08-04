package com.team;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ManagerPage extends JPanel {

	JPanel topBtn;
	JButton viewOrder, manageItem, allPayment, changePwd, backButton;
	JPanel center_panel;
	CardLayout cardLayout;
	
	ManageAllMenu manageAllmenu;
	OrderHistory orderHistory;
	PaymentHistory paymentHistory;
	
	public ManagerPage(DBConn conn) {
		this.setLayout(new BorderLayout());
		
		topBtn = new JPanel(new GridLayout(1, 5));
		this.add(topBtn, BorderLayout.NORTH);
		
		viewOrder = new JButton("주문내역");
		manageItem = new JButton("상품관리");
		allPayment = new JButton("결제내역");
//		changePwd = new JButton("관리자 비밀번호 변경");
		backButton = new JButton("관리자 메뉴 종료");
		topBtn.add(viewOrder);
		topBtn.add(manageItem);
		topBtn.add(allPayment);
//		topBtn.add(changePwd);
		topBtn.add(backButton);
		
		cardLayout = new CardLayout();
		center_panel = new JPanel(cardLayout);
		this.add(center_panel, BorderLayout.CENTER);
		
        // 각 페이지를 위한 패널 생성
		manageAllmenu = new ManageAllMenu(conn);
		orderHistory = new OrderHistory(conn);
		paymentHistory = new PaymentHistory(conn);
//		ChangePassword changePassword = new ChangePassword();
        
		center_panel.add(new JScrollPane(orderHistory), "주문내역"); 
        center_panel.add(new JScrollPane(manageAllmenu), "상품관리");
        center_panel.add(new JScrollPane(paymentHistory), "결제내역");
//        center_panel.add(new JScrollPane(changePassword), "관리자 비밀번호 변경");
        
        cardLayout.show(center_panel, "주문내역");
        
        // 버튼에 ActionListener 추가
        viewOrder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(center_panel, "주문내역");
                paymentHistory.resetFieldsAndTable();
            }
        });
        
        manageItem.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		cardLayout.show(center_panel, "상품관리");
        		paymentHistory.resetFieldsAndTable();
        	}
        });
        
        allPayment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(center_panel, "결제내역");
                paymentHistory.resetFieldsAndTable();
            }
        });
        
        
    }
}
//class OrderHistoryPanel extends JPanel {
//    // 주문 내역 페이지 구현
//}
//
//class MangeAllMenu extends JPanel {
//    // 상품 관리 페이지 구현
//}
//
//class PaymentHistoryPanel extends JPanel {
//    // 결제 내역 페이지 구현
//}
//
//class ChangePasswordPanel extends JPanel {
//    // 비밀번호 변경 페이지 구현
//}