/**
 * Copyright (c) 2008-2009 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package com.ardor3d.example.interpolation;

import com.ardor3d.framework.FrameHandler;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.math.Quaternion;
import com.ardor3d.scenegraph.controller.interpolation.QuaternionInterpolationController;
import com.google.inject.Inject;

/**
 * A simple example showing the QuaternionInterpolationController in action.
 */
public class QuaternionInterpolationControllerExample extends
        InterpolationControllerBase<QuaternionInterpolationController> {

    public static void main(final String[] args) {
        newWindow(QuaternionInterpolationControllerExample.class);
    }

    @Inject
    public QuaternionInterpolationControllerExample(final LogicalLayer logicalLayer, final FrameHandler frameWork) {
        super(logicalLayer, frameWork);
    }

    @Override
    protected QuaternionInterpolationController createController() {
        // Create our control point rotations
        final Quaternion[] quats = { new Quaternion(0.0, 0.0, 0.0, 1.0), //
                new Quaternion(1.0, 0.0, 0.0, 1.0), //
                new Quaternion(0.0, 1.0, 0.0, 1.0), //
                new Quaternion(0.0, 0.0, 1.0, 1.0), //
                new Quaternion(1.0, 1.0, 1.0, 1.0) };

        // Create our controller
        final QuaternionInterpolationController controller = new QuaternionInterpolationController();
        controller.setControls(quats);
        controller.setActive(true);
        controller.setMaxTime(5);

        return controller;
    }
}
