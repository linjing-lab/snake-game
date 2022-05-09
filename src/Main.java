
import java.awt.event.*;
import javax.swing.*;

public class Main {
	private static int id=0;	//���ѵ�¼�����û�id
	private static JLabel label_account;
	
	public static void main(String[] args) {
		JFrame frame=new JFrame();
		frame.setSize(400, 300);
		frame.setLocationRelativeTo(null);
		frame.setTitle("Snake Game By LinJing");
		frame.setResizable(false);
		frame.setLayout(null);
		
		label_account=new JLabel("�˺�:δ��¼");
		label_account.setBounds(5,5,400,30);
		frame.add(label_account);
		
		ImageIcon logo1=new ImageIcon("pic/logo.png");
		JLabel logo=new JLabel();
		logo.setIcon(logo1);
		logo.setBounds(0,40,400,70);
		frame.add(logo);

		JButton btn_game=new JButton("��ʼ��Ϸ");
		btn_game.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(id==0) {
					if(JOptionPane.showConfirmDialog(frame, "�Ƿ�򿪵�¼���棿", "δ��¼",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
						frame.setVisible(false);
						new Account(frame);
					}
				}else {
					frame.setVisible(false);
					new Game(frame,id);
				}
			}
		});
		btn_game.setBounds(frame.getWidth()/2-75,130,150,30);
		frame.add(btn_game);
		
		JButton btn_account=new JButton("�˺Ź���");
		btn_account.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				new Account(frame);
			}
		});
		btn_account.setBounds(frame.getWidth()/2-75,170,150,30);
		frame.add(btn_account);
		
		JButton btn_charts=new JButton("TOP10���а�");
		btn_charts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				new Charts(frame);
			}
		});
		btn_charts.setBounds(frame.getWidth()/2-75,210,150,30);
		frame.add(btn_charts);
		
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	//����Account
	public static void login(int i) {
		id=i;
		label_account.setText("�˺�:"+new Users().getAccount(i));
	}

}
