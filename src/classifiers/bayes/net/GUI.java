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
 *    GUI.java
 *    Copyright (C) 2007-2012 University of Waikato, Hamilton, New Zealand
 *
 */
package classifiers.bayes.net;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyEditor;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.table.AbstractTableModel;

import classifiers.bayes.net.MarginCalculator.JunctionTreeNode;
import core.Instances;
import core.OptionHandler;
import core.SerializedObject;
import core.Utils;
import core.converters.AbstractFileLoader;
import core.converters.AbstractFileSaver;
import core.converters.ArffSaver;
import core.converters.ConverterUtils;
import gui.ConverterFileChooser;
import gui.ExtensionFileFilter;
import gui.GenericObjectEditor;
import gui.LookAndFeel;
import gui.PropertyDialog;
import gui.graphvisualizer.BIFFormatException;
import gui.graphvisualizer.BIFParser;
import gui.graphvisualizer.GraphEdge;
import gui.graphvisualizer.GraphNode;
import gui.graphvisualizer.HierarchicalBCEngine;
import gui.graphvisualizer.LayoutCompleteEvent;
import gui.graphvisualizer.LayoutCompleteEventListener;
import gui.graphvisualizer.LayoutEngine;

/**
 * GUI interface to Bayesian Networks. Allows editing Bayesian networks on
 * screen and provides GUI interface to various Bayesian network facilities in
 * Weka, including random network generation, data set generation and Bayesion
 * network inference.
 * 
 * @author Remco Bouckaert (remco@cs.waikato.ac.nz)
 * @version $Revision$
 */
public class GUI extends JPanel implements LayoutCompleteEventListener {

	/** for serialization */
	private static final long serialVersionUID = -2038911085935515624L;

	/** standard height of node */
	final static int DEFAULT_NODE_WIDTH = 50;

	/** width of node, allowing for some padding */
	final static int PADDING = 10;

	/** The current LayoutEngine */
	protected LayoutEngine m_layoutEngine;

	/** Panel actually displaying the graph */
	protected GraphPanel m_GraphPanel;

	/** String containing file name storing current network */
	protected String m_sFileName = new String("");

	/** used for calculating marginals in Bayesian netwowrks */
	MarginCalculator m_marginCalculator = null;

	/**
	 * used for calculating marginals in Bayesian netwowrks when evidence is present
	 */
	MarginCalculator m_marginCalculatorWithEvidence = null;

	/**
	 * flag indicating whether marginal distributions of each of the nodes should be
	 * shown in display.
	 */
	boolean m_bViewMargins = false;

	boolean m_bViewCliques = false;

	/** The menu bar */
	private JMenuBar mMenuBar;

	/** data selected from file. Used to train a Bayesian network on */
	Instances m_Instances = null;

	/** Text field for specifying zoom */
	final JTextField m_jTfZoom;

	/** toolbar containing buttons at top of window */
	final JToolBar m_jTbTools;

	/** status bar at bottom of window */
	final JLabel m_jStatusBar;

	/** TextField for node's width */
	private final JTextField mJTfNodeWidth = new JTextField(3);

	/** TextField for nodes height */
	private final JTextField mJTfNodeHeight = new JTextField(3);

	/** this contains the m_GraphPanel GraphPanel */
	JScrollPane m_jScrollPane;

	/** path for icons */
	private final String iconpath = "weka/classifiers/bayes/net/icons/";

	/** current zoom value */
	private double mFScale = 1;

	/** standard width of node */
	private int mNNodeHeight = 2 * getFontMetrics(getFont()).getHeight();

	private int mNNodeWidth = DEFAULT_NODE_WIDTH;

	private int mNPaddedNodeWidth = DEFAULT_NODE_WIDTH + PADDING;

	/** used when using zoomIn and zoomOut buttons */
	private final int[] mNZoomPercents = { 10, 25, 50, 75, 100, 125, 150, 175, 200, 225, 250, 275, 300, 350, 400, 450,
			500, 550, 600, 650, 700, 800, 900, 999 };

	/** actions triggered by GUI events */
	Action a_new = new ActionNew();

	Action a_quit = new ActionQuit();

	Action a_save = new ActionSave();

	ActionExport a_export = new ActionExport();

	ActionPrint a_print = new ActionPrint();

	Action a_load = new ActionLoad();

	Action a_zoomin = new ActionZoomIn();

	Action a_zoomout = new ActionZoomOut();

	Action a_layout = new ActionLayout();

	Action a_saveas = new ActionSaveAs();

	Action a_viewtoolbar = new ActionViewToolbar();

	Action a_viewstatusbar = new ActionViewStatusbar();

	Action a_networkgenerator = new ActionGenerateNetwork();

	Action a_datagenerator = new ActionGenerateData();

	Action a_datasetter = new ActionSetData();

	Action a_learn = new ActionLearn();

	Action a_learnCPT = new ActionLearnCPT();

	Action a_help = new ActionHelp();

	Action a_about = new ActionAbout();

	ActionAddNode a_addnode = new ActionAddNode();

	Action a_delnode = new ActionDeleteNode();

	Action a_cutnode = new ActionCutNode();

	Action a_copynode = new ActionCopyNode();

	Action a_pastenode = new ActionPasteNode();

	Action a_selectall = new ActionSelectAll();

	Action a_addarc = new ActionAddArc();

	Action a_delarc = new ActionDeleteArc();

	Action a_undo = new ActionUndo();

	Action a_redo = new ActionRedo();

	Action a_alignleft = new ActionAlignLeft();

	Action a_alignright = new ActionAlignRight();

	Action a_aligntop = new ActionAlignTop();

	Action a_alignbottom = new ActionAlignBottom();

	Action a_centerhorizontal = new ActionCenterHorizontal();

	Action a_centervertical = new ActionCenterVertical();

	Action a_spacehorizontal = new ActionSpaceHorizontal();

	Action a_spacevertical = new ActionSpaceVertical();

	/** node currently selected through right clicking */
	int m_nCurrentNode = -1;

	/** selection of nodes */
	Selection m_Selection = new Selection();

	/** selection rectangle drawn through dragging with left mouse button */
	Rectangle m_nSelectedRect = null;

	ClipBoard m_clipboard = new ClipBoard();

	/**
	 * Constructor<br>
	 * Sets up the gui and initializes all the other previously uninitialized
	 * variables.
	 */
	public GUI() {
		m_GraphPanel = new GraphPanel();

		// creating a new layout engine and adding this class as its listener
		// to receive layoutComplete events

		m_jTfZoom = new JTextField("100%");
		m_jTfZoom.setMinimumSize(m_jTfZoom.getPreferredSize());
		m_jTfZoom.setHorizontalAlignment(JTextField.CENTER);
		m_jTfZoom.setToolTipText("Zoom");

		m_jTfZoom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				JTextField jt = (JTextField) ae.getSource();
				try {
					int i = -1;
					i = jt.getText().indexOf('%');
					if (i == -1) {
						i = Integer.parseInt(jt.getText());
					} else {
						i = Integer.parseInt(jt.getText().substring(0, i));
					}

					if (i <= 999) {
						mFScale = i / 100D;
					}

					jt.setText((int) (mFScale * 100) + "%");
					if (mFScale > 0.1) {
						if (!a_zoomout.isEnabled()) {
							a_zoomout.setEnabled(true);
						}
					} else {
						a_zoomout.setEnabled(false);
					}
					if (mFScale < 9.99) {
						if (!a_zoomin.isEnabled()) {
							a_zoomin.setEnabled(true);
						}
					} else {
						a_zoomin.setEnabled(false);
					}
					setAppropriateSize();
					// m_GraphPanel.clearBuffer();
					m_jScrollPane.revalidate();
				} catch (NumberFormatException ne) {
					JOptionPane.showMessageDialog(GUI.this.getParent(), "Invalid integer entered for zoom.", "Error",
							JOptionPane.ERROR_MESSAGE);
					jt.setText((mFScale * 100) + "%");
				}
			}
		});

		GridBagConstraints gbc = new GridBagConstraints();

		final JPanel p = new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("ExtraControls"),
				BorderFactory.createEmptyBorder(4, 4, 4, 4)));
		p.setPreferredSize(new Dimension(0, 0));

		m_jTbTools = new JToolBar();
		m_jTbTools.setFloatable(false);
		m_jTbTools.setLayout(new GridBagLayout());
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.insets = new Insets(0, 0, 0, 0);
		m_jTbTools.add(p, gbc);
		gbc.gridwidth = 1;

		m_jTbTools.add(a_new);
		m_jTbTools.add(a_save);
		m_jTbTools.add(a_load);
		m_jTbTools.addSeparator(new Dimension(2, 2));
		m_jTbTools.add(a_cutnode);
		m_jTbTools.add(a_copynode);
		m_jTbTools.add(a_pastenode);
		m_jTbTools.addSeparator(new Dimension(2, 2));
		m_jTbTools.add(a_undo);
		m_jTbTools.add(a_redo);
		m_jTbTools.addSeparator(new Dimension(2, 2));
		m_jTbTools.add(a_alignleft);
		m_jTbTools.add(a_alignright);
		m_jTbTools.add(a_aligntop);
		m_jTbTools.add(a_alignbottom);
		m_jTbTools.add(a_centerhorizontal);
		m_jTbTools.add(a_centervertical);
		m_jTbTools.add(a_spacehorizontal);
		m_jTbTools.add(a_spacevertical);

		m_jTbTools.addSeparator(new Dimension(2, 2));
		m_jTbTools.add(a_zoomin);

		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.weighty = 1;
		JPanel p2 = new JPanel(new BorderLayout());
		p2.setPreferredSize(m_jTfZoom.getPreferredSize());
		p2.setMinimumSize(m_jTfZoom.getPreferredSize());
		p2.add(m_jTfZoom, BorderLayout.CENTER);
		m_jTbTools.add(p2, gbc);
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;

		m_jTbTools.add(a_zoomout);
		m_jTbTools.addSeparator(new Dimension(2, 2));

		// jTbTools.add(jBtExtraControls, gbc);
		m_jTbTools.add(a_layout);
		m_jTbTools.addSeparator(new Dimension(4, 2));
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.BOTH;
		// jTbTools.add(m_layoutEngine.getProgressBar(), gbc);
		m_jStatusBar = new JLabel("Status bar");

		this.setLayout(new BorderLayout());
		this.add(m_jTbTools, BorderLayout.NORTH);
		this.add(m_jScrollPane, BorderLayout.CENTER);
		this.add(m_jStatusBar, BorderLayout.SOUTH);

		updateStatus();
		a_datagenerator.setEnabled(false);

		makeMenuBar();
	}

	/**
	 * Get the menu bar for this application.
	 * 
	 * @return the menu bar
	 */
	public JMenuBar getMenuBar() {
		return mMenuBar;
	}

	private void makeMenuBar() {
		mMenuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');

		mMenuBar.add(fileMenu);
		fileMenu.add(a_new);
		fileMenu.add(a_load);
		fileMenu.add(a_save);
		fileMenu.add(a_saveas);
		fileMenu.addSeparator();
		fileMenu.add(a_print);
		fileMenu.add(a_export);
		fileMenu.addSeparator();
		fileMenu.add(a_quit);
		JMenu editMenu = new JMenu("Edit");
		editMenu.setMnemonic('E');
		mMenuBar.add(editMenu);
		editMenu.add(a_undo);
		editMenu.add(a_redo);
		editMenu.addSeparator();
		editMenu.add(a_selectall);
		editMenu.add(a_delnode);
		editMenu.add(a_cutnode);
		editMenu.add(a_copynode);
		editMenu.add(a_pastenode);
		editMenu.addSeparator();
		editMenu.add(a_addnode);
		editMenu.add(a_addarc);
		editMenu.add(a_delarc);
		editMenu.addSeparator();
		editMenu.add(a_alignleft);
		editMenu.add(a_alignright);
		editMenu.add(a_aligntop);
		editMenu.add(a_alignbottom);
		editMenu.add(a_centerhorizontal);
		editMenu.add(a_centervertical);
		editMenu.add(a_spacehorizontal);
		editMenu.add(a_spacevertical);

		JMenu toolMenu = new JMenu("Tools");
		toolMenu.setMnemonic('T');
		toolMenu.add(a_networkgenerator);
		toolMenu.add(a_datagenerator);
		toolMenu.add(a_datasetter);
		toolMenu.add(a_learn);
		toolMenu.add(a_learnCPT);
		toolMenu.addSeparator();
		toolMenu.add(a_layout);
		toolMenu.addSeparator();
		final JCheckBoxMenuItem viewMargins = new JCheckBoxMenuItem("Show Margins", false);
		viewMargins.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				boolean bPrev = m_bViewMargins;
				m_bViewMargins = viewMargins.getState();
				if (bPrev == false && viewMargins.getState() == true) {
					updateStatus();
				}
				repaint();
			}
		});
		toolMenu.add(viewMargins);
		final JCheckBoxMenuItem viewCliques = new JCheckBoxMenuItem("Show Cliques", false);
		viewCliques.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				boolean bPrev = m_bViewCliques;
				m_bViewCliques = viewCliques.getState();
				if (bPrev == false && viewCliques.getState() == true) {
					updateStatus();
				}
				repaint();
			}
		});
		toolMenu.add(viewCliques);

		mMenuBar.add(toolMenu);
		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic('V');
		mMenuBar.add(viewMenu);
		viewMenu.add(a_zoomin);
		viewMenu.add(a_zoomout);
		viewMenu.addSeparator();
		viewMenu.add(a_viewtoolbar);
		viewMenu.add(a_viewstatusbar);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');
		mMenuBar.add(helpMenu);
		helpMenu.add(a_help);
		helpMenu.add(a_about);
	}

	/**
	 * This method sets the node size that is appropriate considering the maximum
	 * label size that is present. It is used internally when custom node size
	 * checkbox is unchecked.
	 */
	protected void setAppropriateNodeSize() {
		int strWidth;
		FontMetrics fm = this.getFontMetrics(this.getFont());
		int nMaxStringWidth = DEFAULT_NODE_WIDTH;
		mNNodeWidth = nMaxStringWidth + 4;
		mNPaddedNodeWidth = mNNodeWidth + PADDING;
		mJTfNodeWidth.setText(new Integer(mNNodeWidth).toString());

		mNNodeHeight = 2 * fm.getHeight();
		mJTfNodeHeight.setText(new Integer(mNNodeHeight).toString());
	}

	/**
	 * Sets the preferred size for m_GraphPanel GraphPanel to the minimum size that
	 * is neccessary to display the graph.
	 */
	public void setAppropriateSize() {
		int maxX = 0;
		int maxY = 0;

	} // setAppropriateSize

	/**
	 * This method is an implementation for LayoutCompleteEventListener class. It
	 * sets the size appropriate for m_GraphPanel GraphPanel and and revalidates
	 * it's container JScrollPane once a LayoutCompleteEvent is received from the
	 * LayoutEngine. Also, it updates positions of the Bayesian network stored in
	 * m_BayesNet.
	 */
	@Override
	public void layoutCompleted(LayoutCompleteEvent le) {
		LayoutEngine layoutEngine = m_layoutEngine; // (LayoutEngine)
													// le.getSource();
		ArrayList<Integer> nPosX = new ArrayList<>();
		ArrayList<Integer> nPosY = new ArrayList<>();
		for (int iNode = 0; iNode < layoutEngine.getNodes().size(); iNode++) {
			GraphNode gNode = layoutEngine.getNodes().get(iNode);
			if (gNode.nodeType == GraphNode.NORMAL) {
				nPosX.add(gNode.x);
				nPosY.add(gNode.y);
			}
		}
		m_jStatusBar.setText("Graph layed out");
		a_undo.setEnabled(true);
		a_redo.setEnabled(false);
		setAppropriateSize();
		m_jScrollPane.revalidate();
	} // layoutCompleted

	/**
	 * BIF reader<br>
	 * Reads a graph description in XMLBIF03 from an file with name sFileName
	 */
	public void readBIFFromFile(String sFileName) throws BIFFormatException, IOException {
		m_sFileName = sFileName;

		setAppropriateNodeSize();
		setAppropriateSize();
	} // readBIFFromFile

	/*
	 * read arff file from file sFileName and start new Bayesian network with nodes
	 * representing attributes in data set.
	 */
	void initFromArffFile(String sFileName) {
		try {
			Instances instances = new Instances(new FileReader(sFileName));
			m_Instances = instances;
			a_learn.setEnabled(true);
			a_learnCPT.setEnabled(true);
			setAppropriateNodeSize();
			setAppropriateSize();
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
	} // initFromArffFile

	/*
	 * apply graph layout algorithm to Bayesian network
	 */
	void layoutGraph() {
		try {
			ArrayList<GraphNode> m_nodes = new ArrayList<>();
			ArrayList<GraphEdge> m_edges = new ArrayList<>();
			updateStatus();
			m_layoutEngine = new HierarchicalBCEngine(m_nodes, m_edges, mNPaddedNodeWidth, mNNodeHeight);
			m_layoutEngine.addLayoutCompleteEventListener(this);
			m_layoutEngine.layoutGraph();
		} catch (Exception e) {
			e.printStackTrace();
		}
	} // layoutGraph

	/*
	 * Update status of various items that need regular updating such as enabled
	 * status of some menu items, marginal distributions if shown, repainting of
	 * graph.
	 */
	void updateStatus() {
		if (!m_bViewMargins && !m_bViewCliques) {
			repaint();
			return;
		}

		try {
			m_marginCalculator = new MarginCalculator();
			SerializedObject so;
			so = new SerializedObject(m_marginCalculator);
			m_marginCalculatorWithEvidence = (MarginCalculator) so.getObject();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		repaint();
	} // updateStatus

	/*
	 * add arc with node iChild as child. This pops up a selection list with
	 * potential parents for the child. All decendants and current parents are
	 * excluded from the list as is the child node itself.
	 * 
	 * @param iChild index of the node for which to add an arc
	 */
	void addArcInto(int iChild) {
		try {
			int nNodes = 5;
			boolean[] isNotAllowedAsParent = new boolean[nNodes];
			// prevent it being a parent of itself
			isNotAllowedAsParent[iChild] = true;
			// prevent a descendant being a parent, since it introduces cycles
			for (int i = 0; i < nNodes; i++) {
				for (int iNode = 0; iNode < nNodes; iNode++) {
					for (int iParent = 0; iParent < nNodes; iParent++) {
						if (isNotAllowedAsParent[iChild]) {
							isNotAllowedAsParent[iNode] = true;
						}
					}
				}
			}
			// count nr of remaining candidates
			int nCandidates = 0;
			for (int i = 0; i < nNodes; i++) {
				if (!isNotAllowedAsParent[i]) {
					nCandidates++;
				}
			}
			if (nCandidates == 0) {
				JOptionPane.showMessageDialog(null,
						"No potential parents available for this node. Choose another node as child node.");
				return;
			}
			String[] options = new String[nCandidates];
			int k = 0;
			for (int i = 0; i < nNodes; i++) {
				if (!isNotAllowedAsParent[i]) {
				}
			}
			String sParent = (String) JOptionPane.showInputDialog(null, "Select parent node ", "Nodes", 0, null,
					options, options[0]);
			if (sParent == null || "" == sParent) {
				return;
			}
			// update all data structures
			updateStatus();
		} catch (Exception e) {
			e.printStackTrace();
		}
	} // addArcInto

	/*
	 * deletes arc from node with name sParent into child with index iChild
	 */
	void deleteArc(int iChild, String sParent) {
		updateStatus();
	} // deleteArc

	/*
	 * deletes arc from node with index iParent into child with name sChild
	 */
	void deleteArc(String sChild, int iParent) {
		updateStatus();
	} // deleteArc

	/*
	 * deletes arc. Pops up list of arcs listed in 'options' as
	 * "<Node1> -> <Node2>".
	 */
	void deleteArc(String[] options) {
		String sResult = (String) JOptionPane.showInputDialog(null, "Select arc to delete", "Arcs", 0, null, options,
				options[0]);
		if (sResult != null && !("" == sResult)) {
			int nPos = sResult.indexOf(" -> ");
			String sParent = sResult.substring(0, nPos);
			String sChild = sResult.substring(nPos + 4);
			updateStatus();
		}
	} // deleteArc

	/*
	 * Rename node with index nTargetNode. Pops up window that allwos for entering a
	 * new name.
	 */
	void renameNode(int nTargetNode) {
		String sName = JOptionPane.showInputDialog(null, null, "New name for node", JOptionPane.OK_CANCEL_OPTION);
		if (sName == null || "" == sName) {
			return;
		}
		repaint();
	} // renameNode

	/*
	 * Rename value with name sValeu of a node with index nTargetNode. Pops up
	 * window that allows entering a new name.
	 */
	void renameValue(int nTargetNode, String sValue) {
		String sNewValue = JOptionPane.showInputDialog(null, "New name for value " + sValue, "Node " + null,
				JOptionPane.OK_CANCEL_OPTION);
		if (sNewValue == null || "" == sNewValue) {
			return;
		}
		a_undo.setEnabled(true);
		a_redo.setEnabled(false);
		repaint();
	} // renameValue

	/* delete a single node with index iNode */
	void deleteNode(int iNode) {
		updateStatus();
	} // deleteNode

	/*
	 * Add a value to currently selected node. Shows window that allows to enter the
	 * name of the value.
	 */
	void addValue() {
		// GraphNode n = (GraphNode) m_nodes.elementAt(m_nCurrentNode);
		String sValue = new String("Value");
		String sNewValue = JOptionPane.showInputDialog(null, "New value " + sValue, "Node " + null,
				JOptionPane.OK_CANCEL_OPTION);
		if (sNewValue == null || "" == sNewValue) {
			return;
		}
		updateStatus();
	} // addValue

	/*
	 * remove value with name sValue from the node with index nTargetNode
	 */
	void delValue(int nTargetNode, String sValue) {
		updateStatus();
	} // delValue

	/*
	 * Edits CPT of node with index nTargetNode. Pops up table with probability
	 * table that the user can change or just view.
	 */
	void editCPT(int nTargetNode) {
		m_nCurrentNode = nTargetNode;
		final GraphVisualizerTableModel tm = new GraphVisualizerTableModel();

		JTable jTblProbs = new JTable(tm);

		JScrollPane js = new JScrollPane(jTblProbs);

		int nParents = 5;
		if (nParents > 0) {
			GridBagConstraints gbc = new GridBagConstraints();
			JPanel jPlRowHeader = new JPanel(new GridBagLayout());

			// indices of the parent nodes in the Vector
			int[] idx = new int[nParents];
			// max length of values of each parent
			int[] lengths = new int[nParents];

			// Adding labels for rows
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0, 1, 0, 0);
			int addNum = 0;
			int temp = 0;
			boolean dark = false;
			while (true) {
				gbc.gridwidth = 1;
				for (int k = 0; k < nParents; k++) {
					int iParent2 = 8;
					JLabel lb = new JLabel("");
					lb.setFont(new Font("Dialog", Font.PLAIN, 12));
					lb.setOpaque(true);
					lb.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 1));
					lb.setHorizontalAlignment(JLabel.CENTER);
					if (dark) {
						lb.setBackground(lb.getBackground().darker());
						lb.setForeground(Color.white);
					} else {
						lb.setForeground(Color.black);
					}

					temp = lb.getPreferredSize().width;
					lb.setPreferredSize(new Dimension(temp, jTblProbs.getRowHeight()));
					if (lengths[k] < temp) {
						lengths[k] = temp;
					}
					temp = 0;

					if (k == nParents - 1) {
						gbc.gridwidth = GridBagConstraints.REMAINDER;
						dark = (dark == true) ? false : true;
					}
					jPlRowHeader.add(lb, gbc);
					addNum++;
				}

				int iParent2 = 8;
				if (idx[0] == iParent2) {
					JLabel lb = (JLabel) jPlRowHeader.getComponent(addNum - 1);
					jPlRowHeader.remove(addNum - 1);
					lb.setPreferredSize(new Dimension(lb.getPreferredSize().width, jTblProbs.getRowHeight()));
					gbc.gridwidth = GridBagConstraints.REMAINDER;
					gbc.weighty = 1;
					jPlRowHeader.add(lb, gbc);
					gbc.weighty = 0;
					break;
				}
			}

			gbc.gridwidth = 1;
			// The following panel contains the names of the
			// parents
			// and is displayed above the row names to identify
			// which value belongs to which parent
			JPanel jPlRowNames = new JPanel(new GridBagLayout());
			for (int j = 0; j < nParents; j++) {
				JLabel lb2;
				JLabel lb1 = new JLabel("");
				lb1.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 1));
				Dimension tempd = lb1.getPreferredSize();
				if (tempd.width < lengths[j]) {
					lb1.setPreferredSize(new Dimension(lengths[j], tempd.height));
					lb1.setHorizontalAlignment(JLabel.CENTER);
					lb1.setMinimumSize(new Dimension(lengths[j], tempd.height));
				} else if (tempd.width > lengths[j]) {
					lb2 = (JLabel) jPlRowHeader.getComponent(j);
					lb2.setPreferredSize(new Dimension(tempd.width, lb2.getPreferredSize().height));
				}
				jPlRowNames.add(lb1, gbc);
			}
			js.setRowHeaderView(jPlRowHeader);
			js.setCorner(JScrollPane.UPPER_LEFT_CORNER, jPlRowNames);
		}

		final JDialog dlg = new JDialog((Frame) GUI.this.getTopLevelAncestor(),
				"Probability Distribution Table For " + null, true);
		dlg.setSize(500, 400);
		dlg.setLocation(GUI.this.getLocation().x + GUI.this.getWidth() / 2 - 250,
				GUI.this.getLocation().y + GUI.this.getHeight() / 2 - 200);

		dlg.getContentPane().setLayout(new BorderLayout());
		dlg.getContentPane().add(js, BorderLayout.CENTER);

		JButton jBtRandomize = new JButton("Randomize");
		jBtRandomize.setMnemonic('R');
		jBtRandomize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				tm.randomize();
				dlg.repaint();
			}
		});

		JButton jBtOk = new JButton("Ok");
		jBtOk.setMnemonic('O');
		jBtOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				tm.setData();

				dlg.setVisible(false);
			}
		});
		JButton jBtCancel = new JButton("Cancel");
		jBtCancel.setMnemonic('C');
		jBtCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				dlg.setVisible(false);
			}
		});
		Container c = new Container();
		c.setLayout(new GridBagLayout());
		c.add(jBtRandomize);
		c.add(jBtOk);
		c.add(jBtCancel);

		dlg.getContentPane().add(c, BorderLayout.SOUTH);
		dlg.setVisible(true);
	} // editCPT

	/**
	 * Main method. Builds up menus and reads from file if one is specified.
	 */
	public static void main(String[] args) {

		LookAndFeel.setLookAndFeel();

		JFrame jf = new JFrame("Bayes Network Editor");
		final GUI g = new GUI();
		JMenuBar menuBar = g.getMenuBar();

		if (args.length > 0) {
			try {
				g.readBIFFromFile(args[0]);
			} catch (BIFFormatException | IOException bf) {
				bf.printStackTrace();
			}
		}

		jf.setJMenuBar(menuBar);
		jf.getContentPane().add(g);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(800, 600);
		jf.setVisible(true);
		g.m_Selection.updateGUI();
		GenericObjectEditor.registerEditors();
	} // main

	class Selection {
		ArrayList<Integer> m_selected;

		public Selection() {
			m_selected = new ArrayList<>();
		} // c'tor

		public ArrayList<Integer> getSelected() {
			return m_selected;
		}

		void updateGUI() {
			if (m_selected.size() > 0) {
				a_cutnode.setEnabled(true);
				a_copynode.setEnabled(true);
			} else {
				a_cutnode.setEnabled(false);
				a_copynode.setEnabled(false);
			}
			if (m_selected.size() > 1) {
				a_alignleft.setEnabled(true);
				a_alignright.setEnabled(true);
				a_aligntop.setEnabled(true);
				a_alignbottom.setEnabled(true);
				a_centerhorizontal.setEnabled(true);
				a_centervertical.setEnabled(true);
				a_spacehorizontal.setEnabled(true);
				a_spacevertical.setEnabled(true);
			} else {
				a_alignleft.setEnabled(false);
				a_alignright.setEnabled(false);
				a_aligntop.setEnabled(false);
				a_alignbottom.setEnabled(false);
				a_centerhorizontal.setEnabled(false);
				a_centervertical.setEnabled(false);
				a_spacehorizontal.setEnabled(false);
				a_spacevertical.setEnabled(false);
			}
		} // updateGUI

		public void addToSelection(int nNode) {
			for (Integer iterator : m_selected) {
				if (nNode == iterator) {
					return;
				}
			}
			m_selected.add(nNode);
			updateGUI();
		} // addToSelection

		public void addToSelection(int[] iNodes) {
			for (int iNode2 : iNodes) {
				addToSelection(iNode2);
			}
			updateGUI();
		} // addToSelection

		public void addToSelection(Rectangle selectedRect) {
			for (int iNode = 0; iNode < 5; iNode++) {
				if (contains(selectedRect, iNode)) {
					addToSelection(iNode);
				}
			}
		} // addToSelection

		public void selectAll() {
			m_selected.removeAll(m_selected);
			for (int iNode = 0; iNode < 5; iNode++) {
				m_selected.add(iNode);
			}
			updateGUI();
		} // selectAll

		public int getSumOfSelection() {
			int sum = 0;
			for (int n : m_selected) {
				sum += n;
			}
			return sum;
		} // getSum

		boolean contains(Rectangle rect, int iNode) {
			return false;
		} // contains

		public void removeFromSelection(int nNode) {
			for (int iNode = 0; iNode < m_selected.size(); iNode++) {
				if (nNode == m_selected.get(iNode)) {
					m_selected.remove(iNode);
				}
			}
			updateGUI();
		} // removeFromSelection

		public void toggleSelection(int nNode) {
			for (int iNode = 0; iNode < m_selected.size(); iNode++) {
				if (nNode == m_selected.get(iNode)) {
					m_selected.remove(iNode);
					updateGUI();
					return;
				}
			}
			addToSelection(nNode);
		} // toggleSelection

		public void toggleSelection(Rectangle selectedRect) {
			for (int iNode = 0; iNode < 5; iNode++) {
				if (contains(selectedRect, iNode)) {
					toggleSelection(iNode);
				}
			}
		} // toggleSelection

		public void clear() {
			m_selected.removeAll(m_selected);
			updateGUI();
		}

		public void draw(Graphics g) {
			if (m_selected.size() == 0) {
				return;
			}

			m_selected.forEach(nNode -> {
				int nPosX = 0;
				int nPosY = 0;
				g.setColor(Color.BLACK);
				int nXRC = nPosX + mNPaddedNodeWidth - mNNodeWidth - (mNPaddedNodeWidth - mNNodeWidth) / 2;
				int nYRC = nPosY;
				int d = 5;
				g.fillRect(nXRC, nYRC, d, d);
				g.fillRect(nXRC, nYRC + mNNodeHeight, d, d);
				g.fillRect(nXRC + mNNodeWidth, nYRC, d, d);
				g.fillRect(nXRC + mNNodeWidth, nYRC + mNNodeHeight, d, d);
			});
		} // draw
	} // Selection

	class ClipBoard {
		String m_sText = null;

		public ClipBoard() {
			if (a_pastenode != null) {
				a_pastenode.setEnabled(false);
			}
		}

		public boolean hasText() {
			return m_sText != null;
		}

		public String getText() {
			return m_sText;
		}

		public void setText(String sText) {
			m_sText = sText;
			a_pastenode.setEnabled(true);
		}
	} // class ClipBoard

	/**
	 * Base class used for definining actions with a name, tool tip text, possibly
	 * an icon and accelerator key.
	 */
	class MyAction extends AbstractAction {
		/** for serialization */
		private static final long serialVersionUID = -2038911111935517L;

		public MyAction(String sName, String sToolTipText, String sIcon, String sAcceleratorKey) {
			super(sName);
			// setToolTipText(sToolTipText);
			putValue(Action.SHORT_DESCRIPTION, sToolTipText);
			putValue(Action.LONG_DESCRIPTION, sToolTipText);
			if (sAcceleratorKey.length() > 0) {
				KeyStroke keyStroke = KeyStroke.getKeyStroke(sAcceleratorKey);
				putValue(Action.ACCELERATOR_KEY, keyStroke);
			}
			putValue(Action.MNEMONIC_KEY, (int) sName.charAt(0));
			java.net.URL tempURL = ClassLoader.getSystemResource(iconpath + sIcon + ".png");
			if (tempURL != null) {
				putValue(Action.SMALL_ICON, new ImageIcon(tempURL));
			} else {
				putValue(Action.SMALL_ICON, new ImageIcon(new BufferedImage(20, 20, BufferedImage.TYPE_4BYTE_ABGR)));
				// System.err.println(ICONPATH + sIcon +
				// ".png not found for weka.gui.graphvisualizer.Graph");
			}
		} // c'tor

		/*
		 * Place holder. Should be implemented by derived classes. (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.
		 * ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent ae) {
		}
	} // class MyAction

	class ActionGenerateNetwork extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -2038911085935517L;

		int m_nNrOfNodes = 10;

		int m_nNrOfArcs = 15;

		int m_nCardinality = 2;

		int m_nSeed = 123;

		JDialog dlg = null;

		public ActionGenerateNetwork() {
			super("Generate Network", "Generate Random Bayesian Network", "generate.network", "ctrl N");
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			if (dlg == null) {
				dlg = new JDialog();
				dlg.setTitle("Generate Random Bayesian Network Options");

				final JLabel jLbNrOfNodes = new JLabel("Nr of nodes");
				final JTextField jTfNrOfNodes = new JTextField(3);
				jTfNrOfNodes.setHorizontalAlignment(JTextField.CENTER);
				jTfNrOfNodes.setText(new Integer(m_nNrOfNodes).toString());
				final JLabel jLbNrOfArcs = new JLabel("Nr of arcs");
				final JTextField jTfNrOfArcs = new JTextField(3);
				jTfNrOfArcs.setHorizontalAlignment(JTextField.CENTER);
				jTfNrOfArcs.setText(new Integer(m_nNrOfArcs).toString());
				final JLabel jLbCardinality = new JLabel("Cardinality");
				final JTextField jTfCardinality = new JTextField(3);
				jTfCardinality.setHorizontalAlignment(JTextField.CENTER);
				jTfCardinality.setText(new Integer(m_nCardinality).toString());
				final JLabel jLbSeed = new JLabel("Random seed");
				final JTextField jTfSeed = new JTextField(3);
				jTfSeed.setHorizontalAlignment(JTextField.CENTER);
				jTfSeed.setText(new Integer(m_nSeed).toString());

				JButton jBtGo;
				jBtGo = new JButton("Generate Network");

				JButton jBtCancel;
				jBtCancel = new JButton("Cancel");
				jBtCancel.setMnemonic('C');
				jBtCancel.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						dlg.setVisible(false);
					}
				});
				GridBagConstraints gbc = new GridBagConstraints();
				dlg.setLayout(new GridBagLayout());

				Container c = new Container();
				c.setLayout(new GridBagLayout());
				gbc.gridwidth = 2;
				gbc.insets = new Insets(8, 0, 0, 0);
				gbc.anchor = GridBagConstraints.NORTHWEST;
				gbc.gridwidth = GridBagConstraints.RELATIVE;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				c.add(jLbNrOfNodes, gbc);
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				c.add(jTfNrOfNodes, gbc);
				gbc.gridwidth = GridBagConstraints.RELATIVE;
				c.add(jLbNrOfArcs, gbc);
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				c.add(jTfNrOfArcs, gbc);
				gbc.gridwidth = GridBagConstraints.RELATIVE;
				c.add(jLbCardinality, gbc);
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				c.add(jTfCardinality, gbc);
				gbc.gridwidth = GridBagConstraints.RELATIVE;
				c.add(jLbSeed, gbc);
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				c.add(jTfSeed, gbc);

				gbc.fill = GridBagConstraints.HORIZONTAL;
				dlg.add(c, gbc);
				dlg.add(jBtGo);
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				dlg.add(jBtCancel);
			}
			dlg.pack();
			dlg.setLocation(100, 100);
			dlg.setVisible(true);
			dlg.setSize(dlg.getPreferredSize());
			dlg.setVisible(false);
			dlg.setVisible(true);
			dlg.repaint();
		} // actionPerformed
	} // class ActionGenerate

	class ActionGenerateData extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -2038911085935516L;

		int m_nNrOfInstances = 100;

		int m_nSeed = 1234;

		String m_sFile = new String("");

		JDialog dlg = null;

		public ActionGenerateData() {
			super("Generate Data", "Generate Random Instances from Network", "generate.data", "ctrl D");
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			if (dlg == null) {
				dlg = new JDialog();
				dlg.setTitle("Generate Random Data Options");

				final JLabel jLbNrOfInstances = new JLabel("Nr of instances");
				final JTextField jTfNrOfInstances = new JTextField(3);
				jTfNrOfInstances.setHorizontalAlignment(JTextField.CENTER);
				jTfNrOfInstances.setText(new Integer(m_nNrOfInstances).toString());
				final JLabel jLbSeed = new JLabel("Random seed");
				final JTextField jTfSeed = new JTextField(3);
				jTfSeed.setHorizontalAlignment(JTextField.CENTER);
				jTfSeed.setText(new Integer(m_nSeed).toString());
				final JLabel jLbFile = new JLabel("Output file (optional)");
				final JTextField jTfFile = new JTextField(12);
				jTfFile.setHorizontalAlignment(JTextField.CENTER);
				jTfFile.setText(m_sFile);

				JButton jBtGo;
				jBtGo = new JButton("Generate Data");

				JButton jBtFile = new JButton("Browse");
				jBtFile.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						ConverterFileChooser fc = new ConverterFileChooser(System.getProperty("user.dir"));
						fc.setDialogTitle("Save Instances As");
						int rval = fc.showSaveDialog(GUI.this);

						if (rval == JFileChooser.APPROVE_OPTION) {
							String filename = fc.getSelectedFile() + "";
							jTfFile.setText(filename);
						}
						dlg.setVisible(true);
					}
				});
				JButton jBtCancel;
				jBtCancel = new JButton("Cancel");
				jBtCancel.setMnemonic('C');
				jBtCancel.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						dlg.setVisible(false);
					}
				});
				GridBagConstraints gbc = new GridBagConstraints();
				dlg.setLayout(new GridBagLayout());

				Container c = new Container();
				c.setLayout(new GridBagLayout());
				gbc.gridwidth = 2;
				gbc.insets = new Insets(8, 0, 0, 0);
				gbc.anchor = GridBagConstraints.NORTHWEST;
				gbc.gridwidth = GridBagConstraints.RELATIVE;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				c.add(jLbNrOfInstances, gbc);
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				c.add(jTfNrOfInstances, gbc);
				gbc.gridwidth = GridBagConstraints.RELATIVE;
				c.add(jLbSeed, gbc);
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				c.add(jTfSeed, gbc);
				gbc.gridwidth = GridBagConstraints.RELATIVE;
				c.add(jLbFile, gbc);
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				c.add(jTfFile, gbc);
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				c.add(jBtFile, gbc);

				gbc.fill = GridBagConstraints.HORIZONTAL;
				dlg.add(c, gbc);
				dlg.add(jBtGo);
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				dlg.add(jBtCancel);
			}
			dlg.setLocation(100, 100);
			dlg.setVisible(true);
			dlg.setSize(dlg.getPreferredSize());
			dlg.setVisible(false);
			dlg.setVisible(true);
			dlg.repaint();

		} // actionPerformed
	} // class ActionGenerateData

	class ActionLearn extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -2038911085935516L;
		JDialog dlg = null;

		public ActionLearn() {
			super("Learn Network", "Learn Bayesian Network", "learn", "ctrl L");
			setEnabled(false);
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			if (dlg == null) {
				dlg = new JDialog();
				dlg.setTitle("Learn Bayesian Network");

				final JButton jBtOptions = new JButton("Options");

				final JTextField jTfOptions = new JTextField(40);
				jTfOptions.setHorizontalAlignment(JTextField.CENTER);

				JButton jBtGo;
				jBtGo = new JButton("Learn");

				jBtGo.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						try {
							layoutGraph();
							updateStatus();

							dlg.setVisible(false);
						} catch (Exception e) {
							e.printStackTrace();
						}
						dlg.setVisible(false);
					}
				});

				JButton jBtCancel;
				jBtCancel = new JButton("Cancel");
				jBtCancel.setMnemonic('C');
				jBtCancel.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						dlg.setVisible(false);
					}
				});
				GridBagConstraints gbc = new GridBagConstraints();
				dlg.setLayout(new GridBagLayout());

				Container c = new Container();
				c.setLayout(new GridBagLayout());
				gbc.gridwidth = 2;
				gbc.insets = new Insets(8, 0, 0, 0);
				gbc.anchor = GridBagConstraints.NORTHWEST;
				gbc.gridwidth = GridBagConstraints.RELATIVE;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				c.add(jBtOptions, gbc);
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				c.add(jTfOptions, gbc);

				gbc.fill = GridBagConstraints.HORIZONTAL;
				dlg.add(c, gbc);
				dlg.add(jBtGo);
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				dlg.add(jBtCancel);
			}
			dlg.setLocation(100, 100);
			dlg.setVisible(true);
			dlg.setSize(dlg.getPreferredSize());
			dlg.setVisible(false);
			dlg.setVisible(true);
			dlg.repaint();
		} // actionPerformed
	} // class ActionLearn

	class ActionLearnCPT extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -2022211085935516L;

		public ActionLearnCPT() {
			super("Learn CPT", "Learn conditional probability tables", "learncpt", "");
			setEnabled(false);
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			if (m_Instances == null) {
				JOptionPane.showMessageDialog(null, "Select instances to learn from first (menu Tools/Set Data)");
				return;
			}
			updateStatus();
		} // actionPerformed
	} // class ActionLearnCPT

	class ActionSetData extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -2038911085935519L;

		public ActionSetData() {
			super("Set Data", "Set Data File", "setdata", "ctrl A");
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			ConverterFileChooser fc = new ConverterFileChooser(System.getProperty("user.dir"));
			fc.setDialogTitle("Set Data File");
			int rval = fc.showOpenDialog(GUI.this);

			if (rval == JFileChooser.APPROVE_OPTION) {
				AbstractFileLoader loader = fc.getLoader();
				try {
					if (loader != null) {
						m_Instances = loader.getDataSet();
					}
					if (m_Instances.classIndex() == -1) {
						m_Instances.setClassIndex(m_Instances.numAttributes() - 1);
					}
					a_learn.setEnabled(true);
					a_learnCPT.setEnabled(true);
					repaint();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	} // class ActionSetData

	class ActionUndo extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -3038910085935519L;

		public ActionUndo() {
			super("Undo", "Undo", "undo", "ctrl Z");
			setEnabled(false);
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			String sMsg = new String("");
			m_jStatusBar.setText("Undo action performed: " + sMsg);
			// if (!sMsg.equals("")) {
			// JOptionPane.showMessageDialog(null, sMsg, "Undo action
			// successful",
			// JOptionPane.INFORMATION_MESSAGE);
			// }
			m_Selection.clear();
			updateStatus();
			repaint();
		}
	} // ActionUndo

	class ActionRedo extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -4038910085935519L;

		public ActionRedo() {
			super("Redo", "Redo", "redo", "ctrl Y");
			setEnabled(false);
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			String sMsg = new String("");
			m_jStatusBar.setText("Redo action performed: " + sMsg);
			// if (!sMsg.equals("")) {
			// JOptionPane.showMessageDialog(null, sMsg, "Redo action
			// successful",
			// JOptionPane.INFORMATION_MESSAGE);
			// }
			m_Selection.clear();
			updateStatus();
			repaint();
		}
	} // ActionRedo

	class ActionAddNode extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -2038910085935519L;

		JDialog dlg = null;

		JTextField jTfName = new JTextField(20);

		JTextField jTfCard = new JTextField(3);

		int m_X = Integer.MAX_VALUE;

		int m_Y;

		public ActionAddNode() {
			super("Add Node", "Add Node", "addnode", "");
		} // c'tor

		public void addNode(int nX, int nY) {
			m_X = nX;
			m_Y = nY;
			addNode();
		} // addNode

		void addNode() {
			if (dlg == null) {
				dlg = new JDialog();
				dlg.setTitle("Add node");
				JLabel jLbName = new JLabel("Name");
				jTfName.setHorizontalAlignment(JTextField.CENTER);
				JLabel jLbCard = new JLabel("Cardinality");
				jTfCard.setHorizontalAlignment(JTextField.CENTER);
				jTfCard.setText("2");

				JButton jBtCancel;
				jBtCancel = new JButton("Cancel");
				jBtCancel.setMnemonic('C');
				jBtCancel.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						dlg.setVisible(false);
					}
				});
				JButton jBtOk = new JButton("Ok");
				jBtOk.setMnemonic('O');
				jBtOk.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						String sName = jTfName.getText();
						if (sName.length() <= 0) {
							JOptionPane.showMessageDialog(null, "Name should have at least one character");
							return;
						}
						int nCard = Integer.valueOf(jTfCard.getText()).intValue();
						if (nCard <= 1) {
							JOptionPane.showMessageDialog(null, "Cardinality should be larger than 1");
							return;
						}
						repaint();
						dlg.setVisible(false);
					}
				});
				dlg.setLayout(new GridLayout(3, 2, 10, 10));
				dlg.add(jLbName);
				dlg.add(jTfName);
				dlg.add(jLbCard);
				dlg.add(jTfCard);
				dlg.add(jBtOk);
				dlg.add(jBtCancel);
				dlg.setSize(dlg.getPreferredSize());
			}
			dlg.setVisible(true);
		} // addNode

		@Override
		public void actionPerformed(ActionEvent ae) {
			m_X = Integer.MAX_VALUE;
			addNode();
		}
	} // class ActionAddNode

	class ActionDeleteNode extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -2038912085935519L;

		public ActionDeleteNode() {
			super("Delete Node", "Delete Node", "delnode", "DELETE");
		} // c'tor

	} // class ActionDeleteNode

	class ActionCopyNode extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -2038732085935519L;

		public ActionCopyNode() {
			super("Copy", "Copy Nodes", "copy", "ctrl C");
		} // c'tor

		public ActionCopyNode(String sName, String sToolTipText, String sIcon, String sAcceleratorKey) {
			super(sName, sToolTipText, sIcon, sAcceleratorKey);
		} // c'rot

		@Override
		public void actionPerformed(ActionEvent ae) {
			copy();
		}

		public void copy() {
			String sXML = new String("");
			m_clipboard.setText(sXML);
		} // copy
	} // class ActionCopyNode

	class ActionCutNode extends ActionCopyNode {
		/** for serialization */
		private static final long serialVersionUID = -2038822085935519L;

		public ActionCutNode() {
			super("Cut", "Cut Nodes", "cut", "ctrl X");
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			copy();
			m_Selection.clear();
			a_undo.setEnabled(true);
			a_redo.setEnabled(false);
			repaint();
		}
	} // class ActionCutNode

	class ActionPasteNode extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -2038732085935519L;

		public ActionPasteNode() {
			super("Paste", "Paste Nodes", "paste", "ctrl V");
		} // c'tor

		@Override
		public boolean isEnabled() {
			return m_clipboard.hasText();
		}
	} // class ActionPasteNode

	class ActionSelectAll extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -2038642085935519L;

		public ActionSelectAll() {
			super("Select All", "Select All Nodes", "selectall", "ctrl A");
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			m_Selection.selectAll();
			repaint();
		}
	} // class ActionSelectAll

	class ActionExport extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -3027642085935519L;
		boolean m_bIsExporting = false;

		public ActionExport() {
			super("Export", "Export to graphics file", "export", "");
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			m_bIsExporting = true;
			m_bIsExporting = false;
			repaint();
		}

		public boolean isExporting() {
			return m_bIsExporting;
		}
	} // class ActionExport

	class ActionAlignLeft extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -3138642085935519L;

		public ActionAlignLeft() {
			super("Align Left", "Align Left", "alignleft", "");
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			a_undo.setEnabled(true);
			a_redo.setEnabled(false);
			repaint();
		}
	} // class ActionAlignLeft

	class ActionAlignRight extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -4238642085935519L;

		public ActionAlignRight() {
			super("Align Right", "Align Right", "alignright", "");
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			a_undo.setEnabled(true);
			a_redo.setEnabled(false);
			repaint();
		}
	} // class ActionAlignRight

	class ActionAlignTop extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -5338642085935519L;

		public ActionAlignTop() {
			super("Align Top", "Align Top", "aligntop", "");
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			a_undo.setEnabled(true);
			a_redo.setEnabled(false);
			repaint();
		}
	} // class ActionAlignTop

	class ActionAlignBottom extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -6438642085935519L;

		public ActionAlignBottom() {
			super("Align Bottom", "Align Bottom", "alignbottom", "");
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			a_undo.setEnabled(true);
			a_redo.setEnabled(false);
			repaint();
		}
	} // class ActionAlignBottom

	class ActionCenterHorizontal extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -7538642085935519L;

		public ActionCenterHorizontal() {
			super("Center Horizontal", "Center Horizontal", "centerhorizontal", "");
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			a_undo.setEnabled(true);
			a_redo.setEnabled(false);
			repaint();
		}
	} // class ActionCenterHorizontal

	class ActionCenterVertical extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -8638642085935519L;

		public ActionCenterVertical() {
			super("Center Vertical", "Center Vertical", "centervertical", "");
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			a_undo.setEnabled(true);
			a_redo.setEnabled(false);
			repaint();
		}
	} // class ActionCenterVertical

	class ActionSpaceHorizontal extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -9738642085935519L;

		public ActionSpaceHorizontal() {
			super("Space Horizontal", "Space Horizontal", "spacehorizontal", "");
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			a_undo.setEnabled(true);
			a_redo.setEnabled(false);
			repaint();
		}
	} // class ActionSpaceHorizontal

	class ActionSpaceVertical extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -838642085935519L;

		public ActionSpaceVertical() {
			super("Space Vertical", "Space Vertical", "spacevertical", "");
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			a_undo.setEnabled(true);
			a_redo.setEnabled(false);
			repaint();
		}
	} // class ActionSpaceVertical

	class ActionAddArc extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -2038913085935519L;

		public ActionAddArc() {
			super("Add Arc", "Add Arc", "addarc", "");
		} // c'tor

	} // class ActionAddArc

	class ActionDeleteArc extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -2038914085935519L;

		public ActionDeleteArc() {
			super("Delete Arc", "Delete Arc", "delarc", "");
		} // c'tor

	} // class ActionDeleteArc

	class ActionNew extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -2038911085935515L;

		public ActionNew() {
			super("New", "New Network", "new", "");
		} // c'tor

	} // class ActionNew

	class ActionLoad extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -2038911085935515L;

		public ActionLoad() {
			super("Load", "Load Graph", "open", "ctrl O");
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
			ExtensionFileFilter ef1 = new ExtensionFileFilter(".arff", "ARFF files");
			ExtensionFileFilter ef2 = new ExtensionFileFilter(".xml", "XML BIF files");
			fc.addChoosableFileFilter(ef1);
			fc.addChoosableFileFilter(ef2);
			fc.setDialogTitle("Load Graph");
			int rval = fc.showOpenDialog(GUI.this);

			if (rval == JFileChooser.APPROVE_OPTION) {
				String sFileName = fc.getSelectedFile() + "";
				if (sFileName.endsWith(ef1.getExtensions()[0])) {
					initFromArffFile(sFileName);
				} else {
					try {
						readBIFFromFile(sFileName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				m_jStatusBar.setText("Loaded " + sFileName);
				updateStatus();
			}
		}
	} // class ActionLoad

	class ActionViewStatusbar extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -20389330812354L;

		public ActionViewStatusbar() {
			super("View statusbar", "View statusbar", "statusbar", "");
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			m_jStatusBar.setVisible(!m_jStatusBar.isVisible());
		} // actionPerformed
	} // class ActionViewStatusbar

	class ActionViewToolbar extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -20389110812354L;

		public ActionViewToolbar() {
			super("View toolbar", "View toolbar", "toolbar", "");
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			m_jTbTools.setVisible(!m_jTbTools.isVisible());
		} // actionPerformed
	} // class ActionViewToolbar

	class ActionSave extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -20389110859355156L;
		ExtensionFileFilter ef1 = new ExtensionFileFilter(".xml", "XML BIF files");

		public ActionSave() {
			super("Save", "Save Graph", "save", "ctrl S");
		} // c'tor

		public ActionSave(String sName, String sToolTipText, String sIcon, String sAcceleratorKey) {
			super(sName, sToolTipText, sIcon, sAcceleratorKey);
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			if (!("" == m_sFileName)) {
				saveFile(m_sFileName);
				m_jStatusBar.setText("Saved as " + m_sFileName);
			} else {
				if (saveAs()) {
					m_jStatusBar.setText("Saved as " + m_sFileName);
				}
			}
		} // actionPerformed

		boolean saveAs() {
			JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
			fc.addChoosableFileFilter(ef1);
			fc.setDialogTitle("Save Graph As");
			if (!("" == m_sFileName)) {
				// can happen on actionQuit
				fc.setSelectedFile(new File(m_sFileName));
			}
			int rval = fc.showSaveDialog(GUI.this);

			if (rval == JFileChooser.APPROVE_OPTION) {
				// System.out.println("Saving to file \""+
				// f.getAbsoluteFile().toString()+"\"");
				String sFileName = fc.getSelectedFile() + "";
				if (!sFileName.endsWith(".xml")) {
					sFileName = sFileName + ".xml";
				}
				saveFile(sFileName);
				return true;
			}
			return false;
		} // saveAs

		protected void saveFile(String sFileName) {
			try (FileWriter outfile = new FileWriter(sFileName)) {
				outfile.write(m_jStatusBar.getText());
				m_sFileName = sFileName;
				m_jStatusBar.setText("Saved as " + m_sFileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} // saveFile
	} // class ActionSave

	class ActionSaveAs extends ActionSave {
		/** for serialization */
		private static final long serialVersionUID = -20389110859354L;

		public ActionSaveAs() {
			super("Save As", "Save Graph As", "saveas", "");
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			saveAs();
		} // actionPerformed
	} // class ActionSaveAs

	class ActionPrint extends ActionSave {
		/** for serialization */
		private static final long serialVersionUID = -20389001859354L;
		boolean m_bIsPrinting = false;

		public ActionPrint() {
			super("Print", "Print Graph", "print", "ctrl P");
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			PrinterJob printJob = PrinterJob.getPrinterJob();
			printJob.setPrintable(m_GraphPanel);
			if (printJob.printDialog()) {
				try {
					m_bIsPrinting = true;
					printJob.print();
					m_bIsPrinting = false;
				} catch (PrinterException pe) {
					m_jStatusBar.setText("Error printing: " + pe);
					m_bIsPrinting = false;
				}
			}
			m_jStatusBar.setText("Print");
		} // actionPerformed

		public boolean isPrinting() {
			return m_bIsPrinting;
		}

	} // class ActionPrint

	class ActionQuit extends ActionSave {
		/** for serialization */
		private static final long serialVersionUID = -2038911085935515L;

		public ActionQuit() {
			super("Exit", "Exit Program", "exit", "");
		} // c'tor

	} // class ActionQuit

	class ActionHelp extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -20389110859354L;

		public ActionHelp() {
			super("Help", "Bayesian Network Workbench Help", "help", "");
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			JOptionPane.showMessageDialog(null, "See Weka Homepage\nhttp://www.cs.waikato.ac.nz/ml", "Help Message",
					JOptionPane.PLAIN_MESSAGE);
		}
	} // class ActionHelp

	class ActionAbout extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -20389110859353L;

		public ActionAbout() {
			super("About", "Help about", "about", "");
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			JOptionPane.showMessageDialog(null, "Bayesian Network Workbench\nPart of Weka\n2007", "About Message",
					JOptionPane.PLAIN_MESSAGE);
		}
	} // class ActionAbout

	class ActionZoomIn extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -2038911085935515L;

		public ActionZoomIn() {
			super("Zoom in", "Zoom in", "zoomin", "+");
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			int i = 0;
			int s = (int) (mFScale * 100);
			if (s < 300) {
				i = s / 25;
			} else if (s < 700) {
				i = 6 + s / 50;
			} else {
				i = 13 + s / 100;
			}

			if (s >= 999) {
				setEnabled(false);
				return;
			} else if (s >= 10) {
				if (i >= 22) {
					setEnabled(false);
				}
				if (s == 10 && !a_zoomout.isEnabled()) {
					a_zoomout.setEnabled(true);
				}
				m_jTfZoom.setText(mNZoomPercents[i + 1] + "%");
				mFScale = mNZoomPercents[i + 1] / 100D;
			} else {
				if (!a_zoomout.isEnabled()) {
					a_zoomout.setEnabled(true);
				}
				m_jTfZoom.setText(mNZoomPercents[0] + "%");
				mFScale = mNZoomPercents[0] / 100D;
			}
			setAppropriateSize();
			m_jScrollPane.revalidate();
			m_jStatusBar.setText("Zooming in");
		}
	} // class ActionZoomIn

	class ActionZoomOut extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -203891108593551L;

		public ActionZoomOut() {
			super("Zoom out", "Zoom out", "zoomout", "-");
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			int i = 0;
			int s = (int) (mFScale * 100);
			if (s < 300) {
				i = (int) Math.ceil(s / 25D);
			} else if (s < 700) {
				i = 6 + (int) Math.ceil(s / 50D);
			} else {
				i = 13 + (int) Math.ceil(s / 100D);
			}

			if (s <= 10) {
				setEnabled(false);
			} else if (s < 999) {
				if (i <= 1) {
					setEnabled(false);
				}
				m_jTfZoom.setText(mNZoomPercents[i - 1] + "%");
				mFScale = mNZoomPercents[i - 1] / 100D;
			} else {
				if (!a_zoomin.isEnabled()) {
					a_zoomin.setEnabled(true);
				}
				m_jTfZoom.setText(mNZoomPercents[22] + "%");
				mFScale = mNZoomPercents[22] / 100D;
			}
			setAppropriateSize();
			m_jScrollPane.revalidate();
			m_jStatusBar.setText("Zooming out");
		}
	} // class ActionZoomOut

	class ActionLayout extends MyAction {
		/** for serialization */
		private static final long serialVersionUID = -203891108593551L;
		JDialog dlg = null;

		public ActionLayout() {
			super("Layout", "Layout Graph", "layout", "ctrl L");
		} // c'tor

		@Override
		public void actionPerformed(ActionEvent ae) {
			if (dlg == null) {
				dlg = new JDialog();
				dlg.setTitle("Graph Layout Options");
				final JCheckBox jCbCustomNodeSize = new JCheckBox("Custom Node Size");
				final JLabel jLbNodeWidth = new JLabel("Width");
				final JLabel jLbNodeHeight = new JLabel("Height");

				mJTfNodeWidth.setHorizontalAlignment(JTextField.CENTER);
				mJTfNodeWidth.setText(new Integer(mNNodeWidth).toString());
				mJTfNodeHeight.setHorizontalAlignment(JTextField.CENTER);
				mJTfNodeHeight.setText(new Integer(mNNodeHeight).toString());
				jLbNodeWidth.setEnabled(false);
				mJTfNodeWidth.setEnabled(false);
				jLbNodeHeight.setEnabled(false);
				mJTfNodeHeight.setEnabled(false);

				jCbCustomNodeSize.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						if (((JCheckBox) ae.getSource()).isSelected()) {
							jLbNodeWidth.setEnabled(true);
							mJTfNodeWidth.setEnabled(true);
							jLbNodeHeight.setEnabled(true);
							mJTfNodeHeight.setEnabled(true);
						} else {
							jLbNodeWidth.setEnabled(false);
							mJTfNodeWidth.setEnabled(false);
							jLbNodeHeight.setEnabled(false);
							mJTfNodeHeight.setEnabled(false);
							setAppropriateSize();
							setAppropriateNodeSize();
						}
					}
				});
				JButton jBtLayout;
				jBtLayout = new JButton("Layout Graph");
				jBtLayout.setMnemonic('L');

				jBtLayout.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						int tmpW;
						int tmpH;

						if (jCbCustomNodeSize.isSelected()) {
							try {
								tmpW = Integer.parseInt(mJTfNodeWidth.getText());
							} catch (NumberFormatException ne) {
								JOptionPane.showMessageDialog(GUI.this.getParent(),
										"Invalid integer entered for node width.", "Error", JOptionPane.ERROR_MESSAGE);
								tmpW = mNNodeWidth;
								mJTfNodeWidth.setText(new Integer(mNNodeWidth).toString());

							}
							try {
								tmpH = Integer.parseInt(mJTfNodeHeight.getText());
							} catch (NumberFormatException ne) {
								JOptionPane.showMessageDialog(GUI.this.getParent(),
										"Invalid integer entered for node height.", "Error", JOptionPane.ERROR_MESSAGE);
								tmpH = mNNodeHeight;
								mJTfNodeWidth.setText(new Integer(mNNodeHeight).toString());
							}

							if (tmpW != mNNodeWidth || tmpH != mNNodeHeight) {
								mNNodeWidth = tmpW;
								mNPaddedNodeWidth = mNNodeWidth + PADDING;
								mNNodeHeight = tmpH;
							}
						}
						// JButton bt = (JButton) ae.getSource();
						// bt.setEnabled(false);
						dlg.setVisible(false);
						updateStatus();
						layoutGraph();
						m_jStatusBar.setText("Laying out Bayes net");
					}
				});
				JButton jBtCancel;
				jBtCancel = new JButton("Cancel");
				jBtCancel.setMnemonic('C');
				jBtCancel.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						dlg.setVisible(false);
					}
				});
				GridBagConstraints gbc = new GridBagConstraints();
				dlg.setLayout(new GridBagLayout());
				// dlg.add(m_le.getControlPanel());

				Container c = new Container();
				c.setLayout(new GridBagLayout());

				gbc.gridwidth = 1;
				gbc.insets = new Insets(8, 0, 0, 0);
				gbc.anchor = GridBagConstraints.NORTHWEST;
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				c.add(jCbCustomNodeSize, gbc);
				gbc.gridwidth = GridBagConstraints.RELATIVE;
				c.add(jLbNodeWidth, gbc);
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				c.add(mJTfNodeWidth, gbc);
				gbc.gridwidth = GridBagConstraints.RELATIVE;
				c.add(jLbNodeHeight, gbc);
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				c.add(mJTfNodeHeight, gbc);
				gbc.fill = GridBagConstraints.HORIZONTAL;
				dlg.add(c, gbc);
				dlg.add(jBtLayout);
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				dlg.add(jBtCancel);
			}
			dlg.setLocation(100, 100);
			dlg.setVisible(true);
			dlg.setSize(dlg.getPreferredSize());
			dlg.setVisible(false);
			dlg.setVisible(true);
			dlg.repaint();
		}
	} // class ActionLayout

	/**
	 * The panel which contains the actual Bayeian network.
	 */
	private class GraphPanel implements Printable {

		/** for serialization */
		private static final long serialVersionUID = -3562813603236753173L;

		/** node drawing modes */
		final static int HIGHLIGHTED = 1;
		final static int NORMAL = 0;

		/**
		 * number of the clique being drawn. Used for selecting the color of the clique
		 */
		int m_nClique = 1;

		public GraphPanel() {
			super();
		} // c'tor

		/*
		 * draws cliques in junction tree.
		 */
		void viewCliques(Graphics g, JunctionTreeNode node) {
			int[] nodes = node.m_nNodes;
			g.setColor(new Color(m_nClique % 7 * 256 / 7, (m_nClique % 2 * 256 / 2), (m_nClique % 3 * 256 / 3)));
			int dX = mNPaddedNodeWidth / 2 + m_nClique;
			int dY = mNNodeHeight / 2;
			int nPosX = 0;
			int nPosY = 0;
			String sStr = new String("");
			for (int node1 : nodes) {
				sStr = sStr + " " + node1;
			}
			m_nClique++;
			nPosX /= nodes.length;
			nPosY /= nodes.length;
			g.drawString("Clique " + m_nClique + "(" + sStr + ")", nPosX, nPosY);
			for (int iChild = 0; iChild < node.m_children.size(); iChild++) {
				viewCliques(g, (JunctionTreeNode) node.m_children.elementAt(iChild));
			}
		} // viewCliques

		/*
		 * Draw a node with index iNode on Graphics g at position Drawing mode can be
		 * NORMAL or HIGHLIGHTED.
		 */
		protected void drawNode(Graphics g, int iNode, int mode) {
			int nPosX = iNode;
			int nPosY = iNode;
			g.setColor(new Color(m_nClique % 7 * 256 / 7, (m_nClique % 2 * 256 / 2), (m_nClique % 3 * 256 / 3)));
			FontMetrics fm = getFontMetrics(getFont());

			if (mode == HIGHLIGHTED) {
				g.setXORMode(Color.green); // g.setColor(Color.green);
			}
			g.fillOval(nPosX + mNPaddedNodeWidth - mNNodeWidth - (mNPaddedNodeWidth - mNNodeWidth) / 2, nPosY,
					mNNodeWidth, mNNodeHeight);
			g.setColor(Color.white);
			if (mode == HIGHLIGHTED) {
				g.setXORMode(Color.red);
			}

			// Draw the node's label if it can fit inside the node's
			// current width otherwise just display its node nr
			// if it can fit in node's current width
			if (fm.stringWidth(new Integer(iNode).toString()) <= mNNodeWidth) {
				g.drawString(new Integer(iNode).toString(),
						nPosX + mNPaddedNodeWidth / 2 - fm.stringWidth(new Integer(iNode).toString()) / 2,
						nPosY + mNNodeHeight / 2 + fm.getHeight() / 2 - 2);
			}

			if (mode == HIGHLIGHTED) {
				g.setXORMode(Color.green);
			}

			if (m_bViewMargins) {
				double[] P = new double[iNode];
				for (int iValue = 0; iValue < P.length; iValue++) {
					String sP = new Double(P[iValue]).toString();
					if (sP.charAt(0) == '0') {
						sP = sP.substring(1);
					}
					if (sP.length() > 5) {
						sP = sP.substring(1, 5);
					}
					g.fillRect(nPosX + mNPaddedNodeWidth, nPosY + iValue * 10 + 2, (int) (P[iValue] * 100), 8);

				}
			}
			if (m_bViewCliques) {
				return;
			}
			g.setColor(Color.black);
			// Drawing all incoming edges into the node,
		} // drawNode

		/**
		 * This method draws an arrow on a line from (x1,y1) to (x2,y2). The arrow head
		 * is seated on (x2,y2) and is in the direction of the line. If the arrow is
		 * needed to be drawn in the opposite direction then simply swap the order of
		 * (x1, y1) and (x2, y2) when calling this function.
		 */
		protected void drawArrow(Graphics g, int nPosX1, int nPosY1, int nPosX2, int nPosY2) {
			g.drawLine(nPosX1, nPosY1, nPosX2, nPosY2);

			if (nPosX1 == nPosX2) {
				if (nPosY1 < nPosY2) {
					g.drawLine(nPosX2, nPosY2, nPosX2 + 4, nPosY2 - 8);
					g.drawLine(nPosX2, nPosY2, nPosX2 - 4, nPosY2 - 8);
				} else {
					g.drawLine(nPosX2, nPosY2, nPosX2 + 4, nPosY2 + 8);
					g.drawLine(nPosX2, nPosY2, nPosX2 - 4, nPosY2 + 8);
				}
			} else {
				// theta=line's angle from base, beta=angle of arrow's side from
				// line
				double hyp = 0;
				double base = 0;
				double perp = 0;
				double theta;
				double beta;
				int nPosX3 = 0;
				int nPosY3 = 0;

				if (nPosX2 < nPosX1) {
					base = nPosX1 - nPosX2;
					hyp = Math.sqrt((nPosX2 - nPosX1) * (nPosX2 - nPosX1) + (nPosY2 - nPosY1) * (nPosY2 - nPosY1));
					theta = Math.acos(base / hyp);
				} else { // x1>x2 as we already checked x1==x2 before
					base = nPosX1 - nPosX2;
					hyp = Math.sqrt((nPosX2 - nPosX1) * (nPosX2 - nPosX1) + (nPosY2 - nPosY1) * (nPosY2 - nPosY1));
					theta = Math.acos(base / hyp);
				}
				beta = 30 * Math.PI / 180;

				hyp = 8;
				base = Math.cos(theta - beta) * hyp;
				perp = Math.sin(theta - beta) * hyp;

				nPosX3 = (int) (nPosX2 + base);
				if (nPosY1 < nPosY2) {
					nPosY3 = (int) (nPosY2 - perp);
				} else {
					nPosY3 = (int) (nPosY2 + perp);
				}

				g.drawLine(nPosX2, nPosY2, nPosX3, nPosY3);

				base = Math.cos(theta + beta) * hyp;
				perp = Math.sin(theta + beta) * hyp;

				nPosX3 = (int) (nPosX2 + base);
				if (nPosY1 < nPosY2) {
					nPosY3 = (int) (nPosY2 - perp);
				} else {
					nPosY3 = (int) (nPosY2 + perp);
				}
				g.drawLine(nPosX2, nPosY2, nPosX3, nPosY3);
			}
		} // drawArrow

		/**
		 * This method highlights a given node and all its incoming and outgoing arcs
		 */
		public void highLight(int iNode) {
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		} // highlight

		/**
		 * implementation of Printable, used for printing
		 * 
		 * @see Printable
		 */
		@Override
		public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
			if (pageIndex > 0) {
				return (NO_SUCH_PAGE);
			} else {
				Graphics2D g2d = (Graphics2D) g;
				g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
				double fHeight = pageFormat.getImageableHeight();
				double fWidth = pageFormat.getImageableWidth();
				int xMax = 1;
				int yMax = 1;
				double fCurrentScale = mFScale;
				xMax = xMax + mNPaddedNodeWidth + 100;
				if (fWidth / xMax < fHeight / yMax) {
					mFScale = fWidth / xMax;
				} else {
					mFScale = fHeight / yMax;
				}

				// Turn off double buffering
				paint(g2d);
				mFScale = fCurrentScale;
				// Turn double buffering back on
				return (PAGE_EXISTS);
			}
		} // print

	} // class GraphPanel

	/**
	 * Table Model for the Table for editing CPTs
	 */
	private class GraphVisualizerTableModel extends AbstractTableModel {

		/** for serialization */
		private static final long serialVersionUID = -4789813491347366596L;

		public GraphVisualizerTableModel() {
			super();
		}

		/**
		 * method that generates random CPTs
		 */
		public void randomize() {
			int nProbs = 5;
			Random random = new Random();

		} // randomize

		public void setData() {
		}

		/** return nr of colums */
		@Override
		public int getColumnCount() {
			return 5;
		}

		/** return nr of rows */
		@Override
		public int getRowCount() {
			return 8;
		}

		/**
		 * return name of specified colum
		 * 
		 * @param iCol index of the column
		 */
		@Override
		public String getColumnName(int iCol) {
			return "";
		}

		/**
		 * return data point
		 * 
		 * @param iRow index of row in table
		 * @param iCol index of column in table
		 */
		@Override
		public Object getValueAt(int iRow, int iCol) {
			return 1.0;
		}

		/**
		 * Set data point, assigns value to CPT entry specified by row and column. The
		 * remainder of the CPT is normalized so that the values add up to 1. IF a value
		 * below zero of over 1 is given, no changes take place.
		 * 
		 * @param oProb data point
		 * @param iRow  index of row in table
		 * @param iCol  index of column in table
		 */
		@Override
		public void setValueAt(Object oProb, int iRow, int iCol) {
			Double fProb = (Double) oProb;
			if (fProb < 0 || fProb > 1) {
				return;
			}
			validate();
		} // setData

		/*
		 * JTable uses this method to determine the default renderer/ editor for each
		 * cell.
		 */
		@Override
		public Class<?> getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		/*
		 * Implemented this to make sure the table is uneditable.
		 */
		@Override
		public boolean isCellEditable(int row, int col) {
			return true;
		}
	} // class GraphVisualizerTableModel

	/**
	 * Listener class for processing mouseClicked
	 */
	private class GraphVisualizerMouseListener extends MouseAdapter {

		/** position clicked on */
		int m_nPosX = 0;
		/**
		 * position clicked on
		 */
		int m_nPosY = 0;

		/**
		 * A left mouseclick on a node adds node to selection (depending on shift and
		 * ctrl keys). A right mouseclick on a node pops up menu with actions to be
		 * performed on the node. A right mouseclick outside another node pops up menu.
		 */
		@Override
		public void mouseClicked(MouseEvent me) {
			int x;
			int y;

			Rectangle r = new Rectangle(0, 0, (int) (mNPaddedNodeWidth * mFScale), (int) (mNNodeHeight * mFScale));
			x = me.getX();
			y = me.getY();

			if (me.getButton() == MouseEvent.BUTTON3) {
				handleRightClick(me, (int) (x / mFScale), (int) (y / mFScale));
			}
		} // mouseClicked

		/*
		 * update selection (non-Javadoc)
		 * 
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased(MouseEvent me) {
			if (m_nSelectedRect != null) {
				if ((me.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != 0) {
					m_Selection.toggleSelection(m_nSelectedRect);
				} else if ((me.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0) {
					m_Selection.addToSelection(m_nSelectedRect);
				} else {
					m_Selection.clear();
					m_Selection.addToSelection(m_nSelectedRect);
				}
				m_nSelectedRect = null;
				repaint();
			}
		} // mouseReleased

		/*
		 * pop up menu with actions that apply in general or to selection (if any
		 * exists)
		 */
		void handleRightClick(MouseEvent me, int nPosX, int nPosY) {

			ActionListener act = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					if ("Add node" == ae.getActionCommand()) {
						a_addnode.addNode(m_nPosX, m_nPosY);
						return;
					}
					repaint();
				}
			};
			JPopupMenu popupMenu = new JPopupMenu("Choose a value");

			JMenuItem addNodeItem = new JMenuItem("Add node");
			addNodeItem.addActionListener(act);
			popupMenu.add(addNodeItem);

			ArrayList<Integer> selected = m_Selection.getSelected();
			int sum = 0;
			for (int n : selected) {
				sum += n;
			}
			JMenu addArcMenu = new JMenu("Add parent");
			popupMenu.add(addArcMenu);
			if (selected.size() == 0) {
				addArcMenu.setEnabled(false);
			} else {
				int nNodes = sum;
				boolean[] isNotAllowedAsParent = new boolean[nNodes];
				selected.forEach(iterator -> {
					isNotAllowedAsParent[iterator] = true;
				});
				// prevent a descendant being a parent, since it introduces
				// cycles
				for (int i = 0; i < nNodes; i++) {
					for (int iNode = 0; iNode < nNodes; iNode++) {
						for (int iParent = 0; iParent < nNodes; iParent++) {
							if (isNotAllowedAsParent[i]) {
								isNotAllowedAsParent[iNode] = true;
							}
						}
					}
				}
				selected.forEach(nNode -> {
					for (int iParent = 0; iParent < nNodes; iParent++) {
						isNotAllowedAsParent[nNode] = true;
					}
				});
				ActionListener addParentAction = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						try {
							updateStatus();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				// count nr of remaining candidates
				int nCandidates = 0;
				for (int i = 0; i < nNodes; i++) {
					if (!isNotAllowedAsParent[i]) {
						JMenuItem item = new JMenuItem("");
						item.addActionListener(addParentAction);
						addArcMenu.add(item);
						nCandidates++;
					}
				}
				if (nCandidates == 0) {
					addArcMenu.setEnabled(false);
				}
			}
			m_nPosX = nPosX;
			m_nPosY = nPosY;
			popupMenu.setLocation(me.getX(), me.getY());
		} // handleRightClick

		/*
		 * pop up menu with actions that apply to node that was clicked on
		 */
		void handleRightNodeClick(MouseEvent me) {
			m_Selection.clear();
			repaint();
			ActionListener renameValueAction = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					renameValue(m_nCurrentNode, ae.getActionCommand());
				}
			};
			ActionListener delValueAction = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					delValue(m_nCurrentNode, ae.getActionCommand());
				}
			};
			ActionListener addParentAction = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					try {
						updateStatus();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			ActionListener delParentAction = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					deleteArc(m_nCurrentNode, ae.getActionCommand());
				}
			};
			ActionListener delChildAction = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					deleteArc(ae.getActionCommand(), m_nCurrentNode);
				}
			};

			ActionListener act = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					if ("Rename" == ae.getActionCommand()) {
						renameNode(m_nCurrentNode);
						return;
					}
					if ("Add parent" == ae.getActionCommand()) {
						addArcInto(m_nCurrentNode);
						return;
					}
					if ("Add value" == ae.getActionCommand()) {
						addValue();
						return;
					}
					if ("Delete node" == ae.getActionCommand()) {
						deleteNode(m_nCurrentNode);
						return;
					}
					if ("Edit CPT" == ae.getActionCommand()) {
						editCPT(m_nCurrentNode);
						return;
					}
					repaint();
				}
			};
			try {
				JPopupMenu popupMenu = new JPopupMenu("Choose a value");

				JMenu setEvidenceMenu = new JMenu("Set evidence");
				popupMenu.add(setEvidenceMenu);

				setEvidenceMenu.setEnabled(m_bViewMargins);

				popupMenu.addSeparator();

				JMenuItem renameItem = new JMenuItem("Rename");
				renameItem.addActionListener(act);
				popupMenu.add(renameItem);

				JMenuItem delNodeItem = new JMenuItem("Delete node");
				delNodeItem.addActionListener(act);
				popupMenu.add(delNodeItem);

				JMenuItem editCPTItem = new JMenuItem("Edit CPT");
				editCPTItem.addActionListener(act);
				popupMenu.add(editCPTItem);

				popupMenu.addSeparator();

				JMenu addArcMenu = new JMenu("Add parent");
				popupMenu.add(addArcMenu);
				int nNodes = 5;
				boolean[] isNotAllowedAsParent = new boolean[nNodes];
				// prevent it being a parent of itself
				isNotAllowedAsParent[m_nCurrentNode] = true;
				// prevent a descendant being a parent, since it introduces
				// cycles
				for (int i = 0; i < nNodes; i++) {
					for (int iNode = 0; iNode < nNodes; iNode++) {
						for (int iParent = 0; iParent < nNodes; iParent++) {
							if (isNotAllowedAsParent[iParent]) {
								isNotAllowedAsParent[iNode] = true;
							}
						}
					}
				}
				// prevent nodes that are already a parent
				for (int iParent = 0; iParent < nNodes; iParent++) {
					isNotAllowedAsParent[iParent] = true;
				}
				// count nr of remaining candidates
				int nCandidates = 0;

				if (nCandidates == 0) {
					addArcMenu.setEnabled(false);
				}

				JMenu delArcMenu = new JMenu("Delete parent");
				popupMenu.add(delArcMenu);

				JMenu delChildMenu = new JMenu("Delete child");
				popupMenu.add(delChildMenu);

				popupMenu.addSeparator();

				JMenuItem addValueItem = new JMenuItem("Add value");
				addValueItem.addActionListener(act);
				popupMenu.add(addValueItem);

				JMenu renameValue = new JMenu("Rename value");
				popupMenu.add(renameValue);

				JMenu delValue = new JMenu("Delete value");
				popupMenu.add(delValue);

				popupMenu.setLocation(me.getX(), me.getY());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} // handleRightNodeClick
	} // class GraphVisualizerMouseListener

	/**
	 * private class for handling mouseMoved events to highlight nodes if the the
	 * mouse is moved on one, move it around or move selection around
	 */
	private class GraphVisualizerMouseMotionListener extends MouseMotionAdapter {

		/* last node moved over. Used for turning highlight on and off */
		int m_nLastNode = -1;
		/* current mouse position clicked */
		int m_nPosX;
		int m_nPosY;

		/*
		 * identify the node under the mouse
		 * 
		 * @returns node index of node under mouse, or -1 if there is no such node
		 */
		int getGraphNode(MouseEvent me) {
			m_nPosX = m_nPosY = 0;

			Rectangle r = new Rectangle(0, 0, (int) (mNPaddedNodeWidth * mFScale), (int) (mNNodeHeight * mFScale));
			m_nPosX = m_nPosX + me.getX();
			m_nPosY = m_nPosY + me.getY();

			return -1;
		} // getGraphNode

		/*
		 * handle mouse dragging event (non-Javadoc)
		 * 
		 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.
		 * MouseEvent )
		 */
		@Override
		public void mouseDragged(MouseEvent me) {
			if (m_nSelectedRect != null) {
				m_nSelectedRect.width = me.getPoint().x - m_nSelectedRect.x;
				m_nSelectedRect.height = me.getPoint().y - m_nSelectedRect.y;
				repaint();
				return;
			}
			int iNode = getGraphNode(me);

			if (iNode < 0) {
				if (m_nLastNode >= 0) {
					m_nLastNode = -1;
				} else {
					m_nSelectedRect = new Rectangle(me.getPoint().x, me.getPoint().y, 1, 1);
				}
			}
		} // mouseDragged

		/*
		 * handles mouse move event (non-Javadoc)
		 * 
		 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.
		 * MouseEvent)
		 */
		@Override
		public void mouseMoved(MouseEvent me) {
			int iNode = getGraphNode(me);
			if (iNode >= 0) {
				if (iNode != m_nLastNode) {
					m_GraphPanel.highLight(iNode);
					if (m_nLastNode >= 0) {
						m_GraphPanel.highLight(m_nLastNode);
					}
					m_nLastNode = iNode;
				}
			}
			if (iNode < 0 && m_nLastNode >= 0) {
				m_nLastNode = -1;
			}
		} // mouseMoved

	} // class GraphVisualizerMouseMotionListener

} // end of class
