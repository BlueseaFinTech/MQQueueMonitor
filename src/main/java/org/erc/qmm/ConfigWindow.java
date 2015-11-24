package org.erc.qmm;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JSlider;
import javax.xml.parsers.ParserConfigurationException;

import org.erc.qmm.config.Queue;
import org.erc.qmm.config.QueueListDataModel;
import org.erc.qmm.i18n.Messages;
import org.erc.qmm.util.FocusTraversalOnArray;
import org.xml.sax.SAXException;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import java.awt.Component;

/**
 * Configuration window
 * 
 * @author xIS15817
 *
 */
public class ConfigWindow extends JDialog {

	private static final long serialVersionUID = 3910825248241443146L;
	
	private final JPanel contentPanel = new JPanel();
	private JTextField txtName;
	private JTextField txtHost;
	private JTextField txtChannel;
	private JTextField txtQueue;
	private JTextField txtPort;
	private JSlider sldPollTime ;
	
	private JList<Queue> queueList;
	private QueueListDataModel queues;

	private Queue selected;
	
	private boolean changes;
	
	private List<ActionListener> listeners;
	
	private abstract class SimpleDocumentListener implements DocumentListener{

		@Override
		public void insertUpdate(DocumentEvent e) {
			update(e);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			update(e);
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			update(e);
		}
		
		public abstract void update(DocumentEvent e);

	}
	
	
	public void addChangedListener(ActionListener listener) {
		if(listeners == null){
			listeners =new ArrayList<ActionListener>();
		}
		listeners.add(listener);
	}
	
	
	protected void fireChangesSaved(){
		if(listeners!=null && changes){
			for(ActionListener listener : listeners){
				listener.actionPerformed(new ActionEvent(this, 1, "SAVE_OK"));
			}
		}
	}
	private void loadSelected(){
		if(selected!=null && txtName!=null){
			txtName.setText(selected.getDesc());
			txtHost.setText(selected.getHost());
			txtChannel.setText(selected.getChannel());
			txtQueue.setText(selected.getName());
			txtPort.setText(String.valueOf(selected.getPort()));
			sldPollTime.setValue(selected.getPollTime());
		}
	}
	
	private void cleanSelected(){
		txtName.setText(""); //$NON-NLS-1$
		txtHost.setText(""); //$NON-NLS-1$
		txtChannel.setText(""); //$NON-NLS-1$
		txtQueue.setText(""); //$NON-NLS-1$
		txtPort.setText("1417"); //$NON-NLS-1$
		sldPollTime.setValue(5000);
	}
	
	/**
	 * Create the dialog.
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public ConfigWindow() throws SAXException, IOException, ParserConfigurationException {
		changes = false;
		
		setTitle(Messages.getString("ConfigWindow.title")); //$NON-NLS-1$
		setBounds(100, 100, 418, 363);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		queueList = new JList<Queue>();
	
		queueList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				selected =(Queue) queueList.getSelectedValue();
				loadSelected();
			}
		});
		queueList.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		queueList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		queueList.setVisibleRowCount(10);
		queueList.setValueIsAdjusting(true);
		
		queues = new QueueListDataModel();
		queues.reload();
		queueList.setModel(queues);
		queueList.setSelectedIndex(0);
		queueList.setBounds(10, 11, 134, 246);
		contentPanel.add(queueList);
		
		
		JButton btnAdd = new JButton(""); //$NON-NLS-1$
		btnAdd.setIcon(new ImageIcon(ConfigWindow.class.getResource(Images.PLUS))); //$NON-NLS-1$
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cleanSelected();
				selected = new Queue();
				selected.setDesc(Messages.getString("ConfigWindow.defaultname")); //$NON-NLS-1$
				selected.setPort(1417);
				selected.setPollTime(5000);
				queues.add(selected);
				queueList.setSelectedValue(selected, true);
				loadSelected();
				changes = true;
			}
		});
		btnAdd.setBounds(10, 268, 53, 23);
		contentPanel.add(btnAdd);
		
		JButton btnRemove = new JButton(""); //$NON-NLS-1$
		btnRemove.setIcon(new ImageIcon(ConfigWindow.class.getResource(Images.MINUS))); //$NON-NLS-1$
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cleanSelected();
				selected =(Queue) queueList.getSelectedValue();
				if (selected!=null){
					queues.remove(selected);
					changes = true;
				}
			}
		});
		btnRemove.setBounds(91, 268, 53, 23);
		contentPanel.add(btnRemove);
		
		JLabel lblHost = new JLabel(Messages.getString("ConfigWindow.host")); //$NON-NLS-1$
		lblHost.setBounds(154, 72, 46, 14);
		contentPanel.add(lblHost);
		
		JLabel lblPort = new JLabel(Messages.getString("ConfigWindow.port")); //$NON-NLS-1$
		lblPort.setBounds(154, 115, 46, 14);
		contentPanel.add(lblPort);
		
		JLabel lblName = new JLabel(Messages.getString("ConfigWindow.name")); //$NON-NLS-1$
		lblName.setBounds(154, 29, 46, 14);
		contentPanel.add(lblName);
		
		JLabel lblChannel = new JLabel(Messages.getString("ConfigWindow.channel")); //$NON-NLS-1$
		lblChannel.setBounds(154, 158, 46, 14);
		contentPanel.add(lblChannel);
		
		JLabel lblQueue = new JLabel(Messages.getString("ConfigWindow.queue")); //$NON-NLS-1$
		lblQueue.setBounds(154, 201, 46, 14);
		contentPanel.add(lblQueue);
		
		JLabel lblPollTime = new JLabel(Messages.getString("ConfigWindow.polltime")); //$NON-NLS-1$
		lblPollTime.setBounds(154, 244, 46, 14);
		contentPanel.add(lblPollTime);
		
		txtName = new JTextField();
		txtName.setBounds(201, 24, 191, 20);
		txtName.getDocument().addDocumentListener(new SimpleDocumentListener(){
			public void update(DocumentEvent e) {
				selected.setDesc(txtName.getText());
				changes = true;
			}
		});
		contentPanel.add(txtName);
		txtName.setColumns(10);
		
		txtHost = new JTextField();
		txtHost.setBounds(201, 68, 191, 20);
		contentPanel.add(txtHost);
		txtHost.setColumns(10);
		txtHost.getDocument().addDocumentListener(new SimpleDocumentListener(){
			public void update(DocumentEvent e) {
				selected.setHost(txtHost.getText());
				changes = true;
			}
		});		
		
		txtPort = new JTextField();
		txtPort.setBounds(201, 112, 58, 20);
		contentPanel.add(txtPort);
		
		txtChannel = new JTextField();
		txtChannel.setBounds(201, 156, 191, 20);
		contentPanel.add(txtChannel);
		txtChannel.setColumns(10);
		txtChannel.getDocument().addDocumentListener(new SimpleDocumentListener(){
			public void update(DocumentEvent e) {
				selected.setChannel(txtChannel.getText());
				changes = true;
			}
		});		
		
		txtQueue = new JTextField();
		txtQueue.setBounds(201, 200, 191, 20);
		contentPanel.add(txtQueue);
		txtQueue.setColumns(10);
		txtQueue.getDocument().addDocumentListener(new SimpleDocumentListener(){
			public void update(DocumentEvent e) {
				selected.setName(txtQueue.getText());
				changes = true;
			}
		});	
		

		sldPollTime = new JSlider();
		sldPollTime.setPaintLabels(true);
		sldPollTime.setMajorTickSpacing(5);
		sldPollTime.setValue(5000);
		sldPollTime.setSnapToTicks(true);
		sldPollTime.setPaintTicks(true);
		sldPollTime.setMinorTickSpacing(1);
		sldPollTime.setMaximum(60);
		sldPollTime.setBounds(201, 231, 200, 50);
		sldPollTime.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				selected.setPollTime(sldPollTime.getValue());
			}
		});
		contentPanel.add(sldPollTime);
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		JButton okButton = new JButton(Messages.getString("ConfigWindow.ok")); //$NON-NLS-1$
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				queues.save();
				setVisible(false);
				dispose();				
			}
		});
		okButton.setActionCommand(Messages.getString("ConfigWindow.ok")); //$NON-NLS-1$
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		
		
		JButton cancelButton = new JButton(Messages.getString("ConfigWindow.cancel")); //$NON-NLS-1$
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		cancelButton.setActionCommand(Messages.getString("ConfigWindow.cancel")); //$NON-NLS-1$
		buttonPane.add(cancelButton);
			
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{getContentPane(), queueList, lblName, txtName, contentPanel, txtHost, lblPort, txtPort, lblChannel, txtChannel, lblQueue, txtQueue, lblPollTime, sldPollTime, lblHost, btnAdd, btnRemove, buttonPane, okButton, cancelButton}));
	}
}