import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.security.acl.Group;

import javax.sound.midi.Sequence;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.w3c.dom.Node;

/**
 * SwingFXWebView
 */
public class SwingFXWebView extends JPanel {

	private Stage stage;
	private WebView browser;
	private JFXPanel jfxPanel;
	private JButton swingButton;
	private WebEngine webEngine;

	public SwingFXWebView() {
		initComponents();
	}
	
	public static void main(String... args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final JFrame frame = new JFrame();

				frame.getContentPane().add(new SwingFXWebView());

				frame.setMinimumSize(new Dimension(640, 480));
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}

	private void initComponents() {

		jfxPanel = new JFXPanel();
		createScene();

		setLayout(new BorderLayout());
		add(jfxPanel, BorderLayout.CENTER);

		swingButton = new JButton();
		swingButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				webEngine.reload();
			}
		});
		swingButton.setText("Reload");

		add(swingButton, BorderLayout.SOUTH);
	}

	/**
	 * createScene
	 * 
	 * Note: Key is that Scene needs to be created and run on "FX user thread"
	 * NOT on the AWT-EventQueue Thread
	 * 
	 */
	private void createScene() {  
        PlatformImpl.startup(new Runnable() {  
            public void run() {  
                 
                stage = new Stage();  
                 
                stage.setTitle("Hello Java FX");  
                stage.setResizable(false);  
   
                Group root = new Group();  
                Scene scene = new Scene(root,80,20);  
                stage.setScene(scene);  
                 
                 
                URL url = null;  
                try{  
                    url = new URL("<a href=\"http://www.google.ie\" target=\"_blank\">http://www.google.ie</a>");  
                }catch(Exception ex){  
                    ex.printStackTrace();  
                }  
                webEngine = new WebEngine(url);  
                browser = new WebView(webEngine);  
  
                Sequence<Node> children = root.getChildren();  
                children.add(browser);                     
                 
                jfxPanel.setScene(scene);  
            }  
        });  
    }}
