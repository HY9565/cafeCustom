package com.team;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class ManageAllMenu extends JPanel {

	Connection conn;
	JTable table;

	public ManageAllMenu(DBConn conn) {
		this.conn = conn.getConn();
		setLayout(new BorderLayout());

		String[] columnNames = { "메뉴 번호", "이름", "가격", "타입" };
		DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				// 모든 셀을 편집 불가능하게 설정
				return false;
			}
		};
		table = new JTable(model);
		
        // 셀 높이 설정
        table.setRowHeight(30);

        // 셀 너비 설정
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(200);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(50);
		
        // bottom 영역에 JPanel 추가
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // east 영역에 "메뉴 추가" 버튼 추가
        JButton addMenuButton = new JButton("메뉴 추가");
        bottomPanel.add(addMenuButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

		String sql = "SELECT menu_num, menu_name, menu_price, mt_name"
				+ " FROM menu m, menu_type mt"
				+ " WHERE m.mt_num = mt.mt_num"
				+ " UNION"
				+ " SELECT cmenu_num as menu_num, cmenu_name as menu_name, cmenu_price as menu_price, mt_name"
				+ " FROM custom_menu cm, menu_type mt"
				+ " WHERE cm.mt_num = mt.mt_num"
				+ " ORDER BY menu_num";
		try {
			PreparedStatement pstmt = this.conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int menuNum = rs.getInt("menu_num");
				String menuName = rs.getString("menu_name");
				int menuPrice = rs.getInt("menu_price");
				String menuType = rs.getString("mt_name");

				Object[] data = { menuNum, menuName, menuPrice, menuType };
				model.addRow(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

        addMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 메뉴 추가 버튼 클릭 시 동작 정의
				JDialog dialog = new JDialog();
				dialog.setTitle("Menu Details");
				dialog.setLayout(new BorderLayout());
				dialog.setSize(300, 200);
				dialog.setLocationRelativeTo(null);
				
				dialog.getRootPane().setBorder(new EmptyBorder(5, 5, 5, 5));

				// 레이블과 텍스트 필드 구성
				JPanel panel = new JPanel(new GridLayout(4, 2));
				
				panel.add(new JLabel("메뉴 번호:"));
				JTextField menuNumField = new JTextField();
				panel.add(menuNumField);

				panel.add(new JLabel("이름:"));
				JTextField menuNameField = new JTextField();
				panel.add(menuNameField);

				panel.add(new JLabel("가격:"));
				JTextField menuPriceField = new JTextField();
				panel.add(menuPriceField);

				// JComboBox를 위한 메뉴 타입 목록을 데이터베이스에서 가져옴
				panel.add(new JLabel("타입:"));
				JComboBox<String> menuTypeComboBox = new JComboBox<>();
				String menuTypeSql = "SELECT MT_NAME FROM MENU_TYPE";

				try {
					PreparedStatement pstmt = conn.getConn().prepareStatement(menuTypeSql);
					ResultSet rs = pstmt.executeQuery();

					while (rs.next()) {
						String comboMenuType = rs.getString("MT_NAME");
						menuTypeComboBox.addItem(comboMenuType);
					}
				} catch (SQLException ex2) {
					ex2.printStackTrace();
				}
				panel.add(menuTypeComboBox);

				dialog.add(panel, BorderLayout.CENTER);
				
				JPanel buttonPanel = new JPanel();
				JButton addButton = new JButton("추가");
				
				buttonPanel.add(addButton);
				dialog.add(buttonPanel, BorderLayout.SOUTH);
				
				// 추가 버튼 클릭 이벤트
				addButton.addActionListener(new ActionListener() {
				    @Override
				    public void actionPerformed(ActionEvent e) {
				        try {
				        	String insertSQL = "";
				        	String selectedMenuType = (String) menuTypeComboBox.getSelectedItem();
				        	if(selectedMenuType.startsWith("커스텀")) {
				        		insertSQL = "INSERT INTO CUSTOM_MENU (CMENU_NUM, CMENU_NAME, CMENU_PRICE, MT_NUM)"
					                    + " VALUES (?, ?, ?, ("
					                    + " SELECT MT_NUM"
					                    + " FROM MENU_TYPE"
					                    + " WHERE MT_NAME = ?))";
				        	} else {
				        		insertSQL = "INSERT INTO MENU (MENU_NUM, MENU_NAME, MENU_PRICE, MT_NUM)"
					                    + " VALUES (?, ?, ?, ("
					                    + " SELECT MT_NUM"
					                    + " FROM MENU_TYPE"
					                    + " WHERE MT_NAME = ?))";
				        	}
				        			
				            PreparedStatement pstmt = conn.getConn().prepareStatement(insertSQL);
				            pstmt.setInt(1, Integer.parseInt(menuNumField.getText()));
				            pstmt.setString(2, menuNameField.getText());
				            pstmt.setInt(3, Integer.parseInt(menuPriceField.getText()));
				            pstmt.setString(4, selectedMenuType);
				            pstmt.executeUpdate();

				            // 테이블 갱신
				            model.addRow(new Object[]{
				                    Integer.parseInt(menuNumField.getText()),
				                    menuNameField.getText(),
				                    Integer.parseInt(menuPriceField.getText()),
				                    (String) menuTypeComboBox.getSelectedItem()
				            });

				            JOptionPane.showMessageDialog(dialog, "메뉴가 추가되었습니다.");
				            dialog.dispose();
				        } catch (NumberFormatException ex) {
				            JOptionPane.showMessageDialog(dialog, "유효한 숫자를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
				        } catch (java.sql.SQLException ex) {
				            //ex.printStackTrace();
				            JOptionPane.showMessageDialog(dialog, "중복되는 메뉴번호는 입력할 수 없습니다.", "입력오류",JOptionPane.ERROR_MESSAGE);

				        }
				    }
				});
				
				dialog.setVisible(true);
				
            }
        });
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = table.getSelectedRow();
					if (row != -1) {
						// 선택한 열의 행 저장
						int menuNum = (int) table.getValueAt(row, 0);
						String menuName = (String) table.getValueAt(row, 1);
						int menuPrice = (int) table.getValueAt(row, 2);
						String menuType = (String) table.getValueAt(row, 3);

						// 다이얼로그 생성
						JDialog dialog = new JDialog();
						dialog.setTitle("Menu Details");
						dialog.setLayout(new BorderLayout());
						dialog.setSize(300, 200);
						dialog.setLocationRelativeTo(null);

						// 레이블과 텍스트 필드 구성
						JPanel panel = new JPanel(new GridLayout(4, 2));
						panel.add(new JLabel("메뉴 번호:"));
						JTextField menuNumField = new JTextField(String.valueOf(menuNum));
						menuNumField.setEditable(false);
						panel.add(menuNumField);

						panel.add(new JLabel("이름:"));
						JTextField menuNameField = new JTextField(menuName);
						panel.add(menuNameField);

						panel.add(new JLabel("가격:"));
						JTextField menuPriceField = new JTextField(String.valueOf(menuPrice));
						panel.add(menuPriceField);

						// JComboBox를 위한 메뉴 타입 목록을 데이터베이스에서 가져옴
						panel.add(new JLabel("타:"));
						JComboBox<String> menuTypeComboBox = new JComboBox<>();
						String menuTypeSql = "SELECT MT_NAME FROM MENU_TYPE";

						try {
							PreparedStatement pstmt = conn.getConn().prepareStatement(menuTypeSql);
							ResultSet rs = pstmt.executeQuery();

							while (rs.next()) {
								String comboMenuType = rs.getString("MT_NAME");
								menuTypeComboBox.addItem(comboMenuType);
								menuTypeComboBox.setSelectedItem(menuType);
							}
						} catch (SQLException ex2) {
							ex2.printStackTrace();
						}
						panel.add(menuTypeComboBox);

						dialog.add(panel, BorderLayout.CENTER);
						dialog.getRootPane().setBorder(new EmptyBorder(5, 5, 5, 5));

						// 버튼 패널 구성
						JPanel buttonPanel = new JPanel();
						JButton updateButton = new JButton("수정");
						JButton deleteButton = new JButton("삭제");
						JButton cancelButton = new JButton("취소");

						buttonPanel.add(updateButton);
						buttonPanel.add(deleteButton);
						buttonPanel.add(cancelButton);

						dialog.add(buttonPanel, BorderLayout.SOUTH);

						// 수정 버튼 클릭 이벤트
						updateButton.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								try {
									String selectedMenuType = (String) menuTypeComboBox.getSelectedItem();
									String updateSQL="";
									if(selectedMenuType.startsWith("커스텀")) {
										updateSQL = "UPDATE CUSTOM_MENU"
												+ " SET CMENU_NAME = ?,"
												+ " CMENU_PRICE = ?,"
												+ " MT_NUM = (" 
												+ " SELECT MT_NUM" 
												+ " FROM MENU_TYPE"
												+ " WHERE MT_NAME = ?" 
												+ " )" 
												+ " WHERE CMENU_NUM = ?";
									} else {
										updateSQL = "UPDATE MENU"
												+ " SET MENU_NAME = ?,"
												+ " MENU_PRICE = ?,"
												+ " MT_NUM = (" 
												+ " SELECT MT_NUM" 
												+ " FROM MENU_TYPE"
												+ " WHERE MT_NAME = ?" 
												+ " )" 
												+ " WHERE MENU_NUM = ?";
									}
									
									PreparedStatement pstmt = conn.getConn().prepareStatement(updateSQL);
									pstmt.setString(1, menuNameField.getText());
									pstmt.setInt(2, Integer.parseInt(menuPriceField.getText()));
									pstmt.setString(3, selectedMenuType);
									pstmt.setInt(4, Integer.parseInt(menuNumField.getText()));
									pstmt.executeUpdate();

									// 테이블 갱신
									model.setValueAt(menuNameField.getText(), row, 1);
									model.setValueAt(Integer.parseInt(menuPriceField.getText()), row, 2);
									model.setValueAt((String) menuTypeComboBox.getSelectedItem(), row, 3);

									JOptionPane.showMessageDialog(dialog, "메뉴가 수정되었습니다.");
									dialog.dispose();
								} catch (NumberFormatException ex) {
									JOptionPane.showMessageDialog(dialog, "유효한 숫자를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
								} catch (SQLException ex) {
									ex.printStackTrace();
									System.out.println("수정 중 오류가 발생했습니다.");
								}
							}
						});

						// 삭제 버튼 클릭 이벤트
						deleteButton.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								try {
									String deleteSQL="";
									if(menuNum>=100 && menuNum<1000) {
										deleteSQL = "DELETE FROM CUSTOM_MENU WHERE CMENU_NUM=?";
									} else {
										deleteSQL = "DELETE FROM MENU WHERE MENU_NUM=?";
									}
									PreparedStatement pstmt = conn.getConn().prepareStatement(deleteSQL);
									pstmt.setInt(1, menuNum);
									pstmt.executeUpdate();

									// 테이블에서 행 삭제
									model.removeRow(row);

									JOptionPane.showMessageDialog(dialog, "메뉴가 삭제되었습니다.");
									dialog.dispose();
								} catch (SQLException ex) {
									ex.printStackTrace();
									System.out.println("삭제 중 오류가 발생했습니다.");
								}
							}
						});

						// 취소 버튼 클릭 이벤트
						cancelButton.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								dialog.dispose();
							}
						});

						dialog.setVisible(true);
					}
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);
	}

}
