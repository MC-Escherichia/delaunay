(ns delaunay.core
  (:import (org.delaunay.algorithm Triangulation Triangulations)
           (org.delaunay.model Vertex Triangle Edge)
           (org.delaunay TriangulationDemo)))


(defn four-liner []
  (TriangulationDemo/drawTriangulation
   (doto (Triangulation.)
     (.addAllVertices (Triangulations/randomVertices 1000 300 300))
     (.triangulate)
     )
   300 300 "triangulation.png"))


(defn -main []
  (four-liner))
