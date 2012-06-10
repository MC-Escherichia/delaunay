package org.delaunay.dtfe.interpolation;

import org.delaunay.dtfe.DensityModel;
import org.delaunay.dtfe.DtfeTriangulationMap;
import org.delaunay.model.Vector;
import org.delaunay.model.Vertex;

final class NearestNeighborInterpolationStrategy implements
		InterpolationStrategy {
	public double getDensity(DtfeTriangulationMap<? extends DensityModel> dtfe, Vector v) {
		Vertex vert = dtfe.getTriangulation().locateNearestVertex(v);

		// Do not report density for triangles outside the convex hull of
		// map vertices.
		if (vert == null || dtfe.getTriangulation().neighborsSuperVertex(vert)) {
			return 0.0;
		}

		return dtfe.getDensity(vert);
	}
}