package de.ploinky.nav.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import de.ploinky.nav.NavCell;
import de.ploinky.nav.NavMesh;
import de.ploinky.nav.NavVertex;

public class TestApp
{
	private NavMesh navMesh;

	private NavCell startCell;

	private NavCell endCell;

	private NavCell[][] neighbours;

	private List<NavCell> path;

	private List<NavVertex> movePath;

	private List<Portal> portals;

	public void start()
	{
		NavVertex v1 = new NavVertex(100, 100, 0);
		NavVertex v2 = new NavVertex(200, 100, 0);
		NavVertex v3 = new NavVertex(300, 100, 0);
		NavVertex v4 = new NavVertex(400, 100, 0);
		NavVertex v5 = new NavVertex(100, 200, 0);
		NavVertex v6 = new NavVertex(200, 200, 0);
		NavVertex v7 = new NavVertex(300, 200, 0);
		NavVertex v8 = new NavVertex(400, 200, 0);
		NavVertex v9 = new NavVertex(100, 300, 0);
		NavVertex v10 = new NavVertex(200, 300, 0);
		NavVertex v11 = new NavVertex(300, 300, 0);
		NavVertex v12 = new NavVertex(400, 300, 0);
		NavVertex v13 = new NavVertex(100, 400, 0);
		NavVertex v14 = new NavVertex(200, 400, 0);
		NavVertex v15 = new NavVertex(300, 400, 0);
		NavVertex v16 = new NavVertex(400, 400, 0);

		List<NavCell> c = new ArrayList<>();

		c.add(new NavCell(v1, v2, v5));
		c.add(new NavCell(v2, v6, v5));
		c.add(new NavCell(v2, v3, v6));
		c.add(new NavCell(v3, v7, v6));
		c.add(new NavCell(v3, v4, v7));
		c.add(new NavCell(v4, v8, v7));
		c.add(new NavCell(v5, v6, v9));
		c.add(new NavCell(v6, v10, v9));

		c.add(new NavCell(v7, v8, v11));
		c.add(new NavCell(v8, v12, v11));
		c.add(new NavCell(v9, v10, v13));
		c.add(new NavCell(v10, v14, v13));
		c.add(new NavCell(v10, v11, v14));
		c.add(new NavCell(v11, v15, v14));
		c.add(new NavCell(v11, v12, v15));
		c.add(new NavCell(v12, v16, v15));

		neighbours = new NavCell[c.size()][3];

		navMesh = new NavMesh(c);

		path = new ArrayList<>();

		findNeighbours();

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(600, 600));

		JPanel drawPanel = new JPanel()
		{
			@Override
			protected void paintComponent(Graphics g)
			{
				super.paintComponent(g);

				g.setColor(Color.RED);
				g.drawRect(0, 0, 580, 560);

				for (NavCell c : navMesh.getNavCells())
				{
					boolean endCellNeighbour = false;
					boolean startCellNeighbour = false;

					if (endCell != null)
					{
						for (NavCell nc : neighbours[navMesh.getNavCells().indexOf(endCell)])
						{
							if (c == nc)
							{
								endCellNeighbour = true;
							}
						}
					}

					if (startCell != null)
					{
						for (NavCell nc : neighbours[navMesh.getNavCells().indexOf(startCell)])
						{
							if (c == nc)
							{
								startCellNeighbour = true;
							}
						}
					}

					if (c == endCell)
					{
						g.setColor(Color.GREEN);
					} else if (c == startCell)
					{
						g.setColor(Color.RED);
					} else if (path.contains(c))
					{
						g.setColor(Color.YELLOW);
					} else if (endCellNeighbour || startCellNeighbour)
					{
						g.setColor(Color.BLUE);
					} else
					{
						g.setColor(Color.RED);
					}

					if (c == endCell || c == startCell || endCellNeighbour || startCellNeighbour || path.contains(c))
					{
						g.fillPolygon(new int[]
						{ (int) c.vertices[0].x, (int) c.vertices[1].x, (int) c.vertices[2].x }, new int[]
						{ (int) c.vertices[0].y, (int) c.vertices[1].y, (int) c.vertices[2].y }, 3);
					}

					g.drawPolygon(new int[]
					{ (int) c.vertices[0].x, (int) c.vertices[1].x, (int) c.vertices[2].x }, new int[]
					{ (int) c.vertices[0].y, (int) c.vertices[1].y, (int) c.vertices[2].y }, 3);

					g.setColor(Color.BLACK);

					g.fillRect((int) c.center.x - 2, (int) c.center.y - 2, 4, 4);
				}

				NavCell lastCell = null;

				for (NavCell pathCell : path)
				{
					if (lastCell == null)
					{
						lastCell = pathCell;
						continue;
					}

					g.setColor(Color.BLACK);
					g.drawLine((int) lastCell.center.x, (int) lastCell.center.y, (int) pathCell.center.x,
							(int) pathCell.center.y);

					lastCell = pathCell;
				}

				if (portals != null)
				{
					for (Portal p : portals)
					{
						g.setColor(Color.PINK);
						g.fillRect((int) p.left.x - 3, (int) p.left.y - 3, 6, 6);

						g.setColor(Color.ORANGE);
						g.fillRect((int) p.right.x - 3, (int) p.right.y - 3, 6, 6);
					}
				}
			}
		};

		drawPanel.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				NavVertex v = new NavVertex(e.getX(), e.getY(), 0);

				for (NavCell c : navMesh.getNavCells())
				{
					if (PointInTriangle(v, c.vertices[0], c.vertices[1], c.vertices[2]))
					{
						if (e.getButton() == MouseEvent.BUTTON1)
						{
							startCell = c;
						}
						if (e.getButton() == MouseEvent.BUTTON3)
						{
							endCell = c;
						}

						findPath();

						return;
					}
				}

				endCell = null;
				startCell = null;
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
			}

		});

		frame.setContentPane(drawPanel);

		frame.pack();
		frame.setVisible(true);

		while (true && frame.isVisible())
		{
			drawPanel.invalidate();
			drawPanel.repaint();
			try
			{
				Thread.sleep(1);
			} catch (InterruptedException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	protected void findPath()
	{
		if (endCell == null || startCell == null)
		{
			return;
		}

		for (NavCell c : navMesh.getNavCells())
		{
			c.globalValue = Float.MAX_VALUE;
			c.localValue = Float.MAX_VALUE;
			c.parent = null;
		}

		List<NavCell> nodesToTest = new ArrayList<>();
		List<NavCell> nodesDone = new ArrayList<>();

		startCell.globalValue = distance(startCell, endCell);
		startCell.localValue = 0;

		nodesToTest.add(startCell);

		NavCell currCell = null;

		while (!nodesToTest.isEmpty())
		{
			nodesToTest.sort((n1, n2) ->
			{
				float v = n1.globalValue - n2.globalValue;
				return v == 0 ? 0 : (v < 0 ? -1 : 1);
			});

			currCell = nodesToTest.get(0);

			// Pop it off open list
			nodesToTest.remove(currCell);

			boolean bb = false;

			for (NavCell neighbour : neighbours[navMesh.getNavCells().indexOf(currCell)])
			{
				if (neighbour == null)
				{
					continue;
				}

				float newLocal = currCell.localValue + distance(currCell, neighbour);

				if (newLocal < neighbour.localValue)
				{
					nodesToTest.add(neighbour);
					neighbour.parent = currCell;
					neighbour.localValue = newLocal;
					neighbour.globalValue = neighbour.localValue + distance(startCell, endCell);
				}
			}

			nodesDone.add(currCell);

			if (bb)
			{
				break;
			}
		}

		path.clear();

		NavCell pathCell = endCell;
		path.add(pathCell);

		while (pathCell != startCell)
		{
			pathCell = pathCell.parent;
			path.add(pathCell);
		}

		Collections.reverse(path);

		pullString();
	}

	class Portal
	{
		public NavCell from;
		public NavCell to;

		public NavVertex left;
		public NavVertex right;
	}

	private void pullString()
	{
		List<NavVertex> leftVertices = new ArrayList<>();
		List<NavVertex> rightVertices = new ArrayList<>();

		NavVertex apex = path.get(0).center;

		portals = new ArrayList<>();

		for (int i = 0; i < path.size() - 1; i++)
		{
			Portal p = new Portal();
			p.from = path.get(i);
			p.to = path.get(i + 1);

			NavVertex vf1 = p.from.vertices[0];
			NavVertex vf2 = p.from.vertices[1];
			NavVertex vf3 = p.from.vertices[2];

			NavVertex vt1 = p.to.vertices[0];
			NavVertex vt2 = p.to.vertices[1];
			NavVertex vt3 = p.to.vertices[2];

			NavVertex av1 = null;
			NavVertex av2 = null;

			if (vf1 == vt1 || vf1 == vt2 || vf1 == vt3)
			{
				av1 = vf1;
			}

			if (vf2 == vt1 || vf2 == vt2 || vf2 == vt3)
			{
				if (av1 == null)
				{
					av1 = vf2;
				} else
				{
					av2 = vf2;
				}
			}

			if (vf3 == vt1 || vf3 == vt2 || vf3 == vt3)
			{
				av2 = vf3;
			}

			float av1d = (av1.x - p.from.center.x) * (p.to.center.y - p.from.center.y)
					- (av1.y - p.from.center.y) * (p.to.center.x - p.from.center.x);

			float av2d = (av2.x - p.from.center.x) * (p.to.center.y - p.from.center.y)
					- (av2.y - p.from.center.y) * (p.to.center.x - p.from.center.x);

			if (av1d < 0)
			{
				p.left = av1;
				p.right = av2;
			} else
			{
				p.left = av2;
				p.right = av1;
			}

			portals.add(p);
		}

		movePath = new ArrayList<>();

		System.out.println(portals);
	}

	private float distance(NavCell a, NavCell b)
	{
		return Math.abs(a.center.x - b.center.x) + Math.abs(a.center.y - b.center.y);
	}

	private void findNeighbours()
	{
		int i = 0;

		NavVertex c1 = null;
		NavVertex c2 = null;
		NavVertex c3 = null;
		NavVertex oc1 = null;
		NavVertex oc2 = null;
		NavVertex oc3 = null;

		for (NavCell cell : navMesh.getNavCells())
		{
			int j = 0;

			for (NavCell oCell : navMesh.getNavCells())
			{
				if (oCell == cell)
				{
					continue;
				}

				c1 = cell.vertices[0];
				c2 = cell.vertices[1];
				c3 = cell.vertices[2];
				oc1 = oCell.vertices[0];
				oc2 = oCell.vertices[1];
				oc3 = oCell.vertices[2];

				boolean b1 = c1 == oc1 || c1 == oc2 || c1 == oc3;
				boolean b2 = c2 == oc1 || c2 == oc2 || c2 == oc3;
				boolean b3 = c3 == oc1 || c3 == oc2 || c3 == oc3;

				if ((b1 && b2) || (b1 && b3) || (b2 && b3))
				{
					neighbours[i][j++] = oCell;
				}
			}

			i++;
		}
	}

	private float sign(NavVertex p1, NavVertex p2, NavVertex p3)
	{
		return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y);
	}

	private boolean PointInTriangle(NavVertex pt, NavVertex v1, NavVertex v2, NavVertex v3)
	{
		float d1, d2, d3;
		boolean has_neg, has_pos;

		d1 = sign(pt, v1, v2);
		d2 = sign(pt, v2, v3);
		d3 = sign(pt, v3, v1);

		has_neg = (d1 < 0) || (d2 < 0) || (d3 < 0);
		has_pos = (d1 > 0) || (d2 > 0) || (d3 > 0);

		return !(has_neg && has_pos);
	}
}
