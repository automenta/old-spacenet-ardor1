/**
 * Copyright (c) 2008-2009 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package com.ardor3d.example.effect;

import com.ardor3d.bounding.BoundingBox;
import automenta.spacenet.run.ArdorSpaceTime;
import com.ardor3d.extension.effect.particle.ParticleFactory;
import com.ardor3d.extension.effect.particle.ParticleSystem;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.image.Texture;
import com.ardor3d.image.Image.Format;
import com.ardor3d.image.Texture.WrapMode;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.TextureManager;
import com.google.inject.Inject;

public class ParticleSystemExample extends ArdorSpaceTime {

    private ParticleSystem particles;
    private final Vector3 currentPos = new Vector3(), newPos = new Vector3();

    public static void main(final String[] args) {
        newWindow(ParticleSystemExample.class);
    }

    @Inject
    public ParticleSystemExample(final LogicalLayer layer, final FrameHandler frameWork) {
        super(layer, frameWork);
    }

    int ignore = 10;

    @Override
    protected void updateWindow(final ReadOnlyTimer timer) {
        // We'll ignore the first 10 iterations because our timer is going to be unstable.
        if (ignore > 0) {
            ignore--;
            return;
        }
        if ((int) currentPos.getX() == (int) newPos.getX() && (int) currentPos.getY() == (int) newPos.getY()
                && (int) currentPos.getZ() == (int) newPos.getZ()) {
            newPos.setX(MathUtils.nextRandomDouble() * 50 - 25);
            newPos.setY(MathUtils.nextRandomDouble() * 50 - 25);
            newPos.setZ(MathUtils.nextRandomDouble() * 50 - 150);
        }
        final double tpf = timer.getTimePerFrame();
        currentPos.setX(currentPos.getX() - (currentPos.getX() - newPos.getX()) * tpf);
        currentPos.setY(currentPos.getY() - (currentPos.getY() - newPos.getY()) * tpf);
        currentPos.setZ(currentPos.getZ() - (currentPos.getZ() - newPos.getZ()) * tpf);
        _root.setTranslation(currentPos);
    }

    @Override
    protected void initWindow() {
        _canvas.setTitle("Particle System - Example");
        _lightState.setEnabled(false);

        particles = ParticleFactory.buildParticles("particles", 300);
        particles.setEmissionDirection(new Vector3(0, 1, 0));
        particles.setInitialVelocity(.006);
        particles.setStartSize(2.5);
        particles.setEndSize(.5);
        particles.setMinimumLifeTime(1200);
        particles.setMaximumLifeTime(1400);
        particles.setStartColor(new ColorRGBA(1, 0, 0, 1));
        particles.setEndColor(new ColorRGBA(0, 1, 0, 0));
        particles.setMaximumAngle(360 * MathUtils.DEG_TO_RAD);
        particles.getParticleController().setControlFlow(false);
        particles.setParticlesInWorldCoords(true);

        final BlendState blend = new BlendState();
        blend.setBlendEnabled(true);
        blend.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        blend.setDestinationFunction(BlendState.DestinationFunction.One);
        particles.setRenderState(blend);

        final TextureState ts = new TextureState();
        ts.setTexture(TextureManager.load("images/flaresmall.jpg", Texture.MinificationFilter.Trilinear, Format.Guess,
                true));
        ts.getTexture().setWrap(WrapMode.BorderClamp);
        ts.setEnabled(true);
        particles.setRenderState(ts);

        final ZBufferState zstate = new ZBufferState();
        zstate.setWritable(false);
        particles.setRenderState(zstate);

        particles.getParticleGeometry().setModelBound(new BoundingBox());

        _root.attachChild(particles);
        // kick things off by setting our start and end
        newPos.setX(MathUtils.nextRandomDouble() * 50 - 25);
        newPos.setY(MathUtils.nextRandomDouble() * 50 - 25);
        newPos.setZ(MathUtils.nextRandomDouble() * 50 - 150);

        currentPos.setX(MathUtils.nextRandomDouble() * 50 - 25);
        currentPos.setY(MathUtils.nextRandomDouble() * 50 - 25);
        currentPos.setZ(MathUtils.nextRandomDouble() * 50 - 150);
        _root.setTranslation(currentPos);

        // update our world transforms so the the particles will be in the right spot when we warm things up
        _root.updateWorldTransform(true);

        particles.warmUp(60);
    }
}
