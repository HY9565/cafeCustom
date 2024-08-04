package com.team;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

// 배경 이미지를 표시하기 위한 커스텀 Jpanel 클래스
class ImagePanel extends JPanel {
	private Image img; // 배경 이미지

	// 생성자 : 이미지 객체를 받아서 초기화
	public ImagePanel(Image img) {
		this.img = img;
		setLayout(null); // 절대 위치 사용
		// setLayout(null) 을 사용하면 레이아웃 매니저를 사용하지 않겠다는 의미,
		// 모든 컴포넌트의 위치와 크기를 직접 설정해줘야한다.
		// setBounds(x, y, width, height) 메서드를 사용하여 위치와 크기를 지정합니다.
	}

	// 배경 이미지를 그리기 위해 paintComponent 메서드를 오버라이딩 한다.
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
	}
}

public class CustomCafe extends JFrame {

	ImagePanel main; // 배경 이미지가 있는 메인 패널
	CardLayout cardLayout; // 카드 레이아웃
	JPanel cardPanel; // 카드 레이아웃이 적용된 패널
	JButton takeIn, takeOut, managerLogin; // 버튼 테이크인/아웃버튼
	Label mainMenu; // 메인 메뉴 라벨(사용x)

	// 생성자 : DB연결 객체를 받아서 초기화
	public CustomCafe(DBConn conn) {

		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout); // 카드 레이아웃을 사용하는 패널 생성
		main = new ImagePanel(new ImageIcon("./image/main_logo.jpg").getImage()); // 배경 이미지를 설정한 패널 생성
		main.setBounds(0, 0, 800, 1000); // 패널 크기 설정

		getContentPane().add(cardPanel); // 프레임에 카드 패널 추가
		cardPanel.add(main, "main"); // 카드 패널에 메인 패널 추가

		OrderMenu orderMenu_takein = new OrderMenu(conn, 0, cardLayout, cardPanel); // 주문 메뉴 패널 생성
		cardPanel.add(orderMenu_takein, "orderMenu_takein"); // 카드 패,널에 주문 메뉴 패널 추가

		OrderMenu orderMenu_takeout = new OrderMenu(conn, 1, cardLayout, cardPanel); // 주문 메뉴 패널 생성
		cardPanel.add(orderMenu_takeout, "orderMenu_takeout"); // 카드 패널에 주문 메뉴 패널 추가

		// 매니저페이지 추가
		ManagerPage managerPage = new ManagerPage(conn);
		cardPanel.add(managerPage, "managerPage");

		takeIn = new JButton();
		takeOut = new JButton();
		takeIn.setIcon(new ImageIcon("./image/takein.png"));
		takeOut.setIcon(new ImageIcon("./image/takeout.png"));
		main.setLayout(null); // 절대 위치 사용

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) (screenSize.width * 0.417);
		int height = (int) (screenSize.height * 0.95);

		takeIn.setBounds((int) (width * 0.2975), (int) (height * 0.65), 124, 59);
		main.add(takeIn);
		takeOut.setBounds((int) (width * 0.5475), (int) (height * 0.65), 124, 59);
		main.add(takeOut);

		managerLogin = new JButton();
		managerLogin.setFocusPainted(false);
		managerLogin.setBorderPainted(false);
		managerLogin.setIcon(new ImageIcon("./image/admin_icon.png"));
		managerLogin.setBounds((int) (width * 0.905), (int) (height * 0.01), 50, 50);
		main.add(managerLogin);

		// 버튼 이벤트
		takeIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				OrderMenu orderMenu_takein = new OrderMenu(conn, 0, cardLayout, cardPanel);
				cardPanel.add(orderMenu_takein, "orderMenu_takein");
				
				orderMenu_takein.bt_top_goback.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						cardLayout.show(cardPanel, "main");
					}
				});
				
				cardLayout.show(cardPanel, "orderMenu_takein");
			}
		});

		takeOut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				OrderMenu orderMenu_takeout = new OrderMenu(conn, 1, cardLayout, cardPanel);
				cardPanel.add(orderMenu_takeout, "orderMenu_takeout");
				
				orderMenu_takeout.bt_top_goback.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						cardLayout.show(cardPanel, "main");
					}
				});
				
				cardLayout.show(cardPanel, "orderMenu_takeout");
			}
		});

		CustomCafe cc = this;

		managerLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// JPasswordField 생성
				JPasswordField passwordField = new JPasswordField(10);
				passwordField.setEchoChar('*');

				// 다이얼로그 창 생성
				int action = JOptionPane.showConfirmDialog(null, passwordField, "비밀번호를 입력하세요:",
						JOptionPane.OK_CANCEL_OPTION);

				// 비밀번호 확인
				if (action == JOptionPane.OK_OPTION) {
					String password = new String(passwordField.getPassword());
					if (password.equals("admin")) { // 비번 설정
						ManagerPage managerPage = new ManagerPage(conn);
						cardPanel.add(managerPage, "managerPage");

						// backButton의 ActionListener 설정
						managerPage.backButton.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								cardLayout.show(cardPanel, "main");
							}
						});

						cardLayout.show(cardPanel, "managerPage");
					} else {
						JOptionPane.showMessageDialog(null, "비밀번호가 틀렸습니다.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

	}

	// 메인 메서드
	public static void main(String[] args) {

		UIManagerSetup.setup(); // ui 매니저

		DBConn conn = new DBConn(); // DB 연결 객체 생성

		CustomCafe cc = new CustomCafe(conn); // 의존성 주입 , CustomCafe 객체 생성 (DB 연결 객체 전달)
		// 컴포넌트의 선호 크기를 설정. 레이아웃 매니저가 컴포넌트를 배치할 때 이 크기를 참고
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) (screenSize.width * 0.417);
		int height = (int) (screenSize.height * 0.95);
		cc.setPreferredSize(new Dimension(width, height));
		cc.setSize(width, height); // 실제 창 크기
		cc.setSize(width, height); // 실제 창
		cc.setLocationRelativeTo(null); // 화면 중앙에 프레임 위치
		cc.setResizable(false); // 사이즈 변경 불가

		// cc.setDefaultCloseOperation(cc.EXIT_ON_CLOSE); // JFame 닫기 기능 (없어도 닫기는 되나,
		// 프로세스는 남아있음)
		cc.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // 닫기 동작 직접 처리
		cc.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int confirm = JOptionPane.showOptionDialog(cc, "정말로 닫으시겠습니까?", "종료 확인", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, null, null); // 이게 되네?
				if (confirm == JOptionPane.YES_OPTION) {
					conn.closeConn(); // DB 연결 종료
					cc.dispose(); // JFrame 닫기
				}
			}
		});
		cc.setVisible(true); // 프레임을 화면에 표시

	}
}