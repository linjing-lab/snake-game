import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Random;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Game extends JFrame implements ActionListener, KeyListener {
	enum Direction {
		up, down, left, right
	}; // ���ƶ����ĸ�����

	private int ID; // ��¼�û���id
	private Food food;
	private Snake snake;
	private JPanel gamePanel; // ������Ϸ�����
	private ImageIcon fail; // ��Ϸʧ�ܵ�ͼƬ
	private ImageIcon pause; // ��Ϸ��ͣ��ͼƬ
	private JLabel label_maxscore; // ��ʾ��߷�
	private JLabel label_time; // ��ʾ����
	private JLabel label_score; // ��ʾ����
	private JLabel label_len; // ��ʾ�߳���
	private JLabel label_account; // ��ʾ��ǰ�˺�
	private JLabel label_audioname; // ��ʾ��ǰ���ŵ�����
	private int score = 0; // ʵʱ����ͳ��
	private Timer timer; // Timer���ʵ��������ѭ��������Ϸ����ѭ������actionPerformed
	private final int speedSlow = 100; // �������ƶ����ٶ�
	private final int speedFast = 30; // ����SHIFT���߿����ƶ����ٶȣ���Ϊ��Timer��delayʱ�䣬���Ը�ֵԽС���ٶ�Խ��
	private final int gamePanelWidth = 875; // ��Ϸ����Ŀ�
	private final int gamePanelHeight = 700; // ��Ϸ����ĸߣ�874*700�Ŀ�ߣ���������35*28��
	private boolean isGameStart = false; // ��Ϸ״̬�ļ�¼
	private boolean isGameRunning = false;
	private boolean isGameFailed = false;
	private Users users;
	
	private String[] audioFiles=new String[]{"audio/wav1.wav", "audio/wav2.wav", "audio/wav3.wav"};

	private AudioPlayWave audioPlayer; // ��Ƶ������

	// ���Ƹ����
	Game(JFrame mainFrame, int id) {
		ID = id;
		this.setSize(gamePanelWidth + 180, gamePanelHeight + 50);
		this.setLayout(null);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setTitle("Snake Game By ����ܰ");
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				timer.stop();
				if (audioPlayer != null) {
					audioPlayer.stopAudio();
				}
				mainFrame.setVisible(true);
			}
		});

		gamePanel = new GamePanel();
		gamePanel.setBounds(0, 0, gamePanelWidth, gamePanelHeight);
		gamePanel.setBackground(new Color(230, 230, 230));
		this.add(gamePanel);

		JPanel panel_account = new JPanel();
		panel_account.setBounds(gamePanelWidth + 10, 0, 150, 100);
		panel_account.setBorder(BorderFactory.createTitledBorder("Account"));
		panel_account.setLayout(new BoxLayout(panel_account, BoxLayout.Y_AXIS));
		this.add(panel_account);
		JLabel label_id = new JLabel("ID:" + ID);
		panel_account.add(label_id);
		label_account = new JLabel("�û���:");
		panel_account.add(label_account);
		label_maxscore = new JLabel("��߷�:");
		panel_account.add(label_maxscore);
		label_time = new JLabel("����:");
		panel_account.add(label_time);

		JPanel panel_game = new JPanel();
		panel_game.setBounds(gamePanelWidth + 10, 100, 150, 60);
		panel_game.setBorder(BorderFactory.createTitledBorder("Game"));
		panel_game.setLayout(new BoxLayout(panel_game, BoxLayout.Y_AXIS));
		this.add(panel_game);
		label_score = new JLabel("����:0");
		panel_game.add(label_score);
		label_len = new JLabel("����:3");
		panel_game.add(label_len);

		JComboBox comboBox_playerSkin = new JComboBox();
		String[] select = { "��ɫ1", "��ɫ2", "��ɫ3" };
		comboBox_playerSkin.setModel(new DefaultComboBoxModel(select));
		comboBox_playerSkin.setBounds(gamePanelWidth + 10, 200, 150, 20);
		comboBox_playerSkin.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				snake.changeSkin(comboBox_playerSkin.getSelectedIndex() + 1); // +1����Ϊ��Դ�ļ������ͼƬ����ű����ﶼ��1
				repaint();
				gamePanel.requestFocus();
			}
		});
		this.add(comboBox_playerSkin);

		JComboBox comboBox_foodSkin = new JComboBox();
		String[] select2 = { "ʳ��1", "ʳ��2", "ʳ��3", "ʳ��4", "ʳ��5", "ʳ��6" };
		comboBox_foodSkin.setModel(new DefaultComboBoxModel(select2));
		comboBox_foodSkin.setBounds(gamePanelWidth + 10, 225, 150, 20);
		comboBox_foodSkin.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				food.changeSkin(comboBox_foodSkin.getSelectedIndex() + 1); // +1����Ϊ��Դ�ļ������ͼƬ����ű����ﶼ��1
				repaint();
				gamePanel.requestFocus(); // ���»�ȡ�����Լ�������
			}
		});
		this.add(comboBox_foodSkin);

		JButton btn_begin = new JButton("��ʼ��Ϸ");
		btn_begin.setBounds(gamePanelWidth + 10, 250, 150, 30);
		btn_begin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isGameStart && !isGameFailed && !isGameRunning) { // ��ǰΪ��ͣ״̬������ͣ�ָ���Ϸ������
					gamePanel.requestFocus(); // ��ȡ�����Դ˼������̰���
					timer.start();
					isGameRunning = true;
				} else { // ��Ϸ�տ�ʼ
					gamePanel.requestFocus();
					snake.reborn();
					score = 0;
					timer.start();
					isGameStart = true;
					isGameRunning = true;
					isGameFailed = false;
				}
			}
		});
		this.add(btn_begin);

		ImageIcon helpicon = new ImageIcon("pic/help.png");
		JLabel help = new JLabel();
		help.setBounds(gamePanelWidth + 10, 300, 150, 250);
		help.setIcon(helpicon);
		this.add(help);

		JComboBox cmb = new JComboBox(); // ����JComboBox
		cmb.setBounds(gamePanelWidth + 10, 550, 150, 20);
		cmb.addItem("--��ѡ��--"); // �������б������һ��
		cmb.addItem("����1");
		cmb.addItem("����2");
		cmb.addItem("����3");
		this.add(cmb);

		JButton btn_startAudio = new JButton("��ʼ���ű�������");
		btn_startAudio.setBounds(gamePanelWidth + 10, 580, 150, 30);
		this.add(btn_startAudio);
		
		JButton btn_stopAudio = new JButton("ֹͣ���ű�������");
		btn_stopAudio.setBounds(gamePanelWidth + 10, 620, 150, 30);
		this.add(btn_stopAudio);
		
		JLabel label_volume=new JLabel("������");
		label_volume.setBounds(gamePanelWidth + 10, 660, 150, 20);
		this.add(label_volume);
		
		JSlider slider = new JSlider();  
		slider.setBounds(gamePanelWidth + 10, 680, 150, 20);
		slider.setMinimum(0);
		slider.setMaximum(100);
		slider.setValue(100);
		this.add(slider);
		
		cmb.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				gamePanel.requestFocus();
			}
		});

		btn_startAudio.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
					gamePanel.requestFocus();
					int index=cmb.getSelectedIndex();
					if(index==0) {
						JOptionPane.showMessageDialog(null, "δѡ������");
						return;
					}

                    File file = new File(audioFiles[index-1]);
                    if(audioPlayer!=null) {
                    	audioPlayer.stopAudio();
                    }
                    audioPlayer=new AudioPlayWave(file);
                    audioPlayer.run();
			}
		});

		btn_stopAudio.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				gamePanel.requestFocus();
				if (audioPlayer != null) {
					audioPlayer.stopAudio();
				}
			}
		});
		
		slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				int value = slider.getValue();
				if (audioPlayer != null) {
					audioPlayer.setVolume(value);
				}
			}
		});

		

		init(); // ��ʼ��Ϸ����
	}

	// ��ʼ��
	private void init() {
		snake = new Snake();
		food = new Food();
		score = 0;
		users = new Users();
		showAccountInformation();
		gamePanel.setFocusable(true);
		gamePanel.addKeyListener(this);
		timer = new Timer(speedSlow, this); // һ��ʼ��δ����SHIFT�ߵ��ٶ�Ӧ��ΪspeedSlow
		fail = new ImageIcon("pic/fail.png");
		pause = new ImageIcon("pic/pause.png");
	}

	// ��ʾ�û���Ϣ
	private void showAccountInformation() {
		label_account.setText("�û���:" + users.getAccount(ID));
		label_maxscore.setText("��߷�:" + users.getMaxScore(ID));
		label_time.setText("����:" + users.getTime(ID));
	}

	// ��Ϸʧ�ܺ���߼�����
	private void failed() {
		users.modifyMaxScoreAndTime(ID, users.getMaxScore(ID) > score ? users.getMaxScore(ID) : score,
				users.getTime(ID) + 1);
		showAccountInformation();
		isGameStart = false;
		isGameRunning = false;
		isGameFailed = true;
	}		
	
	private class GamePanel extends JPanel {
		// ��дpaint���������Ի�����Ϸ����
		public void paint(Graphics g) {
			super.paint(g);

			// ������Ϸ����
			g.setColor(new Color(250, 250, 250));
			for (int i = 0; i < gamePanelWidth; i += 25)
				g.drawLine(i, 0, i, gamePanelHeight);
			for (int i = 0; i < gamePanelHeight; i += 25)
				g.drawLine(0, i, gamePanelWidth, i);

			// ����ʳ�food.food��food�������foodͼƬ
			food.food.paintIcon(this, g, food.x - (food.food.getIconWidth() / 2 - 25 / 2),
					food.y - (food.food.getIconHeight() / 2 - 25 / 2));

			// ��������
			for (int i = 1; i < snake.len; i++) {
				snake.body.paintIcon(this, g, snake.x[i], snake.y[i]);
			}
			// ������ͷ
			switch (snake.direction) {
			case up:
				snake.up.paintIcon(this, g, snake.x[0] - (snake.up.getIconWidth() / 2 - 25 / 2),
						snake.y[0] - (snake.up.getIconHeight() - 25) - 3);
				break;
			case down:
				snake.down.paintIcon(this, g, snake.x[0] - (snake.down.getIconWidth() / 2 - 25 / 2), snake.y[0] + 3);
				break;
			case left:
				snake.left.paintIcon(this, g, snake.x[0] - (snake.left.getIconWidth() - 25) - 3,
						snake.y[0] - (snake.left.getIconHeight() / 2 - 25 / 2));
				break;
			case right:
				snake.right.paintIcon(this, g, snake.x[0] + 3, snake.y[0] - (snake.right.getIconHeight() / 2 - 25 / 2));
				break;
			}

			if (isGameFailed) {
				fail.paintIcon(this, g, 0, 150);
				g.setFont(new Font("arial",Font.BOLD,40));
				g.drawString("Your Score is:"+score, 300, 480);
			}

			if (isGameStart && !isGameRunning) {
				pause.paintIcon(this, g, 0, 150);
			}
		}
	}

	private class Snake {
		int[] x = new int[gamePanelWidth * gamePanelHeight];
		int[] y = new int[gamePanelWidth * gamePanelHeight];
		int len;
		Direction direction;
		ImageIcon up = new ImageIcon("skin/skin1/up.png");
		ImageIcon down = new ImageIcon("skin/skin1/down.png");
		ImageIcon left = new ImageIcon("skin/skin1/left.png");
		ImageIcon right = new ImageIcon("skin/skin1/right.png");
		ImageIcon body = new ImageIcon("skin/skin1/body.png");

		Snake() {
			x[2] = 0;
			x[1] = 25;
			x[0] = 50;
			y[2] = y[1] = y[0] = 25;
			len = 3;
			direction = direction.right;
		}

		public void reborn() {
			x[2] = 0;
			x[1] = 25;
			x[0] = 50;
			y[2] = y[1] = y[0] = 25;
			len = 3;
			direction = direction.right;
		}

		public void changeSkin(int i) {
			up = new ImageIcon("skin/skin" + i + "/up.png");
			down = new ImageIcon("skin/skin" + i + "/down.png");
			left = new ImageIcon("skin/skin" + i + "/left.png");
			right = new ImageIcon("skin/skin" + i + "/right.png");
			body = new ImageIcon("skin/skin" + i + "/body.png");
		}

	}

	private class Food {
		Random random = new Random();
		ImageIcon food = new ImageIcon("skin/skin1/food.png");
		int x;
		int y;

		Food() {
			reborn();
		}

		// �������ʳ���λ��
		public void reborn() {
			x = random.nextInt(875 / 25) * 25;
			y = random.nextInt(700 / 25) * 25;

			for (int k = 0; k < 5; k++) {
				boolean flag = true;
				for (int i = 0; i < snake.len; i++) {
					if (x == snake.x[i] && y == snake.y[i]) {
						x = random.nextInt(875 / 25) * 25;
						y = random.nextInt(700 / 25) * 25;
						flag = false;
						break;
					}
				}
				if (flag)
					break;
			}
		}

		public void changeSkin(int i) {
			food = new ImageIcon("skin/skin" + i + "/food.png");
		}
	}

	// ʵ��ActionListener�ӿڵ�actionPerformed
	public void actionPerformed(ActionEvent e) {

		for (int i = snake.len; i > 0; i--) {
			snake.x[i] = snake.x[i - 1];
			snake.y[i] = snake.y[i - 1];
		}

		switch (snake.direction) {
		case up:
			snake.y[0] -= 25;
			break;
		case down:
			snake.y[0] += 25;
			break;
		case left:
			snake.x[0] -= 25;
			break;
		case right:
			snake.x[0] += 25;
			break;
		}

		if (snake.x[0] == food.x && snake.y[0] == food.y) {
			score++;
			snake.len++;
			food.reborn();
		}

		for (int i = 1; i < snake.len; i++) {
			if (snake.x[0] == snake.x[i] && snake.y[0] == snake.y[i]) {
				failed();
				timer.stop();
			}
		}

		if (snake.x[0] < 0 || snake.x[0] > gamePanelWidth - 25 || snake.y[0] < 0 || snake.y[0] > gamePanelHeight - 25) {
			failed();
			timer.stop();
		}

		label_score.setText("����:" + score);
		label_len.setText("����:" + snake.len);

		gamePanel.repaint();
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		int keyCode = arg0.getKeyCode();

		if (isGameRunning) {
			switch (keyCode) {
			case KeyEvent.VK_UP:
				snake.direction = (snake.direction == Direction.down) ? Direction.down : Direction.up;
				break;
			case KeyEvent.VK_DOWN:
				snake.direction = (snake.direction == Direction.up) ? Direction.up : Direction.down;
				break;
			case KeyEvent.VK_LEFT:
				snake.direction = (snake.direction == Direction.right) ? Direction.right : Direction.left;
				break;
			case KeyEvent.VK_RIGHT:
				snake.direction = (snake.direction == Direction.left) ? Direction.left : Direction.right;
				break;
			}

			if (keyCode == KeyEvent.VK_SHIFT)
				timer.setDelay(speedFast);
		}

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		int keyCode = arg0.getKeyCode();

		if (keyCode == KeyEvent.VK_SHIFT)
			timer.setDelay(speedSlow);

		if (isGameStart) {
			if (keyCode == KeyEvent.VK_SPACE) {
				if (timer.isRunning()) {
					repaint();
					timer.stop();
					isGameRunning = false;
				} else {
					timer.start();
					isGameRunning = true;
				}

			}
		}

		if (keyCode == KeyEvent.VK_R) {
			gamePanel.requestFocus();
			snake.reborn();
			score = 0;
			timer.start();
			isGameStart = true;
			isGameRunning = true;
			isGameFailed = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
}
