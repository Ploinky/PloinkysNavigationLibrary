package de.ploinky.nav;

import java.util.List;

public class NavMesh
{
	private List<NavCell> navCells;

	public NavMesh(List<NavCell> navCells)
	{
		this.navCells = navCells;
	}

	public List<NavCell> getNavCells()
	{
		return navCells;
	}
}
