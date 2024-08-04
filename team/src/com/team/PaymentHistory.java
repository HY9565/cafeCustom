package com.team;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

public class PaymentHistory extends JPanel {

    private JTextField yearField;
    private JTextField monthField;
    private JTextField dayField;
    private DBConn dbConn;
    private DefaultTableModel model;
    private JTable table;
    private JTextArea textArea;

    public PaymentHistory(DBConn conn) {
        this.dbConn = conn;
        initialize();
    }

    public void initialize() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 여백 설정

        yearField = new JTextField(4);
        yearField.setFont(new Font("Arial", Font.PLAIN, 20));
        monthField = new JTextField(2);
        monthField.setFont(new Font("Arial", Font.PLAIN, 20));
        dayField = new JTextField(2);
        dayField.setFont(new Font("Arial", Font.PLAIN, 20));
        JButton searchButton = new JButton("클릭");
        JButton resetButton = new JButton("초기화");

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("년:"), gbc);

        gbc.gridx = 1;
        inputPanel.add(yearField, gbc);

        gbc.gridx = 2;
        inputPanel.add(new JLabel("월:"), gbc);

        gbc.gridx = 3;
        inputPanel.add(monthField, gbc);

        gbc.gridx = 4;
        inputPanel.add(new JLabel("일:"), gbc);

        gbc.gridx = 5;
        inputPanel.add(dayField, gbc);

        gbc.gridx = 6;
        inputPanel.add(searchButton, gbc);
        
        gbc.gridx = 7; // 초기화 버튼의 위치를 클릭 버튼 옆으로 설정
        inputPanel.add(resetButton, gbc);

        // 설명 문구 추가
        JLabel instructionLabel = new JLabel("연도만 출력하고 싶으면 연도만 입력할 수 있으며, 월까지만 출력하고 싶으면 연도와 월을 입력하시오.");
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // 패널 배치
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(instructionLabel, BorderLayout.NORTH);
        northPanel.add(inputPanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);

        // 텍스트 에어리어 추가
        textArea = new JTextArea();
        textArea.setFont(new Font("Arial", Font.PLAIN, 20));
        textArea.setEditable(false); // 텍스트 에어리어는 수정 불가능하게 설정
        add(new JScrollPane(textArea), BorderLayout.SOUTH);

        // 테이블 모델 설정
        String[] columnNames = {"주문 번호", "주문 시간", "결제 방법", "총 주문 금액", "메뉴 상세", "총 수량"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 셀 수정 불가
            }
        };
        table = new JTable(model);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        showDetailDialog(model, selectedRow);
                    }
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 조회 버튼 클릭 이벤트 처리
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchOrders();
            }
        });

        // 초기화 버튼 클릭 이벤트 처리
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetFieldsAndTable();
            }
        });

        // 엔터키 입력 시 조회 메서드 호출
        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchOrders();
                }
            }
        };

        yearField.addKeyListener(enterKeyAdapter);
        monthField.addKeyListener(enterKeyAdapter);
        dayField.addKeyListener(enterKeyAdapter);

        // 더블 클릭 이벤트 처리
        MouseAdapter doubleClickAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    yearField.setText("");
                    monthField.setText("");
                    dayField.setText("");
                    textArea.setText("");
                }
            }
        };

        yearField.addMouseListener(doubleClickAdapter);
        monthField.addMouseListener(doubleClickAdapter);
        dayField.addMouseListener(doubleClickAdapter);

        revalidate();
        repaint();
    }

    public void resetFieldsAndTable() { // 초기화
        yearField.setText("");
        monthField.setText("");
        dayField.setText("");
        textArea.setText("");
        clearTable();
    }

    public void clearTable() {
        model.setRowCount(0);
    }

    private void searchOrders() {
        String year = yearField.getText();
        String month = monthField.getText();
        String day = dayField.getText();

        if (month != null && !month.isEmpty() && (year == null || year.isEmpty())) {
            JOptionPane.showMessageDialog(this, "연도를 입력하시오", "에러", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (day != null && !day.isEmpty() && (year == null || year.isEmpty() || month == null || month.isEmpty())) {
            if (year == null || year.isEmpty()) {
                JOptionPane.showMessageDialog(this, "연도를 입력하시오", "에러", JOptionPane.ERROR_MESSAGE);
            } else if (month == null || month.isEmpty()) {
                JOptionPane.showMessageDialog(this, "월을 입력하시오", "에러", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        if (!isValidDate(year, month, day)) {
            return;
        }

        // 입력된 년, 월, 일 조합하여 날짜 필터링
        String dateFilter = year;
        if (month != null && !month.isEmpty()) {
            dateFilter += "-" + (month.length() == 1 ? "0" + month : month);
        } else {
            dateFilter += "-%";
        }
        if (day != null && !day.isEmpty()) {
            dateFilter += "-" + (day.length() == 1 ? "0" + day : day);
        } else {
            dateFilter += "-%";
        }

        LocalDate today = LocalDate.now();
        LocalDate inputDate = LocalDate.of(
                Integer.parseInt(year),
                month.isEmpty() ? 1 : Integer.parseInt(month),
                day.isEmpty() ? 1 : Integer.parseInt(day)
        );

        // 다음날 날짜를 계산
        LocalDate nextDay = inputDate.plusDays(1);

        if (nextDay.isAfter(today.plusDays(1))) {
            JOptionPane.showMessageDialog(this, "미래 날짜는 입력할 수 없습니다", "에러", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection connection = dbConn.getConn();
            String query = "SELECT r.rec_num, TO_CHAR(r.rec_time, 'YYYY-MM-DD HH24:MI:SS') AS rec_time, r.rec_card, r.rec_total, " +
                           "CASE WHEN rd.menu_num IS NOT NULL THEN m.menu_name ELSE cm.cmenu_name END AS menu_name, rd.de_count " +
                           "FROM RECEIPT r " +
                           "JOIN RECEIPT_DETAIL rd ON r.rec_num = rd.rec_num " +
                           "LEFT JOIN MENU m ON rd.menu_num = m.menu_num " +
                           "LEFT JOIN CUSTOM_MENU cm ON rd.cmenu_num = cm.cmenu_num " +
                           "WHERE TO_CHAR(r.rec_time, 'YYYY-MM-DD') LIKE ? " +
                           "ORDER BY r.rec_num, r.rec_time, rd.de_num";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, dateFilter);
            ResultSet rs = pstmt.executeQuery();

            // 기존 데이터 지우기
            model.setRowCount(0);

            // 총 주문 금액의 총합을 계산하기 위한 변수
            int totalRevenue = 0;

            // 결과를 그룹화할 맵
            Map<String, Object[]> dataMap = new LinkedHashMap<>();

            // ResultSet에서 데이터 가져오기
            while (rs.next()) {
                int recNum = rs.getInt("rec_num");
                String recTime = rs.getString("rec_time");
                int recCard = rs.getInt("rec_card");
                String paymentMethod = (recCard == 1) ? "카드" : "현금";
                int recTotal = rs.getInt("rec_total");
                String menuName = rs.getString("menu_name");
                int deCount = rs.getInt("de_count");
                totalRevenue += recTotal; // 총합 계산

                String key = recNum + recTime;

                if (dataMap.containsKey(key)) {
                    Object[] existingData = dataMap.get(key);
                    existingData[4] = existingData[4] + ", " + menuName + " x" + deCount;
                    existingData[5] = (int) existingData[5] + deCount;
                } else {
                    Object[] data = {recNum, recTime, paymentMethod, recTotal, menuName + " x" + deCount, deCount};
                    dataMap.put(key, data);
                }
            }

            // 맵의 데이터를 테이블 모델에 추가
            for (Object[] data : dataMap.values()) {
                model.addRow(data);
            }

            // 총합을 표시하는 레이블 추가
            JLabel totalRevenueLabel = new JLabel("총 수입: " + totalRevenue + " 원");
            totalRevenueLabel.setHorizontalAlignment(SwingConstants.CENTER);

            add(totalRevenueLabel, BorderLayout.SOUTH);
            revalidate();
            repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDetailDialog(DefaultTableModel model, int selectedRow) {
        String recNum = model.getValueAt(selectedRow, 0).toString();
        String recTime = model.getValueAt(selectedRow, 1).toString();
        String paymentMethod = model.getValueAt(selectedRow, 2).toString();
        String recTotal = model.getValueAt(selectedRow, 3).toString();
        String menuDetail = model.getValueAt(selectedRow, 4).toString();
        String totalCount = model.getValueAt(selectedRow, 5).toString();

        JOptionPane.showMessageDialog(this,
                "주문 번호: " + recNum + "\n" +
                "주문 시간: " + recTime + "\n" +
                "결제 방법: " + paymentMethod + "\n" +
                "총 주문 금액: " + recTotal + "\n" +
                "메뉴 상세: " + menuDetail + "\n" +
                "총 수량: " + totalCount,
                "상세 정보",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean isValidDate(String year, String month, String day) {
        try {
            int yearInt = Integer.parseInt(year);
            if (yearInt > 2025) {
                JOptionPane.showMessageDialog(this, "미래 연도는 입력할 수 없습니다", "에러", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            int monthInt = month.isEmpty() ? 0 : Integer.parseInt(month);
            int dayInt = day.isEmpty() ? 0 : Integer.parseInt(day);

            if (yearInt < 1) {
                throw new NumberFormatException();
            }
            if (monthInt < 0 || monthInt > 12) {
                JOptionPane.showMessageDialog(this, "없는 월을 입력했습니다", "에러", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (!month.isEmpty()) {
                YearMonth yearMonth = YearMonth.of(yearInt, monthInt);
                if (dayInt < 0 || dayInt > yearMonth.lengthOfMonth()) {
                    JOptionPane.showMessageDialog(this, "없는 일을 입력했습니다", "에러", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            LocalDate today = LocalDate.now();
            if (!day.isEmpty()) {
                LocalDate inputDate = LocalDate.of(yearInt, monthInt, dayInt);
                if (inputDate.isAfter(today.plusDays(1))) {
                    JOptionPane.showMessageDialog(this, "미래 날짜는 입력할 수 없습니다", "에러", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "숫자로만 입력하시오", "에러", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "잘못된 날짜 형식입니다", "에러", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    // 결제 처리 메서드
    public int processPayment(int recCard, int recTotal, int recTakeout, List<OrderDetail> orderDetails) {
        Connection conn = null;
        PreparedStatement pstmtReceipt = null;
        PreparedStatement pstmtDetail = null;
        int recNum = 0;

        try {
            conn = dbConn.getConn();
            conn.setAutoCommit(false);

            // Insert into RECEIPT table
            String sqlReceipt = "INSERT INTO RECEIPT (rec_num, rec_time, rec_card, rec_total, rec_takeout) VALUES (seq_rec_num.NEXTVAL, SYSDATE, ?, ?, ?)";
            pstmtReceipt = conn.prepareStatement(sqlReceipt, new String[] {"rec_num"});
            pstmtReceipt.setInt(1, recCard);
            pstmtReceipt.setInt(2, recTotal);
            pstmtReceipt.setInt(3, recTakeout);
            pstmtReceipt.executeUpdate();

            // Get generated rec_num
            ResultSet rs = pstmtReceipt.getGeneratedKeys();
            if (rs.next()) {
                recNum = rs.getInt(1);
            }

            // Insert into RECEIPT_DETAIL table
            String sqlDetail = "INSERT INTO RECEIPT_DETAIL (de_num, rec_num, menu_num, de_count) VALUES (seq_de_num.NEXTVAL, ?, ?, ?)";
            pstmtDetail = conn.prepareStatement(sqlDetail);
            for (OrderDetail detail : orderDetails) {
                pstmtDetail.setInt(1, recNum);
                pstmtDetail.setInt(2, detail.getMenuNum());
                pstmtDetail.setInt(3, detail.getCount());
                pstmtDetail.addBatch();
            }
            pstmtDetail.executeBatch();

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (pstmtReceipt != null) {
                try {
                    pstmtReceipt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pstmtDetail != null) {
                try {
                    pstmtDetail.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
        }
        return recNum;
    }

    // OrderDetail 클래스 내부 클래스
    public static class OrderDetail {
        private int menuNum;
        private int count;
 
        public OrderDetail(int menuNum, int count) {
            this.menuNum = menuNum;
            this.count = count;
        }

        public int getMenuNum() {
            return menuNum;
        }

        public void setMenuNum(int menuNum) {
            this.menuNum = menuNum;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            resetFieldsAndTable(); // 창이 보일 때마다 필드와 테이블을 초기화
        }
    }
}
