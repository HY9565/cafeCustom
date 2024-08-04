package com.team;

import java.awt.event.*;
import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class CustomMenu extends JPanel {

	Connection conn; // 데이터베이스와 연결을 나타냄
    OrderMenu orderMenu; //커스텀메뉴 패널의 부모패널
    
    JPanel panel_middle, panel_main_source, panel_sub_source1, panel_sub_source2, 
    panel_total_source, panel_select_source, panel_source_complete, panel_source1, panel_source2, panel_source3,
    panel_source1_bottom, panel_source2_bottom, panel_source3_bottom;
    
    JLabel lb_main_source, lb_sub_source1, lb_sub_source2;
    JLabel lb_select_sourceImage1, lb_select_sourceImage2, lb_select_sourceImage3,
    lb_select_sourceName1, lb_select_sourceName2, lb_select_sourceName3, 
    lb_select_sourcePrice1, lb_select_sourcePrice2, lb_select_sourcePrice3;
    
    //초기화 버튼과 완료 버튼
    JButton reset, complete;
    
    JScrollPane scroll;
    
    int sourceNum1, sourceNum2, sourceNum3;
    
    CustomMenu customMenu;
    
    int cMenuNum;
    String cMenuName;
    
    public CustomMenu(DBConn conn, OrderMenu orderMenu) {
    	this.conn = conn.getConn(); // 전달받은 DBConn 객체에서 실제 Connection 객체를 가져와 conn 필드에 저장
        this.orderMenu = orderMenu; //  전달받은 OrderMenu 객체를 orderMenu 필드에 저장
        
        this.setLayout(new BorderLayout()); 
                
        //커스텀 재료를 선택하는 패널 담을 패널
        panel_middle=new JPanel(new GridLayout(3,1));    
        this.add(new JScrollPane(panel_middle),"Center");	//재료 부분의 패널을 전체 프레임에 부착
                
        panel_main_source=new JPanel(new BorderLayout());		//주재료를 선택할 수 있는 패널
        panel_sub_source1=new JPanel(new BorderLayout());		//부재료1을 선택할 수 있는 패널
        panel_sub_source2=new JPanel(new BorderLayout());		//부재료2를 선택할 수 있는 패널
        
        panel_middle.add(panel_main_source);
        panel_middle.add(panel_sub_source1);
        panel_middle.add(panel_sub_source2);
        
        lb_main_source=new JLabel("주재료",Label.LEFT);
        lb_sub_source1=new JLabel("부재료 1",Label.LEFT);
        lb_sub_source2=new JLabel("부재료 2",Label.LEFT);
        
        panel_main_source.add(lb_main_source,"North");
        panel_sub_source1.add(lb_sub_source1,"North");
        panel_sub_source2.add(lb_sub_source2,"North");
        
        //각 패널 객체 생성 후 CustomMenu 패널에 부착
        MainSourcePanel mainSourcePanel=new MainSourcePanel(conn, this);
        mainSourcePanel.setBackground(Color.WHITE);
        panel_main_source.add(mainSourcePanel,"Center");
        SubSourcePanel1 subSourcePanel1=new SubSourcePanel1(conn, this);
        subSourcePanel1.setBackground(Color.WHITE);
        panel_sub_source1.add(subSourcePanel1,"Center");       
        SubSourcePanel2 subSourcePanel2=new SubSourcePanel2(conn, this);
        subSourcePanel2.setBackground(Color.WHITE);
        panel_sub_source2.add(subSourcePanel2,"Center");
        
        //하단부
        panel_total_source=new JPanel(new BorderLayout());	//선택한 재료를 보여주는 패널
        panel_total_source.setBackground(Color.white);
        this.add(panel_total_source,"South");
        panel_select_source=new JPanel(new GridLayout(1,3,10,10));	//선택한 재료를 보여주는 라벨을 부착할 패널
        panel_select_source.setBackground(Color.white);
        panel_source_complete=new JPanel(new GridLayout(2,1));	//선택한 재료를 초기화하는 버튼과 완성하기 버튼을 부착할 패널
        //선택한 재료를 보여줄 패널과, 초기화/완성 버튼을 부착할 패널을 아래 패널에 부착
        panel_total_source.add(panel_select_source,"Center");
        panel_total_source.add(panel_source_complete,"East");

        panel_source1=new JPanel(new BorderLayout());
        panel_source1.setBackground(Color.white);
        panel_source2=new JPanel(new BorderLayout());
        panel_source2.setBackground(Color.white);
        panel_source3=new JPanel(new BorderLayout());
        panel_source3.setBackground(Color.white);

        
        panel_select_source.add(panel_source1);
        panel_select_source.add(panel_source2);
        panel_select_source.add(panel_source3);
        
        
        //선택한 재료를 보여주는 라벨, 빈문자열로 초기화
        lb_select_sourceImage1=new JLabel();	//선택1
        lb_select_sourceImage2=new JLabel();	//선택2
        lb_select_sourceImage3=new JLabel();	//선택3
        
        panel_source1.add(lb_select_sourceImage1, "Center");
        panel_source2.add(lb_select_sourceImage2, "Center");
        panel_source3.add(lb_select_sourceImage3, "Center");
        
        panel_source1_bottom=new JPanel(new GridLayout(1,2,10,0));
        panel_source1_bottom.setBackground(Color.white);
        panel_source2_bottom=new JPanel(new GridLayout(1,2,3,0));
        panel_source2_bottom.setBackground(Color.white);
        panel_source3_bottom=new JPanel(new GridLayout(1,2,3,0));
        panel_source3_bottom.setBackground(Color.white);
        
        panel_source1.add(panel_source1_bottom, "South");
        panel_source2.add(panel_source2_bottom, "South");
        panel_source3.add(panel_source3_bottom, "South");
        
        lb_select_sourceName1=new JLabel("");
        lb_select_sourceName1.setBackground(Color.white);
        lb_select_sourceName2=new JLabel("");
        lb_select_sourceName2.setBackground(Color.white);
        lb_select_sourceName3=new JLabel("");
        lb_select_sourceName3.setBackground(Color.white);
        
        panel_source1_bottom.add(lb_select_sourceName1);
        panel_source2_bottom.add(lb_select_sourceName2);
        panel_source3_bottom.add(lb_select_sourceName3);
        
        lb_select_sourcePrice1=new JLabel("");
        lb_select_sourcePrice1.setBackground(Color.white);
        lb_select_sourcePrice2=new JLabel("");
        lb_select_sourcePrice2.setBackground(Color.white);
        lb_select_sourcePrice3=new JLabel("");
        lb_select_sourcePrice3.setBackground(Color.white);
        
        panel_source1_bottom.add(lb_select_sourcePrice1);
        panel_source2_bottom.add(lb_select_sourcePrice2);
        panel_source3_bottom.add(lb_select_sourcePrice3);
        // 라벨의 크기를 정사각형으로 설정
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        int image_size=(int) ((screenSize.width * 0.417)/5.5);
        lb_select_sourceImage1.setPreferredSize(new Dimension(image_size, image_size));
        lb_select_sourceImage1.setSize(image_size, image_size);
        
        lb_select_sourceImage2.setPreferredSize(new Dimension(image_size, image_size));
        lb_select_sourceImage2.setSize(image_size, image_size);
        
        lb_select_sourceImage3.setPreferredSize(new Dimension(image_size, image_size));
        lb_select_sourceImage3.setSize(image_size, image_size);
        
        
        //초기화, 완료 버튼
        reset=new JButton("초기화");
        complete=new JButton("완료");
        
        panel_source_complete.add(reset);
        panel_source_complete.add(complete);
       
        //초기화 버튼 이벤트
        reset.addActionListener(new ActionListener() {
        	
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		lb_select_sourceName1.setText("");
        		lb_select_sourceName2.setText("");
        		lb_select_sourceName3.setText("");
        		
        		lb_select_sourceImage1.setIcon(null);
        		lb_select_sourceImage2.setIcon(null);
        		lb_select_sourceImage3.setIcon(null);
        		
        		lb_select_sourcePrice1.setText("");
        		lb_select_sourcePrice2.setText("");
        		lb_select_sourcePrice3.setText("");
        		
        		sourceNum1 = 0;
        		sourceNum2 = 0;
        		sourceNum3 = 0;
        	}
        });
                
        //완료 버튼 이벤트
        complete.addActionListener(new ActionListener() {
        	
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		
        		//선택한 재료 번호 가져오기
        		String sqlGetSourceNum="SELECT SOURCE_NUM FROM CUSTOM_SOURCE WHERE SOURCE_NAME=?";
        		
        		try {
					PreparedStatement psmt=conn.getConn().prepareStatement(sqlGetSourceNum);
					
					psmt.setString(1, lb_select_sourceName1.getText());				
					ResultSet rs1=psmt.executeQuery();
					if(rs1.next()) {
						sourceNum1=rs1.getInt("SOURCE_NUM");
					}
					
					psmt.setString(1, lb_select_sourceName2.getText());				
					ResultSet rs2=psmt.executeQuery();
					if(rs2.next()) {
						sourceNum2=rs2.getInt("SOURCE_NUM");
					}
					
					psmt.setString(1, lb_select_sourceName3.getText());				
					ResultSet rs3=psmt.executeQuery();
					if(rs3.next()) {
						sourceNum3=rs3.getInt("SOURCE_NUM");
					}
					
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        		
        		
        		if(sourceNum1==0||sourceNum2==0||sourceNum3==0) {
        			String warningMSG = "재료를 모두 선택해주세요.";
        			warningDialog(warningMSG);	//재료를 모두 선택하지 않고 완료 버튼을 눌렀을 때 다이얼로그 띄우기
        			
        			return;
        		} 
        		
        		//라벨아이콘 초기화
        		lb_select_sourceImage1.setIcon(null);
        		lb_select_sourceImage2.setIcon(null);
        		lb_select_sourceImage3.setIcon(null);
        		
        		//선택한 재료 번호를 통해 메뉴번호 가져오기 (같은 조합이지만 다른 메뉴번호가 생성되는 것을 감지하기 위해 변수 6개 필요)
        		int num1=sourceNum1*100+(sourceNum2%10)*10+(sourceNum3%10);
        		int num2=sourceNum1*100+(sourceNum3%10)*10+(sourceNum2%10);
        		int num3=(sourceNum2%10)*100+sourceNum1*10+(sourceNum3%10);
        		int num4=(sourceNum2%10)*100+(sourceNum3%10)*10+sourceNum1;
        		int num5=(sourceNum3%10)*100+sourceNum1*10+(sourceNum2%10);
        		int num6=(sourceNum3%10)*100+(sourceNum2%10)*10+sourceNum1;

        		//System.out.println(num1+" "+num2+" "+num3+" "+num4+" "+num5+" "+num6);
        		//여러 변수들 중 가장 작은 수를 기준으로 cMenuNum을 설정 (맨 앞자리가 0인 것 제외)
				int number[]= {num1, num2, num3, num4, num5, num6};
				
				cMenuNum=number[0];	
				for(int i=1;i<number.length;i++) {
					if (number[i] < cMenuNum && number[i]>=100) {
						cMenuNum = number[i];
		            }
				} 
							
				if(cMenuNum==103) {	//그린치의 경우 여러 가능성 중 가장 작은 수는 103이지만, 티타입의 메뉴로 분류하고 있으므로 메뉴번호가 301로 저장되었다.
					cMenuNum=301;	//103이 아닌 301로 조회하기 위한 코드	
				}else if(cMenuNum==155) {	//카페라떼와 카푸치노는 기본메뉴에서 주문할 수 있도록 설정
					lb_select_sourceName1.setText("");
            		lb_select_sourceName2.setText("");
            		lb_select_sourceName3.setText("");  
            		lb_select_sourcePrice1.setText("");
            		lb_select_sourcePrice2.setText("");
            		lb_select_sourcePrice3.setText("");
            		lb_select_sourceImage1.setIcon(null);
            		lb_select_sourceImage2.setIcon(null);
            		lb_select_sourceImage3.setIcon(null);
					sourceNum1=0;
            		sourceNum2=0;
            		sourceNum3=0;
					cMenuName="카페라떼";
					orderDialog();
					return;
				}else if(cMenuNum==115) {
					lb_select_sourceName1.setText("");
            		lb_select_sourceName2.setText("");
            		lb_select_sourceName3.setText("");   
            		lb_select_sourcePrice1.setText("");
            		lb_select_sourcePrice2.setText("");
            		lb_select_sourcePrice3.setText("");
					sourceNum1=0;
            		sourceNum2=0;
            		sourceNum3=0;
            		lb_select_sourceImage1.setIcon(null);
            		lb_select_sourceImage2.setIcon(null);
            		lb_select_sourceImage3.setIcon(null);
					cMenuName="카푸치노";
					orderDialog();
					return;
				}
        		
        		
        		
        		String sql="SELECT CMENU_NAME, CMENU_PRICE FROM CUSTOM_MENU WHERE CMENU_NUM = ?";
        		try {
					PreparedStatement ps=conn.getConn().prepareStatement(sql);
					
					ps.setInt(1, cMenuNum);
					
					ResultSet rs=ps.executeQuery();
					
					if(rs.next()) {	//선택한 조합이 레시피에 있는 경우
						String cmenuName=rs.getString("CMENU_NAME");
						int cmenuPrice=rs.getInt("CMENU_PRICE");
						
						orderMenu.addCart(cmenuName, cmenuPrice, cMenuNum);    						
					} else {	//선택한 조합이 레시피에 없는 경우
						String sourceName1=lb_select_sourceName1.getText();
						String sourceName2=lb_select_sourceName2.getText();
						String sourceName3=lb_select_sourceName3.getText();
						    						
						String cMenuName=sourceName1;	//1번 재료명으로 기본 세팅
						
						//중복되는 재료명은 생략하는 코드
						if(sourceNum1!=sourceNum2) {
							cMenuName=sourceName2+cMenuName;
							if(sourceNum2!=sourceNum3) {
								if(sourceNum1!=sourceNum3) {
    								cMenuName=sourceName3+cMenuName;
								}
							}
						}else {
							if(sourceNum2!=sourceNum3) {
								if(sourceNum1!=sourceNum3) {
									cMenuName=sourceName3+cMenuName;
								}
							}
						}
						
						//메뉴이름이 우유로 끝나면 라떼로 바꿔서 나오기
						if(cMenuName.endsWith("우유")) {
							cMenuName= cMenuName.substring(0, cMenuName.length() - 2) + "라떼";
						}
						
						//재료가격의 합을 메뉴가격으로 저장
						int aa=Integer.parseInt(lb_select_sourcePrice1.getText());
						int bb=Integer.parseInt(lb_select_sourcePrice2.getText());
						int cc=Integer.parseInt(lb_select_sourcePrice3.getText());
						
						int newCmenuPrice=aa+bb+cc;	
						
						String sql2="INSERT INTO CUSTOM_MENU VALUES (?, ?, ?, ?)";
						
						PreparedStatement ps2=conn.getConn().prepareStatement(sql2);
						
						ps2.setInt(1, cMenuNum);
						ps2.setInt(2, (cMenuNum/100)*100);	//메뉴타입번호는 메뉴번호를 100으로 나눈 몫에 100을 곱한다
						ps2.setString(3, cMenuName);
						ps2.setInt(4, newCmenuPrice);
						
						ps2.executeUpdate();
						
						String sql3="SELECT CMENU_NAME, CMENU_PRICE FROM CUSTOM_MENU WHERE CMENU_NUM=?";
						
						PreparedStatement ps3=conn.getConn().prepareStatement(sql3);
						ps3.setInt(1, cMenuNum);
						
						ResultSet rs3=ps3.executeQuery();
						
						while(rs3.next()) {
							String newCmenuName=rs3.getString("CMENU_NAME");
							int cmenuPrice = rs3.getInt("CMENU_PRICE");						
							//System.out.println(newCmenuName+"\t"+newCmenuPrice+"원");
							
							orderMenu.addCart(newCmenuName, cmenuPrice, cMenuNum);

						}
						  						
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        		//선택했던 재료 초기화
        		lb_select_sourceName1.setText("");
        		lb_select_sourceName2.setText("");
        		lb_select_sourceName3.setText("");   
        		lb_select_sourcePrice1.setText("");
        		lb_select_sourcePrice2.setText("");
        		lb_select_sourcePrice3.setText("");
        		sourceNum1=0;
        		sourceNum2=0;
        		sourceNum3=0;     
        		lb_select_sourceImage1.setIcon(null);
        		lb_select_sourceImage2.setIcon(null);
        		lb_select_sourceImage3.setIcon(null);
        	}
        });
    } 
    
    //경고 다이얼로그 띄우는 메소드
    public void warningDialog(String warningMSG) {
        // 부모 컴포넌트로 JFrame을 사용
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Dialog Title", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new BorderLayout());

        // 대화 상자에 표시할 내용 추가
        JLabel lb=new JLabel(warningMSG,JLabel.CENTER);
        dialog.add(lb, BorderLayout.CENTER);
        
        // 닫기 버튼 추가
        JButton closeButton = new JButton("확인");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        dialog.add(closeButton, BorderLayout.SOUTH);
        
        // 대화 상자 위치 설정 및 표시
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    //기본 메뉴 선택 다이얼로그 띄우는 메소드
    private void orderDialog() {
        // 부모 컴포넌트로 JFrame을 사용
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), " ", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new BorderLayout());

        // 대화 상자에 표시할 내용 추가
        JLabel lb=new JLabel("기본메뉴 <"+cMenuName+">로 주문하시겠습니까?",JLabel.CENTER);
        dialog.add(lb,BorderLayout.CENTER);
        
        JPanel bt_area = new JPanel(new FlowLayout());
        dialog.add(bt_area,"South");
        
        //버튼 추가
        JButton okay = new JButton("확인");
        
        okay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
        		selectIce();
            }
        });
        
        bt_area.add(okay);
        
        JButton close = new JButton("취소");
        
        close.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		dialog.dispose();
        	}
        });
        
        bt_area.add(close);
        
        // 대화 상자 위치 설정 및 표시
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
     
    //아이스/핫 선택 다이얼로그 띄우는 메소드
    private void selectIce() {
    	JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Dialog Title", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new BorderLayout());
        
        JPanel lb_area = new JPanel(new BorderLayout());
        dialog.add(lb_area, "Center");
        JLabel text=new JLabel("ICE / HOT 선택해주세요",JLabel.CENTER);
        lb_area.add(text, "Center");
        
        JPanel bt_area = new JPanel(new FlowLayout());
        dialog.add(bt_area,"South");
        
        JButton ice = new JButton("ICE");
        bt_area.add(ice);
        
        ice.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		if(cMenuName.equals("카페라떼")) {
        			orderMenu.addCart("카페라떼 Ice", 2500, 1003);
        		}else if(cMenuName.equals("카푸치노")) {
        			orderMenu.addCart("카푸치노 Ice", 3500, 1005);
        		}
        		dialog.dispose();
        	}
        });
        
        JButton hot = new JButton("HOT");
        bt_area.add(hot);
        
        hot.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		if(cMenuName.equals("카페라떼")) {
        			orderMenu.addCart("카페라떼 Hot", 2500, 1004);
        		}else if(cMenuName.equals("카푸치노")) {
        			orderMenu.addCart("카푸치노 Hot", 3500, 1006);
        		}    
        		dialog.dispose();
        	}
        });
        
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
   
}






