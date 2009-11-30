/**
 * Copyright (c) 2008-2009 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package com.ardor3d.example.renderer;

import com.ardor3d.bounding.BoundingSphere;
import automenta.spacenet.run.ArdorSpaceTime;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.image.Texture;
import com.ardor3d.image.Image.Format;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.shape.GeoSphere;
import com.ardor3d.scenegraph.shape.Sphere;
import com.ardor3d.scenegraph.shape.Sphere.TextureMode;
import com.ardor3d.util.TextureManager;
import com.google.inject.Inject;

/**
 * Simple example showing differences between GeoSphere and Sphere and their various texture mappings.
 */
public class SphereComparisonsExample extends ArdorSpaceTime {

    public static void main(final String[] args) {
        newWindow(SphereComparisonsExample.class);
    }

    @Inject
    public SphereComparisonsExample(final LogicalLayer layer, final FrameHandler frameWork) {
        super(layer, frameWork);
    }

    @Override
    protected void initWindow() {
        final TextureState ts = new TextureState();
        ts.setTexture(TextureManager.load("images/ardor3d_white_256.jpg", Texture.MinificationFilter.Trilinear,
                Format.Guess, true));
        _root.setRenderState(ts);

        final Node globesNode = new Node("globes");

        Sphere sphere = new Sphere("Linear mapping", Vector3.ZERO, 65, 65, 1, TextureMode.Linear);
        globesNode.attachChild(sphere);

        sphere = new Sphere("With mapping", 65, 65, 1);
        sphere = new Sphere("Projected mapping", Vector3.ZERO, 65, 65, 1, TextureMode.Projected);
        globesNode.attachChild(sphere);

        GeoSphere geoSphere = new GeoSphere("UV geo sphere - original", true, 2.0, 3, GeoSphere.TextureMode.Original);
        globesNode.attachChild(geoSphere);

        geoSphere = new GeoSphere("UV geo sphere - projected", true, 2.0, 3, GeoSphere.TextureMode.Projected);
        globesNode.attachChild(geoSphere);

        sphere = new Sphere("Projected mapping, radius 2", Vector3.ZERO, 65, 65, 2, TextureMode.Projected);
        globesNode.attachChild(sphere);

        globesNode.setRotation(new Quaternion().fromAngleAxis(-MathUtils.HALF_PI, Vector3.UNIT_X));
        int startX = -globesNode.getChildren().size() / 2;
        for (final Spatial s : globesNode.getChildren()) {
            final Mesh m = (Mesh) s;
            m.updateModelBound();
            startX += ((BoundingSphere) m.getModelBound(null)).getRadius() * 2;
            s.setTranslation(startX, 0, 0);
        }

        _root.attachChild(globesNode);
        _canvas.getCanvasRenderer().getCamera().setLocation(new Vector3(0, 0, 10));
        _lightState.setEnabled(false);
    }

}