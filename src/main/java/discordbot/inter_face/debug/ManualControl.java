package discordbot.inter_face.debug;

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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import discordbot.Element119;
import discordbot.utils.Functions;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ManualControl {

	public volatile static boolean commandToggle = true;
	
	public ManualControl(boolean v, MessageReceivedEvent event) {
		new GraphicsInterface(event).setVisible(v);
	}
	
	private class GraphicsInterface extends JFrame {
		
		public GraphicsInterface(MessageReceivedEvent event) {
			super(Element119.class.getSimpleName() + " Control Panel");
			Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
			this.setSize(resolution.width / 3, resolution.height / 3);//int div, will be slightly smaller than 1/3 the screen resolution
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
			this.getContentPane().setLayout(new GridLayout(4, 2));
			JTextField inventory = new JTextField(),
					guildID = new JTextField();
			this.getContentPane().add(new JPanel().add(inventory));
			this.getContentPane().add(new JPanel().add(guildID));
			JButton send = new JButton("Send to Channel");
			send.addActionListener(e -> {
				Functions.Messages.sendMessage(Element119.mainJDA.getTextChannelById(guildID.getText()), inventory.getText());
			});
			this.getContentPane().add(new JPanel().add(send));
			JButton settings = new JButton("Settings");
			settings.addActionListener($0 -> {
				JFrame menu = new JFrame("Settings Menu");
				menu.setSize(resolution.width / 4, resolution.height / 4);
				menu.setLocationRelativeTo(null);
				menu.getContentPane().setLayout(new GridLayout(4,4));
				
				menu.getContentPane().add(new JPanel().add(new JLabel("Server Target ID:")));
				JTextField server = new JTextField();
				menu.getContentPane().add(new JPanel().add(server));
				menu.getContentPane().add(new JPanel()); //null panel
				
				menu.getContentPane().add(new JPanel().add(new JLabel("Change Nickname")));
				JTextField newNickname = new JTextField();
				menu.getContentPane().add(new JPanel().add(newNickname));
				JButton changeNickname = new JButton("Confirm");
				changeNickname.addActionListener($1 -> {
					event.getJDA().getGuildById(server.getText()).getSelfMember().modifyNickname(newNickname.getText()).complete();
				});
				menu.getContentPane().add(new JPanel().add(changeNickname));
				
				menu.setVisible(true);
			});
			this.getContentPane().add(settings);
			
			
			JTextField messageID = new JTextField();
			
			this.getContentPane().add(new JPanel().add(messageID));
			JButton sendReply = new JButton("Reply to Message");
			sendReply.addActionListener($1 -> {
				Element119.mainJDA.getTextChannelById(guildID.getText()).retrieveMessageById(messageID.getText()).complete().reply(inventory.getText()).queue();
			});
			this.getContentPane().add(new JPanel().add(sendReply));
		}
	}
}