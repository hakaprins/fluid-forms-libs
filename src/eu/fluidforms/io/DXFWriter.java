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
public class DXFWriter extends FFWriter {

    final static int MAX_TRI_LAYERS = 100000;

    final static int DXF_NO_LAYER = -1;

    int currentDXFLayer = 0;

    int[] DXFLayerList = new int[MAX_TRI_LAYERS];
    
    int iScale = 100;

    public DXFWriter(boolean binary) {
        super(false);
        fileType = "DXF";
    }

    public void writeMesh() throws IOException, FileNotFoundException {
        header(comment, triangles);
        mesh(triangles);
        footer();
    }

    void header(String fileName, Vector<FTriangle> triangles) throws IOException {
//        text.add("0");
//        text.add("SECTION");
//        text.add("2");
//        text.add("ENTITIES");
        writeln("0");
        writeln("SECTION");
        writeln("2");
        writeln("ENTITIES");
    }

    void footer() throws IOException {
        writeln("0");
        writeln("ENDSEC");
        writeln("0");
        writeln("EOF");
    }

    void mesh(Vector<FTriangle> triangles) throws IOException {
        FVertex v1;
        FVertex v2;
        FVertex v3;

        currentDXFLayer = 0;

        for (int i = 0; i < triangles.size(); i++) {
            if (i % (triangles.size() / 10) == 0)
                System.out.println((int) (100f * i / triangles.size())
                        + "%");
            FTriangle tri = triangles.get(i);
            v1 = tri.getV1();
            v2 = tri.getV2();
            v3 = tri.getV3();

            writeln("0");
            writeln("3DFACE");
            writeln("8");

            if (i < MAX_TRI_LAYERS) {
                if (DXFLayerList[i] >= 0) {
                    currentDXFLayer = DXFLayerList[i];
                }
            }
            writeln(Integer.toString(currentDXFLayer));

            writeln("10");
            writeln(Float.toString(-v1.x/iScale));
            writeln("20");
            writeln(Float.toString(v1.y/iScale));
            writeln("30");
            writeln(Float.toString(v1.z/iScale));
            writeln("11");
            writeln(Float.toString(-v2.x/iScale));
            writeln("21");
            writeln(Float.toString(v2.y/iScale));
            writeln("31");
            writeln(Float.toString(v2.z/iScale));
            writeln("12");
            writeln(Float.toString(-v3.x/iScale));
            writeln("22");
            writeln(Float.toString(v3.y/iScale));
            writeln("32");
            writeln(Float.toString(v3.z/iScale));
            writeln("13");
            writeln(Float.toString(-v3.x/iScale));
            writeln("23");
            writeln(Float.toString(v3.y/iScale));
            writeln("33");
            writeln(Float.toString(v3.z/iScale));
        }
    }

    /**
     * @return Returns the binary.
     */
    public boolean isBinary() {
        return binary;
    }

    /**
     * @param binary
     *            The binary to set.
     */
    public void setBinary(boolean binary) {
        this.binary = binary;
    }
}