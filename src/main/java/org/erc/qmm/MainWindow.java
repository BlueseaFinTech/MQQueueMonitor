package org.erc.qmm;

import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.JTabbedPane;
import javax.swing.ImageIcon;

import org.erc.qmm.config.ConfigManager;
import org.erc.qmm.config.QueueConfig;
import org.erc.qmm.i18n.Messages;
import org.erc.qmm.util.Log;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Insets;

/**
 * The Class MainWindow.
 */
public class MainWindow {

	private static Log log = Log.getLog(MainWindow.class);
			
	/** The frm queue monitor. */
	private JFrame frmQueueMonitor;

	/** The tab panel. */
	private JTabbedPane tabPanel;

	/**
	 * Launch the application.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		
		Log.configureLogger();
		
        java.awt.EventQueue.invokeLater ( new Runnable() {
	        public void run() {
	    		try{
	    			JFrame.setDefaultLookAndFeelDecorated(true);
	    			JDialog.setDefaultLookAndFeelDecorated(true);
	    			System.setProperty("sun.awt.noerasebackground", "true"); //$NON-NLS-1$ //$NON-NLS-2$
	    			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); //$NON-NLS-1$
					MainWindow window = new MainWindow();
					window.frmQueueMonitor.setVisible(true);
				} catch (Exception e) {
					log.error(e);
				}
	        }
        });
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
		loadQueues();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmQueueMonitor = new JFrame();
		frmQueueMonitor.setIconImage(Toolkit.getDefaultToolkit().getImage(MainWindow.class.getResource(Images.RECEIVE))); //$NON-NLS-1$
		frmQueueMonitor.setTitle(Messages.getString("MainWindow.title")); //$NON-NLS-1$
		frmQueueMonitor.setBounds(100, 100, 450, 363);
		frmQueueMonitor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setMargin(new Insets(0, 0, 5, 0));
		frmQueueMonitor.setJMenuBar(menuBar);
		
		JMenu mnArchivo = new JMenu(Messages.getString("MainWindow.file")); //$NON-NLS-1$
		menuBar.add(mnArchivo);
		
		JMenuItem mntmOpciones = new JMenuItem(Messages.getString("MainWindow.options")); //$NON-NLS-1$
		mntmOpciones.setIcon(new ImageIcon(MainWindow.class.getResource(Images.CONFIG))); //$NON-NLS-1$
		mntmOpciones.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					ConfigWindow config = new ConfigWindow();
					config.addChangedListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							loadQueues();
						}
					});					
					config.setModal(true);
					config.setVisible(true);
				} catch (Exception ex) {
					log.error(ex);
				}
			}
		});
		mntmOpciones.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		mnArchivo.add(mntmOpciones);
		
		JMenuItem mntmSalir = new JMenuItem(Messages.getString("MainWindow.exit")); //$NON-NLS-1$
		mntmSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mntmSalir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
		mnArchivo.add(mntmSalir);
		
		JMenu mnActions = new JMenu(Messages.getString("MainWindow.actions")); //$NON-NLS-1$
		menuBar.add(mnActions);
		
		JMenuItem mntmViewActiveQueue = new JMenuItem(Messages.getString("MainWindow.viewmessages")); //$NON-NLS-1$
		mntmViewActiveQueue.setIcon(new ImageIcon(MainWindow.class.getResource(Images.SEARCH))); //$NON-NLS-1$
		mnActions.add(mntmViewActiveQueue);
		mntmViewActiveQueue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					Component comp = tabPanel.getSelectedComponent();
					if(comp!=null && comp instanceof MonitorPanel){
						MonitorPanel panel = (MonitorPanel) comp;
						ExplorerWindow explorerWindow = new ExplorerWindow(panel.getQueue());
						explorerWindow.setVisible(true);
					}
				}catch(Exception ex){
					log.error(ex);
				}
			}
		});
		tabPanel = new JTabbedPane(JTabbedPane.TOP);
		frmQueueMonitor.getContentPane().add(tabPanel, BorderLayout.CENTER);
	}
	
	/**
	 * Load queues.
	 */
	private void loadQueues(){
		ImageIcon icon = new ImageIcon(MainWindow.class.getResource(Images.MESSAGE)); //$NON-NLS-1$
		try {
			ConfigManager config = new ConfigManager();
			
			// Stop monitors before remove panel
			if(tabPanel.getTabCount()>0){
				for (Component c :tabPanel.getComponents()){
					if (c instanceof MonitorPanel){
						MonitorPanel panel = (MonitorPanel)c;
						panel.dispose();
					}
				}
			}
			tabPanel.removeAll();
			List<QueueConfig> queues= config.loadQueues();
			for(QueueConfig queue: queues){
				if (queue.isActive()){
					tabPanel.addTab(queue.getDesc(), icon, new MonitorPanel(queue), null);
				}
			}
		} catch (Exception ex) {
			log.error(ex);
		} 
	}
}