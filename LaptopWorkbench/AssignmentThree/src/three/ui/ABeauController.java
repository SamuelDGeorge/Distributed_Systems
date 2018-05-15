package three.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import three.clients.GIPCClient;
import three.util.APropertyListenerSupport;
import three.util.PropertyListenerRegisterer;
import three.util.PropertyListenerSupport;

import javax.swing.JRadioButton;

public class ABeauController implements KeyListener,ActionListener,BeauController {
	private JTextField textField;
	private JRadioButton local;
	private JRadioButton nonAtomic;
	private JRadioButton atomic;
	private GIPCClient client;
	private JFrame currentFrame;
	private PropertyListenerSupport listeners;
	
	public ABeauController(GIPCClient client) {
		this.currentFrame = new JFrame();
		this.listeners = new APropertyListenerSupport();
		this.currentFrame.setTitle("Controller for: " + client.toString());
		this.client = client;
		this.currentFrame.setSize(500, 120);
		this.currentFrame.setResizable(false);
		this.currentFrame.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(12, 12, 466, 146);
		this.currentFrame.getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel lblCommand = new JLabel("Command:");
		lblCommand.setVerticalAlignment(SwingConstants.TOP);
		lblCommand.setBounds(12, 12, 100, 15);
		panel.add(lblCommand);
		
		textField = new JTextField();
		textField.setBounds(112, 10, 327, 19);
		textField.addKeyListener(this);
		panel.add(textField);
		textField.setColumns(10);
		local = new JRadioButton("Local");
		local.setSelected(true);
		local.setBounds(76, 35, 79, 32);
		panel.add(local);
		
		nonAtomic = new JRadioButton("Non-Atomic");
		nonAtomic.setBounds(170, 40, 122, 23);
		panel.add(nonAtomic);
		
		atomic = new JRadioButton("Atomic");
		atomic.setBounds(309, 40, 149, 23);
		panel.add(atomic);
		
		this.local.addActionListener(this);
		this.nonAtomic.addActionListener(this);
		this.atomic.addActionListener(this);
		
		this.currentFrame.setVisible(true);
		this.currentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}

	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == this.local) {
			this.listeners.notifyAllListeners(new PropertyChangeEvent(this, "ChangeMode", "default", "local"));
		} else if (arg0.getSource() == this.nonAtomic) {
			this.listeners.notifyAllListeners(new PropertyChangeEvent(this, "ChangeMode", "default", "non-atomic"));
		} else if (arg0.getSource() == this.atomic) {
			this.listeners.notifyAllListeners(new PropertyChangeEvent(this, "ChangeMode", "default", "atomic"));
		} else {}
		
	}

	
	public void keyTyped(KeyEvent arg0) {
		
		
	}

	
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
				this.listeners.notifyAllListeners(new PropertyChangeEvent(this, "InputString", "default", this.textField.getText()));
		}
		
	}
	
	public void setSelected(String mode) {
		if("local".equals(mode)) {
			this.local.setSelected(true);
			this.nonAtomic.setSelected(false);
			this.atomic.setSelected(false);
		} else if ("non-atomic".equals(mode)) {
			this.local.setSelected(false);
			this.nonAtomic.setSelected(true);
			this.atomic.setSelected(false);
		} else if ("atomic".equals(mode)) {
			this.local.setSelected(false);
			this.nonAtomic.setSelected(false);
			this.atomic.setSelected(true);
		}
	}
	
	public void setText(String text) {
		this.textField.setText(text);
	}
	
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void addPropertyChangeListener(PropertyChangeListener arg0) {
			this.listeners.addElement(arg0);
		
	}
}
