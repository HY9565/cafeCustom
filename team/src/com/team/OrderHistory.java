package com.team;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.AbstractCellEditor;

public class OrderHistory extends JPanel {

    Connection conn;
    private DefaultTableModel model;

    public OrderHistory(DBConn dbConn) {
        this.conn = dbConn.getConn();
        setLayout(new BorderLayout());

        // 컬럼명을 한글로 변경하고 확인 컬럼 추가
        String[] columnNames = {"주문 번호", "주문 시간", "결제 방법", "총 금액", "포장 여부", "메뉴 상세", "총 수량", "확인"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // 확인 버튼만 수정 가능
            }
        };
        JTable table = new JTable(model);

        // 버튼 렌더러와 에디터 설정
        table.getColumn("확인").setCellRenderer(new ButtonRenderer());
        table.getColumn("확인").setCellEditor(new ButtonEditor(new JButton("확인"), table, this));

        updateOrderHistory(); // 초기 데이터 로드

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateOrderHistory() {
        model.setRowCount(0); // 기존 데이터 제거

        String sql = "SELECT r.rec_num, TO_CHAR(r.rec_time, 'YYYY-MM-DD HH24:MI:SS') AS rec_time, r.rec_card, r.rec_total, r.rec_takeout, " +
                     "CASE WHEN rd.menu_num IS NOT NULL THEN m.menu_name ELSE cm.cmenu_name END AS menu_name, rd.de_count " +
                     "FROM RECEIPT r " +
                     "JOIN RECEIPT_DETAIL rd ON r.rec_num = rd.rec_num " +
                     "LEFT JOIN MENU m ON rd.menu_num = m.menu_num " +
                     "LEFT JOIN CUSTOM_MENU cm ON rd.cmenu_num = cm.cmenu_num " +
                     "WHERE r.rec_finish = 0" +
                     "ORDER BY r.rec_num, r.rec_time, rd.de_num";
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            // 결과를 그룹화할 맵
            Map<String, Object[]> dataMap = new LinkedHashMap<>();

            while (rs.next()) {
                int recNum = rs.getInt("rec_num");
                String recTime = rs.getString("rec_time");
                int recCard = rs.getInt("rec_card");
                String paymentMethod = (recCard == 1) ? "카드" : "현금";
                int recTotal = rs.getInt("rec_total");
                int recTakeout = rs.getInt("rec_takeout");
                String takeout = (recTakeout == 1) ? "포장" : "매장";
                String menuName = rs.getString("menu_name");
                int deCount = rs.getInt("de_count");

                String key = recNum + recTime;

                if (dataMap.containsKey(key)) {
                    Object[] existingData = dataMap.get(key);
                    existingData[5] = existingData[5] + ", " + menuName + " x" + deCount;
                    existingData[6] = (int) existingData[6] + deCount;
                } else {
                    Object[] data = {recNum, recTime, paymentMethod, recTotal, takeout, menuName + " x" + deCount, deCount, "확인"};
                    dataMap.put(key, data);
                }
            }

            // 맵의 데이터를 테이블 모델에 추가
            for (Object[] data : dataMap.values()) {
                model.addRow(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 버튼 렌더러 클래스
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "확인" : value.toString());
            return this;
        }
    }

    // 버튼 에디터 클래스
    class ButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        private JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;
        private OrderHistory orderHistory; // OrderHistory 인스턴스 추가

        public ButtonEditor(JButton button, JTable table, OrderHistory orderHistory) {
            this.button = button;
            this.table = table;
            this.orderHistory = orderHistory; // 초기화
            this.button.addActionListener(this);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "확인" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int selectedRow = table.getSelectedRow();
                int orderNumber = (int) table.getValueAt(selectedRow, 0);
                JOptionPane.showMessageDialog(orderHistory, orderNumber + "번 주문이 완료되었습니다", "확인", JOptionPane.INFORMATION_MESSAGE);

                // 이후 작업을 UI 스레드에서 처리
                SwingUtilities.invokeLater(() -> {
                    fireEditingStopped(); // 편집 중지

                    // 테이블에서 행 삭제
                    ((DefaultTableModel) table.getModel()).removeRow(selectedRow);

                    // 데이터베이스에서 삭제
                    updateOrderFromDatabase(orderNumber);
                });
            }
            isPushed = false;
            return label;
        }

        private void updateOrderFromDatabase(int orderNumber) {
            String updateReceiptFinishSQL = "UPDATE RECEIPT SET REC_FINISH = 1 WHERE rec_num = ?";

            try {
                // RECEIPT에서 주문상태 수정
                PreparedStatement pstmtReceipt = conn.prepareStatement(updateReceiptFinishSQL);
                pstmtReceipt.setInt(1, orderNumber);
                pstmtReceipt.executeUpdate();
                pstmtReceipt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            fireEditingStopped();
        }
    }
}
