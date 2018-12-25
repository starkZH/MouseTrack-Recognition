package com.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import util.FingerPrint;

public class TipModal  implements Runnable{
	final int MAX_MISTAKE_TIMES=5;
	int TIP_WIDTH=400,TIP_HEIGHT=200;
	int lastX=-1,lastY=-1,startX=-1,startY=-1;
	JLabel tip=new JLabel();
	BufferedImage invalid = drawTip("无效操作");
	byte[][] trackImages;
	BufferedImage cut;
	JSONArray tracks=null;
	ArrayList<String> dis=new ArrayList<String>();
	String lastCommand="";//上一个执行的指令
	String func="NONE";
	int windowWidth,windowHeight;
	boolean sameFlag=false;//是否在同次绘制中
	public TipModal() {
		
		tip.setBackground(Color.GRAY);
		tip.setFont(new Font("微软雅黑", Font.BOLD, 32));
		tip.setForeground(Color.WHITE);
	}
	
	@Override
	public void run() {
		long sign=0;//System.currentTimeMillis();
		System.out.println("["+sign+"]New Thread Start...");
		recognize();
		System.out.println("["+sign+"]Thread End...");
	}
	
	void recognize() {//轨迹识别
		long start=System.currentTimeMillis();
		try {
		int offset=(int)(cut.getWidth()/10.0+2);//坐标偏移容错
		offset=offset>20?20:offset;
		System.out.println("offset: "+offset+"  "+cut.getWidth());
		double max=0;
		int max_index=-1;//图片相似度最大的轨迹索引
		int interval=MouseTrack.INTERVAL;
		for(int i=0;i<tracks.length();i++) {
				JSONObject obj=tracks.getJSONObject(i);
				JSONArray ts=obj.getJSONArray("direction");
				int ts_i=0;
				String[] last=null;
				String last_dir=null,//前一次的坐标
						last_res="";//前两个坐标比较后的结果
				int mistake_times=0;//容错率
				String last_mistake="";
				for(String dir:dis) {//遍历鼠标运动方向
				String[] ds=dir.split(",");//此次的坐标
				if(last_dir!=null) {
					last=last_dir.split(",");//上次的坐标
					int lx=Integer.parseInt(last[0]),ly=Integer.parseInt(last[1]),
							nx=Integer.parseInt(ds[0]),ny=Integer.parseInt(ds[1]);
					int ox=nx-lx>offset?1:nx-lx<=-offset?-1:0
							,oy=ny-ly>offset?1:ny-ly<=-offset?-1:0;
						String dir_res=ox+","+oy;//得到方向
						
						if(ts_i>=ts.length()) {//超出标准轨迹的方向数目了，允许小范围内的超出
							if(interval--==0) {//每移动10次记录一次，不然太密集的话总是在误差范围内
							last_res=dir_res;
							interval=MouseTrack.INTERVAL;
							}
							if(!last_res.equals(dir_res)) {//瞎画的时候退出
							ts_i=0;
							break;
							}
							continue;
						}
						
					if((!dir_res.equals(last_res))) {//新的运动方向
						
						if((dir_res).equals(ts.getString(ts_i))) {//运动方向与轨迹吻合
						ts_i++;
						last_res=dir_res;
						}else if(!last_mistake.equals(dir_res)&&mistake_times++>MAX_MISTAKE_TIMES) {//同个错误不重复计入
							last_mistake=dir_res;
							break;
						}
					}
					last_dir=dir;
				}else {
					last_dir=dir;
				}
			}
				System.out.println(i+" ) Mistake: "+mistake_times);
				if(mistake_times>MAX_MISTAKE_TIMES)continue;
				if(ts_i==ts.length()) {//手势运动方向吻合，判断图片相似度
					double tmp=new FingerPrint(cut).compare(trackImages[i]);
					if(tmp>max) {
						max=tmp;
						max_index=i;
					}
					
				}
				
		}
		if(max_index>=0&&max>=0.8) {//有匹配的轨迹
			tip.setVisible(true);
			JSONObject obj=tracks.getJSONObject(max_index);
			tip.setIcon(new ImageIcon(drawTip(obj.getString("description"))));
			func=obj.getString("function");
			if(!sameFlag) {
				
				sameFlag=true;
			}
		}else {
			func="NONE";
			tip.setIcon(new ImageIcon(invalid));
		}
		
		
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		long end=System.currentTimeMillis();
	//	 System.out.println("Spent "+(end-start));
	}
	
	BufferedImage drawTip(String text) {
		BufferedImage bi=new BufferedImage(400,200,BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d=bi.createGraphics();
		g2d.setColor(Color.WHITE);
		Font font=new Font("微软雅黑",Font.BOLD,80);
		g2d.setFont(font);
		  FontMetrics metrics = g2d.getFontMetrics(font);
		    // Determine the X coordinate for the text
		    int x = 0 + (TIP_WIDTH - metrics.stringWidth(text)) / 2;
		    // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
		    int y = 0 + ((TIP_HEIGHT - metrics.getHeight()) / 2) + metrics.getAscent();
		    // Set the font

		g2d.drawString(text, x, y);
		return bi;
	}
	
}
