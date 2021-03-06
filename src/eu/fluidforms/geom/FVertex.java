/*
  (c) copyright
  
  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
 */
 
package eu.fluidforms.geom;

import java.util.Vector;

import processing.core.PMatrix3D;
import processing.core.PVector;

/**
 * @author williams
 * 
 * 
 *  
 */
public class FVertex extends PVector implements Cloneable{
	private static final long serialVersionUID = -1234996531898954859L;

	public float u, v;

    public Vector<FTriangle> vTriangles;

    public FVertex normal;//Processing uses vertex normals for shading.

    public String type;

    public FVertex(float x, float y, float z, float u, float v) {
        init(x, y, z, u, v, "");
    }

	/**
	 * A Copy Constructor to create a copy of the past AVertex.
	 * 
	 * @param vert
	 *            The AVertex to be copied.
	 */
    public FVertex(FVertex vert) {
        this.type = "";
        this.x = vert.x;
        this.y = vert.y;
        this.z = vert.z;
        this.u = vert.u;
        this.v = vert.v;
        normal = new FVertex();
        vTriangles = new Vector<FTriangle>();
    }
    public FVertex(PVector vert) {
    	this(vert.x, vert.y, vert.z);
    }

    public FVertex() {
        this.type = "";
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.u = 0;
        this.v = 0;
        vTriangles = new Vector<FTriangle>();
    }

    /**
     * This is the standard 3d vector constructor.
     * 
     * @param x
     *            The x position in a Cartesian coordinate system
     * @param y
     *            The y position in a Cartesian coordinate system
     * @param z
     *            The z position in a Cartesian coordinate system
     */
    public FVertex(float x, float y, float z) {
        init(x, y, z, 0, 0, "");
    }

    /**
     * Constructor for a 3d vector in which its type may be specified. The type
     * may be used in order to signify different parts of an object in a point
     * cloud for example.
     * 
     * @param x
     *            The x position in a Cartesian coordinate system
     * @param y
     *            The y position in a Cartesian coordinate system
     * @param z
     *            The z position in a Cartesian coordinate system
     * @param type
     *            This is used in order to signify different parts of an object
     *            in a point cloud for example.
     */
    public FVertex(float x, float y, float z, String type) {
        init(x, y, z, 0, 0, type);
    }

    /**
     * This is the standard 2d vector constructor.
     * 
     * @param x
     *            The x position in a Cartesian coordinate system
     * @param y
     *            The y position in a Cartesian coordinate system
     */
    public FVertex(float x, float y) {
        init(x, y, 0, 0, 0, "");
    }

    /**
     * Constructor for a 2d vector in which its type may be specified. The type
     * may be used in order to signify different parts of an object in a point
     * cloud for example.
     * 
     * @param x
     *            The x position in a Cartesian coordinate system
     * @param y
     *            The y position in a Cartesian coordinate system
     * @param type
     *            This is used in order to signify different parts of an object
     *            in a point cloud for example.
     */
    public FVertex(float x, float y, String type) {
        init(x, y, 0, 0, 0, type);
    }

    /**
     * This function is called from within the constructors.
     * 
     * @param x
     *            The x position in a Cartesian coordinate system
     * @param y
     *            The y position in a Cartesian coordinate system
     * @param z
     *            The z position in a Cartesian coordinate system
     * @param type
     *            This is used in order to signify different parts of an object
     *            in a point cloud for example.
     */
    private void init(float x, float y, float z, float u, float v, String type) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.u = u;
        this.v = v;
        vTriangles = new Vector<FTriangle>();
        normal = new FVertex();
    }

	/**
	 * Applies a transformation matrix to the vertex.
	 * @param matrix
	 */
    public void apply(PMatrix3D matrix){
    	x = x * matrix.m00 + y * matrix.m10 + z * matrix.m20;
    	y = y * matrix.m01 + y * matrix.m11 + z * matrix.m21;
    	z = z * matrix.m02 + y * matrix.m12 + z * matrix.m22;
    }
    
    public void scale(float scale){
//    	PMatrix3D matrix = new PMatrix3D();
//    	matrix.scale(scale);
//    	apply(matrix);
    	x *= scale;
    	y *= scale;
    	z *= scale;
    }
    
    /**
     * Sets the normal to be equal to the provide aVector after which the
     * normal is normalised.
     * 
     * @param normal
     * @see FVertex
     */
    public void setNormal(FVertex normal) {
        //        norm = normal;
        this.normal.x = normal.x;
        this.normal.y = normal.y;
        this.normal.z = normal.z;
        this.normal.normalise();
    }
    public FVertex getNormal() {
        this.normal.x = x;
        this.normal.y = y;
        this.normal.z = z;
        this.normal.normalise();
        return this.normal;
    }

    /**
     * Extrudes the position of the vector the ammound specified by the supplied
     * parameter in the direction of the normal.
     * 
     * @param distance The distance that the vertex is extruded.
     */
    public void extrude(float distance) {
        x += normal.x * distance;
        y += normal.y * distance;
        z += normal.z * distance;
    }

    
    /**
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }
    /**
     * @param type The type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    public Object clone(){
        try{
            return super.clone();
        }catch(Exception e){
            e.printStackTrace();
        }
        return new FVertex(x,y,z);
    }
    public void normalise() {
        float t = (float)Math.sqrt(x * x + y * y + z * z);
        if(t!=0){
            x /= t;
        	y /= t;
        	z /= t;
        }
    }
    
	public FVertex cross(FVertex b) {
		return (FVertex)this.cross(b, new FVertex());
	}


	
	public String toString(){
		return "vertex(" + x + " , " + y + " , " + z + " );";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Float.floatToIntBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Float.floatToIntBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Float.floatToIntBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FVertex other = (FVertex) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
			return false;
		return true;
	}
}