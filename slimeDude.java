import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class slimeDude extends JPanel implements Runnable, KeyListener, MouseListener {

	Thread thread;
	int FPS = 60;
	int screenWidth = 700;
	int screenHeight = 900;
	Rectangle player = new Rectangle(0, 0, 30, 30);
	Rectangle[] walls = new Rectangle[5];
	Image[] backgrounds = new Image[5];
	boolean jump, left, right, win;
	double speed = 5;
	double jumpSpeed = 15;
	double xVel = 0;
	double yVel = 0;
	double gravity = 0.8;
	int mouseclickx = 0;
	int mouseclicky = 0;
	int mouseclickreleasex = 0;
	int mouseclickreleasey = 0;
	int level = 0;
	boolean airborne = true;



	public slimeDude() {
		setPreferredSize(new Dimension(screenWidth, screenHeight));
		setVisible(true);

		jump = false;
		left = false;
		right = false;
		win = false;
		addMouseListener(this);
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseclickx = e.getX();
		mouseclicky = e.getY();
		//System.out.println("press" + mouseclickx + " " + mouseclicky);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseclickreleasex = e.getX();
		mouseclickreleasey = e.getY();
		System.out.println("release" + mouseclickreleasex + " " + mouseclickreleasey);
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
	
	@Override
	public void run() {
		initialize();
		while (level < 5) {
			if (level == 0) {
				System.out.println("");
				if (mouseclickx > 25 && mouseclickx < 335 && mouseclicky > 632 && mouseclicky < 693
				&& mouseclickreleasex > 25 && mouseclickreleasex < 335 && mouseclickreleasey > 632 && mouseclickreleasey < 693)
					level++; 
					//System.out.println("hi");
			}
			else {
				initialize();
				move();
				for (int i = 0; i < walls.length; i++)
					checkCollision(walls[i]);
				keepInBound();
				this.repaint();
				try {
					Thread.sleep(1000/FPS);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void initialize() {
		for (int i = 0; i < backgrounds.length; i++)//gets all the backgrounds
			//System.out.println("got img");
			backgrounds[i] = Toolkit.getDefaultToolkit().getImage("background" + i + ".gif");
		//backgrounds[0]= Toolkit.getDefaultToolkit().getImage("background"+1+".gif");
		if (level == 1) {
			walls[0] = new Rectangle(200, 750, 50, 100);
			walls[1] = new Rectangle(300, 40, 40, 100);
			walls[2] = new Rectangle(450, 100, 80, 35);
			walls[3] = new Rectangle(60, 60, 15, 15);
			walls[4] = new Rectangle(250, 350, 150, 200);
		}
		else if (level == 2)
			walls[0] = new Rectangle(200, 200, 100, 100);
		else if (level == 3)
			walls[0] = new Rectangle(200, 200, 200, 200);
		else
			for (int e = 0; e < walls.length; e++)
				walls[e]= new Rectangle(0, 0, 0, 0);

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(backgrounds[level], 0, 0, screenWidth, screenHeight, this);
		g2.setColor(Color.GRAY);
		if (level >= 1) {
			for (int i = 0; i < walls.length; i++)
				g2.fill(walls[i]);
			g2.setColor(Color.RED);
			g2.fill(player);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_A) {
			left = true;
			right = false;
		}
		else if (key == KeyEvent.VK_D) {
			right = true;
			left = false;
		}
		else if (key == KeyEvent.VK_SPACE)
			jump = true;
		else if (key == KeyEvent.VK_E)
			win = true;
		winner();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_A)
			left = false;
		else if (key == KeyEvent.VK_D)
			right = false;
		else if (key == KeyEvent.VK_SPACE)
			jump = false;
	}

	void move() {
		if (left)
			xVel = -speed;
		else if (right)
			xVel = speed;
		else
			xVel = 0;

		if (airborne)
			yVel -= gravity;
		else if (jump) {
			airborne = true;
			yVel = jumpSpeed;
		}

		if (player.x < 870)
			airborne = true;

		player.x += xVel;
		player.y -= yVel;
	}

	void keepInBound() {
		if (player.x < 0)
			player.x = 0;
		else if (player.x > screenWidth - player.width)
			player.x = screenWidth - player.width;

		if (player.y < 0) {
			player.y = 0;
			yVel = 0;
		}
		else if (player.y > screenHeight - player.height) {
			player.y = screenHeight - player.height;
			airborne = false;
			yVel = 0;
		}
	}

	void checkCollision(Rectangle wall) {
		if (player.intersects(wall)) {
			//System.out.println("collision");
			double left1 = player.getX();
			double right1 = player.getX() + player.getWidth();
			double top1 = player.getY();
			double bottom1 = player.getY() + player.getHeight();
			double left2 = wall.getX();
			double right2 = wall.getX() + wall.getWidth();
			double top2 = wall.getY();
			double bottom2 = wall.getY() + wall.getHeight();


			if (right1 > left2 && left1 < left2 && right1 - left2 <= 5) {
				player.x = wall.x - player.width;
				airborne = true;
				//System.out.println("left");
			}
			else if (left1 < right2 && right1 > right2 && right2 - left1 <= 5) {
				player.x = wall.x + wall.width;
				airborne = true;
				//System.out.println("right");
			}
			else if (bottom1 > top2 && top1 < top2) {
				player.y = wall.y - player.height;
				airborne = false;
				//System.out.println("top");
			}
			else if (top1 < bottom2 && bottom1 > bottom2) {
				player.y = wall.y + wall.height;
				airborne = true;
				yVel/=3;
				//System.out.println("bottom");
			}
		}
	}

	public void winner () {
		if (player.x >= 600 && win == true) {
			level++;
			player.y = 900 - player.height;
			player.x = 0;
			win = false;
			airborne = true;
		}
		else
			win = false;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame ("Jump Dude");
		slimeDude myPanel = new slimeDude ();
		frame.add(myPanel);
		frame.addKeyListener(myPanel);
		frame.setVisible(true);
		frame.pack();
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
