/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *    WekaTaskMonitor.java
 *    Copyright (C) 2000-2012 University of Waikato, Hamilton, New Zealand
 *
 */

package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * This panel records the number of weka tasks running and displays a simple
 * bird animation while their are active tasks
 *
 * @author Mark Hall (mhall@cs.waikato.ac.nz)
 * @version $Revision$
 */
public class WekaTaskMonitor extends JPanel implements TaskLogger {

	/** for serialization */
	private static final long serialVersionUID = 508309816292197578L;

	/** The number of running weka threads */
	private int mActiveTasks = 0;

	/** The label for displaying info */
	private JLabel mMonitorLabel;

	/** The icon for the stationary bird */
	private ImageIcon mIconStationary;

	/** The icon for the animated bird */
	private ImageIcon mIconAnimated;

	/** True if their are active tasks */
	private boolean mAnimating = false;

	/**
	 * Constructor
	 */
	public WekaTaskMonitor() {
		java.net.URL imageURL = this.getClass().getClassLoader().getResource("weka/gui/weka_stationary.gif");

		if (imageURL != null) {
			Image pic = Toolkit.getDefaultToolkit().getImage(imageURL);
			imageURL = this.getClass().getClassLoader().getResource("weka/gui/weka_animated.gif");
			Image pic2 = Toolkit.getDefaultToolkit().getImage(imageURL);

			/*
			 * Image pic = Toolkit.getDefaultToolkit().
			 * getImage(ClassLoader.getSystemResource( "weka/gui/weka_stationary.gif"));
			 * Image pic2 = Toolkit.getDefaultToolkit().
			 * getImage(ClassLoader.getSystemResource( "weka/gui/weka_animated.gif"));
			 */

			mIconStationary = new ImageIcon(pic);
			mIconAnimated = new ImageIcon(pic2);
		}

		mMonitorLabel = new JLabel(" x " + mActiveTasks, mIconStationary, SwingConstants.CENTER);
		/*
		 * setBorder(BorderFactory.createCompoundBorder(
		 * BorderFactory.createTitledBorder("Weka Tasks"),
		 * BorderFactory.createEmptyBorder(0, 5, 5, 5) ));
		 */
		setLayout(new BorderLayout());
		Dimension d = mMonitorLabel.getPreferredSize();
		mMonitorLabel.setPreferredSize(new Dimension(d.width + 15, d.height));
		mMonitorLabel.setMinimumSize(new Dimension(d.width + 15, d.height));
		add(mMonitorLabel, BorderLayout.CENTER);

	}

	/**
	 * Tells the panel that a new task has been started
	 */
	@Override
	public synchronized void taskStarted() {
		mActiveTasks++;
		updateMonitor();
	}

	/**
	 * Tells the panel that a task has completed
	 */
	@Override
	public synchronized void taskFinished() {
		mActiveTasks--;
		if (mActiveTasks < 0) {
			mActiveTasks = 0;
		}
		updateMonitor();
	}

	/**
	 * Updates the number of running tasks an the status of the bird image
	 */
	private void updateMonitor() {
		mMonitorLabel.setText(" x " + mActiveTasks);
		if (mActiveTasks > 0 && !mAnimating) {
			mMonitorLabel.setIcon(mIconAnimated);
			mAnimating = true;
		}

		if (mActiveTasks == 0 && mAnimating) {
			mMonitorLabel.setIcon(mIconStationary);
			mAnimating = false;
		}
	}

	/**
	 * Main method for testing this class
	 */
	public static void main(String[] args) {

		try {
			final javax.swing.JFrame jf = new javax.swing.JFrame();
			jf.getContentPane().setLayout(new BorderLayout());
			final WekaTaskMonitor tm = new WekaTaskMonitor();
			tm.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Weka Tasks"),
					BorderFactory.createEmptyBorder(0, 5, 5, 5)));
			jf.getContentPane().add(tm, BorderLayout.CENTER);
			jf.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent e) {
					jf.dispose();
					System.exit(0);
				}
			});
			jf.pack();
			jf.setVisible(true);
			tm.taskStarted();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println(ex.getMessage());
		}
	}
}
