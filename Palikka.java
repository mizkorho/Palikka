
import java.applet.*;
import java.applet.AudioClip;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

public class Palikka extends JPanel implements KeyListener
{
	private Graphics gfx, bufgfx;
	
	private Dimension DIM = new Dimension(800,600);

	private int SLOTSIZE = 20;
	private int XSLOTS = DIM.width/SLOTSIZE;
	private int YSLOTS = DIM.height/SLOTSIZE;
	private int BOXES = 250;
	private int DEMONS = 10;
	private int DEMONWAIT = 400;
	private String BACKGROUND = "RANDOM";

	private int slot[][]; // 0 tyhjä, 1 Box, 2 Hero, 3 Demon

	public boolean loppu, ekakerta = true, paussi = false;

	private Image itausta, inebula1, inebula2, inebula3, inebula4;
	private Image igalaxy1, ibox, iboxR, iboxG, iboxB, iboxY, idude, ipiru, iuserbg;
	private BufferedImage puhveli;

	private AudioClip audiopauseon, audiopauseoff, audiovoitto, audiotappio;

	private Hero dude;
	private java.util.List<Demon> demonit = new ArrayList<Demon>();

	private DemonThread dt;

	public Palikka(int dimx, int dimy, int boxc, int dem, int ss, int dw, String bg) 
	{	// CONSTRUCTOR
		DIM = new Dimension(dimx, dimy);
		SLOTSIZE = ss;
		XSLOTS = DIM.width/SLOTSIZE;
		YSLOTS = DIM.height/SLOTSIZE;
		BOXES = boxc;
		DEMONS = dem;
		DEMONWAIT = dw;
		BACKGROUND = bg;

		this.setPreferredSize(DIM);
		this.setFocusable(true);
		addKeyListener(this);

		slot = new int[XSLOTS][YSLOTS];

		if ((DIM.width == 800) && (DIM.height == 600))
		try 
		{
			inebula1 = ImageIO.read(new File("Nebula1.png"));
			inebula2 = ImageIO.read(new File("Nebula2.png"));
			inebula3 = ImageIO.read(new File("Nebula3.png"));
			inebula4 = ImageIO.read(new File("Nebula4.png"));
			igalaxy1 = ImageIO.read(new File("Galaxy1.png"));

			ibox = ImageIO.read(new File("Boxyellow.png"));
			iboxR = ImageIO.read(new File("Boxred.png"));
			iboxG = ImageIO.read(new File("Boxgreen.png"));
			iboxB = ImageIO.read(new File("Boxblue.png"));
			iboxY = ImageIO.read(new File("Boxyellow.png"));

			idude = ImageIO.read(new File("Dude.png"));
			ipiru = ImageIO.read(new File("Piru.png"));

			if (!BACKGROUND.equals("RANDOM")) 
			{
				iuserbg = ImageIO.read(new File(BACKGROUND));
				iuserbg = iuserbg.getScaledInstance((int)DIM.getWidth(), (int)DIM.getHeight(), Image.SCALE_SMOOTH);
			}
		}
		catch(IOException exep) 
		{
			exep.printStackTrace();
			throw new RuntimeException("Could not open file ");
		}
		
		else
		try 
		{
			inebula1 = ImageIO.read(new File("Nebula1.png"));
			inebula2 = ImageIO.read(new File("Nebula2.png"));
			inebula3 = ImageIO.read(new File("Nebula3.png"));
			inebula4 = ImageIO.read(new File("Nebula4.png"));
			igalaxy1 = ImageIO.read(new File("Galaxy1.png"));

			inebula1 = inebula1.getScaledInstance((int)DIM.getWidth(), (int)DIM.getHeight(), Image.SCALE_SMOOTH);
			inebula2 = inebula2.getScaledInstance((int)DIM.getWidth(), (int)DIM.getHeight(), Image.SCALE_SMOOTH);
			inebula3 = inebula3.getScaledInstance((int)DIM.getWidth(), (int)DIM.getHeight(), Image.SCALE_SMOOTH);
			inebula4 = inebula4.getScaledInstance((int)DIM.getWidth(), (int)DIM.getHeight(), Image.SCALE_SMOOTH);
			igalaxy1 = igalaxy1.getScaledInstance((int)DIM.getWidth(), (int)DIM.getHeight(), Image.SCALE_SMOOTH);

			ibox = ImageIO.read(new File("Boxyellow.png"));
			iboxR = ImageIO.read(new File("Boxred.png"));
			iboxG = ImageIO.read(new File("Boxgreen.png"));
			iboxB = ImageIO.read(new File("Boxblue.png"));
			iboxY = ImageIO.read(new File("Boxyellow.png"));
			
			idude = ImageIO.read(new File("Dude.png"));
			ipiru = ImageIO.read(new File("Piru.png"));

			if (!BACKGROUND.equals("RANDOM")) 
			{
				iuserbg = ImageIO.read(new File(BACKGROUND));
				iuserbg = iuserbg.getScaledInstance((int)DIM.getWidth(), (int)DIM.getHeight(), Image.SCALE_SMOOTH);
			}
		}
		catch(IOException exep) 
		{
			exep.printStackTrace();
			throw new RuntimeException("Could not open file ");
		}

		newgame();
	}

	public void sop(String soppa)
	{
		System.out.println(soppa);
	}

	public void newgame()
	{
		ekakerta = true;
		loppu = false;

		// Valitaan taustakuva
		switch ((int)(Math.random()*10))
		{
			case 0: itausta = inebula1; break;
			case 1: itausta = inebula2; break;
			case 2: itausta = inebula3; break;
			case 3: itausta = inebula4; break;
			case 4: itausta = inebula1; break;
			case 5: itausta = inebula2; break;
			case 6: itausta = inebula3; break;
			case 7: itausta = inebula4; break;
			case 8: itausta = igalaxy1; break;
			case 9: itausta = inebula4; break;
		}
		if (!BACKGROUND.equals("RANDOM")) itausta = iuserbg;

		// Valitaan laatikkokuva
		switch ((int)(Math.random()*4))
		{
			case 0: ibox = iboxR; break;
			case 1: ibox = iboxG; break;
			case 2: ibox = iboxB; break;
			case 3: ibox = iboxY; break;
		}

		// Tyhjennetään slotit
		for (int laskuri = 0; laskuri < XSLOTS; laskuri++)
			for (int laskuri2 = 0; laskuri2 < YSLOTS; laskuri2++)
				slot[laskuri][laskuri2] = 0;

		// Luodaan laatikot
		for (int laskuri = 0; laskuri < BOXES; laskuri++)
		{
			int apux = (int)(Math.random()*(DIM.width / SLOTSIZE));
			int apuy = (int)(Math.random()*(DIM.height / SLOTSIZE));

			if (slot[apux][apuy] == 0)
				slot[apux][apuy] = 1;
			else 
				laskuri--;
		}

		// Luodaan Sankari koordinaatteineen
		dude = new Hero("Seppo");
		for (int i = 0; i < ((DIM.height / SLOTSIZE) * (DIM.width / SLOTSIZE)); i++)
		{
			int apux = (int)(Math.random()*DIM.width / SLOTSIZE);
			int apuy = (int)(Math.random()*DIM.height / SLOTSIZE);

			if (slot[apux][apuy] == 0) 
			{
				dude.setXY(apux, apuy);
				dude.seteXY(apux, apuy);
				slot[apux][apuy] = 2;
				i = ((DIM.width / SLOTSIZE) * (DIM.height / SLOTSIZE)) +100;
			}
		}

		// Luodaan Demonit aloituskoordinaatteineen
		demonit.clear();
		for (int laskuri = 0; laskuri < DEMONS; laskuri++)
		{
			Demon apu = new Demon("Demon " + laskuri);

			for (int i = 0; i < ((DIM.height / SLOTSIZE) * (DIM.width / SLOTSIZE)); i++)
			{
				int apux = (int)(Math.random()*DIM.width / SLOTSIZE);
				int apuy = (int)(Math.random()*DIM.height / SLOTSIZE);

				if (slot[apux][apuy] == 0) 
				{
					if (!(apux-dude.getX() < 5 && apux-dude.getX() > -5 &&
						apuy-dude.getY() < 5 && apuy-dude.getY() > -5))
					{
						apu.setXY(apux, apuy);
						apu.seteXY(apux, apuy);
						slot[apux][apuy] = 3;
						i = ((DIM.width / SLOTSIZE) * (DIM.height / SLOTSIZE)) +100;
					}
				}
			}	
			demonit.add(apu);
		}

		try 
		{
			URL urli; 
				
			urli = new URL(new URL("file:" + System.getProperty("user.dir") + "/"), "Pause.wav");
			audiopauseon = Applet.newAudioClip(urli);

			urli = new URL(new URL("file:" + System.getProperty("user.dir") + "/"), "Unpause.wav");
			audiopauseoff = Applet.newAudioClip(urli);

			urli = new URL(new URL("file:" + System.getProperty("user.dir") + "/"), "Stuck.wav");
			audiovoitto = Applet.newAudioClip(urli);
			
			urli = new URL(new URL("file:" + System.getProperty("user.dir") + "/"), "Die.wav");
			audiotappio = Applet.newAudioClip(urli);
		}
		catch(MalformedURLException urlexception) 
		{
			System.out.println(urlexception);
		}

		dt = new DemonThread(demonit, DEMONWAIT);
		dt.start();
	}

	public void endgame(boolean voitto)
	{
		loppu = true;
		
		paint(gfx);
		
		String teksti = "Virhe";

		if (voitto)
		{
			audiovoitto.play(); 
			teksti = "Demonit ovat jumissa";
		}
		else
		{
			audiotappio.play(); 
			teksti = "Demonit söivät sinut";
		}
			
		Object[] options = {"Uusi peli","Poistu"};

		int n = JOptionPane.showOptionDialog(this,
		"Jatkotoimenpiteet?",
		teksti,
		JOptionPane.YES_NO_OPTION,
		JOptionPane.QUESTION_MESSAGE,
		null,     //don't use a custom Icon
		options,  //the titles of buttons
		options[0]); //default button title

		paint(gfx);
		dt.lopeta();
		
		if (n == 0) 
			newgame();
		else 
			System.exit(0);		
	}

	public void paint(Graphics gfx) 
	{
		if (loppu && gfx != null) 
				gfx.drawImage(puhveli, 0, 0, this);	
		else
		if (ekakerta) 
		{
			ekakerta = false;

			GraphicsEnvironment graphEnv = 
				GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice graphDevice = 
				graphEnv.getDefaultScreenDevice();
			GraphicsConfiguration graphicConf = 
				graphDevice.getDefaultConfiguration();
			
			puhveli = graphicConf.createCompatibleImage(DIM.width, DIM.height, Transparency.TRANSLUCENT);
			bufgfx = puhveli.getGraphics();
			bufgfx.drawImage(itausta, 0, 0, this);

			bufgfx.drawImage(idude, dude.getX()*SLOTSIZE, dude.getY()*SLOTSIZE, 
			dude.getX()*SLOTSIZE+SLOTSIZE, dude.getY()*SLOTSIZE+SLOTSIZE, 
			0, 0, idude.getWidth(this), idude.getHeight(this), this);

			for (int laskuri = 0; laskuri < demonit.size(); laskuri++)
				bufgfx.drawImage(ipiru, 
				demonit.get(laskuri).getX()*SLOTSIZE, 
				demonit.get(laskuri).getY()*SLOTSIZE, 
				demonit.get(laskuri).getX()*SLOTSIZE+SLOTSIZE, 
				demonit.get(laskuri).getY()*SLOTSIZE+SLOTSIZE,
				0, 0, ipiru.getWidth(this), ipiru.getHeight(this), this);

			for(int apux = 0; apux < XSLOTS; apux++) 
				for(int apuy = 0; apuy < YSLOTS; apuy++)
					if(slot[apux][apuy] == 1)
						bufgfx.drawImage(ibox, apux*SLOTSIZE, apuy*SLOTSIZE, 
						apux*SLOTSIZE+SLOTSIZE, apuy*SLOTSIZE+SLOTSIZE, 
						0, 0, ibox.getWidth(this), ibox.getHeight(this), this);

			gfx = this.getGraphics();
			if (gfx != null)
				gfx.drawImage(puhveli, 0, 0, this);	
		}
		else
		{
			int dapux = dude.getX();
			int dapuy = dude.getY();
			int dapuex = dude.geteX();
			int dapuey = dude.geteY();
			int apux, apuy, apuex, apuey;
			
			bufgfx.drawImage(itausta, 
			dapux*SLOTSIZE, dapuy*SLOTSIZE, 
			(dapux+1)*SLOTSIZE, (dapuy+1)*SLOTSIZE, 
			dapux*SLOTSIZE, dapuy*SLOTSIZE, 
			(dapux+1)*SLOTSIZE, (dapuy+1)*SLOTSIZE, this);

			bufgfx.drawImage(idude, dapux*SLOTSIZE, dapuy*SLOTSIZE, 
			dapux*SLOTSIZE+SLOTSIZE, dapuy*SLOTSIZE+SLOTSIZE, 
			0, 0, idude.getWidth(this), idude.getHeight(this), this);

			if (!(dapux == dapuex && dapuy == dapuey))
			bufgfx.drawImage(itausta, 
			dapuex*SLOTSIZE, dapuey*SLOTSIZE, 
			(dapuex+1)*SLOTSIZE, (dapuey+1)*SLOTSIZE, 
			dapuex*SLOTSIZE, dapuey*SLOTSIZE, 
			(dapuex+1)*SLOTSIZE, (dapuey+1)*SLOTSIZE, this);

			for (int laskuri = 0; laskuri < demonit.size(); laskuri++)
			{
				Demon apu = demonit.get(laskuri);

				apux = apu.getX();
				apuy = apu.getY();
				apuex = apu.geteX();
				apuey = apu.geteY();

				bufgfx.drawImage(ipiru, 
				apux*SLOTSIZE, apuy*SLOTSIZE, 
				apux*SLOTSIZE+SLOTSIZE, apuy*SLOTSIZE+SLOTSIZE,
				0, 0, ipiru.getWidth(this), ipiru.getHeight(this), this);
				
				if (apux != apuex || apuy != apuey)
				{
				bufgfx.drawImage(itausta, 
				apuex*SLOTSIZE, apuey*SLOTSIZE, 
				(apuex+1)*SLOTSIZE, (apuey+1)*SLOTSIZE, 
				apuex*SLOTSIZE, apuey*SLOTSIZE, 
				(apuex+1)*SLOTSIZE, (apuey+1)*SLOTSIZE, this);
				}
			}
			
/*			for(int x = 0; x < XSLOTS; x++) 
				for(int y = 0; y < YSLOTS; y++)
					if(slot[x][y] == 1)
						bufgfx.drawImage(ibox, x*SLOTSIZE, y*SLOTSIZE, this);
*/

			for(int y = 0; y < YSLOTS; y++)
				if(slot[dapux][y] == 1)
					bufgfx.drawImage(ibox, dapux*SLOTSIZE, y*SLOTSIZE, 
					dapux*SLOTSIZE+SLOTSIZE, y*SLOTSIZE+SLOTSIZE, 
					0, 0, ibox.getWidth(this), ibox.getHeight(this), this);

			for(int x = 0; x < XSLOTS; x++)
				if(slot[x][dapuy] == 1)
					bufgfx.drawImage(ibox, x*SLOTSIZE, dapuy*SLOTSIZE, 
					x*SLOTSIZE+SLOTSIZE, dapuy*SLOTSIZE+SLOTSIZE, 
					0, 0, ibox.getWidth(this), ibox.getHeight(this), this);

			if (paussi)
			{
/*				float scaleFactor = .2f;
			    RescaleOp op = new RescaleOp(scaleFactor, 0, null);
    			puhveli = op.filter(puhveli, puhveli);
*/
				BufferedImage safetydeposit = puhveli;

				Kernel kernel = new Kernel(3, 3, new float[] 
				{
					-2, 0, 0,
					0, 1, 0,
					0, 0, 2
				});
/*				
				Kernel kernel = new Kernel(3, 3, new float[] {
				1f/9f, 1f/9f, 1f/9f,
				1f/9f, 1f/9f, 1f/9f,
				1f/9f, 1f/9f, 1f/9f});
*/
				BufferedImageOp op = new ConvolveOp(kernel);
				puhveli = op.filter(puhveli, null);

				gfx = this.getGraphics();
				if (gfx != null) 
					gfx.drawImage(puhveli, 0, 0, this);
					
				puhveli = safetydeposit;
	   		}
			else
			{
				gfx = this.getGraphics();
				if (gfx != null) 
					gfx.drawImage(puhveli, 0, 0, this);
			}
		}
	}

	public void update(Graphics gfx)
	{
		paint(bufgfx);
	}

	public void keyPressed(KeyEvent evt) 
	{
		int key = evt.getKeyCode();

		if (key == KeyEvent.VK_B)
		{
			int luku = 1+(int)(Math.random()*4);
			
			try
			{
				switch (luku)
				{
					case 1: ibox = ImageIO.read(new File("Boxred.png")); break;
					case 2: ibox = ImageIO.read(new File("Boxgreen.png")); break;
					case 3: ibox = ImageIO.read(new File("Boxblue.png")); break;
					case 4: ibox = ImageIO.read(new File("Boxyellow.png")); break;
					default: ibox = ImageIO.read(new File("Boxyellow.png")); 
				}
				paint(gfx);
			}		
			catch(IOException exep) 
			{
				exep.printStackTrace();
				throw new RuntimeException("Could not open file ");
			}
		}

		if (!loppu && key == KeyEvent.VK_SPACE)
		{
			if (paussi)
			{
				paussi = false;
				audiopauseoff.play(); 
			}
			else 
			{
				paussi = true;
				paint(gfx);
				audiopauseon.play(); 
			}
		}
		
		if (!loppu && !paussi)
		{
			if (key == KeyEvent.VK_UP) 
				dude.moveto(dude.getX(), dude.getY()-1);
			if (key == KeyEvent.VK_DOWN) 
				dude.moveto(dude.getX(), dude.getY()+1);
			if (key == KeyEvent.VK_LEFT) 
				dude.moveto(dude.getX()-1, dude.getY());
			if (key == KeyEvent.VK_RIGHT) 
				dude.moveto(dude.getX()+1, dude.getY());
		}
	}

	public void keyReleased(KeyEvent evt) {}

	public void keyTyped(KeyEvent evt) {}

class Demon // DEEEEEEEEEEMOOOOOOOOOOONNNNNNNNN
{
	private String nimi;
	private int x, y, ex, ey;

	public Demon()
	{
		nimi = "Marduk";
	}

	public Demon(String newname)
	{
		nimi = newname;
	}
	
	public void seteXY(int newex, int newey)
	{
		ex = newex;
		ey = newey;
		if (ex < 0) ex = 0;
		if (ex >= XSLOTS) ex = XSLOTS-1;
		if (ey < 0) ey = 0;
		if (ey >= YSLOTS) ey = YSLOTS-1;
	}
	
	public void setXY(int newx, int newy)
	{
		x = newx;
		y = newy;
		if (x < 0) x = 0;
		if (x >= XSLOTS) x = XSLOTS-1;
		if (y < 0) y = 0;
		if (y >= YSLOTS) y = YSLOTS-1;
	}
	
	public boolean isStuck()
	{
		int i = 0;

		if (!(isLegal(x+1, y-1))) i++;
		if (!(isLegal(x+1, y+1))) i++;
		if (!(isLegal(x-1, y+1))) i++;
		if (!(isLegal(x-1, y-1))) i++;
		if (!(isLegal(x, y+1))) i++;
		if (!(isLegal(x, y-1))) i++;
		if (!(isLegal(x+1, y))) i++;
		if (!(isLegal(x-1, y))) i++;
		
		return (i == 8);
	}
	
	public boolean isLegal(int newx, int newy)
	{
		boolean legal = false;

		if (newx >= 0 && newx < XSLOTS && 
			newy >= 0 && newy < YSLOTS)
		if (slot[newx][newy] != 1 && slot[newx][newy] != 3)
			legal = true;

		return legal;
	}

	public boolean move()
	{
		boolean moved;
		int xm = 0;
		int ym = 0;

		if (y < dude.getY()) ym = 1;
		if (x < dude.getX()) xm = 1;
		if (y > dude.getY()) ym = -1;
		if (x > dude.getX()) xm = -1;

		moved = moveto(x+xm, y+ym);

		if (!moved)
		{
			moved = moveto(x, y+ym);
			if (!moved) moved = moveto(x+xm, y);
		}

		if (!moved)
			for (int i2 = 0; i2 < 4 && !moved; i2++)
				moved = moveto(x+1-(int)(Math.random()*3), 
					y+1-(int)(Math.random()*3));

		return moved;
	}

	public boolean moveto(int newx, int newy)
	{
		boolean moved = false;
		ex = x;
		ey = y;

		if (newx >= 0 && newx < XSLOTS && 
			newy >= 0 && newy < YSLOTS)
		if (slot[newx][newy] != 1 && slot[newx][newy] != 3)
		{
			x = newx;
			y = newy;
			slot[ex][ey] = 0;
			slot[x][y] = 3;
			moved = true;
		}

		if (x < 0) x = 0;
		if (x >= XSLOTS) x = XSLOTS-1;
		if (y < 0) y = 0;
		if (y >= YSLOTS) y = YSLOTS-1;

		if (dude.getX() == x && dude.getY() == y) 
			endgame(false);

		return moved;
	}

	public void setX(int newx)
	{
		ex = x;
		x = newx;
		slot[ex][y] = 0;
		slot[x][y] = 3;
		if (x < 0) x = 0;
		if (x >= XSLOTS) x = XSLOTS-1;
	}
	
	public void setY(int newy)
	{
		ey = y;
		y = newy;
		slot[x][ey] = 0;
		slot[x][y] = 3;
		if (y < 0) y = 0;
		if (y >= YSLOTS) y = YSLOTS-1;
	}

	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}

	public int geteX()
	{
		return ex;
	}
	
	public int geteY()
	{
		return ey;
	}

	public void vaihdanimi(String newname)
	{
		nimi = newname;
	}

	public String getName()
	{
		return nimi;
	}

	public void paivita()
	{
		paint(gfx);
	}
}

class Hero // HEEEEEEEEEEEEEEEEEEEEEEROOOOOOOOOOOOOOOO
{
	private String nimi;
	private int x, y, ex, ey;

	public Hero(String newname) 
	{
		nimi = newname;
	}

	public boolean moveto(int newx, int newy)
	{
		boolean moved = false;

		ex = x;
		ey = y;

		int suuntax = newx - x;
		int suuntay = newy - y;

		if (newx >= 0 && newx < XSLOTS && 
			newy >= 0 && newy < YSLOTS)
		{
			if (slot[newx][newy] == 0 && !loppu)
			{ // LIIKKUU NORMAALI
				x = newx;
				y = newy;
				slot[ex][ey] = 0;
				slot[x][y] = 2;
				moved = true;
				paint(gfx);
			}
			
			if (slot[newx][newy] == 1 && !loppu) 
			{ // LIIKKUU LAATIKON PÄÄLLE
				if (suuntay == 0 && suuntax != 0 && 
				newx+suuntax >= 0 && newx+suuntax < XSLOTS)
				{
					for (int laskuri = 2; x+(laskuri*suuntax) < XSLOTS && 
					x+(laskuri*suuntax) >= 0 && !moved; laskuri++)
					{
						if (slot[x+(laskuri*suuntax)][y] == 0)
						{
							slot[x+suuntax][y] = 2;
							slot[x+(laskuri*suuntax)][y] = 1;
							x = x+suuntax;
							y = newy;
							slot[ex][ey] = 0;
							slot[x][y] = 2;
							moved = true;
							paint(gfx);
						}
						else if (slot[x+(laskuri*suuntax)][y] == 3) 
							laskuri = XSLOTS + 1;
					}
				}
				
				if (suuntax == 0 && suuntay != 0 &&
				newy+suuntay >= 0 && newy+suuntay < YSLOTS)
				{
					for (int laskuri = 2; y+(laskuri*suuntay) < YSLOTS && 
					y+(laskuri*suuntay) >= 0 && !moved; laskuri++)
					{
						if (slot[x][y+(laskuri*suuntay)] == 0)
						{
							slot[x][y+suuntay] = 0;
							slot[x][y+(laskuri*suuntay)] = 1;
							y += suuntay;
							x = newx;
							slot[ex][ey] = 0;
							slot[x][y] = 2;
							moved = true;
							paint(gfx);
						}
						else if (slot[x][y+(laskuri*suuntay)] == 3) 
							laskuri = YSLOTS + 1;
					}
				}
			}

			if (slot[newx][newy] == 3 && !loppu) 
			{ // LIIKKUU DEMONIN PÄÄLLE
				x = newx;
				y = newy;
				slot[ex][ey] = 0;
				moved = true;
				loppu = true;
				paint(gfx);
				endgame(false);
			}
		}

		return moved;
	}

	public void seteXY(int newex, int newey)
	{
		ex = newex;
		ey = newey;
		if (ex < 0) ex = 0;
		if (ex >= XSLOTS) ex = XSLOTS-1;
		if (ey < 0) ey = 0;
		if (ey >= YSLOTS) ey = YSLOTS-1;
	}
	
	public void setXY(int newx, int newy)
	{
		ex = x;
		ey = y;
		x = newx;
		y = newy;
		slot[ex][ey] = 0;
		slot[x][y] = 2;
		if (x < 0) x = 0;
		if (x >= XSLOTS) x = XSLOTS-1;
		if (y < 0) y = 0;
		if (y >= YSLOTS) y = YSLOTS-1;
	}
	
	public void setX(int newx)
	{
		ex = x;
		x = newx;
		slot[ex][ey] = 0;
		slot[x][y] = 2;
		if (x < 0) x = 0;
		if (x >= XSLOTS) x = XSLOTS-1;
	}
	
	public void setY(int newy)
	{
		ey = y;
		y = newy;
		slot[ex][ey] = 0;
		slot[x][y] = 2;
		if (y < 0) y = 0;
		if (y >= YSLOTS) y = YSLOTS-1;
	}

	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}

	public int geteX()
	{
		return ex;
	}
	
	public int geteY()
	{
		return ey;
	}

	public void vaihdanimi(String newname)
	{
		nimi = newname;
	}

	public void kerronimi()
	{
		System.out.println("I'm a mighty Hero called " + nimi);
	}

	public void paivita()
	{
		paint(gfx);
	}
}

class DemonThread extends Thread 
{
	private java.util.List<Demon> demonilista = new ArrayList<Demon>();
	private int delay, period;
	private java.util.Timer ajastin;
	
	DemonThread(java.util.List<Demon> lista, int demonwaittime)
	{
		demonilista.clear();
		demonilista = lista;
		delay = 20;
		period = demonwaittime;
		ajastin = new java.util.Timer();
	}
 
	public void lopeta()
	{
		ajastin.cancel();
	}

	public void run() 
	{
		ajastin.scheduleAtFixedRate(new TimerTask()
		{
			public void run() 
			{
				int jumi = 0;

				for (int i = 0; i < demonilista.size() && !loppu; i++)
				{
					if (loppu || paussi) break;

					Demon d = demonilista.get(i);

					d.move();
							
					if (dude.getX() == d.getX() && dude.getY() == d.getY())
					{
						loppu = true;
						endgame(false);
					}
						
					if (!loppu)
					{
						if (d.isStuck()) jumi++;

						if (jumi >= demonilista.size()) 
						{
							loppu = true;
							endgame(true);
						}
					}
				}
				paint(gfx);
			}
		}, delay, period);
	}
}
}