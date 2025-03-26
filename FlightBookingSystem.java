import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FlightBookingSystem {
    private static Connection conn;
    private static String currentUser;
    private static boolean isAdmin;
    private static final Color PRIMARY_COLOR = new Color(0, 102, 204);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    public static void main(String[] args) {
        initializeDB();
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void initializeDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/flight_booking?serverTimezone=UTC",
                    "root",
                    ""
            );

            Statement stmt = conn.createStatement();

            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(50) UNIQUE NOT NULL," +
                    "password VARCHAR(50) NOT NULL," +
                    "is_admin BOOLEAN DEFAULT FALSE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS flights (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "flight_number VARCHAR(10) NOT NULL," +
                    "origin VARCHAR(50) NOT NULL," +
                    "destination VARCHAR(50) NOT NULL," +
                    "departure DATETIME NOT NULL," +
                    "seats TEXT)");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection error!");
            System.exit(1);
        }
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Flight Booking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        centerFrame(frame);

        CardLayout cardLayout = new CardLayout();
        JPanel cards = new JPanel(cardLayout);

        JPanel mainMenu = new JPanel(new BorderLayout(0, 20));
        mainMenu.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel("Flight Booking System", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBorder(new EmptyBorder(20, 100, 20, 100));

        JButton loginBtn = createStyledButton("Login", new Color(0, 153, 76), 120);
        JButton registerBtn = createStyledButton("Register", new Color(102, 102, 102), 120);

        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        mainMenu.add(titleLabel, BorderLayout.NORTH);
        mainMenu.add(buttonPanel, BorderLayout.CENTER);

        JPanel loginPanel = createLoginPanel(cardLayout, cards);
        JPanel registerPanel = createRegisterPanel(cardLayout, cards);

        cards.add(mainMenu, "Main");
        cards.add(loginPanel, "Login");
        cards.add(registerPanel, "Register");

        loginBtn.addActionListener(e -> cardLayout.show(cards, "Login"));
        registerBtn.addActionListener(e -> cardLayout.show(cards, "Register"));

        frame.add(cards);
        frame.setVisible(true);
    }

    private static JPanel createLoginPanel(CardLayout cl, JPanel cards) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Login"));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField username = new JTextField(20);
        JPasswordField password = new JPasswordField(20);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        formPanel.add(username, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(password, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton loginBtn = createStyledButton("Login", PRIMARY_COLOR, 120);
        JButton backBtn = createStyledButton("Back", new Color(153, 153, 153), 120);

        buttonPanel.add(backBtn);
        buttonPanel.add(loginBtn);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(buttonPanel, gbc);

        loginBtn.addActionListener(e -> {
            try {
                PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT * FROM users WHERE username=? AND password=?");
                pstmt.setString(1, username.getText());
                pstmt.setString(2, new String(password.getPassword()));
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    currentUser = rs.getString("username");
                    isAdmin = rs.getBoolean("is_admin");
                    if (isAdmin) showAdminDashboard();
                    else showUserDashboard();
                    ((Window) SwingUtilities.getRoot(panel)).dispose();
                } else {
                    JOptionPane.showMessageDialog(panel, "Invalid credentials");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        backBtn.addActionListener(e -> cl.show(cards, "Main"));

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private static JPanel createRegisterPanel(CardLayout cl, JPanel cards) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Registration"));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField username = new JTextField(20);
        JPasswordField password = new JPasswordField(20);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        formPanel.add(username, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(password, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton registerBtn = createStyledButton("Register", PRIMARY_COLOR, 120);
        JButton backBtn = createStyledButton("Back", new Color(153, 153, 153), 120);

        buttonPanel.add(backBtn);
        buttonPanel.add(registerBtn);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(buttonPanel, gbc);

        registerBtn.addActionListener(e -> {
            try {
                PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO users(username, password, is_admin) VALUES(?,?,?)");
                pstmt.setString(1, username.getText());
                pstmt.setString(2, new String(password.getPassword()));
                pstmt.setBoolean(3, false);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(panel, "Registration successful!");
                cl.show(cards, "Main");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(panel, "Username already exists");
            }
        });

        backBtn.addActionListener(e -> cl.show(cards, "Main"));

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private static void showAdminDashboard() {
        JFrame frame = new JFrame("Admin Dashboard");
        frame.setSize(1200, 800);
        centerFrame(frame);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTable flightTable = new JTable();
        flightTable.setRowHeight(30);
        flightTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(flightTable);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(new CompoundBorder(
                new TitledBorder("Create New Flight"),
                new EmptyBorder(10, 10, 10, 10)
        ));

        String[] labels = {"Flight Number:", "Origin:", "Destination:", "Departure (yyyy-MM-dd HH:mm):"};
        JTextField[] fields = new JTextField[labels.length];
        for(int i = 0; i < labels.length; i++) {
            formPanel.add(new JLabel(labels[i]));
            fields[i] = new JTextField();
            formPanel.add(fields[i]);
        }

        JButton createBtn = createStyledButton("Create Flight", new Color(0, 153, 76), 150);
        JButton deleteBtn = createStyledButton("Delete Flight", new Color(204, 0, 0), 150);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(deleteBtn);
        buttonPanel.add(createBtn);

        createBtn.addActionListener(e -> {
            try {
                PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO flights(flight_number, origin, destination, departure, seats) VALUES(?,?,?,?,?)");
                pstmt.setString(1, fields[0].getText());
                pstmt.setString(2, fields[1].getText());
                pstmt.setString(3, fields[2].getText());
                pstmt.setString(4, fields[3].getText());
                pstmt.setString(5, "");
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Flight created!");
                updateFlightTable(flightTable);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error creating flight: " + ex.getMessage());
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = flightTable.getSelectedRow();
            if (row >= 0) {
                int flightId = (Integer) flightTable.getValueAt(row, 0);
                try {
                    PreparedStatement pstmt = conn.prepareStatement(
                            "DELETE FROM flights WHERE id=?");
                    pstmt.setInt(1, flightId);
                    pstmt.executeUpdate();
                    updateFlightTable(flightTable);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        updateFlightTable(flightTable);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private static void updateFlightTable(JTable table) {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime twoDaysLater = now.plusDays(2);

            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT * FROM flights WHERE departure BETWEEN ? AND ?");
            pstmt.setString(1, now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            pstmt.setString(2, twoDaysLater.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

            ResultSet rs = pstmt.executeQuery();
            List<Object[]> data = new ArrayList<>();

            while (rs.next()) {
                data.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("flight_number"),
                        rs.getString("origin"),
                        rs.getString("destination"),
                        rs.getString("departure")
                });
            }

            table.setModel(new DefaultTableModel(
                    data.toArray(new Object[0][]),
                    new String[]{"ID", "Flight Number", "Origin", "Destination", "Departure"}
            ));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void showUserDashboard() {
        JFrame frame = new JFrame("User Dashboard");
        frame.setSize(1200, 800);
        centerFrame(frame);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTable flightTable = new JTable();
        flightTable.setRowHeight(30);
        flightTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(flightTable);

        JButton viewSeatsBtn = createStyledButton("View Available Seats", PRIMARY_COLOR, 200);
        viewSeatsBtn.addActionListener(e -> {
            int row = flightTable.getSelectedRow();
            if (row >= 0) {
                int flightId = (Integer) flightTable.getValueAt(row, 0);
                showSeatSelection(flightId);
            }
        });

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(viewSeatsBtn, BorderLayout.SOUTH);

        updateFlightTable(flightTable);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private static void showSeatSelection(int flightId) {
        JFrame seatFrame = new JFrame("Seat Selection");
        seatFrame.setSize(600, 500);
        centerFrame(seatFrame);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel seatPanel = new JPanel(new GridLayout(10, 5, 5, 5));
        seatPanel.setBorder(new CompoundBorder(
                new TitledBorder("Select Seats (Green = Available, Red = Booked)"),
                new EmptyBorder(10, 10, 10, 10)
        ));

        List<String> bookedSeats = new ArrayList<>();
        try {
            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT seats FROM flights WHERE id = ?");
            pstmt.setInt(1, flightId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String seats = rs.getString("seats");
                if (seats != null && !seats.isEmpty()) {
                    String[] seatsArray = seats.split(",");
                    for (String s : seatsArray) bookedSeats.add(s);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        for (int i = 1; i <= 50; i++) {
            JButton seat = new JButton(String.valueOf(i));
            seat.setFont(BUTTON_FONT);
            seat.setFocusPainted(false);
            seat.setPreferredSize(new Dimension(50, 50));
            if (bookedSeats.contains(String.valueOf(i))) {
                seat.setBackground(Color.RED);
                seat.setEnabled(false);
            } else {
                seat.setBackground(Color.GREEN);
                seat.addActionListener(e -> {
                    JButton clickedSeat = (JButton) e.getSource();
                    if (clickedSeat.getBackground() == Color.GREEN) {
                        clickedSeat.setBackground(Color.YELLOW);
                    } else {
                        clickedSeat.setBackground(Color.GREEN);
                    }
                });
            }
            seatPanel.add(seat);
        }

        JButton confirmBtn = createStyledButton("Confirm Booking", PRIMARY_COLOR, 200);
        confirmBtn.addActionListener(e -> {
            try {
                StringBuilder selectedSeats = new StringBuilder();
                for (Component comp : seatPanel.getComponents()) {
                    if (comp instanceof JButton) {
                        JButton seat = (JButton) comp;
                        if (seat.getBackground() == Color.YELLOW) {
                            selectedSeats.append(seat.getText()).append(",");
                        }
                    }
                }

                if (selectedSeats.length() > 0) {
                    selectedSeats.setLength(selectedSeats.length() - 1);
                    PreparedStatement pstmt = conn.prepareStatement(
                            "UPDATE flights SET seats = CONCAT(IFNULL(seats, ''), ?) WHERE id = ?");
                    pstmt.setString(1, "," + selectedSeats.toString());
                    pstmt.setInt(2, flightId);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(seatFrame, "Booking confirmed!");
                    seatFrame.dispose();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        mainPanel.add(seatPanel, BorderLayout.CENTER);
        mainPanel.add(confirmBtn, BorderLayout.SOUTH);
        seatFrame.add(mainPanel);
        seatFrame.setVisible(true);
    }

    private static JButton createStyledButton(String text, Color bgColor, int width) {
        JButton btn = new JButton(text);
        btn.setFont(BUTTON_FONT);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));    // Reduced padding
        btn.setPreferredSize(new Dimension(width, 30));  // Reduced height
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        return btn;
    }

    private static void centerFrame(Window frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(
                (screenSize.width - frame.getWidth()) / 2,
                (screenSize.height - frame.getHeight()) / 2
        );
    }
}