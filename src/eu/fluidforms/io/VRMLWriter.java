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
 
/*
 * Created on 16-Apr-2005
 *
 * 
 * 
 */
package eu.fluidforms.io;

import java.util.*;
import java.io.*;

import eu.fluidforms.geom.*;


/**
 * @author williams
 * 
 * 
 *  
 */
public class VRMLWriter extends FFWriter {
	public VRMLWriter(){
		this(false);
	}
    public VRMLWriter(boolean binary) {
        super(false);
        fileType = "VRML";
    }

    public void writeMesh() throws IOException, FileNotFoundException {
        header(comment, triangles);
        mesh();
        footer();
    }

    void header(String fileName, Vector<FTriangle> triangles) throws IOException {
        writeln("#VRML V2.0 utf8");
        writeln("#Created by FLUIDFORMS");
        writeln("#parameters:"+comment);
        
//        writeln("Shape {");
//        writeln("appearance DEF FLUIDTEXTURE Appearance {");
//        writeln("material Material {");
//        writeln("diffuseColor 0 0 1 } ");
//        InputStream is = p5.openStream("textures/RisingSun.txt");
//        String[] texLines = PApplet.loadStrings(is);
//        for (int i = 0; i < texLines.length; i++) {
//            writeln(texLines[i]);
//        }
//        writeln("}");

//        writeln("Background{");
//        writeln("groundColor 1.0 1.0 1.0");
//        writeln("skyColor 1.0 1.0 1.0");
//        writeln("}");
    }

    void footer() {
    }

    void mesh() throws IOException {
        FVertex v1;
        FVertex v2;
        FVertex v3;

        writeln("geometry DEF FLUIDFORM IndexedFaceSet {");
        writeln("coord Coordinate {");//USE Nodos
        writeln("point [");
        for (int i = 0; i < verticies.size(); i++) {
            FVertex v = verticies.elementAt(i);
            writeln(v.x+" "+v.y+" "+v.z+",");
        }
        writeln("]");
        writeln("}");
        writeln("coordIndex [");
        for (int i = 0; i < triangles.size(); i++) {
            if (i % (triangles.size() / 10) == 0)
                System.out.println((int) (100f * i / triangles.size())
                        + "%");
            FTriangle tri = triangles.get(i);
            v1 = tri.getV1();
            v2 = tri.getV2();
            v3 = tri.getV3();

            writeln(verticies.indexOf(v1)+" "+verticies.indexOf(v2)+" "+verticies.indexOf(v3)+" -1");

        }
        writeln("]");
        writeln("}");
        writeln("}");
    }


}