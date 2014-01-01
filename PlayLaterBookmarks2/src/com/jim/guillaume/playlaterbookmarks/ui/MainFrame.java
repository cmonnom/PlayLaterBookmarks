package com.jim.guillaume.playlaterbookmarks.ui;

import javax.swing.JFrame;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Robot;
import java.awt.Toolkit;

import javax.swing.DefaultListModel;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.AbstractListModel;

import org.apache.commons.io.FileUtils;

import com.jim.guillaume.playlaterbookmarks.Main;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;

import javax.swing.JScrollPane;
import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.Font;
import java.awt.Color;
import javax.swing.border.LineBorder;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class MainFrame extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7931929549432926609L;
	private JTextField txtAddANew;
	private JList<String> list = new JList<String>();
	private String[] searches;
	private Main main;
	
	public MainFrame() {
		this.main = Main.getMain();
		createFrame();
	}
	
	public MainFrame(String[] searches) {
		this.searches = searches;
		this.main = Main.getMain();
		createFrame();
	}
	
	private void createFrame() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		setIconImage(Toolkit.getDefaultToolkit().getImage("src/playlater.png"));
		setMinimumSize(new Dimension(Variables.WIDTH, 500)); //in case Play Later does not exist
		
		setSizeAndPos();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Bookmarks");
		getContentPane().setLayout(new BorderLayout(5, 5));
		
		txtAddANew = new JTextField();
		txtAddANew.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				txtAddANew.setText(Variables.ADD_NEW_SEARCH);
			}
		});
		txtAddANew.setForeground(Color.GRAY);
		txtAddANew.setFont(new Font("Candara", Font.PLAIN, 12));
		txtAddANew.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (Variables.ADD_NEW_SEARCH.equals(txtAddANew.getText())) {
					txtAddANew.setText("");
					txtAddANew.setForeground(Color.BLACK);
				}
			}
		});
		txtAddANew.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					addSearchToList(txtAddANew.getText());
					txtAddANew.setForeground(Color.GRAY);
					txtAddANew.setText("");
				}
				else if (Variables.ADD_NEW_SEARCH.equals(txtAddANew.getText())) {
					txtAddANew.setText("");
					txtAddANew.setForeground(Color.BLACK);
				}
			}
		});
		txtAddANew.setText(Variables.ADD_NEW_SEARCH);
		getContentPane().add(txtAddANew, BorderLayout.NORTH);
		txtAddANew.setColumns(10);

		createScrollList();
		createPopupMenu();
		
		list.setModel(new DefaultListModel<String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 5046456795576410915L;
			String[] values = new String[] {"Click to send a search to PlayLater.", "Right-click to edit this list.", "Make sure to click on the", "PlayLater's searchbox first", "before using a bookmark!"};
			public int getSize() {
				return values.length;
			}
			public String getElementAt(int index) {
				return values[index];
			}
		});
		
		if (searches.length > 0) {
			list.setListData(searches);
		}
	}
	
	/**
	 * Try to set position relative to PlayLater if it exists
	 */
	private void setSizeAndPos() {
		int[] rect = getPlayLaterRect();
		int topX = rect[0];
		int topY = rect[1];
		int height = rect[3] - topY;
		setMinimumSize(new Dimension(Variables.WIDTH, Variables.HEIGHT));
		setPreferredSize(new Dimension(Variables.WIDTH, height));
		setSize(new Dimension(Variables.WIDTH, height));
		if (topX >= Variables.WIDTH) { //prevent frame to go out of the screen
			setLocation(topX - Variables.WIDTH, topY);
		}
	}
	
	private void addSearchToList(String search) {
		main.addSearchToList(search); //add to the file
		updateList();
	}
	
	private void deleteSearchFromList(int index) {
		main.deleteSearchFromList(index);
		updateList();
	}
	
	private void deleteAllSearchesFromList() {
		main.deleteAllSearchesFromList();
		updateList();
	}
	
	private void updateList() {
		searches = main.listToArray();
		list.setListData(searches);
	}
	
	private void createPopupMenu() {
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem deleteItem = new JMenuItem("Delete");
		JMenuItem deleteAllItem = new JMenuItem("Delete All");
		
		deleteItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				deleteSearchFromList(list.getSelectedIndex());
			}
			
		});
		deleteAllItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				deleteAllSearchesFromList();
			}
			
		});
		popupMenu.add(deleteItem);
		popupMenu.add(deleteAllItem);
		MouseListener popupListener = new PopupListener(popupMenu);
        list.addMouseListener(popupListener);
	}
	
	private void createScrollList() {
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		panel.setBackground(new Color(234,234,234));

		
		JScrollPane scrollPane_1 = new JScrollPane();
		panel.add(scrollPane_1);
		scrollPane_1.setViewportView(list);
		
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
					String selectedValue = list.getSelectedValue();
					if (SwingUtilities.isLeftMouseButton(e)) {
						if (selectedValue != null && selectedValue.length() > 0) {
							if (getPlayLaterWindowToFront()) {
//								test();
								copyValueToPlayLater(selectedValue);
							}
						}
					}
			}
		});
		list.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int index = list.locationToIndex(list.getMousePosition());
				list.setSelectedIndex(index);
			}
		});
		list.setFont(new Font("Candara", Font.PLAIN, 12));
		

	}
	
	/**
	 * Bring PlayLater to the front and activate the window.
	 * 
	 * @return false if PlayLater cannot be found
	 */
	private boolean getPlayLaterWindowToFront() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	      HWND hWnd = User32.instance.FindWindow(null,
		            "PlayLater");
	      if (hWnd != null) {
	    	  User32.instance.SetForegroundWindow(hWnd);
	    	  
	    	  return true;
	      }
	      return false;
	      
	}
	
	private void test() {
		User321.INSTANCE.EnumWindows(new User321.WNDENUMPROC() {
            public boolean callback(HWND hWnd, Pointer userData) { // this will be called for each parent window found by EnumWindows(). the hWnd parameter is the HWND of the window that was found.
                byte[] textBuffer = new byte[512];
                final int[] plRect = getPlayLaterRect();
                User321.INSTANCE.GetWindowTextA(hWnd, textBuffer, 512);
                String wText = Native.toString(textBuffer);
//                if ("MediaMall".equals(wText)) {
//                	System.out.println("Window found: " + wText);
                	final String finalText = wText;
                	// now call EnumChildWindows() giving the previously found parent window as the first parameter
                	User321.INSTANCE.EnumChildWindows(hWnd, new User321.WNDENUMPROC() {
                		public boolean callback(HWND hWnd, Pointer userData) { // this is called for each child window that EnumChildWindows() finds - just like before with EnumWindows().
                			byte[] textBuffer = new byte[512];
                			User321.INSTANCE.GetClassNameA(hWnd, textBuffer, 512);
//                			if ("Search Box".equals(new String(textBuffer).trim())) {
//                				System.out.println(User321.INSTANCE.SetForegroundWindow(hWnd));
                				User321.INSTANCE.SetFocus(hWnd);
                				int[] rect = {0,0,0,0};
                				byte[] contentBuffer = new byte[512];
                				User321.INSTANCE.GetWindowText(hWnd, contentBuffer, 512);
                				User321.INSTANCE.SetForegroundWindow(hWnd);
                				System.out.println(new String(textBuffer).trim());
                				try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
//                				Robot robot;
//								try {
//									if (rect != null) {
//										System.out.println(new String(textBuffer).trim());
//										robot = new Robot();
////										robot.mouseMove(plRect[0] + rect[0], plRect[1] + rect[1]);
//									}
//								} catch (AWTException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//                			}
                			return true;
                		}
                	}
                	, null);
//                }
                return true;
            }
        }, null);
		
		
	}
	
	private int[] getPlayLaterRect() {
		 HWND hWnd = User32.instance.FindWindow(null,
		            "PlayLater");
		 int[] rect = {0, 0, Variables.WIDTH, Variables.HEIGHT};
		 
		 User32.instance.GetWindowRect(hWnd, rect);
		 return rect;
		 
	}
	
	public interface User32 extends W32APIOptions { 
	    // Method declarations, constant and structure definitions go here
	      User32 instance = (User32) Native.loadLibrary("user32", User32.class,
	              DEFAULT_OPTIONS);

	        HWND FindWindow(String winClass, String title);
//			void EnumChildWindows(
//					HWND findWindow,
//					com.jim.guillaume.playlaterbookmarks.ui.MainFrame.User32.WNDENUMPROC wndenumproc,
//					Object object);
			HWND GetForegroundWindow();
			Boolean SetForegroundWindow(HWND handle);
	        int GetWindowRect(HWND handle, int[] rect);
	      
	        boolean EnumWindows(WNDENUMPROC lpEnumFunc, Pointer arg);

	        boolean PostMessage(Pointer hwndParent, String msg, String wParam, String lParam);
	        Pointer FindWindowEx(Pointer hwndParent, String hwndChildAfter, String lpszClass, String lpszWindow);

	        int GetWindowTextA(Pointer hWnd, byte[] lpString, int nMaxCount);
	        
	        interface WNDENUMPROC extends StdCallCallback {
	            boolean callback(Pointer hWnd, Pointer arg);
	        }
	}
	
	/**
	 * Put the selected value in the Clipboard and paste it
	 * in the PlayLater search box.
	 * The box needs to have been clicked in prior to selecting
	 * a value
	 * @param value
	 */
	private void copyValueToPlayLater(String value) {
		try {
			
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		    StringSelection stringSelection = new StringSelection(value);
		    clipboard.setContents(stringSelection, null);

		    Robot robot = new Robot();
		    robot.keyPress(KeyEvent.VK_CONTROL);
		    robot.keyPress(KeyEvent.VK_A);
		    robot.keyRelease(KeyEvent.VK_A);
		    robot.keyRelease(KeyEvent.VK_CONTROL);
		    robot.keyRelease(KeyEvent.VK_DELETE);
		    robot.keyRelease(KeyEvent.VK_DELETE);
		    
		    
		    robot.keyPress(KeyEvent.VK_CONTROL);
		    robot.keyPress(KeyEvent.VK_V);
		    robot.keyRelease(KeyEvent.VK_V);
		    robot.keyRelease(KeyEvent.VK_CONTROL);
		    
		    robot.keyPress(KeyEvent.VK_ENTER);
		    robot.keyRelease(KeyEvent.VK_ENTER);
			
		} catch (AWTException e) {
			e.printStackTrace();
		} 
	}
	
	class PopupListener extends MouseAdapter {
        JPopupMenu popup;
 
        PopupListener(JPopupMenu popupMenu) {
            popup = popupMenu;
        }
 
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }
 
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }
 
        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(),
                           e.getX(), e.getY());
            }
        }
    }
	
	public interface User321 extends StdCallLibrary {
        User321 INSTANCE = (User321) Native.loadLibrary("user32", User321.class, W32APIOptions.DEFAULT_OPTIONS);

        HWND FindWindow(String lpClassName, String lpWindowName);
        int GetWindowRect(HWND handle, int[] rect);
        int SendMessage(HWND hWnd, int msg, int wParam, byte[] lParam); 
        HWND FindWindowEx(HWND parent, HWND child, String className, String window);

        boolean EnumWindows(WNDENUMPROC lpEnumFunc, Pointer arg);
        boolean EnumChildWindows(HWND parent, WNDENUMPROC callback, LPARAM info);

        interface WNDENUMPROC extends StdCallCallback {
            boolean callback(HWND hWnd, Pointer arg);
        }

        int GetWindowTextA(HWND hWnd, byte[] lpString, int nMaxCount);
        long GetWindowLong(HWND hWnd, int index);
        boolean SetForegroundWindow(HWND in);
        int GetClassNameA(HWND in, byte[] lpString, int size);
        HWND SetFocus(HWND hWnd);
        int GetWindowText(HWND in, byte[] lpString, int size);
    }

}
