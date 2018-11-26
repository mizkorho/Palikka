
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.awt.image.*;

class Launcher extends JFrame
{
	PlayMidi pm;
	Palikka panel;
	
	public static void main(String args[]) 
	{
		Launcher frame = new Launcher();
	}
	
	public Launcher() 
	{
		int SLOTSIZE = 0, DIMX = 0, DIMY = 0;
		int BOXES = 0, DEMONS = 0, DEMONWAIT = 0;
		String BACKGROUND = "RANDOM";
		
		try
		{
			FileInputStream fstream = new 
				FileInputStream("config.txt");

			BufferedReader in = 
				new BufferedReader(new InputStreamReader(fstream)); 

			String str;
			while ((str = in.readLine()) != null) 
			{
				for (int i = 0; i < str.length(); i++)
				{
					if (str.substring(i, i+1).equals("="))
					{
						String str2 = str.substring(0,i);

						if (str2.equalsIgnoreCase("SLOTSIZE")) 
						{
							try
							{
								SLOTSIZE = (Integer.parseInt(str.substring(9).trim()));
							}
							catch(Exception e)
							{
								SLOTSIZE = 20;
							}
						}
							
						if (str2.equalsIgnoreCase("DIMX")) 
						{
							try
							{
								DIMX = (Integer.parseInt(str.substring(5).trim()));
							}
							catch(Exception e)
							{
								DIMX = 800;
							}
						}

						if (str2.equalsIgnoreCase("DIMY")) 
						{
							try
							{
								DIMY = (Integer.parseInt(str.substring(5).trim()));
							}
							catch(Exception e)
							{
								DIMY = 600;
							}
						}

						if (str2.equalsIgnoreCase("DEMONS")) 
						{
							try
							{
								DEMONS = (Integer.parseInt(str.substring(7).trim()));
							}
							catch(Exception e)
							{
								DEMONS = 10;
							}
						}

						if (str2.equalsIgnoreCase("BOXES")) 
						{
							try
							{
								BOXES = (Integer.parseInt(str.substring(6).trim()));
							}
							catch(Exception e)
							{
								BOXES = 250;
							}
						}

						if (str2.equalsIgnoreCase("DEMONWAIT")) 
						{
							try
							{
								DEMONWAIT = (Integer.parseInt(str.substring(10).trim()));
							}
							catch(Exception e)
							{
								DEMONWAIT = 400;
							}
						}

						if (str2.equalsIgnoreCase("BACKGROUND")) 
						{
							try
							{
								BACKGROUND = str.substring(11).trim();
							}
							catch(Exception e)
							{
								BACKGROUND = "RANDOM";
							}
						}
					}
				}
			}
			in.close();
		} 
		catch (Exception e)
		{
			System.err.println("File input error: " + e);

			DIMX=800;
			DIMY=600;
			SLOTSIZE=20;
			BOXES=250;
			DEMONS=10;
			DEMONWAIT=400;
			BACKGROUND="RANDOM";
		}
		
		pm = new PlayMidi("Midi.MID");

		panel = new Palikka(DIMX, DIMY, BOXES, DEMONS, SLOTSIZE, DEMONWAIT, BACKGROUND);

		this.getContentPane().add(panel);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setResizable(false);
		this.setTitle("Palikka");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage("Frameicon.png"));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		panel.requestFocus();
		
		int[] pixels = new int[16 * 16];
		Image image = Toolkit.getDefaultToolkit().createImage(
        new MemoryImageSource(16, 16, pixels, 0, 16));
		Cursor transparentCursor =
        Toolkit.getDefaultToolkit().createCustomCursor
             (image, new Point(0, 0), "invisibleCursor");
		setCursor(transparentCursor);
		
	}
}