/*

	Copyright 2007-2008 91NORD

	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License as
	published by the Free Software Foundation; either version 2 of the
	License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful, but
	WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
	02110-1301, USA.
	
*/



package sphericalGeo.scapetoad.core;

import java.io.Serializable;




/**
 * Contains some static methods for geometrical computations.
 * @author Christian Kaiser <Christian.Kaiser@91nord.com"
 * @version v1.0.0, 2007-11-28
 */
public class Geometry implements Serializable
{


	
	/**
	 * Computes the area of the triangle defined by the points A(ax, ay),
	 * B(bx, by) and C(cx, cy).
	 * @param ax the x coordinate of point A
	 * @param ay the y coordinate of point A
	 * @param bx the x coordinate of point B
	 * @param by the y coordinate of point B
	 * @param cx the x coordinate of point C
	 * @param cy the y coordinate of point C
	 * @return the area of the triangle ABC
	 */
	public static double areaOfTriangle (double ax, double ay, 
		double bx, double by, double cx, double cy)
	{
		
		double ux = ax - cx;
		double uy = ay - cy;
		double vx = bx - cx;
		double vy = by - cy;
		
		double A = 0.5 * (ux * vy - uy * vx);
		
		return Math.abs(A);
		
	}	// Geometry.areaOfTriangle




	/**
	 * Computes the area of an irregular quadrangle defined by the points
	 * A(ax, ay), B(bx, by), C(cx, cy) and D(dx, dy). The four points must
	 * be following points in the quadrangle (clockwise or counter-clockwise).
	 * Therefore, point A can not be a neighbour of point C.
	 * @param ax the x coordinate of point A
	 * @param ay the y coordinate of point A
	 * @param bx the x coordinate of point B
	 * @param by the y coordinate of point B
	 * @param cx the x coordinate of point C
	 * @param cy the y coordinate of point C
	 * @param dx the x coordinate of point D
	 * @param dy the y coordinate of point D
	 * @return the area of the quadrangle ABCD
	 */
	public static double areaOfQuadrangle (double ax, double ay,
		double bx, double by, double cx, double cy, double dx, double dy)
	{
	
		double A1 = Geometry.areaOfTriangle(ax, ay, bx, by, cx, cy);
		double A2 = Geometry.areaOfTriangle(ax, ay, cx, cy, dx, dy);
		return (A1 + A2);
	
	}	// Geometry.areaOfQuadrangle




	/**
	 * Computes the intersection of two segments AB and CD.
	 */
	public static double[] intersectionOfSegments (double ax, double ay,
		double bx, double by, double cx, double cy, double dx, double dy)
	{
	
		// This function has been adapted from the JUMP project,
		// the function GeoUtils.intersectSegments.
	
		double vx = bx - ax;
		double vy = by - ay;
        double wx = dx - cx;
		double wy = dy - cy;
		
		double n1 = wy * (cx - ax) - wx * (cy - ay);
		double n2 = vy * (cx - ax) - vx * (cy - ay);
		double d = wy * vx - wx * vy;
        
        if (d != 0.0)
        {
            double t1 = n1 / d;
            double t2 = n2 / d;
			double ex = ax + vx * t1;
			double ey = ay + vy * t1;
            double epsilon = 0.001;
            double lowbound = 0.0 - epsilon;
            double hibound = 1.0 + epsilon;
            boolean onP1P2 = (t1 >= lowbound) && (t1 <= hibound);
    		boolean onP3P4 = (t2 >= lowbound) && (t2 <= hibound);
    		if (onP1P2 && onP3P4)
			{
				double[] e = new double[2];
				e[0] = ex;
				e[1] = ey;
				return e;
			}
    		else
				// The intersection point does not lie on one or both segments
    			return null;
        }
        else
        {
			// The lines are parallel; no intersection
            return null;
        }
	
	}	// Geometry.intersectionOfSegments


	public static double[] projectPoint (double a, double b,
			double ax, double ay, double bx, double by, double cx, double cy, double dx, double dy)
	{
		double ex = ax + a * (bx - ax);
		double ey = ay + a * (by - ay);
		double fx = bx + b * (cx - bx);
		double fy = by + b * (cy - by);
		double gx = dx + a * (cx - dx);
		double gy = dy + a * (cy - dy);
		double hx = ax + b * (dx - ax);
		double hy = ay + b * (dy - ay);
		
		double[] s = Geometry.intersectionOfSegments(ex, ey, gx, gy, fx, fy, hx, hy);
		
		return s;

	}	

    public static double[] reverseprojection(double x, double y,
			double ax, double ay, double bx, double by, double dx, double dy, double cx, double cy)
    {
        final double c1 = ax - bx;
        final double c2 = (c1 - cx + dx);

        final double d1 = (ay-cy);
        final double d2 = (d1 + dy - by);

        final double e1 = y - ay;
        final double e2 = x - ax;
        final double e3 = cx - ax;
        final double e4 = ay - by;

        double[] e = new double[2];

        final double c00 = (e3 * d2 + d1 * c2);
        final double c01 = (d2 * e2 + c1 * d1 + e3 * e4 - c2 * e1);
        final double c02 = (x * e4 - c1 * e1 - ax * e4);
        if( c00 == 0 ) {
            e[1] = c02/c01;
        } else {
            final double d = Math.sqrt(c01 * c01 - 4 * c00 * c02);
            e[1] = (c01 - d) / (2 * c00);
            if( !( 0 <= e[1] && e[1] <= 1) ) {
              e[1] = (c01 + d) / (2 * c00);
                assert (0 <= e[1] && e[1] <= 1) ;
            }
        }
        e[0] = (e2 - e[1] * e3) / (e[1] * c2 - c1);
        return e;
    }


}	// Geometry
