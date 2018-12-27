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
	final int MAX_MISTAKE_TIMES=7;
	int TIP_WIDTH=400,TIP_HEIGHT=200;
	int lastX=-1,lastY=-1,startX=-1,startY=-1;
	JLabel tip=new JLabel();
	BufferedImage invalid = drawTip("��Ч����");
	byte[][] trackImages;
	BufferedImage cut;
	JSONArray tracks=null;
	ArrayList<String> dis=new ArrayList<String>();
	String lastCommand="";//��һ��ִ�е�ָ��
	String func="NONE";
	int windowWidth,windowHeight;
	boolean sameFlag=false;//�Ƿ���ͬ�λ�����
	public TipModal() {
		
		tip.setBackground(Color.GRAY);
		tip.setFont(new Font("΢���ź�", Font.BOLD, 32));
		tip.setForeground(Color.WHITE);
	}
	
	@Override
	public void run() {
		tip.setVisible(true);
		long sign=0;//System.currentTimeMillis();
		System.out.println("["+sign+"]New Thread Start...");
		recognize();
		System.out.println("["+sign+"]Thread End...");
	}
	
	void recognize() {//�켣ʶ��
		long start=System.currentTimeMillis();
		try {
		int offset=(int)(cut.getWidth()/20);//����ƫ���ݴ�
		offset=offset>20?20:offset;
		System.out.println("offset: "+offset+"  "+cut.getWidth());
		double max=0;
		int max_index=-1;//ͼƬ���ƶ����Ĺ켣����
		int interval=MouseTrack.INTERVAL,min_mistake=MAX_MISTAKE_TIMES;
		for(int i=0;i<tracks.length();i++) {
				JSONObject obj=tracks.getJSONObject(i);
				JSONArray ts=obj.getJSONArray("direction");
				int ts_i=0;
				String[] last=null;
				String last_dir=null,//ǰһ�ε�����
						last_res="";//ǰ��������ȽϺ�Ľ��
				int mistake_times=0;//�ݴ���
				String last_mistake="";
				for(String dir:dis) {//��������˶�����
				String[] ds=dir.split(",");//�˴ε�����
				if(last_dir!=null) {
					last=last_dir.split(",");//�ϴε�����
					int lx=Integer.parseInt(last[0]),ly=Integer.parseInt(last[1]),
							nx=Integer.parseInt(ds[0]),ny=Integer.parseInt(ds[1]);
					int ox=nx-lx>offset?1:nx-lx<=-offset?-1:0
							,oy=ny-ly>offset?1:ny-ly<=-offset?-1:0;
						String dir_res=ox+","+oy;//�õ�����
						
						if(ts_i>=ts.length()) {//������׼�켣�ķ�����Ŀ�ˣ�����С��Χ�ڵĳ���
							if(interval--==0) {//ÿ�ƶ�10�μ�¼һ�Σ���Ȼ̫�ܼ��Ļ���������Χ��
							last_res=dir_res;
							interval=MouseTrack.INTERVAL;
							}
							if(!last_res.equals(dir_res)) {//Ϲ����ʱ���˳�
							ts_i=0;
							break;
							}
							continue;
						}
						
					if((!dir_res.equals(last_res))) {//�µ��˶�����
						
						if((dir_res).equals(ts.getString(ts_i))) {//�˶�������켣�Ǻ�
						ts_i++;
						last_res=dir_res;
						}else if(!last_mistake.equals(dir_res)&&mistake_times++>MAX_MISTAKE_TIMES) {//ͬ�������ظ�����
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
				if(ts_i>=ts.length()) {//�����˶������Ǻϣ��ж�ͼƬ���ƶ�
					double tmp=new FingerPrint(cut).compare(trackImages[i]);
					if(min_mistake>mistake_times&&tmp>max) {//������ƥ�����ȣ��ٿ�ͼƬ���ƶ�
						max=tmp;
						max_index=i;
						min_mistake=mistake_times;
					}
					System.out.println(i+" ) Similar: "+tmp);
				}
				
		}
		System.out.println(max_index+" ) Similar: "+max);
		if(max_index>=0&&max>=0.75) {//��ƥ��Ĺ켣
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
		Font font=new Font("΢���ź�",Font.BOLD,80);
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
