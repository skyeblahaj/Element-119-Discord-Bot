package discordbot.inter_face;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import discordbot.Element119;
import discordbot.utils.Functions;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ManualControl {

	public static boolean commandToggle = true;
	
	public ManualControl(boolean v, MessageReceivedEvent event) {
		new GraphicsInterface(event).setVisible(v);
	}
	
	private class GraphicsInterface extends JFrame {
		private static final long serialVersionUID = 1L;
		
		public GraphicsInterface(MessageReceivedEvent event) {
			super(Element119.class.getSimpleName() + " Control Panel");
			Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
			this.setSize(resolution.width / 3, resolution.height / 3);
			this.setLocationRelativeTo(null);
			this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			this.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					commandToggle = true;
				}
			});
			try {
				this.setIconImage(ImageIO.read(new URL(Element119.mainJDA.getSelfUser().getAvatarUrl())));
			} catch (IOException e) {e.printStackTrace();}
			this.setResizable(false);
			this.getContentPane().setLayout(new GridLayout(2, 1));
			JTextField input = new JTextField(),
					guildID = new JTextField();
			this.getContentPane().add(new JPanel().add(input));
			this.getContentPane().add(new JPanel().add(guildID));
			JButton send = new JButton("Send to Channel.");
			send.addActionListener(e -> {
				Functions.Messages.sendMessage(Element119.mainJDA.getTextChannelById(guildID.getText()), input.getText());
			});
			this.getContentPane().add(new JPanel().add(send));
					
		}
	}
}