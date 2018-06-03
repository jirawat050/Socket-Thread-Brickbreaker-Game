/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package brickbracker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author macbook
 */
public class client {

    Socket socket;
    InetAddress host;
    private BufferedReader in;
    private PrintWriter out;

    client() {
        initui();

    }

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 1978);
        client c = new client();
        System.out.println("Client connect");

    }

    public void initui() {
        JFrame obj = new JFrame();

        gameplay game = new gameplay();
        obj.setBounds(10, 10, 700, 600);
        obj.setTitle("Breaker ball");

        obj.setResizable(false);

        obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        obj.add(game);
        obj.setVisible(true);
    }

    private static class gameplay extends JPanel implements KeyListener, ActionListener, Runnable {

        private boolean play = false;
        private int score = 0;
        private int totalBricks = 21;
        private Timer timer;
        private int delay = 8;
        private int playerX = 310;
        private int ballposX = 120;
        private int ballposY = 350;
        private int ballXdir = -1;
        private int ballYdir = -2;
        private MapGenarator map;
        // private Thread timer;

        public gameplay() {
            map = new MapGenarator(3, 8);

            addKeyListener(this);
            setFocusable(true);
            setFocusTraversalKeysEnabled(false);
            timer = new Timer(delay, this);
            timer.start();

        }

        public void paint(Graphics g) {

            g.setColor(Color.BLACK);//backgrounds
            g.fillRect(1, 1, 692, 592);
            map.draw((Graphics2D) g);
            g.setColor(Color.RED);//borders
            g.fillRect(0, 0, 3, 592);
            g.fillRect(0, 0, 692, 3);
            g.fillRect(691, 0, 3, 592);
            //score
            g.setColor(Color.white);
            g.setFont(new Font("serif", Font.BOLD, 25));
            g.drawString("" + score, 590, 30);

            g.setColor(Color.green);//paddle
            g.fillRect(playerX, 550, 100, 8);
            g.setColor(Color.blue);//ball
            g.fillOval(ballposX, ballposY, 20, 20);
            if (ballposY > 570) {
                play = false;
                ballXdir = 0;
                ballYdir = 0;
                g.setColor(Color.red);
                g.setFont(new Font("serif", Font.BOLD, 25));
                g.drawString("Game over scores::" + score, 190, 300);
            }

            g.dispose();

        }

        @Override
        public void keyTyped(KeyEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                if (playerX >= 600) {
                    playerX = 600;
                } else {
                    moveRight();
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {

                if (playerX < 10) {
                    playerX = 10;
                } else {
                    moveLeft();
                }

            }
        }

        public void moveRight() {
            play = true;
            playerX += 20;

        }

        public void moveLeft() {
            play = true;
            playerX -= 20;

        }

        @Override
        public void keyReleased(KeyEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            timer.start();
            if (play) {
                if (new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX, 550, 100, 8))) {
                    ballYdir = -ballYdir;
                }
                A:
                for (int i = 0; i < map.map.length; i++) {
                    for (int j = 0; j < map.map[0].length; j++) {
                        if (map.map[i][j] > 0) {
                            int brickX = j * map.brickWidth + 80;
                            int brickY = i * map.brickHeight + 80;
                            int brickwidth = map.brickWidth;
                            int brickHeight = map.brickHeight;
                            Rectangle rect = new Rectangle(brickX, brickY, brickwidth, brickHeight);
                            Rectangle ballRect = new Rectangle(ballposX, ballposY, 20, 20);
                            Rectangle brickRect = rect;
                            if (ballRect.intersects(brickRect)) {
                                map.setBrickValue(0, i, j);
                                totalBricks--;
                                score += 5;
                                if (score >= 10) {
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                if (ballposX + 19 <= brickRect.x || ballposX + 1 >= brickRect.x + brickRect.width) {
                                    ballXdir = -ballXdir;
                                } else {
                                    ballYdir = -ballYdir;
                                }
                                break A;
                            }

                        }
                    }
                }
                ballposX += ballXdir;
                ballposY += ballYdir;
                if (ballposX < 0) {
                    ballXdir = -ballXdir;
                }
                if (ballposY < 0) {
                    ballYdir = -ballYdir;
                }
                if (ballposX > 670) {
                    ballXdir = -ballXdir;
                }
            }
            repaint();
        }

        PopupMenu paint() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void run() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }
}
