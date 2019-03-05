package org.rrr.gui;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import org.newdawn.slick.opengl.Texture;
import org.rrr.RockRaidersRemake;
import org.rrr.assets.AssetManager;
import org.rrr.assets.LegoConfig.Node;
import org.rrr.assets.sound.SoundClip;
import org.rrr.assets.sound.Source;
import org.rrr.assets.tex.FLHAnimation;
import org.rrr.assets.tex.FLHFile;
import org.rrr.assets.tex.TexLoader;

public class Cursor {
	
	public int x, y;
	public int w, h;
	public int curAnimation = 0;
	public Texture base;
	public CursorAnimation[] animations;
	public Source playSource;
	public SoundClip sfxOkay, sfxNotOkay;
	
	
	public Cursor(Node cfg, AssetManager am, RockRaidersRemake par) {
		
		this.playSource = par.getAudioSystem().getSource();
		sfxOkay = par.getAssetManager().getSample("SFX_Okay");
		sfxNotOkay = par.getAssetManager().getSample("SFX_NotOkay");
		
		w = 32;
		h = 32;
		
		int lanims = 0;
		LinkedList<CursorAnimation> anims = new LinkedList<>();
		// getBMP
		for(String key : cfg.getValueKeys()) {
			System.out.println("POINTER CFG: " + key);
			if(key.equalsIgnoreCase("Pointer_Blank")) {
				System.out.println("GETTING BLANK POINTER: " + cfg.getValue(key));
				base = am.getTexture(cfg.getValue(key));
				System.out.println(base);
				base.setTextureFilter(GL_NEAREST);
				continue;
			}
			
			CursorAnimation anim = new CursorAnimation();
			if(key.startsWith("Pointer_"))
				anim.name = key.substring(8);
			else
				anim.name = key;
			
			String path = cfg.getValue(key).replaceAll("\\\\", "/");
			if(path.contains(",")) {
				anim.usesBaseTex = true;
				anim.stillFrame = false;
				String[] split = path.split(",");
				if(split.length != 3) {
					System.out.println("pointers: argument mismatch");
					continue;
				}
				try {
					anim.x = Integer.parseInt(split[1]);
					anim.y = Integer.parseInt(split[2]);
				} catch (Exception e) {
					System.out.println("pointers: invalid arguments " + split[1] + " " + split[2]);
					continue;
				}
				
				anim.anim = am.getFLHAnimation(split[0]);
				anim.w = anim.anim.data.w;
				anim.h = anim.anim.data.h;
				
			} else {
				anim.usesBaseTex = false;
				anim.stillFrame = true;
				System.out.println("GETTING FILE: " + path);
				anim.tex = am.getTexture(path);
				anim.w = anim.tex.getImageWidth();
				anim.h = anim.tex.getImageHeight();
			}
			
			anims.add(anim);
			lanims++;
			
		}
		
		System.out.println("CURSOR CREATED!");
		
		animations = new CursorAnimation[lanims];
		for(int i = 0; i < lanims; i++) {
			animations[i] = anims.pop();
			System.out.println(i + ": " + animations[i].name);
		}
		
	}
	
	public static class CursorAnimation {
		
		public String name;
		public int x, y, w, h;
		public boolean usesBaseTex, stillFrame;
		public Texture tex;
		public FLHAnimation anim;
		
	}
	
	public void setCursor(String name) {
		for(int i = 0; i < animations.length; i++) {
			if(animations[i].name.equals(name)) {
				curAnimation = i;
				return;
			}
		}
	}
	
	public void update(float dt) {
		if(!animations[curAnimation].stillFrame) {
			System.out.println("Stepping!");
			animations[curAnimation].anim.step(dt);
		}
	}
	
	public void okay() {
		playSource.play(sfxOkay);
	}
	
	public void notOkay() {
		playSource.play(sfxNotOkay);
	}
	
}
