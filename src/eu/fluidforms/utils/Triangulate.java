package eu.fluidforms.utils;

/*
 *	ported from p bourke's triangulate.c
 *	http://astronomy.swin.edu.au/~pbourke/modelling/triangulate/
 *
 *	fjenett, 20th february 2005, offenbach-germany.
 *	contact: http://www.florianjenett.de/
 *
 *      adapted to take a Vector of Point3f objects and return a Vector of Triangles
 *      (and generally be more Java-like and less C-like in usage - 
 *       and probably less efficient but who's benchmarking?)
 *      Tom Carden, tom (at) tom-carden.co.uk 17th January 2006
 *
 *
 *	Stephen Williams - Adapted to use the Fluid-FormLibs geometry classes. 12 February 2009.
 */
import java.util.Collections;
import java.util.Vector;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Comparator;

import eu.fluidforms.geom.FEdge;
import eu.fluidforms.geom.FTriangle;
import eu.fluidforms.geom.FVertex;
import processing.core.PApplet;

public class Triangulate {

  /*
    From P Bourke's C prototype - 

    qsort(p,nv,sizeof(XYZ),XYZCompare);
		
    int XYZCompare(void *v1,void *v2) {
      XYZ *p1,*p2;
      p1 = v1;
      p2 = v2;
      if (p1->x < p2->x)
        return(-1);
      else if (p1->x > p2->x)
        return(1);
      else
        return(0);
    }
  */
  private static class XComparator implements Comparator<FVertex> {
    public int compare(FVertex p1, FVertex p2) {
      if (p1.x < p2.x) {
        return -1;
      }
      else if (p1.x > p2.x) {
        return 1;
      }
      else {
        return 0;
      }
    }
  }

  /*
    Return TRUE if a point (xp,yp) is inside the circumcircle made up
    of the points (x1,y1), (x2,y2), (x3,y3)
    The circumcircle centre is returned in (xc,yc) and the radius r
    NOTE: A point on the edge is inside the circumcircle
  */
  private static boolean circumCircle(FVertex p, FTriangle t, FVertex circle) {

    float m1,m2,mx1,mx2,my1,my2;
    float dx,dy,rsqr,drsqr;
		
    /* Check for coincident points */
    if ( Math.abs(t.getV1().y-t.getV2().y) < PApplet.EPSILON && Math.abs(t.getV2().y-t.getV3().y) < PApplet.EPSILON ) {
      System.err.println("CircumCircle: Points are coincident.");
      return false;
    }

    if ( Math.abs(t.getV2().y-t.getV1().y) < PApplet.EPSILON ) {
      m2 = - (t.getV3().x-t.getV2().x) / (t.getV3().y-t.getV2().y);
      mx2 = (t.getV2().x + t.getV3().x) / 2.0f;
      my2 = (t.getV2().y + t.getV3().y) / 2.0f;
      circle.x = (t.getV2().x + t.getV1().x) / 2.0f;
      circle.y = m2 * (circle.x - mx2) + my2;
    }
    else if ( Math.abs(t.getV3().y-t.getV2().y) < PApplet.EPSILON ) {
      m1 = - (t.getV2().x-t.getV1().x) / (t.getV2().y-t.getV1().y);
      mx1 = (t.getV1().x + t.getV2().x) / 2.0f;
      my1 = (t.getV1().y + t.getV2().y) / 2.0f;
      circle.x = (t.getV3().x + t.getV2().x) / 2.0f;
      circle.y = m1 * (circle.x - mx1) + my1;	
    }
    else {
      m1 = - (t.getV2().x-t.getV1().x) / (t.getV2().y-t.getV1().y);
      m2 = - (t.getV3().x-t.getV2().x) / (t.getV3().y-t.getV2().y);
      mx1 = (t.getV1().x + t.getV2().x) / 2.0f;
      mx2 = (t.getV2().x + t.getV3().x) / 2.0f;
      my1 = (t.getV1().y + t.getV2().y) / 2.0f;
      my2 = (t.getV2().y + t.getV3().y) / 2.0f;
      circle.x = (m1 * mx1 - m2 * mx2 + my2 - my1) / (m1 - m2);
      circle.y = m1 * (circle.x - mx1) + my1;
    }
	
    dx = t.getV2().x - circle.x;
    dy = t.getV2().y - circle.y;
    rsqr = dx*dx + dy*dy;
    circle.z = (float)Math.sqrt(rsqr);
		
    dx = p.x - circle.x;
    dy = p.y - circle.y;
    drsqr = dx*dx + dy*dy;
	
    return drsqr <= rsqr;
  }


  /*
    Triangulation subroutine
    Takes as input vertices (FVertexs) in Vector pxyz
    Returned is a list of triangular faces in the Vector v 
    These triangles are arranged in a consistent clockwise order.
  */
  public static Vector<FTriangle> triangulate( Vector<FVertex> pxyz ) {
  
    // sort vertex array in increasing x values
    Collections.sort(pxyz, new XComparator());
    		
    /*
      Find the maximum and minimum vertex bounds.
      This is to allow calculation of the bounding triangle
    */
    float xmin = pxyz.elementAt(0).x;
    float ymin = pxyz.elementAt(0).y;
    float xmax = xmin;
    float ymax = ymin;
    
    Iterator<FVertex> pIter = pxyz.iterator();
    while (pIter.hasNext()) {
      FVertex p = pIter.next();
      if (p.x < xmin) xmin = p.x;
      if (p.x > xmax) xmax = p.x;
      if (p.y < ymin) ymin = p.y;
      if (p.y > ymax) ymax = p.y;
    }
    
    float dx = xmax - xmin;
    float dy = ymax - ymin;
    float dmax = (dx > dy) ? dx : dy;
    float xmid = (xmax + xmin) / 2.0f;
    float ymid = (ymax + ymin) / 2.0f;
	
    Vector<FTriangle> v = new Vector<FTriangle>(); // for the Triangles
    HashSet<FTriangle> complete = new HashSet<FTriangle>(); // for complete Triangles

    /*
      Set up the supertriangle
      This is a triangle which encompasses all the sample points.
      The supertriangle coordinates are added to the end of the
      vertex list. The supertriangle is the first triangle in
      the triangle list.
    */
    FTriangle superTriangle = new FTriangle();
    superTriangle.setV1( new FVertex( xmid - 2.0f * dmax, ymid - dmax, 0.0f ));
    superTriangle.setV2( new FVertex( xmid, ymid + 2.0f * dmax, 0.0f ));
    superTriangle.setV3( new FVertex( xmid + 2.0f * dmax, ymid - dmax, 0.0f ));
    v.addElement(superTriangle);
    
    /*
      Include each point one at a time into the existing mesh
    */
    Vector<FEdge> edges = new Vector<FEdge>();
    pIter = pxyz.iterator();
    while (pIter.hasNext()) {
    
      FVertex p = pIter.next();
      
      edges.clear();
      
      /*
        Set up the edge buffer.
        If the point (xp,yp) lies inside the circumcircle then the
        three edges of that triangle are added to the edge buffer
        and that triangle is removed.
      */
      FVertex circle = new FVertex();
      
      for (int j = v.size()-1; j >= 0; j--) {
      
        FTriangle t = v.elementAt(j);
        if (complete.contains(t)) {
          continue;
        }
          
        boolean inside = circumCircle( p, t, circle );
        
        if (circle.x + circle.z < p.x) {
          complete.add(t);
        }
        if (inside) {
          edges.addElement(new FEdge(t.getV1(), t.getV2()));
          edges.addElement(new FEdge(t.getV2(), t.getV3()));
          edges.addElement(new FEdge(t.getV3(), t.getV1()));
          v.remove(t);
        }
                
      }

      /*
        Tag multiple edges
        Note: if all triangles are specified anticlockwise then all
        interior edges are opposite pointing in direction.
      */
      for (int j=0; j<edges.size()-1; j++) {
        FEdge e1 = edges.elementAt(j);
        for (int k=j+1; k<edges.size(); k++) {
          FEdge e2 = edges.elementAt(k);
          if (e1.getV1() == e2.getV2() && e1.getV2() == e2.getV1()) {
            e1.setV1( null );
            e1.setV2( null );
            e2.setV1( null );
            e2.setV2( null );
          }
          /* Shouldn't need the following, see note above */
          if (e1.getV1() == e2.getV1() && e1.getV2() == e2.getV2()) {
              e1.setV1( null );
              e1.setV2( null );
              e2.setV1( null );
              e2.setV2( null );
          }
        }
      }
      
      /*
        Form new triangles for the current point
        Skipping over any tagged edges.
        All edges are arranged in clockwise order.
      */
      for (int j=0; j < edges.size(); j++) {
        FEdge e = edges.elementAt(j);
        if (e.getV1() == null || e.getV2() == null) {
          continue;
        }
        v.add(new FTriangle(e.getV1(), e.getV2(), p));
      }
      
    }
      
    /*
      Remove triangles with supertriangle vertices
    */
    for (int i = v.size()-1; i >= 0; i--) {
      FTriangle t = v.elementAt(i);
      if (t.sharesVertex(superTriangle)) {
        v.remove(t);
      }
    }

    return v;	
  }
	
}
