import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Hello World");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(null);
        JLabel welcomeLabel = new JLabel("Welcome to the OOP Project!");
        welcomeLabel.setBounds(100, 50, 200, 30);
        frame.add(welcomeLabel);
        frame.setVisible(true);
    }
}
