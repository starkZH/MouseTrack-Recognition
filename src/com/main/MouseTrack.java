package com.main;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Thread.State;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.json.*;

import util.FingerPrint;

public class MouseTrack extends JWindow{//正式时改为jwindow，因为键盘可以穿透此窗口
/**
 * 鼠标轨迹识别
 * @author Xu
 * 
 * 
 * {
    "radius":260,
    "track":[
        {"trackImage":"d:/0/0.png","description":"下一张","function":"NEXT","scene":0,"direction":[[0,1],[1,0]]}
    ],
    
    参数说明：
    offset:
    	为轨迹终点时鼠标的坐标与轨迹起点时的坐标的关系，
    	0为忽略此参数，1为终坐标大于起始坐标，-1为终坐标小于起始坐标，
       	这样可以识别出轨迹的运动方向，以此对同一个轨迹图片，可以实现不同的功能。
    function:
    	预定义功能的编号
    scene:
    	场景值。在不同的场景下，同一个动作会有不同的功能。
    	0-所有场景
    	1-演讲模式
    	2-休闲娱乐模式
    direction:
    	鼠标移动方向。与上一个点的坐标比较。0-相同 ，1-大于 ，0-小于

}
 * */
	float alpha=0.56f;
	static final int TIP_WIDTH=400,TIP_HEIGHT=200,INTERVAL=6;
	Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	int width=(int)dimension.getWidth(),height=(int)dimension.getHeight(),interval=INTERVAL;
	
	JLabel track=new JLabel(),tip=new JLabel();
	BufferedImage image=new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
	BasicStroke stroke= new BasicStroke(10.0f,BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);//画笔设置
	int lastX=-1,lastY=-1,startX=-1,startY=-1;
	int minx=0,maxx=0,miny=0,maxy=0;
	public JSONArray tracks=null;
	byte[][] trackImages=null;
	static TipModal tm=new TipModal();
	public MouseTrack(JSONArray tracks) {
		this.tracks=tracks;
		minx=width;miny=height;
		 readTrackImages();//读取轨迹图片
		 newImage();
		setFocusableWindowState(true);
		setFocusable(true);
	//	this.setUndecorated(true);
		this.setBounds(0, 0, width, height);
		this.setOpacity(alpha);
		setBackground(Color.BLACK);
		setVisible(true);
		this.setAlwaysOnTop(true);
		getContentPane().setLayout(null);track.setForeground(Color.WHITE);
		
		getContentPane().add(tm.tip);
		getContentPane().add(track);
		tm.tip.setBounds((width-400)/2, (height-200), TIP_WIDTH, TIP_HEIGHT);
		tm.trackImages=trackImages;
		tm.tracks=tracks;
		track.setBounds(0, 0, width, height);
		tm.windowHeight=height;
		tm.windowWidth=width;
		this.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				int x=e.getX(),y=e.getY();
				if(x>maxx)maxx=x;
				if(x<minx)minx=x;
				if(y>maxy)maxy=y;
				if(y<miny)miny=y;
				if(lastX<0) {
					startX=lastX=x;startY=lastY=y;
					tm.dis.add(x+","+y);
				}
				drawTrack(x,y);
				track.setIcon(new ImageIcon(image));
				lastX=x;
				lastY=y;
				if((Math.abs(startX-x)>30||Math.abs(startY-y)>30)&&(--interval==0)) {
					tm.dis.add(x+","+y);
					tm.cut=cutImage();
					tm.lastX=lastX;
					tm.lastY=lastY;
					tm.startX=startX;
					tm.startY=startY;
					new Thread(tm).start();
					
					interval=INTERVAL;
				}
				//测试代码
				if(e.getX()<20) System.exit(0);
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				
				
			}
			
		});
		
		this.addMouseListener(new MouseListener() {


			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				initState();
				hideFrame();
				tm.tip.setVisible(false);
				Function.exec(tm.func);
				
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				showFrame();
				
			}
			
		});
	}
	
	void initState() {
		//Initial variable
		lastX=-1;
		lastY=-1;
		miny=height;
		minx=width;
		maxx=maxy=0;
		tm.dis=new ArrayList<String>();
		newImage();
		tip.setVisible(false);
		tm.sameFlag=false;
	}
	
	public void hideFrame() {
		super.setVisible(false);
	}
	
	public void showFrame() {
		super.setVisible(true);
	}
	
	
	public static void main(String[] args) {
		try {
			new MouseTrack(readProperties((System.getProperty("user.dir")+"/config.json")).getJSONArray("track"));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	BufferedImage cutImage() {
		BufferedImage res =null;
		try {
		res=new BufferedImage(maxx-minx,maxy-miny,BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d=res.createGraphics();
		g2d.drawImage(image, -minx, -miny , width, height, null);
		}catch(Exception e) {
			
		}
		return res;
	}
	
	void drawTrack(int x,int y) {
		Graphics2D g2d=image.createGraphics();
		g2d.setColor(Color.BLUE);
		//设置平滑
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
		g2d.setStroke(stroke);
		g2d.drawLine(lastX, lastY, x, y);
		g2d.dispose();
		
	}
	
	void newImage() {
		 image=new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
//		 gray=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
//		 Graphics2D g2d=gray.createGraphics();
//		 g2d.setColor(Color.white);
//		 g2d.fillRect(0, 0, width, height);
		 track.setIcon(new ImageIcon(image));
	}
	
	void readTrackImages(){
		trackImages=new byte[tracks.length()][];
		for(int i=0;i<tracks.length();i++) {
			try {
				JSONObject obj=tracks.getJSONObject(i);
			//	System.out.println(obj.getString("trackImage"));
				String  fp=obj.getString("trackImage");
				if(fp.indexOf("/")==0)
					fp=System.getProperty("user.dir")+fp;
				trackImages[i]=FingerPrint.hashValue(ImageIO.read(new File(fp)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	static JSONObject readProperties(String path) {
		JSONObject res=null;
		try {
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));
			String str="",t=null;
			while((t=br.readLine())!=null)
				str+=t;
			br.close();
			System.out.println(str);
			res=new JSONObject(str);
			
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "找不到配置文件","错误",JOptionPane.ERROR_MESSAGE);
			
		}
		
		return res;
	}
}
