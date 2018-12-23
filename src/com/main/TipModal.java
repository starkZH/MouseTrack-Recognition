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
		long sign=0;//System.currentTimeMillis();
		System.out.println("["+sign+"]New Thread Start...");
		recognize();
		System.out.println("["+sign+"]Thread End...");
	}
	
	void recognize() {//�켣ʶ��
		long start=System.currentTimeMillis();
		try {
		int offset=(int)(cut.getWidth()/10.0+2);//����ƫ���ݴ�
		offset=offset>20?20:offset;
		System.out.println("offset: "+offset+"  "+cut.getWidth());
		double max=0;
		int max_index=-1;//ͼƬ���ƶ����Ĺ켣����
		for(int i=0;i<tracks.length();i++) {
				JSONObject obj=tracks.getJSONObject(i);
				JSONArray ts=obj.getJSONArray("direction");
				int ts_i=0;
				String[] last=null;
				String last_dir=null,//ǰһ�εķ���
						last_res="";//ǰһ�����η���ȽϺ�Ľ��
				int mistake_times=0;//�ݴ���
				String last_mistake="";
				for(String dir:dis) {//��������˶�����
				String[] ds=dir.split(",");
				if(last_dir!=null&&ts_i<ts.length()) {
					last=last_dir.split(",");
					int lx=Integer.parseInt(last[0]),ly=Integer.parseInt(last[1]),
							nx=Integer.parseInt(ds[0]),ny=Integer.parseInt(ds[1]);
					int ox=nx-lx>offset?1:nx-lx<=-offset?-1:0
							,oy=ny-ly>offset?1:ny-ly<=-offset?-1:0;
						String dir_res=ox+","+oy;
					if((!dir_res.equals(last_res))) {//�µ��˶�����
						if((dir_res).equals(ts.getString(ts_i))) {//�˶�������켣�Ǻ�
						ts_i++;
						last_res=dir_res;
						}else if(!last_mistake.equals(dir_res)&&mistake_times++>5) {//ͬ�������ظ�����
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
				if(mistake_times>5)continue;
				if(ts_i==ts.length()) {//�����˶������Ǻϣ��ж�ͼƬ���ƶ�
					double tmp=new FingerPrint(cut).compare(trackImages[i]);
					if(tmp>max) {
						max=tmp;
						max_index=i;
					}
					
				}
				
		}
		if(max_index>=0&&max>=0.8) {//��ƥ��Ĺ켣
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
		
		
		/**
		HashMap<Integer,Double> indexs=new HashMap<Integer,Double>();
		for(int i=0;i<trackImages.length;i++) {
			double tmp=new FingerPrint(cut).compare(trackImages[i]);
		//	System.out.println(i+" "+tmp);
			if(tmp>=0.8) {
				max=tmp;
				indexs.put(i, tmp);
			}
		}
	//	System.out.println("Map Keys: "+dis.size());
		if(max>=0.8) {//ͼ������
			
			JSONObject jso=null;
			try {
				int cx=lastX-startX,cy=lastY-startY;
				for (Integer index  : indexs.keySet()) { 
					boolean flag=true;
						jso=tracks.getJSONObject(index);
				int ox=jso.getInt("offsetX"),oy=jso.getInt("offsetY");
						
				if((ox==1&&cx<0)||
						(ox==-1&&cx>0)||
						(oy==1&&cy<0)||
						(oy==-1&&cy>0))flag=false;
				
	//		System.out.println(index+" "+indexs.get(index));
	//		tip.setVisible(flag);
			if(flag) {
				tip.setVisible(true);
				tip.setIcon(new ImageIcon(drawTip(jso.getString("description"))));
			//	break;
			}else tip.setIcon(new ImageIcon(invalid));
				}
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
		}else tip.setVisible(false);
		**/
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
