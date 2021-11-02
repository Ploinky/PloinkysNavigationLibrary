package de.ploinky.nav;

public class NavCell
{
	public NavVertex[] vertices = new NavVertex[3];

	public NavVertex center;

	public NavCell parent;

	public float globalValue;

	public float localValue;

	public NavCell(NavVertex v1, NavVertex v2, NavVertex v3)
	{
		vertices[0] = v1;
		vertices[1] = v2;
		vertices[2] = v3;

		center = centerOf(v1, v2, v3);

		globalValue = Float.MAX_VALUE;
		localValue = Float.MAX_VALUE;
	}

	private NavVertex centerOf(NavVertex v1, NavVertex v2, NavVertex v3)
	{
		return new NavVertex((v1.x + v2.x + v3.x) / 3, (v1.y + v2.y + v3.y) / 3, 0);
	}
}
