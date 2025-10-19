import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class Ball extends JPanel implements ActionListener, KeyListener {
    private int ballY = 300;
    private double ballVelocity = 0;
    private double gravity = 1.0; 
    private ArrayList<Pipe> pipes;
    private int pipeWidth = 60;
    private int pipeGap = 180;
    private int pipeSpeed = 3;
    private int score = 0;
    private boolean gameOver = false;
    private Timer timer;
    private Random random;
    private long gameOverTime = 0;
    private final int RESTART_DELAY = 1000;

    private class Pipe {
        Rectangle top;
        Rectangle bottom;
        boolean passed;

        Pipe(int x, int topHeight, int bottomY) {
            top = new Rectangle(x, 0, pipeWidth, topHeight);
            bottom = new Rectangle(x, bottomY, pipeWidth, 600 - bottomY);
            passed = false;
        }
    }

    public Ball() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.CYAN);
        pipes = new ArrayList<>();
        random = new Random();
        timer = new Timer(20, this); 
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addPipe();
        timer.start();
    }

    private void addPipe() {
        int gapY = random.nextInt(300) + 150;
        pipes.add(new Pipe(800, gapY - pipeGap / 2, gapY + pipeGap / 2));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Enable Anti-Aliasing
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

       
        GradientPaint sky = new GradientPaint(0, 0, new Color(135, 206, 235),
                0, getHeight(), new Color(176, 224, 230));
        g2.setPaint(sky);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Draw ground
        g2.setColor(new Color(34, 139, 34));
        g2.fillRect(0, 580, getWidth(), 20);

        
        g2.setColor(new Color(0, 155, 0));
        for (Pipe pipe : pipes) {
            g2.fill(pipe.top);
            g2.fill(pipe.bottom);
            g2.setColor(Color.DARK_GRAY); 
            g2.draw(pipe.top);
            g2.draw(pipe.bottom);
            g2.setColor(new Color(0, 155, 0));
        }

        // Draw ball with eye
        g2.setColor(Color.YELLOW);
        g2.fillOval(100, ballY, 30, 30);
        g2.setColor(Color.BLACK);
        g2.fillOval(115, ballY + 8, 6, 6); // Eye

        
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 22));
        g2.drawString("Score: " + score, 10, 30);

        
        if (gameOver) {
            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 40));
            g2.drawString("Game Over! Score: " + score, 200, 280);
            g2.drawString("Press Space to Start " , 190, 350);

            long currentTime = System.currentTimeMillis();
            if (currentTime - gameOverTime >= RESTART_DELAY) {
                if ((currentTime / 500) % 2 == 0) {
                    g2.setColor(Color.BLACK);
                } else {
                    g2.setColor(Color.BLUE);
                }
                g2.setFont(new Font("Arial", Font.PLAIN, 20));
                g2.drawString("Press SPACE to restart", 270, 330);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            ballVelocity += gravity;
            ballY += (int) ballVelocity;

            for (Pipe pipe : pipes) {
                pipe.top.x -= pipeSpeed;
                pipe.bottom.x -= pipeSpeed;

                if (!pipe.passed && pipe.top.x + pipe.top.width < 100) {
                    score++;
                    pipe.passed = true;
                }
            }

            pipes.removeIf(pipe -> pipe.top.x + pipe.top.width < 0);

            if (pipes.isEmpty() || pipes.get(pipes.size() - 1).top.x < 800 - 300) {
                addPipe();
            }

            Rectangle ball = new Rectangle(100, ballY, 30, 30);
            for (Pipe pipe : pipes) {
                if (ball.intersects(pipe.top) || ball.intersects(pipe.bottom)) {
                    gameOver = true;
                    gameOverTime = System.currentTimeMillis();
                    timer.stop();
                }
            }

            if (ballY > 570 || ballY < 0) {
                gameOver = true;
                gameOverTime = System.currentTimeMillis();
                timer.stop();
            }
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - gameOverTime >= RESTART_DELAY) {
                    ballY = 300;
                    ballVelocity = 0;
                    pipes.clear();
                    score = 0;
                    gameOver = false;
                    addPipe();
                    timer.start();
                }
            } else {
                ballVelocity = -8; 
            }
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Ball");
        Ball game = new Ball();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}