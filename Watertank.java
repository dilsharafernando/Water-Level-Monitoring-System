import javax.swing.*;
import java.awt.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


abstract class WaterLevelObserver {
    public abstract void update(int waterLevel);
}

class AlarmObserver extends WaterLevelObserver {
    private Component parent;
    private boolean alertShown = false;

    public AlarmObserver(Component parent) {
        this.parent = parent;
    }

    public void update(int waterLevel) {
        if (waterLevel > 75 && !alertShown) {
            alertShown = true;
            JOptionPane.showMessageDialog(
                parent,
                "ALARM WARNING: Water level is critical at " + waterLevel + "%!",
                "Water Level Alarm",
                JOptionPane.WARNING_MESSAGE
            );
        } else if (waterLevel <= 75) {
            alertShown = false;
        }
    }
}

class SMSSenderObserver extends WaterLevelObserver {
    private Component parent;
    private String phoneNumber;
    private boolean smsSent = false;

    public SMSSenderObserver(Component parent, String phoneNumber) {
        this.parent = parent;
        this.phoneNumber = phoneNumber;
    }

    public void update(int waterLevel) {
        if (waterLevel > 75 && !smsSent) {
            smsSent = true;
            JOptionPane.showMessageDialog(
                parent,
                "SMS Alert Sent to " + phoneNumber + "\nMessage: 'ALERT! Water level high: " + waterLevel + "%'",
                "SMS Gateway",
                JOptionPane.INFORMATION_MESSAGE
            );
        } else if (waterLevel <= 75) {
            smsSent = false;
        }
    }
}

class ControlRoom {
    private WaterLevelObserver[] observerArray = new WaterLevelObserver[0];
    private int waterLevel;

    public void addObserver(WaterLevelObserver observer) {
        extendArray();
        observerArray[observerArray.length - 1] = observer;
    }

    public void setWaterLevel(int waterLevel) {
        this.waterLevel = waterLevel;
        notifyObservers();
    }

    private void notifyObservers() {
        for (WaterLevelObserver ob : observerArray) {
            ob.update(waterLevel);
        }
    }

    private void extendArray() {
        WaterLevelObserver[] temp = new WaterLevelObserver[observerArray.length + 1];
        for (int i = 0; i < observerArray.length; i++) {
            temp[i] = observerArray[i];
        }
        observerArray = temp;
    }
}



public class Watertank extends JFrame{
	
	private JSlider levelSlider;
	private JProgressBar progressBar;
    private JLabel lblValue;
    private JLabel lblStatus;
    
    private ControlRoom controlRoom;
    
    public Watertank() {
        setTitle("Water Level Management System");
        setSize(450, 350);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        
        JLabel lblTitle = new JLabel("Water Level Controller");
        lblTitle.setFont(new Font("", Font.BOLD, 24));
        lblTitle.setHorizontalAlignment(JLabel.CENTER);
        add("North", lblTitle);
        
        JPanel centerPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        lblValue = new JLabel("Current Water Level: 0 %", JLabel.CENTER);
        lblValue.setFont(new Font("", Font.BOLD, 16));
        centerPanel.add(lblValue);
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        centerPanel.add(progressBar);
        
        JLabel lblSliderTitle = new JLabel("Simulate Water Level (Slider):", JLabel.CENTER);
        lblSliderTitle.setFont(new Font("", Font.PLAIN, 14));
        centerPanel.add(lblSliderTitle);
        
        levelSlider = new JSlider(0, 100, 0);
        levelSlider.setMajorTickSpacing(20);
        levelSlider.setMinorTickSpacing(5);
        levelSlider.setPaintTicks(true);
        levelSlider.setPaintLabels(true);
        centerPanel.add(levelSlider);
        
        lblStatus = new JLabel("Status: NORMAL", JLabel.CENTER);
        lblStatus.setFont(new Font("", Font.BOLD, 16));
        lblStatus.setForeground(new Color(0, 128, 0));
        centerPanel.add(lblStatus);
        
        add("Center", centerPanel);
        
        // Controlroom setup
        
        controlRoom = new ControlRoom();
        controlRoom.addObserver(new AlarmObserver(this));
        controlRoom.addObserver(new SMSSenderObserver(this, "+940123456789"));
        
        levelSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int level = levelSlider.getValue();
                progressBar.setValue(level);
                lblValue.setText("Current Water Level: " + level + " %");

                if (level > 75) {
                    lblStatus.setText("Status: DANGER (HIGH WATER)");
                    lblStatus.setForeground(Color.RED);
                   
                } else {
                    lblStatus.setText("Status: NORMAL");
                    lblStatus.setForeground(new Color(0, 128, 0));
                   
                }

                controlRoom.setWaterLevel(level);
            }
        });

    }
	
	public static void main(String args []){
		SwingUtilities.invokeLater(() -> {
            new Watertank().setVisible(true);
        });
	}
}
